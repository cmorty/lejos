#include "constants.h"

#include "debug.h"
#include "threads.h"
#include "breakpoints.h"
#include "opcodes.h"
#include "rconsole.h"

Debug *debug;
Thread *debugThread;
byte debugEventOptions[DBG_MAX_EVENT + 1];

void init_debug()
{
	debug = null;
	debugThread = null;
	init_breakpoint();
}

void set_debug(Debug *_debug)
{
	debug = _debug;
	debugThread = currentThread;
	int i;
	for (i = 0; i < sizeof(debugEventOptions); i++)
		debugEventOptions[i] = DBG_EVENT_DISABLE;
}

boolean debug_event(int event, Throwable *exception, const Thread *thread,
		const int method, const int pc, const int method2, const int pc2)
{
	if (!debug)
		return false;

	// Check whether the event occured on a system thread. Events on system threads are disabled.
	if (is_system(thread))
		return false;

	// Inform the debug thread (if any) that there has been a debug event.
	// return false if no debug thread is waiting.
	switch (debugEventOptions[event])
	{
	case DBG_EVENT_DISABLE:
		return false;
	case DBG_EVENT_IGNORE:
		return true;
	default:
		break;
	}
	// Check that we have a debugger attached and that it is ready to go...
	if (get_monitor_count((&(debug->_super.sync))) != 0
			|| debugThread->state != CONDVAR_WAITING
			|| (Debug *) debugThread->waitingOn != debug)
		return false;
	// Setup the state
	debug->typ = event;
	debug->exception = ptr2ref(exception);
	debug->thread = ptr2ref(thread);
	debug->pc = pc;
	debug->method = method;
	debug->method2 = method2;
	debug->pc2 = pc2;
	// Suspend all threads (except current)
	suspend_thread(null);
	// Make sure current thread is also suspended
	if (!is_system(currentThread))
		suspend_thread(currentThread);
	//  Allow the debug thread to run
	debugThread->flags &= ~THREAD_DAEMON;
	resume_thread(debugThread);
	monitor_notify_unchecked(&debug->_super, 1);
	return true;
}

boolean debug_exception(Throwable * exception, const Thread * thread,
		const int methodRecord, const int pc, const int catchMethodRecord,
		const int catchPc)
{
	return debug_event(DBG_EXCEPTION, exception, thread, methodRecord, pc,
			catchMethodRecord, catchPc);
}

boolean debug_user_interrupt()
{
	if (debug_event(DBG_USER_INTERRUPT, null, currentThread, 0, 0, 0, 0))
	{
		debugEventOptions[DBG_USER_INTERRUPT] = DBG_EVENT_IGNORE;
		return true;
	}
	return false;
}

boolean debug_program_start()
{
	return debug_event(DBG_PROGRAM_START, null, null, 0, 0, 0, 0);
}

boolean debug_program_exit()
{
	return debug_event(DBG_PROGRAM_EXIT, null, null, 0, 0, 0, 0);
}

boolean debug_thread_start(Thread* thread)
{
	return debug_event(DBG_THREAD_START, null, thread, 0, 0, 0, 0);
}

boolean debug_thread_stop(Thread* thread)
{
	return debug_event(DBG_THREAD_STOP, null, thread, 0, 0, 0, 0);
}

boolean debug_breakpoint(const Thread * thread, const int methodRecord,
		const int pc)
{
	return debug_event(DBG_BREAKPOINT, null, thread, methodRecord, pc, 0, 0);
}

boolean debug_single_step(Thread * thread, const int methodRecord, const int pc)
{
	return debug_event(DBG_SINGLE_STEP, null, thread, methodRecord, pc, 0, 0);
}

/*
 * Checks whether to stop because of stepping
 *
 * method and pc point to the triggering location.
 */
boolean check_stepping(MethodRecord* method, byte *pc)
{
	if (currentThread->debugData)
	{
		// Now check the location
		SteppingRequest *request =
				(SteppingRequest*) ref2obj(currentThread->debugData);
		int methodNo = get_method_no(method);
		int bppc = get_offset(method,pc);
		JINT *pcList = jint_array(request->stepPCs);
		int curFrame = currentThread->stackFrameIndex;
		int stepFrame = request->stepFrame;
		int depth = request->stepDepth;
		printf("s: %u (%u)\n",depth,request->stepDepth);
		if (curFrame > stepFrame)
		{
			if (depth != STEP_DEPTH_INTO) return false;
		}
		else if (curFrame == stepFrame)
		{
			if (depth == STEP_DEPTH_OUT) return false;
                       	if (methodNo == request->methodId)
			{
				// All valid pcs are in the list, ignore them
				int i;
				for (i = 0; i < get_array_length(ref2obj(request->stepPCs)); ++i)
					if (bppc == pcList[i])
						return false;
			}
		}
		else
			if (curFrame == 0) return false;
		// After the step we are still in the breakpoint state.
		// If we stop on a BREAKPOINT instruction, the program should not stop again on resume.
		if (*pc == OP_BREAKPOINT)
		{
			set_thread_debug_state(currentThread, BREAKPOINT, null);
		}
printf("M %d %d\n", methodNo, bppc);
		return debug_single_step(currentThread, methodNo, bppc);
	}
	return false;
}

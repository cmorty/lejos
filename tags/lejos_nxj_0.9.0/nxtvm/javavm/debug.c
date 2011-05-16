#include "constants.h"

#include "debug.h"
#include "threads.h"

Debug *debug;
Thread *debugThread;
byte debugEventOptions[DBG_MAX_EVENT+1];

void init_debug()
{
  debug = null;
  debugThread = null;
}

void set_debug(Debug *_debug)
{
  debug = _debug;
  debugThread = currentThread;
  int i;
  for(i = 0; i < sizeof(debugEventOptions); i++)
    debugEventOptions[i] = DBG_EVENT_DISABLE;
}

boolean debug_event(int event, Throwable *exception, const Thread *thread, const int method, int pc)
{
  // Inform the debug thread (if any) that there has been a debug event.
  // return false if no debug thread is waiting.
  switch(debugEventOptions[event])
  {
    case DBG_EVENT_DISABLE:
      return false;
    case DBG_EVENT_IGNORE:
      return true;
    default:
      break;
  }
  // Check that we have a debugger attached and that it is ready to go...
  if (!debug || get_monitor_count((&(debug->_super.sync))) != 0 ||
      debugThread->state != CONDVAR_WAITING || (Debug *)debugThread->waitingOn != debug)
    return false;
  // Setup the state
  debug->typ = event;
  debug->exception = ptr2ref(exception);
  debug->thread = ptr2ref(thread);
  debug->pc = pc;
  debug->method = method;
  // Suspend all threads (except current)
  suspend_thread(null);
  // Make sure current thread is also suspended
  suspend_thread(currentThread);
  //  Allow the debug thread to run
  debugThread->daemon = false;
  resume_thread(debugThread);
  monitor_notify_unchecked(&debug->_super, 1);
  return true;
}
  



boolean debug_uncaught_exception(Throwable * exception,
                          const Thread * thread,
                          const int methodRecord,
                          const int pc)
{
  return debug_event(DBG_EXCEPTION, exception, thread, methodRecord, pc);
}

boolean debug_user_interrupt()
{
  if (debug_event(DBG_USER_INTERRUPT, null, currentThread, 0, 0))
  {
    debugEventOptions[DBG_USER_INTERRUPT] = DBG_EVENT_IGNORE;
    return true;
  }
  return false;
}

boolean debug_program_exit()
{
  return debug_event(DBG_PROGRAM_EXIT, null, null, 0, 0);
}

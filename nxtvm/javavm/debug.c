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
  debug->methodBase = ptr2word(get_method_table(get_class_record(0)));
  debug->classBase = ptr2word(get_class_base());
  debug->fieldBase = ptr2word(get_field_table(get_class_record(0)));
  debug->threads = threads;
  int i;
  for(i = 0; i < sizeof(debugEventOptions); i++)
    debugEventOptions[i] = DBG_EVENT_DISABLE;
}

boolean debug_event(int event, Object *exception, const Thread *thread, const MethodRecord *method, byte *pc, int frame)
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
  debug->pc = (pc ? pc - get_binary_base() : 0);
  debug->method = (method ? method - get_method_table(get_class_record(0)) : 0);
  debug->frame = frame;
  // Suspend all threads (except current)
  suspend_thread(null);
  // Make sure current thread is also suspended
  suspend_thread(currentThread);
  //  Allow the debug thread to run
  resume_thread(debugThread);
  monitor_notify_unchecked(&debug->_super, 1);
  return true;
}
  



boolean debug_uncaught_exception(Object * exception,
                          const Thread * thread,
                          const MethodRecord * methodRecord,
                          const MethodRecord * rootMethod,
                          byte * pc, int exceptionFrame)
{
  return debug_event(DBG_EXCEPTION, exception, thread, methodRecord, pc, exceptionFrame);
}

boolean debug_user_interrupt()
{
  if (debug_event(DBG_USER_INTERRUPT, null, currentThread, null, null, 0))
  {
    debugEventOptions[DBG_USER_INTERRUPT] = DBG_EVENT_IGNORE;
    return true;
  }
  return false;
}

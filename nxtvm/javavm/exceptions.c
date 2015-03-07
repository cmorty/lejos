
#include "types.h"
#include "trace.h"

#include "threads.h"
#include "constants.h"
#include "specialsignatures.h"
#include "specialclasses.h"
#include "exceptions.h"
#include "classes.h"

#include "language.h"
#include "configure.h"
#include "interpreter.h"

#include "memory.h"
#include "stack.h"

#include "platform_hooks.h"

#include "debug.h"

Throwable *outOfMemoryError;


void init_exceptions()
{
  outOfMemoryError = (Throwable *)new_object_for_class (JAVA_LANG_OUTOFMEMORYERROR);
  protect_obj(outOfMemoryError);
}

/**
 * Create an exception object. Only used by VM code.
 */
int throw_new_exception(int class)
{
  Throwable *exception;
  Object *stackTrace;
  // Maximize our chance of success by forcing a collection
  wait_garbage_collect();
  exception = (Throwable *)new_object_for_class(class);
  stackTrace = create_stack_trace(currentThread, null);
  if (exception == JNULL || stackTrace == JNULL)
  {
    // We are all of memory, clean things up and abort.
    wait_garbage_collect();
    return throw_exception(outOfMemoryError, true);
  }
  exception->stackTrace = obj2ref(stackTrace);
  return throw_exception(exception, true);
}

    

/**
 * Create an exception.
 * Find the handler for this exception if one exists and set things up to
 * handle it. If no handler exists, call the default exception handler.
 * Allow the debug system to intercept the exception handling process
 * and restart it.
 * @return the exection action state.
 */
int throw_exception (Throwable *exception, boolean allowDebug)
{
  int exceptionFrame = currentThread->stackFrameIndex;
  int frameCnt = 0;
  MethodRecord *exceptionMethod;
  MethodRecord *curMethodRecord;
  StackFrame *curStackFrame;
  ExceptionRecord *exceptionRecord;
  int exceptionMethodNo;
  int exceptionOffset;
  int catchMethodNo;
  int catchOffset;
  
  if (exception->_super.flags.class == JAVA_LANG_INTERRUPTEDEXCEPTION)
    // Throwing an interrupted exception clears the flag
    currentThread->interruptState = INTERRUPT_CLEARED;
  // abort the current instruction so things are in a consistant state
  curPc = getPc();
  // record current state
  curStackFrame = current_stackframe();
  update_stack_frame(curStackFrame);
  exceptionMethod = curStackFrame->methodRecord;
  // Serach the stack looking for an exception handler
  do {
    int handlerCnt;
    int pc;
    curMethodRecord = curStackFrame->methodRecord;
    exceptionRecord = (ExceptionRecord *) (get_binary_base() + curMethodRecord->exceptionTable);
    handlerCnt = curMethodRecord->numExceptionHandlers;
    // get pc offset, adjust all but top frame for return address rather then call
    pc = get_offset(curMethodRecord, curStackFrame->pc) - (frameCnt == 0 ? 0 : 2);
    while (handlerCnt-- > 0)
    {
      if (exceptionRecord->start <= pc 
          && pc <= exceptionRecord->end)
      {
        // Check if exception class applies
        if (instance_of ((Object *)exception, exceptionRecord->classIndex))
          goto HANDLER_FOUND;
      }
      exceptionRecord++;
    }
    curStackFrame--;
    exceptionRecord = null;
  } while(++frameCnt < exceptionFrame);
HANDLER_FOUND:
  exceptionMethodNo = get_method_no(exceptionMethod);
  exceptionOffset = get_offset(exceptionMethod, curPc);
  if (exceptionRecord)
  {
    catchMethodNo = get_method_no(curMethodRecord);
    catchOffset = exceptionRecord->handler;
  }
  else
    catchMethodNo = catchOffset = -1;
  // do we have a debugger that wants to handle this?
  if (allowDebug && debug_exception (exception, currentThread,
                                     exceptionMethodNo, exceptionOffset,
                                     catchMethodNo, catchOffset))
  {
    set_thread_debug_state(currentThread, EXCEPTIONBP, (Object *)exception);
    return EXEC_EXCEPTION;
  }
  // prepare to actually throw the exception by unwinding the stack
  while(frameCnt-- > 0)
    do_return(0);
  set_thread_debug_state(currentThread, RUNNING, null);
  // was the exception caught?
  if (exceptionRecord)
  {
    // Clear operand stack
    curStackTop = init_sp (curStackFrame, curMethodRecord);
    // Push the exception object
    push_ref_cur (ptr2word (exception));
    // Jump to handler:
    curPc = get_code_ptr(curMethodRecord) + catchOffset;
  }
  else
    call_exception_handler(exception, exceptionMethodNo, exceptionOffset);
  return EXEC_EXCEPTION;
}


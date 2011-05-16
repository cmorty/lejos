
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
    return throw_exception(outOfMemoryError);
  }
  exception->stackTrace = obj2ref(stackTrace);
  return throw_exception(exception);
}

    

/**
 * Create an exception.
 * Find the handler for this exception if one exists and set things up to
 * handle it. If no handler exists, call the debug interface and if that does
 * not deal with the exception call the default exception handler.
 * @return the exection action state.
 */
int throw_exception (Throwable *exception)
{
  Thread *auxThread;
  int exceptionFrame = currentThread->stackFrameIndex;
  TWOBYTES tempCurrentOffset;
  MethodRecord *tempMethodRecord = null;
  StackFrame *tempStackFrame;
  ExceptionRecord *exceptionRecord;
  byte numExceptionHandlers;
  MethodRecord *excepMethodRec = null;
  byte *exceptionPc;
  
  #ifdef VERIFY
  assert (exception != null, EXCEPTIONS0);
  #endif // VERIFY
#if DEBUG_EXCEPTIONS
  printf("Throw exception\n");
#endif
  if (currentThread == null)
  {
    // No threads have started probably
    return EXEC_CONTINUE;
  }
  else if (exception->_super.flags.class == JAVA_LANG_INTERRUPTEDEXCEPTION)
  {
    // Throwing an interrupted exception clears the flag
    currentThread->interruptState = INTERRUPT_CLEARED;
  }
  
  #ifdef VERIFY
  assert (currentThread->state > DEAD, EXCEPTIONS1);
  #endif // VERIFY
  // abort the current instruction so things are in a consistant state
  curPc = getPc();
  // record current state
  exceptionPc = curPc;
  tempStackFrame = current_stackframe();
  update_stack_frame(tempStackFrame);
  excepMethodRec = tempStackFrame->methodRecord;;
  auxThread = currentThread;

  #if 0
  trace (-1, get_class_index(exception), 3);
  #endif

 LABEL_PROPAGATE:
  tempMethodRecord = tempStackFrame->methodRecord;

  exceptionRecord = (ExceptionRecord *) (get_binary_base() + tempMethodRecord->exceptionTable);
  tempCurrentOffset = ptr2word(curPc) - ptr2word(get_binary_base() + tempMethodRecord->codeOffset);

  numExceptionHandlers = tempMethodRecord->numExceptionHandlers;
#if DEBUG_EXCEPTIONS
  printf("Num exception handlers=%d\n",numExceptionHandlers);
#endif
  while (numExceptionHandlers--)
  {
    if (exceptionRecord->start <= tempCurrentOffset 
        && tempCurrentOffset <= exceptionRecord->end)
    {
      // Check if exception class applies
      if (instance_of ((Object *)exception, exceptionRecord->classIndex))
      {
        // Clear operand stack
        curStackTop = init_sp (tempStackFrame, tempMethodRecord);
        // Push the exception object
        push_ref_cur (ptr2word (exception));
        // Jump to handler:
        curPc = get_binary_base() + tempMethodRecord->codeOffset + 
                 exceptionRecord->handler;
#if DEBUG_EXCEPTIONS
  printf("Found exception handler\n");
#endif
        return EXEC_EXCEPTION;
      }
    }
    exceptionRecord++;
  }
  // No good handlers in current stack frame - go up.
  do_return (0);
  // Note: return takes care of synchronized methods.
  if (auxThread->state == DEAD)
  {
#if DEBUG_EXCEPTIONS
  printf("Thread is dead\n");
#endif
    if (exception->_super.flags.class != JAVA_LANG_THREADDEATH)
    {
#if DEBUG_EXCEPTIONS
  printf("Handle uncaught exception\n");
#endif
      int methodNo = excepMethodRec - get_method_table(get_class_record(0));
      // Restore the stack and pc of the exception thread. This prevents
      // corruption of lower frames if we save the current state. The
      // thread is now dead so this should be safe.
      curPc = exceptionPc;
      currentThread->stackFrameIndex = exceptionFrame;
      tempCurrentOffset = ptr2word(curPc) - ptr2word(get_binary_base() + excepMethodRec->codeOffset);
      if (!debug_uncaught_exception (exception, auxThread,
  			         methodNo,
			         tempCurrentOffset))
        handle_uncaught_exception (exception, auxThread,
  			         methodNo,
			         tempCurrentOffset);
    }
    return EXEC_CONTINUE;
  }
  // After the return the address will point to the next, instruction, we need
  // to back it off to point to the actual caller...Note that this does not 
  // need to point at the start of the instruction since the only use made of
  // PC here is to locate the exception handler, so we can get away with it
  // pointing into the middle...
  curPc--;
  tempStackFrame = current_stackframe();
  goto LABEL_PROPAGATE; 
}


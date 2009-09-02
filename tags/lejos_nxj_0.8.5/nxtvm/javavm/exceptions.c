
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

Object *outOfMemoryError;
Object *noSuchMethodError;
Object *stackOverflowError;
Object *nullPointerException;
Object *classCastException;
Object *arithmeticException;
Object *arrayIndexOutOfBoundsException;
Object *illegalArgumentException;
Object *interruptedException;
Object *illegalStateException;
Object *illegalMonitorStateException;
Object *error;
Object *arrayStoreException;
Object *negativeArraySizeException;

// Temporary globals:

static TWOBYTES tempCurrentOffset;
static MethodRecord *tempMethodRecord = null;
static StackFrame *tempStackFrame;
static ExceptionRecord *gExceptionRecord;
static byte gNumExceptionHandlers;
static MethodRecord *gExcepMethodRec = null;
static byte *gExceptionPc;


void init_exceptions()
{
  outOfMemoryError = new_object_for_class (JAVA_LANG_OUTOFMEMORYERROR);
  noSuchMethodError = new_object_for_class (JAVA_LANG_NOSUCHMETHODERROR);
  stackOverflowError = new_object_for_class (JAVA_LANG_STACKOVERFLOWERROR);

  nullPointerException = new_object_for_class (JAVA_LANG_NULLPOINTEREXCEPTION);
  classCastException = new_object_for_class (JAVA_LANG_CLASSCASTEXCEPTION);
  arithmeticException = new_object_for_class (JAVA_LANG_ARITHMETICEXCEPTION);
  arrayIndexOutOfBoundsException = new_object_for_class (JAVA_LANG_ARRAYINDEXOUTOFBOUNDSEXCEPTION);

  illegalArgumentException = new_object_for_class (JAVA_LANG_ILLEGALARGUMENTEXCEPTION);
  interruptedException = new_object_for_class (JAVA_LANG_INTERRUPTEDEXCEPTION);
  illegalStateException = new_object_for_class (JAVA_LANG_ILLEGALSTATEEXCEPTION);

  illegalMonitorStateException = new_object_for_class (JAVA_LANG_ILLEGALMONITORSTATEEXCEPTION);
  arrayStoreException = new_object_for_class(JAVA_LANG_ARRAYSTOREEXCEPTION);
  negativeArraySizeException = new_object_for_class(JAVA_LANG_NEGATIVEARRAYSIZEEXCEPTION);
  error = new_object_for_class (JAVA_LANG_ERROR);
}

/**
 * Create an exception.
 * Find the handler for this exception if one exists and set things up to
 * handle it. If no handler exists, call the debug interface and if that does
 * not deal with the exception call the default exception handler.
 * @return the exection action state.
 */
int throw_exception (Object *exception)
{
  Thread *auxThread;
  int exceptionFrame = currentThread->stackFrameArraySize;
  
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
  else if (exception == interruptedException)
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
  gExceptionPc = curPc;
  tempStackFrame = current_stackframe();
  update_stack_frame(tempStackFrame);
  gExcepMethodRec = tempStackFrame->methodRecord;;
  auxThread = currentThread;

  #if 0
  trace (-1, get_class_index(exception), 3);
  #endif

 LABEL_PROPAGATE:
  tempMethodRecord = tempStackFrame->methodRecord;

  gExceptionRecord = (ExceptionRecord *) (get_binary_base() + tempMethodRecord->exceptionTable);
  tempCurrentOffset = ptr2word(curPc) - ptr2word(get_binary_base() + tempMethodRecord->codeOffset);

  #if 0
  trace (-1, tempCurrentOffset, 5);
  #endif

  gNumExceptionHandlers = tempMethodRecord->numExceptionHandlers;
#if DEBUG_EXCEPTIONS
  printf("Num exception handlers=%d\n",gNumExceptionHandlers);
#endif
  while (gNumExceptionHandlers--)
  {
    if (gExceptionRecord->start <= tempCurrentOffset /* off by one? < ? */
        && tempCurrentOffset <= gExceptionRecord->end)
    {
      // Check if exception class applies
      if (instance_of (exception, gExceptionRecord->classIndex))
      {
        // Clear operand stack
        curStackTop = init_sp (tempStackFrame, tempMethodRecord);
        // Push the exception object
        push_ref_cur (ptr2word (exception));
        // Jump to handler:
        curPc = get_binary_base() + tempMethodRecord->codeOffset + 
                 gExceptionRecord->handler;
#if DEBUG_EXCEPTIONS
  printf("Found exception handler\n");
#endif
        return EXEC_EXCEPTION;
      }
    }
    gExceptionRecord++;
  }
  // No good handlers in current stack frame - go up.
  do_return (0);
  // Note: return takes care of synchronized methods.
  if (auxThread->state == DEAD)
  {
#if DEBUG_EXCEPTIONS
  printf("Thread is dead\n");
#endif
    if (get_class_index(exception) != JAVA_LANG_THREADDEATH)
    {
#if DEBUG_EXCEPTIONS
  printf("Handle uncaught exception\n");
#endif
      // Restore the stack and pc of the exception thread. This prevents
      // corruption of lower frames if we save the current state. The
      // thread is now dead so this should be safe.
      curPc = gExceptionPc;
      currentThread->stackFrameArraySize = exceptionFrame;
      if (!debug_uncaught_exception (exception, auxThread,
  			         gExcepMethodRec, tempMethodRecord,
			         gExceptionPc, exceptionFrame))
        handle_uncaught_exception (exception, auxThread,
  			         gExcepMethodRec, tempMethodRecord,
			         gExceptionPc);
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


/**
 * Runtime data structures for loaded program.
 */

#include "types.h"
#include "trace.h"
#include "constants.h"
#include "specialsignatures.h"
#include "specialclasses.h"
#include "memory.h"
#include "threads.h"
#include "classes.h"
#include "language.h"
#include "configure.h"
#include "interpreter.h"
#include "exceptions.h"
#include "stack.h"
#include "platform_hooks.h"
#include "rconsole.h"
#if 0
#define get_stack_object(MREC_)  ((Object *) get_ref_at ((MREC_)->numParameters - 1))
#endif

// Reliable globals:

void* installedBinary;

ConstantRecord* constantTableBase;
byte* staticFieldsBase;
byte* entryClassesBase;
ClassRecord* classBase;

#if EXECUTE_FROM_FLASH
byte *classStaticStateBase;
byte *classStatusBase;
#endif

// Temporary globals:

// (Gotta be careful with these; a lot of stuff
// is not reentrant because of globals like these).

// static ClassRecord *tempClassRecord;
// static MethodRecord *tempMethodRecord;

// Methods:

void install_binary( void* ptr)
{
  installedBinary = ptr;

  constantTableBase = __get_constant_base();
  staticFieldsBase = __get_static_fields_base();
  entryClassesBase = __get_entry_classes_base();
  classBase = __get_class_base();
  gVMOptions = VM_DEFAULT;
}

byte get_class_index (Object *obj)
{
  if (is_array(obj))
    return JAVA_LANG_OBJECT;
  return obj->flags.objects.class;
}


/**
 * @return Method record or null.
 */
MethodRecord *find_method (ClassRecord *classRecord, int methodSignature)
{
  MethodRecord* mr0 = get_method_table( classRecord);
  MethodRecord* mr = mr0 + classRecord->numMethods;
  while( -- mr >= mr0)
    if( mr->signatureId == methodSignature)
      return mr;

  return null;
}

/**
 * Exceute the static initializer if required.
 * @return An indication of how the VM should proceed
 */
int dispatch_static_initializer (ClassRecord *aRec, byte *retAddr)
{
  // Are we needed?
  if (is_initialized (aRec))
    return EXEC_CONTINUE;
  // Do we have one?
  if (!has_clinit (aRec))
  {
    set_initialized (aRec);
    return EXEC_CONTINUE;
  }
  #if DEBUG_METHODS
  printf ("dispatch_static_initializer: has clinit: %d, %d\n",
          (int) aRec, (int) retAddr);
  #endif
  // Can we run it?
  if (!dispatch_special (find_method (aRec, _6clinit_7_4_5V), retAddr))
    return EXEC_RETRY;
  // Mark for next time
  set_initialized (aRec);
  return EXEC_RUN;
}

void dispatch_virtual (Object *ref, int signature, byte *retAddr)
{
  ClassRecord *classRecord;
  MethodRecord *methodRecord;
  int classIndex;

#if DEBUG_METHODS
  printf("dispatch_virtual %d\n", signature);
#endif
  if (ref == JNULL)
  {
    throw_exception (nullPointerException);
    return;
  }
  // When calling methods on arrays, we use the methods for the Object class...
  classIndex = get_class_index(ref);
 LABEL_METHODLOOKUP:
  classRecord = get_class_record (classIndex);
  methodRecord = find_method (classRecord, signature);
  if (methodRecord == null)
  {
    #if SAFE
    if (classIndex == JAVA_LANG_OBJECT)
    {
      throw_exception (noSuchMethodError);
      return;
    }
    #endif
    classIndex = classRecord->parentClass;
    goto LABEL_METHODLOOKUP;
  }

  if (dispatch_special (methodRecord, retAddr))
  {
    if (is_synchronized(methodRecord))
    {
      current_stackframe()->monitor = ref;
      enter_monitor (currentThread, ref);
    }
  }
}

/**
 * Calls static initializer if necessary before
 * dispatching with dispatch_special().
 * @param retAddr Return bytecode address.
 * @param btAddr Backtrack bytecode address (in case
 *               static initializer is executed).
 */
void dispatch_special_checked (byte classIndex, byte methodIndex,
                               byte *retAddr, byte *btAddr)
{
  ClassRecord *classRecord;
  MethodRecord *methodRecord;
  #if DEBUG_METHODS
  printf ("dispatch_special_checked: %d, %d, %d, %d\n",
          classIndex, methodIndex, (int) retAddr, (int) btAddr);
  #endif

  // If we need to run the initializer then the real method will get called
  // later, when we re-run the current instruction.
  classRecord = get_class_record (classIndex);
  if (!is_initialized_idx (classIndex))
    if (dispatch_static_initializer (classRecord, btAddr) != EXEC_CONTINUE)
      return;
  methodRecord = get_method_record (classRecord, methodIndex);
  if(dispatch_special (methodRecord, retAddr))
  {
    if (is_synchronized(methodRecord))
    {
      Object *ref = (Object *)curLocalsBase[0];
      current_stackframe()->monitor = ref;
      enter_monitor (currentThread, ref);
    }
  }
}

/**
 * @param classRecord Record for method class.
 * @param methodRecord Calle's method record.
 * @param retAddr What the PC should be upon return.
 * @return true iff the stack frame was pushed.
 */
boolean dispatch_special (MethodRecord *methodRecord, byte *retAddr)
{
  /**
   * Note: This code is a little tricky, particularly when used with
   * a garbage collector. It manipulates the stack frame and in some cases
   * may need to perform memory allocation. In all cases we must take care
   * to ensure that if an allocation can be made then any live objects
   * on the stack must be below the current stack pointer.
   * In addition to the above we take great care so that this function can
   * be restarted (to allow us to wait for available memory). To enable this
   * we avoid making any commitments to changes to global state until both
   * stacks have been commited.
   */
  #if DEBUG_METHODS
  int debug_ctr;
  #endif

  StackFrame *stackFrame;
  byte newStackFrameIndex;
  STACKWORD *newStackTop;

  #if DEBUG_BYTECODE
  printf ("\n------ dispatch special - %d ------------------\n\n",
          methodRecord->signatureId);
  #endif

  #if DEBUG_METHODS
  printf ("dispatch_special: %d, %d\n", 
          (int) methodRecord, (int) retAddr);
  printf ("-- signature id = %d\n", methodRecord->signatureId);
  printf ("-- code offset  = %d\n", methodRecord->codeOffset);
  printf ("-- flags        = %d\n", methodRecord->mflags);
  printf ("-- num params   = %d\n", methodRecord->numParameters);
  printf ("-- stack ptr    = %d\n", (int) get_stack_ptr());
  printf ("-- max stack ptr= %d\n", (int) (currentThread->stackArray + (get_array_size(currentThread->stackArray))*2));
  #endif


  // First deal with the easy case of a native call...
  if (is_native (methodRecord))
  {
  #if DEBUG_METHODS
  printf ("-- native\n");
  #endif 
    // WARNING: Once the instruction below has been executed we may have
    // references on the stack that are above the stack pointer. If a GC
    // gets run when in this state the reference may get collected as
    // grabage. This means that any native functions that take a reference
    // parameter and that may end up allocating memory *MUST* protect that
    // reference before calling the allocator...
    pop_words_cur (methodRecord->numParameters);
    switch(dispatch_native (methodRecord->signatureId, get_stack_ptr_cur() + 1))
    {
      case EXEC_RETRY:
        // Need to re-start the instruction, so reset the state of the stack
        curStackTop += methodRecord->numParameters;
        break;
      case EXEC_CONTINUE:
        // Normal completion return to the requested point.
        curPc = retAddr;
        break;
      case EXEC_RUN:
        // We are running new code, curPc will be set. Nothing to do.
        break;
      case EXEC_EXCEPTION:
        // An exception has been thrown. The PC will be set correctly and
        // the stack may have been adjusted...
        break;
    }
    // Stack frame not pushed
    return false;
  }
  newStackFrameIndex = currentThread->stackFrameArraySize;
  if (newStackFrameIndex >=  255)
  {
      throw_exception (stackOverflowError);
      return false;
  }
  if (newStackFrameIndex >= get_array_length((Object *) word2ptr (currentThread->stackFrameArray)))
  {
#if !FIXED_STACK_SIZE
    // int len = get_array_length((Object *) word2ptr (currentThread->stackFrameArray));
    int newlen = get_array_length((Object *) word2ptr (currentThread->stackFrameArray)) * 3 / 2;
    JINT newStackFrameArray = JNULL;
    // Stack frames are indexed by a byte value so limit the size. 
    if (newStackFrameIndex < 255)
    {
      if (newlen > 255)
        newlen = 255;
      // increase the stack frame size
      newStackFrameArray = ptr2ref(reallocate_array(word2ptr(currentThread->stackFrameArray), newlen));
    }
    // If can't allocate new stack, give in!
    if (newStackFrameArray == JNULL)
    {
#endif
      //throw_exception (stackOverflowError);
      return false;
#if !FIXED_STACK_SIZE
    }
    // Assign new array
    currentThread->stackFrameArray = newStackFrameArray;
#endif
  }
  
  // Now start to build the new stack frames. We start by placing the
  // the new stack pointer below any params. The params will become locals
  // in the new frame.
  newStackTop = get_stack_ptr_cur() - methodRecord->numParameters;
  if (newStackFrameIndex == 0)
  {
    // Assign NEW stack frame
    stackFrame = stackframe_array();
  }
  else
  {
    #if DEBUG_METHODS
    for (debug_ctr = 0; debug_ctr < methodRecord->numParameters; debug_ctr++)
      printf ("-- param[%d]    = %ld\n", debug_ctr, (long) get_stack_ptr()[debug_ctr+1]);  
    #endif

    // Setup OLD stackframe ready for return
    stackFrame = stackframe_array() + (newStackFrameIndex - 1);
    stackFrame->stackTop = newStackTop;
    stackFrame->pc = retAddr;
    // Push NEW stack frame
    stackFrame++;
  }
  // Increment size of stack frame array but do not commit to it until we have
  // completely built both new stacks.
  newStackFrameIndex++;
  // Initialize rest of new stack frame
  stackFrame->methodRecord = methodRecord;
  stackFrame->monitor = null;
  stackFrame->localsBase = newStackTop + 1;
  // Allocate space for locals etc.
  newStackTop = init_sp(stackFrame, methodRecord);
  
  //printf ("m %d stack = %d\n", (int) methodRecord->signatureId, (int) (localsBase - stack_array())); 
  
  // Check for stack overflow
  // (stackTop + methodRecord->maxOperands) >= (stack_array() + STACK_SIZE);
  if (is_stack_overflow (newStackTop, methodRecord))
  {
#if !FIXED_STACK_SIZE
    StackFrame *stackBase;
    byte *oldStart = array_start((Object *)(currentThread->stackArray));
    int offset;
    int i;
    
    // Need at least this many bytes
    // int len = (int)(stackTop + methodRecord->maxOperands) - (int)(stack_array()) - HEADER_SIZE;
    
    // Need to compute new array size (as distinct from number of bytes in array).
    int newlen = (((int)(newStackTop + methodRecord->maxOperands) - (int)(stack_array()) + 3) / 4) * 3 / 2;
    REFERENCE newStackArray = ptr2ref(reallocate_array(word2ptr(currentThread->stackArray), newlen));

    // If can't allocate new stack, give in!
    if (newStackArray == JNULL)
    {
#endif
      //throw_exception (stackOverflowError);
      return false;
#if !FIXED_STACK_SIZE
    }
    // Adjust pointers.
    offset = array_start((Object *)newStackArray) - oldStart;
    stackBase = stackframe_array();
    newStackTop = word2ptr(ptr2word(newStackTop) + offset);
    curLocalsBase = word2ptr(ptr2word(curLocalsBase) + offset);
#if DEBUG_MEMORY
    printf("thread=%d, stackTop(%d), localsBase(%d)=%d\n", currentThread->threadId, (int)stackTop, (int)localsBase, (int)(*localsBase));
#endif
    for (i=((byte)(newStackFrameIndex))-1;
         i >= 0;
         i--)
    {
      stackBase[i].localsBase = word2ptr(ptr2word(stackBase[i].localsBase) + offset);
      stackBase[i].stackTop = word2ptr(ptr2word(stackBase[i].stackTop) + offset);
#if DEBUG_MEMORY
      printf("stackBase[%d].localsBase(%d) = %d\n", i, (int)stackBase[i].localsBase, (int)(*stackBase[i].localsBase));
#endif
    }
    // Assign new array
    currentThread->stackArray = newStackArray;
#endif
  }
  // All set. So now we can finally commit to the new stack frames
  currentThread->stackFrameArraySize = newStackFrameIndex;
  update_constant_registers (stackFrame);
  curStackTop = newStackTop;
  // and jump to the start of the new code
  curPc = get_code_ptr(methodRecord);
  return true;
}

/**
 * Perform a return from a method call. Clean up the stack, setup
 * the return of any results, release any monitor and finally set the
 * PC for the return address.
 */
void do_return (int numWords)
{
  StackFrame *stackFrame;
  STACKWORD *fromStackPtr;

  stackFrame = current_stackframe();

  #if DEBUG_BYTECODE
  printf ("\n------ return ----- %d ------------------\n\n",
          stackFrame->methodRecord->signatureId);
  #endif

  #if DEBUG_METHODS
  printf ("do_return: method: %d  #  num. words: %d\n", 
          stackFrame->methodRecord->signatureId, numWords);
  #endif

  #ifdef VERIFY
  assert (stackFrame != null, LANGUAGE3);
  #endif
  if (stackFrame->monitor != null)
    exit_monitor (currentThread, stackFrame->monitor);

  #if DEBUG_THREADS || DEBUG_METHODS
  printf ("do_return: stack frame array size: %d\n", currentThread->stackFrameArraySize);
  #endif

  if (currentThread->stackFrameArraySize == 1)
  {
    #if DEBUG_METHODS
    printf ("do_return: thread is done: %d\n", (int) currentThread);
    #endif
    currentThread->state = DEAD;
    schedule_request (REQUEST_SWITCH_THREAD);
    return;
  }

  // Place source ptr below data to be copied up the stack
  fromStackPtr = get_stack_ptr_at_cur(numWords);
  // Pop stack frame
  currentThread->stackFrameArraySize--;
  stackFrame--;
  // Assign registers
  update_registers (stackFrame);

  #if DEBUG_METHODS
  printf ("do_return: stack reset to:\n");
  printf ("-- stack ptr = %d\n", (int) get_stack_ptr());
  #endif

  while (numWords--)
  {
    push_word_cur (*(++fromStackPtr));
  }  
}

/**
 * Exceute a "program" on the current thread.
 * @return An indication of how the VM should proceed.
 */
int execute_program(int prog)
{
  /* We run an internal program. Note that we need to take great care to
   * ensure that this function can be re-started (for the garbage collector).
   * so we must not modify the stack until we are ready to go.
   */
  // Save the current state in case we need to back out!
  int curStackFrameSize = currentThread->stackFrameArraySize;
  STACKWORD *sp = curStackTop;
  update_stack_frame(current_stackframe());
  // Now find the class
  MethodRecord *mRec;
  ClassRecord *classRecord;
  classRecord = get_class_record (get_entry_class (prog));
  if (classRecord == null) return EXEC_CONTINUE;
  // reserve space for the argument to main, but do not modify the stack
  // contents yet, in case we need to restart the instruction. 
  curStackTop++;
  // Push stack frame for main method:
  mRec = find_method (classRecord, main_4_1Ljava_3lang_3String_2_5V);
  if (dispatch_special (mRec, curPc) && (dispatch_static_initializer (classRecord, curPc) != EXEC_RETRY))
  {
    // Everything is ready to go. So now update the stack
    *sp = JNULL;
    return EXEC_RUN;
  }
  // We need to wait and re-try the instruction, set things back the way they
  // were...
  currentThread->stackFrameArraySize = curStackFrameSize;
  update_registers(current_stackframe());
  return EXEC_RETRY;
}

/**
 * Type checking functions.
 * The following functions provide mechanisms for obtaining and checking
 * the types of objects and arrays. These tests make use of a type
 * signatures which is a two byte word containing the following fields
 * 15: unused
 * 12-14: Dimension of an array
 * 8-11: basic type 0: object for other types see memory.c
 * 0-7: class index
 * The signatue is only stored in full for BigArrays, for other objects
 * it is created on demand. Note that for small arrays we do not have full
 * type information available, so care must be taken when checking the
 * type of these.
 */

/**
 * Return the type signature of the object.
 */
TWOBYTES get_object_sig(Object *obj)
{
  if (!obj) return 0;
  if (is_array(obj))
  {
    // We have a full sig only for big arrays
    if (is_big_array(obj))
      return ((BigArray *)obj)->sig;
    else
      return sig_new_array(0, obj->flags.arrays.type, 0);
  }
  return obj->flags.objects.class;
}


/**
 * Return the type signature for the contents of an array
 */
TWOBYTES get_constituent_sig(Object *obj)
{
  TWOBYTES sig;  
  // If not an array just treat as a basic object
  if (!obj || !is_array(obj)) return 0;
  sig = get_object_sig(obj);
  // If we don't have a full signature simply return what we have
  if (sig_get_dim(sig) == 0)
    return sig;
  else
    return sig_new_array(sig_get_dim(sig)-1, sig_get_base_type(sig), sig_get_class(sig));
}


/**
 * Check to see if obj is a sub type of the type described by
 * sig. 
 */
static boolean sub_type_of(TWOBYTES obj, TWOBYTES sig)
{
  if ( sig == JAVA_LANG_OBJECT) return true;
  while (obj != sig)
  {
    obj = get_class_record(obj)->parentClass;
    if (obj == JAVA_LANG_OBJECT) return false;
  }
  return true;
}
  

/**
 * Check to see if obj is an instance of the type described by sig.
 * @return true or false
 */
boolean instance_of (Object *obj, TWOBYTES sig)
{
  TWOBYTES rtType;

  if (obj == null)
    return false;
  
  // Check for common cases
  if (sig == JAVA_LANG_OBJECT) return true;
  rtType = get_object_sig(obj);
  if (rtType == sig) return true;
  // Check for special case of arrays
  if (sig_is_array(sig))
  {
    // For arrays we may not have much type information available.
    // In the minimum case we have full information about the signature
    // but we may not have full info for the object. We will only have the
    // base type of the last dimension.

    if (!is_array(obj)) return false;
    // Do we have a full signature for the array.
    if (!sig_is_array(rtType))
    {
      // We only have basic type information, and then only for a 1d array
      if (sig_get_dim(sig) == 1 && sig_get_base_type(sig) != sig_get_base_type(rtType)) return false;
      // for all other cases we assume a match...
      return true;
    }
    // We have a full sig so can test it fully...
    if (sig_get_dim(sig) != sig_get_dim(rtType)) return false;
    if (sig_get_base_type(sig) != sig_get_base_type(rtType)) return false;
    // TBD: support for interfaces
    if (is_interface (get_class_record(sig_get_class(sig))))
      return true;
    return sub_type_of(sig_get_class(rtType), sig_get_class(sig));
  }
  // TBD: support for interfaces
  if (is_interface (get_class_record(sig)))
    return true;
  return sub_type_of(rtType, sig);
}

/**
 * Check to see if it is allowed to assign the an object of type srcSig
 * to an object of type dstSig.
 */
boolean is_assignable(TWOBYTES srcSig, TWOBYTES dstSig)
{
  // Check common cases
  if (srcSig == dstSig || dstSig == JAVA_LANG_OBJECT) return true;
  // TBD Add support for interfaces
  if (is_interface(get_class_record(sig_get_class(dstSig)))) return true;
  return sub_type_of(sig_get_class(srcSig), sig_get_class(dstSig));
}







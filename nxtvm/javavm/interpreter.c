
#include "trace.h"
#include "types.h"
#include "constants.h"
#include "classes.h"
#include "interpreter.h"
#include "platform_hooks.h"
#include "threads.h"
#include "opcodes.h"
#include "configure.h"
#include "memory.h"
#include "language.h"
#include "exceptions.h"
#include "specialclasses.h"
#include "fields.h"
#include "stack.h"
#include "poll.h"


#define F_OFFSET_MASK  0x0F

#if DEBUG_BYTECODE
extern char *OPCODE_NAME[];
#endif

// Interpreter globals:

volatile boolean gMakeRequest;
byte    gRequestCode;
unsigned int gNextProgram;
unsigned int gNextProgramSize;
unsigned int gProgramExecutions = 0;

int curPcOffset = -1;
byte *curPc;
STACKWORD *curStackTop;
STACKWORD *curLocalsBase;

byte *old_pc;
unsigned int debug_word1, debug_word2;

// Temporary globals:

// byte tempByte;
// byte *tempBytePtr;
// JFLOAT tempFloat;
// ConstantRecord *tempConstRec;
// STACKWORD tempStackWord;
// STACKWORD *tempWordPtr;
  
static byte* arrayStart;
static Object* thrownException;

/**
 * Assumes pc points to 2-byte offset, and jumps.
 */
static byte* do_goto ( byte* pc, int aCond)
{
  if (aCond)
  {
    pc += (JSHORT) (((TWOBYTES) *pc << 8) | *(pc+1));
    pc--;
  }
  else
  {
    pc += 2;
  }

  return pc;
}

#define do_isub() \
{ \
  STACKWORD poppedWord = pop_word(); \
  set_top_word (word2jint(get_top_word()) - word2jint(poppedWord)); \
}

#if FP_ARITHMETIC

STACKWORD do_fcmp (JFLOAT f1, JFLOAT f2, STACKWORD def)
{
  STACKWORD res;

  if (f1 > f2)
    res = 1;
  else if (f1 == f2)
    res = 0;
  else if (f1 < f2)
    res = -1;
  else 
    res = def;

  return res;
}

#endif

/**
 * @return A String instance, or JNULL if an exception was thrown
 *         or the static initializer of String had to be executed.
 */
Object *create_string (ConstantRecord *constantRecord, 
                                     byte *btAddr)
{
  Object *ref;
  Object *arr;
  JCHAR *dst;
  byte *src;
  byte *src_end;
  boolean retry = is_gc_retry();

  ref = new_object_checked (JAVA_LANG_STRING, btAddr);
  if (ref == JNULL)
    return JNULL;
  // Guard the partially created object against the GC
  protectedRef[0] = ref;
  arr = new_primitive_array (T_CHAR, constantRecord->constantSize);
  protectedRef[0] = JNULL;
  if (arr == JNULL)
  {
    deallocate (obj2ptr(ref), class_size (JAVA_LANG_STRING));    
    // If this is the 2nd attempt at creating this object give up!
    if (retry) throw_exception(outOfMemoryError);
    return JNULL;
  }
  // printf ("char array at %d\n", (int) arr);
  
  store_word_ns( (byte *) &(((String *) ref)->characters), 4, obj2word(arr));
  dst = jchar_array(arr);
  src = get_constant_ptr(constantRecord);
  src_end = src + constantRecord->constantSize;

  while( src < src_end)
    *dst++ = (JCHAR) (*src++);
  return ref;
}

/**
 * Pops the array index off the stack, checks
 * bounds and null reference. The array reference
 * is the top word on the stack after this operation.
 * Sets arrayStart to start of the array data area.
 * @return array index if successful, -1 if an exception has been scheduled.
 */
static int array_helper( byte *pc, STACKWORD* stackTop)
{
  unsigned int idx = word2jint(pop_word());
  byte* ptr = word2ptr(get_top_ref());

  if (ptr == JNULL)
    return -1;

  if ( /*idx < 0 ||*/ idx >= get_array_length ((Object *) ptr))
    return -2;

  arrayStart = array_start((Object*)ptr);
  return idx;
}

#define SAVE_REGS() (curPc = pc, curStackTop = stackTop, curLocalsBase = localsBase)
#define LOAD_REGS() (localsBase = curLocalsBase, stackTop = curStackTop, pc = curPc)
#define SAVE_REGS2() (curPc = (pc), curStackTop = stackTop, curLocalsBase = localsBase, curPcOffset = 0)
#define LOAD_REGS2() (localsBase = curLocalsBase, stackTop = curStackTop, pc = (curPc), curPcOffset = -1)

/**
 * Everything runs inside here, essentially.
 *
 * To be able use only a single fast test on each instruction
 * several assumptions are made:
 * - currentThread is initialized and non-null and
 *   it is not set to null by any bytecode instruction.
 * - Thus it is not allowed to call schedule_thread() in instructions,
 *   use schedule_request( REQUEST_SWITCH_THREAD) instead.
 * - Whenever gMakeRequest is false, gRequestCode is REQUEST_TICK.
 * - Thus anybody who sets gRequestCode must also set gMakeRequest to true
 *   (using schedule_request assures this).
 * - Only the request handler may set gMakeRequest to false.
 * - The millisecond timer interrupt must set gMakeRequest to true
 *   for time slices to work.
 * When executing instructions the value of pc does not point to the current
 * instruction, it begins by pointing at pc+1 byte. However it may have a value
 * of pc+1 to pc+n where n is the max size of an instruction. When not actually
 * executing instructions pc will point to the next instruction. All of this
 * presents a problem for operations like invoking methods and throwing
 * exceptions, because they need a consistant state. So we define a set of rules
 * to make things easier. We use the macro SAVE_REG/LOAD_REG to define a safe
 * point and we must ensure that at these points the value of curPC is in a
 * predictable state. In particular we require that...
 * 1. A VM function can use the getPC macro to obtain a pointer to the
 *    currently executing instruction. If the instruction is complete (during
 *    a thread switch for example then getPC will return the instruction about
 *    to be executed.
 * 2. If a VM function wishes to perform a jump operation, then it can do so
 *    by assigning directly to curPc. Note that in some cases (allocations and
 *    exceptions) it may also need to take additional actions (returning JNULL)
 *    to ensure that the current instruction is aborted.
 * In particular the above rules ensure that the following sequence...
 *   curPc = getPC();
 * will result in the current instruction being re-started.
 * The macros above enforce this condition. They use the addiional variable
 * curPcOffset to allow correction of the value of curPc.
 *
 * We have a similar issue with the stack. In some cases (primarily method calls
 * and memory allocation), we may need to be able to restart the instruction.
 * to allow this the stack should be left unchanged until after the call to
 * LOAD_REGS. 
 */


void engine()
{
  FOURBYTES switch_time = get_sys_time() + TICKS_PER_TIME_SLICE;;
  STACKWORD tempStackWord;
  STACKWORD *tempWordPtr;
  ConstantRecord *tempConstRec;
  int tempInt;
  byte *pc = curPc;
  STACKWORD *stackTop = curStackTop;
  STACKWORD *localsBase = curLocalsBase;

  assert( currentThread != null, INTERPRETER0);

  schedule_request( REQUEST_SWITCH_THREAD);

 LABEL_ENGINELOOP: 
  instruction_hook();

  assert( currentThread != null, INTERPRETER1);

  while( gMakeRequest)
  {
    byte requestCode = gRequestCode;
    FOURBYTES now;

    SAVE_REGS2();
    gMakeRequest = false;
    now = get_sys_time();
    gRequestCode = REQUEST_TICK;
    
    tick_hook();

    if( requestCode == REQUEST_EXIT)
    {
      return;
    }
    if( requestCode == REQUEST_SWITCH_THREAD
        || now >= switch_time){
#if DEBUG_THREADS
      printf ("switching thread: %d\n", (int)ticks_until_switch);
#endif
      if ((int)(switch_time - now) >= 1)
      {
        run_collector();
        gMakeRequest = 0;
      }
      switch_time = get_sys_time() + TICKS_PER_TIME_SLICE;
      switch_thread();
#if DEBUG_THREADS
      printf ("done switching thread\n");
#endif
      switch_thread_hook();
    }
    else if (switch_time - now == 1)
      run_collector();
    if( currentThread == null   /* no runnable thread */
        && gRequestCode == REQUEST_TICK){ /* no important request */
      idle_hook();
      schedule_request( REQUEST_SWITCH_THREAD);
    }

    LOAD_REGS2();
  }

  assert( gRequestCode == REQUEST_TICK, INTERPRETER2);
  assert( currentThread != null, INTERPRETER3);

  //-----------------------------------------------
  // SWITCH BEGINS HERE
  //-----------------------------------------------

  #if DEBUG_BYTECODE
  printf ("0x%X: \n", (int) pc);
  printf ("OPCODE (0x%X) %s\n", (int) *pc, OPCODE_NAME[*pc]);
  #endif

  // experimental feature: "fast loop", to disable place this label just next to LABEL_ENGINELOOP
 LABEL_ENGINEFASTLOOP: 

  // uncomment the following line if you want to see the opcode after data abort
  // old_pc = pc;
  
  switch (*pc++)
  {
    case OP_NOP:
        goto LABEL_ENGINELOOP;

    #include "op_stack.hc"
    #include "op_locals.hc"
    #include "op_arrays.hc"
    #include "op_objects.hc"
    #include "op_control.hc"
    #include "op_other.hc"
    #include "op_conversions.hc"
    #include "op_logical.hc"
    #include "op_arithmetic.hc"
    #include "op_methods.hc"

/*
#ifdef VERIFY
	default:
		assert(false, (TWOBYTES)(pc-1) % 10000);
		break;
#endif
*/
  }

  //-----------------------------------------------
  // SWITCH ENDS HERE
  //-----------------------------------------------

   #if !FP_ARITHMETIC

   throw_exception (noSuchMethodError);

   #else
  
   // This point should never be reached

   #ifdef VERIFY
   assert (false, 1000 + *pc);
   #endif // VERIFY

   #endif // FP_ARITHMETIC
}






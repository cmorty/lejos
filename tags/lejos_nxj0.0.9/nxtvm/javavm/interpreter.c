
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
#include "rconsole.h"
#include "debug.h"
#include <string.h>
//#include <math.h>
extern double __ieee754_fmod(double, double);

#define F_OFFSET_MASK  0x0F

#if DEBUG_BYTECODE
extern char *OPCODE_NAME[];
#endif

// Interpreter globals:

volatile boolean gMakeRequest;
byte    gRequestCode;
unsigned int gVMOptions = 0;

int curPcOffset = -1;
byte *curPc;
STACKWORD *curStackTop;
STACKWORD *curLocalsBase;

byte *old_pc;
unsigned int debug_word1, debug_word2;
int thrownException;

// Temporary globals:

// byte tempByte;
// byte *tempBytePtr;
// JFLOAT tempFloat;
// ConstantRecord *tempConstRec;
// STACKWORD tempStackWord;
// STACKWORD *tempWordPtr;
  
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

STACKWORD do_dcmp (double f1, double f2, STACKWORD def)
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

#if LONG_ARITHMETIC
STACKWORD do_lcmp (LLONG l1, LLONG l2, STACKWORD def)
{
  STACKWORD res;

  if (l1 > l2)
    res = 1;
  else if (l1 == l2)
    res = 0;
  else if (l1 < l2)
    res = -1;
  else
    res = def;

  return res;
}
#endif

/**
 * Pops the array index off the stack, checks
 * bounds and null reference. The array reference
 * is the top word on the stack after this operation.
 * Sets arrayStart to start of the array data area.
 * @return array index if successful, -1 if an exception has been scheduled.
 */
static STACKWORD *array_helper(unsigned int idx, Object *obj, int sz)
{
  if (obj == JNULL)
  {
    thrownException = JAVA_LANG_NULLPOINTEREXCEPTION;
    return NULL;
  }

  if ( /*idx < 0 ||*/ idx >= get_array_length(obj))
  {
    thrownException = JAVA_LANG_ARRAYINDEXOUTOFBOUNDSEXCEPTION;
    return NULL;
  }
  return (STACKWORD *) (array_start(obj) + idx*sz);
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
 *
 * The following macros allow two types of dispatch to be defined. One using
 * a conventional switch statement, the other using a dispatch table. Note
 * that the dispatch table uses a relative offset to allow the table to be
 * stored in flash memory.
 */
#if FAST_DISPATCH
// Fast byte code dispatch. Uses the GCC labels as values extension.
#define OPCODE(op) L_##op: 
#define UNUSED_OPCODE(op) 
#define MULTI_OPCODE(op)
#define DISPATCH1 goto *(&&CHECK_EVENT + dispatchTable[*pc++])
#define DISPATCH goto *(&&CHECK_EVENT + dispatch[*pc++])
#define DISPATCH_CHECKED {instruction_hook(); DISPATCH1;}
#define START_DISPATCH DISPATCH;
#define END_DISPATCH
#define DISPATCH_EVENTS CHECK_EVENT: (pc--, dispatchTable = dispatch);
#define INIT_DISPATCH (pc++, checkEvent = forceCheck, dispatchTable = dispatch);
DISPATCH_LABEL * volatile dispatchTable;
DISPATCH_LABEL *checkEvent;
#else
// Standard dispatch code uses a switch statement
#define OPCODE(op) case op:
#define UNUSED_OPCODE(op) case op: 
#define MULTI_OPCODE(op) case op:
#define DISPATCH_CHECKED goto CHECK_EVENT
#define DISPATCH goto DISPATCH_NEXT
#define START_DISPATCH DISPATCH_NEXT: switch(*pc++) {
#define END_DISPATCH }
#define DISPATCH_EVENTS CHECK_EVENT: instruction_hook();
#define INIT_DISPATCH
#endif

void engine()
{
  FOURBYTES switch_time = get_sys_time() + TICKS_PER_TIME_SLICE;;
  STACKWORD tempStackWord;
  STACKWORD *tempWordPtr;
  ConstantRecord *tempConstRec;
  int tempInt;
  // Note the following structs were originally declared for each opcode
  // that required them. However GCC does not seem to like sharing stack space
  // between blocks and so this resulted in a large amount of wasted stack
  // space. So for now we declare them here.
  JLONG l1, l2;
  JDOUBLE d1, d2;
  byte *pc = curPc;
  STACKWORD *stackTop = curStackTop;
  STACKWORD *localsBase = curLocalsBase;
#if FAST_DISPATCH
// The following table provides the main opcode dispatch table.
// One entry per opcode, in opcode order. The subtraction makes
// the value a relative offset allowing a smaller table and
// allowing the table to be stored in ROM
//
// For some odd reason with some versions of gcc having an none multiple of 4
// unique label entries in this table, results in approximately 1.5K more
// code! To avoid this we tune the unique entry count by doubling up the
// entries for opcodes that have multiple labels for the same code.
static DISPATCH_LABEL dispatch[] =
{
  &&L_OP_NOP - &&CHECK_EVENT,
  &&L_OP_ACONST_NULL - &&CHECK_EVENT,
  &&L_OP_ICONST_M1 - &&CHECK_EVENT,
  &&L_OP_ICONST_0 - &&CHECK_EVENT,
  &&L_OP_ICONST_1 - &&CHECK_EVENT,
  &&L_OP_ICONST_2 - &&CHECK_EVENT,
  &&L_OP_ICONST_3 - &&CHECK_EVENT,
  &&L_OP_ICONST_4 - &&CHECK_EVENT,
  &&L_OP_ICONST_5 - &&CHECK_EVENT,
  &&L_OP_LCONST_0 - &&CHECK_EVENT,
  &&L_OP_LCONST_1 - &&CHECK_EVENT,
  &&L_OP_FCONST_0 - &&CHECK_EVENT,
  &&L_OP_FCONST_1 - &&CHECK_EVENT,
  &&L_OP_FCONST_2 - &&CHECK_EVENT,
  &&L_OP_DCONST_0 - &&CHECK_EVENT,
  &&L_OP_DCONST_1 - &&CHECK_EVENT,
  &&L_OP_BIPUSH - &&CHECK_EVENT,
  &&L_OP_SIPUSH - &&CHECK_EVENT,
  &&L_OP_LDC - &&CHECK_EVENT,
  &&L_OP_XXXUNUSEDXXX - &&CHECK_EVENT, //OP_LDC_W
  &&L_OP_LDC2_W - &&CHECK_EVENT,
  &&L_OP_ILOAD - &&CHECK_EVENT,
  &&L_OP_LLOAD - &&CHECK_EVENT,
  &&L_OP_FLOAD - &&CHECK_EVENT,
  &&L_OP_DLOAD - &&CHECK_EVENT,
  &&L_OP_ALOAD - &&CHECK_EVENT,
  &&L_OP_ILOAD_0 - &&CHECK_EVENT,
  &&L_OP_ILOAD_1 - &&CHECK_EVENT,
  &&L_OP_ILOAD_2 - &&CHECK_EVENT,
  &&L_OP_ILOAD_3 - &&CHECK_EVENT,
  &&L_OP_LLOAD_0 - &&CHECK_EVENT,
  &&L_OP_LLOAD_1 - &&CHECK_EVENT,
  &&L_OP_LLOAD_2 - &&CHECK_EVENT,
  &&L_OP_LLOAD_3 - &&CHECK_EVENT,
  &&L_OP_FLOAD_0 - &&CHECK_EVENT,
  &&L_OP_FLOAD_1 - &&CHECK_EVENT,
  &&L_OP_FLOAD_2 - &&CHECK_EVENT,
  &&L_OP_FLOAD_3 - &&CHECK_EVENT,
  &&L_OP_DLOAD_0 - &&CHECK_EVENT,
  &&L_OP_DLOAD_1 - &&CHECK_EVENT,
  &&L_OP_DLOAD_2 - &&CHECK_EVENT,
  &&L_OP_DLOAD_3 - &&CHECK_EVENT,
  &&L_OP_ALOAD_0 - &&CHECK_EVENT,
  &&L_OP_ALOAD_1 - &&CHECK_EVENT,
  &&L_OP_ALOAD_2 - &&CHECK_EVENT,
  &&L_OP_ALOAD_3 - &&CHECK_EVENT,
  &&L_OP_IALOAD - &&CHECK_EVENT,
  &&L_OP_LALOAD - &&CHECK_EVENT,
  &&L_OP_FALOAD - &&CHECK_EVENT,
  &&L_OP_DALOAD - &&CHECK_EVENT,
  &&L_OP_AALOAD - &&CHECK_EVENT,
  &&L_OP_BALOAD - &&CHECK_EVENT,
  &&L_OP_CALOAD - &&CHECK_EVENT,
  &&L_OP_SALOAD - &&CHECK_EVENT,
  &&L_OP_ISTORE - &&CHECK_EVENT,
  &&L_OP_LSTORE - &&CHECK_EVENT,
  &&L_OP_FSTORE - &&CHECK_EVENT,
  &&L_OP_DSTORE - &&CHECK_EVENT,
  &&L_OP_ASTORE - &&CHECK_EVENT,
  &&L_OP_ISTORE_0 - &&CHECK_EVENT,
  &&L_OP_ISTORE_1 - &&CHECK_EVENT,
  &&L_OP_ISTORE_2 - &&CHECK_EVENT,
  &&L_OP_ISTORE_3 - &&CHECK_EVENT,
  &&L_OP_LSTORE_0 - &&CHECK_EVENT,
  &&L_OP_LSTORE_1 - &&CHECK_EVENT,
  &&L_OP_LSTORE_2 - &&CHECK_EVENT,
  &&L_OP_LSTORE_3 - &&CHECK_EVENT,
  &&L_OP_FSTORE_0 - &&CHECK_EVENT,
  &&L_OP_FSTORE_1 - &&CHECK_EVENT,
  &&L_OP_FSTORE_2 - &&CHECK_EVENT,
  &&L_OP_FSTORE_3 - &&CHECK_EVENT,
  &&L_OP_DSTORE_0 - &&CHECK_EVENT,
  &&L_OP_DSTORE_1 - &&CHECK_EVENT,
  &&L_OP_DSTORE_2 - &&CHECK_EVENT,
  &&L_OP_DSTORE_3 - &&CHECK_EVENT,
  &&L_OP_ASTORE_0 - &&CHECK_EVENT,
  &&L_OP_ASTORE_1 - &&CHECK_EVENT,
  &&L_OP_ASTORE_2 - &&CHECK_EVENT,
  &&L_OP_ASTORE_3 - &&CHECK_EVENT,
  &&L_OP_IASTORE - &&CHECK_EVENT,
  &&L_OP_LASTORE - &&CHECK_EVENT,
  &&L_OP_FASTORE - &&CHECK_EVENT,
  &&L_OP_DASTORE - &&CHECK_EVENT,
  &&L_OP_AASTORE - &&CHECK_EVENT,
  &&L_OP_BASTORE - &&CHECK_EVENT,
  &&L_OP_CASTORE - &&CHECK_EVENT,
  &&L_OP_SASTORE - &&CHECK_EVENT,
  &&L_OP_POP - &&CHECK_EVENT,
  &&L_OP_POP2 - &&CHECK_EVENT,
  &&L_OP_DUP - &&CHECK_EVENT,
  &&L_OP_DUP_X1 - &&CHECK_EVENT,
  &&L_OP_DUP_X2 - &&CHECK_EVENT,
  &&L_OP_DUP2 - &&CHECK_EVENT,
  &&L_OP_DUP2_X1 - &&CHECK_EVENT,
  &&L_OP_DUP2_X2 - &&CHECK_EVENT,
  &&L_OP_SWAP - &&CHECK_EVENT,
  &&L_OP_IADD - &&CHECK_EVENT,
  &&L_OP_LADD - &&CHECK_EVENT,
  &&L_OP_FADD - &&CHECK_EVENT,
  &&L_OP_DADD - &&CHECK_EVENT,
  &&L_OP_ISUB - &&CHECK_EVENT,
  &&L_OP_LSUB - &&CHECK_EVENT,
  &&L_OP_FSUB - &&CHECK_EVENT,
  &&L_OP_DSUB - &&CHECK_EVENT,
  &&L_OP_IMUL - &&CHECK_EVENT,
  &&L_OP_LMUL - &&CHECK_EVENT,
  &&L_OP_FMUL - &&CHECK_EVENT,
  &&L_OP_DMUL - &&CHECK_EVENT,
  &&L_OP_IDIV - &&CHECK_EVENT,
  &&L_OP_LDIV - &&CHECK_EVENT,
  &&L_OP_FDIV - &&CHECK_EVENT,
  &&L_OP_DDIV - &&CHECK_EVENT,
  &&L_OP_IREM - &&CHECK_EVENT,
  &&L_OP_LREM - &&CHECK_EVENT,
  &&L_OP_FREM - &&CHECK_EVENT,
  &&L_OP_DREM - &&CHECK_EVENT,
  &&L_OP_INEG - &&CHECK_EVENT,
  &&L_OP_LNEG - &&CHECK_EVENT,
  &&L_OP_FNEG - &&CHECK_EVENT,
  &&L_OP_DNEG - &&CHECK_EVENT,
  &&L_OP_ISHL - &&CHECK_EVENT,
  &&L_OP_LSHL - &&CHECK_EVENT,
  &&L_OP_ISHR - &&CHECK_EVENT,
  &&L_OP_LSHR - &&CHECK_EVENT,
  &&L_OP_IUSHR - &&CHECK_EVENT,
  &&L_OP_LUSHR - &&CHECK_EVENT,
  &&L_OP_IAND - &&CHECK_EVENT,
  &&L_OP_LAND - &&CHECK_EVENT,
  &&L_OP_IOR - &&CHECK_EVENT,
  &&L_OP_LOR - &&CHECK_EVENT,
  &&L_OP_IXOR - &&CHECK_EVENT,
  &&L_OP_LXOR - &&CHECK_EVENT,
  &&L_OP_IINC - &&CHECK_EVENT,
  &&L_OP_I2L - &&CHECK_EVENT,
  &&L_OP_I2F - &&CHECK_EVENT,
  &&L_OP_I2D - &&CHECK_EVENT,
  &&L_OP_L2I - &&CHECK_EVENT,
  &&L_OP_L2F - &&CHECK_EVENT,
  &&L_OP_L2D - &&CHECK_EVENT,
  &&L_OP_F2I - &&CHECK_EVENT,
  &&L_OP_F2L - &&CHECK_EVENT,
  &&L_OP_F2D - &&CHECK_EVENT,
  &&L_OP_D2I - &&CHECK_EVENT,
  &&L_OP_D2L - &&CHECK_EVENT,
  &&L_OP_D2F - &&CHECK_EVENT,
  &&L_OP_I2B - &&CHECK_EVENT,
  &&L_OP_I2C - &&CHECK_EVENT,
  &&L_OP_I2S - &&CHECK_EVENT,
  &&L_OP_LCMP - &&CHECK_EVENT,
  &&L_OP_FCMPL - &&CHECK_EVENT,
  &&L_OP_FCMPG - &&CHECK_EVENT,
  &&L_OP_DCMPL - &&CHECK_EVENT,
  &&L_OP_DCMPG - &&CHECK_EVENT,
  &&L_OP_IFEQ - &&CHECK_EVENT,
  &&L_OP_IFNE - &&CHECK_EVENT,
  &&L_OP_IFLT - &&CHECK_EVENT,
  &&L_OP_IFGE - &&CHECK_EVENT,
  &&L_OP_IFGT - &&CHECK_EVENT,
  &&L_OP_IFLE - &&CHECK_EVENT,
  &&L_OP_IF_ICMPEQ - &&CHECK_EVENT,
  &&L_OP_IF_ICMPNE - &&CHECK_EVENT,
  &&L_OP_IF_ICMPLT - &&CHECK_EVENT,
  &&L_OP_IF_ICMPGE - &&CHECK_EVENT,
  &&L_OP_IF_ICMPGT - &&CHECK_EVENT,
  &&L_OP_IF_ICMPLE - &&CHECK_EVENT,
  &&L_OP_IF_ACMPEQ - &&CHECK_EVENT,
  &&L_OP_IF_ACMPNE - &&CHECK_EVENT,
  &&L_OP_GOTO - &&CHECK_EVENT,
  &&L_OP_JSR - &&CHECK_EVENT,
  &&L_OP_RET - &&CHECK_EVENT,
  &&L_OP_TABLESWITCH - &&CHECK_EVENT,
  &&L_OP_LOOKUPSWITCH - &&CHECK_EVENT,
  &&L_OP_IRETURN - &&CHECK_EVENT,
  &&L_OP_LRETURN - &&CHECK_EVENT,
  &&L_OP_FRETURN - &&CHECK_EVENT,
  &&L_OP_DRETURN - &&CHECK_EVENT,
  &&L_OP_ARETURN - &&CHECK_EVENT,
  &&L_OP_RETURN - &&CHECK_EVENT,
  &&L_OP_GETSTATIC - &&CHECK_EVENT,
  &&L_OP_PUTSTATIC - &&CHECK_EVENT,
  &&L_OP_GETFIELD - &&CHECK_EVENT,
  &&L_OP_PUTFIELD - &&CHECK_EVENT,
  &&L_OP_INVOKEVIRTUAL - &&CHECK_EVENT,
  &&L_OP_INVOKESPECIAL - &&CHECK_EVENT,
  &&L_OP_INVOKESTATIC - &&CHECK_EVENT,
  &&L_OP_XXXUNUSEDXXX - &&CHECK_EVENT, //OP_INVOKEINTERFACE
  &&L_OP_XXXUNUSEDXXX - &&CHECK_EVENT,
  &&L_OP_NEW - &&CHECK_EVENT,
  &&L_OP_NEWARRAY - &&CHECK_EVENT,
  &&L_OP_ANEWARRAY - &&CHECK_EVENT,
  &&L_OP_ARRAYLENGTH - &&CHECK_EVENT,
  &&L_OP_ATHROW - &&CHECK_EVENT,
  &&L_OP_CHECKCAST - &&CHECK_EVENT,
  &&L_OP_INSTANCEOF - &&CHECK_EVENT,
  &&L_OP_MONITORENTER - &&CHECK_EVENT,
  &&L_OP_MONITOREXIT - &&CHECK_EVENT,
  &&L_OP_WIDE - &&CHECK_EVENT,
  &&L_OP_MULTIANEWARRAY - &&CHECK_EVENT,
  &&L_OP_IFNULL - &&CHECK_EVENT,
  &&L_OP_IFNONNULL - &&CHECK_EVENT,
  &&L_OP_XXXUNUSEDXXX - &&CHECK_EVENT, //OP_GOTO_W
  &&L_OP_XXXUNUSEDXXX - &&CHECK_EVENT, //OP_JSR_W
  &&L_OP_XXXUNUSEDXXX - &&CHECK_EVENT, //OP_BREAKPOINT
  &&L_OP_GETSTATIC_1 - &&CHECK_EVENT, // Note use of duplicate entries
  &&L_OP_GETSTATIC_1 - &&CHECK_EVENT, // see the comment above for details
  &&L_OP_GETSTATIC_1 - &&CHECK_EVENT,
  &&L_OP_GETSTATIC_1 - &&CHECK_EVENT,
  &&L_OP_PUTSTATIC_1 - &&CHECK_EVENT,
  &&L_OP_PUTSTATIC_1 - &&CHECK_EVENT,
  &&L_OP_PUTSTATIC_1 - &&CHECK_EVENT,
  &&L_OP_PUTSTATIC_1 - &&CHECK_EVENT,
  &&L_OP_LDC_1 - &&CHECK_EVENT,
  &&L_OP_LDC_2 - &&CHECK_EVENT,
  &&L_OP_LDC_2 - &&CHECK_EVENT,
  &&L_OP_LDC_2 - &&CHECK_EVENT, // Duplicate entries. See above
  &&L_OP_GETFIELD_1 - &&CHECK_EVENT,
  &&L_OP_PUTFIELD_1 - &&CHECK_EVENT,
  

};

// The following table is used to force the interpreter to jump to the
// check event code rather than the next instruction. Basically causes a 
// jump to the check event code.
static DISPATCH_LABEL forceCheck[] = 
{
 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
};
#endif

  assert( currentThread != null, INTERPRETER0);
  INIT_DISPATCH
  schedule_request( REQUEST_SWITCH_THREAD);

  assert( currentThread != null, INTERPRETER1);
  DISPATCH_EVENTS
  while( gMakeRequest)
  {
    byte requestCode;
    FOURBYTES now;

    SAVE_REGS2();
    if(gRequestCode == REQUEST_EXIT)
    {
      if (!debug_program_exit())
        return;
    }
    tick_hook();
    requestCode = gRequestCode;
    gRequestCode = REQUEST_TICK;
    gMakeRequest = false;
    now = get_sys_time();

    if( requestCode == REQUEST_SWITCH_THREAD
        || now >= switch_time){
#if DEBUG_THREADS
      printf ("switching thread: %d\n", (int)ticks_until_switch);
#endif
      if ((int)(switch_time - now) >= 1)
      {
        run_collector();
        gMakeRequest = 0;
        now = get_sys_time();
      }
      switch_time = now + TICKS_PER_TIME_SLICE;
      switch_thread(now);
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

  START_DISPATCH

    #include "op_skip.hc"
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
    #include "op_unused.hc"

  END_DISPATCH

  //-----------------------------------------------
  // SWITCH ENDS HERE
  //-----------------------------------------------

  return;

  LABEL_THROW_ARITHMETIC_EXCEPTION:
    thrownException = JAVA_LANG_ARITHMETICEXCEPTION;
    goto LABEL_THROW_EXCEPTION;

  LABEL_THROW_NULLPTR_EXCEPTION:
    thrownException = JAVA_LANG_NULLPOINTEREXCEPTION;
    goto LABEL_THROW_EXCEPTION;

  LABEL_THROW_EXCEPTION:
    SAVE_REGS();
    throw_new_exception(thrownException);
    LOAD_REGS();
    DISPATCH_CHECKED;
}






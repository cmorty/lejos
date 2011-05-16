
#include "classes.h"
#include "language.h"
#include "constants.h"
#include "trace.h"
#include "memory.h"
#include "rconsole.h"
#ifndef _THREADS_H
#define _THREADS_H

#define NEW              0 // Just been created
#define DEAD             1 // run() has exited
#define STARTED          2 // start() has been called but we haven't run yet
#define RUNNING          3 // We're running!
#define MON_WAITING      4 // Trying to enter a synchronized block
#define CONDVAR_WAITING  5 // Someone called wait() on us in a synchronized block.
#define SLEEPING         6 // ZZZZZzzzzzzzz
#define JOIN             7 // Waiting for another thread to exit
#define SYSTEM_WAITING   8 // Waiting on a system var
#define SUSPENDED        0x80 // Or with the above to suspend

#define INTERRUPT_CLEARED    0
#define INTERRUPT_REQUESTED  1
#define INTERRUPT_GRANTED    2

// These values must match the statics in Thread.java
#define MIN_PRIORITY  1
#define NORM_PRIORITY 5
#define MAX_PRIORITY  10

#define SF_SIZE (sizeof(StackFrame))

extern Thread *currentThread;
extern byte gThreadCounter;
extern REFERENCE threads;
extern Thread **threadQ;
extern byte gProgramNumber;
extern boolean gRequestSuicide;

/**
 * A stack frame record
 */
typedef struct S_StackFrame
{
  MethodRecord *methodRecord;
  // Object's monitor if method is synchronized
  Object *monitor;
  // The following field is constant for a given stack frame.
  STACKWORD *localsBase;
  // The following fields only need to be assigned to on switch_thread.
  STACKWORD *stackTop;
  byte *pc;
} StackFrame;

extern void init_threads();
extern int init_thread (Thread *thread);
extern void enter_monitor (Thread *pThread, Object* obj);
extern void exit_monitor (Thread *pThread, Object* obj);
extern boolean switch_thread(FOURBYTES now);
extern void join_thread(Thread *thread, const FOURBYTES time);
extern void dequeue_thread(Thread *thread);
extern void enqueue_thread(Thread *thread);
extern void system_wait(Object *obj);
extern void system_notify(Object *obj, const boolean all);
extern int monitor_wait(Object *obj, const FOURBYTES time);
extern int monitor_notify(Object *obj, const boolean all);
extern void monitor_notify_unchecked(Object *obj, const boolean all);
extern void suspend_thread(Thread *thread);
extern void resume_thread(Thread *thread);

#define stackframe_array_ptr()   (word2ptr(currentThread->stackFrameArray))
#define stack_array_ptr()        (word2ptr(currentThread->stackArray))
#define is_reference_array_ptr() (word2ptr(currentThread->isReferenceArray))
#define stackframe_array()       ((StackFrame *) array_start(stackframe_array_ptr()))
#define current_stackframe()     (stackframe_array() + (byte)(currentThread->stackFrameIndex))
#define stack_array()            ((STACKWORD *) (array_start(stack_array_ptr())))
#define is_reference_array()     ((JBYTE *) (array_start(is_reference_array_ptr()))
#define set_program_number(N_)   {gProgramNumber = (N_);}
#define inc_program_number()     {if (++gProgramNumber >= get_num_entry_classes()) gProgramNumber = 0;}
#define get_program_number()     gProgramNumber 
#define get_sync(obj) (get_class_index((Object *)(obj)) == JAVA_LANG_CLASS ? staticSyncBase + ((ClassRecord *)(obj) - get_class_base()) : &(((Object *)(obj))->sync))

/**
 * Sets thread state to SLEEPING.
 * Thread should be switched immediately after calling this method.
 */
static inline void sleep_thread (const FOURBYTES time)
{
  #ifdef VERIFY
  assert (currentThread != JNULL, THREADS3);
  assert (currentThread->state != MON_WAITING, THREADS9);
  #endif

  currentThread->state = SLEEPING;
  currentThread->sleepUntil = get_sys_time() + time; 	
}

static inline int get_thread_priority(Thread *thread)
{
  #if DEBUG_THREADS
  printf("Thread priority is %d\n", thread->priority);
  #endif
  return thread->priority;
}

/**
 * Mark thread as interrupted.
 */
static inline void interrupt_thread(Thread *thread)
{
  thread->interruptState = INTERRUPT_REQUESTED;
}

extern void set_thread_priority(Thread *thread, const FOURBYTES priority);

#endif




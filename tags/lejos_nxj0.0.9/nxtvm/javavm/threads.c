
#include "types.h"
#include "trace.h"
#include "platform_hooks.h"
#include "constants.h"
#include "specialsignatures.h"
#include "specialclasses.h"
#include "threads.h"
#include "classes.h"
#include "language.h"
#include "configure.h"
#include "interpreter.h"
#include "memory.h"
#include "exceptions.h"
#include "stack.h"
#include <string.h>
#define NO_OWNER 0x00
#define MAX_ID 0xff

#define get_stack_frame() ((StackFrame *) (currentThread->currentStackFrame))

/**
 * Thread currently being executed by engine(). Threads exist in an
 * intrinsic circular list.
 */
Thread* currentThread;

/**
 * Initial thread created at system boot time.
 */
Thread* bootThread;

/**
 * Priority queue of threads. Entry points at the last thread in the queue.
 */
REFERENCE threads;
Thread **threadQ;

/**
 * Current program number, i.e. number of 'main()'s hanging around
 */
byte gProgramNumber;

void update_stack_frame (StackFrame *stackFrame)
{
  stackFrame->stackTop = curStackTop;
  stackFrame->pc = curPc;
}  

void update_registers (StackFrame *stackFrame)
{
  curPc = stackFrame->pc;
  curStackTop = stackFrame->stackTop;
  curLocalsBase = stackFrame->localsBase;
}

/* Turns out inlines aren't really inlined.
inline byte get_thread_id (Object *obj)
{
  return obj->threadId;
}

inline void set_thread_id (Object *obj, byte threadId)
{
  obj->threadId = threadId;
}

inline void inc_monitor_count (Object *obj)
{
  obj->monitorCount++;
}

inline void set_monitor_count (Object *obj, byte count)
{
  obj->monitorCount = count;
}
*/

#define get_thread_id(sync) ((sync)->threadId)
#define set_thread_id(sync,_threadId) ((sync)->threadId = (_threadId))
#define inc_monitor_count(sync) ((sync)->monitorCount++)
#define set_monitor_count(obj,count) ((sync)->monitorCount = (count))

/**
 * Initialise the thread pool and create the bootThread.
 * Note we use a Java object so that we can make
 * the pool available to Java. But to do this we have to take great care
 * because the gc makes use of the thread system! So we must ensure that
 * currentThread is pointing to a valid structure before performing any
 * allocations.
 **/
void init_threads()
{
  int i;
  // Allocate temporary thread structure for use during system startup
  Thread initThread;
  initThread.threadId = 255;
  initThread.state = SUSPENDED;
  currentThread = &initThread;

  // Now create the basic threading system
  threads = ptr2ref(new_single_array(ALJAVA_LANG_OBJECT, 10));
  threadQ = (Thread **)ref_array(threads);
  Thread **pQ = threadQ;
  currentThread = JNULL;
  for (i = 0; i<10; i++)
  {
    *pQ++ = null;
  }
  memory_base[MEM_THREADS] = (byte *)threadQ;
  memory_base[MEM_IMAGE] = (byte *)installedBinary;
  memory_base[MEM_STATICS] = (byte *)classStaticStateBase;

  // Create the system boot thread
  bootThread = (Thread *)new_object_for_class(JAVA_LANG_THREAD);
  init_thread(bootThread);
  // Now we have a valid thread use it.
  currentThread = bootThread;
}

/**
 * Find a free thread id and return it.
 */
static int get_new_thread_id()
{
  // Thread ids must be between 1 and 255, so we need to recycle ids if we
  // create more than 254 threads. We do this by using a bitmap to mark
  // each id in use then search this map looking for an unused (i.e. zero)
  // entry.
#define BITSPERWORD (8*sizeof(FOURBYTES))
  FOURBYTES inUse[MAX_ID/BITSPERWORD];
  int i;
  memset(inUse, 0, sizeof(inUse));
  // scan all threads and mark all in use ids
  for (i=0; i < MAX_PRIORITY; i++)
  {
    Thread *pFirstThread = threadQ[i];
    if (pFirstThread)
    {
      Thread *pThread = pFirstThread;
      do {
        inUse[pThread->threadId/BITSPERWORD] |= 1 << (pThread->threadId % BITSPERWORD);
        pThread = word2ptr(pThread->nextThread);
      } while (pThread != pFirstThread);
    }
  }
  // id 0 is always in use...
  *inUse |= 1;
  // find a free id...
  for(i = 0; i < sizeof(inUse)/sizeof(FOURBYTES); i++)
  {
    FOURBYTES w = inUse[i];
    if (w != ~0)
    {
      int j = 0;
      for(j=0; w & 1; j++)
        w >>= 1;
      return i*BITSPERWORD + j;
    }
  }
  // failed to find a free id...
  return NO_OWNER;
}

/**
 * Allocate stack frames
 * Allocate ID
 * Insert into run list
 * Mark thread as STARTED
 */
int init_thread (Thread *thread)
{
  /* Create a new thread and allocate the resources for it. When being used
   * with the GC we must take steps to allow the calling instruction to be
   * re-started. So take care of any changes to global state.
   */
  thread->threadId = get_new_thread_id();

  if (thread->threadId == NO_OWNER)
    return throw_new_exception(JAVA_LANG_OUTOFMEMORYERROR);
  
  // Catch the primordial thread
  if (currentThread == null)
    thread->priority = NORM_PRIORITY;
  
  #if DEBUG_THREADS
  printf("Setting intial priority to %d\n", thread->priority);
  #endif

  if (thread->state != NEW)
    return throw_new_exception(JAVA_LANG_ILLEGALSTATEEXCEPTION);
  // Allocate space for stack frames.
  if (init_stacks(thread) < 0)
  {
    return EXEC_RETRY;
  }
  
  #ifdef VERIFY
  assert (is_array (word2obj (thread->stackFrameArray)), THREADS0);
  assert (is_array (word2obj (thread->stackArray)), THREADS1);
  #endif
  thread->stackFrameIndex = 0;
  thread->state = STARTED;
    
  enqueue_thread(thread);

  return EXEC_CONTINUE;
}

/**
 * Switches to next thread:
 *
 * do
 *   get next thread
 *   if waiting, grab monitor and run
 *   if sleeping and timer expired, run
 *   if DEAD, clean up and use current thread
 *   if started, initialize and run
 * until selected thread can run
 *  
 * @return false iff there are no live threads
 *         to switch to.
 */
 
boolean switch_thread(FOURBYTES now)
{
  Thread *anchorThread, *previousThread, *candidate;
  Thread **pThreadQ;
  boolean nonDaemonRunnable = false;
  StackFrame *stackFrame = null;
  short i;
  #if DEBUG_THREADS || DEBUG_BYTECODE
  printf ("------ switch_thread: currentThread at %d\n", (int) currentThread);
  #endif

  if (currentThread != null)
  {
    // Only current threads can die. Tidy up dead threads
    if (currentThread->state == DEAD)
    {
      #if DEBUG_THREADS
      printf ("Tidying up DEAD thread %d: %d\n", (int) currentThread, (int)currentThread->threadId);
      #endif
      #if REMOVE_DEAD_THREADS
      free_stacks(currentThread);
      #ifdef SAFE
      currentThread->stackFrameArray = JNULL;
      currentThread->stackArray = JNULL;
      #endif // SAFE
      #endif // REMOVE_DEAD_THREADS
    
      // Remove thread from queue.
      dequeue_thread(currentThread);
    }
    else { // Save context information
      stackFrame = current_stackframe();

#if DEBUG_THREADS
      printf ("switchThread: current stack frame: %d\n", (int) stackFrame);
#endif
  
      update_stack_frame (stackFrame);
    }
  }

  currentThread = null;
  
  // Loop until a frame is found that can be made to run.
  for (i=MAX_PRIORITY-1; i >= 0; i--) {
    pThreadQ = &threadQ[i];

    previousThread = anchorThread = *pThreadQ;
    if (!previousThread)
      continue;

    do
    {
      candidate = word2ptr(previousThread->nextThread);

      #if DEBUG_THREADS
      printf ("Checking state of thread %d(%d)(s=%d,p=%d,i=%d,d=%d)\n",
      	(int)candidate,
      	(int)candidate->threadId,
      	(int)candidate->state,
      	(int)candidate->priority,
      	(int)candidate->interruptState,
      	(int)candidate->daemon
             );
      #endif
      
      // See if we can move a thread to the running state. Used to not do this if we
      // had already found one, but turns out to be easiest if we are avoiding
      // priority inversion.
      switch (candidate->state)
      {
        case CONDVAR_WAITING:
          // We are waiting to be notified
          if ((candidate->sleepUntil != 0) && (now >= (FOURBYTES) candidate->sleepUntil))
          {
#if DEBUG_MONITOR
      printf ("Waking up waiting thread %d: %d\n", (int) candidate, candidate->threadId);
#endif
            // We will drop through to mon waiting.
          }
          else if (candidate->interruptState == INTERRUPT_CLEARED)
            break;
          else
            candidate->interruptState = INTERRUPT_GRANTED;

          // candidate->state = MON_WAITING;
          // drop through
        case MON_WAITING:
          {
            objSync *sync = candidate->sync;
            byte threadId = get_thread_id(sync);

            if (threadId == NO_OWNER)
            {
              // NOW enter the monitor (guaranteed to succeed)
              enter_monitor(candidate, word2obj(candidate->waitingOn));
              
              // Set the monitor depth to whatever was saved.
              set_monitor_count(sync, candidate->monitorCount);
              
              // Let the thread run.
              candidate->state = RUNNING;
  
              #ifdef SAFE
              candidate->waitingOn = JNULL;
              candidate->sync = JNULL;
              #endif
            }
#if PI_AVOIDANCE
            // Only avoid priority inversion if we don't already have a thread to run.
            else if (currentThread == null)
            {
            	Thread *pOwner;
            	int j;
            	
            	// Track down who owns this monitor and run them instead.            	
            	// Could be 'waiting' in a native method, or we could be deadlocked!
find_next:
            	if (candidate->threadId != threadId)
            	{
                    for (j=MAX_PRIORITY-1; j >= 0; j--)
                    {
                      pOwner = threadQ[j];
                      if (!pOwner)
                        continue;
                        
                      do {
                        // Remember threadQ[j] is the last thread on the queue
                        pOwner = word2ptr(pOwner->nextThread);
                        if (pOwner->threadId == threadId)
                        {
            		      if (pOwner->state == RUNNING)
            		      {
            			    currentThread = pOwner;
            			    goto done_pi;
            			  }
            			  
            		      // if owner is waiting too, iterate down.
            		      if (pOwner->state == MON_WAITING)
            		      {
            			    threadId = get_thread_id(pOwner->sync);
            			    if (threadId != NO_OWNER)
            				  goto find_next;
            		      }
                        }
                      } while (pOwner != threadQ[j]);
                    }                   
                    // If we got here, we're in trouble, just drop through.
            	}
            }
done_pi:
		break;
	    ;
#endif // PI_AVOIDANCE
          
          }
          break;
        case JOIN:
          {
            Thread *pThread = (Thread *)word2obj(candidate->waitingOn);
            if (pThread->state == DEAD ||
                candidate->interruptState != INTERRUPT_CLEARED ||
                (candidate->sleepUntil > 0 && now >= (FOURBYTES)candidate->sleepUntil))
            {
              candidate->state = RUNNING;
              candidate->waitingOn = JNULL;
              candidate->sleepUntil = 0;
              if (candidate->interruptState != INTERRUPT_CLEARED)
                candidate->interruptState = INTERRUPT_GRANTED;
            }
            break;
          }
        case SLEEPING:
          if (candidate->interruptState != INTERRUPT_CLEARED
              || (now >= (FOURBYTES) candidate->sleepUntil))
          {
      #if DEBUG_THREADS
      printf ("Waking up sleeping thread %d: %d\n", (int) candidate, candidate->threadId);
      #endif
            candidate->state = RUNNING;
            if (candidate->interruptState != INTERRUPT_CLEARED)
          	candidate->interruptState = INTERRUPT_GRANTED;
            #ifdef SAFE
  	    candidate->sleepUntil = JNULL;
            #endif // SAFE
          }
          break;
        case STARTED:
          if (currentThread == null)
          {      
            // Put stack ptr at the beginning of the stack so we can push arguments
            // to entry methods. This assumes set_top_word or set_top_ref will
            // be called immediately below.
        #if DEBUG_THREADS
        printf ("Starting thread %d: %d\n", (int) candidate, candidate->threadId);
        #endif
            currentThread = candidate;	// Its just easier this way.
            init_sp_pv();
            candidate->state = RUNNING;
            if (candidate == bootThread)
            {
              execute_program(gProgramNumber);
            }
            else
            {
              set_top_ref_cur (ptr2ref (candidate));
              dispatch_virtual ((Object *) candidate, run_4_5V, null);
            }
            // The following is needed because the current stack frame
            // was just created
            stackFrame = current_stackframe();
            update_stack_frame (stackFrame);
          }
          break;
        case SYSTEM_WAITING:
          // Just keep on waiting
        case RUNNING:
          // Its running already
        case DEAD:
          // Dead threads should be handled earlier
        default:
          // ???
          break;
      }

      // Do we now have a thread we want to run?
      // Note we may later decide not to if all non-daemon threads have died        
      if (currentThread == null && candidate->state == RUNNING)
      {
        currentThread = candidate;
        // Move thread to end of queue
        *pThreadQ = candidate;
      }
      
      if (!candidate->daemon)
      {
      	// May or may not be running but it could do at some point
#if DEBUG_THREADS
printf ("Found a non-daemon thread %d: %d(%d)\n", (int) candidate, (int)candidate->threadId, (int) candidate->state);
#endif
           nonDaemonRunnable = true;
      }
      
      #if DEBUG_THREADS
      printf ("switch_thread: done processing thread %d: %d\n", (int) candidate,
              (int) (candidate->state == RUNNING));
      #endif

      // Always use the first running thread as the thread
      // Keep looping: cull dead threads, check there's at least one non-daemon thread
      previousThread = candidate;
    } while (candidate != anchorThread);
  } // end for
  
#if DEBUG_THREADS
printf ("currentThread=%d, ndr=%d\n", (int) currentThread, (int)nonDaemonRunnable);
#endif

#if DEBUG_THREADS
  printf ("Leaving switch_thread()\n");
#endif
  if (nonDaemonRunnable)
  {
    // There is at least one non-daemon thread left alive
    if (currentThread != null)
    {
      // If we found a running thread and there is at least one
      // non-daemon thread left somewhere in the queue...
      #if DEBUG_THREADS
      printf ("Current thread is %d: %d(%d)\n", (int) currentThread, (int)currentThread->threadId, (int) currentThread->state);
      printf ("getting current stack frame...\n");
      #endif
    
      stackFrame = current_stackframe();
    
      #if DEBUG_THREADS
      printf ("updating registers...\n");
      #endif
    
      update_registers (stackFrame);
      #if DEBUG_THREADS
      printf ("done updating registers\n");
      #endif
    
      if (currentThread->interruptState == INTERRUPT_GRANTED)
        throw_new_exception(JAVA_LANG_INTERRUPTEDEXCEPTION);
    }
      
    return true;
  }
  schedule_request(REQUEST_EXIT);
  currentThread = null;
  
  return false;
}

/*
 * Current thread will wait on the specified object, waiting for a 
 * system_notify. Note the thread does not need to own the object,
 * to wait on it. However it will wait to own the monitor for the
 * object once the wait is complete.
 */
void system_wait(Object *obj)
{
#if DEBUG_MONITOR
  printf("system_wait of %d, thread %d(%d)\n",(int)obj, (int)currentThread, currentThread->threadId);
#endif
  // Indicate the we are waiting for a system notify
  currentThread->state = SYSTEM_WAITING;
  
  // Set the monitor count for when we resume (always 1).
  currentThread->monitorCount = 1;
  
  // Save the object who's monitor we will want back
  currentThread->waitingOn = ptr2ref (obj);
  currentThread->sync = get_sync(obj);
  
  // no time out
  currentThread->sleepUntil = 0;
  
#if DEBUG_MONITOR
  printf("system_wait of %d, thread %d(%d)\n",(int)obj, (int)currentThread, currentThread->threadId);
#endif
  // Gotta yield
  schedule_request( REQUEST_SWITCH_THREAD);
}


/*
 * wake up any objects waiting on the passed system object.
 * Note unlike ordinary waits, we do not allow system waits to be interrupted.
 */
void system_notify(Object *obj, const boolean all)
{
  short i;
  Thread *pThread;
  
#if DEBUG_MONITOR
  printf("system_notify_ of %d, thread %d(%d)\n",(int)obj, (int)currentThread, currentThread->threadId);
#endif
  // Find a thread waiting on us and move to WAIT state.
  for (i=MAX_PRIORITY-1; i >= 0; i--)
  {
    pThread = threadQ[i];
    if (!pThread)
      continue;
      
    do {
      // Remember threadQ[i] is the last thread on the queue
      pThread = word2ptr(pThread->nextThread);
      if (pThread->state == SYSTEM_WAITING && pThread->waitingOn == ptr2ref (obj))
      {
        pThread->state = MON_WAITING;
        if (!all)
          return;
      }
    } while (pThread != threadQ[i]);
  }
}


/*
 * Current thread owns object's monitor (we hope) and wishes to relinquish
 * it temporarily (by calling Object.wait()).
 */
int monitor_wait(Object *obj, const FOURBYTES time)
{
  objSync *sync = get_sync(obj);
#if DEBUG_MONITOR
  printf("monitor_wait of %d, thread %d(%d)\n",(int)obj, (int)currentThread, currentThread->threadId);
#endif
  if (currentThread->threadId != get_thread_id (sync))
    return throw_new_exception(JAVA_LANG_ILLEGALMONITORSTATEEXCEPTION);
  
  // Great. We own the monitor which means we can give it up, but
  // indicate that we are listening for notify's.
  currentThread->state = CONDVAR_WAITING;
  
  // Save monitor depth
  currentThread->monitorCount = get_monitor_count(sync);
  
  // Save the object who's monitor we will want back
  currentThread->waitingOn = ptr2ref (obj);
  currentThread->sync = sync;
  // Might be an alarm set too.
  if (time != 0)
    currentThread->sleepUntil = get_sys_time() + time; 	
  else
    currentThread->sleepUntil = 0;
  
#if DEBUG_MONITOR
  printf("monitor_wait of %d, thread %d(%d) until %ld\n",(int)obj, (int)currentThread, currentThread->threadId, time);
#endif

  // Indicate that the object's monitor is now free.
  set_thread_id (sync, NO_OWNER);
  set_monitor_count(sync, 0);
  
  // Gotta yield
  schedule_request( REQUEST_SWITCH_THREAD);
  return EXEC_CONTINUE;
}

/*
 * Current thread owns object's monitor (we hope) and wishes to wake up
 * any other threads waiting on it. (by calling Object.notify()).
 */
int monitor_notify(Object *obj, const boolean all)
{
  objSync *sync = get_sync(obj);
#if DEBUG_MONITOR
  printf("monitor_notify of %d, thread %d(%d)\n",(int)obj, (int)currentThread, currentThread->threadId);
#endif
  if (currentThread->threadId != get_thread_id (sync))
    return throw_new_exception(JAVA_LANG_ILLEGALMONITORSTATEEXCEPTION);
  
  monitor_notify_unchecked(obj, all);
  return EXEC_CONTINUE;
}

/*
 * wake up any objects waiting on the passed object.
 */
void monitor_notify_unchecked(Object *obj, const boolean all)
{
  short i;
  Thread *pThread;
  
#if DEBUG_MONITOR
  printf("monitor_notify_unchecked of %d, thread %d(%d)\n",(int)obj, (int)currentThread, currentThread->threadId);
#endif
  // Find a thread waiting on us and move to WAIT state.
  for (i=MAX_PRIORITY-1; i >= 0; i--)
  {
    pThread = threadQ[i];
    if (!pThread)
      continue;
      
    do {
      // Remember threadQ[i] is the last thread on the queue
      pThread = word2ptr(pThread->nextThread);
      if (pThread->state == CONDVAR_WAITING && pThread->waitingOn == ptr2ref (obj))
      {
        // might have been interrupted while waiting
        if (pThread->interruptState != INTERRUPT_CLEARED)
          pThread->interruptState = INTERRUPT_GRANTED;

        pThread->state = MON_WAITING;
        if (!all)
          return;
      }
    } while (pThread != threadQ[i]);
  }
}

/**
 * currentThread enters obj's monitor:
 *
 * if monitor is in use, save object in thread and re-schedule
 * else grab monitor and increment its count.
 * 
 * Note that this operation is atomic as far as the program is concerned.
 */
void enter_monitor (Thread *pThread, Object* obj)
{
  objSync *sync;
#if DEBUG_MONITOR
  printf("enter_monitor of %d\n",(int)obj);
#endif

  if (obj == JNULL)
  {
    throw_new_exception (JAVA_LANG_NULLPOINTEREXCEPTION);
    return;
  }
  sync = get_sync(obj);
  if (get_monitor_count (sync) != NO_OWNER && pThread->threadId != get_thread_id (sync))
  {
    // There is an owner, but its not us.
    // Make thread wait until the monitor is relinquished.
    pThread->state = MON_WAITING;
    pThread->waitingOn = ptr2ref (obj);
    pThread->sync = sync;
    pThread->monitorCount = 1;
    // Gotta yield
    schedule_request (REQUEST_SWITCH_THREAD);    
    return;
  }
  set_thread_id (sync, pThread->threadId);
  inc_monitor_count (sync);
}

/**
 * Decrement monitor count
 * Release monitor if count reaches zero
 */
void exit_monitor (Thread *pThread, Object* obj)
{
  byte newMonitorCount;
  objSync *sync;

#if DEBUG_MONITOR
  printf("exit_monitor of %d\n",(int)obj);
#endif

  if (obj == JNULL)
  {
    // Exiting due to a NPE on monitor_enter [FIX THIS]
    return;
  }
  sync = get_sync(obj);
  #ifdef VERIFY
  assert (get_thread_id(sync) == pThread->threadId, THREADS7);
  assert (get_monitor_count(sync) > 0, THREADS8);
  #endif

  newMonitorCount = get_monitor_count(sync)-1;
  if (newMonitorCount == 0)
    set_thread_id (sync, NO_OWNER);
  set_monitor_count (sync, newMonitorCount);
}

/**
 * Current thread waits for thread to die.
 *
 * throws InterruptedException
 */
void join_thread(Thread *thread, const FOURBYTES time)
{
  // Make a note of the thread we are waiting for...
  currentThread->waitingOn = ptr2ref (thread);
  // Might be an alarm set too.
  if (time > 0)
    currentThread->sleepUntil = get_sys_time() + time; 	
  else
    currentThread->sleepUntil = 0;
  // Change our state
  currentThread->state = JOIN;
  // Gotta yield
  schedule_request (REQUEST_SWITCH_THREAD);    
}

void dequeue_thread(Thread *thread)
{
  // First take it out of its current queue
  byte cIndex = thread->priority-1;
  Thread **pThreadQ = &threadQ[cIndex];
  
  // Find the previous thread at the old priority
  Thread *previous = *pThreadQ;
  #if DEBUG_THREADS
  printf("Previous thread %ld\n", ptr2word(previous));
  #endif
  while (word2ptr(previous->nextThread) != thread)
    previous = word2ptr(previous->nextThread);

  #if DEBUG_THREADS
  printf("Previous thread %ld\n", ptr2word(previous));
  #endif
  if (previous == thread)
  {
  #if DEBUG_THREADS
  printf("No more threads of priority %d\n", thread->priority);
  #endif
    *pThreadQ = null;
  }
  else
  {
    previous->nextThread = thread->nextThread;
    *pThreadQ = previous;
  }  
}

void enqueue_thread(Thread *thread)
{
  // Could insert it anywhere. Just insert it at the end.
  byte cIndex = thread->priority-1;
  Thread *previous = threadQ[cIndex];
  threadQ[cIndex] = thread;
  if (previous == null)
    thread->nextThread = ptr2ref(thread);
  else {
    Thread *pNext = word2ptr(previous->nextThread);
    thread->nextThread = ptr2ref(pNext);
    previous->nextThread = ptr2ref(thread);
  }
}

/**
 * Set the priority of the passed thread. Insert into new queue, remove
 * from old queue. Overload to remove from all queues if passed priority
 * is zero.
 *
 * Returns the 'previous' thread.
 */
void set_thread_priority(Thread *thread, const FOURBYTES priority)
{
  #if DEBUG_THREADS
  printf("Thread priority set to %ld was %d\n", priority, thread->priority);
  #endif
  if (thread->priority == priority)
    return;

  if (thread->state == NEW)
  {
  	// Not fully initialized
  	thread->priority = priority;
  	return;
  }

  dequeue_thread(thread);
  thread->priority = priority;
  enqueue_thread(thread);      
}

/**
 * Suspend the specified thread. If thread is null suspend all threads
 * except currentThread.
 */
void suspend_thread(Thread *thread)
{
  int i;
  Thread *pThread;
  if (thread)
    thread->state |= SUSPENDED;
  else
  {
    // Suspend all threads
    for (i=MAX_PRIORITY-1; i >= 0; i--)
    {
      pThread = threadQ[i];
      if (!pThread)
        continue;
      
      do {
        // Remember threadQ[i] is the last thread on the queue
        pThread = word2ptr(pThread->nextThread);
        if (pThread != currentThread) pThread->state |= SUSPENDED;
      } while (pThread != threadQ[i]);
    }
  }
  schedule_request( REQUEST_SWITCH_THREAD);
}

void resume_thread(Thread *thread)
{
  int i;
  Thread *pThread;
  if (thread)
  {
    thread->state &= ~SUSPENDED;
    return;
  }
  // Suspend all threads
  for (i=MAX_PRIORITY-1; i >= 0; i--)
  {
    pThread = threadQ[i];
    if (!pThread)
      continue;
      
    do {
      // Remember threadQ[i] is the last thread on the queue
      pThread = word2ptr(pThread->nextThread);
      pThread->state &= ~SUSPENDED;
    } while (pThread != threadQ[i]);
  }
}

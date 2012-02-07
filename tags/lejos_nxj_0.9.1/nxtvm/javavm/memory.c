/**
 * Memory operations.
 * This file encapsulates many of the low level memory operations required
 * by the VM. In particular it include the allocator and garbage collector
 * allong with functions to allow access to non aligned storage.
 *
 * The allocator uses two heap areas one allocates fixed location objects
 * which are refernced directly by other parts of the VM.
 * The other heap contains objects that can be moved, these objects are
 * accessed via a pointer stored in a fixed location object.
 * Both heaps are collected using an incremental
 * concurrent collector. Failed allocation requests will result in the
 * current thread being suspended while the gc runs, after which the
 * request can be retried. 
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
#include "stdlib.h"
#include "stack.h"
#include "display.h"
#include "rconsole.h"

#include <string.h>

#ifdef VERIFY
static boolean memoryInitialized = false;
#endif

// Heap memory needs to be aligned to 4 bytes on ARM
#define WORD_SIZE 4
#define WORD_SHIFT 2
#define TO_WORDS(b) (((b) + (WORD_SIZE-1)) >> WORD_SHIFT)
#define MEM_ROUND(w) (((w) + (WORD_SIZE-1)) & ~(WORD_SIZE-1))
#define MEM_TRUNC(w) ((w) & ~(WORD_SIZE -1)) 

// Sizes and gorwth when using a split heap.
#define INITIAL_HEAP 2048
#define MIN_HEAP 4096
#define MIN_HEAP_ADJUST 256



// Basic types and sizes
static const byte typeSize[] = { 
  4, // 0 == T_REFERENCE
  0, // 1
  0, // 2 == T_CLASS
  0, // 3
  1, // 4 == T_BOOLEAN
  2, // 5 == T_CHAR
  4, // 6 == T_FLOAT
  8, // 7 == T_DOUBLE
  1, // 8 == T_BYTE
  2, // 9 == T_SHORT
  4, // 10 == T_INT
  8, // 11 == T_LONG
  0, // 12 == T_VOID
  0, // 13
  4, // 14 Used for multidimensional arrays
};

// Header for free objects
typedef struct
{
  objFlags flags;
  TWOBYTES next;
} __attribute__((packed)) FreeObject;

// Header for array heap objects
typedef struct
{
  TWOBYTES object:16;
  TWOBYTES thread:15;
  TWOBYTES available:1;
}  __attribute__((packed)) AHeapObj;

// Object sizes in words
#define AH_OBJ_SIZE TO_WORDS(sizeof(AHeapObj))
#define NORM_OBJ_SIZE TO_WORDS(HEADER_SIZE)
#define BA_OBJ_SIZE TO_WORDS(sizeof(BigArray))


// GC states are defined in memory.h to make them available to macros
int gcPhase = GC_IDLE;
static FOURBYTES *heapEnd;      /* pointer to end of heap */
static FOURBYTES *heapStart;    /* pointer to start of heap */
static FOURBYTES memorySize;    /* total number of words in heap */

// Object heap data
static FOURBYTES oheapMemoryFree; /* number of free words in heap */
static Object *oheapStart;      /* pointer to start of object heap */
static Object *oheapEnd;        /* pointer to end of object heap */
static byte *refMap;            /* pointer to map of allocated objects */
// Heap object colors
#define GC_WHITE 0
#define GC_LIGHTGREY 1
#define GC_DARKGREY 2
#define GC_BLACK 3

// Heap allocation and sweep pointers
static TWOBYTES *allocPrev;    /* current position in the free list */
static Object *sweepBase;      /* current position of sweeping */
static FreeObject *sweepFree;  /* current position of sweeping free ptr */
static Object *overflowScan;   /* current position of overflow scan */
// Mark queue
#define MAX_CALL_DEPTH 4
#define MARK_Q_SIZE 64
static TWOBYTES markQ[MARK_Q_SIZE];
static int markQHd = 0;
static int markQTl = 0;
#define markQNext(cur) (((cur) + 1) % MARK_Q_SIZE)

// Value used to mark end of the free list. Will always point beyond the
// end of the heap, when used as an offset.
#define FL_END 0xffff
// Start of the freelist
static TWOBYTES freeList = FL_END;
static int oheapMemoryRequired;        /* Target amount of memory needed */
static int oheapLargestFree;
// Array heap data
static FOURBYTES *aheapStart;   /* pointer to start of array heap */
static FOURBYTES *aheapEnd;     /* pointer to end of object heap */
static FOURBYTES *aheapAlloc;   /* array heap free block */
static FOURBYTES aheapMemoryFree; /* number of free words in array heap */
static FOURBYTES aheapMemoryRequired;

// Macro used to indicate when to suspend this mark/sweep step
#define GC_TIMEOUT() (gMakeRequest)
//#define GC_TIMEOUT() (0)

// Access control and retry tracking
Object gcLock = {{
    .length = LEN_OBJECT,
    .class = JAVA_LANG_OBJECT,
    .mark = GC_BLACK,
}, {0, 0}};
int retryState;
Thread *exclusiveThread = null;
#define GC_WAIT -1
#define GC_NORMAL 0
#define GC_RETRY 1
#define GC_EXCLUSIVE 2
#define GC_ERROR 3
#define GC_ABORT 4

// Location of major memory based structures. Used to allow Java access to
// VM memory.
byte *memory_base[MEM_MEM+1];

/**
 * Forward declarations.
 */
static void mark_object(Object *obj, int callLimit);
static Object *try_allocate(unsigned int size);
static Object *oheap_allocate (JINT size, byte class, byte length);
static AHeapObj *aheapAllocate(JINT size, Object *obj);
static void aheap_free(AHeapObj *ah);
static void compact_aheap();

#define array_data_size(CLS_, LEN_) TO_WORDS(get_class_record(CLS_)->classSize*(LEN_))
#define array_size(OBJ_) (is_std_array(OBJ_) ? NORM_OBJ_SIZE + array_data_size(get_class_index(OBJ_), ((Object *)(OBJ_))->flags.length) : (is_big_array(OBJ_) ? BA_OBJ_SIZE + array_data_size(get_class_index(OBJ_), ((BigArray *)(OBJ_))->length) : BA_OBJ_SIZE ))
#define obj_size(OBJ_) TO_WORDS(get_class_record(get_class_index(OBJ_))->classSize)
#define get_size(OBJ_) (is_array(OBJ_) ? array_size(OBJ_) : obj_size(OBJ_))
#define array_element_size(OBJ_) (get_class_record(get_class_index(OBJ_))->classSize)
#define get_free_size(OBJ_) (is_std_array((Object *)(OBJ_)) ? NORM_OBJ_SIZE : BA_OBJ_SIZE + ((BigArray *)(OBJ_))->length)

/**
 * The following functions manage the allocation request and retry mechanism.
 * Some requests require multiple objects to be allocated, so we implement
 * the notion of a transaction that is bounded by calls to alloc_start and
 * alloc_complete. If a request fails we schedule a garbage collection and
 * arrange to retry the request. If a sunsequent retry fails, we try again
 * but this time stop all other threads from making memory requests. If this
 * retry fails we throw an out of memory exception.
 */ 

/**
 * Start a memory transaction.
 */
static boolean alloc_start()
{
  // Does this thread own the lock? If it does then it is a retry
  retryState = GC_NORMAL;
  if (gcLock.sync.threadId == currentThread->threadId)
  {
    // we must release the monitor
    exit_monitor(currentThread, &gcLock);
    // If the gc is still running we may need to try again
    if (gcPhase == GC_IDLE)
      retryState = GC_RETRY;
  }
  // Are we in exclusive mode?
  if (exclusiveThread)
  {
    if (exclusiveThread != currentThread)
    {
      // Not the exclusive thread, wait and retry
      curPc = getPc();
      sleep_thread(1);
      return false;
    }
    if (retryState == GC_RETRY) retryState = GC_EXCLUSIVE;
  }
  return true;
}

/**
 * Complete a memory transaction.
 * The ret parameter is NULL if the transaction failed.
 */
static Object *alloc_complete(Object *ret)
{
  // If the request has worked, clean up and we are done
  if (ret != JNULL)
  {
    exclusiveThread = null;
    return ret;
  }

  // Failed to allocate the memory, move on to the next state.
  switch(retryState + 1)
  {
  case GC_ERROR:
    throw_new_exception(JAVA_LANG_OUTOFMEMORYERROR);
  case GC_ABORT:
    exclusiveThread = null;
    break;
  case GC_EXCLUSIVE:
    exclusiveThread = currentThread;
  case GC_RETRY:
    // retry the current instruction
    curPc = getPc();
    system_wait(&gcLock);
    if (gcPhase == GC_IDLE) gcPhase = GC_MARKROOTS;
    break;
  }
  return JNULL;
}

/**
 * Notify any threads that are waiting to retry allocations that they can so so
 */
static void alloc_notify()
{
  system_notify(&gcLock, true);
}

/**
 * Abort a memory transaction and throw an excpetion
 */
static void alloc_abort(int exception)
{
  throw_new_exception(exception);
  retryState = GC_ERROR;
}

/**
 * Checks if the class needs to be initialized.
 * If so, the static initializer is dispatched.
 * Otherwise, an instance of the class is allocated.
 *
 * @@param btAddr Back-track PC address, in case
 *               a static initializer needs to be invoked.
 * @@return Object reference or <code>null</code> iff
 *         NullPointerException had to be thrown or
 *         static initializer had to be invoked.
 */
Object *new_object_checked (const byte classIndex, byte *btAddr)
{
  #if 0
  trace (-1, classIndex, 0);
  #endif

  if (!is_initialized_idx (classIndex))
    if (dispatch_static_initializer (get_class_record(classIndex), btAddr) != EXEC_CONTINUE)
    {
#if DEBUG_MEMORY
      printf("New object checked returning null\n");
#endif
      return JNULL;
    }

  return new_object_for_class (classIndex);
}

/**
 * Allocates and initializes the state of
 * an object. It does not dispatch static
 * initializers.
 */
Object *new_object_for_class (const byte classIndex)
{
  Object *ref;
#if DEBUG_MEMORY
  printf("New object for class %d\n", classIndex);
#endif
  if (!alloc_start()) return JNULL;
  ref = oheap_allocate(TO_WORDS(get_class_record(classIndex)->classSize), classIndex, LEN_OBJECT);
  return alloc_complete(ref);
}

/**
 * Allocates an array. 
 * To allow compact representation of small arrays but also allow the
 * use of larger objects we support two classes of array. The standard array
 * has a normal object header. Arrays larger then LEN_BIGARRAY have the
 * std length field set to LEN_BIARRAY and then have an extra length field which
 * is placed immediately at the end of the normal header. This length
 * field contains the real array size.
 */
static Object *new_array (const byte cls, JINT length)
{
  Object *ref;
  AHeapObj *ah;
  JINT allocSize;

  if (length < 0)
  {
    alloc_abort(JAVA_LANG_NEGATIVEARRAYSIZEEXCEPTION);
    return JNULL;
  }
  allocSize = array_data_size (cls, length);
#if DEBUG_MEMORY
  printf("New array of cls %d, length %ld\n", cls, length);
#endif
  // If this is a large array use a bigger header
  if (length >= LEN_BIGARRAY)
  {
    if (aheapStart != aheapEnd)
    {
      ref = oheap_allocate(BA_OBJ_SIZE, cls, LEN_BIGARRAY);
      if (ref == JNULL) return JNULL;
      // We have the header, try and allocate the data from the moveable heap
      ah = aheapAllocate(allocSize + AH_OBJ_SIZE, ref);
      if (ah != JNULL)
      {
        ref->flags.length = LEN_AHARRAY;
        ((BigArray *)ref)->length = length;
        ((BigArray *)ref)->offset = (short)((FOURBYTES *)ah - (FOURBYTES *)ref + AH_OBJ_SIZE);
        return ref;
      }
      // Have we already tried to make space?
      if (retryState == GC_EXCLUSIVE)
        // yes so try to use the object heap
        ref = oheap_allocate(allocSize + BA_OBJ_SIZE, cls, LEN_BIGARRAY);
      else
      {
        // Either we retry or retry in exclusive mode
        aheapMemoryRequired += allocSize + AH_OBJ_SIZE;
        return JNULL;
      }
    }
    else
      ref = oheap_allocate(allocSize + BA_OBJ_SIZE, cls, LEN_BIGARRAY);
    if (ref == JNULL) return JNULL;
    ((BigArray *)ref)->length = length;
    ((BigArray *)ref)->offset = (short)BA_OBJ_SIZE;
    return ref;
  }
  else
  {
    ref = oheap_allocate (NORM_OBJ_SIZE + allocSize, cls, length);
    return ref;
  }
}

/**
 * Create a single dimension array. The class of the array is given by cls.
 * NOTE: cls is the array class NOT the class of the elements.
 */
Object *new_single_array (const byte cls, JINT length)
{
  if (!alloc_start()) return JNULL;
  return alloc_complete(new_array(cls, length));
}

/**
 * @@param cls The class index of the array
 * @@param reqDimensions Number of requested dimensions for allocation.
 * @@param numElemPtr Pointer to first dimension. Next dimension at numElemPtr+1.
 */
static Object *alloc_multi_array (const byte cls,
                         byte reqDimensions, STACKWORD *numElemPtr)
{
  Object *ref;
  Object *ref2;
  ClassRecord *classRec = get_class_record(cls);
  JINT ne;
  int elemCls;

  if (reqDimensions == 0)
    return JNULL;

  ref = new_array (cls, (JINT)*numElemPtr);
  if (ref == JNULL) return JNULL;
  // If this is a partial array we are done...
  if (reqDimensions > 1)
  {
    // Make sure we protect each level from the gc. Once we have returned
    // the ref it will be protected by the level above.
    ne = (JINT)*numElemPtr;
    elemCls = get_element_class(classRec);
    while (ne--)
    {
      ref2 = alloc_multi_array (elemCls, reqDimensions - 1, numElemPtr + 1);
      if (ref2 == JNULL) return JNULL;
      ref_array(ref)[ne] = ptr2word (ref2);
    }
  }
  return ref;
}


Object *new_multi_array (const byte cls,
                         byte reqDimensions, STACKWORD *numElemPtr)
{
  ClassRecord *classRec = get_class_record(cls);
  byte totalDimensions = get_dim(classRec);

  #ifdef VERIFY
  assert (totalDimensions >= 1, MEMORY6);
  assert (reqDimensions <= totalDimensions, MEMORY8);
  #endif
  if (reqDimensions == 0)
    return JNULL;

  if (totalDimensions >= MAX_VM_REFS)
  {
    throw_new_exception (JAVA_LANG_OUTOFMEMORYERROR);
    return JNULL;
  }

  if (totalDimensions == 1)
    return new_single_array (cls, (JINT)*numElemPtr);


  if (!alloc_start()) return JNULL;
  return alloc_complete(alloc_multi_array(cls, reqDimensions, numElemPtr));
}

/**
 * Native array copy method,
 * Copy the (partial) contents of one array to another
 * Placed here tp allow access to element size information.
 */
int arraycopy(Object *src, int srcOff, Object *dst, int dstOff, int len)
{
  int elemSize;
  byte srcCls, dstCls;
  boolean primitive;
  // validate things
  if (src == null || dst == null)
    return throw_new_exception(JAVA_LANG_NULLPOINTEREXCEPTION);
  if (!is_array(src) || !is_array(dst))
    return throw_new_exception(JAVA_LANG_ARRAYSTOREEXCEPTION);
  // Check type compatibility...
  srcCls = get_element_class(get_class_record(get_class_index(src)));
  dstCls = get_element_class(get_class_record(get_class_index(dst)));
  primitive = is_primitive(srcCls) || is_primitive(dstCls);
  if (primitive && srcCls != dstCls)
    return throw_new_exception(JAVA_LANG_ARRAYSTOREEXCEPTION);
  if (len < 0 ||
      srcOff < 0 || (srcOff + len > get_array_length(src)) ||
      dstOff < 0 || (dstOff + len > get_array_length(dst)))
    return throw_new_exception(JAVA_LANG_ARRAYINDEXOUTOFBOUNDSEXCEPTION);
 
  // write barrier
  if (!primitive) update_array(dst);
  if (primitive || !type_checks_enabled() || is_assignable(srcCls, dstCls))
  {
    // copy things the fast way
    elemSize = array_element_size(src);
    memmove(get_array_element_ptr(dst, elemSize, dstOff), get_array_element_ptr(src, elemSize, srcOff), len*elemSize);
  }
  else
  {
    // We have to check every element...
    int i;
    REFERENCE *srcPtr = ref_array(src) + srcOff;
    REFERENCE *dstPtr = ref_array(dst) + dstOff;
    for(i = 0; i < len; i++)
    {
      if (*srcPtr == JNULL || is_assignable(get_class_index((Object *)*srcPtr), dstCls))
        *dstPtr++ = *srcPtr++;
      else
        return throw_new_exception(JAVA_LANG_ARRAYSTOREEXCEPTION);
    }
  }
  return EXEC_CONTINUE;
}


/**
 * @return A String instance, or JNULL if an exception was thrown
 *         or the static initializer of String had to be executed.
 */
Object *new_string (ConstantRecord *constantRecord, 
                                     byte *btAddr)
{
  Object *ref;
  Object *arr;
  JCHAR *dst;
  byte *src;
  byte *src_end;
/*
  if (!is_initialized_idx (JAVA_LANG_STRING) &&
      dispatch_static_initializer (get_class_record(JAVA_LANG_STRING), btAddr) != EXEC_CONTINUE)
    return JNULL;
*/
  if (!alloc_start()) return JNULL;
  ref = oheap_allocate(TO_WORDS(get_class_record(JAVA_LANG_STRING)->classSize), JAVA_LANG_STRING, LEN_OBJECT);
  if (ref != JNULL)
  {
    // what sort of array do we have?
    if (constantRecord->constantType == AC)
      store_word_ns( (byte *) &(((String *) ref)->characters), T_INT, obj2word(get_constant_ptr(constantRecord)));
    else
    {
      arr = new_array (AC, constantRecord->stringLength);
      if (arr != JNULL)
      {
        store_word_ns( (byte *) &(((String *) ref)->characters), T_INT, obj2word(arr));
        src = get_constant_ptr(constantRecord);
        dst = jchar_array(arr);
        src_end = src + constantRecord->stringLength;

        while( src < src_end)
          *dst++ = (JCHAR) (*src++);
      }
      else
        ref = JNULL;
    }
  }
  return alloc_complete(ref);
}

/**
 * Initialize the run time stacks for the given thread.
 */
int init_stacks(Thread *thread)
{
  Object *callStack;
  Object *valueStack;
  if (!alloc_start()) return -1;
  // Allocate the call stack
  callStack = new_array(AB, INITIAL_STACK_FRAMES*sizeof(StackFrame));
  if (callStack)
  {
    // And the value stack
    valueStack = new_array(AI, INITIAL_STACK_SIZE);
    if (valueStack)
    {
      thread->stackFrameArray = ptr2ref(callStack);
      thread->stackArray = ptr2ref(valueStack);
      if (is_ah_array(valueStack))
      {
        AHeapObj *ah = (AHeapObj *)(array_start(valueStack) - sizeof(AHeapObj));
        ah->thread = (FOURBYTES *)thread - heapStart;
      }
      alloc_complete(callStack);
      return 0;
    }
  }
  // allocation failed
  alloc_complete(null);
  return -1;
}

/**
 * If the stack is stored in a moveable object we need to keep a link back to
 * the thread so that we can update the frame values if the value stack moves.
 */
static
void set_thread(Object *stack, Thread *thread)
{
  if (is_ah_array(stack))
  {
    AHeapObj *ah = (AHeapObj *)(array_start(stack) - sizeof(AHeapObj));
    ah->thread = (!thread ? 0 : (FOURBYTES *)thread - heapStart);
  }
}

/**
 * Release the stacks asociated with a thread.
 */
void free_stacks(Thread *thread)
{
  // We need to break the association between the thread object and the
  // value stack array storage to remove any dangling pointer.
  Object *valueStack = ref2ptr(thread->stackArray);
  set_thread(valueStack, null);
}


/**
 * Called when a stack frame has been moved in memory. This updates pointers
 * in the stacks to the new locations.
 */
static
void update_stack_frames(Thread *thread, int offset)
{
  // The value stack has moved. We need to update the pointers in the
  // stack frames.
  StackFrame *sf = (StackFrame *)array_start(thread->stackFrameArray);
  int cnt = thread->stackFrameIndex;
  while(cnt-- >= 0)
  {
    sf->localsBase += offset;
    sf->stackTop += offset;
    sf++;
  }
  if (thread == currentThread)
  {
    curStackTop += offset;
    curLocalsBase += offset;
  }
}

/**
 * Grow the call stack.
 */
int expand_call_stack(Thread *thread)
{
  Object *cur = ref2ptr(thread->stackFrameArray);
  int curSize = get_array_length(cur);
  int newSize = curSize + INITIAL_STACK_FRAMES*sizeof(StackFrame);
  if (newSize > 256*sizeof(StackFrame)) newSize = 256*sizeof(StackFrame);
  Object *newStack = new_single_array(AB, newSize);
  if (newStack == JNULL) return -1;
  memcpy(array_start(newStack), array_start(cur), curSize);
  thread->stackFrameArray = ptr2ref(newStack);
  return 0;
}

/**
 * Grow the value stack
 */
int expand_value_stack(Thread *thread, int minSize)
{
  Object *cur = ref2ptr(thread->stackArray);
  int curSize = get_array_length(cur);
  int newSize = curSize + (minSize > INITIAL_STACK_SIZE ? minSize : INITIAL_STACK_SIZE);
  Object *newStack = new_single_array(AI, newSize);
  if (newStack == JNULL) return -1;
  memcpy(array_start(newStack), array_start(cur), curSize*sizeof(STACKWORD));
  update_stack_frames(thread, (STACKWORD *)array_start(newStack) - (STACKWORD *)array_start(cur));
  // If the value stack is using a moveable array, we need to allow for updates
  set_thread(newStack, thread);
  // We need to remove the reference back to this thread from the old stack.
  set_thread(cur, null);
  thread->stackArray = ptr2ref(newStack);
  return 0;
}

/**
 * Create a copy of an existing Object (including arrays). Copy is shallow...
 */
Object *clone(Object *old)
{
  Object *new;
  if (is_array(old))
  {
    new = new_single_array(get_class_index(old), get_array_length(old));
    if (!new) return NULL;
    // Copy old array to new
    memcpy(array_start(new), array_start(old), get_array_length(old) * array_element_size(old));
  }
  else
  {
    byte classIndex = get_class_index(old);
    ClassRecord* classRecord = get_class_record(classIndex);
    new = new_object_for_class(classIndex);
    if (!new) return NULL;
    memcpy((byte *) new + sizeof(Object), (byte *)old + sizeof(Object), classRecord->classSize - sizeof(Object)); 
  }
  return new;
}

/**
 * System level alocator. Allocate memory of sz bytes.
 */
byte *system_allocate(int sz)
{
  Object *ref;
  int allocSize;
  // We actually use an integer array for the storage, so round up and convert
  // to array length
  if (!alloc_start()) return JNULL;
  sz = (sz + sizeof(JINT) - 1)/sizeof(JINT);
  allocSize = array_data_size(AI, sz);
  ref = oheap_allocate(BA_OBJ_SIZE + allocSize, AI, LEN_BIGARRAY);
  if (alloc_complete(ref) == JNULL)
    return JNULL;
  ((BigArray *)ref)->length = sz;
  ((BigArray *)ref)->offset = (short)BA_OBJ_SIZE;
  // Lock the memory to prevent it being collected
  protect_obj(ref);
  // and return a pointer to the actual memory
  return (byte *) array_start(ref);
}

/**
 * System level memory free
 */
void system_free(byte *mem)
{
  Object *ref;
  if (!mem) return;
  ref = (Object *) (mem - sizeof(BigArray));
  unprotect_obj(ref);
}

/**
 * Note the issue is not just endian. We also need to deal with the fact that
 * java items may not be aligned correctly.
 * Now slightly optmized;
 */
STACKWORD get_word_4_swp(byte *ptr)
{
    return (((STACKWORD)ptr[0]) << 24) | (((STACKWORD)ptr[1]) << 16) |
           (((STACKWORD)ptr[2]) << 8) | ((STACKWORD)ptr[3]);
}

/**
 * Following allow access to non aligned data fields
 */
STACKWORD get_word_ns(byte *ptr, int typ)
{
  switch(typ)
  {
  case T_INT:
  case T_FLOAT:
  case T_REFERENCE:
  case T_DOUBLE:
  case T_LONG:
    return (((STACKWORD)ptr[3]) << 24) | (((STACKWORD)ptr[2]) << 16) |
           (((STACKWORD)ptr[1]) << 8) | ((STACKWORD)ptr[0]);
  case T_SHORT:
    return (STACKWORD)(JINT)(JSHORT)(((TWOBYTES)ptr[1]) << 8) | (ptr[0]);
  case T_CHAR:
    return (STACKWORD)(JCHAR)(((TWOBYTES)ptr[1]) << 8) | (ptr[0]);
  case T_BOOLEAN:
  case T_BYTE:
    return (STACKWORD)(JINT)(JBYTE)ptr[0];
  }
  return 0;
}

STACKWORD get_word_4_ns(byte *ptr)
{
    return (((STACKWORD)ptr[3]) << 24) | (((STACKWORD)ptr[2]) << 16) |
           (((STACKWORD)ptr[1]) << 8) | ((STACKWORD)ptr[0]);
}

void store_word_ns(byte *ptr, int typ, STACKWORD aWord)
{
  switch(typ)
  {
  case T_INT:
  case T_FLOAT:
  case T_REFERENCE:
  case T_DOUBLE:
  case T_LONG:
    ptr[0] = (byte)(aWord);
    ptr[1] = (byte)(aWord >> 8);
    ptr[2] = (byte)(aWord >> 16);
    ptr[3] = (byte)(aWord >> 24);
    return;
  case T_SHORT:
  case T_CHAR:
    ptr[0] = (byte)(aWord);
    ptr[1] = (byte)(aWord >> 8);
    return;
  case T_BOOLEAN:
  case T_BYTE:
    ptr[0] = (byte)aWord;
    return;
  }
}


/**
 * Initialize the memory system.
 */
void memory_init ()
{
  #ifdef VERIFY
  memoryInitialized = true;
  #endif

  memorySize = 0;
  oheapMemoryFree = 0;

}


int getHeapSize() {
  return ((int)memorySize) << WORD_SHIFT;
}

int getHeapFree() {
  return ((int)oheapMemoryFree + (int)aheapMemoryFree) << WORD_SHIFT;
}

int getRegionAddress()
{
  return (int)heapStart;
}

/**
 * return the contants of a memory location
 */
FOURBYTES mem_peek(int base, int offset, int typ)
{
  return get_word_ns((byte *)(memory_base[base] + offset), typ);
}

/**
 * copy memory into the specified object, taking care to obey the write
 * barrier.
 */
void mem_copy(Object *obj, int objoffset, int base, int offset, int len)
{
  if (obj == null)
    throw_new_exception(JAVA_LANG_NULLPOINTEREXCEPTION);
  if (is_array(obj))
  {
    if (!has_norefs(get_class_record(get_class_index(obj)))) update_array(obj);
    memcpy(array_start(obj) + objoffset, memory_base[base]+offset, len);
  }
  else
  {
    update_object(obj);
    memcpy(fields_start(obj) + objoffset, memory_base[base]+offset, len);
  }
}

/**
 * Return a reference to an object.
 */
REFERENCE mem_get_reference(int base, int offset)
{
  return (REFERENCE)(memory_base[base] + offset);
}


/**
 * The concurrent garbage collector implementation starts here.
 * It is an incremental concurrent mark/sweep collector using a snap shot
 * at the beginning and a write barrier. Allocations are made from a single
 * linked list of free areas. A separate bitmap is maintained that identifies
 * the start point of currently allocated objects. This allows the unambiguous
 * identification of pointers into the heap. A collection is triggered when
 * insufficient space is available to make an allocation. When this occurs the
 * thread making the request is suspended and its state is changed to allow a
 * retry of the operation. The collector then runs (effectively on another
 * thread), running for a fixed period of time on each call (currently the
 * collector will run for 1mS consumng one half of a system time slice). The
 * collector uses an extended version of the classic node coloring scheme. The
 * collector runs through four major stages:
 *
 * MARKROOTS
 * At the start of this stage all items in the heap are colored white. This 
 * state is guaranteed by the previous SWEEP stage. The collector now marks
 * each root node by placing it on the mark list (see below for overflow
 * handling). Only objects that contain additional refernces are placed on the
 * list other nodes are marked BLACK immeadiately. Objects on the mark list
 * are colored DARYGREY. The roots consist of static data, threads and thread
 * stacks. We do not have stack maps to help identify references on the stack
 * so they are mark conservatively using the object bitmap described above.
 *
 * MARK
 * The mark phase traces each object marking all of the child references
 * in each object in the mark list. It uses a hybrid marking scheme marking
 * using a recursive call to a pre-set depth and then placing objects beyond
 * this depth on the mark list. This allows fast handling of shallow data
 * structures like arrays. If the mark list overfows the object is colored
 * LIGHTGREY. A separate sweep of the heap will find and mark all LIGHTGREY
 * objects. The mark phase continues until the mark queue is empty and there
 * are no LIGHTGREY objects in the heap. The system runs in parallel to the
 * collector. To allow this we use a snapshot at the beginning by marking all
 * of the roots in one go. After that we ensure that the snapshot is maintained
 * by using a write barrier that will track any modifications made to objects
 * that have not yet been marked. The write barrier is only active during the
 * mark phase.
 *
 * SWEEP
 * At this point any objects that are still colored WHITE are no longer in
 * use and so made be freed. The sweep phase performs this operation. It
 * will merge adjacent free blocks and clears the marked objects back to
 * WHITE. It re-builds the free list as part of the sweep. The sweep operation
 * runs in parallel with allocations, to ensure correct operation any objects
 * that are allocated, during a MARK/SWEEP phase that are ahead of the sweep
 * pointer are colored BLACK. The sweep phase will wake any threads that are
 * waiting for memory as it freeds sufficient space to meet the demand. All
 * such threads will be woken when the sweep completes.
 *
 */

/**
 * "Mark" flag manipulation functions.
 */
static inline void set_gc_marked(Object* obj, byte col)
{
  obj->flags.mark = col;
}

static inline byte get_gc_mark(Object *obj )
{
  return obj->flags.mark;
}

static inline void clr_gc_marked(Object* obj)
{
  obj->flags.mark = GC_WHITE;
}

static inline boolean is_gc_marked(Object* obj)
{
  return obj->flags.mark != GC_WHITE;
}

/**
 * Reference bitmap manipulation functions.
 * The bitmap is allocated at the end of the region.
 */
static void set_reference(FreeObject* ptr)
{
  int refIndex = ((byte*) ptr - (byte*)heapStart) / (WORD_SIZE);

  (refMap)[ refIndex >> 3] |= 1 << (refIndex & 7);

}

static void clr_reference(Object* ptr)
{
  int refIndex = ((byte*) ptr - (byte*)heapStart) / (WORD_SIZE);

  (refMap)[ refIndex >> 3] &= ~ (1 << (refIndex & 7));

}

#if 0
static boolean is_reference_old(TWOBYTES* ptr)
{
  /* The reference must belong to a memory region. */
  TWOBYTES* regBottom = heapStart;
  TWOBYTES* regTop = heapEnd;

  if(ptr >= regBottom && ptr < regTop)
  {
    /* It must be properly aligned */
    if(((int)ptr & ((MEMORY_ALIGNMENT * 2) - 1)) == 0)
    {
      /* Now we can safely check the corresponding bit in the reference bitmap. */
      int refIndex = ((byte*) ptr - (byte*)heapStart) / (MEMORY_ALIGNMENT * 2);

      return (((byte*) heapEnd)[ refIndex >> 3] & (1 << (refIndex & 7))) != 0;
    }
    return false;
  }

  return false;
}

// Test code to ensure new optimize ref code works
static boolean is_reference_test(TWOBYTES* ptr)
{
  /* The reference must belong to a memory region. */
  if(((int)ptr & ((MEMORY_ALIGNMENT * 2) - 1)) == 0)
  {
    unsigned int refIndex = ptr - heapStart;

    if (refIndex  < (unsigned int)memorySize)
    {
      refIndex /= 2;
      //return (((byte*) heapEnd)[ refIndex >> 3] & (1 << (refIndex & 7))) != 0;
      if ((((byte*) heapEnd)[ refIndex >> 3] & (1 << (refIndex & 7))) != 0)
      {
        if (!is_reference_old(ptr))
        {
           printf("Bad accept %x\n", ptr);
           return false;
        }
        return true;
      }
    }
  }
  if (is_reference_old(ptr))
  {
    printf("Bad reject val %x\n", ptr);
    return true;
  }
  return false;
}
#endif

static inline boolean is_reference(Object* ptr)
{
  /* The reference must belong to a memory region. */
  if(((int)ptr & ((WORD_SIZE) - 1)) == 0)
  {
    unsigned int refIndex = ptr - (Object *)heapStart;

    if (refIndex  < (unsigned int)memorySize)
    {
      return ((refMap)[ refIndex >> 3] & (1 << (refIndex & 7))) != 0;
    }
  }
  return false;
}

static inline void set_free_size(FreeObject *ptr, TWOBYTES sz)
{
  if (sz == NORM_OBJ_SIZE)
    // zero length void array
    *((TWOBYTES *)ptr) = T_FREE;
  else
  {
    *((TWOBYTES *)ptr) = T_FREE | (LEN_BIGARRAY << ARRAY_LENGTH_SHIFT);
    ((BigArray *)ptr)->length = (sz - BA_OBJ_SIZE);
  }
}

/**
 * @@param region Beginning of region.
 * @@param size Size of region in bytes.
 */
void memory_add_region (byte *start, byte *end)
{
  TWOBYTES contents_size;
  TWOBYTES bitmap_size;
  AHeapObj *ah;

  /* align upwards */
  heapStart = (FOURBYTES *) MEM_ROUND((unsigned int)start);

  /* align downwards */
  heapEnd = (FOURBYTES *) MEM_TRUNC((unsigned int)end); 
  /* To be able to quickly identify a reference like stack slot
     we use a dedicated referance bitmap. With alignment of 4 bytes
     the map is 32 times smaller then the heap. Let's allocate
     the map by lowering the heap end pointer by the map size.
     The map must be zeroed. */
  contents_size = heapEnd - heapStart;
  /* Calculate the required bitmap size (in words). Note that we devide here
     by 33 rather then 32 to take into account the reduction in size of the
     heap due to the bitmap size!  */
  bitmap_size = contents_size / (((WORD_SIZE) * 8) + 1) + 1;
  heapEnd -= bitmap_size;
  memset((byte *)heapEnd, 0, bitmap_size*WORD_SIZE);
  refMap = (byte *)heapEnd;
  memorySize = heapEnd - heapStart;

  // Split the heap into object and array heaps
  oheapEnd = (Object *)heapEnd;
  if (memory_compact_enabled())
    oheapStart = (oheapEnd - INITIAL_HEAP);
  else
    oheapStart = (Object *)heapStart;
  aheapEnd = (FOURBYTES *)oheapStart;
  aheapStart = heapStart;
  // Now create the array heap */
  aheapMemoryFree = aheapEnd - aheapStart;
  aheapAlloc = aheapStart;
  ah = (AHeapObj *)aheapStart;
  ah->available = 1;
  ah->object = aheapMemoryFree;
  /* create free block in the heap */
  oheapMemoryFree = oheapEnd - oheapStart;
  set_free_size((FreeObject *)oheapStart, oheapMemoryFree);
  // Initialise the free list
  ((FreeObject *)oheapStart)->next = FL_END;
  freeList = oheapStart - (Object *)heapStart;
  allocPrev = &freeList;
  // Incremental mark/sweep state
  sweepBase = oheapEnd;
  sweepFree = NULL;
  oheapMemoryRequired = 0;
  gcPhase = GC_IDLE;

  // Reset the allocator lock
  gcLock.sync.monitorCount = 0;
  gcLock.sync.threadId = 0;

  // Setup to allow access from java
  memory_base[MEM_ABSOLUTE] = 0;
  memory_base[MEM_HEAP] = (void *)heapStart;
  memory_base[MEM_MEM] = (void *)memory_base;
  exclusiveThread = null;
}


/**
 * This is the primary memory allocator for the Java side of the system
 * it will allocate collectable memory from the heap. If there is currently
 * insufficient space, then it will start a garbage collection phase and set
 * the current thread into a state to wait for this to complete. The request
 * will then be retried. if there is still insufficient memory then an
 * exception will be thrown. Note that this operation relies on the original
 * caller being the byte code interpreter. It performs the retry operation
 * by causing a re-start of the current instruction.
 */
static Object *oheap_allocate(JINT size, byte class, byte length)
{
  Object *ref;
  ref = (Object *)try_allocate(size);
  if (ref == JNULL && allocPrev != &freeList)
  {
    // First attempt failed. Start at the beginning and try again.
    allocPrev = &freeList;
    ref = (Object *)try_allocate(size);
  }
  if (ref == JNULL)
    oheapMemoryRequired += size;
  else
  {
    ref->flags.class = class;
    ref->flags.length = length;
    memset((byte *)(ref + 1), 0, size*WORD_SIZE - sizeof(Object)); 
  }
  return ref;
}

/**
 * @@param size Size of block including header in 4-byte words.
 */
static Object *try_allocate(unsigned int size)
{
  if(oheapMemoryFree >= size)
  {

    FreeObject *regionTop = (FreeObject *)oheapEnd;
    TWOBYTES *prev = allocPrev;
    FreeObject *ptr = (FreeObject *)heapStart + *prev;
#if DEBUG_MEMORY
    printf("Allocate %d - free %d\n", size, oheapMemoryFree-size);
#endif
    while (ptr < regionTop)
    {
      unsigned int freeSz = get_free_size(ptr);
      if (size <= freeSz)
      {
        if (ptr == sweepFree) sweepFree = null;
        /* allocate from this block */
        if (size < freeSz)
        {
          /* cut into two blocks */
          /* first block gets remaining free space */
          freeSz -= size;
          set_free_size(ptr, freeSz);
          ptr += freeSz ; /* second block gets allocated */
          allocPrev = prev;
        }
        else
        {
          // All used up unlink from free list
          //*prev = ptr->next;
          *prev = ptr->next;
          // start next search from the head of the list
          allocPrev = &freeList;
        }
        // Mark the object as allocated
        oheapMemoryFree -= size;
        set_reference(ptr);
        // We need to GC mark any object that has not yet been swept
        // we also clear the monitor part of the header all in one go.
        if ((Object *)ptr >= sweepBase)
          *((FOURBYTES *)ptr) = GC_MASK;
        else
          *((FOURBYTES *)ptr) = 0;
        return (Object *)ptr;
      }
      else
      {
        /* continue searching */
        prev = &(ptr->next);
        ptr = (FreeObject *)heapStart + *prev;
      }
    }
  }
  return JNULL;
}

/**
 * Scan static area of all classes. For every non-null reference field
 * call mark_object function.
 */
static void mark_static_objects(void)
{
  MasterRecord* mrec = get_master_record();
  STATICFIELD* staticFieldBase = (STATICFIELD*) get_static_fields_base();
  byte* staticStateBase = get_static_state_base();
  byte* staticState = staticStateBase;
  int cnt = mrec->numStaticFields;
  int idx = 0;

  while(cnt-- > 0)
  {
    STATICFIELD fieldRecord = staticFieldBase[ idx ++];
    byte fieldType = (fieldRecord >> 12) & 0x0F;
    byte fieldSize = typeSize[ fieldType];

    if(fieldType == T_REFERENCE)
    {
      Object* obj = (Object*) get_word_4_ns(staticState);
      if(obj != NULL)
        mark_object(obj, 0);
    }

    staticState += fieldSize;
  }
}

/**
 * Scan slot stacks of threads (local variables and method params).
 * For every slot containing reference value call the mark_object
 * function. Additionally, call this function for the thread itself,
 * for both its stacks and optionly for monitor object. This allows
 * avoiding garbage-collecting them.
 */
static void mark_local_objects()
{
  int i;
  // Make sure the stack frame for the current thread is up to date.
  if (currentThread != null)
  {
    StackFrame *currentFrame = current_stackframe();
    if (currentFrame != null) update_stack_frame(currentFrame);
  }
  if (threads) mark_object((Object *)threads, 0);
  for(i = 0; i < MAX_PRIORITY; i ++)
  {
    Thread* th0 = threadQ[ i];
    Thread* th = th0;

    while(th != NULL)
    {
      mark_object((Object*) th, 0);
      mark_object((Object*) th->stackArray, 0);
      mark_object((Object*) th->stackFrameArray, 0);

      if(th->waitingOn != 0)
        mark_object((Object*) th->waitingOn, 0);

      Object* sfObj = word2ptr(th->stackFrameArray);
      StackFrame* stackFrame = ((StackFrame*) array_start(sfObj)) + th->stackFrameIndex;
      Object* saObj = word2ptr(th->stackArray);
      STACKWORD* stackBottom = (STACKWORD*) jint_array(saObj);
      STACKWORD* stackTop = stackFrame->stackTop;
      STACKWORD* sp;
      for(sp = stackBottom; sp <= stackTop; sp ++)
      {
        Object* ptr = word2obj(*sp);

        if(is_reference(ptr))
        {
          /* Now we know that ptr points to a valid allocated object.
             It does not mean, that this slot contains a reference variable.
             It may be an integer or float variable, which has exactly the
             same value as a reference of one of the allocated objects.
             But it is no harm. We can safely "mark" it, In such a case
             we may just leave an unreachable object uncollected. */
          mark_object(ptr, 0);
        }
      }

      th = word2ptr(th->nextThread);
      if(th == th0)
        break;
    }
  }
}

/**
 * Mark an object. The object has been identified as either an array of refs
 * or a class instance that contains one or more ref fields. We need to mark
 * each ref in the object, and then mark the object itself. Note that we will
 * normally perform a recursive mark of the references. However if we run out
 * of time, we will complete the mark of the object by quickly marking each
 * child object LIGHTGREY. These objects will then be found and marked during
 * the overflow scan stage.
 */
static void process_object(Object *obj, int callLimit)
{
  if(is_array(obj))
  {
    // Must be an array of refs.
#ifdef VERIFY
    assert(get_element_type(obj) == T_REFERENCE, MEMORY3);
#endif
    REFERENCE* refarr = ref_array(obj);
    REFERENCE* refarrend = refarr + get_array_length(obj);
    while(refarr < refarrend)
    {
      Object* ob = (Object*) (*refarr ++);
      if(ob != NULL)
      {
        if (GC_TIMEOUT()) 
          mark_object(ob, 0);
        else
          mark_object(ob, callLimit);
      }
    }
  }
  else
  {
    // Object is a normal class instance with ref fields
    byte classIndex = get_class_index(obj);
    ClassRecord* classRecord;
    byte* statePtr;
  
    classRecord = get_class_record(classIndex);
    statePtr = (byte*) obj;
    /* Scan the object in reverse so we start at the end of the object */
    statePtr += classRecord->classSize;
    /* now we can scan the member fields */
    while (classIndex != JAVA_LANG_OBJECT)
    {
      if(get_field_cnt(classRecord))
      {
        int i;
        for(i = get_field_cnt(classRecord)-1; i >= 0; i--)
        {
          byte fieldType = get_field_type(classRecord, i);
          byte fieldSize = typeSize[fieldType];
          statePtr -= fieldSize;
          if(fieldType == T_REFERENCE)
          {
            //if(! (classIndex == JAVA_LANG_THREAD && i == 0))
            {
              Object* robj = (Object*) get_word_4_ns(statePtr);
              if(robj != NULL)
              {
                if (GC_TIMEOUT()) 
                  mark_object(robj, 0);
                else
                  mark_object(robj, callLimit);
              }
            }
          }
  
        }
      }
      classIndex = classRecord->parentClass;
      classRecord = get_class_record(classIndex);
    }
  }
  set_gc_marked(obj, GC_BLACK);
}

/**
 * A function which performs a "mark" operation for an object.
 * If it is an array of references recursively call mark_object
 * for every non-null array element.
 * Otherwise "mark" every non-null reference field of that object.
 * To handle deep structures we use a combination of recursive marking
 * and an explicit mark list. The recursive approach works well (especially
 * for arrays and objects with a large number of refs), but requires more
 * memory per call. So we use recusrion to a fixed depth and then switch
 * to an explicit list. If this in turn overflows, we color the object
 * LIGHTGREY and pick it up using a sweep through all objects.
 */
static void mark_object(Object *obj, int callLimit)
{
  int col = get_gc_mark(obj);
  if (col >= GC_DARKGREY)
    return;
  if (col == GC_WHITE)
  {
    // Deal with non recusrive data types we don't need to examine these
    // any deeper...
    byte cls = get_class_index(obj);; 
    if (has_norefs(get_class_record(cls)))
    {
      // Object has no references in it so just mark it.
      set_gc_marked(obj, GC_BLACK);
      return;
    }
    if (cls == JAVA_LANG_STRING)
    {
      // Strings are very common, mark them in one go
      Object* chars = word2obj(get_word_4_ns((byte*)(&(((String *)obj)->characters))));
      // mark the character array
      if(chars != NULL)
        set_gc_marked(chars, GC_BLACK);
      set_gc_marked(obj, GC_BLACK);
      return;
    }
  }
  // Object must have fields that require marking
  if (callLimit > 0)
    process_object(obj, callLimit - 1);
  else
  {
    int next = markQNext(markQHd);
    // Try and push the entry on the mark queue
    if (next == markQTl)
    {
      // No space, so we fall back on a complete scan 
      if (col != GC_WHITE) return;
      if (obj < overflowScan)
        overflowScan = obj;
      set_gc_marked(obj, GC_LIGHTGREY);
    }
    else
    {
      markQ[markQHd] = obj - (Object *)heapStart;
      markQHd = next;
      set_gc_marked(obj, GC_DARKGREY);
    }
  }
}

/**
 * Process all of the objects currently on the mark queue. 
 */
static void process_queue()
{
  while (markQTl != markQHd)
  {
    Object *obj = (Object *)heapStart + markQ[markQTl];
    process_object(obj, MAX_CALL_DEPTH);
    markQTl = markQNext(markQTl);
    if (GC_TIMEOUT()) break;
  }
}

/**
 * Perform the sweep phase of the GC. We scan all objects in the heap. Those
 * that are not marked as BLACK are no longer in use and may be released. We
 * merge adjancent free objects together as we go. We build up the free list
 * as part of this scan. Note that this operation runs in parallel with
 * allocation calls, so we must ensure that any newly allocated objects are
 * colored BLACK if they are ahead of the sweep point. We clear the mark bits
 * of currently live objects as we pass.
 */
static void sweep()
{
  int mf = 0;
  Object* ptr = sweepBase;
  FreeObject* fptr = sweepFree;
  Object* regionTop = (Object *)oheapEnd;
  TWOBYTES flist = freeList;

  while(ptr < regionTop && !GC_TIMEOUT())
  {
    byte cls = get_class_index(ptr);
    unsigned int size = get_size(ptr);
    // Round up according to alignment
    if(is_gc_marked(ptr))
    {
      clr_gc_marked(ptr);
      fptr = null;
    }
    else
      if (get_monitor_count(&ptr->sync) == 0 || cls == T_FREE)
      {
          // Set object free
          if (cls != T_FREE)
          {
            clr_reference(ptr);
            if (is_ah_array(ptr))
            {
              aheap_free((AHeapObj *)(array_start(ptr) - sizeof(AHeapObj)));
            }
          }
          mf += size;
          // Got a free block can we merge?
          if (fptr != null)
          {
            unsigned int fsize = get_size((Object *)fptr);
            fsize += size;
            set_free_size(fptr, fsize);
            if (fsize > oheapLargestFree) oheapLargestFree = fsize;
          }
          else
          {
            fptr = (FreeObject *)ptr;
            // add to free list
            fptr->next = flist;
            flist = ((Object *)fptr - (Object *)heapStart);
            set_free_size(fptr, size);
            if (size > oheapLargestFree) oheapLargestFree = size;
          }
      }
      else
        fptr = null;

    ptr += size;
  }

  oheapMemoryFree += mf;
  // Remember current state for next scan
  sweepBase = ptr;
  sweepFree = fptr;
  freeList = flist;
}


/**
 * Scan the heap for objects that overflowed the mark queue. These will be
 * colored LIGHTGREY. We mark these objects as we pass. Note that marking an
 * object may generate new overlow cases, these will re-set the sweep point.
 */
static
void mark_overflow()
{
  Object* ptr = overflowScan;
  Object* regionTop = oheapEnd;
  overflowScan = regionTop;
  
  while(ptr < regionTop && !GC_TIMEOUT())
  {
    unsigned int size = get_size(ptr);
    // Round up according to alignment
    if (get_gc_mark(ptr) == GC_LIGHTGREY)
    {
      //process_object(obj, MAX_CALL_DEPTH);
      mark_object(ptr, 0);
      process_queue();
    }
    ptr += size;
  }
  // If we have not finished the scan remember it for next time.
  if (ptr < overflowScan)
    overflowScan = ptr;
}

/**
 * Serach the object heap looking for a free object after/equal to ptr
 * return a pointer to the free list link item, to allow the list to be
 * updated.
 */
static
TWOBYTES *get_free_ptr(Object *ptr)
{
  Object* regionTop = oheapEnd;
  TWOBYTES* fptr = &freeList;
  while(ptr < regionTop)
  {
    if (get_class_index(ptr) == T_FREE)
    {
      fptr = &(((FreeObject *)ptr)->next);
      break;
    }
    ptr += get_size(ptr);
  }
  return fptr;
}

/**
 * Attempt to expand the space available to the object heap.
 * We do this by "stealing" free space from the array heap
 */
int
expand_object_heap(int required)
{
  if (required < MIN_HEAP_ADJUST) required = MIN_HEAP_ADJUST;
  if (required > aheapMemoryFree) required = aheapMemoryFree;
  if (required > 0)
  {
    TWOBYTES *fptr = get_free_ptr(oheapStart);
    // allocate the space
    aheapMemoryFree -= required;
    aheapEnd -= required;
    oheapStart -= required;
    oheapMemoryFree += required;
    *fptr = oheapStart - (Object *)heapStart;  
    set_free_size((FreeObject *)oheapStart, required);
    ((FreeObject *)oheapStart)->next = FL_END;
  }
  return required;
}

/**
 * Attempt to expand the size of the array heap.
 * We do this by "stealing" free space from the object heap. Note that we can
 * only do this if the first block in the object heap is free. We arrange 
 * things so that this block is always last in the free list.
 */
int
expand_array_heap(int required)
{
  if (required < MIN_HEAP_ADJUST) required = MIN_HEAP_ADJUST;
  // We can only expand into the object heap if the start is free
  if (get_class_index(oheapStart) == T_FREE)
  {
    int sz = get_free_size(oheapStart);
    TWOBYTES* fptr = get_free_ptr(oheapStart + sz);
    if (required > sz) required = sz;
    // allocate the space
    aheapMemoryFree += required;
    aheapEnd += required;
    oheapStart += required;
    if (required != sz)
    {
      // set the new header
      set_free_size((FreeObject *)oheapStart, sz - required);
      ((FreeObject *)oheapStart)->next = FL_END;
      // update the free list
      *fptr = oheapStart - (Object *)heapStart;
    }
    else
      *fptr = FL_END;
    oheapMemoryFree -= required;
    return required;
  }
  else
    return 0;
}


/**
 * Main garbage collecting function.
 * This is called to perform one incremental step. It will step through each
 * of the following stages making progress on each call and returning after
 * performing one increnet of work. The macro GC_TIMEOUT defines when the
 * increment is complete. When all of the phases are complete gcPhase will
 * be reset to GC_IDLE. The phases are:
 * MARKROOTS: All of the statics, stacks and system vars are marked.
 * MARK: All of the refs found from the roots are traced and marked
 * SWEEP: The entire heap is swept freeing and merging unused objects.
 */
void gc_run_collector(void)
{
  switch (gcPhase)
  {
    case GC_IDLE:
      break;
    case GC_MARKROOTS:
      overflowScan = oheapEnd;
      mark_static_objects();
      mark_local_objects();
      sweepBase = oheapStart;
      sweepFree = NULL;
      gcPhase = GC_MARK;
      // Fall through
    case GC_MARK:
      // Trace all refs. Our job is done when the mark queue is empty and we
      // have completed an overflow scan of the heap.
      while ((overflowScan != oheapEnd) || (markQTl != markQHd))
      {
        process_queue();
        if (GC_TIMEOUT()) goto exit;
        if (overflowScan != oheapEnd)
        {
          mark_overflow();
        }
        if (GC_TIMEOUT()) goto exit;
      }
      oheapMemoryFree = 0;
      oheapLargestFree = 0;
      freeList = FL_END;
      allocPrev = &freeList;
      gcPhase = GC_SWEEP;
      // Fall through
    case GC_SWEEP:
      sweep();
      if (oheapMemoryRequired > 0 && oheapLargestFree > oheapMemoryRequired)
      {
        // We may have enough free space to meet current requests, so wake up
        // any threads that are waiting for memory.
        alloc_notify();
        oheapMemoryRequired = 0;
      }
      if (GC_TIMEOUT()) goto exit;
      allocPrev = &freeList;
      sweepBase = oheapEnd;
      sweepFree = null;
      if (aheapStart == aheapEnd)
      {
        alloc_notify();
        oheapMemoryRequired = 0;
        aheapMemoryRequired = 0;
        gcPhase = GC_IDLE;
        break;
      }
      gcPhase = GC_COMPACT;
    case GC_COMPACT:
      if ((aheapMemoryRequired > 0) ||
          (oheapMemoryRequired > oheapLargestFree) ||
          (oheapMemoryFree < MIN_HEAP && (MIN_HEAP - oheapMemoryFree >= aheapMemoryFree)))
        compact_aheap();
      gcPhase = GC_EXPAND;
    case GC_EXPAND:
      if (oheapLargestFree < oheapMemoryRequired)
        expand_object_heap(oheapMemoryRequired);
      else if (aheapMemoryFree < aheapMemoryRequired)
      {
        expand_array_heap(aheapMemoryRequired);
        if (aheapMemoryFree < aheapMemoryRequired)
          expand_object_heap(aheapMemoryRequired);
      }
      else if (oheapMemoryFree < MIN_HEAP)
        expand_object_heap(MIN_HEAP - oheapMemoryFree);
      else if (oheapMemoryFree - MIN_HEAP > MIN_HEAP_ADJUST)
        expand_array_heap(oheapMemoryFree - MIN_HEAP);
      oheapMemoryRequired = 0;
      aheapMemoryRequired = 0;
      // Notify any waiting threads that there may now be more memory
      alloc_notify();
      gcPhase = GC_IDLE;
  }
exit:
  return;
}

/**
 * Perform a full GC and make the thread wait for it to complete
 */
int garbage_collect()
{
  if (!alloc_start()) return EXEC_RETRY;
  if (retryState >= GC_RETRY)
  {
    // The current thread already owns the memory lock so this must
    // be a retry attempt which means we are done!
    alloc_complete((Object *)1);
    return EXEC_CONTINUE;
  }
  // If not already collecting, start a collection
  aheapMemoryRequired += 1;
  retryState = GC_RETRY;
  alloc_complete(JNULL);
  return EXEC_RETRY;
}

/**
 * Force a full garbage collect and wait for it to complete
 */
void wait_garbage_collect()
{
  // Wait for any active collect to complete
  boolean saved = gMakeRequest;
  // release any locks
  exclusiveThread = null;
  alloc_start();
  while (gcPhase != GC_IDLE)
  {
    gMakeRequest = false;
    gc_run_collector();
  }
  // now force a new one
  gcPhase = GC_MARKROOTS;
  aheapMemoryRequired += 1;
  while (gcPhase != GC_IDLE)
  {
    gMakeRequest = false;
    gc_run_collector();
  }
  gMakeRequest = saved;
  alloc_complete((Object *)1);
}

/**
 * The following two functions form the garbage collector "write barrier"
 * they operate to preserve the "snapshot at the begining" data model used
 * by this collector. They ensure that during the mark phase any update to
 * a non-root item is only made after that item has been marked.
 * NOTE: These functions should not be called directly. Instead use the
 * macros in memory.h which implement the require guard conditions.
 */
void gc_update_object(Object *obj)
{
  // Object is a normal class instance with ref fields
  byte classIndex = get_class_index(obj);
  ClassRecord* classRecord;
  byte* statePtr;
  // Following essential tests implement in the calling macro
  //if (gcPhase != GC_MARK) return;
  //if (get_gc_mark(obj) == GC_BLACK) return;
  classRecord = get_class_record(classIndex);
  if (!has_norefs(classRecord))
  {
    statePtr = (byte*) obj;
    /* Scan the object in reverse so we start at the end of the object */
    statePtr += classRecord->classSize;
    /* now we can scan the member fields */
    while (classIndex != JAVA_LANG_OBJECT)
    {
      if(get_field_cnt(classRecord))
      {
        int i;
        for(i = get_field_cnt(classRecord)-1; i >= 0; i--)
        {
          byte fieldType = get_field_type(classRecord, i);
          byte fieldSize = typeSize[fieldType];
          statePtr -= fieldSize;

          if(fieldType == T_REFERENCE)
          {
            /* omit nextThread field of Thread class */
  
            //if(! (classIndex == JAVA_LANG_THREAD && i == 0))
            {
              Object* robj = (Object*) get_word_4_ns(statePtr);
              if(robj != NULL)
                mark_object(robj, 0);
            }
          }
        }
      }
      classIndex = classRecord->parentClass;
      classRecord = get_class_record(classIndex);
    }
  }
  set_gc_marked(obj, GC_BLACK);
}

void gc_update_array(Object *obj)
{
  // Following essential tests implemented in the calling macro
  //if (gcPhase != GC_MARK) return;
  //if (get_gc_mark(obj) == GC_BLACK) return;
  {
    REFERENCE* refarr = ref_array(obj);
    REFERENCE* refarrend = refarr + get_array_length(obj);
    while(refarr < refarrend)
    {
      Object* ob = (Object*) (*refarr ++);
      if(ob != NULL)
        mark_object(ob, 0);
    }
  }
  set_gc_marked(obj, GC_BLACK);
}

/**
 * The following functions implement the moveable array heap.
 * The heap is a simple copy allocator, with a single free space.
 */

/**
 * Allocate space from the free area.
 */
static AHeapObj *aheapAllocate(JINT sz, Object *obj)
{
  if (sz <= aheapMemoryFree)
  {
    AHeapObj *ah = (AHeapObj *)(aheapAlloc);
    aheapMemoryFree -= sz;
    ah->available = 0;
    ah->object = obj - (Object *)heapStart;
    ah->thread = 0;
    aheapAlloc += sz;
    memset((byte *)(ah + 1), 0, sz*WORD_SIZE - sizeof(AHeapObj)); 
    return ah;
  }
  return JNULL;
}

/**
 * Mark part of the heap as no longer in use.
 */
static void aheap_free(AHeapObj *ah)
{
  BigArray *obj = (BigArray *)((Object *)heapStart + ah->object);
  if (obj->length == 0)
  {
    //printf("ahf %x zero\n", ah);
  }
  else
  {
    int sz = array_data_size(obj->hdr.flags.class, obj->length);
    ah->available = 1;
    ah->object = sz + AH_OBJ_SIZE;
  }
}

/**
 * Compact the heap by copying data down filling any free spaces.
 */
static void compact_aheap()
{
  // First walk the objects and create a list
  FOURBYTES *ptr = aheapStart;
  int offset = 0;
  while (ptr < aheapAlloc)
  {
    AHeapObj *ah = (AHeapObj *)ptr;
    if (ah->available)
    {
      offset += ah->object;
      ptr += ah->object;
    }
    else
    {
      BigArray *obj = (BigArray *)((Object *)heapStart + ah->object);
      int sz = array_data_size(obj->hdr.flags.class, obj->length) + AH_OBJ_SIZE;
      if (offset > 0)
      {
        obj->offset -= (offset);
        // If this block is a stack frame, update any pointers to it.
        if (ah->thread)
          update_stack_frames((Thread *)(heapStart + ah->thread), -offset);
        memmove((byte *)((FOURBYTES *)ah - offset), (byte *)ah, sz*WORD_SIZE);
      }
      ptr += sz;
    }
  }
  aheapMemoryFree += offset;
  aheapAlloc -= offset;
}

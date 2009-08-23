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
// Value is in 2-byte units and must be a power of 2
#define MEMORY_ALIGNMENT 2

// Basic types and sizes
const byte typeSize[] = { 
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

static TWOBYTES *heap_end;      /* pointer to end of heap */
static TWOBYTES *heap_start;    /* pointer to start of heap */

static unsigned int memory_size;    /* total number of words in heap */
static unsigned int memory_free;    /* total number of free words in heap */

TWOBYTES failed_alloc_size;

// Location of major memory based structures. Used to allow Java access to
// VM memory.
byte *memory_base[MEM_MEM+1];
static Object *java_allocate (JINT size);


/**
 * @@param numWords Number of 2-byte  words in the data part of the object
 */
#define initialize_state(PTR_,NWORDS_) memset((PTR_), 0, (NWORDS_)*2)

#define array_data_size(CLS_, LEN_) ((get_class_record(CLS_)->classSize*LEN_ + 1)/2)
#define array_size(OBJ_) ((is_std_array(OBJ_) ? NORM_OBJ_SIZE : BA_OBJ_SIZE) + array_data_size(get_class_index(OBJ_), get_array_length(OBJ_)))
#define obj_size(OBJ_) ((get_class_record(get_class_index(OBJ_))->classSize+1)/2)
#define get_size(OBJ_) (is_array(OBJ_) ? array_size(OBJ_) : obj_size(OBJ_))
#define array_element_size(OBJ_) (get_class_record(get_class_index(OBJ_))->classSize)
#define get_free_size(OBJ_) ((is_std_array(OBJ_) ? NORM_OBJ_SIZE : BA_OBJ_SIZE) + get_array_length(OBJ_))

/**
 * Zeroes out memory.
 * @@param ptr The starting address.
 * @@param numWords Number of two-byte words to clear.
 * Now slightly optmized;
 */
void zero_mem(TWOBYTES *ptr, TWOBYTES numWords)
{
  TWOBYTES* end = ptr + numWords;

  while(ptr < end)
    *ptr++ = 0;
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
  JINT instanceSize;
#if DEBUG_MEMORY
  printf("New object for class %d\n", classIndex);
#endif
  instanceSize = (get_class_record(classIndex)->classSize+1)/2;
  
  ref = java_allocate (instanceSize);

  if (ref == null)
  {
#if DEBUG_MEMORY
  printf("New object for class returning null\n");
#endif
    return JNULL;
  }

  // Initialize default values

  ref->flags.length = LEN_OBJECT;
  ref->flags.class = classIndex;

  initialize_state (fields_start(ref), instanceSize - NORM_OBJ_SIZE);
  #if DEBUG_OBJECTS || DEBUG_MEMORY
    printf ("new_object_for_class: returning %d\n", (int) ref);
  #endif
  return ref;
}

/**
 * Return the size in words of an array of the given type
 */

static void set_array (Object *obj, const TWOBYTES length, const int baLength, const byte cls)
{
  #ifdef VERIFY
  assert (baseType <= (ELEM_TYPE_MASK >> ELEM_TYPE_SHIFT), MEMORY0); 
  assert (length <= (ARRAY_LENGTH_MASK >> ARRAY_LENGTH_SHIFT), MEMORY1);
  #endif
  // Set object specific flags
  obj->flags.class = cls;
  obj->flags.length = length;
  // If this is a big array set the real length
  if (baLength)
  {
    ((BigArray *)obj)->length = baLength;
  }
    
  #ifdef VERIFY
  assert (is_array(obj), MEMORY3);
  #endif
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
  JINT allocSize;
  FOURBYTES hdrSize;
  FOURBYTES baLength;

  if (length < 0)
  {
    throw_exception(negativeArraySizeException);
    return JNULL;
  }
  allocSize = array_data_size (cls, length);
#if DEBUG_MEMORY
  printf("New array of cls %d, length %ld\n", cls, length);
#endif
  // If this is a large array use a bigger header
  if (length >= LEN_BIGARRAY)
  {
    hdrSize = BA_OBJ_SIZE;
    baLength = length;
    length = LEN_BIGARRAY;
  }
  else
  {
    baLength = 0;
    hdrSize = NORM_OBJ_SIZE;
  }
  ref = java_allocate (allocSize + hdrSize);
#if DEBUG_MEMORY
  printf("Array ptr=%d\n", (int)ref);
#endif
  if (ref == null)
    return JNULL;
  set_array (ref, length, baLength, cls);
  initialize_state (array_start(ref), allocSize);
  return ref;
}

/**
 * Create an array contain elements of the specified primitive type.
 * NOTE: This function may be used to create objects that are not
 * Java compatible (like the stack frame array). It may also be called before
 * the program context is available, so care must be taken with accesing
 * class information etc.
 */
Object *new_primitive_array(const byte typ, JINT length)
{
  return new_array(ALJAVA_LANG_OBJECT + typ, length);
}


/**
 * Create a single dimension array. The class of the array is given by cls.
 * NOTE: cls is the array class NOT the class of the elements.
 */
Object *new_single_array (const byte cls, JINT length)
{
  return new_array(cls, length);
}



/**
 * @@param cls The class index of the array
 * @@param reqDimensions Number of requested dimensions for allocation.
 * @@param numElemPtr Pointer to first dimension. Next dimension at numElemPtr+1.
 */
Object *new_multi_array (const byte cls,
                         byte reqDimensions, STACKWORD *numElemPtr)
{
  Object *ref;
  Object *ref2;
  ClassRecord *classRec = get_class_record(cls);
  byte totalDimensions = get_dim(classRec);
  JINT ne;
  int elemCls;

  #ifdef VERIFY
  assert (totalDimensions >= 1, MEMORY6);
  assert (reqDimensions <= totalDimensions, MEMORY8);
  #endif
  if (reqDimensions == 0)
    return JNULL;

  if (totalDimensions >= MAX_VM_REFS)
  {
    throw_exception (outOfMemoryError);
    return JNULL;
  }

  if (totalDimensions == 1)
    return new_single_array (cls, (JINT)*numElemPtr);


  ref = new_array (cls, (JINT)*numElemPtr);
  if (ref == JNULL)
    return JNULL;
  // If this is a partial array we are done...
  if (reqDimensions == 1) return ref;
  // Make sure we protect each level from the gc. Once we have returned
  // the ref it will be protected by the level above.
  protect_obj(ref);
  
  ne = (JINT)*numElemPtr;
  elemCls = get_element_class(classRec);
  while (ne--)
  {
    ref2 = new_multi_array (elemCls, reqDimensions - 1, numElemPtr + 1);
    if (ref2 == JNULL)
    {
      unprotect_obj(ref);
      return JNULL;
    }
    ref_array(ref)[ne] = ptr2word (ref2);
  }
  unprotect_obj(ref);

  return ref;
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
    return throw_exception(nullPointerException);
  if (!is_array(src) || !is_array(dst))
    return throw_exception(arrayStoreException);
  // Check type compatibility...
  srcCls = get_element_class(get_class_record(get_class_index(src)));
  dstCls = get_element_class(get_class_record(get_class_index(dst)));
  primitive = is_primitive(srcCls) || is_primitive(dstCls);
  if (primitive && srcCls != dstCls)
    return throw_exception(arrayStoreException);
  if (srcOff < 0 || (srcOff + len > get_array_length(src)) ||
      dstOff < 0 || (dstOff + len > get_array_length(dst)))
    return throw_exception(arrayIndexOutOfBoundsException);
 
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
        return throw_exception(arrayStoreException);
    }
  }
  return EXEC_CONTINUE;
}


void free_array (Object *objectRef)
{
  #ifdef VERIFY
  assert (is_array(objectRef), MEMORY7);
  #endif // VERIFY
  deallocate ((TWOBYTES *) objectRef, get_size (objectRef));
}

#if !FIXED_STACK_SIZE
Object *reallocate_array(Object *obj, JINT newlen)
{
  byte cls = get_class_index(obj);
  Object *newArray = new_array(cls, newlen);
  if (newArray != JNULL)
  {
    // Copy old array to new
    memcpy(array_start(newArray), array_start(obj), get_array_length(obj) * get_class_record(cls)->classSize);
  
    // Free old array
    free_array(obj);
  }
    
  return newArray;
}
#endif

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
  JINT allocSize;

  // We actually use an integer array for the storage, so round up
  sz = (sz + sizeof(JINT) - 1)/sizeof(JINT);
  allocSize = array_data_size (T_INT+ALJAVA_LANG_OBJECT, sz);
  // We always use big array format so the header is a known fixed size
  ref = java_allocate (allocSize + BA_OBJ_SIZE);
  if (ref == null)
    return JNULL;
  set_array (ref, LEN_BIGARRAY, sz, T_INT+ALJAVA_LANG_OBJECT);
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
  // Get a pointer to the object header
  ref = (Object *)(mem - BA_OBJ_SIZE*2);
  unprotect_obj(ref);
  free_array(ref);
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


void memory_init ()
{
  #ifdef VERIFY
  memoryInitialized = true;
  #endif

  memory_size = 0;
  memory_free = 0;

}


int getHeapSize() {
  return ((int)memory_size) << 1;
}

int getHeapFree() {
  return ((int)memory_free) << 1;
}

int getRegionAddress()
{
  return (int)heap_start;
}

FOURBYTES mem_peek(int base, int offset, int typ)
{
  return get_word_ns((byte *)(memory_base[base] + offset), typ);
}

void mem_copy(Object *obj, int objoffset, int base, int offset, int len)
{
  if (obj == null)
    throw_exception(nullPointerException);
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
 * Classic GC colours
 */
#define GC_WHITE 0
#define GC_LIGHTGREY 1
#define GC_DARKGREY 2
#define GC_BLACK 3

// Heap allocation and sweep pointers
static TWOBYTES *alloc_prev;    /* current position in the free list */
static TWOBYTES *sweep_base;    /* current position of sweeping */
static TWOBYTES *sweep_free;    /* current position of sweeping free ptr */
static TWOBYTES *overflowScan;  /* current position of overflow scan */
Object gcLock = {{
    .length = LEN_OBJECT,
    .class = JAVA_LANG_OBJECT,
    .mark = GC_BLACK,
}, {0, 0}};
// Mark queue
#define MAX_CALL_DEPTH 8
#define MARK_Q_SIZE 64
static TWOBYTES markQ[MARK_Q_SIZE];
static int markQHd = 0;
static int markQTl = 0;
#define markQNext(cur) (((cur) + 1) % MARK_Q_SIZE)

// GC states are defined in memory.h to make them available to macros
int gcPhase = GC_IDLE;
// Macro used to indicate when to suspend this mark/sweep step
#define GC_TIMEOUT() (gMakeRequest)
//#define GC_TIMEOUT() (0)

// Value used to mark end of the free list. Will always point beyond the
// end of the heap, when used as an offset.
#define FL_END 0xffff
// Start of the freelist
static TWOBYTES freeList = FL_END;
static int newlyFreed;         /* Amount of memory freed so far in this sweep */
static int memRequired;        /* Target amount of memory needed */
/**
 * Forward declarations.
 */
static void mark_object(Object *obj, int callLimit);
static TWOBYTES *try_allocate(unsigned int size);

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
static void set_reference(TWOBYTES* ptr)
{
  int refIndex = ((byte*) ptr - (byte*)heap_start) / (MEMORY_ALIGNMENT * 2);

  ((byte*) heap_end)[ refIndex >> 3] |= 1 << (refIndex & 7);

}

static void clr_reference(TWOBYTES* ptr)
{
  int refIndex = ((byte*) ptr - (byte*)heap_start) / (MEMORY_ALIGNMENT * 2);

  ((byte*) heap_end)[ refIndex >> 3] &= ~ (1 << (refIndex & 7));

}

#if 0
static boolean is_reference_old(TWOBYTES* ptr)
{
  /* The reference must belong to a memory region. */
  TWOBYTES* regBottom = heap_start;
  TWOBYTES* regTop = heap_end;

  if(ptr >= regBottom && ptr < regTop)
  {
    /* It must be properly aligned */
    if(((int)ptr & ((MEMORY_ALIGNMENT * 2) - 1)) == 0)
    {
      /* Now we can safely check the corresponding bit in the reference bitmap. */
      int refIndex = ((byte*) ptr - (byte*)heap_start) / (MEMORY_ALIGNMENT * 2);

      return (((byte*) heap_end)[ refIndex >> 3] & (1 << (refIndex & 7))) != 0;
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
    unsigned int refIndex = ptr - heap_start;

    if (refIndex  < (unsigned int)memory_size)
    {
      refIndex /= 2;
      //return (((byte*) heap_end)[ refIndex >> 3] & (1 << (refIndex & 7))) != 0;
      if ((((byte*) heap_end)[ refIndex >> 3] & (1 << (refIndex & 7))) != 0)
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

static inline boolean is_reference(TWOBYTES* ptr)
{
  /* The reference must belong to a memory region. */
  if(((int)ptr & ((MEMORY_ALIGNMENT * 2) - 1)) == 0)
  {
    unsigned int refIndex = ptr - heap_start;

    if (refIndex  < (unsigned int)memory_size)
    {
      refIndex /= 2;
      return (((byte *) heap_end)[ refIndex >> 3] & (1 << (refIndex & 7))) != 0;
    }
  }
  return false;
}

static inline void set_free_size(Object *ptr, TWOBYTES sz)
{
  if (sz == ((sizeof(Object)/2 + MEMORY_ALIGNMENT - 1) & ~(MEMORY_ALIGNMENT - 1)))
    // zero length void array
    *((TWOBYTES *)ptr) = T_FREE;
  else
  {
    *((TWOBYTES *)ptr) = T_FREE | (LEN_BIGARRAY << ARRAY_LENGTH_SHIFT);
    ((BigArray *)ptr)->length = sz - sizeof(BigArray)/2;
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

  /* align upwards */
  heap_start = (TWOBYTES *) (((unsigned int)start+ MEMORY_ALIGNMENT*2 - 1) & ~(MEMORY_ALIGNMENT*2 - 1));

  /* align downwards */
  heap_end = (TWOBYTES *) ((unsigned int)end & ~(MEMORY_ALIGNMENT*2 - 1)); 
  /* To be able to quickly identify a reference like stack slot
     we use a dedicated referance bitmap. With alignment of 4 bytes
     the map is 32 times smaller then the heap. Let's allocate
     the map by lowering the heap end pointer by the map size.
     The map must be zeroed. */
  contents_size = heap_end - heap_start;
  /* Calculate the required bitmap size (in words). Note that we devide here
     by 33 rather then 32 to take into account the reduction in size of the
     heap due to the bitmap size!  */
  bitmap_size = (contents_size / (((MEMORY_ALIGNMENT * 2) * 8) + 1) + 2) & ~1;
  heap_end -= bitmap_size;
  zero_mem(heap_end, bitmap_size);
  /* create free block in the heap */
  contents_size = heap_end - heap_start;
  set_free_size((Object *)heap_start, contents_size);
  // Initialise the free list
  *(heap_start+1) = FL_END;
  freeList = 0;
  alloc_prev = &freeList;
  // Incremental mark/sweep state
  sweep_base = heap_end;
  sweep_free = NULL;
  memRequired = 0;
  gcPhase = GC_IDLE;
  // Reset the allocator lock
  gcLock.sync.monitorCount = 0;
  gcLock.sync.threadId = 0;
  /* memory accounting */
  memory_size += contents_size;
  memory_free += contents_size;
  // Setup to allow access from java
  memory_base[MEM_ABSOLUTE] = 0;
  memory_base[MEM_HEAP] = (void *)heap_start;
  memory_base[MEM_MEM] = (void *)memory_base;
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
static Object *java_allocate (JINT size)
{
  Object *ref;
  // Align memory to boundary appropriate for system  
  size = (size + (MEMORY_ALIGNMENT-1)) & ~(MEMORY_ALIGNMENT-1);
  ref = (Object *)try_allocate(size);
  if (ref == JNULL && alloc_prev != &freeList)
  {
    // First attempt failed. Start at the beginning and try again.
    alloc_prev = &freeList;
    ref = (Object *)try_allocate(size);
  }
  if (is_gc_retry())
  {
    // The current thread already owns the memory lock so this must
    // be retry attempt to allocate memory
    exit_monitor(currentThread, &gcLock);
    // If we failed to allocate and the gc has been completed then we give up!
    if (ref == JNULL && gcPhase == GC_IDLE)
    {
      #ifdef VERIFY
      assert (outOfMemoryError != null, MEMORY5);
      #endif
      throw_exception (outOfMemoryError);
      return JNULL;
    }
  }
  if (ref != JNULL)
  {
    // We have allocated the required memory, return it.
    return ref;
  }

  // Allocation failed
  // First attempt failure. We need to force a GC and then make this thread
  // wait for it to complete.
  if (gcPhase == GC_IDLE) gcPhase = GC_MARKROOTS;
  // restart the current instruction when we are woken up
  curPc = getPc();
  //enter_monitor(currentThread, &gcLock);
  // Say how much memory we need...
  system_wait(&gcLock);
  memRequired += 2*size;
  return JNULL;
}

/**
 * @@param size Must be exactly same size used in allocation.
 */
void deallocate (TWOBYTES *p, FOURBYTES size)
{
  // Nothing to do. We let the GC work things out
}


/**
 * @@param size Size of block including header in 2-byte words.
 */
static TWOBYTES *try_allocate(unsigned int size)
{
  if(memory_free >= size)
  {

    TWOBYTES *regionTop = heap_end;
    TWOBYTES *prev = alloc_prev;
    TWOBYTES *ptr = heap_start + *prev;
#if DEBUG_MEMORY
    printf("Allocate %d - free %d\n", size, memory_free-size);
#endif
    while (ptr < regionTop)
    {
      unsigned int freeSz = get_free_size((Object *)ptr);
      if (size <= freeSz)
      {
        if (ptr == sweep_free) sweep_free = null;
        /* allocate from this block */
        if (size < freeSz)
        {
          /* cut into two blocks */
          /* first block gets remaining free space */
          freeSz -= size;
          set_free_size((Object *)ptr, freeSz);
          ptr += freeSz ; /* second block gets allocated */
          /* NOTE: allocating from the top downwards avoids most
                   searching through already allocated blocks */
          alloc_prev = prev;
        }
        else
        {
          // All used up unlink from free list
          *prev = *(ptr + 1);
          // start next search from the head of the list
          alloc_prev = &freeList;
        }
        // Mark the object as allocated
        memory_free -= size;
        set_reference(ptr);
        // We need to GC mark any object that has not yet been swept
        // we also clear the monitor part of the header all in one go.
        if (ptr >= sweep_base)
          *(FOURBYTES *)ptr = GC_MASK;
        else
          *(FOURBYTES *)ptr = 0;
        return ptr;
      }
      else
      {
        /* continue searching */
        prev = ptr + 1;
        ptr = heap_start + *prev;
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
      byte arraySize;

      mark_object((Object*) th, 0);
      mark_object((Object*) th->stackArray, 0);
      mark_object((Object*) th->stackFrameArray, 0);

      if(th->waitingOn != 0)
        mark_object((Object*) th->waitingOn, 0);

      arraySize = th->stackFrameArraySize;
      if(arraySize != 0)
      {
        Object* sfObj = word2ptr(th->stackFrameArray);
        StackFrame* stackFrame = ((StackFrame*) array_start(sfObj)) + (arraySize);
        Object* saObj = word2ptr(th->stackArray);
        STACKWORD* stackBottom = (STACKWORD*) jint_array(saObj);
        STACKWORD* stackTop = stackFrame->stackTop;
        STACKWORD* sp;
    
        for(sp = stackBottom; sp <= stackTop; sp ++)
        {
          TWOBYTES* ptr = word2ptr(*sp);

          if(is_reference(ptr))
          {
            /* Now we know that ptr points to a valid allocated object.
               It does not mean, that this slot contains a reference variable.
               It may be an integer or float variable, which has exactly the
               same value as a reference of one of the allocated objects.
               But it is no harm. We can safely "mark" it, In such a case
               we may just leave an unreachable object uncollected. */

            mark_object((Object*) ptr, 0);
          }
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
      if ((TWOBYTES *)obj < overflowScan)
        overflowScan = (TWOBYTES *)obj;
      set_gc_marked(obj, GC_LIGHTGREY);
    }
    else
    {
      markQ[markQHd] = ((TWOBYTES *)obj - heap_start);
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
    Object *obj = (Object *) (heap_start + markQ[markQTl]);
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
  TWOBYTES* ptr = sweep_base;
  TWOBYTES* fptr = sweep_free;
  TWOBYTES* regionTop = heap_end;

  while(ptr < regionTop && !GC_TIMEOUT())
  {
    unsigned int size;
    Object* obj = (Object*) ptr;
    byte cls = get_class_index(obj);
    size = get_size(obj); 
    // Round up according to alignment
    size = (size + (MEMORY_ALIGNMENT-1)) & ~(MEMORY_ALIGNMENT-1);
    if(is_gc_marked(obj))
    {
      clr_gc_marked(obj);
      fptr = null;
    }
    else
      if (get_monitor_count(&obj->sync) == 0 || cls == T_FREE)
      {
          // Set object free
          if (cls != T_FREE)
            clr_reference(ptr);
          mf += size;
          // Got a free block can we merge?
          if (fptr != null)
          {
            unsigned int fsize = get_size((Object *)fptr);
            fsize += size;
            set_free_size((Object *)fptr, fsize);
          }
          else
          {
            fptr = ptr;
            // add to free list
            *(ptr + 1) = freeList;
            freeList = (ptr - heap_start);
            set_free_size(obj, size);
          }
      }
      else
        fptr = null;

    ptr += size;
  }

  memory_free += mf;
  newlyFreed += mf;
  // Remember current state for next scan
  sweep_base = ptr;
  sweep_free = fptr;
}

/**
 * "Mark" preallocated instances of exception objects
 * to avoid garbage-collecting them.
 */
static void mark_exception_objects(void)
{
  mark_object(outOfMemoryError, 0);
  mark_object(noSuchMethodError, 0);
  mark_object(stackOverflowError, 0);
  mark_object(nullPointerException, 0);
  mark_object(classCastException, 0);
  mark_object(arithmeticException, 0);
  mark_object(arrayIndexOutOfBoundsException, 0);
  mark_object(illegalArgumentException, 0);
  mark_object(interruptedException, 0);
  mark_object(illegalStateException, 0);
  mark_object(illegalMonitorStateException, 0);
  mark_object(arrayStoreException, 0);
  mark_object(error, 0);
}


/**
 * Scan the heap for objects that overflowed the mark queue. These will be
 * colored LIGHTGREY. We mark these objects as we pass. Note that marking an
 * object may generate new overlow cases, these will re-set the sweep point.
 */
static
void mark_overflow()
{
  TWOBYTES* ptr = overflowScan;
  TWOBYTES* regionTop = heap_end;
  overflowScan = regionTop;
  
  while(ptr < regionTop && !GC_TIMEOUT())
  {
    Object* obj = (Object*) ptr;
    unsigned int size = get_size(obj);
    // Round up according to alignment
    size = (size + (MEMORY_ALIGNMENT-1)) & ~(MEMORY_ALIGNMENT-1);
    if (get_gc_mark(obj) == GC_LIGHTGREY)
    {
      //process_object(obj, MAX_CALL_DEPTH);
      mark_object(obj, 0);
      process_queue();
    }
    ptr += size;
  }
  // If we have not finished the scan remember it for next time.
  if (ptr < overflowScan)
    overflowScan = ptr;
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
      overflowScan = heap_end;
      mark_exception_objects();
      mark_static_objects();
      mark_local_objects();
      newlyFreed = 0;
      sweep_base = heap_start;
      sweep_free = NULL;
      gcPhase = GC_MARK;
      // Fall through
    case GC_MARK:
      // Trace all refs. Our job is done when the mark queue is empty and we
      // have completed an overflow scan of the heap.
      while ((overflowScan != heap_end) || (markQTl != markQHd))
      {
        process_queue();
        if (GC_TIMEOUT()) goto exit;
        if (overflowScan != heap_end)
        {
          mark_overflow();
        }
        if (GC_TIMEOUT()) goto exit;
      }
      memory_free = 0;
      freeList = FL_END;
      alloc_prev = &freeList;
      gcPhase = GC_SWEEP;
      // Fall through
    case GC_SWEEP:
      sweep();
      if (memRequired > 0 && newlyFreed > memRequired)
      {
        // We may have enough free space to meet current requests, so wake up
        // any threads that are waiting for memory.
        //monitor_notify_unchecked(&gcLock, true);
        system_notify(&gcLock, true);
        memRequired = 0;
        newlyFreed = 0;
      }
      if (GC_TIMEOUT()) goto exit;
      alloc_prev = &freeList;
      sweep_base = heap_end;
      sweep_free = null;
      memRequired = 0;
      gcPhase = GC_IDLE;
      // Notify any waiting threads that there may now be more memory
      system_notify(&gcLock, true);
  }
exit:
  return;
}

/**
 * Perform a full GC and make the thread wait for it to complete
 */
int garbage_collect()
{
  if (is_gc_retry())
  {
    // The current thread already owns the memory lock so this must
    // be a retry attempt
    exit_monitor(currentThread, &gcLock);
    if (gcPhase == GC_IDLE) return EXEC_CONTINUE;
  }
  // If not already collecting, start a collection
  if (gcPhase == GC_IDLE) gcPhase = GC_MARKROOTS;
  // restart the current instruction when we are woken up
  curPc = getPc();
  //enter_monitor(currentThread, &gcLock);
  //monitor_wait(&gcLock, 0);
  system_wait(&gcLock);
  return EXEC_RETRY;
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
  
            if(! (classIndex == JAVA_LANG_THREAD && i == 0))
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

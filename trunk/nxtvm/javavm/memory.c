
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

#include <string.h>

#ifdef VERIFY
static boolean memoryInitialized = false;
#endif

// Heap memory needs to be aligned to 4 bytes on ARM
// Value is in 2-byte units and must be a power of 2
#define MEMORY_ALIGNMENT 2

#define NULL_OFFSET 0xFFFF

// Size of stack frame in 2-byte words
#define NORM_SF_SIZE ((sizeof(StackFrame) + 1) / 2)

const byte typeSize[] = { 
  4, // 0 == T_REFERENCE
  SF_SIZE, // 1 == T_STACKFRAME
  0, // 2
  0, // 3
  1, // 4 == T_BOOLEAN
  2, // 5 == T_CHAR
  4, // 6 == T_FLOAT
  8, // 7 == T_DOUBLE
  1, // 8 == T_BYTE
  2, // 9 == T_SHORT
  4, // 10 == T_INT
  8, // 11 == T_LONG
  0, // 12
  0, // 13
  4, // 14 Used for multidimensional arrays
};

typedef struct MemoryRegion_S {
#if SEGMENTED_HEAP
  struct MemoryRegion_S *next;  /* pointer to next region */
#endif
  TWOBYTES *end;                /* pointer to end of region */
  TWOBYTES *allocBase;          /* pointer to last allocated or splitted block */
  TWOBYTES contents;            /* start of contents, even length */
} MemoryRegion;

/**
 * Beginning of heap.
 */
#if SEGMENTED_HEAP
static MemoryRegion *memory_regions; /* list of regions */
#else
static MemoryRegion *region; /* list of regions */
#endif

static TWOBYTES memory_size;    /* total number of words in heap */
static TWOBYTES memory_free;    /* total number of free words in heap */

extern void deallocate (TWOBYTES *ptr, TWOBYTES size);
extern TWOBYTES *allocate (TWOBYTES size);
Object *protectedRef[MAX_VM_REFS];

/**
 * @param numWords Number of 2-byte  words in the data part of the object
 */
#define initialize_state(PTR_,NWORDS_) zero_mem(((TWOBYTES *) (PTR_)), (NWORDS_) )
#define get_object_size(OBJ_)          (get_class_record(get_na_class_index(OBJ_))->classSize)

#if GARBAGE_COLLECTOR
static void set_reference( TWOBYTES* ptr);
static void clr_reference( TWOBYTES* ptr);
#else
static inline void set_reference( TWOBYTES* ptr) {}
static inline void clr_reference( TWOBYTES* ptr) {}
#endif

/**
 * Zeroes out memory.
 * @param ptr The starting address.
 * @param numWords Number of two-byte words to clear.
 * Now slightly optmized;
 */
void zero_mem( TWOBYTES *ptr, TWOBYTES numWords)
{
  TWOBYTES* end = ptr + numWords;

  while( ptr < end)
    *ptr++ = 0;
}

static inline void set_array (Object *obj, const byte elemType, const TWOBYTES length, const int baLength)
{
  #ifdef VERIFY
  assert (elemType <= (ELEM_TYPE_MASK >> ELEM_TYPE_SHIFT), MEMORY0); 
  assert (length <= (ARRAY_LENGTH_MASK >> ARRAY_LENGTH_SHIFT), MEMORY1);
  #endif
  obj->flags.all = IS_ALLOCATED_MASK | IS_ARRAY_MASK | ((TWOBYTES) elemType << ELEM_TYPE_SHIFT) | length;
  // If this is a big array set the real length
  if (baLength)
    ((BigArray *)obj)->length = baLength;
  #ifdef VERIFY
  assert (is_array(obj), MEMORY3);
  #endif
}

Object *memcheck_allocate (const TWOBYTES size)
{
  Object *ref;
  ref = (Object *) allocate (size);
  if (ref == JNULL)
  {
    #ifdef VERIFY
    assert (outOfMemoryError != null, MEMORY5);
    #endif
    throw_exception (outOfMemoryError);
    return JNULL;
  }
  
  ref->monitorCount = 0;
  ref->threadId = 0;
#if SAFE
  ref->flags.all = 0;
#endif
  return ref;
}

/**
 * Checks if the class needs to be initialized.
 * If so, the static initializer is dispatched.
 * Otherwise, an instance of the class is allocated.
 *
 * @param btAddr Back-track PC address, in case
 *               a static initializer needs to be invoked.
 * @return Object reference or <code>null</code> iff
 *         NullPointerException had to be thrown or
 *         static initializer had to be invoked.
 */
Object *new_object_checked (const byte classIndex, byte *btAddr)
{
  #if 0
  trace (-1, classIndex, 0);
  #endif

  if (!is_initialized_idx (classIndex))
    if (dispatch_static_initializer (get_class_record(classIndex), btAddr))
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
  TWOBYTES instanceSize;

#if DEBUG_MEMORY
  printf("New object for class %d\n", classIndex);
#endif
  instanceSize = get_class_record(classIndex)->classSize;
  
  ref = memcheck_allocate (instanceSize);

  if (ref == null)
  {
#if DEBUG_MEMORY
  printf("New object for class returning null\n");
#endif
    return JNULL;
  }

  // Initialize default values

  ref->flags.all = IS_ALLOCATED_MASK | classIndex;

  initialize_state (fields_start(ref), instanceSize - NORM_OBJ_SIZE);

  #if DEBUG_OBJECTS || DEBUG_MEMORY
  printf ("new_object_for_class: returning %d\n", (int) ref);
  #endif
  
  return ref;
}

/**
 * Return the size in words of an array of the given type
 */

#if 0
TWOBYTES comp_array_size (const TWOBYTES length, const byte elemType)
{
  return NORM_OBJ_SIZE + (((TWOBYTES) length * typeSize[elemType]) + 1) / 2;
}
#else
#define comp_array_size( length, elemType) \
  ((((TWOBYTES) (length) * typeSize[ elemType]) + 1) / 2)
#endif

/**
 * Allocates an array. 
 * To allow compact representation of small arrays but also allow the
 * use of larger objects we support two classes of array. The standard array
 * has a normal object header. Arrays larger then BIGARRAYLEN have the
 * std length field set to BIARRAYLEN and then have an extra length field which
 * is placed immediately at the end of the normal header. This length
 * field contains the real array size.
 */
Object *new_primitive_array (const byte primitiveType, STACKWORD length)
{
  Object *ref;
  TWOBYTES allocSize;
  TWOBYTES hdrSize;
  int baLength;

  allocSize = comp_array_size (length, primitiveType);
#if DEBUG_MEMORY
  printf("New array of type %d, length %ld\n", primitiveType, length);
#endif
  // If this is a large array use a bigger header
  if (length >= BIGARRAYLEN)
  {
    hdrSize = BA_OBJ_SIZE;
    baLength = length;
    length = BIGARRAYLEN;
  }
  else
  {
    baLength = 0;
    hdrSize = NORM_OBJ_SIZE;
  }
  ref = memcheck_allocate (allocSize + hdrSize);
#if DEBUG_MEMORY
  printf("Array ptr=%d\n", (int)ref);
#endif
  if (ref == null)
    return JNULL;
  set_array (ref, primitiveType, length, baLength);
  initialize_state (array_start(ref), allocSize);
  return ref;
}

#if 0
TWOBYTES get_array_size (Object *obj)
{
  return comp_array_size (get_array_length (obj),
                          get_element_type (obj));  
}
#else
#define get_array_size( obj) \
  ((is_std_array(obj) ? NORM_OBJ_SIZE : BA_OBJ_SIZE) + comp_array_size( get_array_length( obj), get_element_type( obj)))
#endif

void free_array (Object *objectRef)
{
  #ifdef VERIFY
  assert (is_array(objectRef), MEMORY7);
  #endif // VERIFY

#if !GARBAGE_COLLECTOR
  deallocate ((TWOBYTES *) objectRef, get_array_size (objectRef));
#endif
}

#if !FIXED_STACK_SIZE
Object *reallocate_array(Object *obj, STACKWORD newlen)
{
  byte elemType = get_element_type(obj);
  Object *newArray = new_primitive_array(elemType, newlen);
    
    // If can't allocate new array, give in!
    if (newArray != JNULL)
    {
      // Copy old array to new
      memcpy(array_start(newArray), array_start(obj), get_array_length(obj) * typeSize[elemType]);
    
      // Free old array
      free_array(obj);
    }
    
    return newArray;
}
#endif

/**
 * @param elemType Type of primitive element of multi-dimensional array.
 * @param totalDimensions Same as number of brackets in array class descriptor.
 * @param reqDimensions Number of requested dimensions for allocation.
 * @param numElemPtr Pointer to first dimension. Next dimension at numElemPtr+1.
 */
Object *new_multi_array (byte elemType, byte totalDimensions, 
                         byte reqDimensions, STACKWORD *numElemPtr)
{
  Object *ref;

  TWOBYTES ne;

  #ifdef VERIFY
  assert (totalDimensions >= 1, MEMORY6);
  assert (reqDimensions <= totalDimensions, MEMORY8);
  #endif

  #if 0
  printf ("new_multi_array (%d, %d, %d)\n", (int) elemType, (int) totalDimensions, (int) reqDimensions);
  #endif
  if (reqDimensions == 0)
    return JNULL;

  #if 0
  printf ("num elements: %d\n", (int) *numElemPtr);
  #endif
  if (totalDimensions >= MAX_VM_REFS)
  {
    throw_exception (outOfMemoryError);
    return JNULL;
  }

  if (totalDimensions == 1)
    return new_primitive_array (elemType, *numElemPtr);


  ref = new_primitive_array (T_REFERENCE, *numElemPtr);
  if (ref == JNULL)
    return JNULL;
  // Make sure we protect each level from the gc. Once we have returned
  // the ref it will be protected by the level above.
  protectedRef[totalDimensions] = ref;
  
  ne = *numElemPtr;
  while (ne--)
  {
    ref_array(ref)[ne] = ptr2word (new_multi_array (elemType, totalDimensions - 1, reqDimensions - 1, numElemPtr + 1));
  }
  protectedRef[totalDimensions] = JNULL;

  return ref;
}
/**
 * Native array copy method,
 * Copy the (partial) contents of one array to another
 * Placed here tp allow access to element size information.
 */
void arraycopy(Object *src, int srcOff, Object *dst, int dstOff, int len)
{
  int elemSize;
  // validate things
  if (src == null || dst == null)
  {
    throw_exception(nullPointerException);
    return;
  }
  if (!is_array(src) || !is_array(dst) || (get_element_type(src) != get_element_type(dst)))
  {
    throw_exception(illegalArgumentException);
    return;
  }
  if (srcOff < 0 || (srcOff + len > get_array_length(src)) ||
      dstOff < 0 || (dstOff + len > get_array_length(dst)))
  {
    throw_exception(arrayIndexOutOfBoundsException);
    return;
  }
  // and finally do the copy!
  elemSize = typeSize[get_element_type(src)];
  memcpy(get_array_element_ptr(dst, elemSize, dstOff), get_array_element_ptr(src, elemSize, srcOff), len*elemSize);
}


/**
 * Problem here is bigendian v. littleendian. Java has its
 * words stored bigendian, intel is littleendian.
 * Now slightly optmized;
 */

STACKWORD get_word( byte *ptr, int aSize)
{
  switch(aSize)
  {
  case 1:
    return (STACKWORD)(JINT)(JBYTE)ptr[0];
  case 2:
    return (STACKWORD)(JINT)(JSHORT)(((TWOBYTES)ptr[0]) << 8) | (ptr[1]);
  case 4:
    return (((STACKWORD)ptr[0]) << 24) | (((STACKWORD)ptr[1]) << 16) |
           (((STACKWORD)ptr[2]) << 8) | ((STACKWORD)ptr[3]);
  }
  return 0;
}

STACKWORD get_word_4( byte *ptr)
{
    return (((STACKWORD)ptr[0]) << 24) | (((STACKWORD)ptr[1]) << 16) |
           (((STACKWORD)ptr[2]) << 8) | ((STACKWORD)ptr[3]);
}

void store_word( byte *ptr, int aSize, STACKWORD aWord)
{
  switch(aSize)
  {
  case 1:
    ptr[0] = (byte)aWord;
    return;
  case 2:
    ptr[0] = (byte)(aWord >> 8);
    ptr[1] = (byte)(aWord); 
    return;
  case 4:
    ptr[0] = (byte)(aWord >> 24);
    ptr[1] = (byte)(aWord >> 16); 
    ptr[2] = (byte)(aWord >> 8);
    ptr[3] = (byte)(aWord); 
    return;
  }
}

#if DEBUG_RCX_MEMORY

void scan_memory (TWOBYTES *numNodes, TWOBYTES *biggest, TWOBYTES *freeMem)
{
}

#endif // DEBUG_RCX_MEMORY


void memory_init ()
{
  #ifdef VERIFY
  memoryInitialized = true;
  #endif

#if SEGMENTED_HEAP
  memory_regions = null;
#endif
  memory_size = 0;
  memory_free = 0;
}

/**
 * @param region Beginning of region.
 * @param size Size of region in bytes.
 */
void memory_add_region (byte *start, byte *end)
{
#if SEGMENTED_HEAP
  MemoryRegion *region;
#endif
  TWOBYTES contents_size;

  /* word align upwards */
  region = (MemoryRegion *) (((unsigned int)start+1) & ~1);

#if SEGMENTED_HEAP
  /* initialize region header */
  region->next = memory_regions;

  /* add to list */
  memory_regions = region;
#endif
  region->allocBase = &(region->contents);
  region->end = (TWOBYTES *) ((unsigned int)end & ~1); /* 16-bit align
 downwards */

#if GARBAGE_COLLECTOR
  {
    /* To be able to quickly identify a reference like stack slot
       we use a dedicated referance bitmap. With alignment of 4 bytes
       the map is 32 times smaller then the heap. Let's allocate
       the map by lowering the region->end pointer by the map size.
       The map must be zeroed. */
    TWOBYTES bitmap_size;
    contents_size = region->end - &(region->contents);
    bitmap_size = (contents_size / (((MEMORY_ALIGNMENT * 2) * 8) + 1) + 2) & ~1;
    region->end -= bitmap_size;
    zero_mem( region->end, bitmap_size);
  }
#endif

  /* create free block in region */
  contents_size = region->end - &(region->contents);
  ((Object*)&(region->contents))->flags.all = contents_size;

  /* memory accounting */
  memory_size += contents_size;
  memory_free += contents_size;

#if SEGMENTED_HEAP
  #if DEBUG_MEMORY
  printf ("Added memory region\n");
  printf ("  start:          %5d\n", (int)start);
  printf ("  end:            %5d\n", (int)end);
  printf ("  region:         %5d\n", (int)region);
  printf ("  region->next:   %5d\n", (int)region->next);
  printf ("  region->end:    %5d\n", (int)region->end);
  printf ("  memory_regions: %5d\n", (int)memory_regions);
  printf ("  contents_size:  %5d\n", (int)contents_size);
  #endif
#endif
}


/**
 * @param size Size of block including header in 2-byte words.
 */
static TWOBYTES *try_allocate (TWOBYTES size)
{
#if SEGMENTED_HEAP
  MemoryRegion *region;
#endif

  // Align memory to boundary appropriate for system  
  size = (size + (MEMORY_ALIGNMENT-1)) & ~(MEMORY_ALIGNMENT-1);

#if DEBUG_MEMORY
  printf("Allocate %d - free %d\n", size, memory_free-size);
#endif

#if SEGMENTED_HEAP
  for (region = memory_regions; region != null; region = region->next)
#endif
  {
    TWOBYTES *ptr = region->allocBase; // was &(region->contents);
    TWOBYTES *regionTop = region->end;

    while (ptr < regionTop) {
      TWOBYTES blockHeader = *ptr;

      if (blockHeader & IS_ALLOCATED_MASK) {
        /* jump over allocated block */
        TWOBYTES s = (blockHeader & IS_ARRAY_MASK) 
          ? get_array_size ((Object *) ptr)
          : get_object_size ((Object *) ptr);
          
        // Round up according to alignment
        s = (s + (MEMORY_ALIGNMENT-1)) & ~(MEMORY_ALIGNMENT-1);
        ptr += s;
      }
      else
      {
        if (size <= blockHeader) {
          /* allocate from this block */
#if GARBAGE_COLLECTOR == 0
#if COALESCE
          {
            TWOBYTES nextBlockHeader;

            /* NOTE: Theoretically there could be adjacent blocks
               that are too small, so we never arrive here and fail.
               However, in practice this should suffice as it keeps
               the big block at the beginning in one piece.
               Putting it here saves code space as we only have to
               search through the heap once, and deallocat remains
               simple.
            */
            
            while (true) {
              TWOBYTES *next_ptr = ptr + blockHeader;
              nextBlockHeader = *next_ptr;
              if (next_ptr >= regionTop ||
                  (nextBlockHeader & IS_ALLOCATED_MASK) != 0)
                break;
              blockHeader += nextBlockHeader;
              *ptr = blockHeader;
            }
          }
#endif
#else
          /* remember ptr as a current allocation base */
          region->allocBase = ptr;
#endif
          if (size < blockHeader) {
            /* cut into two blocks */
            blockHeader -= size; /* first block gets remaining free space */
            *ptr = blockHeader;
            ptr += blockHeader; /* second block gets allocated */
#if SAFE
            *ptr = size;
#endif
            /* NOTE: allocating from the top downwards avoids most
                     searching through already allocated blocks */
          }
          memory_free -= size;

          /* set the corresponding bit of the reference map */
          set_reference( ptr);

          return ptr;
        } else {
          /* continue searching */
          ptr += blockHeader;
        }
      }
    }
  }
  /* couldn't allocate block */
  /* restore allocation base to the begin of the region */
  region->allocBase = &(region->contents);
  return JNULL;
}

#if GARBAGE_COLLECTOR

TWOBYTES *allocate (TWOBYTES size)
{
  TWOBYTES *ptr = try_allocate( size);

  if( ptr == JNULL)
  {
    /* no memory left so run the garbage collector */
    garbage_collect();

    /* now try to allocate object again */
    ptr = try_allocate( size);
  }

  return ptr;
}

#else

TWOBYTES *allocate (TWOBYTES size)
{
  return try_allocate( size);
}

#endif

/**
 * @param size Must be exactly same size used in allocation.
 */
void deallocate (TWOBYTES *p, TWOBYTES size)
{
  /* clear the corresponding bit of the reference map */
  clr_reference( p);

  // Align memory to boundary appropriate for system  
  size = (size + (MEMORY_ALIGNMENT-1)) & ~(MEMORY_ALIGNMENT-1);
  
  #ifdef VERIFY
  assert (size <= (FREE_BLOCK_SIZE_MASK >> FREE_BLOCK_SIZE_SHIFT), MEMORY3);
  #endif

  memory_free += size;

#if DEBUG_MEMORY
  printf("Deallocate %d at %d - free %d\n", size, (int)p, memory_free);
#endif

  ((Object*)p)->flags.all = size;
}

int getHeapSize() {
  return ((int)memory_size) << 1;
}

int getHeapFree() {
  return ((int)memory_free) << 1;
}

int getRegionAddress()
{
#if SEGMENTED_HEAP
  return 0xf002;
#else
  return (int)region;
#endif
}

#if GARBAGE_COLLECTOR

/**
 * The garbage collector implementation starts here.
 * It is a classic mark and sweep implementation.
 * The garbage collection triggers automaticly only
 * after runing out of memory or by a java gc method
 * invocation. Thus, if a program is a "well behaving" one,
 * the garbage collector remains dormant. It consumes 
 * about 1000 bytes of flash and about 1800 bytes of ram.
 * Although the algorithm used is simple, it's pretty fast.
 * A typical single gc run should take no more then 10 ms
 * to complete. Besides, the presence of garbage collector
 * does not impair the speed of program execution.
 * There is no reference tracing on writes and no stack
 * or object size increase.
 * Potential problems: long linked list may cause
 * processor stack overflow due to recursion.
 */

/**
 * Just a forward declaration.
 */
static void mark_object( Object *obj);

/**
 * "Mark" flag manipulation functions.
 */
static inline void set_gc_marked( Object* obj)
{
  obj->flags.objects.mark = 1;
}

static inline void clr_gc_marked( Object* obj)
{
  obj->flags.objects.mark = 0;
}

static inline boolean is_gc_marked( Object* obj)
{
  return obj->flags.objects.mark != 0;
}

/**
 * Reference bitmap manipulation functions.
 * The bitmap is allocated at the end of the region.
 */
static void set_reference( TWOBYTES* ptr)
{
#if SEGMENTED_HEAP
  MemoryRegion *region;
  for (region = memory_regions; region != null; region = region->next)
  {
    TWOBYTES* regBottom = &(region->contents);
    TWOBYTES* regTop = region->end;

    if( ptr >= regBottom && ptr < regTop)
    {
#endif
      int refIndex = ((byte*) ptr - (byte*)&(region->contents)) / (MEMORY_ALIGNMENT * 2);

      ((byte*) region->end)[ refIndex >> 3] |= 1 << (refIndex & 7);

#if SEGMENTED_HEAP
      return;
    }
  }
#endif
}

static void clr_reference( TWOBYTES* ptr)
{
#if SEGMENTED_HEAP
  MemoryRegion *region;
  for (region = memory_regions; region != null; region = region->next)
  {
    TWOBYTES* regBottom = &(region->contents);
    TWOBYTES* regTop = region->end;

    if( ptr >= regBottom && ptr < regTop)
    {
#endif
      int refIndex = ((byte*) ptr - (byte*)&(region->contents)) / (MEMORY_ALIGNMENT * 2);

      ((byte*) region->end)[ refIndex >> 3] &= ~ (1 << (refIndex & 7));

#if SEGMENTED_HEAP
      return;
    }
  }
#endif
}

static boolean is_reference( TWOBYTES* ptr)
{
#if SEGMENTED_HEAP
  MemoryRegion *region;
  for (region = memory_regions; region != null; region = region->next)
#endif
  {
    /* The reference must belong to a memory region. */
    TWOBYTES* regBottom = &(region->contents);
    TWOBYTES* regTop = region->end;

    if( ptr >= regBottom && ptr < regTop)
    {
      /* It must be properly aligned */
      if( ((int)ptr & ((MEMORY_ALIGNMENT * 2) - 1)) == 0)
      {
        /* Now we can safely check the corresponding bit in the reference bitmap. */
        int refIndex = ((byte*) ptr - (byte*)&(region->contents)) / (MEMORY_ALIGNMENT * 2);

        return (((byte*) region->end)[ refIndex >> 3] & (1 << (refIndex & 7))) != 0;
      }

      return false;
    }
  }

  return false;
}

/**
 * Scan static area of all classes. For every non-null reference field
 * call mark_object function.
 */
static void mark_static_objects( void)
{
  MasterRecord* mrec = get_master_record();
  STATICFIELD* staticFieldBase = (STATICFIELD*) get_static_fields_base();
  byte* staticStateBase = get_static_state_base();
  byte* staticState = staticStateBase;
  byte* staticEnd = staticStateBase + mrec->staticStateLength * 2 - 1;
  int idx = 0;

  while( staticState < staticEnd)
  {
    STATICFIELD fieldRecord = staticFieldBase[ idx ++];
    byte fieldType = (fieldRecord >> 12) & 0x0F;
    byte fieldSize = typeSize[ fieldType];

    if( fieldType == T_REFERENCE)
    {
      Object* obj = (Object*) get_word_4( staticState);
      if( obj != NULL)
        mark_object( obj);
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
  StackFrame *currentFrame = current_stackframe();
  if (currentFrame != null) update_stack_frame(currentFrame);
  // If needed make sure we protect the VM temporary references
  for(i=0; i < MAX_VM_REFS; i++)
    if (protectedRef[i] != JNULL) mark_object(protectedRef[i]);

  for( i = 0; i < MAX_PRIORITY; i ++)
  {
    Thread* th0 = threadQ[ i];
    Thread* th = th0;

    while( th != NULL)
    {
      byte arraySize;

      mark_object( (Object*) th);
      mark_object( (Object*) th->stackArray);
      mark_object( (Object*) th->stackFrameArray);

      if( th->waitingOn != 0)
        mark_object( (Object*) th->waitingOn);

      arraySize = th->stackFrameArraySize;
      if( arraySize != 0)
      {
        Object* sfObj = word2ptr( th->stackFrameArray);
        StackFrame* stackFrame = ((StackFrame*) array_start( sfObj)) + (arraySize - 1);
        Object* saObj = word2ptr( th->stackArray);
        STACKWORD* stackBottom = (STACKWORD*) jint_array( saObj);
        STACKWORD* stackTop = stackFrame->stackTop;
        STACKWORD* sp;
    
        for( sp = stackBottom; sp <= stackTop; sp ++)
        {
          TWOBYTES* ptr = word2ptr( *sp);

          if( is_reference( ptr))
          {
            /* Now we know that ptr points to a valid allocated object.
               It does not mean, that this slot contains a reference variable.
               It may be an integer or float variable, which has exactly the
               same value as a reference of one of the allocated objects.
               But it is no harm. We can safely "mark" it, In such a case
               we may just leave an unreachable object uncollected. */

            mark_object( (Object*) ptr);
          }
        }
      }

      th = word2ptr( th->nextThread);
      if( th == th0)
        break;
    }
  }
}

/**
 * Scan member fields of class instance, and for every
 * non-null reference field call the mark_object function.
 */
static void mark_reference_fields( Object* obj)
{
  byte classIndex = get_na_class_index( obj);
  ClassRecord* classRecord;
  byte classIndexTable[ 16];
  int classIndexTableIndex = 0;
  byte* statePtr;

  /* first we need to prepare a reversed order of inheritance */

  for(;;)
  {
    if( classIndex == JAVA_LANG_OBJECT)
      break;

    classRecord = get_class_record( classIndex);

    if( classRecord->numInstanceFields)
      classIndexTable[ classIndexTableIndex ++] = classIndex;

    classIndex = classRecord->parentClass;
  } 

  /* now we can scan the member fields */

  statePtr = (byte*) (((TWOBYTES *) (obj)) + NORM_OBJ_SIZE);

  while( -- classIndexTableIndex >= 0)
  {
    classIndex = classIndexTable[ classIndexTableIndex];
    classRecord = get_class_record (classIndex);

    if( classRecord->numInstanceFields)
    {
      int i;

      for( i = 0; i < classRecord->numInstanceFields; i++)
      {
        byte fieldType = get_field_type( classRecord, i);
        byte fieldSize = typeSize[ fieldType];

        if( fieldType == T_REFERENCE)
        {
          /* omit nextThread field of Thread class */

          if( ! (classIndex == JAVA_LANG_THREAD && i == 0))
          {
            Object* robj = (Object*) get_word_4( statePtr);
            if( robj != NULL)
              mark_object( robj);
          }
        }

        statePtr += fieldSize;
      }
    }
  }
}

/**
 * A function which performs a "mark" operation for an object.
 * If it is an array of references recursively call mark_object
 * for every non-null array element.
 * Otherwise "mark" every non-null reference field of that object.
 */
static void mark_object( Object *obj)
{
  if( is_gc_marked( obj))
    return;

  set_gc_marked( obj);

  if( is_array( obj))
  {
    if( get_element_type( obj) == T_REFERENCE)
    {
      REFERENCE* refarr = ref_array( obj);
      REFERENCE* refarrend = refarr + get_array_length( obj);
      
      while( refarr < refarrend)
      {
        Object* obj = (Object*) (*refarr ++);
        if( obj != NULL)
          mark_object( obj);
      }
    }
  }
  else
  if( get_na_class_index( obj) == JAVA_LANG_STRING)
  {
    String* str = (String*)obj;
    Object* chars = word2obj(get_word_4((byte*)(&(str->characters))));

    if( chars != NULL)
      set_gc_marked( chars);
  }
  else
    mark_reference_fields( obj);
}

/**
 * A function which performs a "sweep" operation for an object.
 * If it's "marked" clear the mark. Otherwise delete the object.
 * For safety omit objects with active monitor.
 */
/*
static void sweep_object( Object *obj, TWOBYTES size)
{
  if( is_gc_marked( obj))
    clr_gc_marked( obj);
  else
  if( get_monitor_count( obj) == 0)
    deallocate( (TWOBYTES*) obj, size);
}
*/

/**
 * Scan heap objects and for every allocated object call
 * the sweep_object function.
 */
void sweep_heap_objects( void)
{

#if SEGMENTED_HEAP
  MemoryRegion *region;
  for (region = memory_regions; region != null; region = region->next)
#endif
  {
    int mf = memory_free;
    TWOBYTES* ptr = &(region->contents);
    TWOBYTES* fptr = null;
    TWOBYTES* regionTop = region->end;
    while( ptr < regionTop)
    {
      unsigned int blockHeader = *ptr;
      unsigned int size;

      if( blockHeader & IS_ALLOCATED_MASK)
      {
        Object* obj = (Object*) ptr;

        /* jump over allocated block */
        size = (blockHeader & IS_ARRAY_MASK) ? get_array_size( obj)
                                             : get_object_size( obj);
          
        // Round up according to alignment
        size = (size + (MEMORY_ALIGNMENT-1)) & ~(MEMORY_ALIGNMENT-1);

        if( is_gc_marked( obj))
          clr_gc_marked( obj);
        else
        if( get_monitor_count( obj) == 0)
        {
          // Set object free
          mf += size;

          obj->flags.all = size;
          blockHeader = size;
        }
      }
      else
      {
        /* continue searching */
        size = blockHeader;
      }

      if( !(blockHeader & IS_ALLOCATED_MASK))
      {
        // Got a free block can we merge?
        if (fptr != null)
          *fptr += size;
        else
          fptr = ptr;
      }
      else
          fptr = null;

      ptr += size;
    }

    memory_free = mf;
  }
}

/**
 * "Mark" preallocated instances of exception objects
 * to avoid garbage-collecting them.
 */
static void mark_exception_objects( void)
{
  mark_object( outOfMemoryError);
  mark_object( noSuchMethodError);
  mark_object( stackOverflowError);
  mark_object( nullPointerException);
  mark_object( classCastException);
  mark_object( arithmeticException);
  mark_object( arrayIndexOutOfBoundsException);
  mark_object( illegalArgumentException);
  mark_object( interruptedException);
  mark_object( illegalStateException);
  mark_object( illegalMonitorStateException);
  mark_object( error);
}

/**
 * Main garbage collecting function.
 * Perform "mark" operation for internal objects,
 * class static areas and thread local areas.
 * After that perform a "sweep" operation for
 * every "unmarked" heap object.
 */
void garbage_collect( void)
{
  mark_exception_objects();
  mark_static_objects();
  mark_local_objects();
  sweep_heap_objects();
}

#else

void garbage_collect( void)
{
}

#endif // GARBAGE_COLLECTOR


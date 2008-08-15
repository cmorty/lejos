
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
  TWOBYTES *end;                /* pointer to end of region */
  TWOBYTES *alloc_base;         /* pointer to last allocated or splitted block */
  TWOBYTES *sweep_base;         /* current position of sweeping */
  TWOBYTES contents;            /* start of contents, even length */
} MemoryRegion;

/**
 * Beginning of heap.
 */
static MemoryRegion *region; /* list of regions */

static MemoryRegion *sweep_region; /* current region being swept or NULL if not sweeping */

static unsigned int memory_size;    /* total number of words in heap */
static unsigned int memory_free;    /* total number of free words in heap */

TWOBYTES failed_alloc_size;

VarStat gc_mark_vs;
VarStat gc_sweep_vs;
VarStat gc_total_vs;
VarStat mem_alloctm_vs;
VarStat mem_freeblk_vs;
VarStat mem_usedblk_vs;
VarStat gc_overflow_vs;

extern void deallocate (TWOBYTES *ptr, TWOBYTES size);
extern TWOBYTES *allocate (TWOBYTES size);

Object *protectedRef[MAX_VM_REFS];

static int overflowCnt = 0;
#define MAX_CALL_DEPTH 16
static int callLimit = MAX_CALL_DEPTH;
static TWOBYTES *overflowScanStart = null;
#define MARK_STACK_SZ 32
static Object *markStack[MARK_STACK_SZ];
static int markStackPtr = 0;



/**
 * @param numWords Number of 2-byte  words in the data part of the object
 */
#define initialize_state(PTR_,NWORDS_) zero_mem(((TWOBYTES *) (PTR_)), (NWORDS_) )
#define get_object_size(OBJ_)          ((get_class_record(get_na_class_index(OBJ_))->classSize+1)/2)

#if GARBAGE_COLLECTOR
static void set_reference( TWOBYTES* ptr);
static void clr_reference( TWOBYTES* ptr);
static void collect_mem_stat();
#else
static inline void set_reference( TWOBYTES* ptr) {}
static inline void clr_reference( TWOBYTES* ptr) {}
#endif

#if USE_VARSTAT

void varstat_init( VarStat* vs)
{
  vs->last = 0;
  vs->min = 0x7FFFFFFF;
  vs->max = 0;
  vs->sum = 0;
  vs->count = 0;
}

void varstat_adjust( VarStat* vs, int v)
{
  vs->last = v;
  if( vs->max < v)
    vs->max = v;
  if( vs->min > v)
    vs->min = v;
  vs->sum += v;
  vs->count ++;
}

int varstat_get( VarStat* vs, int id)
{
  int val = 0;

  switch( id)
  {
  case 0:  val = vs->last;  break;
  case 1:  val = vs->min;   break;
  case 2:  val = vs->max;   break;
  case 3:  val = vs->sum;   break;
  case 4:  val = vs->count; break;
  }

  return val;
}

#define varstat_gettime()       get_sys_time()

#else

#define varstat_init( vs)
#define varstat_adjust( vs, v)
#define varstat_get( vs, id)    0
#define varstat_gettime()       0

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
  instanceSize = (get_class_record(classIndex)->classSize+1)/2;
  
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
  return elemType == T_CHAR ? length : 
            (((unsigned int) length * typeSize[elemType]) + 1) / 2;
}
#else
#define comp_array_size( length, elemType) \
(                                          \
  elemType == T_CHAR ? length :            \
      ((((unsigned int) (length) * typeSize[ elemType]) + 1) / 2))
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
  unsigned int allocSize;
  unsigned int hdrSize;
  unsigned int baLength;

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
    return new_primitive_array ((elemType == T_OBJECT ? T_REFERENCE : elemType), *numElemPtr);


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
 * Note the issue is not just endian. We also need to deal with the fact that
 * java items may not be aligned correctly.
 * Now slightly optmized;
 */

STACKWORD get_word_swp( byte *ptr, int aSize)
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


STACKWORD get_word_4_swp( byte *ptr)
{
    return (((STACKWORD)ptr[0]) << 24) | (((STACKWORD)ptr[1]) << 16) |
           (((STACKWORD)ptr[2]) << 8) | ((STACKWORD)ptr[3]);
}

void store_word_swp( byte *ptr, int aSize, STACKWORD aWord)
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

/**
 * Following are non-swapping versions of the above.
 */
STACKWORD get_word_ns( byte *ptr, int aSize)
{
  switch(aSize)
  {
  case 1:
    return (STACKWORD)(JINT)(JBYTE)ptr[0];
  case 2:
    return (STACKWORD)(JINT)(JSHORT)(((TWOBYTES)ptr[1]) << 8) | (ptr[0]);
  case 4:
    return (((STACKWORD)ptr[3]) << 24) | (((STACKWORD)ptr[2]) << 16) |
           (((STACKWORD)ptr[1]) << 8) | ((STACKWORD)ptr[0]);
  }
  return 0;
}

STACKWORD get_word_4_ns( byte *ptr)
{
    return (((STACKWORD)ptr[3]) << 24) | (((STACKWORD)ptr[2]) << 16) |
           (((STACKWORD)ptr[1]) << 8) | ((STACKWORD)ptr[0]);
}

void store_word_ns( byte *ptr, int aSize, STACKWORD aWord)
{
  switch(aSize)
  {
  case 1:
    ptr[0] = (byte)aWord;
    return;
  case 2:
    ptr[0] = (byte)(aWord); 
    ptr[1] = (byte)(aWord >> 8);
    return;
  case 4:
    ptr[0] = (byte)(aWord); 
    ptr[1] = (byte)(aWord >> 8);
    ptr[2] = (byte)(aWord >> 16); 
    ptr[3] = (byte)(aWord >> 24);
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

  sweep_region = NULL;

  memory_size = 0;
  memory_free = 0;

  varstat_init( &gc_mark_vs);
  varstat_init( &gc_sweep_vs);
  varstat_init( &gc_total_vs);
  varstat_init( &mem_alloctm_vs);
  varstat_init( &mem_freeblk_vs);
  varstat_init( &mem_usedblk_vs);
  varstat_init( &gc_overflow_vs);
}

/**
 * @param region Beginning of region.
 * @param size Size of region in bytes.
 */
void memory_add_region (byte *start, byte *end)
{
  TWOBYTES contents_size;

  /* align upwards */
  region = (MemoryRegion *) (((unsigned int)start+ MEMORY_ALIGNMENT*2 - 1) & ~(MEMORY_ALIGNMENT*2 - 1));

  region->alloc_base = &(region->contents);
  region->sweep_base = NULL;
  /* align downwards */
  region->end = (TWOBYTES *) ((unsigned int)end & ~(MEMORY_ALIGNMENT*2 - 1)); 

#if GARBAGE_COLLECTOR
  {
    /* To be able to quickly identify a reference like stack slot
       we use a dedicated referance bitmap. With alignment of 4 bytes
       the map is 32 times smaller then the heap. Let's allocate
       the map by lowering the region->end pointer by the map size.
       The map must be zeroed. */
    TWOBYTES bitmap_size;
    contents_size = region->end - &(region->contents);
    /* Calculate the required bitmap size (in words). Note that we devide here
       by 33 rather then 32 to take into account the reduction in size of the
       heap due to the bitmap size!  */
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
}

void init_sweep( void)
{
  region->alloc_base = region->sweep_base = &region->contents;
  sweep_region = region;
}

/**
 * @param size Size of block including header in 2-byte words.
 */

static TWOBYTES *try_allocate( unsigned int size)
{

  if( memory_free >= size)
  {

    TWOBYTES *ptr = region->alloc_base;
    TWOBYTES *regionTop = region->end;

#if DEBUG_MEMORY
    printf("Allocate %d - free %d\n", size, memory_free-size);
#endif

    while (ptr < regionTop)
    {
      unsigned int blockHeader = *ptr;

      if (blockHeader & IS_ALLOCATED_MASK)
      {
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
        if (size <= blockHeader)
        {
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
          
            while (true)
            {
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
          region->alloc_base = ptr;
#endif
          if (size < blockHeader)
          {
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
        }
        else
        {
          /* continue searching */
          ptr += blockHeader;
        }
      }
    }

    /* restore allocation base to the begin of the region */
    region->alloc_base = &(region->contents);
  }

  /* couldn't allocate block */
  return JNULL;
}

#if GARBAGE_COLLECTOR

TWOBYTES *allocate (TWOBYTES size)
{
  long t0, t1;
  TWOBYTES *ptr;
  unsigned int asize;

  t0 = varstat_gettime();

  // Align memory to boundary appropriate for system  
  asize = (size + (MEMORY_ALIGNMENT-1)) & ~(MEMORY_ALIGNMENT-1);

  if( sweep_region != NULL)
    garbage_collect( asize);

  ptr = try_allocate( asize);

  if( ptr == JNULL)
  {
    /* no memory left so run the garbage collector */
    garbage_collect( asize);

    /* now try to allocate object again */
    ptr = try_allocate( asize);

    if( ptr == JNULL)
    {
      failed_alloc_size = asize;
      collect_mem_stat();
    }
  }

  t1 = varstat_gettime();

  varstat_adjust( &mem_alloctm_vs, t1 - t0);

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
  return (int)region;
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
  int refIndex = ((byte*) ptr - (byte*)&(region->contents)) / (MEMORY_ALIGNMENT * 2);

  ((byte*) region->end)[ refIndex >> 3] |= 1 << (refIndex & 7);

}

static void clr_reference( TWOBYTES* ptr)
{
  int refIndex = ((byte*) ptr - (byte*)&(region->contents)) / (MEMORY_ALIGNMENT * 2);

  ((byte*) region->end)[ refIndex >> 3] &= ~ (1 << (refIndex & 7));

}

static boolean is_reference( TWOBYTES* ptr)
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
  int cnt = mrec->numStaticFields;
  int idx = 0;

  while(cnt-- > 0)
  {
    STATICFIELD fieldRecord = staticFieldBase[ idx ++];
    byte fieldType = (fieldRecord >> 12) & 0x0F;
    byte fieldSize = typeSize[ fieldType];

    if( fieldType == T_REFERENCE)
    {
      Object* obj = (Object*) get_word_4_ns( staticState);
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
  if (threads) mark_object((Object *)threads);
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

#ifdef OLD
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
            Object* robj = (Object*) get_word_4_ns( statePtr);
            if( robj != NULL)
              mark_object( robj);
          }
        }

        statePtr += fieldSize;
      }
    }
  }
}
#else

/**
 * Scan member fields of class instance, and for every
 * non-null reference field call the mark_object function.
 */
static void mark_reference_fields( Object* obj)
{
  byte classIndex = get_na_class_index( obj);
  ClassRecord* classRecord;
  byte* statePtr;

  classRecord = get_class_record( classIndex);
  statePtr = (byte*) obj;
  /* Scan the object in reverse so we start at the end of the object */
  statePtr += classRecord->classSize;
  /* now we can scan the member fields */
  while (classIndex != JAVA_LANG_OBJECT)
  {
    if( classRecord->numInstanceFields)
    {
      int i;
      for( i = classRecord->numInstanceFields-1; i >= 0; i--)
      {
        byte fieldType = get_field_type( classRecord, i);
        byte fieldSize = typeSize[ fieldType];
        statePtr -= fieldSize;

        if( fieldType == T_REFERENCE)
        {
          /* omit nextThread field of Thread class */

          if( ! (classIndex == JAVA_LANG_THREAD && i == 0))
          {
            Object* robj = (Object*) get_word_4_ns( statePtr);
            if( robj != NULL)
              mark_object( robj);
          }
        }

      }
    }
    classIndex = classRecord->parentClass;
    classRecord = get_class_record(classIndex);
  }
}
#endif

/**
 * A function which performs a "mark" operation for an object.
 * If it is an array of references recursively call mark_object
 * for every non-null array element.
 * Otherwise "mark" every non-null reference field of that object.
 * To handle deep structures we use a combination of recursive marking
 * and an explicit mark stack. The recursive approach works well (especially
 * for arrays and objects with a large number of refs), but requires more
 * memory per call. So we use recusrion to a fixed depth and then switch
 * to an explicit stack. If this inturn overflows, we simply give up and
 * recover later.
 */
static void mark_object( Object *obj)
{
  TWOBYTES hdr;
  if( is_gc_marked( obj))
    return;

  set_gc_marked( obj);
  // Deal with non recusrive data types we don't need to examine these
  // any deeper...
  hdr = obj->flags.all; 
  if (hdr & IS_ARRAY_MASK)
  {
    if ((hdr & ELEM_TYPE_MASK) != T_REFERENCE) return;
  }
  else if ((hdr & CLASS_MASK) == JAVA_LANG_STRING)
  {
    String* str = (String*)obj;
    Object* chars = word2obj(get_word_4_ns((byte*)(&(str->characters))));
  
    if( chars != NULL)
      set_gc_marked( chars);
    return;
  }
  else if (has_norefs(get_class_record((hdr & CLASS_MASK))))
    return;

  // Check to see if we have reached our recursive limit
  if (callLimit == 0)
  {
    // Try and push the entry on the mark stack.
    if (markStackPtr >= MARK_STACK_SZ)
    {
      // No space give up!
      overflowCnt++;
      if ((TWOBYTES *)obj < overflowScanStart)
        overflowScanStart = (TWOBYTES *)obj;
    }
    else
      markStack[markStackPtr++] = obj;

    return;
  }
  callLimit--;
  for(;;)
  {
    if(hdr & IS_ARRAY_MASK)
    {
      // Must be an array of refs.
#ifdef VERIFY
      assert(get_element_type(obj) == T_REFERENCE, MEMORY3);
#endif
      REFERENCE* refarr = ref_array( obj);
      REFERENCE* refarrend = refarr + get_array_length( obj);
      
      while( refarr < refarrend)
      {
        Object* obj = (Object*) (*refarr ++);
        if( obj != NULL)
          mark_object( obj);
      }
    }
    else
      mark_reference_fields( obj);
    // By not processing stacked nodes until we have unwound the recursive
    // calls we maximise the number of levels to process things. Normally
    // this seems to be a win.
    if (callLimit < (MAX_CALL_DEPTH-1) || markStackPtr <= 0) break;
    //if (markStackPtr <= 0) break;
    // Process a stacked node.
    obj = markStack[--markStackPtr];
    hdr = obj->flags.all; 
  }
  callLimit++;
}

/**
 * Scan heap objects and for every allocated object call
 * the sweep_object function.
 * Sweep at least "reqsize" in contiguous block and preferably "optsize" of
 * memory area.
 */
static void sweep_heap_objects( unsigned int reqsize, unsigned int optsize)
{
  unsigned int contsize = 0;
  int mf = memory_free;
  TWOBYTES* ptr = region->sweep_base;
  TWOBYTES* fptr = null;
  TWOBYTES* regionTop = region->end;
  TWOBYTES* limit = region->sweep_base + optsize;

  if( limit > regionTop)
    limit = regionTop;

  while( (ptr < limit) || ((ptr < regionTop) && (contsize < reqsize)))
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
          clr_reference(ptr);
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
      {
        unsigned int fsize;

        fsize = *fptr;
        fsize += size;
        *fptr = fsize;
        if( contsize < fsize)
          contsize = fsize;
      }
      else
      {
        fptr = ptr;
        if( contsize < size)
          contsize = size;
      }
    }
    else
      fptr = null;

    ptr += size;
  }

  memory_free = mf;

  if( ptr < regionTop)
  {
    region->sweep_base = ptr;
  }
  else
  {
    region->sweep_base = NULL;
  }

  sweep_region = region->sweep_base != NULL ? region : NULL;
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

static
void mark_overflow()
{
  TWOBYTES* ptr = &(region->contents);
  TWOBYTES* regionTop = region->end;
  ptr = overflowScanStart;
  overflowScanStart = region->end;
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
      if (is_gc_marked(obj))
      {
        clr_gc_marked(obj);
        mark_object(obj);
      }
    }
    else
    {
      /* continue searching */
      size = blockHeader;
    }

    ptr += size;
  }
}

/**
 * Main garbage collecting function.
 * Perform "mark" operation for internal objects,
 * class static areas and thread local areas.
 * After that perform a "sweep" operation for
 * every "unmarked" heap object.
 */

static void mark_objects( void)
{
  int cnt = 0;
  overflowCnt = 0;
  overflowScanStart = region->end;
  mark_exception_objects();
  mark_static_objects();
  mark_local_objects();
  // If the mark stack overflowed we treat each marked object in the
  // heap as a root, and start marking from there. 
  while (overflowCnt > 0)
  {
    overflowCnt = 0;
    mark_overflow();
    cnt++;
  }
  varstat_adjust(&gc_overflow_vs, cnt);
}

void garbage_collect( int reqsize)
{
  int t0, t1, t2;
  int optsize;
  t0 = varstat_gettime();

  if( sweep_region == NULL)
  {
    mark_objects();

    init_sweep();

    optsize = 128;

    t1 = varstat_gettime();

    varstat_adjust( &gc_mark_vs, t1 - t0);
  }
  else
  {
    optsize = 1024;  // 2 KB
    t1 = t0;
  }

  sweep_heap_objects( reqsize, optsize);

  t2 = varstat_gettime();

  varstat_adjust( &gc_sweep_vs, t2 - t1);
  varstat_adjust( &gc_total_vs, t2 - t0);
}

static void collect_mem_stat( void)
{
  varstat_init( &mem_freeblk_vs);
  varstat_init( &mem_usedblk_vs);

  TWOBYTES* ptr = &(region->contents);
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

      varstat_adjust( &mem_usedblk_vs, size << 1);
    }
    else
    {
      /* continue searching */
      size = blockHeader;

      varstat_adjust( &mem_freeblk_vs, size << 1);
    }

    ptr += size;
  }
}

#else

void garbage_collect( int size)
{
}

#endif // GARBAGE_COLLECTOR

int sys_diagn( int code, int param)
{
#if GARBAGE_COLLECTOR && USE_VARSTAT
  switch( code)
  {
  case 1:
    return varstat_get( &gc_mark_vs, param);
  case 2:
    return varstat_get( &gc_sweep_vs, param);
  case 3:
    collect_mem_stat();
    break;
  case 4:
    return varstat_get( &mem_freeblk_vs, param);
  case 5:
    return varstat_get( &mem_usedblk_vs, param);
  case 6:
    return varstat_get( &gc_total_vs, param);
  case 7:
    return varstat_get( &mem_alloctm_vs, param);
  case 8:
    return varstat_get( &gc_overflow_vs, param);
  }
#endif

  return 0;
}


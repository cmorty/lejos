
#include "types.h"
#include "classes.h"

#ifndef _MEMORY_H
#define _MEMORY_H

#define DEBUG_RCX_MEMORY 0

extern const byte typeSize[];
extern Object *protectedRef[];
extern void memory_init ();
extern void memory_add_region (byte *region, byte *end);

extern void free_array (Object *objectRef);
extern void deallocate (TWOBYTES *ptr, TWOBYTES size);
extern Object *new_object_checked (const byte classIndex, byte *btAddr);
extern Object *new_object_for_class (const byte classIndex);
extern Object *new_primitive_array (const byte primitiveType, STACKWORD length);
extern Object *reallocate_array(Object *obj, STACKWORD newlen);
extern Object *new_multi_array (byte elemType, byte totalDimensions, byte reqDimensions, STACKWORD *numElemPtr);
extern void arraycopy(Object *src, int srcOff, Object *dst, int dstOff, int len);
extern void store_word (byte *ptr, int aSize, STACKWORD aWord);
extern STACKWORD get_word(byte *ptr, int aSize);
extern STACKWORD get_word_4(byte *ptr);
extern void zero_mem (TWOBYTES *ptr, TWOBYTES numWords);
extern int getHeapSize();
extern int getHeapFree();
extern int getRegionAddress();
extern void garbage_collect( int size);
extern int sys_diagn( int code, int param);

#if DEBUG_RCX_MEMORY
extern void scan_memory (TWOBYTES *numNodes, TWOBYTES *biggest, TWOBYTES *freeMem);
#endif // DEBUG_RCX_MEMORY

#define HEADER_SIZE (sizeof(Object))
// Size of object header in 2-byte words
#define NORM_OBJ_SIZE ((HEADER_SIZE + 1) / 2)
// Size of BigArry header in 2 byte words
#define BA_OBJ_SIZE ((sizeof(BigArray) + 1) / 2)

#define fields_start(OBJ_)  ((byte *) (OBJ_) + HEADER_SIZE)
//#define array_start(OBJ_)   ((byte *) (OBJ_) + HEADER_SIZE)
// Generic access to array data given an array object
#define array_start(OBJ_)   (is_std_array((Object *)(OBJ_)) ? (byte *) (OBJ_) + HEADER_SIZE : (byte *)(OBJ_) + sizeof(BigArray))
// Typed access to the data
#define jbyte_array(OBJ_)   ((JBYTE *) array_start(OBJ_))
#define word_array(OBJ_)    ((STACKWORD *) array_start(OBJ_))
#define ref_array(OBJ_)     ((REFERENCE *) array_start(OBJ_))
#define jint_array(OBJ_)    ((JINT *) array_start(OBJ_))
#define jshort_array(OBJ_)  ((JSHORT *) array_start(OBJ_))
#define jchar_array(OBJ_)   ((JCHAR *) array_start(OBJ_))
#define jlong_array(OBJ_)   ((JLONG *) array_start(OBJ_))
#define jfloat_array(OBJ_)  ((JFLOAT *) array_start(OBJ_))
// Following provide access to the array data given a pointer to the start
#define jbyte_array_ptr(PTR_)   ((JBYTE *) PTR_)
#define word_array_ptr(PTR_)    ((STACKWORD *) PTR_)
#define ref_array_ptr(PTR_)     ((REFERENCE *) PTR_)
#define jint_array_ptr(PTR_)    ((JINT *) PTR_)
#define jshort_array_ptr(PTR_)  ((JSHORT *) PTR_)
#define jchar_array_ptr(PTR_)   ((JCHAR *) PTR_)
#define jlong_array_ptr(PTR_)   ((JLONG *) PTR_)
#define jfloat_array_ptr(PTR_)  ((JFLOAT *) PTR_)

#define get_array_element_ptr(ARR_,ESIZE_,IDX_) (array_start((ARR_)) + (IDX_) * (ESIZE_))

extern TWOBYTES failed_alloc_size;

typedef struct
{
  int last;
  int min;
  int max;
  int sum;
  int count;
} VarStat;

extern VarStat gc_mark_vs;
extern VarStat gc_sweep_vs;
extern VarStat gc_total_vs;
extern VarStat mem_alloctm_vs;
extern VarStat mem_freeblk_vs;
extern VarStat mem_usedblk_vs;


#endif // _MEMORY_H




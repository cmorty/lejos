
#include "types.h"
#include "classes.h"

#ifndef _MEMORY_H
#define _MEMORY_H
#include "language.h"

extern void memory_init ();
extern void memory_add_region (byte *region, byte *end);

extern Object *new_object_checked (const byte classIndex, byte *btAddr);
extern Object *new_object_for_class (const byte classIndex);
extern Object *new_primitive_array (const byte typ, JINT length);
extern Object *new_single_array (const byte cls, JINT length);
extern int init_stacks(Thread *thread);
extern void free_stacks(Thread *thread);
extern int expand_call_stack(Thread *thread);
extern int expand_value_stack(Thread *thread, int minSize);
extern Object *new_multi_array (const byte cls, byte reqDimensions, STACKWORD *numElemPtr);
extern Object *new_string(ConstantRecord *constantRecord, byte *btAddr);
extern int arraycopy(Object *src, int srcOff, Object *dst, int dstOff, int len);
extern byte *system_allocate(int sz);
extern void system_free(byte *mem);
extern STACKWORD get_word_4_swp(byte *ptr);
extern void store_word_ns (byte *ptr, int typ, STACKWORD aWord);
extern STACKWORD get_word_ns(byte *ptr, int typ);
extern STACKWORD get_word_4_ns(byte *ptr);
extern void zero_mem (TWOBYTES *ptr, TWOBYTES numWords);
extern int getHeapSize();
extern int getHeapFree();
extern int getRegionAddress();
extern int garbage_collect();
extern void wait_garbage_collect();
extern Object *clone(Object *old);

#define GC_IDLE 0
#define GC_MARKROOTS 1
#define GC_MARK 2
#define GC_SWEEP 3
#define GC_COMPACT 4
#define GC_EXPAND 5
#define GC_COMPLETE 6
extern int gcPhase;
extern void gc_update_array(Object *obj);
extern void gc_update_object(Object *obj);
extern void gc_run_collector(void);
extern Object gcLock;

#define update_array(obj) {if(gcPhase == GC_MARK && ((*(TWOBYTES *)(obj)) & GC_MASK) != GC_MASK) gc_update_array((obj));}
#define update_object(obj) {if(gcPhase == GC_MARK && ((*(TWOBYTES *)(obj)) & GC_MASK) != GC_MASK) gc_update_object((obj));}
#define run_collector() (gcPhase != GC_IDLE ? gc_run_collector(), 1 : 0)

#define HEADER_SIZE (sizeof(Object))

#define fields_start(OBJ_)  ((byte *) (OBJ_) + HEADER_SIZE)
// Generic access to array data given an array object
#define array_start(OBJ_)   (is_std_array((Object *)(OBJ_)) ? (((byte *) (OBJ_)) + HEADER_SIZE) : (byte *)(((FOURBYTES *)(OBJ_)) + ((int)((BigArray *)(OBJ_))->offset)))
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

#define protect_obj(OBJ) (((Object *)OBJ)->sync.monitorCount++)
#define unprotect_obj(OBJ) (((Object *)OBJ)->sync.monitorCount--)

extern TWOBYTES failed_alloc_size;

#define MEM_ABSOLUTE 0
#define MEM_THREADS 1
#define MEM_HEAP 2
#define MEM_IMAGE 3
#define MEM_STATICS 4
#define MEM_MEM 5

extern byte *memory_base[];

extern FOURBYTES mem_peek(int base, int offset, int typ);
extern void mem_copy(Object *obj, int objoffset, int base, int offset, int len);
extern REFERENCE mem_get_reference(int base, int offset);

#endif // _MEMORY_H




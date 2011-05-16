
#include "types.h"
#include "classes.h"
#include "memory.h"

#ifndef _LANGUAGE_H
#define _LANGUAGE_H


#define STATIC_INITIALIZER 0

// Class flags:

#define C_INITIALIZED  0x01
#define C_ARRAY        0x02
#define C_HASCLINIT    0x04
#define C_INTERFACE    0x08
#define C_NOREFS       0x10
#define C_PRIMITIVE    0x20
#define C_INITIALIZING 0x40

typedef struct S_MasterRecord
{
  TWOBYTES magicNumber;
  TWOBYTES constantTableOffset;
  TWOBYTES constantValuesOffset;
  TWOBYTES numConstants;
  
  /**
   * Offset to STATICFIELD[].
   */
  TWOBYTES staticFieldsOffset;
  
  /**
   * Size of all static state in bytes.
   */
  TWOBYTES staticStateLength;
  TWOBYTES numStaticFields;
  
  /**
   * Offset to sequence of class indices (bytes).
   */
  TWOBYTES entryClassesOffset;
  byte numEntryClasses;
  byte lastClass;

  TWOBYTES runtimeOptions;
} MasterRecord;

typedef struct S_ClassRecord
{
  /**
   * Object header, to allow access as a Java object.
   */
  objFlags objectHdr;
  /**
   * Space occupied by instance in bytes.
   */
  TWOBYTES classSize;
  /**
   * The following 4 fields are used differently depending upon the type
   * of class. They could be defined here as a union, but this structure
   * is also mad available to Java and it is not possible to represent
   * unions easily in Java. For this reason we use simple field names in
   * both the C and Java code. The following table describes the field
   * usage.
   *
   * Normal Class
   * CIAData1: Method table offset from the start of the binary.
   * CIAData2: Field table offset from the start of the binary.
   * CIACnt1:  Number of method records
   * CIACnt2:  Number of field records
   *
   * Interface Class
   * CIAData1: Method table offset from the start of the binary. if it is zero
   *           there are no methods. Otherwise it points to a single entry for
   *           the static initializer.
   * CIAData2: Instance member map offset from the start of the binary.
   * CIACnt1:  Class base, the class number of the first bit in the table
   * CIACnt2:  Number of valid bits in the table.
   *
   * Array Class
   * CIAData1: The dimension of the array
   * CIAData2: The class of the array element.
   * CIACnt1:  Unused
   * CIACnt2:  Unused 
   */
  TWOBYTES CIAData1;
  TWOBYTES CIAData2;
  byte CIACnt1;
  byte CIACnt2;

  byte parentClass;
  byte cflags;
} __attribute__((packed)) ClassRecord;

// Method flags:

#define M_NATIVE       0x01
#define M_SYNCHRONIZED 0x02
#define M_STATIC       0x04

typedef struct S_MethodRecord
{
  // Unique id for the signature of the method
  TWOBYTES signatureId;
  // Offset to table of exception information
  TWOBYTES exceptionTable;
  TWOBYTES codeOffset;
  // Number of 32-bit locals (long is counted as 2 locals).
  byte numLocals;
  // Maximum size of local operand stack, in 32-bit words.
  byte maxOperands;
  // It should be such that stackTop-numParameters unwinds the stack.
  // The receiver in non-static methods is counted as one word. 
  byte numParameters;
  // Number of exception handlers
  byte numExceptionHandlers;
  byte mflags;
} MethodRecord;

typedef struct S_ExceptionRecord
{
  TWOBYTES start;
  TWOBYTES end;
  TWOBYTES handler; 
  // The index of a Throwable class.
  byte classIndex; 
} ExceptionRecord;

typedef struct S_ConstantRecord
{
  /**
   * Offset to bytes of constant.
   */
  TWOBYTES offset;
  /**
   * Type of constant.
   */
  byte constantType;
  byte stringLength;
} ConstantRecord;

typedef TWOBYTES STATICFIELD;

extern byte *installedBinary;

extern ConstantRecord* constantTableBase;
extern byte* staticFieldsBase;
extern byte* entryClassesBase;
extern ClassRecord* classBase;
extern objSync *staticSyncBase;
// base of static area of all classes
extern byte *classStaticStateBase;
#if EXECUTE_FROM_FLASH
// base of class status table
extern byte *classStatusBase;
#endif
//base of constant values
extern byte *constantValuesBase;

extern void dispatch_virtual (Object *obj, int signature, byte *rAddr);
extern MethodRecord *find_method (ClassRecord *classRec, int signature);
extern boolean instance_of (Object *obj, const byte cls);
extern void do_return (int numWords);
extern int dispatch_static_initializer (ClassRecord *aRec, byte *rAddr);
extern boolean dispatch_special (MethodRecord *methodRecord, byte *retAddr);
void dispatch_special_checked (byte classIndex, byte methodIndex, byte *retAddr, byte *btAddr);
Object *create_stack_trace(Thread *thread, Object *ignore);
int execute_program(int prog);
extern boolean is_assignable(const byte srcSig, const byte dstSig);
extern byte get_base_type(ClassRecord *classRec);

void install_binary(const byte *ptr);
extern boolean is_valid_executable(byte * start, int len);
//#define install_binary(PTR_)        (installedBinary=(PTR_))

#define get_master_record()         ((MasterRecord *) installedBinary)
#define get_magic_number()          get_master_record()->magicNumber
#define get_binary_base()           ((byte *) installedBinary)
#define __get_class_base()          ((ClassRecord *) (get_binary_base() + sizeof(MasterRecord)))
#define get_class_base()            ((ClassRecord *) (classBase))

#define get_class_record(CLASSIDX_) (get_class_base() + (CLASSIDX_))
#define get_method_table(CREC_)     ((MethodRecord *) (get_binary_base() + (CREC_)->CIAData1))

#define get_field_table(CREC_)      ((byte *) (get_binary_base() + (CREC_)->CIAData2))

#define get_field_type(CR_,I_)      (*(get_field_table(CR_) + (I_)))

#define get_method_record(CR_,I_)   (get_method_table(CR_) + (I_)) 

#define __get_constant_base()       ((ConstantRecord *) (get_binary_base() + get_master_record()->constantTableOffset))
#define get_constant_base()         (constantTableBase)

#define get_constant_record(IDX_)   (get_constant_base() + (IDX_))

#define get_constant_ptr(CR_)       (get_binary_base() + (CR_)->offset)
#define __get_constant_values_base() (get_binary_base() + get_master_record()->constantValuesOffset)
#define get_constant_values_base()  (constantValuesBase)

#define class_size(CLASSIDX_)       (get_class_record(CLASSIDX_)->classSize)

#if EXECUTE_FROM_FLASH
#define get_class_status(CREC_)     (classStatusBase[ CREC_ - get_class_base()])
#endif

#define is_array_class(CREC_)       (((CREC_)->cflags & C_ARRAY) != 0)
#define has_clinit(CREC_)           (((CREC_)->cflags & C_HASCLINIT) != 0)
#define is_interface(CREC_)         (((CREC_)->cflags & C_INTERFACE) != 0)
#define has_norefs(CREC_)           (((CREC_)->cflags & C_NOREFS) != 0)

#define get_dim(CREC_)              ((CREC_)->CIAData1)
#define get_element_class(CREC_)    ((CREC_)->CIAData2)
#define get_method_cnt(CREC_)        ((CREC_)->CIACnt1)
#define get_field_cnt(CREC_)        ((CREC_)->CIACnt2)
#define get_interface_map(CREC_)    ((byte*)(get_binary_base() + (CREC_)->CIAData2))
#define get_interface_map_base(CREC_) ((CREC_)->CIACnt1)
#define get_interface_map_len(CREC_) ((CREC_)->CIACnt2)
#define is_primitive(CLASSIDX_)     ((CLASSIDX_) >= BOOLEAN && (CLASSIDX_) <= LONG )
#if EXECUTE_FROM_FLASH
#define set_init_state(CREC_, state)(get_class_status(CREC_) |= (state))
#define get_init_state_idx(IDX_)    (classStatusBase[IDX_])
#define get_init_state(CREC_)       (get_class_status(CREC_))
#else
#define set_init_state(CREC_, state)((CREC_)->cflags |= (state))
#define get_init_state_idx(IDX_)    (get_class_record(IDX_)->cflags)
#define get_init_state(CREC_)       ((CREC_)->cflags)
#endif
#define is_initialized_idx(IDX_)    (get_init_state_idx(IDX_) & C_INITIALIZED)
#define is_initialized(CREC_)       (get_init_state(CREC_) & C_INITIALIZED)

#define is_synchronized(MREC_)      (((MREC_)->mflags & M_SYNCHRONIZED) != 0)
#define is_static(MREC_)      (((MREC_)->mflags & M_STATIC) != 0)
#define is_native(MREC_)            (((MREC_)->mflags & M_NATIVE) != 0)
#define get_code_ptr(MREC_)         (get_binary_base() + (MREC_)->codeOffset)

#define __get_static_fields_base()  (get_binary_base() + get_master_record()->staticFieldsOffset)
#define get_static_fields_base()    (staticFieldsBase)

#define get_static_state_base()     (classStaticStateBase)

#define get_static_field_offset(R_) ((R_) & 0x0FFF)

#define get_num_entry_classes()     (get_master_record()->numEntryClasses)
#define __get_entry_classes_base()  (get_binary_base() + get_master_record()->entryClassesOffset)
#define get_entry_classes_base()    (entryClassesBase)
#define get_entry_class(N_)         (*(get_entry_classes_base() + (N_)))

static inline void initialize_binary()
{
  MasterRecord *mrec;
  
  mrec = get_master_record();

/*  printf("Got master record\n");
  printf("Base is %d\n",get_binary_base());
  printf("Offset is %d\n",(int) mrec->staticStateOffset); */

  /* printf("Length is %d\n",(int) mrec->staticStateLength);*/
  //zero_mem ((TWOBYTES *) (get_static_state_base()), (mrec->staticStateLength+1)/2);
//  printf("Zeroed memory\n");
}

// return codes used to indicate the state of byte code execution
#define EXEC_RETRY   -1 /* a retry of the current instrucion is required */
#define EXEC_CONTINUE 0 /* No action required simply return/continue */
#define EXEC_RUN      1 /* Execute from the new value now in curPc */
#define EXEC_EXCEPTION 2 /* An exception has been thrown PC will be correct */

#endif // _LANGUAGE_H










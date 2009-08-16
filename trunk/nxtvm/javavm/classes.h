/**
 * classes.h
 * Contains conterparts of special classes as C structs.
 */
 
#ifndef _CLASSES_H
#define _CLASSES_H

#include "types.h"

#define CLASS_MASK      0x00FF
#define CLASS_SHIFT     0

#define GC_MASK         0xc000
#define GC_SHIFT        14

#define ARRAY_LENGTH_MASK  0x3F00
#define ARRAY_LENGTH_SHIFT 8

#define LEN_OBJECT      0x3f
#define LEN_BIGARRAY    0x3e

#define is_array(OBJ_)           ((OBJ_)->flags.length != LEN_OBJECT)
#define get_monitor_count(SYNC_)  ((SYNC_)->monitorCount)
#define is_gc(OBJ_)              ((OBJ_)->flags.mark)

// Double-check these data structures with the 
// Java declaration of each corresponding class.
/**
 * Object header used for all objects types.
 * The mark field is used by the garbage collector.
 * Normal objects have a length field set to LEN_OBJECT
 * Arrays with more then 61 elements have the length field set to LEN_BIGARRAY
 * Arrays with less than 61 elements have the actual length set in length.
 * Free memory has a class of T_FREE and either a 0 length or LEN_BIGARRAY
 * Big arrays have the actual length stored in the length field of the
 * BigArray header.
 */
typedef struct _flags
{
  byte     class;
  byte     length:6;
  byte     mark:2;
} __attribute__((packed)) objFlags;
/**
 * Synchronization state.
 */
typedef struct
{
  byte monitorCount;
  byte threadId;
} __attribute__((packed)) objSync;

/**
 * Object class native structure
 */
typedef struct S_Object
{
  objFlags flags;
  objSync sync;
} __attribute__((packed)) Object;

typedef struct S_BigArray
{
  Object hdr;
  TWOBYTES length;
  TWOBYTES padding;
} BigArray;

/**
 * Thread class native structure
 */
typedef struct S_Thread
{
  Object _super;	     // Superclass object storage

  REFERENCE nextThread;      // Intrinsic circular list of threads
  REFERENCE waitingOn;       // Object who's monitor we want
  objSync *sync;             // Pointer to the sync data for the object
  JINT sleepUntil;           // Time to wake up
  REFERENCE stackFrameArray; // Array of stack frames
  REFERENCE stackArray;      // The stack itself
  JBYTE stackFrameArraySize; // Number of stack frames in use.
  JBYTE monitorCount;        // Saved monitor depth for context switches
  JBYTE threadId;            // Unique thread ID
  JBYTE state;               // RUNNING, DEAD, etc.
  JBYTE priority;            // The priority
  JBYTE interruptState;      // INTERRUPT_CLEARED, INTERRUPT_REQUESTED, ...
  JBYTE daemon;              // true == daemon thread
} Thread;

/**
 * Runtime class native structure. Doesn't actually contain
 * any instance data. Maybe it ought to? Like ALL of the leJOS
 * specific runtime instance data?
 */
typedef struct S_Runtime
{
  Object _super;
} Runtime;

/**
 * String class native structure
 */
typedef struct S_String
{
  Object _super;

  REFERENCE characters;
} String;

#define is_big_array(ARR_)       ((ARR_)->flags.length == LEN_BIGARRAY)
#define is_std_array(ARR_)       ((ARR_)->flags.length < LEN_BIGARRAY)
#define get_array_length(ARR_)   (is_std_array(ARR_) ? (ARR_)->flags.length : ((BigArray *)(ARR_))->length)
#define get_class_index(OBJ_) ((OBJ_)->flags.class)

#endif // _CLASSES_H










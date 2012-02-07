#ifndef _CONSTANTS_H
#define _CONSTANTS_H
#include "specialclasses.h"

#define null ((void *) 0)
#define true (1)
#define false (0)

#define JNULL (0)

// Standard Java types & primitives.
// These must match the values used by the linker and the corresponding
// entries in classes.db/specialclasses.h
#define T_REFERENCE 0
#define T_CLASS 2
#define T_BOOLEAN 4
#define T_CHAR 5
#define T_FLOAT 6
#define T_DOUBLE 7
#define T_BYTE 8
#define T_SHORT 9
#define T_INT 10
#define T_LONG 11
#define T_VOID 12

// Special class value used to represent free space by the GC. We use 
// a void array entry which is not a valid type in Java.
#define T_FREE AV

#endif // _CONSTANTS_H

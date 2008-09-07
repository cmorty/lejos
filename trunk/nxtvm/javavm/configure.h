
#ifndef _CONFIGURE_H
#define _CONFIGURE_H

#define FIXED_STACK_SIZE				0

#if FIXED_STACK_SIZE

/**
 * Initial level of recursion.
 */
#define INITIAL_STACK_FRAMES             10
 
/**
 * Initial number of words in a thread's stack
 * (for both locals and operands). Needs to be an
 * even number.
 */
#define INITIAL_STACK_SIZE               70

#else

#define INITIAL_STACK_FRAMES             4
#define INITIAL_STACK_SIZE               10

#endif

/**
 * Should always be 1.
 */
#define STACK_CHECKING                   1

/**
 * Should always be 1.
 */
#define ARRAY_CHECKING                   1

/**
 * If not 0, coalesce adjacent free blocks in the heap
 */
#define COALESCE                         1

/**
 * If not 0 allow multiple heap segments
 */
#define SEGMENTED_HEAP                   0

/**
 * If not 0, threads in the DEAD state are
 * removed from the circular list. Recommended.
 */
#define REMOVE_DEAD_THREADS              1

/**
 * Slightly safer code (?)
 */
#define SAFE                             1

/**
 * Set to non-zero if we want the scheduler to perform priority
 * inversion avoidance.
 */
#define PI_AVOIDANCE                     1

/**
 * If not 0, use a garbage collector. It consumes about
 * 1300 bytes of flash and about 1800 bytes of ram.
 */
#define GARBAGE_COLLECTOR                1

/**
 * Max number of VM objects that we need to protect, from the gc.
 */
#define MAX_VM_REFS                      8

/**
 * If not 0, leave binary in rom instead of copying it to ram.
 */
#define EXECUTE_FROM_FLASH               1

/**
 * If not 0, use diagnostic statistical instrumentation
 */
#define USE_VARSTAT                      0

/**
 * If not 0 include the Remote Console output functions
 */
#define REMOTE_CONSOLE                   0

#endif


#ifndef _CONFIGURE_H
#define _CONFIGURE_H

#define FIXED_STACK_SIZE		0

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

#define INITIAL_STACK_FRAMES             8
#define INITIAL_STACK_SIZE               32

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
 * Choose the type of memory allocator to use:
 */
#define MEM_SIMPLE                       0
#define MEM_MARKSWEEP                    1
#define MEM_CONCURRENT                   2
#define GARBAGE_COLLECTOR                MEM_CONCURRENT

/**
 * Max number of VM objects that we need to protect, from the gc.
 */
#define MAX_VM_REFS                      8

/**
 * If not 0, leave binary in rom instead of copying it to ram.
 */
#define EXECUTE_FROM_FLASH               1

/**
 * If not 0 include the Remote Console output functions
 */
#define REMOTE_CONSOLE                   0

/**
 * Choose the type of interpreter loop to use. Set to none zero
 * for the faster, gcc specific direct jump code.
 */
#define FAST_DISPATCH                    1

/**
 * Include support for LONG operations
 */
#define LONG_ARITHMETIC                  1

#endif

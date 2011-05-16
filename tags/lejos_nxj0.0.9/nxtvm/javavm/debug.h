/**
 * debug.h
 * Contains conterparts of special classes as C structs.
 */
 
#ifndef _DEBUG_H
#define _DEBUG_H

#include "constants.h"
#include "classes.h"
#include "language.h"

/**
 * DebugInfo class native structure
 */
typedef struct S_Debug
{
  Object _super;	     // Superclass object storage
  JINT typ;                  // type of debug event
  REFERENCE exception;
  REFERENCE thread;
  JINT method;
  JINT pc;
} Debug;

#define DBG_NONE 0
#define DBG_EXCEPTION 1
#define DBG_USER_INTERRUPT 2
#define DBG_PROGRAM_EXIT 3
#define DBG_MAX_EVENT DBG_PROGRAM_EXIT

#define DBG_EVENT_DISABLE 0
#define DBG_EVENT_ENABLE 1
#define DBG_EVENT_IGNORE 2

extern byte debugEventOptions[];
extern void set_debug(Debug *_debug);
extern boolean debug_uncaught_exception(Throwable * exception,
                          const Thread * thread,
                          const int methodRecord,
                          const int pc);
extern boolean debug_user_interrupt();
extern boolean debug_program_exit();

extern void init_debug();
#endif // _DEBUG_H

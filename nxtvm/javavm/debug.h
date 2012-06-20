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
  JINT method2;
  JINT pc2;
} Debug;

#define DBG_NONE 0
#define DBG_EXCEPTION 1
#define DBG_USER_INTERRUPT 2
#define DBG_THREAD_START 3
#define DBG_THREAD_STOP 4
#define DBG_PROGRAM_START 5
#define DBG_BREAKPOINT 6
#define DBG_SINGLE_STEP 7
#define DBG_PROGRAM_EXIT 8
#define DBG_MAX_EVENT DBG_PROGRAM_EXIT

#define DBG_EVENT_DISABLE 0
#define DBG_EVENT_ENABLE 1
#define DBG_EVENT_IGNORE 2

typedef struct S_SteppingRequest{
	Object _super;
	JINT stepDepth;
        JINT stepFrame;
	JINT methodId;
	REFERENCE stepPCs;
} SteppingRequest;

#define STEP_DEPTH_INTO 0
#define STEP_DEPTH_OVER 1
#define STEP_DEPTH_OUT 2

extern byte debugEventOptions[];
extern void set_debug(Debug *_debug);
boolean debug_event(int event, Throwable *exception, const Thread *thread,
		const int method, const int pc, const int method2, const int pc2);
extern boolean debug_exception(Throwable * exception,
                          const Thread * thread,
                          const int methodRecord,
                          const int pc,
                          const int catchMethodRecord,
                          const int catchPc);
extern boolean debug_user_interrupt();
extern boolean debug_program_exit();
extern boolean debug_program_start();
extern boolean debug_thread_start(Thread* thread);
extern boolean debug_thread_stop(Thread* thread);
extern boolean debug_breakpoint(const Thread * thread,
                          const int methodRecord,
                          const int pc);
extern boolean debug_single_step(Thread * thread,
                          const int methodRecord,
                          const int pc);

extern void init_debug();

extern boolean check_stepping(MethodRecord* method, byte *pc);

#endif // _DEBUG_H

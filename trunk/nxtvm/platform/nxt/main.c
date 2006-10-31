/**
 * main.c
 * NXT VM.
 */

#include <stdio.h>
#include "types.h"
#include "constants.h"
#include "classes.h"
#include "threads.h"
#include "stack.h"
#include "specialclasses.h"
#include "specialsignatures.h"
#include "language.h"
#include "memory.h"
#include "interpreter.h"
#include "exceptions.h"
#include "trace.h"
#include "poll.h"
#include "sensors.h"
#include "platform_hooks.h"
#include "java_binary.h"

#define MEMORY_SIZE 4096 /* 8 Kb */

#define STATUS_BYTE ((char *) 0x20F000)

#define STATUS_WORD ((int *) 0x20F004)

byte *region;
Thread   *bootThread;

FOURBYTES sys_time = 0;

int last_sys_time;              /* to generate ticks */
int last_ad_time;               /* to generate sensor reads */

FOURBYTES get_sys_time_impl()
{
  return sys_time;
}


void handle_uncaught_exception (Object *exception,
                                       const Thread *thread,
				       const MethodRecord *methodRecord,
				       const MethodRecord *rootMethod,
				       byte *pc)
{
					       
}

void switch_thread_hook()
{
  // NOP
}


void assert_hook (boolean aCond, int aCode)
{
}


void run(void)
{
  init_poller();

  //printf("Initializing Binary\n");

  // Initialize binary image state
  initialize_binary();

  //printf("Initializing memory\n");

  // Initialize memory
  {
    TWOBYTES size;

    memory_init ();
    size = MEMORY_SIZE;
    region = (byte *) 0x20A000;
    memory_add_region (0x20A000, 0x20C000);
  }

  //printf("Initializing exceptions\n");

  // Initialize exceptions
  init_exceptions();

  *STATUS_BYTE = 1;

  // Create the boot thread (bootThread is a special global)
  bootThread = (Thread *) new_object_for_class (JAVA_LANG_THREAD);

  init_threads();
  if (!init_thread (bootThread))
  {
    return;	  
  }

  *STATUS_BYTE = 2;

  //printf("Executing Interpreter\n");

  // Execute the bytecode interpreter
  set_program_number (0);

  *STATUS_BYTE = 3;

  engine();
  // Engine returns when all non-daemon threads are dead
}

/***************************************************************************
 * int nxt_main *--------------------------------------------------------------------------
 ***************************************************************************/
//int main (int argc, char *argv[])
int nxt_main()
{
        init_sensors ();

        *STATUS_BYTE = 0;
        *STATUS_WORD = 0;

 //       printf("Installing Binary\n");

        install_binary(java_binary);

 //      printf("Running\n");

	run();

        return 0;
} 

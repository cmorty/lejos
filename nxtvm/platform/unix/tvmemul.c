/**
 * tvmemul.c
 * Entry source file for TinyVM emulator.
 */

#include <stdio.h>
#include <stdlib.h>
#include <sys/time.h>
#include <signal.h>
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
#include "load.h"
#include "trace.h"
#include "poll.h"
#include "sensors.h"
#include "platform_hooks.h"

#define MEMORY_SIZE 8192 /* 16 Kb */
#define EXTRA_MEMORY_SIZE 1536 /* 3 Kb */
#define DEBUG_RUNS  0

byte *region;
Thread   *bootThread;
struct timeval gStart;
struct itimerval itimer =
{
  {0, 1000},
  {0, 1000}
};

int	verbose = 0;	/* If 1, print descriptive strings. */

int last_sys_time;              /* to generate ticks */
int last_ad_time;               /* to generate sensor reads */

void handle_uncaught_exception (Object *exception,
                                       const Thread *thread,
				       const MethodRecord *methodRecord,
				       const MethodRecord *rootMethod,
				       byte *pc)
{
    printf ("*** UNCAUGHT EXCEPTION/ERROR: \n");
    printf ("--  Exception class   : %u\n", (unsigned) get_class_index (exception));
    printf ("--  Thread            : %u\n", (unsigned) thread->threadId);
    printf ("--  Method signature  : %u\n", (unsigned) methodRecord->signatureId);
    printf ("--  Root method sig.  : %u\n", (unsigned) rootMethod->signatureId);
    printf ("--  Bytecode offset   : %u\n", (unsigned) pc - 
            (int) get_code_ptr(methodRecord));  					       
}

void switch_thread_hook()
{
  // NOP
}

FOURBYTES sys_time = 0;
struct timeval now;

void timer_handler(int signo)
{
  gettimeofday(&now, 0);
  // 11/17/01: Jose commented out:
  // 03/29/02: Paul put back in. Regression tests will fail otherwise.
  timersub(&now, &gStart, &now);
  sys_time = now.tv_sec*1000 + now.tv_usec/1000;
  signal(SIGALRM, timer_handler);
}

FOURBYTES get_sys_time_impl()
{
  return sys_time;
}

void run(void)
{
  #if DEBUG_RUNS
  int count = 0;
  #endif

  init_poller();
  // Initialize binary image state
  initialize_binary();

  // Initialize memory
  {
    TWOBYTES size;

    memory_init ();
#if SEGMENTED_HEAP
    size = EXTRA_MEMORY_SIZE;
    region = (byte *) malloc (size * sizeof (TWOBYTES));
    memory_add_region (region, region + size * sizeof (TWOBYTES));
#endif
    size = MEMORY_SIZE;
    region = (byte *) malloc (size * sizeof (TWOBYTES));
    memory_add_region (region, region + size * sizeof (TWOBYTES));
  }

  // Initialize exceptions
  init_exceptions();
  // Create the boot thread (bootThread is a special global)
  bootThread = (Thread *) new_object_for_class (JAVA_LANG_THREAD);
  
  #if DEBUG_THREADS
  printf ("Created bootThread: %d. Initializing...\n", (int) bootThread);
  #endif
  
  #if DEBUG_RUNS
  for (count = 0; count < 100; count++)
  {
  #endif // DEBUG_RUNS

  #if DEBUG_RCX_MEMORY
  {
    TWOBYTES numNodes, biggest, freeMem;	
    scan_memory (&numNodes, &biggest, &freeMem);
    printf ("nodes = %d\n", (int) numNodes);
    printf ("biggest = %d\n", (int) biggest);
    printf ("freeMem = %d\n", (int) freeMem);
  }
  #endif

  init_threads();
  if (!init_thread (bootThread))
  {
    printf ("Unable to initialize threading module.\n");
    exit (1);	  
  }
  // Execute the bytecode interpreter
  set_program_number (0);
  #if DEBUG_STARTUP
  printf ("Engine starting.\n");
  #endif
  engine();
  // Engine returns when all non-daemon threads are dead
  #if DEBUG_STARTUP
  printf ("Engine finished.\n");
  #endif

  #if DEBUG_RUNS
  }
  #endif // DEBUG_RUNS
}

/***************************************************************************
 * int main
 * Parses command line. Format is:
 *	argv[0] [-v] bin_file
 *
 * options:
 *	-v	Verbose mode. Prints text output rather than raw output.
 *
 *--------------------------------------------------------------------------
 * To go into man page:
 * Name:	emu-lejosrun - Emulate lejos RCX code in Unix
 *
 * Synosis:	emu-lejosrun [-v] bin_file
 *
 * Description:	Executes a binary file created by the lejos compiler within
 *		Unix rather than in the RCX environment. The Java byte-codes
 *		are executed here, and their actions are listed rather than
 *		executed as they would be on the real RCX device.
 *
 * Options:	-v	Verbose mode. Normally the output is printed in raw
 *			mode. The actual hex values are printed. Using this
 *			option displays more user-friendly output.
 *--------------------------------------------------------------------------
 ***************************************************************************/
int main (int argc, char *argv[])
{
	int	c;
	char	*file = argv[argc - 1];

	/* Process any options. */
	while(--argc > 0 && (*++argv)[0] == '-')
		while((c = *++argv[0]) != 0)
			switch (c) {
			case 'v':
				verbose = 1;
				break;
			default:
				printf("%s: unknown option %c\n",
					argv[0], c);
				exit(1);
			}
	if (argc != 1)
	{
		printf ("%s runs a binary dumped by the linker.\n", argv[0]);
		printf ("Use: %s [-v] binary_file\n", argv[0]);
		exit (1);
	}
#if DEBUG_STARTUP
	printf ("Reading binary %s\n", file);
#endif
	readBinary (file);
	if (gettimeofday(&gStart, NULL)) 
		perror("main: gettimeofday");
	timer_handler (SIGALRM);
	if (setitimer(ITIMER_REAL, &itimer, null) < 0)
	{
	  printf ("Failed to set interval timer\n");
	  exit(1);
	}

        init_sensors ();
        last_ad_time = get_sys_time();

	run();
	itimer.it_value.tv_usec = 0;
	setitimer(ITIMER_REAL, &itimer, null);
	signal(SIGALRM, SIG_DFL);
	exit(0);
} 

#ifndef _PLATFORM_HOOKS_H
#  define _PLATFORM_HOOKS_H

// Methods declared here must be implemented by
// each platform.

#  include "types.h"
#  include "classes.h"
#  include "language.h"
#  include "interpreter.h"

#  include "poll.h"
#  include "display.h"

extern int last_ad_time;

static inline void
instruction_hook(void)
{
  //gMakeRequest = true;
}


static inline void
tick_hook(void)
{
  register int st = get_sys_time();

  if (st >= last_ad_time + 3) {
    last_ad_time = st;
    poll_inputs();
  }
  if (st > display_update_time)
    display_update();
}

static inline void
idle_hook()
{
}

extern void switch_thread_hook();

/**
 * Called when thread is about to die due to an uncaught exception.
 */
extern void handle_uncaught_exception(Object * exception,
				      const Thread * thread,
				      const MethodRecord * methodRecord,
				      const MethodRecord * rootMethod,
				      byte * pc);

/**
 * Dispatches a native method.
 */
extern int dispatch_native(TWOBYTES signature, STACKWORD * paramBase);

/**
 * Sensor interface
 */
#define N_SENSORS (4)
extern int read_sensor(int port);

/**
 * Button interface
 */
extern int buttons_get();

#endif // _PLATFORM_HOOKS_H

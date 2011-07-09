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
#include "AT91SAM7.h"

static inline void
instruction_hook(void)
{
  //gMakeRequest = true;
}


static inline void
tick_hook(void)
{
  register int st = get_sys_time();

  check_events();
  if (st > display_update_time)
    display_update();
}

static inline void
idle_hook()
{
  // Turn off the Arm clock when idle (interrupts etc. will re-enable)
  *AT91C_PMC_SCDR = AT91C_PMC_PCK;
}

extern void switch_thread_hook();

/**
 * Dispatches a native method.
 */
extern int dispatch_native(TWOBYTES signature, STACKWORD * paramBase);

/**
 * Event interface
 */
extern JINT sp_check_event(JINT filter);
extern JINT buttons_check_event(JINT filter);
extern JINT bt_event_check(JINT filter);
extern JINT udp_event_check(JINT filter);
extern JINT i2c_event_check(JINT filter);
#endif // _PLATFORM_HOOKS_H

#include "platform_hooks.h"

#include "systick.h"


int last_ad_time;

FOURBYTES
get_sys_time_impl(void)
{
  return systick_get_ms();
}

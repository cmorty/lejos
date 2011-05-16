#include "platform_hooks.h"

#include "systick.h"


FOURBYTES
get_sys_time_impl(void)
{
  return systick_get_ms();
}

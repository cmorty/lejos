#include "types.h"
#if REMOTE_CONSOLE
int printf(const char *format, ...);
#else
#define printf(fmt, ...)
#endif



#include "types.h"
#include "classes.h"

#ifndef _EXCEPTIONS_H
#define _EXCEPTIONS_H

extern void init_exceptions();
extern int throw_new_exception (int class);
extern int throw_exception (Throwable *exception);

#endif



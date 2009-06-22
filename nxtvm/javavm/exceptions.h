
#include "types.h"
#include "classes.h"

#ifndef _EXCEPTIONS_H
#define _EXCEPTIONS_H

extern Object *outOfMemoryError;
extern Object *noSuchMethodError;
extern Object *stackOverflowError;
extern Object *nullPointerException;
extern Object *classCastException;
extern Object *arithmeticException;
extern Object *arrayIndexOutOfBoundsException;
extern Object *illegalArgumentException;
extern Object *interruptedException;
extern Object *illegalStateException;
extern Object *illegalMonitorStateException;
extern Object *arrayStoreException;
extern Object *negativeArraySizeException;
extern Object *error;

extern void init_exceptions();
extern int throw_exception (Object *throwable);

#endif



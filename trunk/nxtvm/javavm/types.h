#ifndef _TYPES_H
#define _TYPES_H

#include "configure.h"

#include "platform_config.h"

typedef byte boolean;

/*
 * The following types must be defined in platform_config.h:
 * JBYTE
 * JSHORT
 * JINT
 * TWOBYTES
 * FOURBYTES
 */


typedef float        JFLOAT;
typedef JBYTE        JBOOLEAN;
typedef TWOBYTES       JCHAR;
typedef FOURBYTES    REFERENCE;
typedef FOURBYTES    STACKWORD;
#if LONG_ARITHMETIC
typedef long long    LLONG;
typedef unsigned long long    ULLONG;
#else
typedef long    LLONG;
typedef unsigned long    ULLONG;
#endif

typedef union
{
  JFLOAT fnum;
  STACKWORD sword;
} AuxConvUnion1;

// Map stackwords onto native long long
typedef union
{
  LLONG lnum;
  struct { STACKWORD lo; STACKWORD hi; } sw;
} JLONG;

// Map stackwords onto native double
typedef union
{
  double dnum;
  struct { STACKWORD hi; STACKWORD lo; } sw;
} JDOUBLE; 


#ifndef LITTLE_ENDIAN
#error LITTLE_ENDIAN not defined in platform_config.h
#endif

#ifndef jfloat2word
#define jfloat2word(FLOAT_) (((AuxConvUnion1) (FLOAT_)).sword)
#endif

#ifndef word2jfloat
#define word2jfloat(WORD_)  (((AuxConvUnion1) (WORD_)).fnum)
#endif

#define byte2jint(BYTE_)    ((JINT) (signed char) (BYTE_))
#define word2jint(WORD_)    ((JINT) (WORD_))
#define word2jshort(WORD_)  ((JSHORT) (WORD_))
#define word2obj(WORD_)     ((Object *) word2ptr(WORD_))
#define obj2word(OBJ_)      ptr2word(OBJ_)
#define obj2ref(OBJ_)       ptr2ref(OBJ_)
#define obj2ptr(OBJ_)       ((void *) (OBJ_))
#define ptr2ref(PTR_)       ((REFERENCE) ptr2word(PTR_))
#define ref2ptr(REF_)       word2ptr((STACKWORD) (REF_))
#define ref2obj(REF_)       ((Object *) ref2ptr(REF_))

#endif // _TYPES_H



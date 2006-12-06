#ifndef  __SYSTICK_H__
#define __SYSTICK_H__

#include "mytypes.h"

void systick_init(void);

void systick_get_time(U32 *sec, U32 *usec);

U32 systick_get_ms(void);

void systick_wait_ms(U32 ms);


void systick_test(void);

#endif

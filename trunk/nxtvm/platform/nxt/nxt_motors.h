#ifndef __NXT_MOTORS_H__
#define __NXT_MOTORS_H__

#include "mytypes.h"


int nxt_motor_get_count(int n);

int nxt_motor_set(int n, int cmd, int target_count, int speed_percent);


void nxt_motor_gpio_process(U32 gpio);

void nxt_motor_kHz_process(void);

#endif


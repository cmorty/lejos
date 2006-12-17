#include "nxt_motors.h"

# define MA0 0
# define MA1 1
# define MB0 2
# define MB1 3
# define MC0 4
# define MC1 5

static const U32 motor_pin_mask[NXT_N_MOTORS][2] = {
  { 1 << MA0, 1 << MA1 },
  { 1 << MB0, 1 << MB1},
  { 1 << MC0, 1 << MC1},
  
};


static struct {
  int current_count;
  int target_count;
  int speed_percent;
}motor [NXT_N_MOTORS];


int nxt_motor_get_count(U32 n)
{
  if(n < NXT_N_MOTORS)
    return motor[n].current_count;
  else
    return 0; 
}

void nxt_motor_set_count(U32 n, int count)
{
  if(n < NXT_N_MOTORS)
    motor[n].current_count = count;
}

void nxt_motor_set(U32 n, int cmd, int target_count, int speed_percent)
{
  if(n < NXT_N_MOTORS) {
    motor[n].target_count = target_count;
    motor[n].speed_percent = speed_percent;
  }
}


// ISR points used by motor processing
void nxt_motor_pio_process(U32 gpio)
{
}

void nxt_motor_kHz_process(void)
{
}



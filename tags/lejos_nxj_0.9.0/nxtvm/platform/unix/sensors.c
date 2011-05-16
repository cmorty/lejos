#include "platform_config.h"

#include "types.h"
#include "stack.h"
#include "threads.h"
#include "classes.h"
#include "language.h"
#include "sensors.h"
#include "poll.h"

extern int verbose;

sensor_t sensors[N_SENSORS] = {
  { 0, 0, 0, 0, 0 },
  { 0, 0, 0, 0, 0 },
  { 0, 0, 0, 0, 0 },
  { 0, 0, 0, 0, 0 }
};

FOURBYTES last_time[N_SENSORS];

void init_sensors( void)
{
  FOURBYTES time = get_sys_time();
  byte i;

  for (i=0; i<N_SENSORS; i++) {
    last_time[i] = time;
  }
}

/**
 * Increment sensor values every 200, 400, 600 and 800 ms
 * for sensor 0, 1, 2 & 3 respectively.
 */
void poll_sensors( void)
{
  byte i;
  sensor_t *pSensor = sensors;
  FOURBYTES time = get_sys_time();

  for( i=0; i<N_SENSORS; i++,pSensor++){
    if ((time - last_time[i]) > 200*(i+1))
    {
      last_time[i] = time;
      pSensor->value = (pSensor->value + 1) % 100;
    }
  }
}

void read_buttons(int dummy, short *output)
{
  *output = 0;
}


void check_for_data (char *valid, char **nextbyte)
{
  *valid = 0;
}


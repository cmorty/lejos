#include "platform_config.h"

#include "types.h"
#include "stack.h"
#include "threads.h"
#include "classes.h"
#include "language.h"
#include "sensors.h"
#include "poll.h"
#include "at91sam7s256.h"

extern int verbose;

sensor_t sensors[3] = {
  { 0, 0, 0, 0, 0 },
  { 0, 0, 0, 0, 0 },
  { 0, 0, 0, 0, 0 }
};

FOURBYTES last_time[3];

void init_sensors( void)
{
  FOURBYTES time = get_sys_time();
  byte i;

  for (i=0; i<3; i++) {
    last_time[i] = time;
  }
}

/**
 * Increment sensor values every 300, 600 and 900 ms
 * for sensor 0, 1 & 2 respectively.
 */
void poll_sensors( void)
{
  byte i;
  sensor_t *pSensor = sensors;
  FOURBYTES time = get_sys_time();

  for( i=0; i<3; i++,pSensor++){
    if ((time - last_time[i]) > 300*(i+1))
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

void activate(int sensor) 
{
  /* Enable output on light LED */

  int functions[] = {AT91C_PIO_PA23, AT91C_PIO_PA28, 
                   AT91C_PIO_PA29, AT91C_PIO_PA30};

  *AT91C_PIOA_PER |= functions[sensor];

  *AT91C_PIOA_OER |= functions[sensor];

  /* Switch the LED on */

  *AT91C_PIOA_SODR |= functions[sensor];

}

void passivate(int sensor) 
{
  /* Enable output on light LED */

  int functions[] = {AT91C_PIO_PA23, AT91C_PIO_PA28, 
                   AT91C_PIO_PA29, AT91C_PIO_PA30};

  *AT91C_PIOA_PER |= functions[sensor];

  *AT91C_PIOA_OER |= functions[sensor];

  /* Switch the LED off */

  *AT91C_PIOA_CODR |= functions[sensor];

}






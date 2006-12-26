#include "platform_config.h"

#include "types.h"
#include "stack.h"
#include "threads.h"
#include "classes.h"
#include "language.h"
#include "sensors.h"
#include "poll.h"
#include "at91sam7s256.h"
#include "nxt_avr.h"

extern int verbose;

sensor_t sensors[N_SENSORS] = {
  { 0, 0, 0, 0, 0 },
  { 0, 0, 0, 0, 0 },
  { 0, 0, 0, 0, 0 },
  { 0, 0, 0, 0, 0 }
};

void init_sensors( void)
{
}

/**
 * Read sensor values
 */
void poll_sensors( void)
{
  byte i;
  sensor_t *pSensor = sensors;

  for( i=0; i<N_SENSORS; i++,pSensor++){
    pSensor->value = sensor_adc(i);
  }
}

void read_buttons(int dummy, short *output)
{
  *output = (short) buttons_get();
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






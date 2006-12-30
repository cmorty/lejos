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
  int i;
  
  for(i=0;i<N_SENSORS;i++) 
  {
  	unset_digi0(i);
  	unset_digi1(i);
  	nxt_avr_set_input_power(i,0);
  }
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

void set_digi0(int sensor) 
{
  /* Enable output on the pin */

  int functions[] = {AT91C_PIO_PA23, AT91C_PIO_PA28, 
                   AT91C_PIO_PA29, AT91C_PIO_PA30};

  *AT91C_PIOA_PER |= functions[sensor];

  *AT91C_PIOA_OER |= functions[sensor];

  /* Set high */

  *AT91C_PIOA_SODR |= functions[sensor];

}

void unset_digi0(int sensor) 
{
  /* Enable output on the pin */

  int functions[] = {AT91C_PIO_PA23, AT91C_PIO_PA28, 
                   AT91C_PIO_PA29, AT91C_PIO_PA30};

  *AT91C_PIOA_PER |= functions[sensor];

  *AT91C_PIOA_OER |= functions[sensor];

  /* Set low */

  *AT91C_PIOA_CODR |= functions[sensor];

}

void set_digi1(int sensor) 
{
  /* Enable output on the pin */

  int functions[] = {AT91C_PIO_PA18, AT91C_PIO_PA19, 
                   AT91C_PIO_PA20, AT91C_PIO_PA2};

  *AT91C_PIOA_PER |= functions[sensor];

  *AT91C_PIOA_OER |= functions[sensor];

  /* Set high */

  *AT91C_PIOA_SODR |= functions[sensor];

}

void unset_digi1(int sensor) 
{
  /* Enable output on the pin */

  int functions[] = {AT91C_PIO_PA18, AT91C_PIO_PA19, 
                   AT91C_PIO_PA20, AT91C_PIO_PA2};

  *AT91C_PIOA_PER |= functions[sensor];

  *AT91C_PIOA_OER |= functions[sensor];

  /* Set low */

  *AT91C_PIOA_CODR |= functions[sensor];

}





#include "platform_config.h"

#include "types.h"
#include "mytypes.h"
#include "stack.h"
#include "threads.h"
#include "classes.h"
#include "language.h"
#include "sensors.h"
#include "poll.h"
#include "AT91SAM7.h"
#include "nxt_avr.h"

// Sensor port digital pins
const port_pins sensor_pins[4] = {
  {AT91C_PIO_PA23, AT91C_PIO_PA18},
  {AT91C_PIO_PA28, AT91C_PIO_PA19},
  {AT91C_PIO_PA29, AT91C_PIO_PA20},
  {AT91C_PIO_PA30, AT91C_PIO_PA2}
};


/**
 * Reset the port to a known state
 */
void
reset_sensor(int port)
{
  // Reset the port to be normal digital I/O
  U32 pins = sensor_pins[port].digi0 | sensor_pins[port].digi1;
  *AT91C_PIOA_PER = pins;
  *AT91C_PIOA_OER = pins;
  // and set the output to be zero
  *AT91C_PIOA_CODR |= pins;
  // If this is port with RS485 on it, reset those pins as well
  if (port == RS485_PORT)
  {
    *AT91C_PIOA_PER |= AT91C_PIO_PA5 | AT91C_PIO_PA6 | AT91C_PIO_PA7;
    *AT91C_PIOA_PPUDR |= AT91C_PIO_PA5 | AT91C_PIO_PA6 | AT91C_PIO_PA7;
    *AT91C_PIOA_OER |= AT91C_PIO_PA5 | AT91C_PIO_PA6 | AT91C_PIO_PA7;
    *AT91C_PIOA_CODR |= AT91C_PIO_PA5 | AT91C_PIO_PA6 | AT91C_PIO_PA7;
  }
  // reset the power being supplied to the port
  nxt_avr_set_input_power(port, 0);
}


void
init_sensors(void)
{
  int i;

  for (i = 0; i < N_SENSORS; i++)
    reset_sensor(i);
}

int
read_sensor(int port)
{
    return sensor_adc(port);
}

void
set_sensor(int port, int setting)
{
  if (setting & 1)
    *AT91C_PIOA_SODR = sensor_pins[port].digi0;
  else
    *AT91C_PIOA_CODR = sensor_pins[port].digi0;
  if (setting & 2)
    *AT91C_PIOA_SODR = sensor_pins[port].digi1;
  else
    *AT91C_PIOA_CODR = sensor_pins[port].digi1;
}

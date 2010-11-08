/**
 * NXT Sensor port interface
 * The NXT sensor port is complex it has 3 active pins plus two ground
 * pins and a 5v supply. One of the pins (ana) is accessed via the AVR and 
 * has the following operating modes:
 * 9v Constant power
 * 9v pulsed power (3ms on 0.1ms off)
 * 5v pull up only
 * In all cases the ADC in the AVR will read the voltage on the pin during
 * during the 0.1ms off period giving a sampling rate of 333Hz.
 * Pin digi0 is a simple digital I/O pin it can be set/get.
 * Pin digi1 can be used either as a digital I/O pin (via set/get) or as an
 * addional analogue input pin (via read).
 */
#include "platform_config.h"

#include "types.h"
#include "mytypes.h"
#include "stack.h"
#include "threads.h"
#include "classes.h"
#include "language.h"
#include "AT91SAM7.h"
#include "sensors.h"
#include "poll.h"
#include "nxt_avr.h"

// Sensor port digital pins
const port_pins sensor_pins[N_SENSORS] = {
  {{AT91C_PIO_PA23, AT91C_PIO_PA18}, AT91C_ADC_CH1, AT91C_ADC_CDR1},
  {{AT91C_PIO_PA28, AT91C_PIO_PA19}, AT91C_ADC_CH2, AT91C_ADC_CDR2},
  {{AT91C_PIO_PA29, AT91C_PIO_PA20}, AT91C_ADC_CH3, AT91C_ADC_CDR3},
  {{AT91C_PIO_PA30, AT91C_PIO_PA2}, AT91C_ADC_CH7, AT91C_ADC_CDR7}
};


// masks and bits used for sensor events
#define GT_EVENTS 0xf
#define LT_EVENTS 0xf0
#define GT_SHIFT 0
#define LT_SHIFT 4
#define GET_TARGET(f) (((f) >> 8) & 0x3ff)
#define GET_TOLERANCE(f) (((f) >> 18) & 0xff)



/**
 * Reset the port to a known state
 */
void
sp_reset(int port)
{
  // Reset the port to be normal digital I/O
  sp_set_mode(port, SP_DIGI0, SP_MODE_OUTPUT);
  sp_set_mode(port, SP_DIGI1, SP_MODE_OUTPUT);
  // and set the output to be zero
  sp_set(port, SP_DIGI0, 0);
  sp_set(port, SP_DIGI1, 0);
  // If this is port with RS485 on it, reset those pins as well
  if (port == RS485_PORT)
  {
    *AT91C_PIOA_PER = AT91C_PIO_PA5 | AT91C_PIO_PA6 | AT91C_PIO_PA7;
    *AT91C_PIOA_PPUDR = AT91C_PIO_PA5 | AT91C_PIO_PA6 | AT91C_PIO_PA7;
    *AT91C_PIOA_OER = AT91C_PIO_PA5 | AT91C_PIO_PA6 | AT91C_PIO_PA7;
    *AT91C_PIOA_CODR = AT91C_PIO_PA5 | AT91C_PIO_PA6 | AT91C_PIO_PA7;
  }
  // reset the power being supplied to the port
  sp_set_power(port, 0);
}


/**
 * Reset all ports.
 */
void
sp_init(void)
{
  int i;

  for (i = 0; i < N_SENSORS; i++)
    sp_reset(i);
}

/**
 * Read the analogue value for this port. We allow access to both the
 * AVR ADC and the AT91 ADC
 */
int
sp_read(int port, int pin)
{
  if (pin == SP_ANA)
    return sensor_adc(port);
  else
    return *sensor_pins[port].ADCData;
}

int
sp_check_event(int filter)
{
  int port;
  int bit = (1 << GT_SHIFT) | (1 << LT_SHIFT);
  int changed = 0;
  int target = GET_TARGET(filter);
  int tolerance = GET_TOLERANCE(filter);
  for(port = 0; port < N_SENSORS; port++, bit <<= 1)
    if (filter & bit)
    {
      U16 val = sensor_adc(port);
      if ((filter & bit & GT_EVENTS) && (val > (target + tolerance)))
        changed |= (bit & GT_EVENTS);
      if ((filter & bit & LT_EVENTS) && (val < (target - tolerance)))
        changed |= (bit & LT_EVENTS);
    }
  return changed;
}
        

void sp_set_mode(int port, int pin, int mode)
{
  int at_pin = sensor_pins[port].pins[pin];
  *AT91C_PIOA_PPUDR = at_pin;
  switch(mode)
  {
  case SP_MODE_OFF:
    *AT91C_PIOA_ODR = at_pin;
    *AT91C_PIOA_PDR = at_pin;
    break;
  case SP_MODE_INPUT:
    *AT91C_PIOA_PER = at_pin;
    *AT91C_PIOA_ODR = at_pin;
    break;
  case SP_MODE_OUTPUT:
    *AT91C_PIOA_PER = at_pin;
    *AT91C_PIOA_OER = at_pin;
    break;
  case SP_MODE_ADC:
    *AT91C_PIOA_ODR = at_pin;
    *AT91C_PIOA_PER = at_pin;
    break;
  }
  if (pin == SP_DIGI1)
  {
    if (mode == SP_MODE_ADC)
      *AT91C_ADC_CHER = sensor_pins[port].ADCChan;
    else
      *AT91C_ADC_CHDR = sensor_pins[port].ADCChan;
  }
}

int sp_get(int port, int pin)
{
  return (sensor_pins[port].pins[pin] & *AT91C_PIOA_PDSR) != 0;
}

void
sp_set(int port, int pin, int val)
{
  int at_pin = sensor_pins[port].pins[pin];
  if (val)
    *AT91C_PIOA_SODR = at_pin;
  else
    *AT91C_PIOA_CODR = at_pin;

}

void
sp_set_power(int port, int val)
{
  nxt_avr_set_input_power(port, val);
}


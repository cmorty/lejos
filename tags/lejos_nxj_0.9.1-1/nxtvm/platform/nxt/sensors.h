#ifndef _SENSORS_H
#include "at91sam7s256.h"
#  define _SENSORS_H

#  define N_SENSORS (4)
#  define RS485_PORT (3)

// Sensor port pin modes
#define SP_MODE_OFF 0
#define SP_MODE_INPUT 1
#define SP_MODE_OUTPUT 2
#define SP_MODE_ADC 3

#define SP_DIGI0 0
#define SP_DIGI1 1
#define SP_ANA 2

typedef struct
{
  int pins[2];
  U32 ADCChan;
  AT91_REG * ADCData;
} port_pins;

extern const port_pins sensor_pins[];


extern void sp_reset(int);
extern void sp_init(void);
extern void sp_set(int, int, int);
extern int sp_get(int, int);
extern void sp_set_mode(int, int, int);
extern int sp_read(int, int);
extern void sp_set_power(int, int);
extern int sp_check_event(int);

#endif // _SENSORS_H

#ifndef _SENSORS_H
#  define _SENSORS_H

#  define N_SENSORS (4)
#  define RS485_PORT (3)

typedef struct
{
  U32 digi0;
  U32 digi1;
} port_pins;

extern const port_pins sensor_pins[];


extern void reset_sensor(int);
extern void init_sensors(void);
extern void set_sensor(int, int);
extern int read_sensor(int);

#endif // _SENSORS_H

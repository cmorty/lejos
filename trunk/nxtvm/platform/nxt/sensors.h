#ifndef _SENSORS_H
#define _SENSORS_H

typedef struct {
    char type;
    char mode;
    short raw;
    short value;
    char boolean;
} sensor_t;

extern sensor_t sensors[3];
extern void init_sensors( void);
extern void poll_sensors( void);
extern void read_buttons(int, short*);
extern void check_for_data (char *valid, char **nextbyte);
extern void activate(int);
extern void passivate(int);

#endif // _SENSORS_H

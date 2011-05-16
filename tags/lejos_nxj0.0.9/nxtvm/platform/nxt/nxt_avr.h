#ifndef __NXT_AVR_H__
#  define __NXT_AVR_H__

#  include "mytypes.h"

/* Main user interface */
void nxt_avr_init(void);

void nxt_avr_1kHz_update(void);

void nxt_avr_set_motor(U32 n, int power_percent, int brake);

void nxt_avr_power_down(void);

void nxt_avr_firmware_update_mode(void);

void nxt_avr_test_loop(void);

void nxt_avr_update(void);

U32 buttons_get(void);

S32 buttons_check_event(S32 filter);

U32 battery_voltage(void);

U32 sensor_adc(U32 n);

void nxt_avr_set_input_power(U32 n, U32 power_type);

void nxt_avr_set_key_click(U32 freq, U32 len, U32 vol);

#define BUTTON_ENTER 0x1
#define BUTTON_ESCAPE 0x8
#define BUTTON_LEFT 0x2
#define BUTTON_RIGHT 0x4

#endif

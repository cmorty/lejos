#ifndef __NXT_LCD_H__
#define __NXT_LCD_H__

#include "mytypes.h"

void nxt_lcd_init(void);
void nxt_lcd_power_up(void);
void nxt_lcd_power_down(void);
void nxt_lcd_data(const U8 *buffer, U32 nBytes);

void nxt_lcd_test(void);

#endif



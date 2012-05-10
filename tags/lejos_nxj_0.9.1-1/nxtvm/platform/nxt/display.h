#ifndef __DISPLAY_H__
#  define __DISPLAY_H__
#  include "platform_config.h"
#  include "classes.h"
#  include "mytypes.h"

void display_init(void);

void display_reset(void);

void display_update(void);

void display_force_update(void);

void display_clear(U32 updateToo);

void display_goto_xy(int x, int y);

void display_char(int c);

void display_string(const char *str);

void display_jstring(String *str);

void display_int(int val, U32 places);
void display_hex(U32 val, U32 places);

void display_unsigned(U32 val, U32 places);

void display_bitmap_copy(const U8 *data, U32 width, U32 depth, U32 x, U32 y);
void display_bitblt(byte *src, int sw, int sh, int sx, int sy, byte *dst, int dw, int dh, int dx, int dy, int w, int h, int rop);
void display_test(void);

U8 *display_get_buffer(void);

#define DEFAULT_UPDATE_PERIOD 250
int display_set_auto_update_period(int);

int display_get_update_complete_time();
extern U32 display_update_time;

#endif

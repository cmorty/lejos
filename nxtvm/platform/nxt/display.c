#include "display.h"
#include "nxt_lcd.h"
#include "systick.h"
#include "constants.h"
#include "specialclasses.h"
#include "classes.h"
#include <string.h>

typedef unsigned int uint;

#define DISPLAY_WIDTH (NXT_LCD_WIDTH)
#define DISPLAY_DEPTH (NXT_LCD_DEPTH)

/* NOTE
 * The following buffer is declared with one extra line (the +1).
 * This is to allow fast dma update of the screen (see nxt_spi.c
 * for details). The buffer is now created wrapped inside of a Java
 * array. This allows the buffer to be shared with Java applications.
 */
//static U8 display_buffer[DISPLAY_DEPTH+1][DISPLAY_WIDTH];
static struct
{
  BigArray arrayHdr;
  U8 display[DISPLAY_DEPTH+1][DISPLAY_WIDTH];
} __attribute__((packed)) display_array;
static U8 (*display_buffer)[DISPLAY_WIDTH] = display_array.display;

/* Font table for a 5x8 font. 1 pixel spacing between chars */
#define N_CHARS 128
#define FONT_WIDTH 5
#define CELL_WIDTH (FONT_WIDTH + 1)
#define DISPLAY_CHAR_WIDTH (DISPLAY_WIDTH/(CELL_WIDTH))
#define DISPLAY_CHAR_DEPTH (DISPLAY_DEPTH)
static const struct
{
  BigArray arrayHdr;
  U8 font[N_CHARS][FONT_WIDTH];
} __attribute__((packed)) font_array =
{{{{
    .length=LEN_BIGARRAY,
    .mark = 3,
    .class = T_BYTE+ALJAVA_LANG_OBJECT
   },
   {0,
   0}
  },
  N_CHARS*FONT_WIDTH
 },
 {
/* 0x00 */ {0x3E, 0x36, 0x2A, 0x36, 0x3E},
/* 0x01 */ {0x3E, 0x55, 0x61, 0x55, 0x3E},
/* 0x02 */ {0x3E, 0x6B, 0x5F, 0x6B, 0x3E},
/* 0x03 */ {0x0C, 0x1E, 0x3C, 0x1E, 0x0C},
/* 0x04 */ {0x08, 0x1C, 0x3E, 0x1C, 0x08},
/* 0x05 */ {0x08, 0x7c, 0x0e, 0x7c, 0x08}, /* SHIFT char */
/* 0x06 */ {0x18, 0x5C, 0x7E, 0x5C, 0x18},
/* 0x07 */ {0x3E, 0x36, 0x2A, 0x36, 0x3E},
/* 0x08 */ {0x08, 0x1c, 0x3e, 0x08, 0x08}, /* BACKSPACE char */
/* 0x09 */ {0x3E, 0x36, 0x2A, 0x36, 0x3E},
/* 0x0A */ {0x3E, 0x36, 0x2A, 0x36, 0x3E},
/* 0x0B */ {0x3E, 0x36, 0x2A, 0x36, 0x3E},
/* 0x0C */ {0x3E, 0x36, 0x2A, 0x36, 0x3E},
/* 0x0D */ {0x10, 0x38, 0x7c, 0x10, 0x1e}, /* ENTER char */
/* 0x0E */ {0x3E, 0x36, 0x2A, 0x36, 0x3E},
/* 0x0F */ {0x3E, 0x36, 0x2A, 0x36, 0x3E},
/* 0x10 */ {0x3E, 0x36, 0x2A, 0x36, 0x3E},
/* 0x11 */ {0x3E, 0x36, 0x2A, 0x36, 0x3E},
/* 0x12 */ {0x3E, 0x36, 0x2A, 0x36, 0x3E},
/* 0x13 */ {0x3E, 0x36, 0x2A, 0x36, 0x3E},
/* 0x14 */ {0x3E, 0x36, 0x2A, 0x36, 0x3E},
/* 0x15 */ {0x3E, 0x36, 0x2A, 0x36, 0x3E},
/* 0x16 */ {0x3E, 0x36, 0x2A, 0x36, 0x3E},
/* 0x17 */ {0x3E, 0x36, 0x2A, 0x36, 0x3E},
/* 0x18 */ {0x3E, 0x36, 0x2A, 0x36, 0x3E},
/* 0x19 */ {0x3E, 0x36, 0x2A, 0x36, 0x3E},
/* 0x1A */ {0x3E, 0x36, 0x2A, 0x36, 0x3E},
/* 0x1B */ {0x3E, 0x36, 0x2A, 0x36, 0x3E},
/* 0x1C */ {0x3E, 0x36, 0x2A, 0x36, 0x3E},
/* 0x1D */ {0x3E, 0x36, 0x2A, 0x36, 0x3E},
/* 0x1E */ {0x3E, 0x36, 0x2A, 0x36, 0x3E},
/* 0x1F */ {0x3E, 0x36, 0x2A, 0x36, 0x3E},
/* 0x20 */ {0x00, 0x00, 0x00, 0x00, 0x00},
/* 0x21 */ {0x00, 0x00, 0x5F, 0x00, 0x00},
/* 0x22 */ {0x00, 0x07, 0x00, 0x07, 0x00},
/* 0x23 */ {0x14, 0x3E, 0x14, 0x3E, 0x14},
/* 0x24 */ {0x04, 0x2A, 0x7F, 0x2A, 0x10},
/* 0x25 */ {0x26, 0x16, 0x08, 0x34, 0x32},
/* 0x26 */ {0x36, 0x49, 0x59, 0x26, 0x50},
/* 0x27 */ {0x00, 0x00, 0x07, 0x00, 0x00},
/* 0x28 */ {0x00, 0x1C, 0x22, 0x41, 0x00},
/* 0x29 */ {0x00, 0x41, 0x22, 0x1C, 0x00},
/* 0x2A */ {0x2A, 0x1C, 0x7F, 0x1C, 0x2A},
/* 0x2B */ {0x08, 0x08, 0x3E, 0x08, 0x08},
/* 0x2C */ {0x00, 0x50, 0x30, 0x00, 0x00},
/* 0x2D */ {0x08, 0x08, 0x08, 0x08, 0x08},
/* 0x2E */ {0x00, 0x60, 0x60, 0x00, 0x00},
/* 0x2F */ {0x20, 0x10, 0x08, 0x04, 0x02},
/* 0x30 */ {0x3E, 0x51, 0x49, 0x45, 0x3E},
/* 0x31 */ {0x00, 0x42, 0x7F, 0x40, 0x00},
/* 0x32 */ {0x42, 0x61, 0x51, 0x49, 0x46},
/* 0x33 */ {0x21, 0x41, 0x45, 0x4B, 0x31},
/* 0x34 */ {0x18, 0x14, 0x12, 0x7F, 0x10},
/* 0x35 */ {0x27, 0x45, 0x45, 0x45, 0x39},
/* 0x36 */ {0x3C, 0x4A, 0x49, 0x49, 0x30},
/* 0x37 */ {0x01, 0x01, 0x79, 0x05, 0x03},
/* 0x38 */ {0x36, 0x49, 0x49, 0x49, 0x36},
/* 0x39 */ {0x06, 0x49, 0x49, 0x29, 0x1E},
/* 0x3A */ {0x00, 0x36, 0x36, 0x00, 0x00},
/* 0x3B */ {0x00, 0x56, 0x36, 0x00, 0x00},
/* 0x3C */ {0x08, 0x14, 0x22, 0x41, 0x00},
/* 0x3D */ {0x14, 0x14, 0x14, 0x14, 0x14},
/* 0x3E */ {0x41, 0x22, 0x14, 0x08, 0x00},
/* 0x3F */ {0x02, 0x01, 0x59, 0x05, 0x02},
/* 0x40 */ {0x1C, 0x2A, 0x36, 0x3E, 0x0C},
/* 0x41 */ {0x7E, 0x09, 0x09, 0x09, 0x7E},
/* 0x42 */ {0x7F, 0x49, 0x49, 0x49, 0x3E},
/* 0x43 */ {0x3E, 0x41, 0x41, 0x41, 0x22},
/* 0x44 */ {0x7F, 0x41, 0x41, 0x22, 0x1C},
/* 0x45 */ {0x7F, 0x49, 0x49, 0x49, 0x41},
/* 0x46 */ {0x7F, 0x09, 0x09, 0x09, 0x01},
/* 0x47 */ {0x3E, 0x41, 0x41, 0x49, 0x3A},
/* 0x48 */ {0x7F, 0x08, 0x08, 0x08, 0x7F},
/* 0x49 */ {0x00, 0x41, 0x7F, 0x41, 0x00},
/* 0x4A */ {0x20, 0x40, 0x41, 0x3F, 0x01},
/* 0x4B */ {0x7F, 0x08, 0x14, 0x22, 0x41},
/* 0x4C */ {0x7F, 0x40, 0x40, 0x40, 0x40},
/* 0x4D */ {0x7F, 0x02, 0x04, 0x02, 0x7F},
/* 0x4E */ {0x7F, 0x04, 0x08, 0x10, 0x7F},
/* 0x4F */ {0x3E, 0x41, 0x41, 0x41, 0x3E},
/* 0x50 */ {0x7F, 0x09, 0x09, 0x09, 0x06},
/* 0x51 */ {0x3E, 0x41, 0x51, 0x21, 0x5E},
/* 0x52 */ {0x7F, 0x09, 0x19, 0x29, 0x46},
/* 0x53 */ {0x26, 0x49, 0x49, 0x49, 0x32},
/* 0x54 */ {0x01, 0x01, 0x7F, 0x01, 0x01},
/* 0x55 */ {0x3F, 0x40, 0x40, 0x40, 0x3F},
/* 0x56 */ {0x1F, 0x20, 0x40, 0x20, 0x1F},
/* 0x57 */ {0x7F, 0x20, 0x18, 0x20, 0x7F},
/* 0x58 */ {0x63, 0x14, 0x08, 0x14, 0x63},
/* 0x59 */ {0x03, 0x04, 0x78, 0x04, 0x03},
/* 0x5A */ {0x61, 0x51, 0x49, 0x45, 0x43},
/* 0x5B */ {0x00, 0x7F, 0x41, 0x41, 0x00},
/* 0x5C */ {0x02, 0x04, 0x08, 0x10, 0x20},
/* 0x5D */ {0x00, 0x41, 0x41, 0x7F, 0x00},
/* 0x5E */ {0x04, 0x02, 0x01, 0x02, 0x04},
/* 0x5F */ {0x40, 0x40, 0x40, 0x40, 0x40},
/* 0x60 */ {0x00, 0x01, 0x02, 0x04, 0x00},
/* 0x61 */ {0x20, 0x54, 0x54, 0x54, 0x78},
/* 0x62 */ {0x7f, 0x48, 0x44, 0x44, 0x38},
/* 0x63 */ {0x30, 0x48, 0x48, 0x48, 0x20},
/* 0x64 */ {0x38, 0x44, 0x44, 0x48, 0x7f},
/* 0x65 */ {0x38, 0x54, 0x54, 0x54, 0x18},
/* 0x66 */ {0x08, 0x7e, 0x09, 0x09, 0x02},
/* 0x67 */ {0x0c, 0x52, 0x52, 0x52, 0x3e},
/* 0x68 */ {0x7f, 0x08, 0x04, 0x04, 0x78},
/* 0x69 */ {0x00, 0x44, 0x7d, 0x40, 0x00},
/* 0x6A */ {0x20, 0x40, 0x40, 0x3d, 0x00},
/* 0x6B */ {0x7f, 0x10, 0x28, 0x44, 0x00},
/* 0x6C */ {0x00, 0x41, 0x7f, 0x40, 0x00},
/* 0x6D */ {0x7c, 0x04, 0x18, 0x04, 0x78},
/* 0x6E */ {0x7c, 0x08, 0x04, 0x04, 0x78},
/* 0x6F */ {0x38, 0x44, 0x44, 0x44, 0x38},
/* 0x70 */ {0xfc, 0x14, 0x14, 0x14, 0x08},
/* 0x71 */ {0x08, 0x14, 0x14, 0x18, 0x7c},
/* 0x72 */ {0x7c, 0x08, 0x04, 0x04, 0x08},
/* 0x73 */ {0x48, 0x54, 0x54, 0x54, 0x20},
/* 0x74 */ {0x04, 0x3f, 0x44, 0x40, 0x20},
/* 0x75 */ {0x3c, 0x40, 0x40, 0x20, 0x7c},
/* 0x76 */ {0x1c, 0x20, 0x40, 0x20, 0x1c},
/* 0x77 */ {0x3c, 0x40, 0x38, 0x40, 0x3c},
/* 0x78 */ {0x44, 0x28, 0x10, 0x28, 0x44},
/* 0x79 */ {0x0c, 0x50, 0x50, 0x50, 0x3c},
/* 0x7A */ {0x44, 0x64, 0x54, 0x4c, 0x44},
/* 0x7B */ {0x00, 0x08, 0x36, 0x41, 0x00},
/* 0x7C */ {0x00, 0x00, 0x7F, 0x00, 0x00},
/* 0x7D */ {0x00, 0x41, 0x36, 0x08, 0x00},
/* 0x7E */ {0x08, 0x06, 0x08, 0x30, 0x08},
/* 0x7F */ {0x3E, 0x36, 0x2A, 0x36, 0x3E},
}};
static const U8 (*font)[FONT_WIDTH] = font_array.font;

int display_update_time = 0;
int display_auto_update = 1;

void
display_update(void)
{
  if(display_auto_update)
    display_update_time = get_sys_time() + 250;
  nxt_lcd_update();
}

void display_set_auto_update(int mode)
{
  // Enable/disable automatic refresh of the display.
  display_auto_update = mode;
  if (mode == 0)
    display_update_time = 0x7fffffff;
  else
    display_update_time = 0;
}
  

void display_force_update(void)
{
  // Force a display update even if interrupts are disabled
  nxt_lcd_force_update();
}


void
display_clear(U32 updateToo)
{
  //memset(display_buffer, 0, sizeof(display_buffer));
  memset(display_buffer, 0, DISPLAY_WIDTH*DISPLAY_DEPTH);
  if (updateToo)
    display_update();
}


int display_x;
int display_y;

/* text functions, top left is (0,0) */
void
display_goto_xy(int x, int y)
{
  display_x = x;
  display_y = y;
}

void
display_char(int c)
{
  U8 *b;
  const U8 *f, *fend;

  if ((uint) c < N_CHARS &&
      (uint) display_x < DISPLAY_CHAR_WIDTH &&
      (uint) display_y < DISPLAY_CHAR_DEPTH) {
    b = &display_buffer[display_y][display_x * CELL_WIDTH];
    f = font[c];
    fend = f + FONT_WIDTH;

    do {
      *b++ = *f++;
    } while( f < fend);
  }
}

void
display_string(const char *str)
{
  while (*str) {
    if (*str != '\n') {
      display_char(*str);
      display_x++;
    } else {
      display_x = 0;
      display_y++;
    }
    str++;
  }
}

void
display_hex(U32 val, U32 places)
{
  char x[9];

  char *p = &x[8];
  int p_count = 0;


  *p = 0;

  if (places > 8)
    places = 8;

  while (val) {
    p--;
    p_count++;
    *p = "0123456789ABCDEF"[val & 0x0f];
    val >>= 4;
  }

  while (p_count < places) {
    p--;
    p_count++;
    *p = '0';
  }

  display_string(p);
}

static void
display_unsigned_worker(U32 val, U32 places, U32 sign)
{
  char x[12];			// enough for 10 digits + sign + NULL 
  char *p = &x[11];
  int p_count = 0;
  U32 val0;

  *p = 0;

  if (places > 11)
    places = 11;

  while (val) {
    p--;
    p_count++;
    val0 = val;
    val /= 10;
    *p = (val0 - val * 10) + '0';
  }

  if (!p_count) {
    p--;
    p_count++;
    *p = '0';
  }

  if (sign) {
    p--;
    p_count++;
    *p = '-';
  }

  while (p_count < places) {
    p--;
    p_count++;
    *p = ' ';
  }

  display_string(p);
}

void
display_unsigned(U32 val, U32 places)
{
  display_unsigned_worker(val, places, 0);
}

void
display_int(int val, U32 places)
{
  display_unsigned_worker((val < 0) ? -val : val, places, (val < 0));
}

void
display_bitmap_copy(const U8 *data, U32 width, U32 depth, U32 x, U32 y)
{
  display_bitblt((byte *)data, width, depth*8, 0, 0, (byte *)display_buffer, DISPLAY_WIDTH, DISPLAY_DEPTH*8, x, y*8, width, depth*8, 0x0000ff00); 
/*
  U32 i;
  U32 j;
  U32 dy;
  U32 dx;

  for (i = 0; i < depth; i++)
    for (j = 0; j < width; j++) {
      dx = x + j;
      dy = y + i;

      if (dx < DISPLAY_WIDTH && dy < DISPLAY_DEPTH)
        display_buffer[y + i][x + j] = data[width * i + j];
    }*/
}

void display_bitblt(byte *src, int sw, int sh, int sx, int sy, byte *dst, int dw, int dh, int dx, int dy, int w, int h, int rop)
{
  /* This is a partial implementation of the BitBlt algorithm. It provides a
   * complete set of raster operations and handles partial and fully aligned
   * images correctly. Overlapping source and destination images is also 
   * supported. It does not performing mirroring. The code was converted
   * from an initial Java implementation and has not been optimized for C.
   * The genral mechanism is to perform the block copy with Y as the inner
   * loop (because on the display the bits are packed y-wise into a byte). We
   * perform the various rop cases by reducing the operation to a series of
   * AND and XOR operations. Each step is controlled by a byte in the rop code.
   * This mechanism is based upon that used in the X Windows system server.
   */
  // Clip to source and destination
  int trim;
  if (dx < 0)
  {
    trim = -dx;
    dx = 0;
    sx += trim;
    w -= trim;
  }
  if (dy < 0)
  {
    trim = -dy;
    dy = 0;
    sy += trim;
    h -= trim;
  }
  if (sx < 0 || sy < 0) return;
  if (dx + w > dw) w = dw - dx;
  if (sx + w > sw) w = sw - sx;
  if (w <= 0) return;
  if (dy + h > dh) h = dh - dy;
  if (sy + h > sh) h = sh - sy;
  if (h <= 0) return;
  // Setup initial parameters and check for overlapping copy
  int xinc = 1;
  int yinc = 1;
  byte firstBit = 1;
  if (src == dst)
  {
    // If copy overlaps we use reverse direction
    if (dy > sy)
    {
      sy = sy + h - 1;
      dy = dy + h - 1;
      firstBit = (byte)0x80;
      yinc = -1;
    }
    if (dx > sx)
    {
      xinc = -1;
      sx = sx + w - 1;
      dx = dx + w - 1;
    }
  }
  int inStart = (sy/8)*sw;
  int outStart = (dy/8)*dw;
  byte inStartBit = (byte)(1 << (sy & 0x7));
  byte outStartBit = (byte)(1 << (dy & 0x7));
  dw *= yinc;
  sw *= yinc;
  // Extract rop sub-fields
  byte ca1 = (byte)(rop >> 24);
  byte cx1 = (byte)(rop >> 16);
  byte ca2 = (byte)(rop >> 8);
  byte cx2 = (byte) rop;
  boolean noDst = (ca1 == 0 && cx1 == 0);
  int xcnt;
  // Check for byte aligned case and optimise for it
  if (h >= 8 && inStartBit == firstBit && outStartBit == firstBit)
  {
    int ix = sx;
    int ox = dx;
    int byteCnt = h/8;
    xcnt = w;
    while (xcnt-- > 0)
    {
      int inIndex = inStart + ix;
      int outIndex = outStart + ox;
      int cnt = byteCnt;
      while(cnt-- > 0)
      {
        if (noDst)
          dst[outIndex] = (byte)((src[inIndex] & ca2)^cx2);            
        else
        {
          byte inVal = src[inIndex];
          dst[outIndex] = (byte)((dst[outIndex] & ((inVal & ca1)^cx1)) ^ ((inVal & ca2)^cx2));
        }
        outIndex += dw;
        inIndex += sw;
      }
      ox += xinc;
      ix += xinc;
    }
    // Do we have a final non byte multiple to do?
    h &= 0x7;
    if (h == 0) return;
    outStart += (byteCnt*dw);
    inStart += (byteCnt*sw);
  }
  // General non byte aligned case
  int ix = sx;
  int ox = dx;
  xcnt = w;
  while(xcnt-- > 0)
  {
    int inIndex = inStart + ix;
    byte inBit = inStartBit;
    byte inVal = src[inIndex];
    byte inAnd = (byte)((inVal & ca1)^cx1);
    byte inXor = (byte)((inVal & ca2)^cx2);
    int outIndex = outStart + ox;
    byte outBit = outStartBit;
    byte outPixels = dst[outIndex];
    int cnt = h;
    while(true)
    {
      if (noDst)
      {
        if ((inXor & inBit) != 0)
          outPixels |= outBit;
        else
          outPixels &= ~outBit;
      }
      else
      {
        byte resBit = (byte)((outPixels & ((inAnd & inBit) != 0 ? outBit : 0))^((inXor & inBit) != 0 ? outBit : 0));
        outPixels = (byte)((outPixels & ~outBit) | resBit);
      }
      if (--cnt <= 0) break;
      if (yinc > 0)
      {
        inBit <<= 1;
        outBit <<= 1;
      }
      else
      {
        inBit >>= 1;
        outBit >>= 1;
      }
      if (inBit == 0)
      {
        inBit = firstBit;
        inIndex += sw;
        inVal = src[inIndex];
        inAnd = (byte)((inVal & ca1)^cx1);
        inXor = (byte)((inVal & ca2)^cx2);
      }
      if (outBit == 0)
      {
        dst[outIndex] = outPixels;
        outBit = firstBit;
        outIndex += dw;
        outPixels = dst[outIndex];
      }
    }
    dst[outIndex] = outPixels;
    ix += xinc;
    ox += xinc;
  }
}

U8 *
display_get_buffer(void)
{
  return (U8 *)display_buffer;
}

STACKWORD
display_get_array(void)
{
  return (STACKWORD)ptr2word(&display_array);
}

STACKWORD
display_get_font(void)
{
  return (STACKWORD)ptr2word(&font_array);
}

void
display_init(void)
{
  // Initialise the array parameters so that the display can
  // be memory mapped into the Java address space
  // NOTE This object must always be marked, otherwise very, very bad
  // things will happen!
  display_array.arrayHdr.hdr.flags.mark = 3;
  display_array.arrayHdr.hdr.flags.length = LEN_BIGARRAY;
  display_array.arrayHdr.hdr.flags.class = T_BYTE + ALJAVA_LANG_OBJECT;
  display_array.arrayHdr.hdr.sync.monitorCount = 0;
  display_array.arrayHdr.hdr.sync.threadId = 0;
  display_array.arrayHdr.length = DISPLAY_DEPTH*DISPLAY_WIDTH;
/*
  // We use the same trick for the font characters
  font_array.arrayHdr.hdr.flags.arrays.isArray = 1;
  // NOTE This object must always be marked, otherwise very, very bad
  // things will happen!
  font_array.arrayHdr.hdr.flags.arrays.mark = 1;
  font_array.arrayHdr.hdr.flags.arrays.length = BIGARRAYLEN;
  font_array.arrayHdr.hdr.flags.arrays.isAllocated = 1;
  font_array.arrayHdr.hdr.flags.arrays.type = T_BYTE;
  font_array.arrayHdr.hdr.monitorCount = 0;
  font_array.arrayHdr.hdr.threadId = 0;
  font_array.arrayHdr.length = sizeof(font_array.font);
*/
  display_clear(0);
  display_auto_update = 1;
  nxt_lcd_init((U8 *)display_buffer);
}

#if 0
void
display_test(void)
{
  int iterator = 0;

  nxt_lcd_init((U8 *)display_buffer);
  while (1) {
    display_clear(0);
    display_goto_xy(iterator, 0);
    display_string("LEJOS NXT");
    display_goto_xy(0, 1);
    display_string("0123456789.:/");
    display_goto_xy(0, 2);
    display_string("abcdefghijklm");
    display_goto_xy(0, 3);
    display_string("nopqrstuvwxyz");
    display_goto_xy(0, 4);
    display_string("ABCDEFGHIJKLM");
    display_goto_xy(0, 5);
    display_string("NOPQRSTUVWXYZ");

    display_goto_xy(0, 7);
    display_string("TIME ");
    display_unsigned(systick_get_ms(), 0);
    iterator = (iterator + 1) & 7;
    display_update();
    systick_wait_ms(2000);
  }
}
#endif

/**
 * High Speed / RS485 Interface
 * This module provides basic access to the NXT RS485 hardware
 * Includes additional routines to perform lowlevel BitBus packet I/O
 * functions.
 * Author: Andy Shaw
 */
#include "mytypes.h"
#include "AT91SAM7.h"
#include "hs.h"
#include "aic.h"
#include  <string.h>
#include "display.h"
#include "systick.h"
#include "memory.h"
#include "sensors.h"
#include "usart.h"

// Max data size
#define BUFSZ 128
// Extra bytes needed for packet header etc.
#define EXTRA 6
// max size of a a packet assuming worse case byte stuffing
#define MAXBUF ((BUFSZ+EXTRA)*2)
#define IN_BUF_SZ (MAXBUF/2)
#define OUT_BUF_SZ MAXBUF
#define BAUD_RATE 921600

	
usart *hs;

/**
 * Enable the high speed RS485 interface, using the specified baud rate and
 * buffer size. If the bause rate or buffer size is specified as zero use
 * values that are suitable for BitBus transactions.
 */
int hs_enable(int baud, int buf_sz)
{
  if (baud == 0) baud = BAUD_RATE;
  if (buf_sz == 0) buf_sz = MAXBUF;
  if (hs == NULL)
  {
    hs = usart_allocate(AT91C_BASE_US0,AT91C_BASE_PDC_US0, buf_sz/2, buf_sz);
    if (hs == NULL) return 0;
  }
  
  // Enable power to the device
  *AT91C_PMC_PCER = (1 << AT91C_ID_US0); 
  
  // Disable pull ups
  *AT91C_PIOA_PPUDR = HS_RX_PIN | HS_TX_PIN | HS_RTS_PIN; 
  // Disable PIO A on I/O lines */
  *AT91C_PIOA_PDR = HS_RX_PIN | HS_TX_PIN | HS_RTS_PIN; 
  // Enable device control
  *AT91C_PIOA_ASR = HS_RX_PIN | HS_TX_PIN | HS_RTS_PIN; 
  // Now program up the device
  *AT91C_US0_CR   = AT91C_US_RSTSTA;
  *AT91C_US0_CR   = AT91C_US_STTTO;
  *AT91C_US0_RTOR = 2400; 
  *AT91C_US0_IDR  = AT91C_US_TIMEOUT;
  *AT91C_US0_MR = AT91C_US_USMODE_RS485;
  *AT91C_US0_MR &= ~AT91C_US_SYNC;
  *AT91C_US0_MR |= AT91C_US_CLKS_CLOCK | AT91C_US_CHRL_8_BITS | AT91C_US_PAR_NONE | AT91C_US_NBSTOP_1_BIT | AT91C_US_OVER;
  *AT91C_US0_BRGR = ((CLOCK_FREQUENCY/8/baud) | (((CLOCK_FREQUENCY/8) - ((CLOCK_FREQUENCY/8/baud) * baud)) / ((baud + 4)/8)) << 16);

  aic_mask_off(AT91C_ID_US0);
  aic_clear(AT91C_ID_US0);
  usart_enable(hs);

  return 1;
}

void hs_disable(void)
{
  if (hs != NULL) usart_free(hs);
  hs = NULL;
  // Turn off the device and make the pins available for other uses
  *AT91C_PMC_PCDR = (1 << AT91C_ID_US0);
  sp_reset(RS485_PORT);
}

void hs_init(void)
{
  // Initial state is off
  hs = NULL;
  hs_disable();
}

U32 hs_write(U8 *buf, U32 off, U32 len)
{
  return usart_write(hs, buf, off, len);
}

U32 hs_pending()
{
  return usart_status(hs);
}



U32 hs_read(U8 * buf, U32 off, U32 len)
{
  return usart_read(hs, buf, off, len);
}

// The following provides a set of low level BitBus frame I/O routines. These
// functions are C versions of the original Java routines

// Packet construction constants
#define BB_FLAG 0x7e
#define BB_ESCAPE 0x7d
#define BB_XOR 0x20
#define CRC_INIT 0xffff
#define ST_FLAG  0
#define ST_ESCAPE  1
#define ST_DATA  2

// "Class vars"
U8 *frame; // pointer to he curent frame
U16 frameCRC; // Accumulated CRC value
U32 frameLen; // current frame length
U32 state; // input state
U16 *CRCTable; // pointer to initialised CRC lookup table

/**
 * Add a single byte to the current frame. Include the value in the CRC. Byte
 * stuff if needed.
 */
static void addByte(U8 b)
{
  //RConsole.println("Add byte " + b + " len " + frameLen + " max " + frame.length);
  // update crc
  frameCRC = (U16)((frameCRC << 8) ^ CRCTable[(b ^ (frameCRC >> 8)) & 0xff]);
  // Byte stuff?
  if (b == BB_FLAG || b == BB_ESCAPE)
  {
    frame[frameLen++] = BB_ESCAPE;
    frame[frameLen++] = (U8)(b ^ BB_XOR);
  }
  else
    frame[frameLen++] = b;
}

/**
 * Add a series of bytes to the current frame, add to CRC, byte stuff if needed
 */
static void addBytes(U8 *data, int len)
{
  while (len-- > 0)
  {
    U8 b = *data++;
    frameCRC = (U16)((frameCRC << 8) ^ CRCTable[(b ^ (frameCRC >> 8)) & 0xff]);
    if (b == BB_FLAG || b == BB_ESCAPE)
    {
      frame[frameLen++] = BB_ESCAPE;
      frame[frameLen++] = (U8)(b ^ BB_XOR);
    }
    else
      frame[frameLen++] = (U8)b;
  }
}

/**
 * Add the CRC value (FCS Frame Check Sum). Note this value must be byte stuffed
 * but must not impact the actual CRC.
 */
static void addFCS(U16 FCS)
{
  addByte((U8)(FCS >> 8));
  addByte((U8)FCS);
}

/**
 * Create and send a frame.
 */
int hs_send(U8 address, U8 control, U8 *data, int offset, int len, U16 *CRCTab)
{
  // Make sure we have room
  frame = usart_get_write_buffer(hs);
  if (!frame) return 0;
  // Set things up.
  CRCTable = CRCTab;
  // Create the frame
  frameCRC = CRC_INIT;
  frameLen = 0;
  // Framing character
  frame[frameLen++] = BB_FLAG;
  // Header
  addByte(address);
  addByte(control);
  // Data
  addBytes(data+offset, len);
  addFCS(frameCRC);
  // Framing character
  frame[frameLen++] = BB_FLAG;
  // Send the data
  usart_write_buffer(hs, frameLen);
  return frameLen;
}

/**
 * Assemble and return a packet. Uses a state machine to track packet
 * content. Return
 * > 0 packet length
 * < 0 packet not yet started. 
 * == 0 packet being assembled but not yet complete.
 */
int hs_recv(U8 *data, int len, U16 *CRCTab, int reset)
{
  U8 cur;

  // Set things up
  frame = data;
  CRCTable = CRCTab;
  // If we have timed out we may need to reset.
  if (reset) state = ST_FLAG;
  while (usart_read(hs, &cur, 0, 1) > 0)
  {
    switch(state)
    {
      case ST_FLAG:
        // Waiting for packet start
        if (cur == BB_FLAG)
        {
          frameLen = 0;
          frameCRC = CRC_INIT;
          state = ST_DATA;
        }
        break;
      case ST_ESCAPE:
        // Previous byte was an escape, so escap current byte
        cur ^= BB_XOR;
        if (frameLen >= len)
          state = ST_FLAG;
        else
        {
          // Add the byte into the frame.
          frame[frameLen++] = (U8)cur;
          frameCRC = (U16)((frameCRC << 8) ^ CRCTable[(cur ^ (frameCRC >> 8)) & 0xff]);
        }
        state = ST_DATA;
        break;
      case ST_DATA:
        // Check for end of frame
        if (cur == BB_FLAG)
        {
          // Check that we have a good CRC
          state = ST_FLAG;
          if (frameCRC == 0) 
            return frameLen;
        }
        else if (cur == BB_ESCAPE)
          state = ST_ESCAPE;
        else if (frameLen >= len)
          state = ST_FLAG;
        else
        {
          frame[frameLen++] = (U8)cur;
          frameCRC = (U16)((frameCRC << 8) ^ CRCTable[(cur ^ (frameCRC >> 8)) & 0xff]);
        }
        break;
    }
  }
  return state != ST_FLAG ? 0 : -1;
}


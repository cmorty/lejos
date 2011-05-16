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

// Buffer sizes etc,
// NOTE: The input code for this device assumes that 2 buffers are in use.
// Max data size
#define BUFSZ 128
// Extra bytes needed for packet header etc.
#define EXTRA 6
// max size of a a packet assuming worse case byte stuffing
#define MAXBUF ((BUFSZ+EXTRA)*2)
#define IN_BUF_SZ (MAXBUF/2)
#define OUT_BUF_SZ MAXBUF
#define IN_BUF_CNT 2
#define OUT_BUF_CNT 2
#define BAUD_RATE 921600
static U8 *in_buf[IN_BUF_CNT];
static U8 *out_buf[OUT_BUF_CNT];
static U8 in_buf_in_ptr, out_buf_ptr;

static U8* buf_ptr;

static int in_buf_idx = 0;

	
int hs_enable(void)
{
  U8 trash;
  if (in_buf[0] == NULL)
  {
    // do memory allocation for buffer space
    U8 *mem = system_allocate(IN_BUF_CNT*IN_BUF_SZ+OUT_BUF_CNT*OUT_BUF_SZ);
    if (mem == NULL) return 0;
    in_buf[0] = mem;
    in_buf[1] = mem + IN_BUF_SZ;
    out_buf[0] = mem + 2*IN_BUF_SZ;
    out_buf[1] = mem + 2*IN_BUF_SZ + OUT_BUF_SZ;
  }

  // Initialize the device
  in_buf_in_ptr = out_buf_ptr = 0; 
  in_buf_idx = 0;
  
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
  *AT91C_US0_BRGR = ((CLOCK_FREQUENCY/8/BAUD_RATE) | (((CLOCK_FREQUENCY/8) - ((CLOCK_FREQUENCY/8/BAUD_RATE) * BAUD_RATE)) / ((BAUD_RATE + 4)/8)) << 16);
  *AT91C_US0_PTCR = (AT91C_PDC_RXTDIS | AT91C_PDC_TXTDIS); 
  *AT91C_US0_RCR  = 0; 
  *AT91C_US0_TCR  = 0; 
  *AT91C_US0_RNPR = 0;
  *AT91C_US0_TNPR = 0;
  
  aic_mask_off(AT91C_ID_US0);
  aic_clear(AT91C_ID_US0);

  trash = *AT91C_US0_RHR;
  trash = *AT91C_US0_CSR;
  
  *AT91C_US0_RPR  = (unsigned int)&(in_buf[0][0]); 
  *AT91C_US0_RCR  = IN_BUF_SZ;
  *AT91C_US0_RNPR = (unsigned int)&(in_buf[1][0]);
  *AT91C_US0_RNCR = IN_BUF_SZ;
  *AT91C_US0_CR   = AT91C_US_RXEN | AT91C_US_TXEN; 
  *AT91C_US0_PTCR = (AT91C_PDC_RXTEN | AT91C_PDC_TXTEN); 
  
  buf_ptr = &(in_buf[0][0]);
  return 1;
}

void hs_disable(void)
{
  // Turn off the device and make the pins available for other uses
  *AT91C_PMC_PCDR = (1 << AT91C_ID_US0);
  sp_reset(RS485_PORT);
  // free memory if we have any
  if (in_buf[0] != NULL) system_free((byte*)in_buf[0]);
  in_buf[0] = NULL;
}

void hs_init(void)
{
  // Initial state is off
  in_buf[0] = NULL;
  hs_disable();
}

U32 hs_write(U8 *buf, U32 off, U32 len)
{
  // Write data to the device. Return the number of bytes written
  if (*AT91C_US0_TNCR == 0)
  {	
    if (len > OUT_BUF_SZ) len = OUT_BUF_SZ;	
    memcpy(&(out_buf[out_buf_ptr][0]), buf+off, len);
    *AT91C_US0_TNPR = (unsigned int) &(out_buf[out_buf_ptr][0]);
    *AT91C_US0_TNCR = len;
    out_buf_ptr = (out_buf_ptr+1) % OUT_BUF_CNT;
    return len;
  }
  else
    return 0;
}

U32 hs_pending()
{
  // return the state of any pending i/o requests one bit for input one bit
  // for output.
  // First check for any input
  int ret = 0;
  int bytes_ready;
  if (*AT91C_US0_RNCR == 0) 
    bytes_ready = IN_BUF_SZ*2 - *AT91C_US0_RCR;
  else 
    bytes_ready = IN_BUF_SZ - *AT91C_US0_RCR;
  if (bytes_ready  > in_buf_idx) ret |= 1;
  if ((*AT91C_US0_TCR != 0) || (*AT91C_US0_TNCR != 0)) ret |= 2;
  return ret;
}



U32 hs_read(U8 * buf, U32 off, U32 len)
{
  int bytes_ready, total_bytes_ready;
  int cmd_len, i;
  U8* tmp_ptr;
  
  cmd_len = 0;
  if (*AT91C_US0_RNCR == 0) {
    bytes_ready = IN_BUF_SZ;
    total_bytes_ready = IN_BUF_SZ*2 - *AT91C_US0_RCR;
  }
  else
    total_bytes_ready = bytes_ready = IN_BUF_SZ - *AT91C_US0_RCR;
  
  if (total_bytes_ready > in_buf_idx)
  {
    cmd_len = (int) (total_bytes_ready - in_buf_idx);
    if (cmd_len > len) cmd_len = len;
  	
    if (bytes_ready >= in_buf_idx + cmd_len)
    { 	
      for(i=0;i<cmd_len;i++) buf[off+i] = buf_ptr[in_buf_idx++];
    }
    else
    {
      for(i=0;i<cmd_len && in_buf_idx < IN_BUF_SZ;i++) buf[off+i] = buf_ptr[in_buf_idx++];
      in_buf_idx = 0;
      tmp_ptr = &(in_buf[(in_buf_in_ptr+1)%2][0]);
      for(;i<cmd_len;i++) buf[off+i] = tmp_ptr[in_buf_idx++];
      in_buf_idx += IN_BUF_SZ;
    } 
  }
  
  // Current buffer full and fully processed
  
  if (in_buf_idx >= IN_BUF_SZ && *AT91C_US0_RNCR == 0)
  { 	
    // Switch current buffer, and set up next 
    in_buf_idx -= IN_BUF_SZ;
    *AT91C_US0_RNPR = (unsigned int) buf_ptr;
    *AT91C_US0_RNCR = IN_BUF_SZ;
    in_buf_in_ptr = (in_buf_in_ptr+1) % IN_BUF_CNT;
    buf_ptr = &(in_buf[in_buf_in_ptr][0]);
  }
  return cmd_len;   
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
  if (*AT91C_US0_TNCR != 0) return 0;
  // Set things up.
  CRCTable = CRCTab;
  frame = out_buf[out_buf_ptr];
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
  *AT91C_US0_TNPR = (unsigned int) &(out_buf[out_buf_ptr][0]);
  *AT91C_US0_TNCR = frameLen;
  out_buf_ptr = (out_buf_ptr+1) % OUT_BUF_CNT;
  return frameLen;
}

/**
 * Return a single byte from the input queue. return -1 if no data avalable.
 */
int getByte()
{
  int bytes_ready, total_bytes_ready;
  int ret;
  U8* tmp_ptr;
  
  // Assume no data
  ret = -1;
  // first buffer al used up?
  if (*AT91C_US0_RNCR == 0) {
    bytes_ready = IN_BUF_SZ;
    total_bytes_ready = IN_BUF_SZ*2 - *AT91C_US0_RCR;
  }
  else
    total_bytes_ready = bytes_ready = IN_BUF_SZ - *AT91C_US0_RCR;
  
  // Anything new?
  if (total_bytes_ready > in_buf_idx)
  {
    if (bytes_ready >= in_buf_idx + 1)
    { 	
      ret = buf_ptr[in_buf_idx++];
    }
    else
    {
      // Can this ever happen?
      in_buf_idx = 0;
      tmp_ptr = &(in_buf[(in_buf_in_ptr+1)%2][0]);
      ret = tmp_ptr[in_buf_idx++];
      in_buf_idx += IN_BUF_SZ;
    } 
  }
  
  // Current buffer full and fully processed?
  
  if (in_buf_idx >= IN_BUF_SZ && *AT91C_US0_RNCR == 0)
  { 	
    // Switch current buffer, and set up next 
    in_buf_idx -= IN_BUF_SZ;
    *AT91C_US0_RNPR = (unsigned int) buf_ptr;
    *AT91C_US0_RNCR = IN_BUF_SZ;
    in_buf_in_ptr = (in_buf_in_ptr+1) % IN_BUF_CNT;
    buf_ptr = &(in_buf[in_buf_in_ptr][0]);
  }
  return ret;   
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
  int cur;

  // Set things up
  frame = data;
  CRCTable = CRCTab;
  // If we have timed out we may need to reset.
  if (reset) state = ST_FLAG;
  while ((cur = getByte()) >= 0)
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


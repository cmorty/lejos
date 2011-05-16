#include "mytypes.h"
#include "AT91SAM7.h"
#include "uart.h"
#include "bt.h"
#include "aic.h"
#include  <string.h>
#include "display.h"
#include "systick.h"
#include "memory.h"

#define IN_BUF_SZ 128
#define OUT_BUF_SZ 256
#define IN_BUF_CNT 2
#define OUT_BUF_CNT 2
static U8 *in_buf[IN_BUF_CNT];
static U8 *out_buf[OUT_BUF_CNT];
static U8 in_buf_in_ptr, out_buf_ptr;

static U8* buf_ptr;

static int in_buf_idx = 0;

#define BAUD_RATE 460800
// Update rate (per second) for the ADC
#define ADC_UPDATE_RATE 50000 

// Bit values used for events
#define BT_READABLE   0x1
#define BT_WRITEABLE  0x2
#define BT_WRITEEMPTY 0x4
#define BT_CMDMODE    0x10
#define BT_STREAMMODE 0x20


	
void bt_init(void)
{
  U32 trash;

  // Configure the Usart
  *AT91C_PMC_PCER = (1 << AT91C_ID_US1); 
  *AT91C_PIOA_PDR = BT_RX_PIN | BT_TX_PIN | BT_SCK_PIN | BT_RTS_PIN | BT_CTS_PIN; 
  *AT91C_PIOA_ASR = BT_RX_PIN | BT_TX_PIN | BT_SCK_PIN | BT_RTS_PIN | BT_CTS_PIN; 
  *AT91C_US1_CR   = AT91C_US_RSTSTA|AT91C_US_RXDIS|AT91C_US_TXDIS;
  *AT91C_US1_CR   = AT91C_US_STTTO;
  *AT91C_US1_RTOR = 10000; 
  *AT91C_US1_IDR  = AT91C_US_TIMEOUT;
  *AT91C_US1_MR = (AT91C_US_USMODE_HWHSH & ~AT91C_US_SYNC) | AT91C_US_CLKS_CLOCK | AT91C_US_CHRL_8_BITS | AT91C_US_PAR_NONE | AT91C_US_NBSTOP_1_BIT | AT91C_US_OVER;
  *AT91C_US1_BRGR = ((CLOCK_FREQUENCY/8/BAUD_RATE) | (((CLOCK_FREQUENCY/8) - ((CLOCK_FREQUENCY/8/BAUD_RATE) * BAUD_RATE)) / ((BAUD_RATE + 4)/8)) << 16);
  *AT91C_US1_PTCR = (AT91C_PDC_RXTDIS | AT91C_PDC_TXTDIS); 
  *AT91C_US1_RCR  = 0; 
  *AT91C_US1_TCR  = 0; 
  *AT91C_US1_RNPR = 0;
  *AT91C_US1_TNPR = 0;
  
  aic_mask_off(AT91C_ID_US1);
  aic_clear(AT91C_ID_US1);

  // Configure the control lines
  *AT91C_PIOA_PER   = BT_CS_PIN | BT_RST_PIN; 
  *AT91C_PIOA_OER   = BT_CS_PIN | BT_RST_PIN; 
  *AT91C_PIOA_SODR  = BT_CS_PIN;
  *AT91C_PIOA_CODR  = BT_RST_PIN;
  *AT91C_PIOA_PPUDR = BT_ARM7_CMD_PIN;
  *AT91C_PIOA_PER   = BT_ARM7_CMD_PIN; 
  *AT91C_PIOA_CODR  = BT_ARM7_CMD_PIN;
  *AT91C_PIOA_OER   = BT_ARM7_CMD_PIN; 

  // Configure timer 01 as trigger for ADC
  *AT91C_PMC_PCER = (1 << AT91C_ID_TC1); 
  *AT91C_TC1_CCR = AT91C_TC_CLKDIS;
  *AT91C_TC1_IDR = ~0;
  trash = *AT91C_TC1_SR;
  *AT91C_TC1_CMR = AT91C_TC_WAVE | AT91C_TC_WAVESEL_UP_AUTO | AT91C_TC_ACPA_SET | AT91C_TC_ACPC_CLEAR | AT91C_TC_ASWTRG_SET; /* MCLK/2, wave mode 10 */
  *AT91C_TC1_RC = (CLOCK_FREQUENCY/2)/(ADC_UPDATE_RATE);
  *AT91C_TC1_RA = ((CLOCK_FREQUENCY/2)/(ADC_UPDATE_RATE))/2;
  *AT91C_TC1_CCR = AT91C_TC_CLKEN;
  *AT91C_TC1_CCR = AT91C_TC_SWTRG;

  // Configure the ADC
  *AT91C_PMC_PCER = (1 << AT91C_ID_ADC); 
  *AT91C_ADC_MR  = 0;
  *AT91C_ADC_MR |= AT91C_ADC_TRGEN_EN | AT91C_ADC_TRGSEL_TIOA1;
  //*AT91C_ADC_MR |= 0x00003F00; // 375KHz
  *AT91C_ADC_MR |= 0x00000500; // 4MHz
  // *AT91C_ADC_MR |= 0x00020000; // 64uS
  *AT91C_ADC_MR |= 0x001f0000; // 64uS
  // *AT91C_ADC_MR |= 0x09000000; // 24uS
  *AT91C_ADC_MR |= 0x03000000; // 750nS
  *AT91C_ADC_CHER  = AT91C_ADC_CH6 | AT91C_ADC_CH4; 
  in_buf[0] = NULL;
}

int bt_enable(void)
{
  U8 trash;
  if (!in_buf[0])
  {
    // Allocate buffer memory
    U8 *mem = system_allocate(IN_BUF_SZ*IN_BUF_CNT + OUT_BUF_SZ*OUT_BUF_CNT);
    if (!mem) return 0;
    in_buf[0] = mem;
    in_buf[1] = mem + IN_BUF_SZ;
    out_buf[0] = mem + IN_BUF_SZ*2;
    out_buf[1] = mem + IN_BUF_SZ*2 + OUT_BUF_SZ;
  }
  in_buf_in_ptr = out_buf_ptr = 0; 
  in_buf_idx = 0;
  
  trash = *AT91C_US1_RHR;
  trash = *AT91C_US1_CSR;
  
  *AT91C_US1_RPR  = (unsigned int)&(in_buf[0][0]); 
  *AT91C_US1_RCR  = 128;
  *AT91C_US1_RNPR = (unsigned int)&(in_buf[1][0]);
  *AT91C_US1_RNCR = 128;
  *AT91C_US1_CR   = AT91C_US_RXEN | AT91C_US_TXEN; 
  *AT91C_US1_PTCR = (AT91C_PDC_RXTEN | AT91C_PDC_TXTEN); 
  
  buf_ptr = &(in_buf[0][0]);
  return 1;
}

void bt_disable()
{
  *AT91C_US1_CR   = AT91C_US_RXDIS|AT91C_US_TXDIS;
  *AT91C_US1_PTCR = (AT91C_PDC_RXTDIS | AT91C_PDC_TXTDIS); 
  *AT91C_US1_RCR  = 0; 
  *AT91C_US1_TCR  = 0; 
  *AT91C_US1_RNPR = 0;
  *AT91C_US1_TNPR = 0;
  if (in_buf[0]) system_free((byte *)in_buf[0]);
  in_buf[0] = NULL;
}
  

U32 bt_get_mode()
{
  // return the bt4 mode value.
  return (U32) *AT91C_ADC_CDR6;
}


U32 bt_write(U8 *buf, U32 off, U32 len)
{
  if (*AT91C_US1_TNCR == 0)
  {	
    if (len > 256) len = 256;	
    memcpy(&(out_buf[out_buf_ptr][0]), buf+off, len);
    *AT91C_US1_TNPR = (unsigned int) &(out_buf[out_buf_ptr][0]);
    *AT91C_US1_TNCR = len;
    out_buf_ptr = (out_buf_ptr+1) % 2;
    return len;
  }
  else
    return 0;
}

S32 bt_event_check(S32 filter)
{
  // Return the current event state.
  S32 ret = 0;
  // check read state
  int bytes_ready;
  if (*AT91C_US1_RNCR == 0) 
    bytes_ready = 256 - *AT91C_US1_RCR;
  else 
    bytes_ready = 128 - *AT91C_US1_RCR;
  if (bytes_ready  > in_buf_idx) ret |= BT_READABLE;
  // check write state.
  if (*AT91C_US1_TNCR == 0)
  {
    ret |= BT_WRITEABLE;
    if (*AT91C_US1_TCR == 0) ret |= BT_WRITEEMPTY;
  }
  // check command state
  if (*AT91C_ADC_CDR6 > 512)
    ret |= BT_STREAMMODE;
  else
    ret |= BT_CMDMODE;
  return ret & filter;
}


void bt_clear_arm7_cmd(void)
{
  *AT91C_PIOA_CODR  = BT_ARM7_CMD_PIN;
}

void bt_set_arm7_cmd(void)
{
  *AT91C_PIOA_SODR  = BT_ARM7_CMD_PIN;
}

void bt_set_reset_high(void)
{
  *AT91C_PIOA_SODR = BT_RST_PIN;
}

U32 bt_read(U8 * buf, U32 off, U32 len)
{
  int bytes_ready, total_bytes_ready;
  int cmd_len, i;
  U8* tmp_ptr;
  
  cmd_len = 0;
  if (*AT91C_US1_RNCR == 0) {
    bytes_ready = 128;
    total_bytes_ready = 256 - *AT91C_US1_RCR;
  }
  else
    total_bytes_ready = bytes_ready = 128 - *AT91C_US1_RCR;
  
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
      for(i=0;i<cmd_len && in_buf_idx < 128;i++) buf[off+i] = buf_ptr[in_buf_idx++];
      in_buf_idx = 0;
      tmp_ptr = &(in_buf[(in_buf_in_ptr+1)%2][0]);
      for(;i<cmd_len;i++) buf[off+i] = tmp_ptr[in_buf_idx++];
      in_buf_idx += 128;
    } 
  }
  
  // Current buffer full and fully processed
  
  if (in_buf_idx >= 128 && *AT91C_US1_RNCR == 0)
  { 	
  	// Switch current buffer, and set up next 
  	
  	in_buf_idx -= 128;
  	*AT91C_US1_RNPR = (unsigned int) buf_ptr;
  	*AT91C_US1_RNCR = 128;
  	in_buf_in_ptr = (in_buf_in_ptr+1) % 2;
  	buf_ptr = &(in_buf[in_buf_in_ptr][0]);
  }
  return cmd_len;   
}

void bt_set_reset_low(void)
{
  *AT91C_PIOA_CODR = BT_RST_PIN;
}

void bt_reset(void)
{
  // Perform hardware reset. This function has some relatively long
  // delays in it and so should probably only be called during application
  // initialisation and termination. Calling it at other times may cause
  // problems for other real time tasks.

  // If we do not have any buffers do nothing
  if (in_buf[0] == NULL) return;
  // If power is currently off to the BC4 do not reset it!
  if ((*AT91C_PIOA_ODSR & BT_RST_PIN) == 0) return; 
  //display_goto_xy(0, 1);
  //display_string("BT Reset....");
  //display_update();
  //systick_wait_ms(10000);
  // Ask for command mode
  bt_clear_arm7_cmd();
  // BC4 reset sequence. First take the reset line low for 100ms
  bt_set_reset_low();
  // Wait and discard any packets that may be around
  int cnt = 100;
  U8 *buf = out_buf[0];
  while (cnt-- > 0)
  {
    bt_read(buf, 0, OUT_BUF_SZ);
    systick_wait_ms(1);
  }
  bt_set_reset_high();
  // Now wait either for 5000ms or for the BC4 chip to signal reset
  // complete. Note we use the out buffer as a scratch area here
  // this is safe since we are forcing a reset.
  cnt = 5000;
  while (cnt > 0)
  {
    int offset = 0;
    // Read packet header
    while (cnt-- > 0 && bt_read(buf, 0, 1) != 1)
      systick_wait_ms(1);
    // Read packet body
    while (cnt-- > 0 && offset < buf[0])
    {
      if (bt_read(buf, offset+1, 1) == 1)
        offset++;
      systick_wait_ms(1);
    }
    // Look for the reset indication and the checksum
    if ((buf[0] == 3) && (buf[1] == MSG_RESET_INDICATION) && 
        (buf[2] == 0xff) && (buf[3] == 0xe9))
        break;
  }
  // Force command mode
  bt_clear_arm7_cmd();
  //display_goto_xy(10, 1);
  //display_int(cnt, 5);
  //display_update();
  //systick_wait_ms(10000);
}

#include "mytypes.h"
#include "AT91SAM7.h"
#include "uart.h"
#include "bt.h"
#include "aic.h"
#include  <string.h>
#include "display.h"
#include "systick.h"
#include "memory.h"
#include "usart.h"

#define IN_BUF_SZ 128
#define OUT_BUF_SZ 256
#define BAUD_RATE 460800
// Update rate (per second) for the ADC
#define ADC_UPDATE_RATE 50000 

// Bit values used for events
#define BT_READABLE   US_READABLE
#define BT_WRITEABLE  US_WRITEABLE
#define BT_WRITEEMPTY US_WRITEEMPTY
#define BT_CMDMODE    0x10
#define BT_STREAMMODE 0x20

usart *bt;

	
void bt_init(void)
{
  U8 trash;
  bt = NULL;
  bt_disable();
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
}

int bt_enable(void)
{
  if (bt == NULL)
  {
    bt = usart_allocate(AT91C_BASE_US1,AT91C_BASE_PDC_US1, IN_BUF_SZ, OUT_BUF_SZ);
    if (bt == NULL) return 0;
  }
  // Configure the Usart
  *AT91C_PMC_PCER = (1 << AT91C_ID_US1); 
  *AT91C_PIOA_PDR = BT_RX_PIN | BT_TX_PIN | BT_RTS_PIN | BT_CTS_PIN; 
  *AT91C_PIOA_ASR = BT_RX_PIN | BT_TX_PIN | BT_SCK_PIN | BT_RTS_PIN | BT_CTS_PIN; 
  *AT91C_US1_CR   = AT91C_US_RSTSTA|AT91C_US_RXDIS|AT91C_US_TXDIS;
  *AT91C_US1_CR   = AT91C_US_STTTO;
  *AT91C_US1_RTOR = 10000; 
  *AT91C_US1_IDR  = AT91C_US_TIMEOUT;
  *AT91C_US1_MR = (AT91C_US_USMODE_HWHSH & ~AT91C_US_SYNC) | AT91C_US_CLKS_CLOCK | AT91C_US_CHRL_8_BITS | AT91C_US_PAR_NONE | AT91C_US_NBSTOP_1_BIT | AT91C_US_OVER;
  *AT91C_US1_BRGR = ((CLOCK_FREQUENCY/8/BAUD_RATE) | (((CLOCK_FREQUENCY/8) - ((CLOCK_FREQUENCY/8/BAUD_RATE) * BAUD_RATE)) / ((BAUD_RATE + 4)/8)) << 16);
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
  usart_enable(bt);
  return 1;
}

void bt_disable()
{
  if (bt != NULL) usart_free(bt);
  bt = NULL;
  *AT91C_PMC_PCDR = (1 << AT91C_ID_US1); 
}
  

U32 bt_get_mode()
{
  // return the bt4 mode value.
  return (U32) *AT91C_ADC_CDR6;
}


U32 bt_write(U8 *buf, U32 off, U32 len)
{
  return usart_write(bt, buf, off, len);
}

S32 bt_event_check(S32 filter)
{
  // Return the current event state.
  S32 ret = usart_status(bt);
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
  return usart_read(bt, buf, off, len);
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
  if (bt == NULL) return;
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
  U8 *buf = bt->out_buf[0];
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

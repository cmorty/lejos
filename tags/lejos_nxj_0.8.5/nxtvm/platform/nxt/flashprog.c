#include "flashprog.h"
#include "interrupts.h"
#include "twi.h"
#include "systick.h"
#include "nxt_avr.h"
#include "display.h"
#include "sound.h"
#include "at91sam7s256.h"

// Timeouts in ms
#define TWI_TIMEOUT 5
#define FLASH_TIMEOUT 20

static U32 get_ms()
{
  /* Return a timer value in ms that can be used while interrupts are disabled
   * NOTE: This function must be here (rather then in systick), because it is
   * called during a flash write and so must be located in ram, not flash rom.
   */
  // We use the missed interupt count from the system timer
  return (*AT91C_PITC_PIIR & AT91C_PITC_PICNT) >> 20;
}
 
static int wait_flash_ready()
{
  // Wait for the flash controller to be ready or to timeout. Note although
  // we implement a timeout operation, it is not clear what to do if we
  // ever get one. If the system is still trying to write to flash then
  // it may not be possible to execute code from flash...
  int status;
  int timeout = get_ms() + FLASH_TIMEOUT;
  do {
    status = *AT91C_MC_FSR;
    if (status & AT91C_MC_FRDY) return status;
  } while (get_ms() < timeout);
  return status;
}

static int wait_twi_complete()
{
  int status;
  int timeout = systick_get_ms() + TWI_TIMEOUT;
  do {
    status = twi_status();
    if (status <= 0) return status;
  } while (systick_get_ms() < timeout);
  return status;
}
    
/**
 * Write a page to flash memory
 * returns > 0 number of bytes written < 0 error
 * Error returns:
 * -1 Timeout waiting for TWI to complete
 * -2 Timeout waiting for flash write to complete
 * -3 Bad page number
 */
int
flash_write_page(U32 *page, int page_num)
{
  /* Write page to flash memory.
   * This function must run out of ram and while it executes no other code
   * (especially any flash resident) code must run. This is becuase the
   * flash memory is only a single plane and can not be accessed for both read
   * and write at the same time.
   */
  int i, istate;
  int status;
  if (page_num + flash_start_page >= FLASH_MAX_PAGES) return -3;
  /* We must disbale interrupts. However we need to try and ensure that all
   * current interrupt activity is complete before we do that. We talk to
   * the avr every 1ms and this uses interrupt driven I/O so we try to make
   * sure this is complete.
   */
  // Allow any playing sound to complete
  i = sound_get_time();
  if (i > 0)
    systick_wait_ms(i + 2);
  // Turn off timer tick call backs
  systick_suspend();
   	
  // Wait until next tick
  systick_wait_ms(1);
 
  // Force a tick to talk to the avr
  nxt_avr_1kHz_update();
 
  // Wait for it to complete
  status = wait_twi_complete();
  if (status != 0) return -1;
  // Now we can turn off all ints
  istate = interrupts_get_and_disable();

  // Write the data to the flash buffer
  for (i = 0; i < FLASH_PAGE_SIZE; i++)
    FLASH_BASE[(page_num*FLASH_PAGE_SIZE)+i] = page[i];

  // Write the buffer to the selected page
  FLASH_CMD_REG = (0x5A000001 + (((page_num + flash_start_page) & 0x000003FF) << 8));

  status = wait_flash_ready();
  
  // Turn ints back on
  if (istate) interrupts_enable();
  // Ensure that we are back in-sync.
  systick_wait_ms(1);
  // Allow call backs on 1ms tick
  systick_resume();
  if (!(status & AT91C_MC_FRDY)) return -2;
  return FLASH_PAGE_SIZE*sizeof(U32);
}

/**
 * Read a page from flash memory
 * returns > 0 number of bytes written < 0 error
 * Error returns:
 * -3 Bad page number
 */
int
flash_read_page(U32 *page, int page_num)
{
  int i;
  if (page_num + flash_start_page >= FLASH_MAX_PAGES) return -3;
  for (i = 0; i < FLASH_PAGE_SIZE; i++)
    page[i] = FLASH_BASE[(page_num*FLASH_PAGE_SIZE)+i];
  return FLASH_PAGE_SIZE*sizeof(U32);
}



#include "flashprog.h"
#include "interrupts.h"
#include "twi.h"
#include "systick.h"
#include "nxt_avr.h"
#include "display.h"
#include "sound.h"
#include "flashwrite.h"
#include "at91sam7s256.h"
#include "stdlib.h"

// Timeouts in ms
#define TWI_TIMEOUT 5

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
 * Write a page from the supplied flash buffer to flash memory
 * The flash buffer must have been obtained through a call to
 * flash_get_page_buffer, before making this call
 * returns > 0 number of bytes written < 0 error
 * Error returns:
 * -1 Timeout waiting for TWI to complete
 * -2 Timeout waiting for flash write to complete
 * -3 Bad page number
 * -4 bad flash buffer
 */
int
flash_write_page_buffer(FOURBYTES *page, int page_num)
{
  /* Write page to flash memory.
   * This function must run out of ram and while it executes no other code
   * (especially any flash resident) code must run. This is becuase the
   * flash memory is only a single plane and can not be accessed for both read
   * and write at the same time.
   */
  int istate;
  int status;
  if (page_num + flash_start_page >= FLASH_MAX_PAGES) return -3;
  if (VINTPTR(page) != &(FLASH_BASE[page_num*FLASH_PAGE_SIZE])) return -4;
  /* We must disbale interrupts. However we need to try and ensure that all
   * current interrupt activity is complete before we do that. We talk to
   * the avr every 1ms and this uses interrupt driven I/O so we try to make
   * sure this is complete.
   */
  // Allow any playing sound to complete
  sound_wait();

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

  // Write the buffer to the selected page
  status = flash_write(page_num + flash_start_page);
  
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
 * return a pointer to a page buffer that can be used to write to the
 * requested page.
 * Return NULL if the page is not valid.
 */
U32 *
flash_get_page_buffer(int page)
{
  if (page + flash_start_page >= FLASH_MAX_PAGES) return NULL;
  return (U32 *) (FLASH_BASE + page*FLASH_PAGE_SIZE);
}
  

/**
 * Write a page from a memory buffer to flash memory
 * returns > 0 number of bytes written < 0 error
 * Error returns:
 * -1 Timeout waiting for TWI to complete
 * -2 Timeout waiting for flash write to complete
 * -3 Bad page number
 */
int
flash_write_page(FOURBYTES *page, int page_num)
{
  int i;
  FOURBYTES *buf = flash_get_page_buffer(page_num);
  if (buf == NULL) return -3;
  // Write the data to the flash buffer
  for (i = 0; i < FLASH_PAGE_SIZE; i++)
    buf[i] = page[i];
  return flash_write_page_buffer(buf, page_num);
}

/**
 * Read a page from flash memory
 * returns > 0 number of bytes written < 0 error
 * Error returns:
 * -3 Bad page number
 */
int
flash_read_page(FOURBYTES *page, int page_num)
{
  int i;
  if (page_num + flash_start_page >= FLASH_MAX_PAGES) return -3;
  for (i = 0; i < FLASH_PAGE_SIZE; i++)
    page[i] = FLASH_BASE[(page_num*FLASH_PAGE_SIZE)+i];
  return FLASH_PAGE_SIZE*sizeof(U32);
}



#include "flashprog.h"
#include "interrupts.h"
#include "twi.h"
#include "systick.h"
#include "nxt_avr.h"
#include "display.h"

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
  
  if (page_num + flash_start_page >= 1024) return 0;
  /* We must disbale interrupts. However we need to try and ensure that all
   * current interrupt activity is complete before we do that. We talk to
   * the avr every 1ms and this uses interrupt driven I/O so we try to make
   * sure this is complete.
   */

  // Turn off timer tick call backs
  systick_suspend();
   	
  // Wait until next tick
  systick_wait_ms(1);
 
  // Force a tick to talk to the avr
  nxt_avr_1kHz_update();
 
  // Wait for it to complete
  while (twi_busy());
  
  //systick_wait_ms(1);
  
  // Now we can turn off all ints
  istate = interrupts_get_and_disable();
  while (!(FLASH_STATUS_REG & 0x1));

  for (i = 0; i < 64; i++)
    FLASH_BASE[(page_num*64)+i] = page[i];

  FLASH_CMD_REG = (0x5A000001 + (((page_num + flash_start_page) & 0x000003FF) << 8));

  while (!(FLASH_STATUS_REG & 0x1));
  
  // Turn ints back on
  if (istate) interrupts_enable();
  // Ensure that we are back in-sync.
  systick_wait_ms(1);
  // Allow call backs on 1ms tick
  systick_resume();
  
  return 1;
}

void flash_set_mode(U32 fmcn) {
  FLASH_MODE_REG = ((fmcn << 16) | (1 << 8));
}

void
flash_erase_range(U32 addr, U32 nBytes)
{
  int i = 0;
  int istate = interrupts_get_and_disable();

  while (nBytes--) {
    i++;
  }
  if (istate)
    interrupts_enable();
}

void
flash_write(U32 addr, void *buffer, U32 nBytes)
{
  int i = 0;
  int istate = interrupts_get_and_disable();

  while (nBytes--) {
    i++;
  }
  if (istate)
    interrupts_enable();
}

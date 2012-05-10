#include "flashprog.h"
#include "at91sam7s256.h"

// Timeouts in ms
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
flash_write(int page_num)
{
  /* Write page to flash memory.
   * This function must run out of ram and while it executes no other code
   * (especially any flash resident) code must run. This is becuase the
   * flash memory is only a single plane and can not be accessed for both read
   * and write at the same time.
   */
  // Write the buffer to the selected page
  FLASH_CMD_REG = (0x5A000001 + (((page_num) & 0x000003FF) << 8));
  return wait_flash_ready();
}

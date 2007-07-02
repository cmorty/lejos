#include "flashprog.h"
#include "interrupts.h"
#include "twi.h"
#include "systick.h"
#include "nxt_avr.h"

int
flash_write_page(U32 *page, int page_num)
{
  int i, istate;
  
  if (page_num + FLASH_START_PAGE > 1023) return 0;
  
  systick_suspend();
   	
  systick_wait_ms(1);
 
  nxt_avr_1kHz_update();
 
  while (twi_busy());
  
  systick_wait_ms(1);
  
  istate = interrupts_get_and_disable();

  while (!(FLASH_STATUS_REG & 0x1));

  for (i = 0; i < 64; i++)
    FLASH_BASE[(page_num*64)+i] = page[i];

  FLASH_CMD_REG = (0x5A000001 + (((page_num + FLASH_START_PAGE) & 0x000003FF) << 8));

  while (!(FLASH_STATUS_REG & 0x1));
  
  if (istate) interrupts_enable();
  
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

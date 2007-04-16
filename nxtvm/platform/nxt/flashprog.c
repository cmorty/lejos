#include "flashprog.h"
#include "interrupts.h"

void 
flash_write_page(U32 *page, int page_num)
{
  int i;
  //int istate = interrupts_get_and_disable();
  
  while (!(FLASH_STATUS_REG & 0x1));
  
  FLASH_MODE_REG = 0x00340100;

  for (i = 0; i < 64; i++)
    FLASH_BASE[(page_num*64)+i] = page[i];

  FLASH_CMD_REG = (0x5A000001 + (((page_num + 128) & 0x000003FF) << 8));

  while (!(FLASH_STATUS_REG & 0x1));
  
  //if (istate) interrupts_enable();
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

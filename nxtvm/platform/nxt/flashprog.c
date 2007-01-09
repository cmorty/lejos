#include "flashprog.h"
#include "interrupts.h"


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

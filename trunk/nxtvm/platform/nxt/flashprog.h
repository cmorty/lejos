#ifndef __FLASHPROG_H__
#define __FLASHPROG_H__
#include "mytypes.h"

void flash_erase_range(U32 addr, U32 nBytes);
void flash_write(U32 addr, void *buffer, U32 nBytes);
#endif


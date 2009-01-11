#ifndef __FLASHPROG_H__
#  define __FLASHPROG_H__
#  include "mytypes.h"

int flash_write_page(U32 *buf, int page_num);
void flash_erase_range(U32 addr, U32 nBytes);
void flash_write(U32 addr, void *buffer, U32 nBytes);
void flash_set_mode(U32 fmcn); 
extern const U32 flash_start_page;
#define VINTPTR(addr) ((volatile unsigned int *)(addr))
#define VINT(addr) (*(VINTPTR(addr)))

#define FLASH_ADDRESS 0x00100000
#define FLASH_BASE VINTPTR(FLASH_ADDRESS + (flash_start_page * 256))
#define FLASH_MODE_REG VINT(0xFFFFFF60)
#define FLASH_CMD_REG VINT(0xFFFFFF64)
#define FLASH_STATUS_REG VINT(0xFFFFFF68)

#endif

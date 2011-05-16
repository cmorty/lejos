#ifndef __FLASHPROG_H__
#  define __FLASHPROG_H__
#  include "mytypes.h"

int flash_write_page(U32 *buf, int page_num);
int flash_read_page(U32 *buf, int page_num);

// First usable page 
extern const U32 flash_start_page;
#define VINTPTR(addr) ((volatile unsigned int *)(addr))
#define VINT(addr) (*(VINTPTR(addr)))

// Page size in WORDS
#define FLASH_PAGE_SIZE 64
#define FLASH_MAX_PAGES 1024
#define FLASH_ADDRESS 0x00100000
#define FLASH_BASE VINTPTR(FLASH_ADDRESS + (flash_start_page * 256))
#define FLASH_MODE_REG VINT(0xFFFFFF60)
#define FLASH_CMD_REG VINT(0xFFFFFF64)
#define FLASH_STATUS_REG VINT(0xFFFFFF68)

#endif

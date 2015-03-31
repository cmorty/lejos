#ifndef __FLASHPROG_H__
#define __FLASHPROG_H__
#include "platform_config.h"
#include "init.h"

int flash_write_page(FOURBYTES *buf, int page_num);
int flash_read_page(FOURBYTES *buf, int page_num);
FOURBYTES *flash_get_page_buffer(int page_num);
int flash_write_page_buffer(FOURBYTES *buf, int page_num);

#define VINTPTR(addr) ((volatile FOURBYTES *)(addr))
#define VINT(addr) (*(VINTPTR(addr)))

// Page size in WORDS
#define FLASH_PAGE_SIZE 64
#define FLASH_MAX_PAGES 1024
#define FLASH_ADDRESS 0x00100000
#define FLASH_BASE VINTPTR(FLASH_ADDRESS + (flash_start_page * 256))
#define FLASH_MODE_REG VINT(0xFFFFFF60)
#define FLASH_CMD_REG VINT(0xFFFFFF64)
#define FLASH_STATUS_REG VINT(0xFFFFFF68)
#define FLASH_PAGE(addr) (((FOURBYTES *)(addr) - &FLASH_BASE[0])/FLASH_PAGE_SIZE)

#endif

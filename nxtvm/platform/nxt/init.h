#ifndef _INIT_H_
#define _INIT_H_
#ifndef __ASSEMBLER__
#include <stdint.h>

// data provided by init.S
extern const void *menu_address;
extern const uint32_t menu_length;
extern const uint32_t flash_start_page;

// functions called by init.S
void main(void);
void data_abort_C(void* pc);

#endif
#endif

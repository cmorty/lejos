#ifndef __AT91_TWI_H__
#define __AT91_TWI_H__ 

#include "mytypes.h"

/* Main user interface */
int twi_init(void);

int twi_write(U32 dev_addr, U32 int_addr_bytes, U32 int_addr, U8 *data, U32 nBytes);
int twi_read(U32 dev_addr, U32 int_addr_bytes, U32 int_addr, U8 *data, U32 nBytes);


void twi_debug(void);
#endif


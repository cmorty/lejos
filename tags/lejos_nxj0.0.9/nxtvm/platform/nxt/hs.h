#ifndef HS_H_
#define HS_H_

#include "mytypes.h"

#define   HS_RX_PIN  AT91C_PIO_PA5
#define   HS_TX_PIN  AT91C_PIO_PA6
#define   HS_RTS_PIN AT91C_PIO_PA7

void hs_init(void);
int hs_enable(void);
void hs_disable(void);
U32 hs_write(U8 *buf, U32 off, U32 len);
U32 hs_read(U8 * buf, U32 off, U32 len);
U32 hs_pending(void);

int hs_send(U8 address, U8 control, U8 *data, int offset, int len, U16 *CRCTab);
int hs_recv(U8 *data, int len, U16 *CRCTab, int reset);

#endif /*HS_H_*/

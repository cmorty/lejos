#ifndef __I2C_H__
#define __I2C_H__

#include "mytypes.h"

#define I2C_N_PORTS 4

void i2c_disable(int port);
int i2c_enable(int port, int mode);
void i2c_disable_all(void);

void i2c_init(void);

int i2c_busy(int port);
int i2c_start(int port, 
              U32 address, 
              int internal_address, 
              int n_internal_address_bytes, 
              U8 *data, 
              U32 nbytes,
              int write);

int i2c_complete( int port, U8 *data, U32 nbytes);
void i2c_test(void);

#endif

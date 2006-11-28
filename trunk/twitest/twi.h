#ifndef TWI_H_
#define TWI_H_

extern void twi_init(void);
extern void  twi_write(unsigned char, unsigned short, 
                       unsigned char); 

unsigned char  twi_read(unsigned char, unsigned short);

#endif /*TWI_H_*/

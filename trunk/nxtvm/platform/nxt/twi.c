
#include "mytypes.h"
#include "twi.h"
#include "interrupts.h"
#include "AT91SAM7.h"

#include "byte_fifo.h"

#include "aic.h"



extern void twi_isr_entry(void);

static volatile  int txcomp;
void twi_isr_C(void)
{
   /* This jusrt assumes we're waiting for TXCOMP */
   txcomp = 1;
   *AT91C_TWI_IDR = ~0;
}


void twi_wait_complete(void)
{
  txcomp = 0;
  *AT91C_TWI_IER = 1; /* enable tx comp interrupt */
  
  while(!txcomp){}
  
}

int twi_init(void)
{
	int i_state;

	i_state = interrupts_get_and_disable();

	/* Grab the clock we need */
  	*AT91C_PMC_PCER = (1<< AT91C_PERIPHERAL_ID_PIOA)| /* Need PIO too */
			  (1<< AT91C_PERIPHERAL_ID_TWI); /* TWI clock domain */

	*AT91C_TWI_CR   = 0x88; /* Disable and reset */
	
	/* Grab the pins we need */
	*AT91C_PIOA_PDR = (1<<3) | (1<<4);
	*AT91C_PIOA_ASR = (1<<3) | (1<<4);
/*	*AT91C_PIOA_MDER = (1<<3) | (1<<4); /* open drain */
	
	/* Todo: set up interrupt */
	*AT91C_TWI_IDR = ~0; /* Disable all interrupt sources */
	aic_mask_off(AT91C_PERIPHERAL_ID_TWI);
	aic_set_vector(AT91C_PERIPHERAL_ID_TWI,AIC_INT_LEVEL_NORMAL,twi_isr_entry);
	aic_mask_on(AT91C_PERIPHERAL_ID_TWI);
	

	/* Init peripheral */
	*AT91C_TWI_CWGR = 0x020f0f; /* Set for 380kHz */
	*AT91C_TWI_CR	= 0x04;	/* Enable as master */

	if(i_state)
            interrupts_enable();
            
        return 1;
}



int twi_read(U32 dev_addr, U32 int_addr_bytes, U32 int_addr, U8 *data, U32 nBytes)
{
    U32 mode = ((dev_addr & 0x7f) << 16) | ((int_addr_bytes & 3) << 8) | (1<<12);
    U32 status;
    
    *AT91C_TWI_MMR = mode;
    
    if(nBytes == 1){
      *AT91C_TWI_CR = 0x07; /* Start, stop, enable */
      do{
        status = *AT91C_TWI_SR;
      } while(!(status & 2));
      
      *data = *AT91C_TWI_RHR;
    } else {
      *data = *AT91C_TWI_RHR;
      *AT91C_TWI_CR = 0x05; /* Start, enable */
      do{
        status = *AT91C_TWI_SR;
      } while(!(status & 2));
      while(nBytes){
        
      do{
        status = *AT91C_TWI_SR;
      } while(!(status & 2));
        *data = *AT91C_TWI_RHR;
        nBytes--;
        data++;
                          
        status = *AT91C_TWI_SR;
      }
      *AT91C_TWI_CR = 2; /* Stop */
    }
    
    twi_wait_complete();
    status = *AT91C_TWI_SR;
      
    return (status & 0x100) ? 0 : 1; 
}

int twi_write(U32 dev_addr, U32 int_addr_bytes, U32 int_addr, U8 *data, U32 nBytes)
{
    U32 mode = ((dev_addr & 0x7f) << 16) | ((int_addr_bytes & 3) << 8);
    U32 status;
    
    *AT91C_TWI_MMR = mode;
    
    if(nBytes == 1){
      *AT91C_TWI_CR = 0x03; /* Start, stop */
      *AT91C_TWI_THR = *data;
    } else {
      *AT91C_TWI_CR = 0x01; /* Start */
      while(nBytes){
      
        do{
          status = *AT91C_TWI_SR;
        } while(!(status & 4));
//        if(nBytes == 1)
//          *AT91C_TWI_CR = 2;
          
        *AT91C_TWI_THR = *data;
        nBytes--;
        data++;
        twi_wait_complete();
        status = *AT91C_TWI_SR;
      }
    }

     do{
        status = *AT91C_TWI_SR;
      } while(!(status & 4));
    
    *AT91C_TWI_CR = 2;
    
    twi_wait_complete();
    status = *AT91C_TWI_SR;
      
    return (status & 0x100) ? 0 : 1; 
}

static U32 responses[128];


int twi_check_address_responds(int addr)
{
  char zz;
  int retval =  twi_write(addr,0,0,&zz,1);
    
  return retval;
}



static int twi_check_all_addresses(void)
{
  int i;
  U32 xx;
  for(i = 0; i < 128; i++)
    responses[i] = twi_check_address_responds(i);
}

void twi_debug(void)
{
  twi_check_all_addresses();
}

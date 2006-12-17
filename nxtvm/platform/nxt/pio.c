#include "pio.h"
#include "nxt_motors.h"

#include "aic.h"

#include "AT91SAM7.h"

extern void pio_isr_entry(void);

void  pio_isr_C(void)
{ 
  U32 pinChanges = *AT91C_PIOA_ISR;// Acknowledge change
  U32 currentPins = *AT91C_PIOA_PDSR;  // Read pins
  
  nxt_motor_pio_process(currentPins);
}



void pio_init(void)
{
    *AT91C_PMC_PCER = (1<< AT91C_PERIPHERAL_ID_PIOA); /* Power to the pinns! */
  *AT91C_PIOA_IDR = ~0; 			      // Disable all pin change interrupts
  
  /* Enable ISR */
  aic_mask_off(AT91C_PERIPHERAL_ID_PIOA);
  aic_set_vector(AT91C_PERIPHERAL_ID_PIOA,AIC_INT_LEVEL_NORMAL,pio_isr_entry);
  aic_mask_on(AT91C_PERIPHERAL_ID_PIOA);
}


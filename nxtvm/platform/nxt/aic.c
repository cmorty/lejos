#include "aic.h"
#include "AT91SAM7.h"
#include "interrupts.h"

#define sysc ((volatile struct _AT91S_SYSC *)0xFFFFF000)

extern void default_isr(void);
extern void default_fiq(void);
extern void spurious_isr(void);


void aic_initialise(void)
{
	int i;
	
	/* Disable all interrupts at the core level */
	interrupts_get_and_disable();

	/* Clear all pending and enabled interrupts */
	sysc->SYSC_AIC_IDCR = 0xFFFFFFFF;
	sysc->SYSC_AIC_FFDR = 0xFFFFFFFF; /* None are fiq */
	sysc->SYSC_AIC_ICCR = 0xFFFFFFFF;

	for(i = 0; i < 32; i++){
		sysc->SYSC_AIC_SMR[i] = 0;
		sysc->SYSC_AIC_SVR[i] = (U32)default_isr;
	}

	sysc->SYSC_AIC_SVR[AT91C_PERIPHERAL_ID_FIQ] = (U32) default_fiq;

	sysc->SYSC_AIC_SPU = (U32)spurious_isr;
	sysc->SYSC_AIC_EOICR = 1;
	sysc->SYSC_AIC_DCR = 1; /* enable debug protection */
	

	/* Note: Leaves interrupts off */
}

void aic_set_vector(U32 vector, U32 mode, U32 isr)
{
	if(vector < 32) {
		int i_state = interrupts_get_and_disable();
		sysc->SYSC_AIC_SMR[vector] = mode;
		sysc->SYSC_AIC_SVR[vector] = isr;
		if(i_state)
			interrupts_enable();
	}
}

void aic_mask_on(U32 vector)
{
	int i_state = interrupts_get_and_disable();
	sysc->SYSC_AIC_IECR = (1 << vector);
	if(i_state) 
		interrupts_enable();	
}

void aic_mask_off(U32 vector)
{
	int i_state = interrupts_get_and_disable();
	sysc->SYSC_AIC_IDCR = (1 << vector);
	if(i_state) 
		interrupts_enable();	
}

void aic_clear(U32 vector)
{
	int i_state = interrupts_get_and_disable();
	sysc->SYSC_AIC_ICCR = (1 << vector);
	if(i_state) 
		interrupts_enable();	
}

/******************************************************************************/ 
/*                                                                            */ 
/*  TWI.C:  Low Level I2C Routines                                      */ 
/*                                                                            */ 
/******************************************************************************/ 

#include "at91sam7s256.h"

inline void init_clocks()
{
  /* Enable internal main oscillator */
  *AT91C_CKGR_MOR = AT91C_CKGR_MOSCEN | (0x6 << 8);

  /* Wait for oscillator to stabilize */
  while ((*AT91C_PMC_SR & AT91C_PMC_MOSCS) == 0);

  /* Initialize the PLL clock. The quartz runs at 18.432MHz, we run
   * the PLL at ~96MHz.
   */
  *AT91C_CKGR_PLLR = (0xE | (0x1C << 8) | (0x48 << 16) | AT91C_CKGR_USBDIV_1);

  /* Wait for the PLL to lock */
  while ((*AT91C_PMC_SR & AT91C_PMC_LOCK) == 0);

  /* Wait for master clock ready (just in case) */
  while ((*AT91C_PMC_SR & AT91C_PMC_MCKRDY) == 0);

  /* Set the master clock prescaler to /2 (48MHz, the max cpu speed) */
  *AT91C_PMC_MCKR = AT91C_PMC_CSS_SLOW_CLK | AT91C_PMC_PRES_CLK_2;

  /* Wait for master clock ready */
  while ((*AT91C_PMC_SR & AT91C_PMC_MCKRDY) == 0);

  /* Switch the main clock over to the PLL */
  //*AT91C_PMC_MCKR = AT91C_PMC_CSS_PLL_CLK | AT91C_PMC_PRES_CLK_2;

  /* Wait for master clock ready */
  //while ((*AT91C_PMC_SR & AT91C_PMC_MCKRDY) == 0);

  /* TODO enable USB clocks */
}


//*----------------------------------------------------------------------------
//* \fn    AT91F_TWI_Configure
//* \brief Configure TWI in master mode
//*----------------------------------------------------------------------------
__inline void AT91F_TWI_Configure ( AT91PS_TWI pTWI )          // \arg pointer to a TWI controller
{
    //* Disable interrupts
	pTWI->TWI_IDR = (unsigned int) -1;

    //* Reset peripheral
	pTWI->TWI_CR = AT91C_TWI_SWRST;

	//* Set Master mode
	pTWI->TWI_CR = AT91C_TWI_MSEN;

}

//*----------------------------------------------------------------------------
//* \fn    AT91F_PMC_EnablePeriphClock
//* \brief Enable peripheral clock
//*----------------------------------------------------------------------------
__inline void AT91F_PMC_EnablePeriphClock (
	AT91PS_PMC pPMC, // \arg pointer to PMC controller
	unsigned int periphIds)  // \arg IDs of peripherals
{
	pPMC->PMC_PCER = periphIds;
}

//*----------------------------------------------------------------------------
//* \fn    AT91F_PIO_CfgPeriph
//* \brief Enable pins to be drived by peripheral
//*----------------------------------------------------------------------------
__inline void AT91F_PIO_CfgPeriph(
	AT91PS_PIO pPio,             // \arg pointer to a PIO controller
	unsigned int periphAEnable,  // \arg PERIPH A to enable
	unsigned int periphBEnable)  // \arg PERIPH B to enable

{
	pPio->PIO_ASR = periphAEnable;
	pPio->PIO_BSR = periphBEnable;
	pPio->PIO_PDR = (periphAEnable | periphBEnable); // Set in Periph mode
}

//*----------------------------------------------------------------------------
//* \fn    AT91F_TWI_CfgPIO
//* \brief Configure PIO controllers to drive TWI signals
//*----------------------------------------------------------------------------
__inline void AT91F_TWI_CfgPIO (void)
{
	// Configure PIO controllers to periph mode
	AT91F_PIO_CfgPeriph(
		AT91C_BASE_PIOA, // PIO controller base address
		((unsigned int) AT91C_PA3_TWD     ) |
		((unsigned int) AT91C_PA4_TWCK    ), // Peripheral A
		0); // Peripheral B
}

//*----------------------------------------------------------------------------
//* \fn    AT91F_TWI_CfgPMC
//* \brief Enable Peripheral clock in PMC for  TWI
//*----------------------------------------------------------------------------
__inline void AT91F_TWI_CfgPMC (void)
{
	AT91F_PMC_EnablePeriphClock(
		AT91C_BASE_PMC, // PIO controller base address
		((unsigned int) 1 << AT91C_ID_TWI));
}

 void  twi_init(void) 
 { 
    // init_clocks();
 	
    // PIOA-pin setup: 
    AT91F_TWI_CfgPIO(); 

   // clk enable: 
   AT91F_TWI_CfgPMC(); 

   // TWI setup: 
   AT91F_TWI_Configure(AT91C_BASE_TWI); 

   // set TWI clk: 
   //*AT91C_TWI_CWGR = (7<<16)|(255<<8)|(255);   // CKDIV | CHDIV | CLDIV = 111 1111 1111 

    *AT91C_TWI_CWGR = (2<<16)|(15<<8)|(15); 
   // set dev.adr to 7-bits, adr.space to 16-bits: 
   //*AT91C_TWI_MMR = 0x550200; 
   *AT91C_TWI_MMR = 0x0000;

 } 

void  twi_write(unsigned char dev_adr, unsigned short mem_adr, unsigned char data) 
 { 
   unsigned int status; 

   *AT91C_TWI_IADR = mem_adr; 

   *AT91C_TWI_MMR &= ( 0xFF00EFFF | (dev_adr<<16) ); 

   *AT91C_TWI_CR = AT91C_TWI_START | AT91C_TWI_MSEN | AT91C_TWI_STOP; 

   *AT91C_TWI_THR = data; 

   status = *AT91C_TWI_SR; 
   *((unsigned int *) 0x20F004) = status;

   while ( !(status & AT91C_TWI_TXCOMP) ) { 
      status = *AT91C_TWI_SR; 
      //*((unsigned int *) 0x20F004) = status;
   } 

 } 

unsigned char  twi_read(unsigned char dev_adr, unsigned short mem_adr) 
{ 
   unsigned int status; 

   *AT91C_TWI_IADR = mem_adr; 

   *AT91C_TWI_MMR |= ( 0x00001000 | (dev_adr<<16) ); 

   *AT91C_TWI_CR = AT91C_TWI_START | AT91C_TWI_MSEN | AT91C_TWI_STOP; 

   status = *AT91C_TWI_SR; 

   while ( !(status & AT91C_TWI_TXCOMP) ) { 
      status = *AT91C_TWI_SR; 
      *((unsigned int *) 0x20F004) = status;
   } 
  
   return (unsigned char)*AT91C_TWI_RHR; 
 } 


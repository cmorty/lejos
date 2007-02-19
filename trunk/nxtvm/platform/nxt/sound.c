#include "mytypes.h"
#include "sound.h"
#include "AT91SAM7.h"
#include "aic.h"

extern void sound_isr_entry(void);

const SAMPLEWORD tone_pattern[16] = 
  {
    0xF0F0F0F0,0xF0F0F0F0, // Step 4 = 0,+22,+32,+22,0,-22,-32,-22
    0xFCFCFCFC,0xFCFCFDFD,
    0xFFFFFFFF,0xFFFFFFFF,
    0xFDFDFCFC,0xFCFCFCFC,
    0xF0F0F0F0,0xF0F0F0F0,
    0xC0C0C0C0,0xC0C08080,
    0x00000000,0x00000000,
    0x8080C0C0,0xC0C0C0C0
  };
  
U32 tone_cycles;

void sound_init()
{
  *AT91C_PMC_PCER = (1L << AT91C_ID_SSC);  /* Enable MCK clock   */
  *AT91C_PIOA_PER = AT91C_PA17_TD;         /* Disable TD on PA17  */
  *AT91C_PIOA_ODR = AT91C_PA17_TD;
  *AT91C_PIOA_OWDR = AT91C_PA17_TD;
  *AT91C_PIOA_MDDR = AT91C_PA17_TD;
  *AT91C_PIOA_PPUDR = AT91C_PA17_TD;
  *AT91C_PIOA_IFDR = AT91C_PA17_TD;
  *AT91C_PIOA_CODR = AT91C_PA17_TD;
  *AT91C_PIOA_IDR = AT91C_PA17_TD;
  *AT91C_SSC_CR = AT91C_SSC_SWRST;
  *AT91C_SSC_TCMR = AT91C_SSC_CKS_DIV + AT91C_SSC_CKO_CONTINOUS + AT91C_SSC_START_CONTINOUS;
  *AT91C_SSC_TFMR = (SAMPLEWORDBITS - 1) + ((SAMPLEWORDS & 0xF) << 8) + AT91C_SSC_MSBF;
  *AT91C_SSC_CR = AT91C_SSC_TXEN; /* TX enable */                                         
  *AT91C_AIC_ICCR = (1L << AT91C_ID_SSC); /* Clear interrupt */
  *AT91C_AIC_IECR = (1L << AT91C_ID_SSC); /* Enable int. controller */
  
  aic_set_vector(AT91C_PERIPHERAL_ID_SSC, AT91C_AIC_PRIOR_LOWEST | AT91C_AIC_SRCTYPE_INT_EDGE_TRIGGERED,
		 sound_isr_entry);
}

void sound_freq(U32 freq, U32 ms)
{
  *AT91C_SSC_CMR = ((96109714L / (2L * 512L)) / freq) + 1L;
  *AT91C_SSC_PTCR = AT91C_PDC_TXTEN;
  tone_cycles = (freq * ms) / 2000 - 1L;
  sound_int_enable();
}

void sound_int_enable()
{
  *AT91C_SSC_IER = AT91C_SSC_ENDTX;
}

void sound_int_disable()
{
  *AT91C_SSC_IDR = AT91C_SSC_ENDTX;
}

void sound_enable()
{
  *AT91C_PIOA_PDR = AT91C_PA17_TD;
}

void sound_disable()
{
  *AT91C_PIOA_PER = AT91C_PA17_TD;
}

void sound_isr_C()
{
  if (tone_cycles--)
  {
    *AT91C_SSC_TNPR = (unsigned int) tone_pattern;
    *AT91C_SSC_TNCR = 16;
    sound_enable();
  }
  else
  {
  	sound_disable();
  	sound_int_disable();
  }
}

#ifndef _AT91SAM7_H_
#define _AT91SAM7_H_

#ifdef __ASSEMBLER__
#define __ASSEMBLY__
#endif
#include "at91sam7s256.h"

#ifdef __ASSEMBLER__
// add constants missing in Atmel's header file
#define PMC_MOR  0x20
#define PMC_MCFR 0x24
#define PMC_PLLR 0x2C
#endif


#define CLOCK_FREQUENCY 48054850

// constants for program status register
#define CPSR_MODE        0x1F
#define CPSR_MODE_USER   0x10
#define CPSR_MODE_FIQ    0x11
#define CPSR_MODE_IRQ    0x12
#define CPSR_MODE_SUPER  0x13
#define CPSR_MODE_ABORT  0x17
#define CPSR_MODE_UNDEF  0x1B
#define CPSR_MODE_SYSTEM 0x1F
#define CPSR_THUMB       (1 << 5)
#define CPSR_NOFIQ       (1 << 6)
#define CPSR_NOIRQ       (1 << 7)

#endif

#include "at91sam7.h"
@  This is the vector set up for an AT91SAM7

	.section .vectors, "ax"
	.code 32
	.align 2

@ This is not actally executed in this code stream. Instead it is
@ copied into the reloacted vector space.
@
@ For the IRQ and FIQ, we read the vectors held in the AIC.
@

vectors_start:
	b       start                             @ Reset Vector
	b       undef_handler                     @ Undefined Instruction
	b       swi_handler                       @ Software Interrupt
	b       prefetch_abort_handler            @ Prefetch Abort
	b       data_abort_handler                @ Data Abort
	b       reserved_handler                  @ Reserved
	ldr     pc, vectors_start + AT91C_AIC_IVR @ IRQ : read the AIC
	ldr     pc, vectors_start + AT91C_AIC_FVR @ FIQ : read the AIC

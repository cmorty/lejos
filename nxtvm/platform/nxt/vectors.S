#include "at91sam7.h"
@  This is the vector set up for an AT91SAM7

	.text
	.code 32
	.align 2

@ This is not actally executed in this code stream. Instead it is
@ copied into the reloacted vector space.
@
@ For the IRQ and FIQ, we read the vectors held in the AIC (address 0xFFFFF100).
@
@ Note: We do not do branches because these would get screwed up by relocation.
@

	.extern start
	.extern undef_handler
	.extern swi_handler
	.extern prefetch_abort_handler
	.extern data_abort_handler
	.extern reserved_handler

vectors_start:
	ldr     pc, =start                        @ Reset Vector
	ldr     pc, =undef_handler                @ Undefined Instruction
	ldr     pc, =swi_handler                  @ Software Interrupt
	ldr     pc, =prefetch_abort_handler       @ Prefetch Abort
	ldr     pc, =data_abort_handler           @ Data Abort
	ldr     pc, =reserved_handler             @ Reserved
	ldr     pc, vectors_start + AT91C_AIC_IVR @ IRQ : read the AIC
	ldr     pc, vectors_start + AT91C_AIC_FVR @ FIQ : read the AIC

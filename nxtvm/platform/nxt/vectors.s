@  This is the vector set up for an AT91SAM7

	.text
	.code 32
	.align 	0

@ This is not actally executed in this code stream. Instead it is
@ copied into the reloacted vector space.
@ NB When executing the first instruction in an abort/interrupt, the pc is 8 (ie 2 instructions)
@ ahead of the start of instruction being executed.
@ Hence,  for the first 6 vectors, the ldr loads the correct address into pc by looking at pc + 0x18.
@
@ For the IRQ and FIQ, we subtract 0xF20 to get to the vectors held in the AIC.
@
@ NB We do not do branches because these would get screwed up by relocation.
@

vectors_start:
		ldr   pc,=start                   @ reset vector
		ldr   pc,=undef_handler           @ Undefined Instruction
		ldr   pc,=swi_handler             @ Software Interrupt
		ldr   pc,=prefetch_abort_handler  @ Prefetch Abort
		ldr   pc,=data_abort_handler      @ Data Abort
		ldr   pc,=reserved_handler        @ reserved
		ldr   pc,vectors_start+0xFFFFF100 @ IRQ : read the AIC
		ldr   pc,vectors_start+0xFFFFF104 @ FIQ : read the AIC


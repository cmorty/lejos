#include "AT91SAM7.h"
#include "asmtools.h"

@ Disables interrupts.
@ Returns zero if interupts were previously off
global_arm_func_section interrupts_get_and_disable
	mrs     r0, cpsr
	tst     r0, #CPSR_NOIRQ
	movne   r0, #0
	bxne    lr
	orr     r0, r0, #CPSR_NOIRQ
	msr     cpsr_c, r0
	mov     r0, #1
	bx      lr

global_arm_func_section interrupts_enable
	mrs     r0, cpsr
	bic     r0, r0, #CPSR_NOIRQ
	msr     cpsr_c, r0
	bx      lr


@ Disables interrupts.
@ Returns zero if interupts were previously off
global_arm_func_section fiq_get_and_disable
	mrs     r0, cpsr
	tst     r0, #CPSR_NOFIQ
	movne   r0, #0
	bxne    lr
	orr     r0, r0, #CPSR_NOFIQ
	msr     cpsr_c, r0
	mov     r0, #1
	bx      lr

global_arm_func_section fiq_enable
	mrs     r0, cpsr
	bic     r0, r0, #CPSR_NOFIQ
	msr     cpsr_c, r0
	bx      lr


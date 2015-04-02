#include "AT91SAM7.h"
#include "asmtools.h"

@
@  IRQ wrappers.
@
@  These call a C function.
@  We switch to supervisor mode and reenable interrupts to allow nesting.
@
@


@
@  Macros
@

.macro  irq_wrapper_nested, C_function

	@ Save registers on stack
	sub     lr, lr, #4 @ fix up for return
	stmfd   sp!, {lr}
	mrs     lr, spsr
	stmfd   sp!, {lr}

	@ Acknowledge the IVR for debugging to support Protected Mode
	ldr     lr, =0xFFFFF100
	str     lr, [lr]

	@ swich to system mode and enable IRQ, but not FIQ
	msr     cpsr_c, #CPSR_MODE_SYSTEM|CPSR_NOFIQ

	@ push stack
	stmfd   sp!, {r0-r12, lr}

	@ Call the function
	ldr     r0, =\C_function
	mov     lr, pc
	bx      r0

	@ pop stack
	ldmfd   sp!, {r0-r12, lr}

	@ swich to interrupt mode and disable IRQs and FIQs
	msr     cpsr_c, #CPSR_MODE_IRQ|CPSR_NOIRQ|CPSR_NOFIQ

	@End of interrupt by doing a write to AIC_EOICR
	ldr     lr, =0xFFFFF130
	str     lr, [lr]

	@ Unstack the saved spsr
	ldmfd   sp!, {lr}
	msr     spsr_all, lr

	@ Return from interrupt (unstacking the modified lr)
	ldmfd   sp!, {pc}^

.endm


.macro  irq_wrapper_not_nested, C_function

	@ Save registers on stack
	sub     lr, lr, #4 @ fix up for return
	stmfd   sp!, {r0-r12, lr}

	@ Acknowledge the IVR for debugging to support Protected Mode
	ldr     lr, =0xFFFFF100
	str     lr, [lr]

	@ Call the function
	ldr     r0, =\C_function
	mov     lr, pc
	bx      r0

	@End of interrupt by doing a write to AIC_EOICR
	ldr     lr, =0xFFFFF130
	str     lr, [lr]

	@ Return from interrupt (unstacking the modified lr)
	ldmfd   sp!, {r0-r12, pc}^

.endm

@
@	ISRs
@
@


global_arm_func_section spurious_isr
global_arm_func_label   default_isr
global_arm_func_label   default_fiq
0:	b	0b

global_arm_func_section systick_isr_entry
	irq_wrapper_nested systick_isr_C

global_arm_func_section systick_low_priority_entry
	irq_wrapper_nested systick_low_priority_C

global_arm_func_section udp_isr_entry
	irq_wrapper_nested udp_isr_C

global_arm_func_section spi_isr_entry
	irq_wrapper_nested spi_isr_C

global_arm_func_section twi_isr_entry
	irq_wrapper_nested twi_isr_C

global_arm_func_section sound_isr_entry
	irq_wrapper_nested sound_isr_C

@global_arm_func_section uart_isr_entry_0
@	irq_wrapper_nested uart_isr_C_0

@global_arm_func_section uart_isr_entry_1
@	irq_wrapper_nested uart_isr_C_1

global_arm_func_section nxt_motor_isr_entry
	irq_wrapper_nested nxt_motor_isr_C

global_arm_func_section i2c_timer_isr_entry
	irq_wrapper_nested i2c_timer_isr_C


#include "AT91SAM7.h"
#include "asmtools.h"
@
@ This is the init for an AT91SAM7S64
@ This code is entered with interrupts disabled
@ This code should work as start code from flash or RAM.
@ It does not have to be placed at address 0 since it will copy the vectors
@ there if needed.
@

@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

@ Macros to do memory initialisation

.macro  mem_copy,  source, source_end, dest
	ldr     r0, =\source
	ldr     r1, =\source_end
	ldr     r2, =\dest
	bl      mem_copy_func
.endm

.macro  mem_initialise, dest, dest_end, val
	ldr     r0, =\dest
	ldr     r1, =\dest_end
	ldr     r2, =\val
	bl      mem_init_func
.endm


@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@

	.section .rodata
	.align 2
global_data_label menu_address
	.ascii "MNUA"
global_data_label menu_length
	.ascii "MNUL"
global_data_label flash_start_page
	.ascii "FLSP"


	.text
	.code 32
	.align 2

	.global start

@
@ Flash is always a 1MB
@ RAM is always at 2MB
@ 0MB will be flash or RAM depending on the remap state.
@
start:
	@ Switch to system mode and disable interrupts (IRQ and FIQ).
	@ Should not have to do this, but lets not assume anything.
	msr     CPSR_c, #CPSR_MODE_SYSTEM|CPSR_NOIRQ|CPSR_NOFIQ

	@ Have zero in r0 throughout
	@ Allows us to easily load constants via orr (has bigger range than mov)
	@ and allows us to access registers from 0xFFFFF004 upwards.
	mov     r0, #0

@
@ Do the remapping, if required, so that RAM is at address 0.
@
@ We read what is at address 0, invert it, and write it to 2MB.
@ If the value at address 0 has changed to the inverted value, then
@ there's no need to remap.
@
	orr     r1, r0, #0x200000
	ldr     r2, [r0]
	mvn     r3, r2
	str     r3, [r1]
	ldr     r2, [r0]

	cmp     r2, r3
	movne   r1, #1
	strne   r1, [r0, #0xFFFFFF00]


continue_boot:

@   MC_FMR:
@   Set up flash memory accessing and memory for 48 MHz FMCN = 50)
@   2 wait states (FWS = 1)
@   No special flags
@

	ldr     r1, =((72 << 16) | (1 << 8))
	str     r1, [r0, #0xFFFFFF60]

@
@    Disable watchdog
@    Set MDT_MR disable bit. 
@    Note: probably want to set this differently at some stage...
@
	orr     r1, r0, #0x00008000
	str     r1, [r0, #0xFFFFFD44]

@
@    Set up PLL & clock source
@    The main crystal is 18.432 MHz
@    We want as close as we can get to 48MHz
@    We use the PLL to generate 18432000 / 14 * 73 = 96109714
@    We then divide this by 2

	@ Start main osc
	ldr     r1, =0x00000601
	str     r1, [r0, #0xFFFFFC20] @ PMC_MOR

wait_MOSCS:
	ldr     r1, [r0, #0xFFFFFC68] @ PMC_SR
	tst     r1, #1
	beq     wait_MOSCS

	@ set up PLL
	ldr     r1, =0x00481C0E
	str     r1, [r0, #0xFFFFFC2C] @ PMC_PLLR

wait_pll_lock:
	ldr     r1, [r0, #0xFFFFFC68] @ PMC_SR
	tst     r1, #4
	beq     wait_pll_lock

wait_MCLK_Ready0:
	ldr     r1, [r0, #0xFFFFFC68] @ PMC_SR
	tst     r1, #8
	beq     wait_MCLK_Ready0

	@ Set up master clock to use clock div 2
	ldr     r1, =0x4
	str     r1, [r0, #0xFFFFFC30] @ PMC_MCKR

wait_MCLK_Ready1:
	ldr     r1, [r0, #0xFFFFFC68] @ PMC_SR
	tst     r1, #8
	beq     wait_MCLK_Ready1

	@ Set up master clock to use PLL clock div 2
	ldr     r1, =0x7
	str     r1, [r0, #0xFFFFFC30] @ PMC_MCKR

wait_MCLK_Ready2:
	ldr     r1, [r0, #0xFFFFFC68] @ PMC_SR
	tst     r1, #8
	beq     wait_MCLK_Ready2


@
@ Copy the vector table
@ Initialise memory regions
@
vector_copy:
	mem_copy       __vectors_load_start__, __vectors_load_end__, __vectors_ram_start__
data_copy:
	mem_copy       __data_load_start__, __data_load_end__, __data_ram_start__
ram_text_copy:
	mem_copy       __ramtext_load_start__, __ramtext_load_end__, __ramtext_ram_start__
bss_init:
	mem_initialise __bss_start__, __bss_end__, 0
stack_init_0:
	mem_initialise __stack_start__, __stack_end__, 0x6b617453 @ 'Stak'

@
@   Set up stacks etc.
@   We set up the irq and system stacks
@   We switch to system mode for further execution
@   NB Stacks should be 8-byte aligned for APCS
@   They should already be aligned, but we mask the values to make sure.
@
stack_init_1:
	@ Set up Interrupt stack
	msr     CPSR_c, #CPSR_MODE_IRQ|CPSR_NOIRQ|CPSR_NOFIQ
	ldr     sp, =__irq_stack__
	bic     sp, sp, #7          @ make sure it is 8-byte aligned

	@ Set up System stack
	msr     CPSR_c, #CPSR_MODE_SYSTEM|CPSR_NOIRQ|CPSR_NOFIQ
	ldr     sp, =__system_stack__
	bic     sp, sp, #7          @ make sure it is 8-byte aligned

	@ Set up initial frame pointers etc
	mov     fp, #0              @ Null frame pointer
	mov     r7, #0              @ Null frame pointer for Thumb

	@ Kick into main code using interworking
	ldr     r5, =main
	adr     lr, main_returned
	bx      r5

	@ If we get here then main returned -- bad!
main_returned:
	b       main_returned




@
@ Dummy handlers
@
	.global undef_handler
	.global swi_handler
	.global prefetch_abort_handler
	.global data_abort_handler
	.global reserved_handler


undef_handler:
	b       undef_handler
swi_handler:
	b       swi_handler
prefetch_abort_handler:
	b       prefetch_abort_handler


data_abort_handler:
	mov     r0, lr
	msr     CPSR_c, #0xDF		@ System mode , I and F bits set (interrupts disabled)
	ldr     sp, =__system_stack__
	ldr     r1, =data_abort_C
	adr     lr, data_abort_C_returned
	bx      r1
data_abort_C_returned:
	b       data_abort_C_returned
reserved_handler:
	b       reserved_handler



@ Little helper funcs


@ mem_copy_func r0 = source start, r1 = end of source, r2 = destination
@ Will not copy if source == destination
@ Will try to use ldm/stm if 16-byte aligned.
mem_copy_func:
	@ bail if source and dest addresses are the same
	cmp     r0, r2
	bxeq    lr
	
	@ test if all addressed are 16-byte aligned, if so use ldm/stm copy
	orr     r3, r1, r0
	orr     r3, r3, r2
	tst     r3, #15
	beq     mcf_16_aligned
	
	@ else use 4-byte aligned loop
mcf_loop:
	cmp     r0, r1
	ldrlo   r3, [r2], #4
	strlo   r3, [r0], #4
	blo     mcf_loop
	bx      lr

mcf_16_aligned:
	cmp     r0, r1
	ldmloia r0!, {r3-r6}
	stmloia r2!, {r3-r6}
	blo     mcf_16_aligned
	bx      lr
	

@ mem_init_func: r0 = start address, r1 = end address, r2 is value to write
@ If start and end addresses are multiples of 16 then we use stms to do the
@ storing.
mem_init_func:
	orr     r3, r1, r0
	tst     r3, #15
	beq     mif_16_aligned
mif_loop:
	cmp     r0, r1
	strlo   r2, [r0], #4
	blo     mif_loop
	bx      lr

mif_16_aligned:
	mov     r3, r2
	mov     r4, r2
	mov     r5, r2
mif_16_loop:
	cmp     r0, r1
	stmloia r0!, {r2-r5}
	blo     mif_16_loop
	bx      lr

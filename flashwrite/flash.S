.global _start
.align 2
.text

#define ROM_BASE        0x00100000
#define EFC_BASE        0xFFFFFF00
#define EFC_REG_STATUS  0x68
#define EFC_REG_COMMAND 0x64
#define CMD_WRITE_BASE  0x5A000001
#define PAGE_SIZE       256

/**
 * Memory Layout:
 *  stack
 *	helper code
 *	page data
 *
 * Page Data Layout:
 *	+0x0000	uint32	page number
 *	+0x0004	data (256 bytes)
 */

/**
 * Register Layout:
 *	pc/r15	program counter
 *	lr/r14	address of SAM-BA - must NOT be overwritten, so never ever use bl and friends
 *	sp/r13	stack pointer (not used)
 *	r6	base address of flash registers
 *	r5	base address of flash memory
 *	r4	page data address
 *	r3	page offset
 *	r2	scratch
 *	r1	scratch
 *	r0	scratch
 */


//push {r0, r1, r2, r3, r4, r5, r6 }
//pop {r0, r1, r2, r3, r4, r5, r6 }

_start:
	// place stack before program and save registers
	adr sp, _start
	// load some useful addresses
	ldr r6, =EFC_BASE
	ldr r5, =ROM_BASE
	adr r4, eof

wait_loop1:
	// wait as long as lowest bit is NOT set
	ldr r0, [r6, #EFC_REG_STATUS]
	tst r0, #1
	beq wait_loop1

copy_page:
	ldr r3,	[r4], #4
	lsl r3, r3, #22  // r3 := (r3 & 0x3FF) << 8
	lsr r3, r3, #14
	add r1, r3, r5
	add r2, r1, #PAGE_SIZE
copy_loop:
	ldr r0, [r4], #4
	str r0, [r1], #4
	cmp r1, r2
	blo copy_loop

flash_command:
	ldr r0, =CMD_WRITE_BASE
	add r0, r0, r3
	str r0, [r6, #EFC_REG_COMMAND]

wait_loop2:
	// wait as long as lowest bit is NOT set
	ldr r0, [r6, #EFC_REG_STATUS]
	tst r0, #1
	beq wait_loop2

return:
	// return
	bx lr

literals:
	// the compiler might store some values here
	.ltorg

eof:

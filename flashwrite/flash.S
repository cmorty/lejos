#define __ASSEMBLY__
#include "at91sam7s256.h"

#define CMD_WRITE_BASE  ((0x5A << 24) + AT91C_MC_FCMD_START_PROG)
#define page_number     (eof)
#define page_data_start (eof+4)
#define page_data_end   (eof+4+256)

	.global _start
	.align 2
	.text

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
 *	r7	zero
 *	r4	page data address
 *	r3	page offset
 *	r2	scratch
 *	r1	scratch
 *	r0	scratch
 */


//push {r0, r1, r2, r3, r4, r5, r7 }
//pop {r0, r1, r2, r3, r4, r5, r7 }

_start:
	// place stack before program. Actually, we need a stack
	// even though we don't enable interrupts or use the stack ourselves.
	adr     sp, _start
	ldr     r7, =0

wait_loop1:
	// wait as long as lowest bit is NOT set
	ldr     r0, [r7, #AT91C_MC_FSR]
	tst     r0, #AT91C_MC_FRDY
	beq     wait_loop1

copy_page:
	ldr     r2, page_number
	cmp     r2, #AT91C_IFLASH_NB_OF_PAGES
	bhs     return
	lsl     r3, r2, #8
	add     r4, r3, #AT91C_IFLASH
	adr     r1, page_data_start
	adr     r2, page_data_end
copy_loop:
	ldr     r0, [r1], #4
	str     r0, [r4], #4
	cmp     r1, r2
	blo     copy_loop

flash_command:
	ldr     r0, =CMD_WRITE_BASE
	add     r0, r0, r3
	str     r0, [r7, #AT91C_MC_FCR]

wait_loop2:
	// wait as long as lowest bit is NOT set
	ldr     r0, [r7, #AT91C_MC_FSR]
	tst     r0, #AT91C_MC_FRDY
	beq     wait_loop2

return:
	// return
	bx	lr

literals:
	// the compiler might store some values here
	.ltorg

eof:

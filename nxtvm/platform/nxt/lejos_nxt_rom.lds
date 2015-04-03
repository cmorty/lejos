/* This script is for a system that runs with the memory 
 * mapped so that ram is visible at both 0 and 2M.
 *
 * Notes on alignment:
 * 1) Sections should be 4-byte aligned otherwise ARM fetches
 *    will be misaligned.
 * 2) The stack should be 8-byte aligned for the APCS. That's because STRD and LDRD
 *    assume that they are on 8-byte boundaries.
 * 3) Align 16 is a good idea for any copied or initialised sections because this allows
 *    the faster LDM/STM code to run instead.
 */

/* Memory layout:
 *  ROM: .relocate .fixed
 *  RAM: .relocate .bss .stack <heap>
 * On boot, .relocate is copied from ROM to RAM in 16-byte chunks.
 * Then .bss is overwritten with zeros, also in 16-byte chunks.
 * Both need to be 4-byte aligned. As we use 16-byte chunks, we
 * may copy and initialize more than needed, but who cares ...
 */


/* 
 * Memory definitions for SAM7S256.
 */

MEMORY {
  ROM : ORIGIN = 1M, LENGTH = 256k
  RAM : ORIGIN = 2M, LENGTH =  64K
}

SECTIONS {

  .data : {
    KEEP(*(.vectors))
    *(.ramfunc .ramfunc.*)
    *(.data .data.*)
  } >RAM AT>ROM

  __relocate_vma_start__ = ADDR(.data);
  __relocate_vma_end__   = ADDR(.data) + SIZEOF(.data);
  __relocate_lma_start__ = LOADADDR(.data);
  __relocate_lma_end__   = LOADADDR(.data) + SIZEOF(.data);

  .text : {
    *(.rodata .rodata.*)
    *(.text .text.*)
  } >ROM

  .bss (NOLOAD) : ALIGN(16) {
    *(.bss .bss.*)
  } >RAM

  __bss_start__ = ADDR(.bss);
  __bss_end__   = ADDR(.bss) + SIZEOF(.bss);

  .stack (NOLOAD) : {
    *(.stack .stack.*)
  } >RAM

  __free_ram_start__ = ALIGN(ADDR(.stack) + SIZEOF(.stack), 16);
  __free_ram_end__   = ORIGIN(RAM) + LENGTH(RAM);
}

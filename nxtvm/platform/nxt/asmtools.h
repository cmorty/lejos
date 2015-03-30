#ifndef _ASMTOOLS_H_
#define _ASMTOOLS_H_

#ifdef __ASSEMBLER__
.macro global_data_label name
	.global \name
	.type   \name, %object
	\name :
.endm
.macro global_arm_func_label name
	.global \name
	.type   \name, %function
	\name :
.endm

.macro global_data_section name
	.section .data.\name
	.code 32
	.align 2
	global_data_label \name
.endm
.macro global_rodata_section name
	.section .rodata.\name
	.code 32
	.align 2
	global_data_label \name
.endm
.macro global_arm_func_section name
	.section .text.\name
	.code 32
	.align 2
	global_arm_func_label \name
.endm
#endif

#endif

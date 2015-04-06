#ifndef _LEJOS_NXT_H_
#define _LEJOS_NXT_H_

// macro to place function in RAM, not ROM
#define __ramfunc __attribute__((__section__(".ramfunc")))

// symbols exported by linker script
extern int __free_ram_start__;
extern int __free_ram_end__;

#endif

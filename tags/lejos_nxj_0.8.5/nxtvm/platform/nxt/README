Some quick notes on LEJOS NXT Building

You will need a Linux or equivalent environment with gcc arm tool chains.

To build, just type 'make'.

This should produce 3 output files:

lejos_nxt_rom.bin  binary rom image
lejos_nxt_ram.elf  ram image for debugging with jtag
lejos_nxt_samba_ram.xxx samba ram image used with runjava

A word about the make file structure.

targetdefs.mak   add files to the build here
enviromnet.mak   toolchain setup

sam7.lds         a file that is parse by sed to produce the linker scripts

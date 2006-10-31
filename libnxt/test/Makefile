TOOL_PREFIX=arm-elf-

all:
	$(TOOL_PREFIX)gcc -W -Wall -O3 -mcpu=arm7tdmi -mapcs -mthumb-interwork -c -o test.o test.c
	$(TOOL_PREFIX)as --warn -mcpu=arm7tdmi -mapcs-32 -EL -mthumb-interwork -o crt0.o crt0.s
	$(TOOL_PREFIX)ld -O3 --gc-sections crt0.o test.o -o test -lc
	$(TOOL_PREFIX)objcopy -O binary test test.bin
	$(TOOL_PREFIX)objdump --disassemble-all -b binary -m arm7tdmi test.bin > test.asm

clean:
	rm -f test.o test.bin test.asm

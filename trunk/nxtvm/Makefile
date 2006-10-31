TOOL_PREFIX=arm-elf-
#TOOL_PREFIX=
VM_SRC=javavm
NXT_SRC=platform/nxt
CFLAGS=-I$(VM_SRC) -I$(NXT_SRC) -W -Wall -O3 -mcpu=arm7tdmi -mapcs -mthumb-interwork -c
#CFLAGS=-I$(LEJOS_HOME)/src/javavm -I. -W -Wall -c

all:

	$(TOOL_PREFIX)gcc $(CFLAGS) -o main.o $(NXT_SRC)/main.c
	$(TOOL_PREFIX)gcc $(CFLAGS) -o native.o $(NXT_SRC)/native.c
	$(TOOL_PREFIX)gcc $(CFLAGS) -o interpreter.o $(VM_SRC)/interpreter.c
	$(TOOL_PREFIX)gcc $(CFLAGS) -o threads.o $(VM_SRC)/threads.c
	$(TOOL_PREFIX)gcc $(CFLAGS) -o exceptions.o $(VM_SRC)/exceptions.c
	$(TOOL_PREFIX)gcc $(CFLAGS) -o memory.o $(VM_SRC)/memory.c
	$(TOOL_PREFIX)gcc $(CFLAGS) -o language.o $(VM_SRC)/language.c
	$(TOOL_PREFIX)gcc $(CFLAGS) -o sensors.o $(NXT_SRC)/sensors.c
	$(TOOL_PREFIX)gcc $(CFLAGS) -o poll.o $(VM_SRC)/poll.c

	$(TOOL_PREFIX)as --warn -mcpu=arm7tdmi -mapcs-32 -EL -mthumb-interwork -o crt0.o $(NXT_SRC)/crt0.s
	$(TOOL_PREFIX)ld -O3 --section-start .text=202000 crt0.o main.o native.o interpreter.o threads.o exceptions.o memory.o language.o sensors.o poll.o -o nxt_lejos -lc -Map lejos.map
#	gcc main.o native.o interpreter.o threads.o exceptions.o memory.o language.o sensors.o poll.o -o nxt_lejos
	$(TOOL_PREFIX)objcopy -O binary nxt_lejos nxt_lejos.bin
	$(TOOL_PREFIX)objdump --disassemble-all -b binary -m arm7tdmi  nxt_lejos.bin > lejos.asm

clean:
	rm -f *.o

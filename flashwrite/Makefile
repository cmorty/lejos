#all:
#	gcc -Wall -O0 -mcpu=arm7tdmi-s -mapcs -mthumb-interwork -o flash.o flash.c -nostartfiles -nodefaultlibs -nostdlib -Wl,-e,main
#	objcopy -Obinary -j.text flash.o flash.bin
#	objdump --disassemble-all -bbinary -marm7tdmi flash.bin > flash.asm
#
TOOL_PREFIX=arm-elf-

.PHONY: all clean

all: flash.asm flash.bin.asm
	
clean:
	rm -f crt0.o flash.o flash flash.asm flash.bin flash.bin.asm

flash: crt0.o flash.o
	$(TOOL_PREFIX)ld --gc-sections -o '$@' $^

%.o: %.s
	$(TOOL_PREFIX)as --warn -mcpu=arm7tdmi -mfloat-abi=soft -mthumb-interwork -mlittle-endian -mapcs-32 -o '$@' '$<'
	
%.o: %.c
	$(TOOL_PREFIX)gcc -std=c99 -W -Wall -Os -mcpu=arm7tdmi -mfloat-abi=soft -mthumb-interwork -mlittle-endian -mapcs -c -o '$@' '$<'

%.bin: %
	$(TOOL_PREFIX)objcopy -O binary '$<' '$@'

%.bin.asm: %.bin
	$(TOOL_PREFIX)objdump -D -b binary -m arm7tdmi --endian=little '$<' >'$@'	

%.asm: %
	$(TOOL_PREFIX)objdump -d -s -m arm7tdmi '$<' >'$@'	
	
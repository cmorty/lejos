#all:
#	gcc -Wall -O0 -mcpu=arm7tdmi-s -mapcs -mthumb-interwork -o flash.o flash.c -nostartfiles -nodefaultlibs -nostdlib -Wl,-e,main
#	objcopy -Obinary -j.text flash.o flash.bin
#	objdump --disassemble-all -bbinary -marm7tdmi flash.bin > flash.asm
#
TOOL_PREFIX=arm-elf-

.PHONY: all clean

all: flash flash.asm flash.bin flash.bin.asm
	
clean:
	rm -f flash flash.asm flash.bin flash.bin.asm

%: %.S
	$(TOOL_PREFIX)gcc -nostdlib -Wall -mcpu=arm7tdmi -mfloat-abi=soft -mlittle-endian -o '$@' '$<'

%.bin: %
	$(TOOL_PREFIX)objcopy -O binary '$<' '$@'

%.bin.asm: %.bin
	$(TOOL_PREFIX)objdump -D -b binary -m arm7tdmi --endian=little '$<' >'$@'	

%.asm: %
	$(TOOL_PREFIX)objdump -d -s -m arm7tdmi '$<' >'$@'	

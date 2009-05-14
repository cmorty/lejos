#all:
#	gcc -Wall -O0 -mcpu=arm7tdmi-s -mapcs -mthumb-interwork -o flash.o flash.c -nostartfiles -nodefaultlibs -nostdlib -Wl,-e,main
#	objcopy -Obinary -j.text flash.o flash.bin
#	objdump --disassemble-all -bbinary -marm7tdmi flash.bin > flash.asm
#
TOOL_PREFIX=arm-elf-

.PHONY: all clean

all: flash.bin flash.asm
	
clean:
	rm -f crt0.o flash.o flash flash.bin flash.asm

flash: crt0.o flash.o
	$(TOOL_PREFIX)ld -O3 --gc-sections -o '$@' $^

%.o: %.s
	$(TOOL_PREFIX)as --warn -mcpu=arm7tdmi -mfloat-abi=soft -mthumb-interwork -mapcs-32 -EL -o '$@' '$<'
	
%.o: %.c
	$(TOOL_PREFIX)gcc -std=c99 -W -Wall -O3 -mcpu=arm7tdmi -mfloat-abi=soft -mthumb-interwork -mapcs -mlittle-endian -c -o '$@' '$<'

%.bin: %
	$(TOOL_PREFIX)objcopy -O binary '$<' '$@'

%.asm: %.bin
	$(TOOL_PREFIX)objdump --endian=little --disassemble-all -b binary -m arm7tdmi '$<' >'$@'	

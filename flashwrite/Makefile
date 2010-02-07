LEJOS_NXT_ARM_GCC ?= arm-elf-gcc

BASE_ABI_FLAGS := -mcpu=arm7tdmi -mlittle-endian -mfloat-abi=soft -mthumb-interwork

MACRO_PROG_PATH = $(abspath $(shell "$(CC)" -print-prog-name="$(1)"))

CC        := $(LEJOS_NXT_ARM_GCC)
OBJCOPY   := $(call MACRO_PROG_PATH,objcopy)
OBJDUMP   := $(call MACRO_PROG_PATH,objdump)

.PHONY: all clean
.SECONDARY: flash.elf flash.bin

all: flash.elf.asm flash.bin.asm
	
clean:
	rm -f flash.elf flash.elf.asm flash.bin flash.bin.asm FlashWrite.java 

FlashWrite.java: flash.bin
	python make_flash_header.py '$<' '$@'
	
%.elf: %.S
	$(CC) $(BASE_ABI_FLAGS) -Wall -nostdlib -o '$@' '$<'

%.bin: %.elf
	$(OBJCOPY) -O binary '$<' '$@'
	
%.elf.asm: %.elf
	$(OBJDUMP) -d -s '$<' >'$@'	

%.bin.asm: %.bin
	$(OBJDUMP) -D -b binary -m arm7tdmi --endian=little '$<' >'$@'

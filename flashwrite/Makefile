LEJOS_NXT_ARM_GCC ?= arm-elf-gcc

BASE_ABI_FLAGS := -mcpu=arm7tdmi -mlittle-endian -mfloat-abi=soft -mthumb-interwork

MACRO_PROG_PATH = $(abspath $(shell "$(CC)" -print-prog-name="$(1)"))

CC        := $(LEJOS_NXT_ARM_GCC)
OBJCOPY   := $(call MACRO_PROG_PATH,objcopy)
OBJDUMP   := $(call MACRO_PROG_PATH,objdump)

.PHONY: all clean
.SECONDARY: flash.elf flash.bin

all: flash.bin
	
clean:
	rm -f flash.elf flash.asm flash.bin

%.elf: %.S
	$(CC) $(BASE_ABI_FLAGS) -Wall -nostdlib -o '$@' '$<'

%.bin: %.elf
	$(OBJCOPY) -O binary '$<' '$@'
	
%.asm: %.elf
	$(OBJDUMP) -d -z -x '$<' >'$@'	

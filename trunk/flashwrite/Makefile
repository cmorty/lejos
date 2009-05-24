TARGET_PREFIX := arm-elf
COMP_PATH     := /opt/arm-elf-tools

pathsearch = $(if $(wildcard $(1)/$(2)),$(abspath $(1)/$(2)),$(2))
CC        := $(call pathsearch,$(COMP_PATH)/bin,$(TARGET_PREFIX)-gcc)
OBJCOPY   := $(call pathsearch,$(COMP_PATH)/bin,$(TARGET_PREFIX)-objcopy)
OBJDUMP   := $(call pathsearch,$(COMP_PATH)/bin,$(TARGET_PREFIX)-objdump)

.PHONY: all clean

all: flash flash.asm flash.bin flash.bin.asm
	
clean:
	rm -f flash flash.asm flash.bin flash.bin.asm

%: %.S
	$(CC) -nostdlib -Wall -mcpu=arm7tdmi -mfloat-abi=soft -mlittle-endian -o '$@' '$<'

%.bin: %
	$(OBJCOPY) -O binary '$<' '$@'

%.bin.asm: %.bin
	$(OBJDUMP) -D -b binary -m arm7tdmi --endian=little '$<' >'$@'	

%.asm: %
	$(OBJDUMP) -d -s -m arm7tdmi '$<' >'$@'	

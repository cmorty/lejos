BASE_ABI_FLAGS := -mcpu=arm7tdmi -mlittle-endian -mfloat-abi=soft -mthumb-interwork

TARGET_PREFIX := arm-elf
COMP_PATH     := /opt/$(TARGET_PREFIX)-tools

prefer_path   = $(if $(wildcard $(1)/$(2)),$(abspath $(1)/$(2)),$(2))
get_prog_path = $(abspath $(shell "$(CC)" -print-prog-name=$(1)))

CC        := $(call prefer_path,$(COMP_PATH)/bin,$(TARGET_PREFIX)-gcc)
OBJCOPY   := $(call get_prog_path,objcopy)
OBJDUMP   := $(call get_prog_path,objdump)

.PHONY: all clean

all: flash.elf flash.elf.asm flash.bin flash.bin.asm FlashWrite.java
	
clean:
	rm -f flash.elf flash.elf.asm flash.bin flash.bin.asm FlashWrite.java 

FlashWrite.java: flash.bin
	python make_flash_header.py '$<' '$@'
	
%.elf: %.S
	$(CC) $(BASE_ABI_FLAGS) -Wall -nostdlib -o '$@' '$<'

%.bin: %.elf
	$(OBJCOPY) -O binary '$<' '$@'
	
%.elf.asm: %.elf
	$(OBJDUMP) -d -s -m arm7tdmi '$<' >'$@'	

%.bin.asm: %.bin
	$(OBJDUMP) -D -b binary -m arm7tdmi --endian=little '$<' >'$@'

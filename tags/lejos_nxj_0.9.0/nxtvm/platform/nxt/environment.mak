LEJOS_NXT_ARM_GCC ?= arm-elf-gcc

BASE_ABI_FLAGS := -mcpu=arm7tdmi -mlittle-endian -mfloat-abi=soft -mthumb-interwork
LIB_ABI_FLAGS  := $(BASE_ABI_FLAGS) -mthumb

MACRO_PROG_PATH = $(abspath $(shell "$(CC)" -print-prog-name="$(1)"))
MACRO_LIB_PATH  = $(abspath $(shell "$(CC)" $(LIB_ABI_FLAGS) -print-file-name="$(1)"))

CC        := $(LEJOS_NXT_ARM_GCC)
LD        := $(call MACRO_PROG_PATH,ld)
OBJCOPY   := $(call MACRO_PROG_PATH,objcopy)
OBJDUMP   := $(call MACRO_PROG_PATH,objdump)

LIBGCC    := $(call MACRO_LIB_PATH,libgcc.a)
LIBC      := $(call MACRO_LIB_PATH,libc.a)
LIBM      := $(call MACRO_LIB_PATH,libm.a)

.PHONY: EnvironmentMessage
EnvironmentMessage:
	@echo " CC      $(CC)"
	@echo " LD      $(LD)"
	@echo " OBJCOPY $(OBJCOPY)"
	@echo " OBJDUMP $(OBJDUMP)"
	@echo " LIBGCC  $(LIBGCC)"
	@echo " LIBC    $(LIBC)"
	@echo " LIBM    $(LIBM)"
	@echo ""
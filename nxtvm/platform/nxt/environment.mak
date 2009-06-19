BASE_ABI_FLAGS := -mcpu=arm7tdmi -mlittle-endian -mfloat-abi=soft -mthumb-interwork
LIB_ABI_FLAGS := $(BASE_ABI_FLAGS) -mthumb

# add -mthumb to LIB_ABI_FLAGS to use thumb libraries

TARGET_PREFIX := arm-elf
COMP_PATH     := /opt/$(TARGET_PREFIX)-tools

prefer_path   = $(if $(wildcard $(1)/$(2)),$(abspath $(1)/$(2)),$(2))
get_prog_path = $(abspath $(shell "$(CC)" -print-prog-name=$(1)))
get_lib_path  = $(abspath $(shell "$(CC)" $(LIB_ABI_FLAGS) -print-file-name=$(1)))

CC        := $(call prefer_path,$(COMP_PATH)/bin,$(TARGET_PREFIX)-gcc)
AS        := $(call get_prog_path,as)
LD        := $(call get_prog_path,ld)
OBJCOPY   := $(call get_prog_path,objcopy)
OBJDUMP   := $(call get_prog_path,objdump)

LIBGCC    := $(call get_lib_path,libgcc.a)
LIBC      := $(call get_lib_path,libc.a)
LIBM      := $(call get_lib_path,libm.a)

.PHONY: EnvironmentMessage
EnvironmentMessage:
	@echo " CC      $(CC)"
	@echo " AS      $(AS)"
	@echo " LD      $(LD)"
	@echo " OBJCOPY $(OBJCOPY)"
	@echo " OBJDUMP $(OBJDUMP)"
	@echo " LIBGCC  $(LIBGCC)"
	@echo " LIBC    $(LIBC)"

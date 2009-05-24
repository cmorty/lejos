TARGET_PREFIX := arm-elf
COMP_PATH     := /opt/arm-elf-tools

pathsearch = $(if $(wildcard $(1)/$(2)),$(abspath $(1)/$(2)),$(2))
CC        := $(call pathsearch,$(COMP_PATH)/bin,$(TARGET_PREFIX)-gcc)
AS        := $(call pathsearch,$(COMP_PATH)/bin,$(TARGET_PREFIX)-as)
AR        := $(call pathsearch,$(COMP_PATH)/bin,$(TARGET_PREFIX)-ar)
LD        := $(call pathsearch,$(COMP_PATH)/bin,$(TARGET_PREFIX)-ld)
OBJCOPY   := $(call pathsearch,$(COMP_PATH)/bin,$(TARGET_PREFIX)-objcopy)
OBJDUMP   := $(call pathsearch,$(COMP_PATH)/bin,$(TARGET_PREFIX)-objdump)

LIBFLAGS := -mthumb-interwork
GCC_LIB  := $(realpath $(shell "$(CC)" $(LIBFLAGS) -print-libgcc-file-name))
LIBC     := $(realpath $(shell "$(CC)" $(LIBFLAGS) -print-file-name=libc.a))

.PHONY: EnvironmentMessage
EnvironmentMessage:
	@echo " CC      $(CC)"
	@echo " AS      $(AS)"
	@echo " AR      $(AR)"
	@echo " LD      $(LD)"
	@echo " OBJCOPY $(OBJCOPY)"
	@echo " OBJDUMP $(OBJDUMP)"
	@echo " LIBGCC  $(GCC_LIB)"
	@echo " LIBC    $(LIBC)"


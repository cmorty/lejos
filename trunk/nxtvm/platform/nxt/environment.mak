

  COMP_PATH     := /opt/arm-elf-tools
  TARGET_PREFIX := arm-elf

  pathsearch = $(if $(wildcard $(2)/$(1)),$(2)/$(1),$(1))

  CC       := $(call pathsearch,$(TARGET_PREFIX)-gcc,$(COMP_PATH)/bin)
  AS       := $(call pathsearch,$(TARGET_PREFIX)-as,$(COMP_PATH)/bin)
  AR       := $(call pathsearch,$(TARGET_PREFIX)-ar,$(COMP_PATH)/bin)
  LD       := $(call pathsearch,$(TARGET_PREFIX)-ld,$(COMP_PATH)/bin)
  OBJCOPY  := $(call pathsearch,$(TARGET_PREFIX)-objcopy,$(COMP_PATH)/bin)

  LIBFLAGS := -mthumb-interwork
  GCC_LIB  := $(realpath $(shell "$(CC)" $(LIBFLAGS) -print-libgcc-file-name))
  LIBC     := $(realpath $(shell "$(CC)" $(LIBFLAGS) -print-file-name=libc.a))

PHONY: EnvironmentMessage
EnvironmentMessage:
	@echo " CC      $(CC)"
	@echo " AS      $(AS)"
	@echo " AR      $(AR)"
	@echo " LD      $(LD)"
	@echo " OBJCOPY $(OBJCOPY)"
	@echo " LIBGCC  $(GCC_LIB)"
	@echo " LIBC    $(LIBC)"


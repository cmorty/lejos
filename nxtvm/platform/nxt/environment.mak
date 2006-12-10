
  GCC_VERSION :=4.0.2
  COMP_PATH   := /opt/arm-elf-tools/
  LIBPREFIX   := $(COMP_PATH)/arm-elf/lib
  GCC_LIB     := $(COMP_PATH)/lib/gcc/arm-elf/$(GCC_VERSION)/interwork/libgcc.a
  LIBC        := $(COMP_PATH)/arm-elf/lib/interwork/libc.a

  TARGET_PREFIX :=arm-elf
  INC_PATH := $(COMP_PATH)/$(TARGET_PREFIX)/include
  CC       := $(COMP_PATH)/bin/$(TARGET_PREFIX)-gcc
  AS       := $(COMP_PATH)/bin/$(TARGET_PREFIX)-as
  AR       := $(COMP_PATH)/bin/$(TARGET_PREFIX)-ar
  LD       := $(COMP_PATH)/bin/$(TARGET_PREFIX)-ld
  OBJCOPY  := $(COMP_PATH)/bin/$(TARGET_PREFIX)-objcopy

PHONY: EnvironmentMessage
EnvironmentMessage:
	@echo " CC      $(CC)"
	@echo " AS      $(AS)"
	@echo " AR      $(AR)"
	@echo " LD      $(LD)"
	@echo " OBJCOPY $(OBJCOPY)"


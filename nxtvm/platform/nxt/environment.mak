
  GCC_VERSION :=3.4.3
  COMP_PATH   := /usr/local/gnuarm-3.4.3
  NEWLIB_PATH := /usr/local
  LIBPREFIX        := /usr/local/arm-elf/lib
  GCC_LIB     := $(COMP_PATH)/lib/gcc/arm-elf/3.4.3/interwork/libgcc.a
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


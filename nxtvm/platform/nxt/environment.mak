
  COMP_PATH   := /opt/arm-elf-tools

  TARGET_PREFIX :=arm-elf
  CC       := $(shell export PATH="$(COMP_PATH)/bin:$$PATH"; which "$(TARGET_PREFIX)-gcc")
  AS       := $(shell export PATH="$(COMP_PATH)/bin:$$PATH"; which "$(TARGET_PREFIX)-as")
  AR       := $(shell export PATH="$(COMP_PATH)/bin:$$PATH"; which "$(TARGET_PREFIX)-ar")
  LD       := $(shell export PATH="$(COMP_PATH)/bin:$$PATH"; which "$(TARGET_PREFIX)-ld")
  OBJCOPY  := $(shell export PATH="$(COMP_PATH)/bin:$$PATH"; which "$(TARGET_PREFIX)-objcopy")

  LIBFLAGS := -mthumb-interwork
  GCC_LIB  := $(shell "$(CC)" $(LIBFLAGS) -print-libgcc-file-name)
  LIBC     := $(shell "$(CC)" $(LIBFLAGS) -print-file-name=libc.a)

PHONY: EnvironmentMessage
EnvironmentMessage:
	@echo " CC      $(CC)"
	@echo " AS      $(AS)"
	@echo " AR      $(AR)"
	@echo " LD      $(LD)"
	@echo " OBJCOPY $(OBJCOPY)"
	@echo " LIBGCC  $(GCC_LIB)"
	@echo " LIBC    $(LIBC)"


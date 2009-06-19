.SUFFIXES:

default: def_target

include environment.mak
include targetdef.mak
include version.mak

RAM_TARGET   := $(TARGET)_ram.elf
ROM_TARGET   := $(TARGET)_rom.elf
SAMBA_TARGET := $(TARGET)_samba_ram.bin
ROMBIN_TARGET := $(TARGET)_rom.bin

RAM_LDSCRIPT   := $(TARGET)_ram.ld
ROM_LDSCRIPT   := $(TARGET)_rom.ld
SAMBA_LDSCRIPT := $(TARGET)_samba.ld

S_OBJECTS := $(S_SOURCES:.s=.o)
C_OBJECTS := $(C_SOURCES:.c=.o) $(C_RAMSOURCES:.c=.oram)

C_OPTIMISATION_FLAGS = -Os
#C_OPTIMISATION_FLAGS = -Os -Xassembler -aslh
#C_OPTIMISATION_FLAGS = -O0

SVNDEF := -DSVN_REV=$(shell svnversion -n ../.. | grep -o "[^:]*$$" | grep -o "[0-9]*")

CFLAGS = $(BASE_ABI_FLAGS) -mthumb \
	-ffreestanding -fsigned-char \
	$(C_OPTIMISATION_FLAGS) -g \
	-Wall -Winline -Werror-implicit-function-declaration \
	-I. -I$(VM_DIR) \
	-ffunction-sections -fdata-sections \
    $(SVNDEF) -DMAJOR_VERSION=$(MAJOR_VERSION) -DMINOR_VERSION=$(MINOR_VERSION)

LDFLAGS = -Map $@.map -cref --gc-sections

ASFLAGS = $(BASE_ABI_FLAGS)

def_target: all


ALL_TARGETS := $(ROM_TARGET) $(ROMBIN_TARGET) 


.PHONY:  all
all:  BuildMessage $(ALL_TARGETS)

PHONY: TargetMessage
TargetMessage:
	@echo ""
	@echo "Building: $(ALL_TARGETS)"
	@echo ""
	@echo "C sources: $(C_SOURCES) to $(C_OBJECTS)"
	@echo ""
	@echo "Assembler sources: $(S_SOURCES) to $(S_OBJECTS)"
	@echo ""
	@echo "LD source: $(LDSCRIPT_SOURCE)"
	@echo ""

PHONY: BuildMessage
BuildMessage: TargetMessage EnvironmentMessage


$(SAMBA_LDSCRIPT): $(LDSCRIPT_SOURCE)
	cat $< | sed -e 's/^SAMBA_ONLY//' -e '/^RAM_ONLY/d' -e'/^ROM_ONLY/d' >$@

$(RAM_LDSCRIPT): $(LDSCRIPT_SOURCE)
	cat $< | sed -e 's/^RAM_ONLY//' -e'/^ROM_ONLY/d' -e'/^SAMBA_ONLY/d' >$@

$(ROM_LDSCRIPT): $(LDSCRIPT_SOURCE)
	cat $< | sed -e 's/^ROM_ONLY//' -e'/^RAM_ONLY/d' -e'/^SAMBA_ONLY/d' >$@

$(SAMBA_TARGET)_elf: $(C_OBJECTS) $(S_OBJECTS) $(SAMBA_LDSCRIPT)
	@echo "Linking $@"
	$(LD) -o $@ $(C_OBJECTS) $(S_OBJECTS) -T $(SAMBA_LDSCRIPT) $(LIBC) $(LIBGCC) $(LDFLAGS)

$(RAM_TARGET): $(C_OBJECTS) $(S_OBJECTS) $(RAM_LDSCRIPT)
	@echo "Linking $@"
	$(LD) -o $@ $(C_OBJECTS) $(S_OBJECTS) -T $(RAM_LDSCRIPT) $(LIBC) $(LIBGCC) $(LDFLAGS)

$(ROM_TARGET): $(C_OBJECTS) $(S_OBJECTS) $(ROM_LDSCRIPT)
	@echo "Linking $@"
	$(LD) -o $@ $(C_OBJECTS) $(S_OBJECTS) -T $(ROM_LDSCRIPT) $(LIBM) $(LIBC) $(LIBGCC) $(LDFLAGS)

$(ROMBIN_TARGET): $(ROM_TARGET)
	@echo "Generating binary file $@"
	$(OBJCOPY) -O binary $< $@

$(SAMBA_TARGET): $(SAMBA_TARGET)_elf
	@echo "Generating binary file $@"
	$(OBJCOPY) -O binary $< $@

%.o: %.s
	@echo "Assembling $< to $@"
	$(AS) $(ASFLAGS) -o $@ $< 

%.o: %.c
	@echo "Compiling $< to $@"
	$(CC) $(CFLAGS) -c -o $@ $< 

../../javavm/interpreter.o: ../../javavm/interpreter.c
	@echo "Compiling $< to $@"
	$(CC) $(CFLAGS) -O3 -c -o $@ $< 

%.asm: %.c
	@echo "Compiling $< to $@"
	$(CC) -S $(CFLAGS) -g0 -o $@ $< 

../../javavm/interpreter.asm: ../../javavm/interpreter.c
	@echo "Compiling $< to $@"
	$(CC) -S $(CFLAGS) -g0 -O3 -o $@ $< 

%.oram: %.c
	@echo "Compiling $< to $@"
	$(CC) $(CFLAGS) -c -o $@ $< 


.PHONY: clean
clean:  
	@echo "Removing All Objects"
	@rm -f $(S_OBJECTS) $(C_OBJECTS) *.o
	@echo "Removing generated ld scripts"
	@rm  -f *.ld
	@echo "Removing target"
	@rm -f $(ALL_TARGETS)
	@echo "Removing map files"
	@ rm -f *map

-include $(C_SOURCES:.c=.d)


.SUFFIXES:

default: all

include environment.mak
include targetdef.mak
include version.mak

RAM_TARGET   := $(TARGET)_ram.elf
ROM_TARGET   := $(TARGET)_rom.elf
SAMBA_TARGET := $(TARGET)_samba.elf
ROMBIN_TARGET := $(TARGET)_rom.bin

RAM_LDSCRIPT   := $(TARGET)_ram.ld
ROM_LDSCRIPT   := $(TARGET)_rom.ld
SAMBA_LDSCRIPT := $(TARGET)_samba.ld

C_OPTIMISATION_FLAGS = -Os
#C_OPTIMISATION_FLAGS = -Os -Xassembler -aslh
#C_OPTIMISATION_FLAGS = -O0

SVN_REV := $(shell svnversion -n ../.. | grep -o "[^:]*$$" | grep -o "[0-9]*")

CFLAGS = $(BASE_ABI_FLAGS) -mthumb \
	-ffreestanding -fsigned-char \
	$(C_OPTIMISATION_FLAGS) -g \
	-Wall -Winline -Werror-implicit-function-declaration \
	-I. -I$(VM_DIR) \
	-ffunction-sections -fdata-sections \
	-DSVN_REV=$(SVN_REV) -DVERSION_NUMBER=$(VERSION_NUMBER)

LDFLAGS = -cref --gc-sections

ALL_ELF := $(RAM_TARGET) $(ROM_TARGET) $(SAMBA_TARGET)
ALL_BIN := $(ALL_ELF:.elf=.bin)
ALL_LDS := $(ALL_ELF:.elf=.ld)
ALL_MAP := $(addsuffix .map,$(ALL_ELF))
ALL_OBJECTS := $(C_OBJECTS) $(S_OBJECTS)
ALL_ASM := $(addsuffix .asm,$(ALL_ELF)) $(addsuffix .asm,$(ALL_OBJECTS))
ALL_HEAD := $(VM_DIR)/specialclasses.h $(VM_DIR)/specialsignatures.h

MACRO_LDS_GEN = sed -e 's/^$(1)//' -e '/^RAM_ONLY/d' -e'/^ROM_ONLY/d' -e'/^SAMBA_ONLY/d'

.SECONDARY: $(ALL_ELF) $(ALL_LDS) $(ALL_MAP) $(ALL_OBJECTS) $(ALL_HEAD)

.PHONY: all
all:  BuildMessage $(ROMBIN_TARGET)

.PHONY: everything
everything: BuildMessage $(ALL_BIN) $(ALL_ASM)

.PHONY: header
header: $(ALL_HEAD)

.PHONY: TargetMessage
TargetMessage:
	@echo ""
	@echo "Building: $(ALL_TARGETS)"
	@echo ""
	@echo "C objects: $(C_OBJECTS)"
	@echo ""
	@echo "Assembler objects: $(S_OBJECTS)"
	@echo ""
	@echo "LD source: $(LDSCRIPT_SOURCE)"
	@echo ""

.PHONY: BuildMessage
BuildMessage: TargetMessage EnvironmentMessage

.PHONY: clean
clean:  
	@echo "Removing All Objects"
	@rm -f $(ALL_OBJECTS)
	@echo "Removing generated ld scripts"
	@rm -f $(ALL_LDS)
	@echo "Removing target"
	@rm -f $(ALL_ELF) $(ALL_BIN)
	@echo "Removing map files"
	@rm -f $(ALL_MAP)
	@echo "Removing asm files"
	@rm -f $(ALL_ASM)
	@echo "Removing generated headers"
	@rm -f $(ALL_HEAD)


$(RAM_LDSCRIPT): $(LDSCRIPT_SOURCE)
	@echo "Generating $@ from template $<"
	$(call MACRO_LDS_GEN,RAM_ONLY) $< >$@

$(ROM_LDSCRIPT): $(LDSCRIPT_SOURCE)
	@echo "Generating $@ from template $<"
	$(call MACRO_LDS_GEN,ROM_ONLY) $< >$@

$(SAMBA_LDSCRIPT): $(LDSCRIPT_SOURCE)
	@echo "Generating $@ from template $<"
	$(call MACRO_LDS_GEN,SAMBA_ONLY) $< >$@

%.elf.map %.elf: %.ld $(C_OBJECTS) $(S_OBJECTS)
	@echo "Linking $@ using linker script $<"
	$(LD) $(LDFLAGS) -T $< -Map $@.map -o $@ $(C_OBJECTS) $(S_OBJECTS) $(LIBM) $(LIBC) $(LIBGCC)

%.bin: %.elf
	@echo "Generating binary file $@ from $<"
	$(OBJCOPY) -O binary $< $@

# generated headers:

$(VM_DIR)/specialclasses.h: $(VM_DIR)/specialclasses.db
	../../dbtoh.sh class $< $@ 

$(VM_DIR)/specialsignatures.h: $(VM_DIR)/specialsignatures.db
	../../dbtoh.sh signature $< $@ 


# default rules for compiling sources

%.o: %.s
	@echo "Assembling $< to $@"
	$(CC) $(CFLAGS) -c -o $@ $< 

%.o: %.c $(ALL_HEAD)
	@echo "Compiling $< to $@"
	$(CC) $(CFLAGS) -c -o $@ $< 


### special rules for compiling RAM sources

%.oram: %.s
	@echo "Assembling $< to $@"
	$(CC) $(CFLAGS) -c -o $@ $< 

%.oram: %.c $(ALL_HEAD)
	@echo "Compiling $< to $@"
	$(CC) $(CFLAGS) -c -o $@ $< 


### special rules for compiling JVM sources

$(VM_PREFIX)%.o: $(VM_DIR)/%.c $(ALL_HEAD)
	@echo "Compiling $< to $@"
	$(CC) $(CFLAGS) -c -o $@ $< 

$(VM_PREFIX)interpreter.o: $(VM_DIR)/interpreter.c $(ALL_HEAD)
	@echo "Compiling $< to $@"
	$(CC) $(CFLAGS) -O3 -c -o $@ $< 


### rules disassembling

%.asm: %
	@echo "Disassembling $< to $@"
	$(OBJDUMP) -z -d $< >$@


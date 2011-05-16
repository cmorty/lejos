#
# This file defines the source and target file names
#

# TARGET is the base name for the outputs.
# C_RAMSOURCES are the C files that must always be located in RAM.
# C_SOURCES are the rest of the C files
# S_SOURCES are the assembler files

VM_DIR := ../../javavm
VM_PREFIX := jvm_

TARGET := lejos_nxt

C_RAM_OBJECTS := \
	flashprog.oram

C_PLATFORM_OBJECTS := \
	aic.o \
	systick.o \
	udp.o \
	twi.o \
	nxt_spi.o \
	nxt_motors.o \
	data_abort.o \
	display.o \
	i2c.o \
	sound.o \
	bt.o \
	hs.o \
	printf.o

C_HOOK_OBJECTS := \
	main.o \
	nxt_avr.o \
	sensors.o \
	nxt_lcd.o \
	native.o \
	platform_hooks.o

C_VM_OBJECTS := \
	$(VM_PREFIX)interpreter.o \
	$(VM_PREFIX)threads.o \
	$(VM_PREFIX)exceptions.o \
	$(VM_PREFIX)memory.o \
	$(VM_PREFIX)language.o \
	$(VM_PREFIX)poll.o \
	$(VM_PREFIX)debug.o

C_OBJECTS := \
	$(C_PLATFORM_OBJECTS) \
	$(C_HOOK_OBJECTS) \
	$(C_RAM_OBJECTS) \
	$(C_VM_OBJECTS)

S_OBJECTS := \
	interrupts.o \
	vectors.o \
	init.o \
	irq.o


LDSCRIPT_SOURCE := sam7.lds

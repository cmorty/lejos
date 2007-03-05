#
# This file defines the source and target file names
#

# TARGET is the base name for the outputs.
# C_RAMSOURCES are the C files that must always be located in RAM.
# C_SOURCES are the rest of the C files
# S_SOURCES are the assembler files

VM_DIR := ../../javavm

TARGET := lejos_nxt

C_RAMSOURCES := flashprog.c

C_PLATFORM_SOURCES := \
	uart.c \
	byte_fifo.c \
	aic.c \
	systick.c \
	udp.c \
	twi.c \
	nxt_spi.c \
	nxt_motors.c \
	data_abort.c \
	display.c \
	i2c.c \
	sound.c \
	bt.c

C_HOOK_SOURCES := \
	main.c \
	nxt_avr.c \
	sensors.c \
	nxt_lcd.c \
	native.c \
	platform_hooks.c

C_VM_SOURCES := \
	$(VM_DIR)/interpreter.c \
	$(VM_DIR)/threads.c \
	$(VM_DIR)/exceptions.c \
	$(VM_DIR)/memory.c \
	$(VM_DIR)/language.c \
	$(VM_DIR)/poll.c

C_SOURCES := $(C_PLATFORM_SOURCES) $(C_VM_SOURCES) $(C_HOOK_SOURCES)

S_SOURCES := init.s interrupts.s vectors.s irq.s

LDSCRIPT_SOURCE := sam7.lds

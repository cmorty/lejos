JAVA_HOME ?= JAVA_HOME_not_set
JNI_INCLUDE ?= $(JAVA_HOME)/include
JNI_INCLUDE_LINUX ?= $(JNI_INCLUDE)/linux
LIBUSB_INCLUDE ?= /usr/include/libusb-1.0

CFLAGS ?= 
CFLAGS += -std=c99 -Wall -fPIC -I "$(JNI_INCLUDE)" -I "$(JNI_INCLUDE_LINUX)" -I "$(LIBUSB_INCLUDE)"

LDFLAGS ?=
LDFLAGS += -s -shared -Wl,--version-script=symbols-gnu.txt

FILES_IN_C:=jlibnxt.c
FILES_OUT_O:=$(FILES_IN_C:.c=.o)
FILES_OUT_LIB:=libjlibnxt.so

.PHONY: all clean

# All Target
all: $(FILES_OUT_LIB)

# Other Targets
clean:
	rm -rf \
		$(FILES_OUT_O) \
		$(FILES_OUT_LIB)

# Tool invocations
$(FILES_OUT_LIB): $(FILES_OUT_O)
	@echo '### Building target: $@'
	gcc $(LDFLAGS) -o "$@" $(FILES_OUT_O) -lusb

%.o: %.c
	@echo '### Compiling $< to $@'
	gcc $(CFLAGS) -o "$@" -c "$<"

# we don't use .d files at the moment
#	-MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@:%.o=%.d)"

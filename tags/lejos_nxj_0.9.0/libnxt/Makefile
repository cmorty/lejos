JAVA_HOME ?= JAVA_HOME_not_set
JNI_INCLUDE ?= $(JAVA_HOME)/include
JNI_INCLUDE_LINUX ?= $(JNI_INCLUDE)/linux

CFLAGS ?= 
CFLAGS += -std=gnu99 -Wall -fPIC -I$(JNI_INCLUDE) -I$(JNI_INCLUDE_LINUX)

LDFLAGS ?=
LDFLAGS += -shared -lusb 

FILES_IN_C:=$(wildcard *.c)
FILES_OUT_D:=$(FILES_IN_C:.c=.d)
FILES_OUT_O:=$(FILES_IN_C:.c=.o)
FILES_OUT_LIB:=libjlibnxt.so

.PHONY: all clean

# All Target
all: $(FILES_OUT_LIB)

# Other Targets
clean:
	rm -rf \
		$(FILES_OUT_D) \
		$(FILES_OUT_O) \
		$(FILES_OUT_LIB)

# Tool invocations
$(FILES_OUT_LIB): $(FILES_OUT_O)
	@echo 'Building target: $@'
	@echo 'Invoking: GCC C Linker'
	gcc $(LDFLAGS) -o "$@" $(FILES_OUT_O)
	@echo 'Finished building target: $@'
	@echo ' '

$(FILES_OUT_O): %.o: %.c
	@echo 'Compiling file: $<'
	@echo 'Invoking: GCC C Compiler'
	gcc $(CFLAGS) -o "$@" -c "$<"
	@echo 'Finished compiling: $<'

# we don't use .d files at the moment
#	-MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@:%.o=%.d)"

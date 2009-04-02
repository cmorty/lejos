FILES_IN_C:=$(wildcard *.c)
FILES_OUT_O:=$(patsubst %.c,%.o,$(FILES_IN_C))
FILES_OUT_LIB:=libjbluez.so

.PHONY: all clean

# All Target
all: $(FILES_OUT_LIB)

# Other Targets
clean:
	rm -rf $(FILES_OUT_O) $(FILES_OUT_LIB)

# Tool invocations
$(FILES_OUT_LIB): $(FILES_OUT_O)
	@echo 'Building target: $@'
	@echo 'Invoking: GCC C Linker'
	gcc -shared -lbluetooth -o"libjbluez.so" $(OBJS)
	@echo 'Finished building target: $@'
	@echo ' '

$(FILES_OUT_O): %.o: %.c
	@echo 'Compiling file: $<'
	@echo 'Invoking: GCC C Compiler'
	gcc -O0 -g3 -Wall -c -fmessage-length=0 \
		-I$(JAVA_HOME)/include -I$(JAVA_HOME)/include/linux \
		-MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@:%.o=%.d)" \
		-o"$@" "$<"
	@echo 'Finished compiling: $<'

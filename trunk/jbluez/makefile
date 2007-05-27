OBJS = jbluez.o

# All Target
all: libjbluez.so

# Tool invocations
libjbluez.so: $(OBJS)
	@echo 'Building target: $@'
	@echo 'Invoking: GCC C Linker'
	gcc -shared -o"libjbluez.so" $(OBJS)  -lbluetooth
	@echo 'Finished building target: $@'
	@echo ' '

# Other Targets
clean:
	rm -rf libjbluez.so

.PHONY: all clean dependents

%.o: ../%.c
	@echo 'Building file: $<'
	@echo 'Invoking: GCC C Compiler'
	gcc -I$JAVA_HOME/include -I$JAVA_HOME/include/linux -O0 -g3 -Wall -c -fmessage-length=0 -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@:%.o=%.d)" -o"$@" "$<"
	@echo 'Finished building: $<'
	@echo ' 

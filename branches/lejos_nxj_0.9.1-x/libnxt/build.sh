#export JAVA_HOME=/c:/j2sdk1.4.2_01
export JAVA_HOME=/c/progra~1/Java/jdk1.6.0_06
export USB_HOME=/c/progra~1/LibUSB-Win32
gcc -I$JAVA_HOME/include -I$JAVA_HOME/include/win32 -I$USB_HOME/include -Wall -std=gnu99 -g -ggdb -D_NXT_LITTLE_ENDIAN -c *.c
gcc -I$JAVA_HOME/include -I$JAVA_HOME/include/win32 -I$USB_HOME/include -shared -o jlibnxt.dll -D_JNI_IMPLEMENTATION_ -Wl,--kill-at main_jlibnxt.c -L$USB_HOME/lib/gcc -L. -lusb

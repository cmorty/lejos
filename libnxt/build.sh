export JAVA_HOME=/c:/j2sdk1.4.2_01
gcc -I$JAVA_HOME/include -I$JAVA_HOME/include/win32 -Wall -std=gnu99 -g -ggdb -D_NXT_LITTLE_ENDIAN -c *.c
ar r libnxt.a error.o firmware.o flash.o lowlevel.o samba.o
gcc -o fwflash.exe main_fwflash.o -L. -lnxt -lusb
gcc -o runc.exe main_runc.o -L. -lnxt -lusb
gcc -o nxjupload.exe main_nxjupload.o -L. -lnxt -lusb
gcc -o nxjflash.exe main_nxjflash.o -L. -lnxt -lusb
gcc -I$JAVA_HOME/include -I$JAVA_HOME/include/win32 -shared -o jlibnxt.dll -D_JNI_IMPLEMENTATION_ -Wl,--kill-at main_jlibnxt.c -L. -lnxt -lusb

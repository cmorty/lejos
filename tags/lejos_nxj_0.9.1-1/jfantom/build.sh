#export JAVA_HOME=/c:/j2sdk1.4.2_01
export JAVA_HOME=/c/progra~1/Java/jdk1.6.0_11
export FANTOM_HOME=/h/Lego/fantom
gcc \
	-I$JAVA_HOME/include \
	-I$JAVA_HOME/include/win32 \
	-I$FANTOM_HOME/includes \
	-shared \
	-o jfantom.dll \
	-D_JNI_IMPLEMENTATION_ \
	-Wl,--kill-at \
	jfantom.cpp \
	-L$USB_HOME/lib/gcc \
	-L. \
	$FANTOM_HOME/targets/win32U/i386/msvc71/release/fantom.lib

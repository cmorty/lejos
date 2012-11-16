#!/bin/bash
LIBUSBX_VER=1.0.14
GCC_VER=4.2

OSX_VER=10.5
SYSROOT=/Developer/SDKs/MacOSX10.5.sdk

JAVA_INC=$SYSROOT/System/Library/Frameworks/JavaVM.framework/Versions/1.5.0/Headers
LIBUSBX_DIR=$(pwd)/libusbx-build

#rm -rf "libusbx-$LIBUSBX_VER"
tar -xf "libusbx-$LIBUSBX_VER.tar.bz2"

(
	cd "libusbx-$LIBUSBX_VER"

	CC="gcc-$GCC_VER" \
	CFLAGS="-arch ppc -arch ppc64 -arch i386 -arch x86_64 -mmacosx-version-min=$OSX_VER -isysroot $SYSROOT" \
	./configure --disable-dependency-tracking --prefix="$LIBUSBX_DIR"

	make clean all install
)

"gcc-$GCC_VER" -arch ppc -arch ppc64 -arch i386 -arch x86_64 -dynamiclib \
	-mmacosx-version-min="$OSX_VER" -isysroot "$SYSROOT" \
	-std=c99 -I "$JAVA_INC" -I "$LIBUSBX_DIR/include/libusb-1.0" \
	-o libjlibnxt.jnilib \
	-exported_symbols_list symbols-osx.txt \
	-lobjc -framework IOKit -framework CoreFoundation \
	$LIBUSBX_DIR/lib/libusb-1.0.a \
	jlibnxt.c



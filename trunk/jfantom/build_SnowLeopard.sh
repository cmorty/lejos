# Script for Snow Leopard

# gcc 4.0 must be used to compile for OS X 10.4
GCCVER=4.0
SYSVER=10.4
SYSROOT=/Developer/SDKs/MacOSX10.4u.sdk
JAVA_INC="$SYSROOT/System/Library/Frameworks/JavaVM.framework/Versions/1.5.0/Headers"
FANTOM_INC=~/Downloads/Lego/fantom/includes

# We create an empty "fake" dynamic library, in order to override the location of NiSpy
"g++-$GCCVER" -arch ppc -arch i386 -dynamiclib \
	-mmacosx-version-min="$SYSVER" \
	-isysroot "$SYSROOT" \
	-o fakeNiSpy.dylib \
	fakeNiSpy.cpp

# We link using the fake NiSpy library. This will not show up in the created libjfantom.jnilib.
"g++-$GCCVER" -arch ppc -arch i386 -dynamiclib \
	-mmacosx-version-min="$SYSVER" \
	-isysroot "$SYSROOT" \
	-I "$FANTOM_INC" \
	-I "$JAVA_INC" \
	-framework Fantom \
	-dylib_file /Library/Frameworks/NiSpyLog.framework/Versions/2/NiSpyLog:fakeNiSpy.dylib \
	-o libjfantom.jnilib \
	jfantom.cpp


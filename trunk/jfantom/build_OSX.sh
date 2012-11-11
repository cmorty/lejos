export JAVA_HOME=/System/Library/Frameworks/JavaVM.framework/Versions/1.5.0/Home
export FANTOM_HOME=~/devel/lejos/fantom

# works for leopard and build a universal binary
gcc -arch ppc -arch i386 -mmacosx-version-min=10.4 -isysroot /Developer/SDKs/MacOSX10.4u.sdk -I$JAVA_HOME/include -dynamiclib -I $FANTOM_HOME/includes/ -o libjfantom.jnilib -framework Fantom -dylib_file /Library/Frameworks/VISA.framework/Versions/A/VISA:/Library/Frameworks/VISA.framework/Versions/A/VISA -lstdc++ jfantom.cpp

# for snow leopard the 32 bit flag (-m32) must be set  
# from Java 6 rev 3, jni.h is in the Java Developer package (/System/Library/Frameworks/JavaVM.framework/Headers)
# however -arch ppc does not seem buildable on snow leopard.  It may require and earlier version of XTools to work.
#gcc -m32  -arch i386 -mmacosx-version-min=10.5  -I /System/Library/Frameworks/JavaVM.framework/Headers -dynamiclib -I $FANTOM_HOME/includes/ -o libjfantom.jnilib -framework Fantom -dylib_file Library/Frameworks/VISA.framework/Versions/A/VISA:/Library/Frameworks/VISA.framework/Versions/A/VISA -lstdc++ jfantom.cpp

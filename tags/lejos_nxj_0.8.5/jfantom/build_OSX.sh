export JAVA_HOME=/System/Library/Frameworks/JavaVM.framework/Versions/1.5.0/Home

export FANTOM_HOME=~/dev/lejos/fantom_home/fantom_sdk

gcc -arch ppc -arch i386 -mmacosx-version-min=10.4 -isysroot /Developer/SDKs/MacOSX10.4u.sdk -I$JAVA_HOME/include -dynamiclib -I $FANTOM_HOME/includes/ -o libjfantom.jnilib -framework Fantom -dylib_file Library/Frameworks/VISA.framework/Versions/A/VISA:/Library/Frameworks/VISA.framework/Versions/A/VISA -lstdc++ jfantom.cpp
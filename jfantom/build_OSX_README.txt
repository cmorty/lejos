 libjfantom.jnilib is a prebuilt universal binary (i386 ppc) for Tiger and Leopard which you can rebuild by following the steps below

----------------------------------------------------------------------------------------------

***************************
To build the fantom driver for OS X:
***************************

1) make sure you have OS X developer tools installed http://developer.apple.com/technology/xcode.html

2) install the VISA and NiSpyLog frameworks from National Instruments. http://www.ni.com/support/ (you need to register)

	Note:  get NI-VISA (about 57mb) and not the runtime engine (5mb)

3) install the SDK from http://mindstorms.lego.com/Overview/nxtreme.aspx

4) set your JAVA_HOME in ./build_OSX.sh

5) set your FANTOM_HOME in  ./build_OSX.sh

	This should be set to fantom_sdk from the mindostorms SDK  (step 3)

6)  run ./build_OSX.sh 

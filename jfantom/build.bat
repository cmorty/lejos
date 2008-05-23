set JAVA_HOME=c:\j2sdk1.4.2_05
cl /I . /I%JAVA_HOME%\include /I%JAVA_HOME%\include\win32 fantom.lib jfantom.cpp /LD

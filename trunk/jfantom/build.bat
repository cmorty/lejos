rem set JAVA_HOME=c:\j2sdk1.4.2_05
set FANTOM=..\..\..\fantom
cl /I . /I %FANTOM%\includes /I "%JAVA_HOME%\include" /I "%JAVA_HOME%\include\win32" %FANTOM%\targets\win32U\i386\msvc71\release\fantom.lib jfantom.cpp /LD

@echo off
if "%NXJ_HOME%" == ""  goto homeless

set THIRDPARTY_LIBS="%NXJ_HOME%\3rdparty\lib"

java -Djava.library.path="%NXJ_HOME%\bin" -classpath "%NXJ_HOME%\lib\pctools.jar;%NXJ_HOME%\lib\pccomm.jar;%NXJ_HOME%\3rdparty\lib\bluecove.jar" lejos.pc.tools.NXJBrowser %1 %2 %3 %4 %5 %6 %7 %8 %9
goto end

:homeless
echo NXJ_HOME not defined

:end
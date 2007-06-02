@echo off
if "%LEJOS_HOME%" == ""  goto homeless

set THIRDPARTY_LIBS="%LEJOS_HOME%\3rdparty\lib"

java -Djava.library.path="%LEJOS_HOME%\bin" -classpath "%LEJOS_HOME%\lib\jtools.jar;%THIRDPARTY_LIBS%\commons-cli-1.0.jar;%LEJOS_HOME%\lib\pcrcxcomm.jar" js.tools.Lejosdl --tty %RCXTTY% %1 %2 %3 %4 %5 %6 %7 %8 %9
goto end

:homeless
echo LEJOS_HOME not defined

:end

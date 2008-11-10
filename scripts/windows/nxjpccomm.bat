@echo off
rem Run a PC program that uses the PC API - first parameters is the main class
if "%NXJ_HOME%" == ""  goto homeless

set THIRDPARTY_LIBS=%NXJ_HOME%\3rdparty\lib

java "-Djava.library.path=%NXJ_HOME%\bin" "-Dnxj.home=%NXJ_HOME%" -classpath "%NXJ_HOME%\lib\pctools.jar;%NXJ_HOME%\lib\pccomm.jar;%THIRDPARTY_LIBS%\bluecove.jar" %*
goto end

:homeless
echo NXJ_HOME not defined

:end
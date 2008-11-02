rem @echo off
if "%NXJ_HOME%" == ""  goto homeless

java "-Djava.library.path=%NXJ_HOME%\bin" "-Dnxj.home=%NXJ_HOME%" -classpath "%NXJ_HOME%\lib\pctools.jar;%NXJ_HOME%\lib\pccomm.jar" lejos.pc.tools.NXJFlashG %*
pause
goto end

:homeless
echo NXJ_HOME not defined

:end

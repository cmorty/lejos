@echo off
rem This is for use by the Windows Installer only
set NXJ_HOME=..

java "-Djava.library.path=%NXJ_HOME%\bin" "-Dnxj.home=%NXJ_HOME%" -classpath "%NXJ_HOME%\lib\pctools.jar;%NXJ_HOME%\lib\pccomm.jar" lejos.pc.tools.NXJFlashG %*


@echo off
if "%OS%" == "Windows_NT" goto :winnt

:win9x
	if not "%NXJ_HOME%" == "" goto vars_set_nxj

	echo Windows 9x/ME detected. Aborting because the
	echo the NXJ_HOME variable is not set.
	goto :eof

:winnt
	setlocal
	if not "%NXJ_HOME%" == "" goto vars_set_nxj

	call :winnt_normalize NXJ_BIN "%~dp0\."
	call :winnt_normalize NXJ_HOME "%~dp0\.."
	goto :vars_ready

:winnt_normalize
	set "%1=%~f2"
	goto :eof

:vars_set_nxj
	set NXJ_BIN=%NXJ_HOME%\bin

:vars_ready

set NXJ_LIBS=%NXJ_HOME%\lib
set NXJ_LIBS_3rd=%NXJ_HOME%\3rdparty\lib

set NXJ_JAR_BCEL=%NXJ_LIBS_3rd%\bcel.jar
set NXJ_JAR_BLUECOVE=%NXJ_LIBS_3rd%\bluecove.jar
set NXJ_JAR_BLUECOVE_GPL=%NXJ_LIBS_3rd%\bluecove-gpl.jar
set NXJ_JAR_COMMONS_CLI=%NXJ_LIBS_3rd%\commons-cli.jar

set NXJ_JAR_CLASSES=%NXJ_LIBS%\classes.jar
set NXJ_JAR_JTOOLS=%NXJ_LIBS%\jtools.jar
set NXJ_JAR_PCCOMM=%NXJ_LIBS%\pccomm.jar
set NXJ_JAR_PCTOOLS=%NXJ_LIBS%\pctools.jar

set NXJ_CP_BLUECOVE=%NXJ_JAR_BLUECOVE%

set NXJ_CP_BOOT=%NXJ_JAR_CLASSES%
set NXJ_CP_LINK=%NXJ_JAR_BCEL%;%NXJ_JAR_COMMONS_CLI%;%NXJ_JAR_JTOOLS%
set NXJ_CP_TOOL=%NXJ_CP_BLUECOVE%;%NXJ_CP_LINK%;%NXJ_JAR_PCCOMM%;%NXJ_JAR_PCTOOLS%

@echo off
if "%OS%" == "Windows_NT" setlocal

if "%NXJ_HOME%" == "" goto :home_unset
	set NXJ_BIN=%NXJ_HOME%\bin
	goto :home_endif
:home_unset
	if "%OS%" == "Windows_NT" goto :home_nt
	REM echo Windows 9x/ME detected. You have to set the NXJ_HOME variable.
	REM goto :EOF
	set NXJ_BIN=%0\..
	set NXJ_HOME=%0\..\..
	goto :home_endif
:home_nt
	pushd "%0\.."
	for /F "delims=" %%i in ('cd') do set "NXJ_BIN=%%i"
	cd ".."
	for /F "delims=" %%i in ('cd') do set "NXJ_HOME=%%i"
	popd
:home_endif

set NXJ_LIBS=%NXJ_HOME%\lib
set NXJ_LIBS_3rd=%NXJ_HOME%\3rdparty\lib

set NXJ_JAR_BCEL=%NXJ_LIBS_3rd%\bcel-5.1.jar
set NXJ_JAR_BLUECOVE=%NXJ_LIBS_3rd%\bluecove.jar
set NXJ_JAR_BLUECOVE_GPL=%NXJ_LIBS_3rd%\bluecove-gpl.jar
set NXJ_JAR_COMMONS_CLI=%NXJ_LIBS_3rd%\commons-cli-1.0.jar

set NXJ_JAR_CLASSES=%NXJ_LIBS%\classes.jar
set NXJ_JAR_JTOOLS=%NXJ_LIBS%\jtools.jar
set NXJ_JAR_PCCOMM=%NXJ_LIBS%\pccomm.jar
set NXJ_JAR_PCTOOLS=%NXJ_LIBS%\pctools.jar

set NXJ_CP_BLUECOVE=%NXJ_JAR_BLUECOVE%

set NXJ_CP_BOOT=%NXJ_JAR_CLASSES%
set NXJ_CP_LINK=%NXJ_JAR_BCEL%;%NXJ_JAR_COMMONS_CLI%;%NXJ_JAR_JTOOLS%
set NXJ_CP_TOOL=%NXJ_CP_BLUECOVE%;%NXJ_CP_LINK%;%NXJ_JAR_PCCOMM%;%NXJ_JAR_PCTOOLS%

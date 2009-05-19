@echo off
if "%OS%" == "Windows_NT" goto :winnt

:win9x
	if not "%NXJ_HOME%" == "" goto vars_set_nxj
	if not "%LEJOS_HOME%" == "" goto vars_set_lejos 

	echo Windows 9x/ME detected. Aborting because neither
	echo the LEJOS_HOME nor the NXJ_HOME variable is set.
	goto :eof

:winnt
	setlocal
	if not "%NXJ_HOME%" == "" goto vars_set_nxj
	if not "%LEJOS_HOME%" == "" goto vars_set_lejos 

	set NXJ_BIN=%~dp0
	goto :vars_ready

:vars_set_lejos
	set NXJ_HOME=%LEJOS_HOME%

:vars_set_nxj
	set NXJ_BIN=%NXJ_HOME%\bin

:vars_ready


"%NXJ_BIN%\nxjc.bat" %*
:eof

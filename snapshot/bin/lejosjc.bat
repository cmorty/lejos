@echo off
setlocal

if "%LEJOS_HOME%" == "" goto home_unset
	if "%NXJ_HOME%" == "" set "NXJ_HOME=%LEJOS_HOME%"
:home_unset


"%0\..\nxjc.bat" %*

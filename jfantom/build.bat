@echo off

REM Use this batch file from a command prompt which is already setup for building x86 binaries.
REM One possibility is to install the Windows SDK, and then chose the "Windows SDK Command Prompt" shortcut on the startmenu.
REM The shortcut basically calls SetEnv.cmd. You might have to pass the parameter /x86 if you're on a 64bit machine.

REM set "LEJOS_NXT_WIN32_FANTOMSDK=C:\Users\skoehler\Downloads\Lego\fantom"
REM set "LEJOS_NXT_WIN32_JDK=C:\Program Files (x86)\Java\jdk1.5.0_22"

if "%LEJOS_NXT_WIN32_FANTOMSDK%" == "" (
	echo "You have to set LEJOS_NXT_WIN32_FANTOMSDK"
	exit /B 1
)
if "%LEJOS_NXT_WIN32_JDK%" == "" (
	echo "You have to set LEJOS_NXT_WIN32_JDK"
	exit /B 1
)

set "LEJOS_NXT_WIN32_FANTOMINC=%LEJOS_NXT_WIN32_FANTOMSDK%/includes"
set "LEJOS_NXT_WIN32_FANTOMLIB=%LEJOS_NXT_WIN32_FANTOMSDK%/targets/win32U/i386/msvc71/release"


echo on
cl ^
	/I "%LEJOS_NXT_WIN32_FANTOMINC%" ^
	/I "%LEJOS_NXT_WIN32_JDK%\include" ^
	/I "%LEJOS_NXT_WIN32_JDK%\include\win32" ^
	"%LEJOS_NXT_WIN32_FANTOMLIB%\fantom.lib" ^
	jfantom.cpp /LD

@REM Container to allow backward compatibility for old commands
@REM Feel free to delete lejosdl.bat, lejosjc.bat and lejosfirmdl.bat

@echo off

@if "%LEJOS_HOME%" == "" GoTo :UNDEFINED
@REM The following only works with Windows 2000 on up:
@if NOT %lejos_home:~-3% == nxj GoTo :DONE

:CHANGE
SET NXJ_HOME=%LEJOS_HOME%

@REM Check if LIBUSB is installed:
@if exist "%SystemRoot%\system32\libusb0.dll" GoTo :MAIN

@REM Check operating system and skip Vista:
Ver | Find "Version 6" >NUL
  If ErrorLevel 1 Goto :INSTALL
  Echo Vista Detected. Please manually install libusb in XP compatibility mode, otherwise USB devices will be deactivated. http://libusb-win32.sourceforge.net/#downloads
  Goto :DONE

:INSTALL
echo LIBUSB not installed. Running setup program...
START "LIBUSB INSTALLER" /wait "%NXJ_HOME%\3rdparty\lib\libusb-win32-filter-bin-0.1.12.1.exe"
echo NOTE: If you encounter the error NXT Not Found, reboot your computer, make sure the brick is still in firmware upload mode, and try again.

:MAIN
@nxjflash.exe %*
GoTo :Done

:UNDEFINED
echo LEJOS_HOME not defined.

:DONE
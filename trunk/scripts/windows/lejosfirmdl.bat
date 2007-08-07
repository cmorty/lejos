@REM Container to allow backward compatibility for old commands
@REM Feel free to delete lejosdl.bat, lejosjc.bat and lejosfirmdl.bat

@echo off
@if "%LEJOS_HOME%" == "" GoTo :UNDEFINED
@REM The following only works with Windows 2000 on up:
@if NOT %lejos_home:~-3% == nxj GoTo :DONE

:CHANGE
SET NXJ_HOME=%LEJOS_HOME%
@nxjflash.exe %1 %2 %3 %4 %5 %6 %7 %8 %9
GoTo :Done

:UNDEFINED
echo LEJOS_HOME not defined.

:DONE
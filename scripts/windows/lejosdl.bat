@REM Container to allow backward compatibility for old commands
@REM Feel free to delete lejosdl.bat, lejosjc.bat and lejosfirmdl.bat

@echo off
@if "%LEJOS_HOME%" == "" GoTo :UNDEFINED
@REM The following only works with Windows 2000 on up:
@if NOT %lejos_home:~-3% == nxj GoTo :DONE

:CHANGE
SET NXJ_HOME=%LEJOS_HOME%
@nxj.bat -r %*
GoTo :Done

:UNDEFINED
echo LEJOS_HOME not defined.

:DONE
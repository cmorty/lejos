@echo off
if "%LEJOS_HOME%" == ""  goto homeless

runjava "%LEJOS_HOME%\bin\lejos_nxt.bin" %1
goto end

:homeless
echo LEJOS_HOME not defined

:end

@REM Container to allow backward compatibility for old commands
@REM Feel free to delete lejosdl.bat, lejosjc.bat and lejosfirmdl.bat
@if NOT "%LEJOS_HOME%" == "" SET NXJ_HOME=%LEJOS_HOME%
@nxjflash.exe %1 %2 %3 %4 %5 %6 %7 %8 %9
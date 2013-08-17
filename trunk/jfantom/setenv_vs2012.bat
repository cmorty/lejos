@echo off
goto :start

:check_dir
	if not exist "%~1" (
		echo Directory "%~1" does not exist.
		echo Things may not work as expected.
	)
	goto :eof

:start
	setlocal
	if "%PROCESSOR_ARCHITECTURE%" == "x86" (
		set "ProgramFiles(x86)=%ProgramFiles%"
	)
	
	set "BINDIR=%ProgramFiles(x86)%\Microsoft SDKs\Windows\v7.1A\Bin"
	set "INCDIR=%ProgramFiles(x86)%\Microsoft SDKs\Windows\v7.1A\Include"
	set "LIBDIR=%ProgramFiles(x86)%\Microsoft SDKs\Windows\v7.1A\Lib"
	set "SUBSYS=CONSOLE,5.01"
	
	REM for x64 instead of x86:
	REM set "LIBDIR=%ProgramFiles(x86)%\Microsoft SDKs\Windows\v7.1A\Lib\x64"
	REM set "SUBSYS=CONSOLE,5.02"
	
	call :check_dir "%%BINDIR%%"
	call :check_dir "%%INCDIR%%"
	call :check_dir "%%LIBDIR%%"
	
	endlocal & (
		set "CL=/D_USING_V110_SDK71_;%CL%"
		set "LINK=/SUBSYSTEM:%SUBSYS% %LINK%"
		set "LIB=%LIBDIR%;%LIB%"
		set "PATH=%BINDIR%;%PATH%"
		set "INCLUDE=%INCDIR%;%INCLUDE%"
	)

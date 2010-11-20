@echo off
if "%OS%" == "Windows_NT" goto :winnt

:win98
	echo Windows 9x/ME is no longer supported.
	echo Please upgrade to Windows 2000 or later.
	goto :eof

:append_jar
	set "TMP_CP=%TMP_CP%;%~1"
	goto :eof

:build_classpath
	set "TMP_CP="
	for /R "%~2" %%i in (*.jar) do (
		call :append_jar "%%i"
	)
	set "%~1=%TMP_CP:~1%"
	goto :eof

:normalize_path
	set "%~1=%~f2"
	goto :eof

:winnt
	setlocal
	if not "%NXJ_HOME%" == "" goto :nxj_home_found

	call :normalize_path NXJ_BIN "%~dp0\."
	call :normalize_path NXJ_HOME "%~dp0\.."
	goto :build_classpaths

:nxj_home_found
	set NXJ_BIN=%NXJ_HOME%\bin

:build_classpaths
	call :build_classpath NXJ_CP_PC "%NXJ_HOME%\lib\pc"
	call :build_classpath NXJ_CP_NXT "%NXJ_HOME%\lib\nxt"


java -Dnxj.home="%NXJ_HOME%" -DCOMMAND_NAME="nxjdataviewer" -Djava.library.path="%NXJ_BIN%" -classpath "%NXJ_CP_PC%" lejos.pc.tools.DataViewer  %*
:eof

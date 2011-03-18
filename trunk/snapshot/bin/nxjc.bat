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
	if not "%NXJ_HOME%" == "" (
		set "NXJ_BIN=%NXJ_HOME%\bin"
	) else (
		call :normalize_path NXJ_BIN "%~dp0\."
		call :normalize_path NXJ_HOME "%~dp0\.."
	)

	call :build_classpath NXJ_CP_PC "%NXJ_HOME%\lib\pc"
	call :build_classpath NXJ_CP_NXT "%NXJ_HOME%\lib\nxt"

	if not "%LEJOS_NXT_JAVA_HOME%" == "" (
		set "JAVA=%LEJOS_NXT_JAVA_HOME%\bin\java"
		set "JAVAC=%LEJOS_NXT_JAVA_HOME%\bin\javac"
	) else if not "%JAVA_HOME%" == "" (
		set "JAVA=%JAVA_HOME%\bin\java"
		set "JAVAC=%JAVA_HOME%\bin\javac"
	) else (
		set "JAVA=java"
		set "JAVAC=javac"
	)


"%JAVAC%" -bootclasspath "%NXJ_CP_NXT%" -extdirs "" %*
:eof

@echo off
if "%OS%" == "Windows_NT" goto :winnt

:win98
	echo Windows 9x/ME is no longer supported.
	echo Please upgrade to Windows 2000 or later.
	goto :eof

:build_classpath
	if not exist "%~2" (
	  echo Your NXJ_HOME variable seems to be incorrect.
	  echo The following folder does not exist:
	  echo   "%~2"
	  exit /B 1
	)

	set "%~1="
	for /R "%~2" %%i in (*.jar) do (
		set "%~1=!%~1!;%%i"
	)
	goto :eof

:normalize_path
	set "%~1=%~f2"
	goto :eof

:set_java_and_javac
	set "JAVA=!%~1!\bin\java"
	set "JAVAC=!%~1!\bin\javac"
	if not exist "%JAVA%" (
		echo The variable %~1 does not point to the root directory
		echo of a JRE or JDK.
	) else if not exist "%JAVAC%" (
		echo The variable %~1 seems to point to the root directory
		echo of a JRE. It should point to the root directory of a JDK.
		echo Otherwise, some tools might not work.
	)
	goto :eof

:winnt
	setlocal EnableDelayedExpansion
	
	if not "%NXJ_HOME%" == "" (
		set "NXJ_BIN=%NXJ_HOME%\bin"
	) else (
		call :normalize_path NXJ_BIN "%~dp0\."
		call :normalize_path NXJ_HOME "%~dp0\.."
	)

	call :build_classpath NXJ_CP_PC "%NXJ_HOME%\lib\pc"
	call :build_classpath NXJ_CP_NXT "%NXJ_HOME%\lib\nxt"

	if not "%LEJOS_NXT_JAVA_HOME%" == "" (
		call :set_java_and_javac LEJOS_NXT_JAVA_HOME 
	) else if not "%JAVA_HOME%" == "" (
		call :set_java_and_javac JAVA_HOME 
	) else (
		set "JAVA=java"
		set "JAVAC=javac"
	)


"%JAVA%" -Dnxj.home="%NXJ_HOME%" -DCOMMAND_NAME="nxjlink" -classpath "%NXJ_CP_PC%" lejos.pc.tools.NXJLink --writeorder "LE" --bootclasspath "%NXJ_CP_NXT%" --classpath "." %*
:eof

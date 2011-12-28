set "NXJ_HOME=%~f1"
set "LEJOS_NXT_JAVA_HOME=%~f2"
start "NXJFlash" /MIN cmd.exe /C "%NXJ_HOME%\bin\nxjflashg.bat"

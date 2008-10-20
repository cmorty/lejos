@echo off
if "%NXJ_HOME%" == ""  goto homeless

javac -bootclasspath "%NXJ_HOME%\lib\classes.jar;%CLASSPATH%" %*
goto end

:homeless
echo NXJ_HOME not defined

:end

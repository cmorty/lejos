@echo off
if "%NXJ_HOME%" == ""  goto homeless

javac -source 1.3 -target 1.1 -bootclasspath "%NXJ_HOME%\lib\classes.jar;%CLASSPATH%" %*
goto end

:homeless
echo NXJ_HOME not defined

:end

@echo off
if "%NXJ_HOME%" == ""  goto homeless

javac -source 1.3 -target 1.1 -bootclasspath "%NXJ_HOME%\lib\classes.jar;%CLASSPATH%" %1 %2 %3 %4 %5 %6 %7 %8 %9
goto end

:homeless
echo NXJ_HOME not defined

:end

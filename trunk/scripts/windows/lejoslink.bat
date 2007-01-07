echo off
if "%LEJOS_HOME%" == ""  goto homeless

set THIRDPARTY_LIBS="%LEJOS_HOME%\3rdparty\lib"
set LINK_CLASSPATH=.;%THIRDPARTY_LIBS%\bcel-5.1.jar;%THIRDPARTY_LIBS%\commons-cli-1.0.jar;%LEJOS_HOME%\lib\jtools.jar;%LEJOS_HOME%\lib\classes.jar

java -classpath "%LINK_CLASSPATH%" js.tinyvm.TinyVM --writeorder LE --classpath "%LINK_CLASSPATH%" %1 %2 %3 %4 %5 %6 %7 %8 %9 
goto end

:homeless
echo LEJOS_HOME not defined

:end

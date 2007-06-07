@echo off
if "%NXJ_HOME%" == ""  goto homeless

set THIRDPARTY_LIBS="%NXJ_HOME%\3rdparty\lib"
set LINK_CLASSPATH=".;%THIRDPARTY_LIBS%\bcel-5.1.jar;%THIRDPARTY_LIBS%\commons-cli-1.0.jar;%NXJ_HOME%\lib\jtools.jar;%NXJ_HOME%\lib\classes.jar"

java -Djava.library.path="%NXJ_HOME%\bin" -classpath "%LINK_CLASSPATH%" lejos.pc.tools.NXJLinkAndUpload --writeorder LE --classpath "%LINK_CLASSPATH%" %1 %2 %3 %4 %5 %6 %7 %8 %9 

goto end

:homeless
echo NXJ_HOME not defined

:end
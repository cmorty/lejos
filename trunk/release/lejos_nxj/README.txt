leJOS NXJ 0.9.1 beta
=====================

What is leJOS NXJ?
------------------

leJOS NXJ allows you to program your LEGO Mindstorms NXT robotics system in Java. To do this you must 
replace the LEGO supplied firmware, with the leJOS NXJ firmware, which includes a Java Virtual machine (JVM).

leJOS NXJ comes with many tools for building, uploading and debugging Java programs. They are usually
used from a Java Integrated Development Environment (IDE). lEJOS NXJ includes a plug-in for
the Eclipse IDE, but can also be used from other IDEs. If you prefer, you can just
use a Programmer's editor and command line tools.

What is the correct download for my system?
-------------------------------------------

On Windows, you should download leJOS_NXJ_0.9.0-Setup.exe from the leJOS-NXJ-win32/0.9.0beta folder.
This should be the default download from a Windows system. leJOS NXJ is supported on Windows 2000, XP, 
Vista, and 7, but not earlier versions.

Before you run the installer, you should make sure you have a Java JDK installed on your PC, and its 
bin folder is on the PATH. To check this, open a command window and type "javac -version". It it gives 
you the version of your JDK and it is a version 1.5 or 1.6 JDK, you are good to go.

If you have problems with the the installer, you can try the zip file. It contains a README.html file
that includes installation instructions.

If you are on a Linux or MAC OS X system, you should download the lejos_NXJ_0_9_0beta.tar.gz file from
the leJOS-NXJ/0.9.0beta folder. This should be the default download for these systems. On both of
these systems, you should unpack this file to a suitable installation directory, and then follow the instructions 
in the README.html file. On Linux, you ,must build some on the binaries by executing the "ant" command
in the lejos_nxj/build directory.

New users should read the tutorial at http://lejos.sourceforge.net/nxt/nxj/tutorial/.

Eclipse users should install the plugin from the update site http:/lejos.sourceforge.net/tools/eclipse/plugin/nxj


What is new in this release
---------------------------

There are two new utilities: nxjmapcommand and nxjchartinglogger.

Furthermore, the API has been enriched with the class lejos.robotics.navigation.OmniPilot
for holonomic robots with omni wheels.

Support for some new third party sensors has been added: the LEGO temperature sensor,
Dexter Industries dIMU, Mindsensors SumoEyes.

To understand the latest API, you should look at http://lejos.sourceforge.net/nxt/nxj/api/,
and for the PC API http://lejos.sourceforge.net/nxt/pc/api/.

Also, many bugs have been fixed. See the release notes for details.   

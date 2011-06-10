6/8/2011 22:50:54 KPT
\lejos\trunk\leJOSlab\ChartingLogger\README.txt
version ??? (I really need to figure out a good versioning system)

ChartingLogger.jar is the PC-side ChartLogger application. All dependencies _should_ be included. Built with leJOS 
snapshot classes/firmware 0.9 as of 6/1/11.

run with "java -jar ChartLogger.jar" at cmd prompt. Sample sine dataset is displayed.
NXT sample TestLogger.java located at \lejos\trunk\leJOSlab\ChartingLogger\nxt\src\net\mosen\nxt\instrumentation.

Please report anything you deem reportable to lejos@mosen.net. Comments, suggestions, constructive criticism always
welcome.

Developers, you will need JFreeCHart 1.0.13 (jfreechart-1.0.13.zip) and the JCommon lib (jcommon-1.0.16.zip). These files are
in the same directory as this file. Go to http://www.jfree.org/jfreechart/ for information. I included the classes in the 
ChartingLogger.jar but the zip files provide source, etc. as per the LGPL. (What a developer craves! It's got 
electrolytes!)

Chart management:
Zoom extents: Doubleclick or click-drag to left, and/or up
Zoom window: CLick-drag (to right and down) release
Slider changes domain scale dynamically from 0.1 to 100% of (domain) X dataset extents/range
Tootip: Hover over data point and series name, x-y val is shown
left-click moves crosshair to nearest x-y datapoint. Displays coordinate in lower rigth of chart

-Kirk Thompson
lejos@mosen.net
6/8/2011

TODO
-------------------
Add LGPL license and attribution to Dave Gilbert (Thanks for such a great lib, Dave!) in the frame
Javadoc

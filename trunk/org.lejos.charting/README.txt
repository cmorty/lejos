6/18/2011 14:30:08 KPT
\lejos\trunk\leJOSlab\ChartingLogger\README.txt
version ??? 

lejos.pc.charting.ChartingLogger is the PC-side ChartLogger Swing application. 

Please report anything you deem reportable to lejos@mosen.net. Comments, suggestions, constructive criticism always
welcome.

Developers, you will need JFreeCHart 1.0.13 (jfreechart-1.0.13.zip) and the JCommon lib (jcommon-1.0.16.zip) for source, 
docs, etc. These files are available from http://www.jfree.org/jfreechart/. 

Chart management:
Zoom extents: Doubleclick or click-drag to left, and/or up
Zoom window: Click-drag (to right and down) release
Zoom in/out dynamic: Use mouse wheel
Slider changes domain scale dynamically from 0.1 to 100% of (domain) X dataset extents/range
Tootip: Hover over data point on a series and series name, x-y val is shown
left-click moves crosshair to nearest x-y datapoint. Displays coordinate in lower right of chart

-Kirk Thompson
lejos@mosen.net

TODO
-------------------
Add LGPL license and attribution to Dave Gilbert (Thanks for such a great lib, Dave!) in the frame
Javadoc
add multi-range axis support
change header call (and dependents) to allow show-in-chart flag, axis #, color, style
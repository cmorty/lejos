import lejos.nxt.*;

/**
 * Shows how leJOS free RAM reduces as String objects are created.
 * 
 * Now that leJOS NXJ has a garbage collector, it shows the
 * garbage collector kicking in and the memory increasing again.
 * 
 * @author Lawrie Griffiths
 *
 */
public class MemoryTest {
	public static void main (String[] arg)
    	throws InterruptedException
    {
	    do
	    {
	      String s = "Some more text";
	      LCD.clear();
	      LCD.drawInt( (int)(Runtime.getRuntime().freeMemory()),0,0);
	      LCD.refresh();
	      Thread.sleep(10);
	    } while (true);
    }
}

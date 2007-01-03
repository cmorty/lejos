import java.lang.System;
import lejos.nxt.*;

public class MemoryTest {
	public static void main (String[] arg)
    	throws InterruptedException
    {
	    do
	    {
	      String s = "Some text";
	      LCD.clear();
	      LCD.drawInt( (int)(Runtime.getRuntime().freeMemory()),0,0);
	      LCD.refresh();
	      Thread.sleep(10);
	    } while (true);
    }
}

package org.lejos.sample.helloworld;

import lejos.nxt.LCD;

/**
 * A simple hello world program.
 * 
 * @author Lawrie Griffiths
 */
public class HelloWorld {
	public static void main(String[] aArg) throws Exception {
		LCD.drawString("Hello World", 3, 4);
		Thread.sleep(2000);
	}
}

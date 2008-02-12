package java.lang;

import java.io.PrintStream;

import lejos.nxt.LCDOutputStream;

/**
 * System utilities.
 */
public final class System
{
  private System() {}
  
  /**
   * Copies one array to another.
   */
  public static native void arraycopy (Object src, int srcOffset, Object dest, int destOffset, int length);


  /**
   * Terminate the application.
   */
  public static native void exit(int code);
    
  /**
   * Current time expressed in milliseconds. In the RCX, this is the number
   * of milliseconds since the RCX has been on. (In Java, this would
   * be since January 1st, 1970).
   */
  public static native long currentTimeMillis();
  
  /**
   * Get the singleton instance of Runtime.
   */
  public static Runtime getRuntime() {
  	return Runtime.getRuntime();
  }
  
  /**
   * Collect garbage
   */
 public static native void gc();
 
 /**
  * Shutdown the brick
  */
 public static native void shutDown();
 
 public static PrintStream out = new PrintStream(new LCDOutputStream());
 
}




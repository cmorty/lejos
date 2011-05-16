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
	 * Returns <code>null</code>. In leJOS, there are no system properties. So this function returns <code>null</code>.
	 * @param name name of the system property
	 * @return <code>null</code>
	 */
	public static String getProperty(String name)
	{
		return null;
	}

	/**
	 * Returns <code>def</code>. In leJOS, there are no system properties. So this function returns <code>def</code>.
	 * @param name name of the system property
	 * @param def default value that is returns if system property doesn't exist
	 * @return <code>def</code>
	 */
	public static String getProperty(String name, String def)
	{
		return def;
	}

  /**
   * Get the singleton instance of Runtime.
   */
  public static Runtime getRuntime() {
  	return Runtime.getRuntime();
  }
  
  public static int identityHashCode(Object obj) {
	  return System.getDataAddress(obj);
  }
	
  private native static int getDataAddress (Object obj);
  
  /**
   * Collect garbage
   */
 public static native void gc();
 
  
 public static PrintStream out = new PrintStream(new LCDOutputStream());
 
 /**
  * Redirect System.out
  * 
  * @param out a PrintStream
  */
 public static void setOut(PrintStream out) {
	 System.out = out;
 }

 public static PrintStream err = new PrintStream(new LCDOutputStream());
 
 /**
  * Redirect System.err
  * 
  * @param err a PrintStream
  */
 public static void setErr(PrintStream err) {
	 System.err = err;
 }
 

 public static native long nanoTime();

}




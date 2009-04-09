package java.lang;

import java.io.PrintStream;

import lejos.nxt.LCDOutputStream;

/**
 * System utilities.
 */
public final class System
{
  // Flags used to control the Virtual Machine.
  public static final int VM_TYPECHECKS = 1;

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
  
  public static int identityHashCode(Object obj) {
	  return System.getDataAddress(obj);
  }
	
  private native static int getDataAddress (Object obj);
  
  /**
   * Collect garbage
   */
 public static native void gc();
 
 /**
  * Shutdown the brick
  */
 public static native void shutDown();
 
 /**
  * Boot into firmware update mode.
  */
 public static native void boot();
 
 /**
  * Diagnostic tool (for firmware developers only)
  */
 public static native int diagn( int code, int param);
 
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
 
 /**
  * Get the number of times a Java program (including the menu)
  * has executed since the brick was swiched on
  * 
  * @return the count
  */
 public static native int getProgramExecutionsCount();
 
 public static native int getFirmwareMajorVersion();
 
 public static native int getFirmwareMinorVersion();
 
 public static native int getFirmwareRevision();

 /**
  * Control the run time operation of the leJOS Virtual Machine.
  * @param options Bit flags.
  */
 public static native void setVMOptions(int options);

 /**
  * Return the currently operating Virtual Machine options.
  * @return
  */
 public static native int getVMOptions();

 /**
  * Enable/Disable strict run time type checking for some operations within
  * the Virtual Machine.
  * @param on
  */
 public static void enableRunTimeTypeChecks(boolean on)
 {
   int cur = getVMOptions();
   if (on)
       cur |= VM_TYPECHECKS;
   else
       cur &= ~VM_TYPECHECKS;
   setVMOptions(cur);
 }



}




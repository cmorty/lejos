package josx.platform.nxt;

/**
 * Provides access to ROM routines.
 */
public class ROM
{
  public static native void call (short aAddr);
  public static native void call (short aAddr, short a1);
  public static native void call (short aAddr, short a1, short a2);
  public static native void call (short aAddr, short a1, short a2, short a3);
  public static native void call (short aAddr, short a1, short a2, short a3, short a4);
}

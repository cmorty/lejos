package lejos.nxt;

/**
 * LCD routines.
 */
public class LCD
{
  private LCD()
  {
  }

  /**
   * Display a string on the LCD at specified x,y co-ordinate.
   */
  public static native void drawString(String str, int x, int y);

  /**
   * Display an int on the LCD at specified x,y co-ordinate.
   */
  public static native void drawInt(int i, int x, int y);

  /**
   * Update the display.
   */
  public static native void refresh();
  
  /**
   * Clear the display.
   */
  public static native void clear();
  
  /**
   * Write graphics from a Java buffer to the display.
   */
  public static native void setDisplay(int[] buff);

}


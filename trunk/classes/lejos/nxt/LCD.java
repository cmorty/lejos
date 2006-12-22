package lejos.nxt;

/**
 * LCD routines.
 */
public class LCD
{
  private LCD()
  {
  }

  public static native void drawString(String str, int x, int y);

  public static native void drawInt(int i, int x, int y);

  public static native void refresh();
  
  public static native void clear();

}


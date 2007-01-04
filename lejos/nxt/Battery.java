/**
 * NXT access classes.
 */
package lejos.nxt;

/**
 * Provides access to Battery.
 */
public class Battery
{

  private Battery()
  {
  }
  /**
   * @return Battery voltage in mV.
   */
  public static native int getVoltageMilliVolt();

  /**
   * @return Battery voltage in Volt.
   */
  public static float getVoltage()
  {
    return (float)(Battery.getVoltageMilliVolt() * 0.001);
  }
}

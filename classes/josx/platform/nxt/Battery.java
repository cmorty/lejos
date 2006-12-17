/**
 * NXT access classes.
 */
package josx.platform.nxt;

/**
 * Provides access to Battery.
 */
public class Battery
{

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

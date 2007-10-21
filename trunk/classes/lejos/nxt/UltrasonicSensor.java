package lejos.nxt;

/**
 *
 * Abstraction for a NXT Ultrasonic Sensor.
 *
 */
public class UltrasonicSensor extends I2CSensor
{
    byte[] buf = new byte[1];
   
    public UltrasonicSensor(SensorPort port)
    {
        super(port);
    }
   
    /**
     * Return distance of object.
     *
     * @return distance or 255 if no object in range
     */
    public int getDistance()
    {
        int ret = getData(0x42, buf, 1);
        return (ret == 0 ? (buf[0] & 0xff) : 255);
    }

    /**
     * Return array of 8 echo distances
     *
     * @return 0 for success <> 0 otherwise
     */
    public int getDistance(byte dist[])
    {
        if (dist.length < 8) return -1;
        int ret = getData(0x42, dist, 8);
        return ret;
    }
    /**
     * Send a single ping
     *
     * @return 0 if ok <> 0 otherwise
     *
     */
    public int ping()
    {
        buf[0] = 0x1;
        int ret = sendData(0x41, buf, 1);
        return ret;
    }

    /**
     * Switch to continuous ping mode
     *
     * @return 0 if ok <> 0 otherwise
     *
     */
    public int continuous()
    {
        buf[0] = 0x2;
        int ret = sendData(0x41, buf, 1);
        return ret;
    }
    /**
     * Turn off ping mode
     *
     * @return 0 if ok <> 0 otherwise
     *
     */
    public int off()
    {
        buf[0] = 0x0;
        int ret = sendData(0x41, buf, 1);
        return ret;
    }
    /**
     * Reset the device
     *
     * @return 0 if ok <> 0 otherwise
     *
     */
    public int reset()
    {
        buf[0] = 0x4;
        int ret = sendData(0x41, buf, 1);
        return ret;
    }
}
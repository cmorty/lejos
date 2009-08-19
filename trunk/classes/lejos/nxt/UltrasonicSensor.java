package lejos.nxt;

import lejos.robotics.RangeFinder;
import lejos.util.Delay;

/*
 * WARNING: THIS CLASS IS SHARED BETWEEN THE classes AND pccomms PROJECTS.
 * DO NOT EDIT THE VERSION IN pccomms AS IT WILL BE OVERWRITTEN WHEN THE PROJECT IS BUILT.
 */

/**
 * Abstraction for a NXT Ultrasonic Sensor.
 *
 */
public class UltrasonicSensor extends I2CSensor implements RangeFinder
{
	/* Device control locations */
	private static final byte MODE = 0x41;
	private static final byte DISTANCE = 0x42;
	private static final byte FACTORY_DATA = 0x11;
	private static final byte UNITS = 0x14;
	private static final byte CALIBRATION = 0x4a;
	private static final byte PING_INTERVAL = 0x40;
	/* Device modes */
	private static final byte MODE_OFF = 0x0;
	private static final byte MODE_SINGLE = 0x1;
	private static final byte MODE_CONTINUOUS = 0x2;
	private static final byte MODE_CAPTURE = 0x3;
	private static final byte MODE_RESET = 0x4;
	/* Device timing */
	private static final int DELAY_CMD = 0x5;
	private static final int DELAY_AVAILABLE = 0xf;
	
	private byte[] buf = new byte[1];
	private byte[] inBuf = new byte[8];
	private String units = null;
	private long nextCmdTime;
	private long dataAvailableTime;
	private int currentDistance;
	private byte mode;
	
	/*
	 * Return the current time in milliseconds
	 */
	private long now()
	{
		return System.currentTimeMillis();
	}
	
	/*
	 * Wait until the specified time
	 *
	 */
	private void waitUntil(long when)
	{
		long delay = when - now();
        Delay.msDelay(delay);
	}
	
	/*
	 * Over-ride standard get function to ensure correct inter-command timing
	 * when using the ultrasonic sensor. The Lego Ultrasonic sensor uses a
	 * "bit-banged" i2c interface and seems to require a minimum delay between
	 * commands otherwise the commands fail.
	 */
	public int getData(int register, byte [] buf, int len)
	{
		waitUntil(nextCmdTime);
		int ret = super.getData(register, buf, len);
		nextCmdTime = now() + DELAY_CMD;
		return ret;
	}
	
	/*
	 * Over-ride the standard send function to ensure the correct inter-command
	 * timing for the ultrasonic sensor.
	 *
	 */
	public int sendData(int register, byte [] buf, int len)
	{
		waitUntil(nextCmdTime);
		int ret = super.sendData(register, buf, len);
		nextCmdTime = now() + DELAY_CMD;
		return ret;
	}
	
	public UltrasonicSensor(I2CPort port)
	{
		super(port);
		// Set correct sensor type, default is TYPE_LOWSPEED
		port.setType(TYPE_LOWSPEED_9V);
		// Default mode is continuous
		mode = MODE_CONTINUOUS;
		// Set initial inter-command delays
		nextCmdTime = now() + DELAY_CMD;
		dataAvailableTime = now() + DELAY_AVAILABLE;
		currentDistance = 255;
	}
	
	/**
	 * Return distance to an object. To ensure that the data returned is valid
	 * this method may have to wait a short while for the distance data to
	 * become available.
	 *
	 * @return distance or 255 if no object in range
	 */
	public int getDistance()
	{
		// If we are in continuous mode and new data will not yet be available
		// simply return the current reading (since this is what the sensor
		// will do anyway.)
		if (mode == MODE_CONTINUOUS && now() < dataAvailableTime)
			return currentDistance;
		waitUntil(dataAvailableTime);
		int ret = getData(DISTANCE, buf, 1);
		currentDistance = (ret == 0 ? (buf[0] & 0xff) : 255);
		// Make a note of when new data should be available.
		if (mode == MODE_CONTINUOUS)
			dataAvailableTime = now() + DELAY_AVAILABLE;
		return currentDistance;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public float getRange() {
		return (float) getDistance();
	}
	
	/**
	 * Return an array of 8 echo distances. These are generated when using ping
	 * mode. A value of 255 indicates that no echo was obtained. The array must
	 * contain at least 8 elements, if not -1 is returned. If the distnace data
	 * is not yet available the method will wait until it is.
	 *
	 * @return 0 if ok <> 0 otherwise
	 */
	public int getDistances(int dist[])
	{
		if (dist.length < inBuf.length || mode != MODE_SINGLE) return -1;
		waitUntil(dataAvailableTime);
		int ret = getData(DISTANCE, inBuf, inBuf.length);
		for(int i = 0; i < inBuf.length; i++)
			dist[i] = (int)inBuf[i] & 0xff;
		return ret;
	}
	
	/*
	 * Set the sensor into the specified mode. Keep track of which mode we are
	 * operating in. Make a note of when any distance data will become available
	 *
	 */
	private int setMode(byte mode)
	{
		buf[0] = mode;
		int ret = sendData(MODE, buf, 1);
		// Make a note of when the data will be available
		dataAvailableTime = now() + DELAY_AVAILABLE;
		if (ret == 0) this.mode = mode;
		return ret;
	}
	
	/**
	 * Send a single ping.
	 * The sensor operates in two modes, continuous and ping. When in continuous
	 * mode the sensor sends out pings as often as it can and the most recently
	 * obtained result is available via a call to getDistance. When in ping mode
	 * a ping is only transmitted when a call is made to this method. This sends a
	 * single ping and up to 8 echoes are captured. These may be read by making
	 * a call to getDistance and passing a suitable array. A delay of
	 * approximately 20ms is required between the call to ping and obtaining the
     * results. The getDistance call automatically takes care of this. The normal
	 * getDistance call may also be used with ping, returning information for
	 * the first echo. Calling this method will disable the default continuous
	 * mode, to switch back to continuous mode call continuous.
     * 
	 * @return 0 if ok <> 0 otherwise
	 *
	 */
	public int ping()
	{
		return setMode(MODE_SINGLE);
	}
	
	/**
	 * Switch to continuous ping mode.
	 * This method enables continuous ping and capture mode. This is the default
	 * operating mode of the sensor. Please the notes for ping for more details.
	 *
	 * @return 0 if ok <> 0 otherwise
	 *
	 */
	public int continuous()
	{
		return setMode(MODE_CONTINUOUS);
	}
	/**
	 * Turn off the sensor.
	 * This call disables the sensor. No pings will be issued after this call,
	 * until either ping, continuous or reset is called.
	 *
	 * @return 0 if ok <> 0 otherwise
	 *
	 */
	public int off()
	{
		return setMode(MODE_OFF);
	}
	
	/**
	 * Set capture mode
	 * Set the sensor into capture mode. The Lego documentation states:
	 * "Within this mode the sensor will measure whether any other ultrasonic
	 * sensors are within the vicinity. With this information a program can
	 * evaluate when it is best to make a new measurement which will not
	 * conflict with other ultrasonic sensors."
	 * I have no way of testing this. Perhaps someone with a second NXT could
	 * check it out!
	 *
	 * @return 0 if ok <> 0 otherwise
	 *
	 */
	public int capture()
	{
		return setMode(MODE_CAPTURE);
	}
	
	/**
	 * Reset the device
	 * Performs a "soft reset" of the device. Restores things to the default
	 * state. Following this call the sensor will be operating in continuous
	 * mode.
	 *
	 * @return 0 if ok <> 0 otherwise
	 *
	 */
	public int reset()
	{
		int ret = setMode(MODE_RESET);
		// In continuous mode after a reset;
		if (ret == 0) mode = MODE_CONTINUOUS;
		return ret;
	}
	
	private int getMultiBytes(int reg, byte data[], int len)
	{
		/*
		 * For some locations that are adjacent in address it is not possible
		 * to read the locations in a single read, instead we must read them
		 * using a series of individual reads. No idea why this should be, but
		 * that is how it is!
		 */
		int ret;
		for(int i = 0; i < len; i++)
		{
			ret = getData(reg+i, buf, 1);
			if (ret != 0) return ret;
			data[i] = buf[0];
		}
		return 0;
	}
	
	private int setMultiBytes(int reg, byte data[], int len)
	{
		/*
		 * For some locations that are adjacent in address it is not possible
		 * to read the locations in a single write, instead we must write them
		 * using a series of individual writes. No idea why this should be, but
		 * that is how it is!
		 */
		int ret;
		for(int i = 0; i < len; i++)
		{
			buf[0] = data[i];
			ret = sendData(reg+i, buf, 1);
			if (ret != 0) return ret;
		}
		return 0;
	}
	
	/**
	 * Return 10 bytes of factory calibration data. The bytes are as follows
	 * data[0] : Factory zero (cal1)
	 * data[1] : Factory scale factor (cal2)
	 * data[2] : Factory scale divisor.
	 *
	 * @return 0 if ok <> 0 otherwise
	 */
	public int getFactoryData(byte data[])
	{
		if (data.length < 3) return -1;
		return getMultiBytes(FACTORY_DATA, data, 3);
	}
	/**
	 * Return a string indicating the type of units in use by the unit.
	 * The default response is 10E-2m indicating centimetres in use.
	 *
	 * @return 7 byte string
	 */
	public String getUnits()
	{
		int ret = getData(UNITS, inBuf, 7);
		if(ret != 0)
			return "       ";
		char [] charBuff = new char[7];
		for(int i=0;i<7;i++)
			charBuff[i] = (char)inBuf[i];
		units = new String(charBuff, 0, 7);
					
		return units;
	}
	
	/**
	 * Return 3 bytes of calibration data. The bytes are as follows
	 * data[0] : zero (cal1)
	 * data[1] : scale factor (cal2)
	 * data[2] : scale divisor.
	 *
	 * @return 0 if ok <> 0 otherwise
	 */
	public int getCalibrationData(byte data[])
	{
		/* Note the lego documentation says this is at loacation 0x50, however
		 * it looks to me like this is a hex v decimal thing and it should be
		 * location 0x49 + 1 which is 0x4a not 0x50! There certainly seems to be
		 * valid data at 0x4a...
		 */
		if (data.length < 3) return -1;
		return getMultiBytes(CALIBRATION, data, 3);
	}
	
	/**
	 * Set 3 bytes of calibration data. The bytes are as follows
	 * data[0] : zero (cal1)
	 * data[1] : scale factor (cal2)
	 * data[2] : scale divisor.
	 *
	 * This does not currently seem to work.
	 *
	 * @return 0 if ok <> 0 otherwise
	 */
	public int setCalibrationData(byte data[])
	{
		if (data.length < 3) return -1;
		return setMultiBytes(CALIBRATION, data, 3);
	}
	
	/**
	 * Return the interval used in continuous mode.
	 * This seems to be in the range 1-15. It can be read and set. However tests
	 * seem to show it has no effect. Others have reported that this does vary
	 * the ping interval (when used in other implementations). Please report
	 * any new results.
	 *
	 * @return -1 if error otherwise the interval
	 */
	public byte getContinuousInterval()
	{
		int ret = getData(PING_INTERVAL, buf,1);
		return (ret == 0 ? buf[0] : -1);
	}
	
	/**
	 * Set the ping inetrval used when in continuous mode.
	 * See getContinuousInterval for more details.
	 *
	 * @return 0 if 0k <> 0 otherwise.
	 */
	public int setContinuousInterval(byte interval)
	{
		buf[0] = interval;
		int ret = sendData(PING_INTERVAL, buf, 1);
		return ret;
	}
	
	/**
	 * Returns the current operating mode of the sensor.
	 * 0 : sensor is off
	 * 1 : Single shot ping mode
	 * 2 : continuous ping mode (default)
	 * 3 : Event capture mode
	 *
	 * @return -1 if error otherwise the operating mode
	 */
	public byte getMode()
	{
		int ret = getData(MODE, buf,1);
		return (ret == 0 ? buf[0] : -1);
	}
}
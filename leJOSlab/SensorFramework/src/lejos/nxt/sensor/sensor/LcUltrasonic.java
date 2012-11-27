package lejos.nxt.sensor.sensor;

import lejos.nxt.I2CPort;
import lejos.nxt.I2CSensor;
import lejos.nxt.sensor.api.*;
import lejos.util.Delay;

/**
 * Abstraction for a NXT Ultrasonic Sensor. The sensor knows three modes: off,
 * continuous and ping. In continuous mode, the device periodically
 * measures the range off the nearest object.
 * In ping mode, the sensor only takes a measurement upon request (when the fetchSample method is called). 
 * Taking a measurement in ping mode takes about 50 msec. 
 * (The US sensor datasheet mentions a fourth mode of operation called capture, but we could not get this mode to work.)
 */
public class LcUltrasonic extends I2CSensor implements SampleProvider, SensorInfo{

	

		/* Device modes */
		public static final byte MODE_OFF = 0x00;
		public static final byte MODE_PING = 0x01;
		public static final byte MODE_CONTINUOUS = 0x02;
		public static final byte MODE_CAPTURE = 0x03;
		public static final byte MODE_RESET = 0x04;
		/* Device control locations */
		private static final byte REG_FACTORY_DATA = 0x11;
		private static final byte REG_CONTINUOUS_INTERVAL = 0x40;
		private static final byte REG_MODE = 0x41;
		private static final byte REG_DISTANCE = 0x42;
		private static final byte REG_CALIBRATION = 0x4a;  // according to the datasheet this should be 0x50, but this works!
		/* Device timing */
		private static final int DELAY_CMD = 5;
		private static final int DELAY_DATA_PING = 50;
		private static final int DELAY_DATA_OTHER = 30;

		private long nextCmdTime = 0;
		private byte mode = MODE_CONTINUOUS;
		private byte[] byteBuff = new byte[8];

		/**
		 * Wait until the specified time
		 */
		private void waitUntil(long when)
		{
			long delay = when - System.currentTimeMillis();
			Delay.msDelay(delay);
		}

		/*
		 * Over-ride standard get function to ensure correct inter-command timing
		 * when using the ultrasonic sensor. The Lego Ultrasonic sensor uses a
		 * "bit-banged" i2c interface and seems to require a minimum delay between
		 * commands otherwise the commands fail.
		 */
		@Override
		public synchronized int getData(int register, byte[] buf, int off, int len)
		{
			waitUntil(nextCmdTime);
			int ret = super.getData(register, buf, off, len);
			nextCmdTime = System.currentTimeMillis() + DELAY_CMD;
			return ret;
		}

		/*
		 * Over-ride the standard send function to ensure the correct inter-command
		 * timing for the ultrasonic sensor.
		 */
		@Override
		public synchronized int sendData(int register, byte[] buf, int off, int len)
		{
			waitUntil(nextCmdTime);
			int ret = super.sendData(register, buf, off, len);
			nextCmdTime = System.currentTimeMillis() + DELAY_CMD;
			return ret;
		}

		public LcUltrasonic(I2CPort port)
		{
			// Set correct sensor type, default is TYPE_LOWSPEED
			super(port, DEFAULT_I2C_ADDRESS, I2CPort.LEGO_MODE, TYPE_LOWSPEED_9V);
			nextCmdTime = System.currentTimeMillis() + DELAY_CMD;
			// Perform a reset, to clean up settings from previous program
			reset();
	    setMode(MODE_CONTINUOUS);
		}

		/**
		 * Return distance to an object. To ensure that the data returned is valid
		 * this method may have to wait a short while for the distance data to
		 * become available.
		 * 
		 * @return distance or 255 if no object in range or an error occurred
		 */
		public float fetchSample()
		{
			switch (mode)
			{
				case MODE_OFF:
					throw new IllegalStateException("sensor is off");
				case MODE_PING:
					byteBuff[0] = MODE_PING;
					sendData(REG_MODE, byteBuff, 1);
					Delay.msDelay(DELAY_DATA_PING);
					getData(REG_DISTANCE, byteBuff, 8);
					return byteBuff[0] & 0xff;
				default:
					getData(REG_DISTANCE, byteBuff, 1);
					nextCmdTime = System.currentTimeMillis() + DELAY_DATA_OTHER;
					return byteBuff[0] & 0xff;
			}
		}



		/**
		 * Return an array of distances. If in continuous mode, at most one distance
		 * is returned. If in ping mode, up to 8 distances are returned. 
		 * If the distance data is not yet available the
		 * method will wait until it is. The LEGO ultrasonic sensor can return multiple 
		 * readings in approx. 75 ms.
		 * 
		 * @param dist the destination array
		 * @param off the index of the first distance
		 */
		public void fetchSample(float dist[], int off){
			switch (mode)
			{
				case MODE_OFF:
					throw new IllegalStateException("sensor is off");
					
				case MODE_PING:
					byteBuff[0] = MODE_PING;
					sendData(REG_MODE, byteBuff, 1);
					Delay.msDelay(DELAY_DATA_PING);
					getData(REG_DISTANCE, byteBuff, 8);
					for (int i = 0; i < ((dist.length-off<8) ? dist.length-off:8) ; i++)
						dist[off + i] = byteBuff[i] & 0xff;
					break;
					
				default:
					getData(REG_DISTANCE, byteBuff, 1);
					dist[off] = byteBuff[0] & 0xff;
			}
		}

		/**
		 * Set the sensor into the specified mode. 
		 * @param mode the mode, either {@link #MODE_OFF},  {@link #MODE_CONTINUOUS}, {@link #MODE_PING}
		 * @return a negative value on error, 0 otherwise
		 */
		public int setMode(int mode)
		{
			switch (mode)
			{
				case MODE_OFF:
					byteBuff[0] = MODE_OFF;
					break;
				case MODE_PING:
					byteBuff[0] = MODE_OFF;
					break;
				case MODE_CONTINUOUS:
					byteBuff[0] = MODE_CONTINUOUS;
					break;
				default:
					throw new IllegalArgumentException("unknown mode");
			}
			int ret = sendData(REG_MODE, byteBuff, 1);
			if (mode==MODE_PING) 
				Delay.msDelay(DELAY_DATA_PING);
			if (ret == 0)
			{
				this.mode = (byte)mode;
			}
			return ret;
		}

		private int getMultiBytes(int reg, byte data[], int len)
		{
			/*
			 * For some locations that are adjacent in address it is not possible to
			 * read the locations in a single read, instead we must read them using
			 * a series of individual reads. 
			 */
			for (int i = 0; i < len; i++)
			{
				Delay.msDelay(50);
				int ret = getData(reg + i, byteBuff,0, 1);
				if (ret < 0)
					return ret;
				data[i] = byteBuff[0];
			}
			return 0;
		}

		private int setMultiBytes(int reg, byte data[], int len)
		{
			/*
			 * For some locations that are adjacent in address it is not possible to
			 * read the locations in a single write, instead we must write them
			 * using a series of individual writes. 
			 */
			for (int i = 0; i < len; i++)
			{
				byteBuff[0] = data[i];
				int ret = sendData(reg + i, byteBuff,0, 1);
				if (ret < 0)
					return ret;
			}
			return 0;
		}

		/**
		 * Return 10 bytes of factory calibration data. The bytes are as follows
		 * data[0] : Factory zero (cal1) data[1] : Factory scale factor (cal2)
		 * data[2] : Factory scale divisor.
		 * 
		 * @return negative value on error, 0 otherwise
		 */
		public int getFactoryData(byte data[])
		{
			if (data.length < 3)
				throw new IllegalArgumentException("array too small");

			return getMultiBytes(REG_FACTORY_DATA, data, 3);
		}


		/**
		 * Return 3 bytes of calibration data. The bytes are as follows data[0] :
		 * zero (cal1) data[1] : scale factor (cal2) data[2] : scale divisor.
		 * 
		 * @return negative value on error, 0 otherwise
		 */
		public int getCalibrationData(byte data[])
		{
			/*
			 * Note the lego documentation says this is at loacation 0x50, however
			 * it looks to me like this is a hex v decimal thing and it should be
			 * location 0x49 + 1 which is 0x4a not 0x50! There certainly seems to be
			 * valid data at 0x4a...
			 */
			if (data.length < 3)
				throw new IllegalArgumentException("array too small");

			return getMultiBytes(REG_CALIBRATION, data, 3);
		}

		/**
		 * Set 3 bytes of calibration data. The bytes are as follows data[0] : zero
		 * (cal1) data[1] : scale factor (cal2) data[2] : scale divisor. This does
		 * not currently seem to work.
		 * 
		 * @return 0 if ok <> 0 otherwise
		 */
		public int setCalibrationData(byte data[])
		{
			if (data.length < 3)
				throw new IllegalArgumentException("array too small");

			return setMultiBytes(REG_CALIBRATION, data, 3);
		}

		/**
		 * Return the interval used in continuous mode. This seems to be in the
		 * range 1-15. It can be read and set. However tests seem to show it has no
		 * effect. Others have reported that this does vary the ping interval (when
		 * used in other implementations). Please report any new results.
		 * 
		 * @return negative value on error, the interval otherwise
		 */
		public int getContinuousInterval()
		{
			int ret = getData(REG_CONTINUOUS_INTERVAL, byteBuff, 1);
			return ret < 0 ? ret : (byteBuff[0] & 0xFF);
		}

		/**
		 * Set the ping inetrval used when in continuous mode. See
		 * getContinuousInterval for more details.
		 * 
		 * @return negative value on error, 0 otherwise.
		 */
		public int setContinuousInterval(int interval)
		{
			if (interval < 0 || interval > 0xFF)
				throw new IllegalArgumentException("value between 0 and 0xFF expected");

			byteBuff[0] = (byte)interval;
			return sendData(REG_CONTINUOUS_INTERVAL, byteBuff, 1);
		}

		/**
		 * Returns the current operating mode of the sensor.
		 * 
		 * @return the operating mode
		 * @see #MODE_OFF
		 * @see #MODE_CONTINUOUS
		 * @see #MODE_PING
		 * @see #MODE_CAPTURE
		 */
		public int getMode()
		{
			return this.mode;
		}
		
		/**
		 * Returns the current operating mode of the sensor. In contrast to
		 * {@link #getMode()}, this method determines the operation mode by actually
		 * querying the hardware.
		 * 
		 * @return negative value on error, the operating mode otherwise
		 * @see #MODE_OFF
		 * @see #MODE_CONTINUOUS
		 * @see #MODE_PING
		 * @see #MODE_CAPTURE
		 */
		public int getActualMode()
		{
			int ret = getData(REG_MODE, byteBuff, 1);
			return ret < 0 ? ret : (byteBuff[0] & 0xFF);
		}


		public float getSampleRate() {
					return 1000.0f/70.0f;
		}

		public float getMaximumRange() {
			return 255;
		}

		public int getQuantity() {
			return Quantities.LENGTH;
		}

		public int getElementsCount() {
			if (mode==MODE_PING) return 8;
			return 1;
		}

		/**
		 * Reset the device. Performs a "soft reset" of the device. Restores things
		 * to the default state. Following this call the sensor will be off.
		 * 
		 * @return negative value on error, 0 otherwise
		 */
		public int reset()
		{
			byteBuff[0]=MODE_RESET;
			int ret = sendData(REG_MODE, byteBuff, 1);
			mode=MODE_OFF;
			return ret;
		}
		
	
	}


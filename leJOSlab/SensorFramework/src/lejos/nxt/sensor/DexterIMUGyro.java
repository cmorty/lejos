package lejos.nxt.sensor;

import lejos.nxt.I2CPort;
import lejos.nxt.I2CSensor;

public class DexterIMUGyro extends I2CSensor implements SensorVectorDataProvider {
	
	/**
	 * Internal sample rates supported by the sensor
	 */
	public enum SampleRate {

		_100Hz((byte) 0x00, 100), 
		_200Hz((byte) 0x40, 200), 
		_400Hz((byte) 0x80, 400), 
		_800Hz((byte) 0xC0, 800);
		
		private final byte	code;
		private final int rate;
 
		SampleRate(byte code, int rate) {
			this.code = code;
			this.rate=rate;
		}

		public byte getCode() {
			return code;
		}
		
		public int getRate() {
			return rate;
		}
	}

	/**
	 * Dynamic ranges supported by the sensor
	 */
	public enum Range {
		_250DPS((byte) 0x00, 8.75f), 
		_500DPS((byte) 0x10, 17.5f), 
		_2000DPS((byte) 0x20, 70f);
		private final byte	code;
		private final float	multiplier;

		Range(byte code, float multiplier) {
			this.code = code;
			this.multiplier = multiplier;
		}

		public byte getCode() {
			return code;
		}

		public float getMultiplier() {
			return multiplier;
		}

	}

	private static int				CTRL_REG1				= 0x020;
	private static int				CTRL_REG2				= 0x021;
	private static int				CTRL_REG3				= 0x022;
	private static int				CTRL_REG4				= 0x023;
	private static int				CTRL_REG5				= 0x024;
	private static int				DATA_REG				= 0x28 | 0x80;
	private static int				REG_STATUS			= 0x27;
	private static int 				address 				= 0xD2;
	protected Range						range						= Range._500DPS;
	protected SampleRate			sampleRate			= SampleRate._800Hz;
  protected float multiplier=1;
  protected byte[] buf=new byte[6];


	
	public DexterIMUGyro(I2CPort port) {
		this(port, address);
		init();
	}
	
  public DexterIMUGyro(I2CPort port, int address) {
    super(port, address, I2CPort.HIGH_SPEED, TYPE_LOWSPEED);
  }


	/**
	 * configures the sensor. Method is called whenever one of the sensor settings changes
	 */

	private void init() {
		int reg;
		Vector3f raw=new Vector3f();
		// put in sleep mode;
		sendData(CTRL_REG1, (byte) 0x08);
		// oHigh-pass cut off 1 Hz;
		sendData(CTRL_REG2, (byte) 0x00);
		// no interrupts, no fifo
		sendData(CTRL_REG3, (byte) 0x08);
		// set range
		reg = range.getCode() | 0x80;
		multiplier = (float) range.getMultiplier() / 1000f;
		sendData(CTRL_REG4, (byte) reg);
		// disable fifo and high pass
		sendData(CTRL_REG5, (byte) 0x00);
		// stabilize output signal;
		// enable all axis, set output data rate ;
		reg = sampleRate.getCode() | 0x3F;
		// set sample rate, wake up
		sendData(CTRL_REG1, (byte) reg);
		for (int s = 1; s <= 15; s++) {
			while(!isNewDataAvailable()) Thread.yield();
			fetchData(raw);
		}
	}
	
	public void fetchData(Vector3f ret) {
		int rc = getData(DATA_REG, buf, 6);
		ret.x = (float)(((buf[1]) << 8) | (buf[0] & 0xFF)) *  multiplier;
		ret.y = (float)(((buf[3]) << 8) | (buf[2] & 0xFF)) *  multiplier;
		ret.z = (float)(((buf[5]) << 8) | (buf[4] & 0xFF)) *  multiplier;
	}


	private byte getStatus() {
		getData(REG_STATUS, buf, 1);
		return buf[0];
	}

	protected boolean isDataOverrun() {
		if ((getStatus() & 0x80) == 0x80)
			return true;
		else
			return false;
	}

	/**
	 * Returns true if new data is available from the sensor
	 */
	public boolean isNewDataAvailable() {
		if ((getStatus() & 0x08) == 0x08)
			return true;
		else
			return false;
	}

	/**
	 * Sets the internal sample rate of the sensor
	 * @param rate
	 * a SampleRate object
	 */
	public void setSampleRate(SampleRate rate) {
		sampleRate = rate;
		init();
	}

	/**
	 * Returns the internal sample rate of the sensor
	 * @return
	 * a SampleRate object
	 */
	public SampleRate getSampleRate() {
		return sampleRate;
	}

	/**
	 * Sets the dynamic range of the sensor
	 * @param range
	 * a Range object
	 */
	public void setRange(Range range) {
		this.range = range;
		init();
	}

	/**
	 * Returns the dynamic range of the sensor
	 * @return
	 * a Range object
	 */
	public final Range getRange() {
		return range;
	}


	@Override
	public String getVendorID() {
		return "Dexter";
	}

	@Override
	public String getProductID() {
		return "L3G4200D";
	}

	@Override
	public String getVersion() {
		return "1.0";
	}
	
	public int getMinimumFetchInterval() {
		return (int) Math.ceil(1000.0f/(float)sampleRate.getRate());
	}

}

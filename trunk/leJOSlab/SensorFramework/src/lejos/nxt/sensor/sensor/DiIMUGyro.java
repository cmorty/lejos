/**
 * 
 */
package lejos.nxt.sensor.sensor;

import lejos.nxt.I2CPort;
import lejos.nxt.I2CSensor;
import lejos.nxt.sensor.api.*;

/**
 * @author Aswin
 *
 */
public class DiIMUGyro extends I2CSensor implements SampleProvider, SensorInfo,SensorControl {
	
	private final static float[]	RATES								= { 100,200,400,800};
	private final static int[]	RATECODES						= { 0x00,0x40,0x80,0xC0};
	private final static float[]	RANGES								= { 250,500,2000};
	private final static int[]	RANGECODES						= { 0x00,0x10,0x20};
	private final static float[]	MULTIPLIERS						= { 8.75f, 17.5f, 70f};
	
	private int range=2;
	private int rate=3;
	private float multiplier=MULTIPLIERS[range]/1000f;
	
	private static int				CTRL_REG1				= 0x020;
	private static int				CTRL_REG2				= 0x021;
	private static int				CTRL_REG3				= 0x022;
	private static int				CTRL_REG4				= 0x023;
	private static int				CTRL_REG5				= 0x024;
	private static int				REG_STATUS			= 0x27;
	private static int 				ADDRESS 				= 0xD2;
	private static int 				DATA_REG 				= 0x27 | 0x80;

	
	byte[]										buf							= new byte[7];
	float[] dummy=new float[3];	
	/**
	 * @param port
	 */
	public DiIMUGyro(I2CPort port) {
    super(port, ADDRESS, I2CPort.HIGH_SPEED, TYPE_LOWSPEED);
    init();
	}
	
	/**
	 * configures the sensor. Method is called whenever one of the sensor settings changes
	 */

	private void init() {
		int reg;

		// put in sleep mode;
		sendData(CTRL_REG1, (byte) 0x08);
		// oHigh-pass cut off 1 Hz;
		sendData(CTRL_REG2, (byte) 0x01);
		// no interrupts, no fifo
		sendData(CTRL_REG3, (byte) 0x08);
		// set range
		reg = RANGECODES[range] | 0x80;
		multiplier = MULTIPLIERS[range] / 1000f;
		sendData(CTRL_REG4, (byte) reg);
		// disable fifo and high pass
		sendData(CTRL_REG5, (byte) 0x00);
		// stabilize output signal;
		// enable all axis, set output data rate ;
		reg = RATECODES[rate] | 0xF;
		// set sample rate, wake up
		sendData(CTRL_REG1, (byte) reg);
		for (int s = 1; s <= 15; s++) {
			while(!isNewDataAvailable()) Thread.yield();
			fetchSample(dummy,0);
		}
	}
	
	/**
	 * Returns true if new data is available from the sensor
	 */
	public boolean isNewDataAvailable() {
		if ((getStatus() & 0x08) == 0x08)
			return true;
		return false;
	}
	
	private byte getStatus() {
		getData(REG_STATUS, buf, 1);
		return buf[0];
	}


	public int getQuantity() {
		return Quantities.TURNRATE;
	}

	public int getElementsCount() {
		return 3;
	}

	public void fetchSample(float[] dst, int off) {
			buf[0]=0;
			int attempts=0;
			// loop while no new data available or data overrun occured, break out after 20 attempts
			while(((buf[0] &  0x80) == 0x80 || (buf[0] & 0x08) != 0x08) && attempts++<=20) {
				getData(DATA_REG, buf, 7);
				if ((buf[0] & 0x08) != 0x08) Thread.yield();
			}
			//if (attempts>=20) Sound.beep();
			for (int i=0;i<3;i++) {
				dst[i+off]=(float) Math.toRadians((((buf[i+2]) << 8) | (buf[i+1] & 0xFF))*multiplier);
		}
	}

	public float fetchSample() {
		fetchSample(dummy,0);
		return dummy[0];
	}

	public float getSampleRate() {
		return RATES[rate];
	}

	public void setSampleRate(float rate) {
		for (int i = 0; i < RATES.length; i++)
			if (RATES[i] == rate)
				this.rate = i;
		init();
	}

	public float[] getSampleRates() {
		return RATES;
	}

	public void start() {
		sendData(CTRL_REG1, (byte) (RATECODES[rate] | 0x3F));
	}

	public void stop() {
		sendData(CTRL_REG1, (byte) 0x08);
	}

	public void setRange(float range) {
		for (int i=0;i<RANGES.length;i++)
			if (RANGES[i]==range) this.range=i;
		init();
		
	}

	public float[] getRanges() {
		return RANGES;
	}

	public float getMaximumRange() {
		return RANGES[range];
	}

}

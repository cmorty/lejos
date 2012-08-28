package lejos.nxt.sensor.sensor;

import lejos.nxt.I2CPort;
import lejos.nxt.I2CSensor;
import lejos.nxt.sensor.api.*;
import lejos.util.EndianTools;

/**
 * Driver for the ITG3200 gyrosensor
 * @author Aswin
 *
 */
public class CuITG3200 extends I2CSensor implements SampleProvider, SensorControl, SensorInfo {

	
	private static int I2C_ADDRESS = 0xd0;
	private static int REG_SRD = 0x015;
	private static int REG_LPF = 0x016;
	private static int REG_RATE = 0x1d;
	private static int PWR_MGM = 0x3e;

  private final static float[]	RATES								={800,500,400,320, 250,200,125,100,50,32};
  private final static int[]		RATECODES						={9,15,19,24, 31,39,79,159,249};
  
  private final static float[]	RANGES							= {2000};
  protected int sampleRate=0;
  protected int sampleRange=0; 
  private static float MULTIPLIER=(float) Math.toRadians(1/ 14.375);
  
  byte[] buf=new byte[6];
  float[] dummy=new float[3];

	
	public CuITG3200(I2CPort port) {
    this(port, I2CPort.HIGH_SPEED);
	}

	public CuITG3200(I2CPort port, int mode) {
		this(port, I2C_ADDRESS, mode, TYPE_LOWSPEED);
	}

	public CuITG3200(I2CPort port, int address, int mode, int type) {
		super(port, address, mode, type);
		setSampleRate(250);
		sendData(REG_LPF, (byte) 0x19);
		// set the clock
		start();
	}

	public float getSampleRate() {
		return RATES[sampleRate];
	}

	public float getMaximumRange() {
		return RANGES[sampleRange];
	}

	public void setSampleRate(float rate) {
		for (int i = 0; i < RATES.length; i++)
			if (RATES[i] == rate)
				sampleRate = i;
		sendData(REG_SRD,(byte) RATECODES[sampleRate]);
	}
	
	public float[] getSampleRates() {
		return RATES;
	}

	public void setRange(float range) {
		// Sensor supports just one range;
	}

	public float[] getRanges() {
		return RANGES;
	}

	public void start() {
		sendData(PWR_MGM, (byte) 0x01);
	}

	public void stop() {
		sendData(PWR_MGM, (byte) 0x40 );
	}

	public int getQuantity() {
		return Quantities.TURNRATE;
	}

	public int getElementsCount() {
		return 3;
	}

	public void fetchSample(float[] dst, int off) {
		getData(REG_RATE, buf, 6);
		dst[off]=EndianTools.decodeShortBE(buf, 0) * - MULTIPLIER;
		dst[off+1]=EndianTools.decodeShortBE(buf, 2) * -MULTIPLIER;
		dst[off+2]=EndianTools.decodeShortBE(buf, 4) * MULTIPLIER;
		}

	public float fetchSample() {
		fetchSample(dummy,0);
		return dummy[0];
	}

}

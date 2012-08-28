package lejos.nxt.sensor.sensor;

import lejos.nxt.I2CPort;
import lejos.nxt.I2CSensor;
import lejos.nxt.sensor.api.*;
import lejos.util.EndianTools;

/**
 * Driver for the ADXL345 accelerometer
 * @author Aswin
 *
 */
public class CuADXL345 extends I2CSensor implements SampleProvider, SensorInfo, SensorControl {
	private static int I2C_ADDRESS = 0xa6;
  private static byte ACCEL = 0x32;
  private static byte REG_RATE = 0x2c;
  private static byte REG_RANGE=0x31;
  private static byte POWER_CTL=0x2d;
  private static float MULTIPLIER=9.81f*1f/256f;

  private final static float[]	RATES								= {3.125f,6.25f,12.5f,25,50,100,200,400,800,1600};
  private final static float[]	RANGES							= { 2,4,8,16};
  
  protected int sampleRate=0;
  protected int sampleRange=0; 
  
  byte buf[]=new byte[6];
  float[] dummy=new float[3];


	public CuADXL345(I2CPort port) {
    this(port, I2CPort.HIGH_SPEED);
	}

	public CuADXL345(I2CPort port, int mode) {
		this(port, I2C_ADDRESS, mode, TYPE_LOWSPEED);
	}

	public CuADXL345(I2CPort port, int address, int mode, int type) {
		super(port, address, mode, type);
		setRange(4);
		setSampleRate(200);
		start();
	}

	public void setSampleRate(float rate) {
		for (int i = 0; i < RATES.length; i++)
			if (RATES[i] == rate)
				sampleRate = i;
		sendData(REG_RATE,(byte) (sampleRate+6));
	}

	public float[] getSampleRates() {
		return RATES;
	}

	public void setRange(float range) {
		for (int i = 0; i < RANGES.length; i++)
			if (RANGES[i] == range)
				sampleRange=i;
		sendData(REG_RANGE,(byte) (sampleRange+8));
	}

	public float[] getRanges() {
		return RANGES;
	}

	public void start() {
    sendData(POWER_CTL, (byte) 0x08);
	}

	public void stop() {
    sendData(POWER_CTL, (byte) 0x00);
	}

	public float getSampleRate() {
		return RATES[sampleRate];
	}

	public float getMaximumRange() {
		return RANGES[sampleRange];
	}

	public int getQuantity() {
		return Quantities.ACCELERATION;
		}

	public int getElementsCount() {
		return 3;
	}

	public void fetchSample(float[] dst, int off) {
	   getData(ACCEL, buf, 6);
//	   for (int i=0;i<3;i++)
//	  	 dst[off+i]=EndianTools.decodeShortLE(buf, i*2) * MULTIPLIER;
			dst[off]=EndianTools.decodeShortLE(buf, 0) * - MULTIPLIER;
			dst[off+1]=EndianTools.decodeShortLE(buf, 2) * -MULTIPLIER;
			dst[off+2]=EndianTools.decodeShortLE(buf, 4) * MULTIPLIER;

	}

	public float fetchSample() {
		fetchSample(dummy,0);
		return dummy[0];
	}

}

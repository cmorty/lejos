/**
 * 
 */
package lejos.nxt.sensor.sensor;

import lejos.nxt.I2CPort;
import lejos.nxt.I2CSensor;
import lejos.nxt.sensor.api.*;
import lejos.util.Delay;
import lejos.util.EndianTools;

/**
 * Class to access the MindSensors Acceleration sensor (V2 and V3)
 * @author Aswin
 *
 */
public class MsAccelV3 extends I2CSensor implements SampleProvider {
	
	private final static float[]	RANGES							= { 1.5f,2,4,6};
	
	private byte[] buf=new byte[6];
	private float[] dummy=new float[3];
	private int range=2;
  private static byte ACCEL = 0x45;
  private static byte REGRANGE = 0x41;
  private static float MULTIPLIER=0.00981f;


	/**
	 * @param port
	 */
	public MsAccelV3(I2CPort port) {
		super(port, DEFAULT_I2C_ADDRESS);
	}

	/**
	 * @param port
	 * @param address
	 */
	public MsAccelV3(I2CPort port, int address) {
		super(port, address, I2CPort.HIGH_SPEED, TYPE_LOWSPEED);
		sendData(REGRANGE, (byte) (49+range));
    Delay.msDelay(50);	}

	public void fetchSample(float[] data, int off) {
		getData(ACCEL, buf, 6);
		for (int i=0;i<3;i++) 
	   data[i+off]=EndianTools.decodeShortLE(buf, i*2)*MULTIPLIER;
	}

	public int getQuantity() {
		return Quantities.ACCELERATION;
	}

	public int getElemensCount() {
		return 3;
	}

	
	@Override
	public String getVendorID() {
	  return "MindSensors";
	}

	@Override
	public String getProductID() {
	  return "ACCL-Nx";
	}

	@Override
	public String getVersion() {
	  return "v3";
	}
	
  /**
   * Sets the sensitivity unit
   * @param range
   */
  public void setRange(int range) {
  	for (int i=0;i<RANGES.length;i++) {
  		if (range==RANGES[i]) {
  			range=i;
  	    sendData(REGRANGE, (byte) (49+range));
  	    Delay.msDelay(150);
  	    return;
  		}
  	}
  }

  /**
   * Returns the current sample range of the sensor
   * @return sensitivity
   */
  public float getRange() {
    return RANGES[range];
  }
  
  public float[] getRanges() {
  	return RANGES;
  }

	public float fetchSample() {
		getData(ACCEL, buf, 2);
	  return EndianTools.decodeShortLE(buf, 0)*MULTIPLIER;
	}
  
}

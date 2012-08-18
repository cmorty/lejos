/**
 * 
 */
package lejos.nxt.sensor.sensor;

import lejos.nxt.I2CPort;
import lejos.nxt.I2CSensor;
import lejos.nxt.sensor.api.*;
import lejos.util.EndianTools;


/**
 * Class to access the Acceleration sensor from Dexter Industries IMU (MMA7455)
 * @author Aswin
 *
 */
public class DiImuAccel extends I2CSensor implements SampleProvider {
	protected static final int	ACCEL	= 0x00;
	protected static final int	MODE_REG	= 0x16;
	protected static final int	ADDRESS	= 0x3A;
	float multiplier	= 1.0f/(64.0f*9.81f);

	byte[] buf=new byte[6];

	/**
	 * @param port
	 */
	public DiImuAccel(I2CPort port) {
    super(port, ADDRESS, I2CPort.HIGH_SPEED, TYPE_LOWSPEED);
		sendData(MODE_REG, (byte) 0x41);
	}

	public float getSampleRate() {
		return 250;
	}

	
	public int getQuantity() {
		return Quantities.ACCELERATION;
	}

	public int getElementsCount() {
		return 3;
	}

	public void fetchSample(float[] dst, int off) {
		getData(ACCEL, buf, 6);
		for (int i=0;i<3;i++) {
			dst[i+off]=EndianTools.decodeShortBE(buf, i);
			if ((buf[i+1] & 0x02)!=0 ) 
				dst[i+off]=-(dst[i+off]-512);
			dst[i+off]*=multiplier;
		}
	}

	public float fetchSample() {
		return 0;
	}



}


/*

protected static final int	ACCEL	= 0x00;
protected static final int	MODE_REG	= 0x16;
protected static final int	DEFAULT_ADDRESS	= 0x3A;





private byte[]  buf=new byte[6];


public AccelDIMU(I2CPort port) {
	super(port, DEFAULT_ADDRESS);
	sendData(MODE_REG, (byte) 0x01);
	calibrator.setCalibrationMode(CalibrationMode.ACCELOROMETER2);
	calibrator.setName("dIMUaccel");
	calibrator.setRange(1.0f);
	calibrator.load();
	sensorUnit= AccelUnits.G;
	multiplier	= 1.0f/64.0f;
}



public void fetchRawAccel(Vector3f raw2) {
	getData(ACCEL, buf, 6);
  raw2.x=((buf[1] ) << 8) | (buf[0] & 0xFF) & 0x03ff; if (raw2.x>512) raw2.x-=1024;
  raw2.y=((buf[3] ) << 8) | (buf[2] & 0xFF) & 0x03ff; if (raw2.y>512) raw2.y-=1024;
  raw2.z=((buf[5] ) << 8) | (buf[4] & 0xFF) & 0x03ff; if (raw2.z>512) raw2.z-=1024;

}

@Override
public String getVendorID() {
  return "Dexter";
}

@Override
public String getProductID() {
  return "MMA7455L";
}

@Override
public String getVersion() {
  return "1.0";
}

*/




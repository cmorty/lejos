package lejos.nxt.sensor.sensor;

import lejos.nxt.I2CPort;
import lejos.nxt.addon.AccelHTSensor;
import lejos.nxt.sensor.api.*;

/**
 * Class to access the HiTechnic NXT Acceleration / Tilt Sensor (NAC1040)
 * @author Aswin
 *
 */
public class HtAccel extends AccelHTSensor implements SampleProvider{
	int buf[]=new int[3];

	public HtAccel(I2CPort port) {
		super(port);
	}
	
	public HtAccel(I2CPort port, int address) {
		super(port,address);
	}

	public int getQuantity() {
		return Quantities.ACCELERATION;
	}

	public int getElementsCount() {
		return 3;
	}

	public void fetchSample(float[] dst, int off) {
		getAllAccel(buf, 0);
		for (int i=0;i<3;i++) 
			dst[i+off]=buf[i]*(9.81f/200.0f);
	}

	public float fetchSample() {
		getAllAccel(buf, 0);
		return buf[0]*(9.81f/200.0f);
	}

}

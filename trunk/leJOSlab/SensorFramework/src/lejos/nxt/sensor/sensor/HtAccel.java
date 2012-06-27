package lejos.nxt.sensor.sensor;

import lejos.nxt.I2CPort;
import lejos.nxt.addon.AccelHTSensor;
import lejos.nxt.sensor.api.*;

/**
 * Class to access the HiTechnic NXT Acceleration / Tilt Sensor (NAC1040)
 * @author Aswin
 *
 */
public class HtAccel extends AccelHTSensor implements SensorVectorDataProvider{
	int buf[]=new int[3];

	public HtAccel(I2CPort port) {
		super(port);
	}
	
	public HtAccel(I2CPort port, int address) {
		super(port,address);
	}

	public int getMinimumFetchInterval() {
		return 10;
	}

	
	/* Provide acceleration in (m/s2) over three axes
	 * @see lejos.nxt.sensor.api.SensorVectorDataProvider#fetchData(lejos.nxt.vecmath.Vector3f)
	 */
	public void fetchData(Vector3f data) {
		getAllAccel(buf, 0);
		data.set(buf[0]*9.81f/200.0f,buf[1]*9.81f/200.0f,buf[2]*9.81f/200.0f);
	}

}

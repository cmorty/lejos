/**
 * 
 */
package lejos.nxt.sensor.sensor;

import lejos.nxt.I2CPort;
import lejos.nxt.addon.AccelMindSensor;
import lejos.nxt.sensor.api.SensorVectorDataProvider;
import lejos.nxt.vecmath.Vector3f;

/**
 * Class to access the MindSensors Acceleration sensor (V2 and V3)
 * @author Aswin
 *
 */
public class MsAccelV3 extends AccelMindSensor implements SensorVectorDataProvider {
	int buf[]=new int[3];

	/**
	 * @param port
	 */
	public MsAccelV3(I2CPort port) {
		super(port);
	}

	/**
	 * @param port
	 * @param address
	 */
	public MsAccelV3(I2CPort port, int address) {
		super(port, address);
	}

	/* (non-Javadoc)
	 * @see lejos.nxt.sensor.api.SensorVectorDataProvider#getMinimumFetchInterval()
	 */
	public int getMinimumFetchInterval() {
		return 4;
	}

	/* Provide acceleration in (m/s2) over three axes
	 * @see lejos.nxt.sensor.api.SensorVectorDataProvider#fetchData(lejos.nxt.vecmath.Vector3f)
	 */
	public void fetchData(Vector3f data) {
		getAllAccel(buf, 0);
		data.set(buf[0]*9810f,buf[1]*9810f,buf[2]*9810f);
	}

}

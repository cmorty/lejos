/**
 * 
 */
package lejos.nxt.sensor.sensor;

import lejos.nxt.I2CPort;
import lejos.nxt.addon.DIMUAccel;
import lejos.nxt.sensor.api.*;


/**
 * Class to access the Acceleration sensor from Dexter Industries IMU (MMA7455)
 * @author Aswin
 *
 */
public class DiImuAccel extends DIMUAccel implements SampleProviderVector {
	float buf[]=new float[3];

	/**
	 * @param port
	 */
	public DiImuAccel(I2CPort port) {
		super(port);
		super.setAccelUnit(AccelUnits.MS2);
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
	public void fetchSample(Vector3f data) {
		fetchAllAccel(buf);
		data.set(buf[0],buf[1],buf[2]);
	}

}

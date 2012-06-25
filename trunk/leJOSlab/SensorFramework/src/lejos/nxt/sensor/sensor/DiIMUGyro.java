/**
 * 
 */
package lejos.nxt.sensor.sensor;

import lejos.nxt.I2CPort;
import lejos.nxt.addon.DIMUGyro;
import lejos.nxt.sensor.api.SensorVectorDataProvider;
import lejos.nxt.vecmath.Vector3f;

/**
 * @author Aswin
 *
 */
public class DiIMUGyro extends DIMUGyro implements SensorVectorDataProvider {
	float[] buf=new float[3];

	/**
	 * @param port
	 */
	public DiIMUGyro(I2CPort port) {
		super(port);
		super.setRateUnit(RateUnits.RPS);
	}

	/* (non-Javadoc)
	 * @see lejos.nxt.sensor.api.SensorVectorDataProvider#getMinimumFetchInterval()
	 */
	public int getMinimumFetchInterval() {
		return 2;
	}

	/**
	 * Returns turnrate (in rad/s)
	 * @see lejos.nxt.sensor.api.SensorVectorDataProvider#fetchData(lejos.nxt.vecmath.Vector3f)
	 */
	public void fetchData(Vector3f data) {
		super.fetchAllRate(buf);
		data.set(buf[0],buf[1],buf[2]);
	}

}

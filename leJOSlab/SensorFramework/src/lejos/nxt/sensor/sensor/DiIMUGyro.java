/**
 * 
 */
package lejos.nxt.sensor.sensor;

import lejos.nxt.I2CPort;
import lejos.nxt.addon.DIMUGyro;
import lejos.nxt.sensor.api.*;

/**
 * @author Aswin
 *
 */
public class DiIMUGyro extends DIMUGyro implements SampleProviderVector {
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

	public void fetchSample(Vector3f data) {
		super.fetchAllRate(buf);
		data.set(buf[0],buf[1],buf[2]);
	}

}

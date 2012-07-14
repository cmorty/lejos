/**
 * 
 */
package lejos.nxt.sensor.sensor;

import lejos.nxt.I2CPort;
import lejos.nxt.addon.CruizcoreGyro;
import lejos.nxt.sensor.api.*;

/**
 * @author Aswin
 *
 */
/**
 * Class that reads data from the Cruizcore sensor (gyro and accelerometer)
 * @author Aswin
 *
 */
public class MiCruizcore extends CruizcoreGyro implements SampleProviderVector,SampleProvider,SensorQuantities {

	Accel accel=new Accel();
	Gyro gyro=new Gyro();
	Azimuth azimuth=new Azimuth();
	
	/**
	 * @param port
	 */
	public MiCruizcore(I2CPort port) {
		super(port);
	}

	/* (non-Javadoc)
	 * @see lejos.nxt.sensor.api.SensorVectorDataProvider#getMinimumFetchInterval()
	 */
	public int getMinimumFetchInterval() {
		return 10;
	}

	/**
	 *  Returns acceleration (in m/s2) over three axes
	 * @see lejos.nxt.sensor.api.SampleProviderVector#fetchSample(Vector3f)
	 */
	public void fetchSample(Vector3f data) {
	  accel.fetchSample(data);
	}
	
	
	/**
	 * returns rotation (in rad);
	 * @return
	 */
	public float fetchSample() {
		return azimuth.fetchSample();
	}
	
	/**
	 * Returns a data provider for scalar data
	 * @param quantity 
	 * This sensor only supports AZIMUTH as scalar quantity
	 * @return
	 * reference to a SensorDataProvider
	 */
	public SampleProvider getDataProvider(int quantity) {
		if (quantity!=ROTATION) throw new IllegalArgumentException("Invalid quantity");
		return azimuth;
	}

	/**
	 * Returns a data provider for vector data
	 * @param quantity 
	 * This sensor only supports TURNRATE and ACCELERATION as scalar quantity
	 * @return
	 * reference to a SensorDataProvider
	 */
	public SampleProviderVector getVectorDataProvider(int quantity) {
		if (quantity==TURNRATE)return gyro;
		if (quantity==ACCELERATION)return accel;
		throw new IllegalArgumentException("Invalid quantity");
	}

	
	
	
	private class Gyro implements SampleProviderVector {

		public int getMinimumFetchInterval() {
			return getMinimumFetchInterval();
		}

		/* Returns rate of turn (in Rad/s)
		 * @see lejos.nxt.sensor.api.SensorVectorDataProvider#fetchData(lejos.nxt.vecmath.Vector3f)
		 */
		public void fetchSample(Vector3f data) {
			data.x=Float.NaN;
			data.y=Float.NaN;
			data.z=(float) Math.toRadians(getRate()/100.0f);
		}
		
	}

	private class Accel implements SampleProviderVector {

		public int getMinimumFetchInterval() {
			return getMinimumFetchInterval();
		}

		public void fetchSample(Vector3f data) {
			int[] buf=getAccel();
			// assuming 2 G range
			data.x=buf[0]*9.81f/1000.0f;
			data.y=buf[1]*9.81f/1000.0f;
			data.z=buf[2]*9.81f/1000.0f;
		}
		
	}
	
	private class Azimuth implements SampleProvider {

		public int getMinimumFetchInterval() {
			return getMinimumFetchInterval();
		}

		/* Returns rate of turn (in Rad/s)
		 * @see lejos.nxt.sensor.api.SensorVectorDataProvider#fetchData(lejos.nxt.vecmath.Vector3f)
		 */
		public float fetchSample() {
			return (float) Math.toRadians(getAngle()/100.0f);
		}
		
	}
	
	
	
}

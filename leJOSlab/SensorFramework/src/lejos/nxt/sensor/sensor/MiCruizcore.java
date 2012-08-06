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
public class MiCruizcore extends CruizcoreGyro implements MultipleSampleProvider {

	private static final int[] QUANTITIES={Quantities.ACCELERATION, Quantities.ROTATION, Quantities.ANGLE};
	@SuppressWarnings("synthetic-access")
	private final SampleProvider[] providers={new Accel(),new Gyro(),new Azimuth()};
	
	/**
	 * @param port
	 */
	public MiCruizcore(I2CPort port) {
		super(port);
	}

	
	/**
	 * Returns a data provider for vector data
	 * @param quantity 
	 * This sensor only supports TURNRATE and ACCELERATION as scalar quantity
	 * @return
	 * reference to a SensorDataProvider
	 */
	public SampleProvider getSampleProvider(int quantity) {
		for (int i=0;i<QUANTITIES.length;i++)
			if (quantity==QUANTITIES[i]) return providers[i];
		return null;
	}

	
	
	
	private class Gyro implements SampleProvider {

	
		/* Returns rate of turn (in Rad/s)
		 * @see lejos.nxt.sensor.api.SensorVectorDataProvider#fetchData(lejos.nxt.vecmath.Vector3f)
		 */
		public void fetchSample(float[] data, int off) {
			data[off]=fetchSample();
		}

		public int getQuantity() {
			return Quantities.TURNRATE;
		}

		public int getElemensCount() {
			return 1;
		}

		public float fetchSample() {
			return (float) Math.toRadians(getRate()/100.0f);
		}
		
	}

	private class Accel implements SampleProvider {


		public void fetchSample(float[] data, int off) {
			int[] buf=getAccel();
			// assuming 2 G range
			for (int i=0;i<3;i++)
				data[i+off]=buf[0]*9.81f/1000.0f;
		}

		public int getQuantity() {
			return Quantities.ACCELERATION;
		}

		public int getElemensCount() {
			return 3;
		}

		public float fetchSample() {
			return getAccel()[0]*9.81f/1000.0f;
		}
		
	}
	
	private class Azimuth implements SampleProvider {

		public float fetchSample() {
			return (float) Math.toRadians(getAngle()/100.0f);
		}

		public int getQuantity() {
			return Quantities.ANGLE;
		}

		public int getElemensCount() {
			return 1;
		}

		public void fetchSample(float[] dst, int off) {
			dst[off]=fetchSample();
		}
		
	}

	public int[] getSupportedQuantities() {
		return new int[]{Quantities.ACCELERATION, Quantities.ROTATION, Quantities.ANGLE};
	}

	
}

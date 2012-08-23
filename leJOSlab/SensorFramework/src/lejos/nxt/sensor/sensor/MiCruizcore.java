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
public class MiCruizcore extends CruizcoreGyro{

	private SampleProvider acceleration=null, turnRate=null, angle=null;
	
	/**
	 * @param port
	 */
	public MiCruizcore(I2CPort port) {
		super(port);
	}

	public SampleProvider getTurnRateProvider() {
		if (turnRate==null) {
			turnRate=new Gyro();
		}
		return turnRate;
	}

	public SampleProvider getAccelerationProvider() {
		if (acceleration==null) {
			acceleration=new Accel();
		}
		return acceleration;
	}

	public SampleProvider getAngleProvider() {
		if (angle==null) {
			angle=new Azimuth();
		}
		return angle;
	}

	
	
	
	private class Gyro implements SampleProvider {

	
		Gyro() {
		}

		public void fetchSample(float[] data, int off) {
			data[off]=fetchSample();
		}

		public int getQuantity() {
			return Quantities.TURNRATE;
		}

		public int getElementsCount() {
			return 1;
		}

		public float fetchSample() {
			return (float) Math.toRadians(getRate()/100.0f);
		}
		
	}

	private class Accel implements SampleProvider {
		
		Accel(){
		}
		

		public void fetchSample(float[] data, int off) {
			int[] buf=getAccel();
			// TODO: assuming 2 G range
			for (int i=0;i<3;i++)
				data[i+off]=buf[0]*9.81f/1000.0f;
		}

		public int getQuantity() {
			return Quantities.ACCELERATION;
		}

		public int getElementsCount() {
			return 3;
		}

		public float fetchSample() {
			return getAccel()[0]*9.81f/1000.0f;
		}
		
	}
	
	private class Azimuth implements SampleProvider {
		
		Azimuth() {
		}

		public float fetchSample() {
			return (float) Math.toRadians(getAngle()/100.0f);
		}

		public int getQuantity() {
			return Quantities.ANGLE;
		}

		public int getElementsCount() {
			return 1;
		}

		public void fetchSample(float[] dst, int off) {
			dst[off]=fetchSample();
		}
		
	}
	
}

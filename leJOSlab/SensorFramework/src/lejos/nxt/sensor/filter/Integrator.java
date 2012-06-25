package lejos.nxt.sensor.filter;

import lejos.nxt.sensor.api.SensorDataProvider;

/**
 * Integrates sensor data over time. Usefull for example to:
 * <li>convert gyro output (degrees/second) to azymuth (Degrees)</li>
 * <li>Acceleration to speed</li>
 * <li>Speed to position</li>
 * @author Aswin
 *
 */
public class Integrator extends SensorDataBuffer{
	long lastTime=0;

	
	public Integrator(SensorDataProvider source) {
		super(source);
	}
	
	/**
	 * Sets the current value of the integrator to the specified value.
	 * @param value
	 * The value 
	 */
	public void resetTo(float value) {
		this.currentValue=value;
	}


	@Override
	protected float fetchAndProcess() {
		float value=super.fetchAndProcess();
		long now=System.nanoTime();
		if (lastTime==0) lastTime=now;
		double dt=(now-lastTime)/Math.pow(10,9);
		lastTime=now;
		return (float) (currentValue+value*dt);
	}
	
	

}

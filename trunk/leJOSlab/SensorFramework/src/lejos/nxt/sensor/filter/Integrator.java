package lejos.nxt.sensor.filter;

import lejos.nxt.sensor.api.*;

/**
 * Integrates sensor data over time. Usefull for example to:
 * <li>convert gyro output (degrees/second) to azymuth (Degrees)</li>
 * <li>Acceleration to speed</li>
 * <li>Speed to position</li>
 * @author Aswin
 *
 */
public class Integrator extends AbstractFilter{
	long lastTime=0;
	private float[]	currentValue;
	private int	quantity=Quantities.UNDEFINED;

	
	public Integrator(SampleProvider source) {
		super(source);
		currentValue=new float[elements];
		switch(source.getQuantity()){
			case Quantities.ACCELERATION:
				quantity=Quantities.VELOCITY;
				break;
			case Quantities.VELOCITY:
				quantity=Quantities.LENGTH;
				break;
			case Quantities.TURNRATE:
				quantity=Quantities.ANGLE;
				break;
				default:
					quantity=Quantities.UNDEFINED;
					break;
		}
	}
	
	
	/**
	 * Sets the current value of the integrator to the specified value.
	 * @param value
	 * The value 
	 */
	public void resetTo(float value) {
		for (int i=0;i<elements;i++)
			currentValue[i]=value;
		lastTime=0;
	}



	public void fetchSample(float dst[],int off) {
		source.fetchSample(dst, off);
		long now=System.nanoTime();
		if (lastTime==0) lastTime=now;
		double dt=(now-lastTime)*Math.pow(10,-9);
		lastTime=now;
		for (int i=0;i<elements;i++) {
			currentValue[i]+=dst[i]*dt;
			dst[i+off]=currentValue[i];
		}
	}
	
	@Override
	public int getQuantity() {
		return quantity;
	}

	
	
	

}

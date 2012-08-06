package lejos.nxt.sensor.sensor;

import lejos.nxt.ADSensorPort;
import lejos.nxt.sensor.api.*;

/**
 * Adapter POC class lejos.nxt.LightSensor-to-SensorDataProvider 
 * 
 * @author Kirk
 *
 */
public class LcLight extends lejos.nxt.LightSensor implements SampleProvider{
	
	/**
	 * create instance of LightSensorAdapterPOC2.
	 * @param port
	 */
	public LcLight(ADSensorPort port){
		super(port);
	}
	
	
	public float fetchSample() {
		return this.getLightValue();
	}


	public int getQuantity() {
		return Quantities.RAW;
	}


	public int getElemensCount() {
		return 1;
	}


	public void fetchSample(float[] dst, int off) {
		dst[off]=fetchSample();
	}
	
}

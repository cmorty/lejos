package lejos.nxt.sensor.sensor;

import lejos.nxt.ADSensorPort;
import lejos.nxt.sensor.api.SampleProvider;

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
	
	public int getMinimumFetchInterval() {
		return 4;
	}

	public float fetchSample() {
		return this.getLightValue();
	}
	
}

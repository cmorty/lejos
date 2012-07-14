package lejos.nxt.sensor.sensor;

import lejos.nxt.ADSensorPort;
import lejos.nxt.sensor.api.SampleProvider;

/**
 * Adapter POC class lejos.nxt.LightSensor-to-SensorDataProvider 
 * 
 * @author Kirk
 *
 */
public class LightSensorAdapterPOC2 extends lejos.nxt.LightSensor implements SampleProvider{
	
	/**
	 * create instance of LightSensorAdapterPOC2.
	 * @param port
	 */
	public LightSensorAdapterPOC2(ADSensorPort port){
		super(port);
	}
	
	public int getMinimumFetchInterval() {
		return 4;
	}

	public float fetchSample() {
		return this.getLightValue();
	}
	
}

package lejos.nxt.sensor.sensor;

import lejos.nxt.ADSensorPort;

/**
 * Adapter POC class lejos.nxt.LightSensor-to-SensorDataProvider 
 * 
 * @author Kirk
 *
 */
public class LightSensorAdapterPOC2 extends lejos.nxt.LightSensor implements SensorDataProvider{
	
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

	public float fetchData() {
		return this.getLightValue();
	}
	
}

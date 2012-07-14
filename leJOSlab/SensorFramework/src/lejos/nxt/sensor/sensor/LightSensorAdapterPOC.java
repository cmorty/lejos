package lejos.nxt.sensor.sensor;

import lejos.nxt.ADSensorPort;
import lejos.nxt.sensor.api.SampleProvider;

/**
 * Adapter POC class lejos.nxt.LightSensor-to-SensorDataProvider via 
 * <code>getLightSensorDataProvider()</code> method.
 * 
 * @author Kirk
 *
 */
public class LightSensorAdapterPOC extends lejos.nxt.LightSensor{
	private LightSensorAdapter lsa;
	
	private class LightSensorAdapter implements SampleProvider {

		public LightSensorAdapter() {
			// do nothing for now...
		}

		public int getMinimumFetchInterval() {
			return 4;
		}

		public float fetchSample() {
			return LightSensorAdapterPOC.this.getLightValue();
		}
		
	}
	
	/**
	 * create instance of LightSensorAdapterPOC. This sets up instance of private class LightSensorAdapter
	 * to pass back via factory method getLightSensorDataProvider()
	 * @param port
	 */
	public LightSensorAdapterPOC(ADSensorPort port){
		super(port);
		lsa = new LightSensorAdapter();
	}
	
	/**
	 * @return this LightSensor wrapped as SensorDataProvider
	 */
	public SampleProvider getLightSensorDataProvider(){
		return lsa;
	}
	
}

package lejos.nxt.sensor;

import lejos.nxt.ADSensorPort;

/**
 * adapter class lejos.nxt.LightSensor to SensorDataProvider
 * @author Kirk
 *
 */
public class LightSensorAdapterPOC extends lejos.nxt.LightSensor{
	private LightSensorAdapter lsa;
	
	private class LightSensorAdapter implements SensorDataProvider {

		public LightSensorAdapter() {
			// do nothing for now...
		}

		public int getMinimumFetchInterval() {
			return 4;
		}

		public float fetchData() {
			// TODO Auto-generated method stub
			return LightSensorAdapterPOC.this.getLightValue();
		}
		
	}
	
	/**
	 * create instance of LightSensorAdapterPOC. Sets up instance of private class LightSensorAdapter
	 * to pass bak via factory method getLightSensorDataProvider()
	 * @param port
	 */
	public LightSensorAdapterPOC(ADSensorPort port){
		super(port);
		lsa = new LightSensorAdapter();
	}
	
	/**
	 * @return the LightSensor wrapped as SensorDataProvider
	 */
	public SensorDataProvider getLightSensorDataProvider(){
		return lsa;
	}
	
}

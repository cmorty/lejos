package javax.microedition.sensor;

/**
 * Implementation of SensorInfo for the Mindsensors range of acceleration sensors.
 * It has acceleration channels and the tilt channels.
 * 
 * @author Lawrie Griffiths
 */
public class MindsensorsAccelerationSensorInfo extends I2CSensorInfo {

	public MindsensorsAccelerationSensorInfo() {
		infos = new I2CChannelInfo[]
		                 {new AccelerationChannelInfo("x"),
		            	  new AccelerationChannelInfo("y"),
		            	  new AccelerationChannelInfo("z"),
		            	  new TiltChannelInfo("x"),
			              new TiltChannelInfo("y"),
			              new TiltChannelInfo("z")};
	}

	public String getQuantity() {
		return "acceleration";
	}
	
	public String[] getModelNames() {
		return new String[]{"ACCL3X03", "ACCL-NX"};
	}
}

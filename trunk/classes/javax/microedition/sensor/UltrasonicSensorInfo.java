package javax.microedition.sensor;

/**
 * SensorInfo for the LEGO Mindstorms ultrasonic sensor
 * 
 * @author Lawrie Griffiths
 */
public class UltrasonicSensorInfo extends I2CSensorInfo {
	public UltrasonicSensorInfo() {
		infos = new I2CChannelInfo[]{new UltrasonicChannelInfo()};
	}

	public String getQuantity() {
		return "proximity";
	}
	
	public String[] getModelNames() {
		return new String[]{"Sonar"};
	}
}

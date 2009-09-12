package javax.microedition.sensor;

/**
 * Sensor Info for HiTechnic compass
 * 
 * @author Lawrie Griffiths
 */
public class HiTechnicCompassSensorInfo extends I2CSensorInfo {	
	public HiTechnicCompassSensorInfo() {
		infos = new I2CChannelInfo[]{new HeadingChannelInfo()};
	}
	
	public String getQuantity() {
		return "direction";
	}
	
	public String[] getModelNames() {
		return new String[]{"Compass"};
	}
}

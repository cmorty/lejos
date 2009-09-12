import lejos.nxt.*;

public class I2CDevices {
	public static void main(String[] args) throws Exception {
		I2CSensor[] sensors = {
				new I2CSensor(SensorPort.S1),
				new I2CSensor(SensorPort.S2),
				new I2CSensor(SensorPort.S3),
		        new I2CSensor(SensorPort.S4)};
		
		LCD.setAutoRefresh(0);
		while (!Button.ESCAPE.isPressed()) {
			for(int i=0;i<sensors.length;i++) {
				LCD.drawString("        ", 0, i);
				LCD.drawString(getName(sensors[i]), 0, i);
				LCD.refresh();
			}
		}
	}
	
	static String getName(I2CSensor sensor) {
		String name;
		
		// Try all possible I2C addresses until a device is found
		for (int i=1;i<0x7F;i++) {
			sensor.setAddress(i);
			name = sensor.getSensorType();
			if (name.length() > 0 && name.charAt(0)>= 'A') return name;
		}
		return "none";
	}
}

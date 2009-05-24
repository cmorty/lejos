import lejos.nxt.*;

/**
 * This is a test of remote reading of sensors from the PC
 * using the iCommand equivalent classes in pccomm.jar
 * 
 * @author Lawrie Griffiths
 *
 */
public class SensorTest {
	public static void main(String[] args) {
		LightSensor light = new LightSensor(SensorPort.S1);
		SoundSensor sound = new SoundSensor(SensorPort.S2);
		TouchSensor touch = new TouchSensor(SensorPort.S3);
		UltrasonicSensor sonic = new UltrasonicSensor(SensorPort.S4);
		
		while(sound.readValue() < 90) {		
			System.out.println("light = " + light.readValue());
			System.out.println("sound = " + sound.readValue());
			System.out.println("touch = " + touch.isPressed());
			System.out.println("distance = " + sonic.getDistance());
			try {
				Thread.sleep(1000);
			} catch (InterruptedException ie) {}
		}
	}
}

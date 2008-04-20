
/**
 * Demo for Mindsensors RXmux
 *  
 * @author Michael Smith <mdsmitty@gmail.com>
 */
import lejos.nxt.*;
public class RCXMuxTest{
	public static void main(String[] args) throws Exception {
		RCXSensorMultiplexer mux = new RCXSensorMultiplexer(SensorPort.S1);
		RCXLightSensor light = new RCXLightSensor(SensorPort.S1);
		
		while(true){
			mux.setChannelOne();
			LCD.drawInt(light.readValue(), 4, 0, 0);
			Thread.sleep(250);
			mux.setChannelTwo();
			LCD.drawInt(light.readValue(), 4, 0, 0);
			Thread.sleep(250);
			mux.setChannelThree();
			LCD.drawInt(light.readValue(), 4, 0, 0);
			Thread.sleep(250);
			mux.setChannelFour();
			LCD.drawInt(light.readValue(), 4, 0, 0);
			Thread.sleep(250);

		}
	}
}
import lejos.nxt.*;

public class RCXMMTest {

	public static void main(String[] args) throws Exception {
		RCXMotorMultiplexer mm = new RCXMotorMultiplexer(SensorPort.S1);
		
		while (true) {
			LCD.drawString(mm.getSensorType(), 0, 0);
			LCD.drawInt(mm.getDirection(0),3,0,1);
			LCD.drawInt(mm.getSpeed(0),3,0,2);
			LCD.refresh();
			
			mm.A.setPower(100);
			mm.A.forward();
			Thread.sleep(1000);
			mm.A.backward();
			Thread.sleep(1000);
		}
	}
}

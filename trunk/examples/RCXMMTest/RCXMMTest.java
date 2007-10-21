import lejos.nxt.*;

public class RCXMMTest {

	public static void main(String[] args) throws Exception {
		RCXMotorMultiplexer mm = new RCXMotorMultiplexer(SensorPort.S1, 0);
		RCXMotor m = new RCXMotor(mm);
		
		while (true) {
			LCD.drawString(mm.getSensorType(), 0, 0);
			LCD.drawInt(mm.getDirection(),0,1);
			LCD.drawInt(mm.getSpeed(),0,2);
			LCD.refresh();
			
			m.setPower(100);
			m.forward();
		}
	}
}

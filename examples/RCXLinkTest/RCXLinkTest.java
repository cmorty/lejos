import lejos.nxt.*;

public class RCXLinkTest  {

	public static void main(String[] args) throws Exception {
		RCXLink link = new RCXLink(SensorPort.S1);
		
		while (true) {
			LCD.drawString(link.getSensorType(), 0, 0);
			LCD.refresh();
			
			link.flush();
			link.setDefault();
			
			link.beep();			
			link.runProgram(1);
			
			Thread.sleep(5000);
		}
	}
}

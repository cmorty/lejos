
import lejos.nxt.*;
import lejos.nxt.addon.NXTMMX;
import lejos.nxt.addon.NXTMMXMotor;
/**
 *This is sample code demonstrating how to use the mindsensors NXTMMX.
 * 
 * @author Michael D. Smith mdsmitty@gmail.com
 *
 */
public class NXTMMXSample {
	
	public static void main(String[] args) {
		NXTMMX mux = new NXTMMX(SensorPort.S1);
		
		NXTMMXMotor cat = mux.A;
		NXTMMXMotor dog = mux.B;
		
		//Demo of basic forwards and backwards operations
		cat.setSpeed(100);
		dog.setSpeed(50);
				
		cat.forward();
		dog.backward();

		Button.ENTER.waitForPressAndRelease();

		mux.breakMotors();
		
		
		Button.ENTER.waitForPressAndRelease();
		
		//demo tacho stuff
		cat.rotate(500);
		dog.rotateTo(-400, true);
		
		Button.ENTER.waitForPressAndRelease();
		
		//demo time stuff
		cat.setSpeed(5);
		cat.rotateTime(15, true);
	}
}

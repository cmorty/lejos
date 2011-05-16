
import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.SensorPort;
import lejos.nxt.addon.MMXRegulatedMotor;
import lejos.nxt.addon.NXTMMX;

import lejos.robotics.RegulatedMotor;
import lejos.robotics.RegulatedMotorListener;

import lejos.util.Delay;

/**
 *This is sample code demonstrating how to use the mindsensors NXTMMX.
 * 
 * @author Michael D. Smith mdsmitty@gmail.com
 * @author Kirk P. Thompson
 *
 */
public class NXTMMXSample {
	
	public static void main(String[] args) {
		NXTMMX mux = new NXTMMX(SensorPort.S1);
		
		MMXRegulatedMotor cat = new MMXRegulatedMotor(mux, NXTMMX.MMX_MOTOR_1);
		MMXRegulatedMotor dog = new MMXRegulatedMotor(mux, NXTMMX.MMX_MOTOR_2);
		
	    // add a listener to tell us when cat starts and stops
        RegulatedMotorListener rml = new RegulatedMotorListener(){
	        public void rotationStarted(RegulatedMotor motor,int tachoCount, boolean stalled, long timeStamp){
	            LCD.drawString("cat start:" + tachoCount + "  ", 0,2); 
	        }

	        public void rotationStopped(RegulatedMotor motor, int tachoCount, boolean stalled, long timeStamp) {
	            LCD.drawString("cat stop:" + tachoCount + "   ", 0,2); 
	        }
	    };
	    cat.addListener(rml); 
        
		//Demo of basic forwards and backwards operations
		cat.setPower(10);
		dog.setSpeed(500);
				
		cat.forward();
		dog.backward();

		LCD.drawString("Enter to stop ",0,1);
	    while(!Button.ENTER.isPressed()) {
	        Delay.msDelay(130);
	        LCD.drawString("cat tach:" + cat.getTachoCount() + " ", 0,4);
	        LCD.drawString("dog tach:" + dog.getTachoCount() + " ", 0,5);
	        LCD.drawString("cat dps:" + cat.getRotationSpeed() + " ", 0,6);
	        LCD.drawString("dog dps:" + dog.getRotationSpeed() + " ",0,7);
	    }
        
	    LCD.clearDisplay();
		mux.stopMotors();
		
		Delay.msDelay(500);
	    LCD.drawString("Enter to Rotate",0,1);
		Button.ENTER.waitForPressAndRelease();
		
		//demo tacho stuff
		cat.rotate(-2000, true);
		dog.rotateTo(8000, true);
		
	    LCD.drawString("Enter to exit  ",0,1);
	    while(!Button.ENTER.isPressed()) {
            Delay.msDelay(130);
            LCD.drawString("cat stall:" + cat.isStalled() + " ", 0,4); 
            LCD.drawString("dog moving:" + dog.isMoving() + " ", 0,5); 
            LCD.drawString("cat tach:" + cat.getTachoCount() + " ", 0,6);
            LCD.drawString("dog dps:" + dog.getRotationSpeed() + " ",0,7);
        }
        mux.fltMotors();

	}
}

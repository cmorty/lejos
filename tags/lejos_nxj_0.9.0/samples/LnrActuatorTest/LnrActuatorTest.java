import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.MotorPort;
import lejos.nxt.addon.LinearActuator;

import lejos.util.Delay;

/**
 * Test of leJOS LinearActuator driver for the Firgelli L12-NXT-50&100 series linear actuators.
 * <p>
 * Connect the Linear Actuator to port A.
 * Use left button to retract, right to extend, ENTer to change power, and ESC to exit.
 * <p>
 * Avoid running the stroke to the end stops as this is hard on the actuator. The LinearActuator class provides stall
 * detection but it still takes some milliseconds to shut down the motor as it is working against the stop.
 * 
 * @author Kirk P. Thompson
 */
public class LnrActuatorTest {
    private LinearActuator _la;
    private MotorPort _motorPort;
    
    public LnrActuatorTest() {
    }

    public static void main(String[] args) {
        LnrActuatorTest testActuator = new LnrActuatorTest();
        testActuator.controlIt();
    }
    
    private void controlIt(){
        LCD.drawString("Control It!",0,1);
        LCD.drawString("<Retract Extend>",0,2);
        LCD.drawString("ENT=Change power",0,3);
        LCD.drawString("ESC=Exit",0,7);
        int power = 100;
        _motorPort=MotorPort.A;
        _la = new LinearActuator(_motorPort);
        _la.setPower(power);
        LCD.drawString("power=" + power + "  ",0,4);
        while (!Button.ESCAPE.isPressed()) {
            if (Button.ENTER.isPressed()){
                power++;
                if (power>100) power = 50;
                LCD.drawString("power=" + power + "  ",0,4);
                _la.setPower(power);
                Delay.msDelay(200);
            }
            if (Button.LEFT.isPressed()){
                _la.retract(200, true);
                while (Button.LEFT.isPressed()) Thread.yield();
                _la.stopActuator();
                Delay.msDelay(200);
            }
            if (Button.RIGHT.isPressed()){
                _la.extend(200, true);
                while (Button.RIGHT.isPressed()) Thread.yield();
                _la.stopActuator();
                Delay.msDelay(200);
            }
            Delay.msDelay(100);            
        }
    }
}

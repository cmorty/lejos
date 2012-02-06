package org.lejos.sample.lnractuatortest;
import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.MotorPort;
import lejos.nxt.addon.LnrActrFirgelliNXT;
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

    public static void main(String[] args) {
        LnrActuatorTest testActuator = new LnrActuatorTest();
        testActuator.controlIt();
    }
    
    private void controlIt(){
        LnrActrFirgelliNXT la = new LnrActrFirgelliNXT(MotorPort.A);
        int power = 100;
        la.setPower(power);
        LCD.drawString("Control It!",0,1);
        LCD.drawString("<Retract Extend>",0,2);
        LCD.drawString("ENT=Change power",0,6);
        LCD.drawString("ESC=Exit",0,7);
        
        la.setPower(power);
        LCD.drawString("power=" + power + "  ",0,3);
        while (!Button.ESCAPE.isDown()) {
            if (Button.ENTER.isDown()){
                power++;
                if (power>100) power = 0;
                LCD.drawString("                ",0,3);
                LCD.drawString("power=" + power,0,3);
                la.setPower(power);
                Delay.msDelay(40);
            }
            if (Button.LEFT.isDown()){
                LCD.drawString("       ",0,5);
                la.move(-200, true);
                while (Button.LEFT.isDown()) {
                    Delay.msDelay(80);
                    if (la.isStalled()) {
                        LCD.drawString("STALL!",0,5);
                        break;
                    }
                    LCD.drawString("tach:" + la.getTachoCount() + "  ", 0, 4);
                }
                la.stop();
                Delay.msDelay(120);
            }
            if (Button.RIGHT.isDown()){
                LCD.drawString("       ",0,5);
                la.move(200, true);
                while (Button.RIGHT.isDown()) {
                    Delay.msDelay(80);
                    if (la.isStalled()) {
                        LCD.drawString("STALL!",0,5);
                        break;
                    }
                    LCD.drawString("tach:" + la.getTachoCount() + "  ", 0, 4);
                }
                la.stop();
                Delay.msDelay(120);
            }
            Delay.msDelay(80);
        }
    }
}

package lejos.nxt.addon.tetrix;

import lejos.nxt.I2CPort;
import lejos.nxt.I2CSensor;

/** HiTechnic Servo Controller abstraction. Provides <code>TetrixMotor</code> instances which are used to control
 * the Tetrix servos.
 *
 * @see ControllerProvider
 * @author Kirk P. Thompson
 */
public class ServoController extends I2CSensor {
    public static final int SERVO_1 = 0;
    public static final int SERVO_2 = 1;
    public static final int SERVO_3 = 2;
    public static final int SERVO_4 = 3;
    public static final int SERVO_5 = 4;
    public static final int SERVO_6 = 5;
    
    private final int i2cAddress;
    private ServoMotor[] servos= new ServoMotor[6];// TODO real limits
    
    ServoController(I2CPort port, int i2cAddress) {
        super(port, i2cAddress, I2CPort.LEGO_MODE, TYPE_LOWSPEED);
        this.i2cAddress = i2cAddress;
    }

    /** Get the <code>ServoMotor</code> instance that is associated with the <code>servoID</code>.
     * @param servoID The motor ID number <code>SERVO_1</code> to <code>SERVO_6</code>. This is indicated on the 
     * HiTechnic Servo Controller.
     * @return The <code>ServoMotor</code> instance associated with the labeled channel
     */
    public ServoMotor getServo(int servoID) {
        if (servoID<SERVO_1 || servoID>SERVO_6) {
            throw new IllegalArgumentException("Invalid servo ID");
        }
        if (servos[servoID]==null) servos[servoID]=new ServoMotor(this, servoID);
        return servos[servoID];
    }
    
    synchronized int doCommand(int command, int operand, int channel) {
        // TODO
        return 0;   
    }
    
    /**
     * Returns whether or not there are servos moving on this controller.
     * 
     * @return <code>true</code> if any servo is moving to position.
     */
    public boolean isMoving(){
        return false; // TODO
    }

    /** Set all servos connected to this controller to float mode.
     */
    public void flt() {
        // TODO
    }
    
    /**
     * Sets the step time used for all servos on this controller. The Step time sets the step time for the servo channel 
     * which has the furthest to move. Other servo channels which are not at their designated positions yet will run at a 
     * slower rate to ensure they reach their destination positions at the same time.
     * <p>
     * The step time is a delay before progressing to the next step. For example, if a servo is at 50, and you give it a 
     * new position of 200, it will normally go as fast as it can to get to the new position. If you want it to 
     * go to 200, but not at maximum output, you can set the speed to a value from 0 to 15.
     * <p>
     * One of the main things it could be useful for, is if you have two servos with different loads, and you want them 
     * to be as much in sync as possible. You can set the speed to slow the controller from changing the servo signals instantly.
     * 
     * @param step Step Time, 0-15. 0=disable step time.
     * @throws IllegalArgumentException If step is not in the range 0 to 15
     * @see #getStepTime
     */
    public void setStepTime(int step){
        if ((step < 0) || (15 < step)) throw new IllegalArgumentException();
        // TODO
        
    }
    
    /**
     * Gets the step time used for all servos on this controller.
     * @return The Step Time
     * @see #setStepTime
     */
    public int getStepTime(){
        // TODO
        return 0;
    }
}

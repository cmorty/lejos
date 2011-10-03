package lejos.nxt.addon.tetrix;

/** Basic Tetrix DC motor without encoder support.
 * 
 * @author Kirk P. Thompson
 */
public class BasicMotor implements lejos.robotics.DCMotor{
    MotorController mc;
    int channel;
    int retVal;
    
    BasicMotor(MotorController mc, int channel) {
        this.mc=mc;
        this.channel=channel;
        setPower(100);
    }

    public void setPower(int power) {
        power=Math.abs(power);
        if (power>100) power=100;
        retVal = mc.doCommand(MotorController.CMD_SETPOWER, power, channel);
    }

    public int getPower() {
        return mc.doCommand(MotorController.CMD_GETPOWER, 0, channel);
    }
    
    public void forward() {
        retVal = mc.doCommand(MotorController.CMD_FORWARD, 0, channel);
    }

    public void backward() {
        retVal = mc.doCommand(MotorController.CMD_BACKWARD, 0, channel);
    }

    public void stop() {
        retVal = mc.doCommand(MotorController.CMD_STOP, 0, channel);
    }

    public void flt() {
        retVal = mc.doCommand(MotorController.CMD_FLT, 0, channel);
    }

    public boolean isMoving() {
        return 1==mc.doCommand(MotorController.CMD_ISMOVING, 0, channel);
    }

    /** Used to alter the forward/reverse direction mapping for the motor output. This is primarily intended to
     * harmonize the forward and reverse directions for motors on opposite sides of a skid-steer chassis.
     * @param reverse <code>true</code> to reverse direction mapping for this motor
     */
    public void setReverse(boolean reverse) {
        int op=0;
        if (reverse) op=1;
        retVal = mc.doCommand(MotorController.CMD_SETREVERSE, op, channel);
    }
}

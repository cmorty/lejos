package lejos.nxt.addon.tetrix;

/** Basic Tetrix DC motor abstraction without encoder support.
 * 
 * @author Kirk P. Thompson
 */
public class TetrixMotor implements lejos.robotics.DCMotor{
    TetrixMotorController mc;
    int channel;
    int retVal;
    
    TetrixMotor(TetrixMotorController mc, int channel) {
        this.mc=mc;
        this.channel=channel;
        setPower(100);
    }

    public void setPower(int power) {
        power=Math.abs(power);
        if (power>100) power=100;
        retVal = mc.doCommand(TetrixMotorController.CMD_SETPOWER, power, channel);
    }

    public int getPower() {
        return mc.doCommand(TetrixMotorController.CMD_GETPOWER, 0, channel);
    }
    
    public void forward() {
        retVal = mc.doCommand(TetrixMotorController.CMD_FORWARD, 0, channel);
    }

    public void backward() {
        retVal = mc.doCommand(TetrixMotorController.CMD_BACKWARD, 0, channel);
    }

    public void stop() {
        retVal = mc.doCommand(TetrixMotorController.CMD_STOP, 0, channel);
    }

    public void flt() {
        retVal = mc.doCommand(TetrixMotorController.CMD_FLT, 0, channel);
    }

    public boolean isMoving() {
        return 1==mc.doCommand(TetrixMotorController.CMD_ISMOVING, 0, channel);
    }

    /** Used to alter the forward/reverse direction mapping for the motor output. This is primarily intended to
     * harmonize the forward and reverse directions for motors on opposite sides of a skid-steer chassis.
     * <p>
     * Changes to this setting take effect on the next motor command.
     * @param reverse <code>true</code> to reverse direction mapping for this motor
     */
    public void setReverse(boolean reverse) {
        int op=0;
        if (reverse) op=1;
        retVal = mc.doCommand(TetrixMotorController.CMD_SETREVERSE, op, channel);
    }
}

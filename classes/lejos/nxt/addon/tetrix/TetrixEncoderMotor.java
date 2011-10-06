package lejos.nxt.addon.tetrix;

import lejos.nxt.NXTRegulatedMotor;

import lejos.robotics.Encoder;

import lejos.util.Delay;

/** Tetrix DC motor abstraction with encoder support. The Tetrix motor must have an encoder installed and connected to
 * the controller for the methods in this class to work. If an encoder is not installed, use the <code>{@link TetrixMotor}</code>
 * class instead.
 * 
 * @author Kirk P. Thompson
 */
public class TetrixEncoderMotor extends TetrixMotor implements Encoder{
    TetrixEncoderMotor(TetrixMotorController mc, int channel) {
        super(mc, channel);
    }

    public int getTachoCount() {
        return (int)(mc.getEncoderValue(channel)*.25);
    }

    /** Reset the the tachometer count. Calling this method will stop any current motor action. This is imposed by the HiTechic
     * Motor Controller firmware. 
     */
    public void resetTachoCount() {
        mc.doCommand(TetrixMotorController.CMD_RESETTACHO, 0, channel);
    }
   
    private synchronized void waitRotateComplete() {
        while (mc.rotateIsBUSY(channel)) {
            Delay.msDelay(100);
        }
    }
    
    /**
     * Rotate by the requested number of degrees with option for wait until completion or immediate return where the motor
     * completes its rotation asynchronously.
     * 
     * @param degrees number of degrees to rotate relative to the current position.
     * @param immediateReturn if <code>true</code>, do not wait for the move to complete.
     */
    public void rotate(int degrees, boolean immediateReturn){
        int cmd=TetrixMotorController.CMD_ROTATE;
        if (!immediateReturn) cmd=TetrixMotorController.CMD_ROTATE_WAIT;
        mc.doCommand(cmd, degrees, channel);
        if (!immediateReturn) waitRotateComplete();
    }
    
    /**
     * Rotate to the target angle with option for wait until completion or immediate return where the motor
     * completes its rotation asynchronously.
     * 
     * @param limitAngle Angle [in degrees] to rotate to.
     * @param immediateReturn if <code>true</code>, do not wait for the move to complete
     */
    public void rotateTo(int limitAngle, boolean immediateReturn){
        int cmd=TetrixMotorController.CMD_ROTATE_TO;
        if (!immediateReturn) cmd=TetrixMotorController.CMD_ROTATE_TO_WAIT;
        mc.doCommand(cmd, limitAngle, channel);
        if (!immediateReturn) waitRotateComplete();
    }

    /**
     * Disable or Enable internal motor controller speed regulation. Setting this to <code>true</code> will cause 
     * the motor controller firmware to adjust the motor power to compensate for changing loads in order to maintain 
     * a constant motor speed.
     * 
     * @param regulate <code>true</code> to enable regulation, <code>false</code> otherwise.
     */
    public void setRegulated(boolean regulate){
        int operand=0;
        if (regulate) operand=1;
        mc.doCommand(TetrixMotorController.CMD_SETREGULATE, operand, channel);
    }
}

package lejos.nxt.addon.tetrix;

import lejos.robotics.Encoder;

/** Tetrix DC motor abstraction with encoder support.
 * 
 * @author Kirk P. Thompson
 */
public class TetrixEncoderMotor extends TetrixMotor implements Encoder{
    TetrixEncoderMotor(TetrixMotorController mc, int channel) {
        super(mc, channel);
    }

    public int getTachoCount() {
        return mc.doCommand(TetrixMotorController.CMD_GETTACHO, 0, channel);
    }

    /** Reset the the tachometer count. Calling this method will stop any current motor action. This is imposed by the HiTechic
     * Motor Controller firmware. To keep any current motor action active, immediately call the appropriate 
     * <code>forward()</code>, etc. method after
     * calling this method.
     */
    public void resetTachoCount() {
        retVal = mc.doCommand(TetrixMotorController.CMD_RESETTACHO, 0, channel);
    }
}

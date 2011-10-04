package lejos.nxt.addon.tetrix;

import lejos.robotics.Encoder;

/** Tetrix DC motor abstraction with encoder support.
 * 
 * @author Kirk P. Thompson
 */
public class TachoMotor extends BasicMotor implements Encoder{
    TachoMotor(MotorController mc, int channel) {
        super(mc, channel);
    }

    public int getTachoCount() {
        return mc.doCommand(MotorController.CMD_GETTACHO, 0, channel);
    }

    /** Reset the the tachometer count. Calling this method will stop any current motor action. This is imposed by the HiTechic
     * Motor Controller firmware. To keep any current motor action active, immediately call the appropriate 
     * <code>forward()</code>, etc. method after
     * calling this method.
     */
    public void resetTachoCount() {
        retVal = mc.doCommand(MotorController.CMD_RESETTACHO, 0, channel);
    }
}

package lejos.nxt.addon;

import lejos.robotics.DCMotor;
import lejos.robotics.Encoder;

/**
 * Interface that defines the minimal implementation for a Linear Actuator device. Linear Actuator classes should provide 
 * non-blocking extend/retract actions through the <code>actuate()</code> method
 * and stall detection to avoid motor damage due to running against the end stops, etc.
 * <p>
 * Motor regulation is not specified in this interface as it may be difficult to determine the accurate length per time 
 * (ie. mm/sec) rate due to encoder tick granularity of the linear actuator. It is up to the implementor to decide if the 
 *  <code>actuate()</code> method should produce regulated movement.
 * 
 * @see LnrActrFirgelliNXT
 * @author Kirk P. Thompson
 */
public interface LinearActuator extends Encoder {
    /**
     * Set the power level 0%-100% to be applied to the actuator motor where 0% is no movement and 100% is full speed.
     * @param power new motor power 0-100%
     */
    public void setPower(int power);

    /**
     * Returns the current actuator motor power setting.
     * @return current power 0-100%
     */
    public int getPower();

    /** The actuator should retract (negative <code>distance</code> value) or extend (positive <code>distance</code> value)
     * in encoder ticks <code>distance</code>. The <tt>distance</tt> is specified to be relative to the actuator shaft position
     * at the time of calling this method. The absolute unit per encoder tick is device-dependent and should be specified in the 
     * implementation documentation. 
     * <p>
     * Stall detection needs to be implemented to stop the actuator in the event of an actuator motor stall condition.
     * <P>
     * If <code>immediateReturn</code> is true, this method should not block and return immediately. The actuator stops when the
     * stroke <tt>distance</tt> is met or a stall is detected. 
     * @param distance The distance to move the actuator shaft
     * @param immediateReturn <code>true</code> returns immediately, <code>false</code> waits for the action to complete (or a stall)
     */
    public void actuate(int distance, boolean immediateReturn);
    
    /** Return <code>true</code> if the actuator is in motion due to an <code>actuate()</code> order.
     * @return <code>true</code> if the actuator is in motion. <code>false</code> otherwise.
     */
    public boolean isMoving();
    
    /**
     * Returns true if an <code>actuate()</code> order ended due to a stalled motor. This should behave like a latch where the 
     * reset of the stall status is done on a new <code>actuate()</code> order.
     * @return <code>true</code> if actuator motor stalled during an <code>actuate()</code> order. <code>false</code> otherwise.
     */
    public boolean isStalled();
    
    /**
     * Cause the actuator to stop immediately and resist any further motion. Cancel any <code>actuate()</code> orders in progress.
     */
    public void stop();
}

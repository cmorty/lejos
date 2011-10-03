package lejos.nxt.addon.tetrix;

/** The pulse nominally ranges from 1.0 ms to 2.0 ms with 1.5 ms always being center of range. 
 * Pulse widths outside this range can be used for "overtravel" -moving the servo beyond its normal range.
 * <p>
 * A servo pulse of 1.5 ms width will typically set the servo to its "neutral" position or 45°, 
 * a pulse of 1.25 ms could set it to 0° and a pulse of 1.75 ms to 90°. 
 * The physical limits and timings of the servo hardware varies between brands and models, but a 
 * general servo's angular motion will travel somewhere in the range of 90° - 120° and the 
 * neutral position is almost always at 1.5 ms. This is the "standard pulse servo mode" used by all hobby analog servos.
 * <p>
 * The HiTechic Servo Controller allows setting of the PWM output from 0.75 – 2.25mS. Note that some servos may hit their 
 * internal mechanical limits at each end of this range causing them to consume excessive current and <b>potentially be damaged</b>.
 * 
 * @author Kirk P. Thompson
 */
public class ServoMotor {
    private ServoController sc;
    private int channel;
    
    ServoMotor(ServoController sc, int channel) {
        this.sc=sc;
        this.channel=channel;
        sc.doCommand(1,1,channel); // TODO Remove
    }

    /** Set the operating range of the servo in degrees. Default at instantiation is 180.
     * @param degrees
     */
    public void setRange(int degrees){
        // TODO
    }
    
    /**
     * Sets the angle target of a Servo
     * @param angle Set servo angle (0 to 255)
     * 
     */
    public void setAngle(float angle){
        // TODO
    }
    /**
     * Returns the current servo angle as specified by the last call to <code>setAngle()</code>.
     * The servo position may or may not be at the returned angle if mechanical limits have been reached.
     * 
     * @return Current servo angle
     */
    public float getAngle(){
        // TODO
        return 0;
    }
    
    
}

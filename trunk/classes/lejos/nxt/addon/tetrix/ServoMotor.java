package lejos.nxt.addon.tetrix;

 /** Basic Tetrix servo motor abstraction. Servos are driven by a PWM signal from the controller with varying pulse widths
  * contolling the rotational position of the servo actuator shaft.
  * <P>
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

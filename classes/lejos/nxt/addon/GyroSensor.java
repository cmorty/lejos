package lejos.nxt.addon;
   
import lejos.nxt.ADSensorPort;
import lejos.nxt.SensorConstants;
import lejos.robotics.Gyroscope;
import lejos.util.Delay;

/*
 * WARNING: THIS CLASS IS SHARED BETWEEN THE classes AND pccomms PROJECTS.
 * DO NOT EDIT THE VERSION IN pccomms AS IT WILL BE OVERWRITTEN WHEN THE PROJECT IS BUILT.
 */

/**
 * Support the HiTechnic Gyro sensor.
 * <p>
 * <b>Note:</b> You may want to use <code>setSpeed()</code> on any motor you will be using before instantiating so the AD sensor voltage stablizes. 
 * Otherwise, the offset
 * may be skewed. See LeJOS forum post <a href="http://lejos.sourceforge.net/forum/viewtopic.php?f=7&t=2276">"motor setSpeed() 
 * changes AD sensor value"</a>
 * <p>
 * http://www.hitechnic.com/
 * 
 * @author Lawrie Griffiths
 * @author Kirk Thompson
 *
 */
public class GyroSensor implements SensorConstants, Gyroscope {
	ADSensorPort port;
	private int offset = 0;
    private double gsRawTotal;
    private int samples;
    
    /**
     * Creates and initializes a new <code>GyroSensor</code> bound to passed <code>ADSensorPort</code>.
     * 
     * @param port The <code>SensorPort</code> the Gyro is connected to
     * @see lejos.nxt.SensorPort
     */
    public GyroSensor(ADSensorPort port) {
		this.port = port;
		port.setTypeAndMode(TYPE_CUSTOM, MODE_RAW);
	}
	
    /**
     * Creates and initializes a new <code>GyroSensor</code> bound to passed <code>ADSensorPort</code> and sets the 
     * offset.
     * 
     * @param port The <code>SensorPort</code> the Gyro is connected to
     * @param offset The offset to apply to <code>readValue()</code>
     * @see lejos.nxt.SensorPort
     */
	public GyroSensor(ADSensorPort port, int offset) {
		this(port);
		this.offset = offset;
	}
	
	/**
	 * Read the gyro raw value and return with offset applied. Set offset to zero to return raw value.
	 * 
	 * @return gyro value
     * @see #setOffset(int)
     * @see #getAngularVelocity
	 */
	public int readValue() { 
		return (port.readRawValue() - offset); 
	}
	
	/**
	 * Set the offset used by <code>readValue()</code>. Default at instantiation is zero.
     * @param offset The <code>int</code> offset value
     * @see #readValue
	 */
	public void setOffset(int offset) {
		this.offset = offset;
	}

    /** Calculate and return the current angular velocity. When integrating for a heading, values less than 1.0 can be ignored 
     * to minimize perceived drift since the resolution of the Gyroscope sensor is 1 deg/sec.
     * 
     * @return The current angular velocity in degrees/sec
     */
    public double getAngularVelocity() {
        int gsVal;
        double stdev;
        
        gsVal=readValue();
        // get the standard deviation of the raw value against the total bias population + itself.
        stdev=Math.sqrt(Math.pow(gsVal-(gsRawTotal+gsVal)/(samples+1),2));
        // if less than 2 standard deviations, allow to be used as the bias population
        if (stdev<2.0f) {
            gsRawTotal+=gsVal;
            samples++; // if run long enough, this will... wrap?, blowup?
        }
        // subtract the mean bias from the raw value and return it
        return gsVal-gsRawTotal/samples;
    }


    /** Sample the <u>stationary</u> (make sure it is) Gyro Sensor to determine the offset. Will reset the offset for
     * <code>ReadValue()</code> to 0 (zero). Takes 5 seconds.
     * 
     * @see #setOffset(int)
     */
    public void setOffset() {
        // seed the initial bias population
        offset = 0;
        gsRawTotal=readValue();
        Delay.msDelay(10);
        gsRawTotal+=readValue();
        samples=2;
        // populate bias population for 5 seconds
        for (int i=0;i<500;i++) {
            getAngularVelocity();
            Delay.msDelay(10);
        }
    }
}

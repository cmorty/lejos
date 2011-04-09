package lejos.nxt.addon;
   
import lejos.nxt.ADSensorPort;
//import lejos.nxt.LCD;
import lejos.nxt.SensorConstants;
import lejos.robotics.Gyroscope;
import lejos.util.Delay;

/*
 * WARNING: THIS CLASS IS SHARED BETWEEN THE classes AND pccomms PROJECTS.
 * DO NOT EDIT THE VERSION IN pccomms AS IT WILL BE OVERWRITTEN WHEN THE PROJECT IS BUILT.
 */

/**
 * Support the HiTechnic Gyro sensor. Provides raw <code>int</code> (with optional offset) and <code>float</code> angular velocity in degrees/sec.
 * <p>
 * <b>Note:</b> You may want to use <code>setSpeed()</code> on any motor you will be using before instantiating so the AD sensor voltage stablizes. 
 * Otherwise, the offset
 * may be skewed. See LeJOS forum post <a href="http://lejos.sourceforge.net/forum/viewtopic.php?f=7&t=2276">"motor setSpeed() 
 * changes AD sensor value"</a>
 * 
 * <h3>Assumptions:</h3>
 * <ul>
 * <li>The HiTechnic Gyro sensor NGY1044 (or equivalent) is being used. (<a href="http://www.hitechnic.com/" target=-"_blank">
 * http://www.hitechnic.com/</a>)
 * <li>If used, the <code>{@link #getAngularVelocity}</code> method call rate is at least 100 times/sec.
 * </ul>
 * 
 * @author Lawrie Griffiths
 * @author Kirk Thompson
 *
 */
public class GyroSensor implements SensorConstants, Gyroscope {
	protected ADSensorPort port;
	private int offset = 0;
    private float gsRawTotal =0f;
    private float gsvarianceTotal =0f;
    private int samples=0;
    private boolean calibrating=false;
    private long timestamp;
    private int consecutiveStdv=0;
    /**
     * Creates and initializes a new <code>GyroSensor</code> bound to passed <code>ADSensorPort</code>.
     * 
     * @param port The <code>SensorPort</code> the Gyro is connected to
     * @see lejos.nxt.SensorPort
     */
    public GyroSensor(ADSensorPort port) {
		this.port = port;
		port.setTypeAndMode(TYPE_CUSTOM, MODE_RAW);
	    timestamp = System.currentTimeMillis();
	}
	
    /**
     * Creates and initializes a new <code>GyroSensor</code> bound to passed <code>ADSensorPort</code> and sets the 
     * offset to be used in <code>{@link #readValue}</code>.
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
     * <p>
     * Be sure to call <code>recalibrateOffset()</code> to establish the offset before using this method. 
     * 
     * @return The current angular velocity in degrees/sec
     * @see #recalibrateOffset
     */
    public float getAngularVelocity() {
        int gsVal;
        float stdev;
        float gsvarianceTemp;
        // get sensor raw value (note that offset was zeroed in recalibrateOffset()  but could be changed by user)
        gsVal=readValue();
        // calc variance
        gsvarianceTemp=(float)Math.pow(gsVal-(gsRawTotal+gsVal)/(samples+1),2);
        // get the standard deviation of the raw value against the total bias population
        stdev=(float)Math.sqrt((gsvarianceTotal + gsvarianceTemp)/(samples+1));
        // if less than x standard deviation from maintained offset population and somewhat consecutive, allow to 
        // be used in the offset/bias population
        if(stdev<.55f)consecutiveStdv++; else consecutiveStdv=0; 
        // assume consecutive stdevs within defined range provide representative sample of non-moving sensor
        if (consecutiveStdv>10||calibrating) {
            consecutiveStdv=0;
            // add value to sample pop
            gsRawTotal+=gsVal;
            samples++; 
            // add variance to variance population so we can do standard deviation calc in future iterations
            gsvarianceTotal+=Math.pow(gsVal-gsRawTotal/samples, 2);
        }
        
        // re-baseline every 5 seconds
        if (System.currentTimeMillis()-timestamp>5000) {
            timestamp = System.currentTimeMillis();
//            LCD.drawString("mean:" + (gsRawTotal/samples) + " ", 0, 1);
            stdev=(float)Math.sqrt(gsvarianceTotal/samples);
//            LCD.drawString("stdev:" + stdev + " ", 0, 2);
            // re-baseline using current averages
            gsRawTotal /= samples;
            gsvarianceTotal /= samples;
            samples = 1;
        }
        
        // subtract the mean bias from the raw value and return it
        return gsVal-gsRawTotal/samples;
    }


    /** Samples the <u>stationary</u> (make sure it is) Gyro Sensor to determine the offset. Will reset the offset for
     * <code>ReadValue()</code> to 0 (zero). Takes 5 seconds.
     * 
     * @see #setOffset(int)
     */
    public void recalibrateOffset() {
        // *** seed the initial bias/offset population
        offset=0;
        gsvarianceTotal=0;
        gsRawTotal=0;
        samples=0;
        // populate bias population for 5 seconds
        calibrating=true;
        for (int i=0;i<1000;i++) {
            getAngularVelocity();
            Delay.msDelay(5);
        }
        calibrating=false;
    }
}

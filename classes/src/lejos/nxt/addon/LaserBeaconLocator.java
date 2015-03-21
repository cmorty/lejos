package lejos.nxt.addon;

import lejos.robotics.RegulatedMotor;
import lejos.robotics.localization.BeaconLocator;
import lejos.nxt.*;
import lejos.util.Delay;
import java.util.ArrayList;

// ** DEVELOPER NOTES **
// TODO: Option to stop scanning and return as soon as X beacons located. This is a tweak to speed up.
// TODO: Why doesn't the MirrorMotor work? Something change with motor classes or something about LaserBeaconLocator code?
// TODO: Could be improved by allowing scan to occur clockwise or counterclockwise. More freedom of robot design.
// Boolean switch in constructor, simply causes order of scan to be flipped before returning.

/**
 * <p>This class scans a room for beacons and identifies the angles to the beacons. It uses a laser sensor to perform the scan,
 * and pylons covered with reflective tape for the beacons.</p>
 * 
 * <p>The laser reflecting off the beacon to the light sensor on the laser sensor indicates a beacon is found. 
 * The laser must have the ability to pulse on and off within a 10 ms period. This class pulses the light on and off to
 * test if a bright spot occurs only when the laser is on.</p>
 * 
 * @author Andy Shaw and Brian Bagnall
 *
 */
public class LaserBeaconLocator implements BeaconLocator
{
	private double gear_ratio = 1; // Degrees to rotate motor to achieve 1 degree movement
	private RegulatedMotor motor=null;
	private LaserSensor ls;
	private int baseTacho;
    private static final int SCAN_ANGLE = 180; // 1/2 the scan angle. 180 will do 360 sweep all around
    //private static final int HIT_THRESHOLD = 10;
    private ArrayList<Double> beaconAngles = new ArrayList<Double>();
    private int speed = 16;
    private int motor_return_speed;// speed of motor when returning to 0
    private int motor_scan_speed; // speed of motor when scanning
    
    /**
     * 
     * @param port
     * @param motor The motor to rotate the sensor. 
     * @param gearRatio Degrees to rotate motor to achieve 1 degree movement. If motor gear has 12 teeth and scanner 36, gearRatio = 3
     */
	public LaserBeaconLocator(LaserSensor ls, RegulatedMotor motor, double gearRatio) {
		this.gear_ratio = gearRatio;
		setScanSpeed(this.speed);
		this.ls = ls;
		this.motor = motor;
		baseTacho = motor.getTachoCount();
		reset();
	}
	
	/**
	 * <p>Sets the speed at which the scan takes place, in degrees per second. If a speed of 20 degrees per second is used, it will
	 * take 18 seconds for a complete scan (360 degrees / 20 degrees per sec = 18 seconds)</p> 
	 * <p>Default speed is 16 degrees per second.</p>
	 * <p>If a reduction gear is used, the gearRatio in the constructor ensures this speed is correct.</p>
	 * <p> The return speed when it is done a scan, and moving the sensor back to the forward position, is 5 times the scan speed. 
	 * 
	 * @param speed in degrees per second
	 */
	public void setScanSpeed(int speed) {
		this.speed = speed;
		motor_scan_speed = (int)(speed * gear_ratio); // speed of motor when scanning
		motor_return_speed = 5 * motor_scan_speed; // speed of motor when returning to 0
	}
	
	public int getScanSpeed() {
		return this.speed;
	}
	
	private void reset()
	{
        ls.setLaser(false);
		stop();
		motor.setSpeed(motor_return_speed);
		motor.rotateTo(baseTacho);
	}

	private void stop()
	{
		motor.stop();
	}

	/**
	 * The angle the motor is currently facing.
	 * @return
	 */
	public double getAngle()
	{
		return ((double)motor.getTachoCount() - baseTacho)/gear_ratio;
	}

	private void rotateTo(int angle)
	{
		motor.rotateTo((int)(angle*gear_ratio), true);
	}
	private void rotateTo(double angle)
	{
		motor.rotateTo((int)Math.round(angle*gear_ratio), true);
	}

    private int ping()
    {
        Delay.msDelay(10);
        int val1 = ls.readNormalizedValue();
        
        for(int i = 0; i < 10; i++)
        {
            int newVal = ls.readNormalizedValue();
            if (newVal > val1) val1 = newVal;
            Delay.msDelay(1);
        }
        ls.setLaser(true);
        Delay.msDelay(10);
        int val2 = ls.readNormalizedValue();

        for(int i = 0; i < 10; i++)
        {
            int newVal = ls.readNormalizedValue();
            if (newVal > val2) val2 = newVal;
            Delay.msDelay(1);
        }
        ls.setLaser(false);
        return val2 - val1;
        
    }
    
    private static final int LOW_THRESHOLD = 2;
	
    /**
     * <p>This method performs a scan around the robot. The angle values are always relative to the robot, because it does
     * not know which direction it is facing. 0 degrees is the forward direction the robot is facing. Angle increases 
     * counter-clockwise from 0. So 90 degrees is to the left of the robot, 180 is behind, and 270 is to the right.</p>
     * 
     *  <p>Note that the method physically rotates the laser scanner to 180 degrees to begin the scan, and then
     *  rotates a full 360 degrees. This means the order the scan takes place is not always sequentially ordered 
     *  from least to most, nor most to least.</p>
     *    
     * @return an ArrayList of double values indicating angles to the beacons 
     */
    public ArrayList<Double> locate()
	{
        beaconAngles.clear();
        double peakAngle = -360;
        int peakValue = Integer.MIN_VALUE;
        int lowCnt = 0;
        int peakTotal = 0;
        int peakCnt = 0;
        motor.setSpeed(motor_return_speed);
        rotateTo(-SCAN_ANGLE);
        while (motor.isMoving())
            Delay.msDelay(1);
        motor.setSpeed(motor_scan_speed);
        rotateTo(SCAN_ANGLE);
        int sval = 0;
        int svar = 16;
        int ssval = 0;
        int ssvar = 0;
        while(motor.isMoving())
        {
            int value = ping()*8;
            
            //RConsole.println(" " + getAngle()+ " " + (float)value/8 + " s " + (float)sval/8 + " v " + (float)svar/8);
            // detect peak values
            if (lowCnt > 0)
            {
                // We are in peak detection mode
                //if (value > sval + 2*svar)
                //{
                    // Do we have a genuine peak?
                    if (value > peakValue)
                    {
                        peakAngle = getAngle();
                        peakValue = value;
                    }
                if (value > ssval + 2*ssvar)
                {
                    lowCnt = LOW_THRESHOLD;
                    peakCnt++;
                    peakTotal += value;
                }
                //}
                else if (--lowCnt <= 0)
                {
                    // end of peak detection
                    boolean detected = (peakTotal > (ssval + 4*ssvar)*peakCnt) && peakCnt > 1;
                    //RConsole.println("***** end " + detected + " ang " + peakAngle + " average " + (float)peakTotal/peakCnt/8);
                    if (detected)
                    {
                        if (peakAngle < 0) peakAngle = peakAngle + 360;
                        beaconAngles.add(peakAngle);
                        svar = ssvar;
                        sval = ssval;
                        Sound.beep();
                    }
                    peakAngle = -360;
                    lowCnt = 0;
                }
            }
            else if (value > sval + svar*2)
            {
                // Start peak detection
                lowCnt = LOW_THRESHOLD;
                ssvar = svar;
                ssval = sval;
                peakValue = value;
                peakCnt = 1;
                peakAngle = getAngle();
                peakTotal = value;
                //RConsole.println("***** Start");
            }
            // Adjust background readings
            int delta = (value - sval)/8;
            sval += delta;
            if (delta < 0) delta = -delta;
            delta -= svar/8;
            svar += delta;
        }
        //if (peakAngle > -180)
       //     beaconAngles.add(peakAngle);
        motor.setSpeed(motor_return_speed);
        rotateTo(0);
        while (motor.isMoving())
            Delay.msDelay(1);
        
        return beaconAngles;
	}

    /**
     * This is a method to point the laser to an array of angles. It is a convenient helper method
     * to physically eye-check where the locate() method has pinpointed the beacons. The laser will
     * rotate to each laser, pause 2 seconds, and move on to the next one.
     * 
     * @param beacons An ArrayList of any size containing angles as double values
     */
    public void showBeacons(ArrayList<Double> beacons)
    {
        motor.setSpeed(motor_return_speed);
        rotateTo(-SCAN_ANGLE);
        while (motor.isMoving())
            Delay.msDelay(1);
        for(Double d : beacons)
        {
            if (d > 180) d -= 360;
            rotateTo(d);
            while (motor.isMoving())
                Delay.msDelay(1);
            ls.setLaser(true);
            Delay.msDelay(2000);
            ls.setLaser(false);
        }
        rotateTo(0);
        while (motor.isMoving())
            Delay.msDelay(1);
    }
    
    // TODO: This method is unused, can be deleted.
	private ArrayList<Double> locate1()
	{
        beaconAngles.clear();
        double peakAngle = -360;
        int peakValue = Integer.MIN_VALUE;
        int peakWidth = 0;
        int peakTotal = 0;;
        int peakCnt = 0;
        motor.setSpeed(motor_return_speed);
        rotateTo(-SCAN_ANGLE);
        while (motor.isMoving())
            Delay.msDelay(1);
        motor.setSpeed(motor_scan_speed);
        rotateTo(SCAN_ANGLE);
        int sval = 0;
        int svar = 16;
        while(motor.isMoving())
        {
            int value = ping()*8;
            // detect peak values
            if (peakWidth > 0)
            {
                peakCnt++;
                peakTotal += value;
                // We are in peak detection mode
                //if (value > sval + 2*svar)
                //{
                    // Do we have a genuine peak?
                    if (value > peakValue)
                    {
                        peakAngle = getAngle();
                        peakValue = value;
                    peakWidth = 1;
                    }
                if (value > sval + 2*svar)
                    peakWidth = 1;
                //}
                else if (++peakWidth > 2)
                {
                    // end of peak detection
                    boolean detected = (peakTotal > (sval + 4*svar)*peakCnt);
                    if (detected)
                        beaconAngles.add(peakAngle);
                    else
                        svar++;
                    peakAngle = -360;
                    peakWidth = 0;
                }
            }
            else if (value > sval + svar*2)
            {
                // Start peak detection
                peakWidth = 1;
                peakValue = value;
                peakCnt = 1;
                peakAngle = getAngle();
                peakTotal = value;
            }
            else
            {
                // Adjust background readings
                int delta = (value - sval)/8;
                sval += delta;
                if (delta < 0) delta = -delta;
                delta -= svar/8;
                svar += delta;
            }
        }
        if (peakAngle > -180)
            beaconAngles.add(peakAngle);
        motor.setSpeed(motor_return_speed);
        rotateTo(0);
        while (motor.isMoving())
            Delay.msDelay(1);
        
        return beaconAngles;
	}
}

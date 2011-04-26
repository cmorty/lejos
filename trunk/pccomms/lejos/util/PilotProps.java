package lejos.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import lejos.nxt.Motor;
import lejos.robotics.RegulatedMotor;

/**
 * Configuration class for Differential Pilot.
 * 
 * @author Lawrie Griffiths
 * 
 */
public class PilotProps extends Properties
{
	public static final String DEFAULT_FILENAME = "pilot.props";

	public static final String KEY_WHEELDIAMETER = "wheelDiameter";
	public static final String KEY_TRACKWIDTH = "trackWidth";
	public static final String KEY_LEFTMOTOR = "leftMotor";
	public static final String KEY_RIGHTMOTOR = "rightMotor";
	public static final String KEY_REVERSE = "reverse";

	/**
	 * Create a property file (pilot.props) for a DifferentialPilot
	 * 
	 * @param wheelDiameter the wheel diameter
	 * @param trackWidth the distance between the centers of the tyres
	 * @param leftMotor the left motor (A,B or C)
	 * @param rightMotor the right Motor (A,B or C)
	 * @param reverse true iff rhe Motors are connected so the motor forward
	 *            drives the robot backwards
	 * @throws IOException if there is a problem writing the property file
	 */
	public static void storeDefaultProperties(final float wheelDiameter, final float trackWidth,
			final String leftMotor, final String rightMotor, final boolean reverse) throws IOException
	{
		PilotProps p = new PilotProps();
		p.setProperty(KEY_WHEELDIAMETER, String.valueOf(wheelDiameter));
		p.setProperty(KEY_TRACKWIDTH, String.valueOf(trackWidth));
		p.setProperty(KEY_LEFTMOTOR, leftMotor);
		p.setProperty(KEY_RIGHTMOTOR, rightMotor);
		p.setProperty(KEY_REVERSE, String.valueOf(reverse));
		
		p.storeAsDefault();
	}

	/**
	 * Read the property file as a set of properties
	 * @throws IOException 
	 */
	public static PilotProps loadDefaultProperties() throws IOException
	{
		PilotProps p = new PilotProps();
		p.loadDefault();
		return p;
	}

	public void loadDefault() throws IOException
	{
		File f = new File(DEFAULT_FILENAME);
		if (!f.exists())
			return;
		
		FileInputStream fis = new FileInputStream(f);
		try
		{
			this.load(fis);
		}
		finally
		{
			fis.close();
		}
	}

	public void storeAsDefault() throws IOException
	{
		File f = new File(DEFAULT_FILENAME);
		FileOutputStream fos = new FileOutputStream(f);
		try
		{
			this.store(fos, null);
		}
		finally
		{
			fos.close();
		}
	}

	/**
	 * Utility method to get Motor instance from string (A, B or C)
	 */
	public static RegulatedMotor getMotor(String motor)
	{
		if (motor.equals("A"))
			return Motor.A;
		else if (motor.equals("B"))
			return Motor.B;
		else if (motor.equals("C"))
			return Motor.C;
		else
			return null;
	}
}

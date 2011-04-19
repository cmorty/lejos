package lejos.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Properties;
import lejos.nxt.Motor;
import lejos.robotics.RegulatedMotor;

/**
 * Configuration class for Differential Pilot.
 * 
 * @author Lawrie Griffiths
 *
 */
public class PilotProps extends Properties {
	  private static final String fileName = "pilot.props";
	 
	  /**
	   * Create a property file (pilot.props) for a DifferentialPilot
	   * 
	   * @param wheelDiameter the wheel diameter
	   * @param trackWidth the distance between the centers of the tyres
	   * @param leftMotor the left motor (A,B or C)
	   * @param rightMotor the right Motor (A,B or C)
	   * @param reverse true iff rhe Motors are connected so the motor forward drives the robot backwards
	   * @throws IOException if there is a problem writing the property file
	   */
	  public PilotProps(final float wheelDiameter, final float trackWidth,
	          final String leftMotor, final String rightMotor,
	          final boolean reverse) throws IOException {
		  
          File f = new File(fileName);
                 	  
          if(f.exists()) f.delete();         	
          f.createNewFile();
          
    	  FileOutputStream fos = new  FileOutputStream(f);
    	  PrintStream ps = new PrintStream(fos);
    	  
    	  ps.println("wheelDiameter=" + wheelDiameter);
    	  ps.println("trackWidth=" + trackWidth);
    	  ps.println("leftMotor=" + leftMotor);
    	  ps.println("rightMotor=" + rightMotor);
    	  ps.println("reverse=" + reverse);
    	  
    	  ps.close();
    	  fos.close(); 
	  }
	  
	  /**
	   * Read the property file as a set of properties
	   */
	  public PilotProps() {
		  File f = new File(fileName);
		  FileInputStream fis = null;

		  try {
			fis = new FileInputStream(f);
			load(fis);
		  } catch (IOException e) {} // silently ignore failure to read
	  }
	  
	  /**
	   * Utility method to get Motor instance from string (A, B or C)
	   */
	  public RegulatedMotor getMotor(String motor) {
		  if (motor.equals("A")) return Motor.A;
		  else if (motor.equals("B")) return Motor.B;
		  else if (motor.equals("C")) return Motor.C;
		  else return null;
	  }
}

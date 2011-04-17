package lejos.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Properties;

import lejos.nxt.Motor;
import lejos.robotics.RegulatedMotor;

public class PilotProps extends Properties {
	  private static final String fileName = "pilot.props";
	 
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
	  
	  public PilotProps() {
		  File f = new File(fileName);
		  FileInputStream fis = null;

		  try {
			fis = new FileInputStream(f);
			load(fis);
		  } catch (IOException e) {}
	  }
	  
	  public RegulatedMotor getMotor(String motor) {
		  if (motor.equals("A")) return Motor.A;
		  else if (motor.equals("B")) return Motor.B;
		  else if (motor.equals("C")) return Motor.C;
		  else return null;
	  }
}

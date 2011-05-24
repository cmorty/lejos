import java.io.PrintStream;
import java.util.Random;

import lejos.robotics.RegulatedMotor;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.util.KalmanFilter;
import lejos.util.Matrix;
import lejos.util.PilotProps;
import lejos.nxt.*;
import lejos.nxt.addon.CompassMindSensor;
import lejos.nxt.comm.RConsole;

/**
 * Kalman Filter example 2. 
 * 
 * A pilot robot with an ultrasonic sensor  and a compass sensor, both facing
 * forwards, moves backwards and forwards at right angles to a wall.
 * 
 * The state of the system is the distance from the wall and the heading of the robot.
 * 
 * The control data is the velocity, which is chosen randomly every second and the
 * rotation angle.
 * 
 * The measurement data is the ultrasonic sensor range reading to the wall and the 
 * compass reading.
 * 
 * The Kalman filter predicts the  robot position from its velocity and updates
 * the prediction from the range reading. It keeps the robot asa right angles to the
 * wall.
 * 
 * Put the robot about a meter from the wall and see the estimated mean displayed on
 * System.out. (You can modify the program to use RConsole to divert the System.out 
 * readings to the PC to make it easier to read).
 * 
 * You can run the PilotParams sample to create a property file which 
 * sets the parameters of the Pilot to the dimensions
 * and motor connections for your robot.
 * 
 * @author Lawrie Griffiths
 *
 */
public class KalmanTest2 {  
  public static void main(String[] args) throws Exception {
   	PilotProps pp = new PilotProps();
	pp.loadPersistentValues();
	float wheelDiameter = Float.parseFloat(pp.getProperty(PilotProps.KEY_WHEELDIAMETER, "5.6"));
	float trackWidth = Float.parseFloat(pp.getProperty(PilotProps.KEY_TRACKWIDTH, "16.0"));
	RegulatedMotor leftMotor = PilotProps.getMotor(pp.getProperty(PilotProps.KEY_LEFTMOTOR, "B"));
	RegulatedMotor rightMotor = PilotProps.getMotor(pp.getProperty(PilotProps.KEY_RIGHTMOTOR, "C"));
	boolean reverse = Boolean.parseBoolean(pp.getProperty(PilotProps.KEY_REVERSE,"false"));
		
    UltrasonicSensor sonic = new UltrasonicSensor(SensorPort.S1);
    CompassMindSensor compass = new CompassMindSensor(SensorPort.S2);
    Random rand = new Random();
    Matrix a = new Matrix(new double[][]{{1,0},{0,1}}); // Position is only changed by control
    Matrix b = new Matrix(new double[][]{{1,0},{0,1}}); // Velocity is in cm/sec
    Matrix c = new Matrix(new double[][]{{1,0},{0,1}}); // Measurement is in cm
    Matrix q = new Matrix(new double[][]{{4,0}, {0,4}}); // Ultrasonic sensor noise factor
    Matrix r = new Matrix(new double[][]{{9,1},{1,9}}); // Movement noise factor
    Matrix state = new Matrix(new double[][]{{100},{0}}); // Start one meter from the wall
    Matrix covariance = new Matrix(new double[][]{{100, 0},{0,1}}); // Big error
    Matrix control = new Matrix(2,1);
    Matrix measurement = new Matrix(2,1);
    float initialHeading = compass.getDegrees();
    float heading = initialHeading;
    
    sonic.continuous();
    
    //RConsole.openBluetooth(0);
    //System.setOut(new PrintStream(RConsole.getPrintStream()));
    
    System.out.println("Initial Heading:" + initialHeading);
    
    // Create the pilot
    DifferentialPilot pilot = new DifferentialPilot( 
        wheelDiameter,trackWidth,leftMotor,rightMotor,reverse);
    
    //Create the filter
    KalmanFilter filter = new KalmanFilter(a,b,c,q,r);
    
    // Set the initial heading
    state.set(1, 0, initialHeading);
    
    // Set the initial state  
    filter.setState(state, covariance);

    // Loop 100 times setting velocity, reading the range and updating the filter
    for(int i=0;i<100;i++) {
      // Generate a random velocity -20 to +20cm/sec
      double velocity = (rand.nextInt(41) - 20);
      
      // Adjust velocity so we keep in range
      double position = filter.getMean().get(0, 0);
      if (velocity < 0 && position < 20) velocity = -velocity;
      if (velocity > 0 && position > 220) velocity = -velocity;
      
      control.set(0, 0, velocity);
      System.out.println("Velocity: " + (int) velocity);

      // Move the robot
      pilot.setTravelSpeed((float) Math.abs(velocity));
      if (velocity > 0) pilot.backward();
      else pilot.forward();
      Thread.sleep(1000);
      pilot.stop();
      
      float angle = initialHeading - (float) filter.getMean().get(1, 0);
      control.set(1, 0, angle); // Set rotation angle
      System.out.println("Angle: " + angle);
      
      if (Math.abs(angle) > 1) pilot.rotate(angle);
      
      // Take readings
      float range = sonic.getRange();
      System.out.println("Range: " + (int) range);
      
      heading = compass.getDegrees();
      System.out.println("Heading: " + (int) heading);
      
      measurement.set(0,0, (double) range);
      measurement.set(1,0, (double) heading);
      
      // Update the state
      try {
        filter.update(control, measurement);
      } catch(Exception e) {
        System.out.println("Exception: " + e.getClass()+ ":" +  e.getMessage());
      }
      
      // Print the results
      System.out.print("Mean:");
      filter.getMean().print(System.out);;
      System.out.print("Covariance:");
      filter.getCovariance().print(System.out);      
    }
  }
}

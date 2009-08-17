import java.io.PrintStream;
import java.util.Random;
import lejos.robotics.proposal.DifferentialPilot;
import lejos.util.KalmanFilter;
import lejos.util.Matrix;
import lejos.nxt.*;
import lejos.nxt.comm.RConsole;

/**
 * Kalman Filter example. 
 * 
 * A pilot robot with an ultrasonic sensor facing
 * forwards moves backwards and forwards at right angles to a wall.
 * 
 * The state of the system is the distance from the wall.
 * 
 * The control data is the velocity, which is chosen randomly every second.
 * 
 * The measurement data is the ultrasonic sensor range reading to the wall.
 * 
 * The Kalman filter predicts the  robot position from its velocity and updates
 * the prediction from the range reading.
 * 
 * Put the robot about a meter from the wall and see the estimated mean displayed on
 * System.out. (You can modify the program to use RConsole to divert the System.out 
 * readings to the PC to make it easier to read).
 * 
 * @author Lawrie Griffiths
 *
 */
public class KalmanTest {  
  // Tyre diameter and distance between wheels 
  private static final float TYRE_DIAMETER = 5.6f;
  private static final float AXLE_TRACK = 16.0f;
  
  public static void main(String[] args) throws InterruptedException {
    UltrasonicSensor sonic = new UltrasonicSensor(SensorPort.S1);
    Random rand = new Random();
    Matrix a = new Matrix(new double[][]{{1}}); // Position is only changed by control
    Matrix b = new Matrix(new double[][]{{1}}); // Velocity is in cm/sec
    Matrix c = new Matrix(new double[][]{{1}}); // Measurement is in cm
    Matrix q = new Matrix(new double[][]{{4}}); // Ultrasonic sensor noise factor
    Matrix r = new Matrix(new double[][]{{9}}); // Movement noise factor
    Matrix state = new Matrix(new double[][]{{100}}); // Start one meter from the wall
    Matrix covariance = new Matrix(new double[][]{{100}}); // Big error
    Matrix control = new Matrix(1,1);
    Matrix measurement = new Matrix(1,1);
    
    //RConsole.openBluetooth(0);
    //System.setOut(new PrintStream(RConsole.openOutputStream()));
    
    // Create the pilot
    DifferentialPilot pilot = new DifferentialPilot( 
        TYRE_DIAMETER, AXLE_TRACK, Motor.B, Motor.C, true);
    
    //Create the filter
    KalmanFilter filter = new KalmanFilter(a,b,c,q,r);
    
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
      pilot.setMoveSpeed((float) Math.abs(velocity));
      if (velocity > 0) pilot.backward();
      else pilot.forward();
      Thread.sleep(1000);
      pilot.stop();
      
      // Take a reading
      float range = sonic.getRange();
      System.out.println("Range: " + (int) range);
      measurement.set(0,0, (double) range);
      
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

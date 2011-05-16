import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import lejos.nxt.*;
import lejos.robotics.localization.OdometryPoseProvider;
import lejos.robotics.navigation.ArcRotateMoveController;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.navigation.Pose;
import lejos.robotics.RegulatedMotor;
import lejos.util.PilotProps;
import lejos.geom.Point;
import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;

/**
 *Example of a navigating robot  operating under remote control
 * uses Command  enum  to decode incoming messages
 * 
 * You can run the PilotParams sample to create a property file which 
 * sets the parameters of the Pilot to the dimensions
 * and motor connections for your robot.
 * 
 * @author Roger Glassey
 */
public class RCNavigator
{

   public RCNavigator(final ArcRotateMoveController aPilot)
    {
      pilot = aPilot;
      poseProvider = new OdometryPoseProvider(pilot);
      pilot.setTravelSpeed(20);
      pilot.setRotateSpeed(180);
    }
   
/**
 * wheel diameter and track width in cm.
 * @param args
 * @throws IOException 
 */
   public static void main(String[] args) throws IOException
    {
    	PilotProps pp = new PilotProps();
    	pp.loadPersistentValues();
    	float wheelDiameter = Float.parseFloat(pp.getProperty(PilotProps.KEY_WHEELDIAMETER, "4.96"));
    	float trackWidth = Float.parseFloat(pp.getProperty(PilotProps.KEY_TRACKWIDTH, "13.0"));
    	RegulatedMotor leftMotor = PilotProps.getMotor(pp.getProperty(PilotProps.KEY_LEFTMOTOR, "B"));
    	RegulatedMotor rightMotor = PilotProps.getMotor(pp.getProperty(PilotProps.KEY_RIGHTMOTOR, "C"));
    	boolean reverse = Boolean.parseBoolean(pp.getProperty(PilotProps.KEY_REVERSE,"false"));
    	
      DifferentialPilot pilot = new DifferentialPilot(wheelDiameter, trackWidth, leftMotor, rightMotor, reverse);
      new RCNavigator(pilot).go();
    }
/**
 * decode incoming messages and issue commands 
 */
   private void readData()
    {
      int code;
      try
      {
         code = dataIn.readInt();
         Command command = Command.values()[code];
         LCD.clear();
         LCD.drawInt(code,0,1);
         Sound.playTone(800 + 100 * code, 200);
         if (command == Command.GOTO)// convert enum to int for comparison
         {
           pose = poseProvider.getPose();
            float x = dataIn.readFloat();
            float y = dataIn.readFloat();
            Point destination = new Point(x,y);
            float angle = pose.angleTo(destination);
            pilot.rotate(angle - pose.getHeading());
            pilot.travel(pose.distanceTo(destination));
         } else if (command == Command.TRAVEL)
         {
            float distance = dataIn.readFloat();
            LCD.drawString("D "+Math.round(distance),0 ,2);
            pilot.travel(distance);
         } else if (command == Command.ROTATE)
         {
            float angle = dataIn.readFloat();
            LCD.drawString("A "+ Math.round(angle),0,2);
            pilot.rotate(angle);
         }
         report();
         Sound.pause(100);
      } catch (IOException e)
      {
        System.out.println("Read exception "+e);
      }
    }
/**
 * report x,y and heading to mission control
 */
   public void report()
    {
      try
      {
        pose = poseProvider.getPose();
         dataOut.writeFloat(pose.getX());
         dataOut.writeFloat(pose.getY());
         dataOut.writeFloat(pose.getHeading());
         dataOut.flush();
         LCD.drawInt(Math.round(pose.getX()), 4,0,1);
         LCD.drawInt(Math.round(pose.getY()), 4,5,1);
         LCD.drawInt(Math.round(pose.getHeading()), 4,10,1);
      } catch (IOException e)
      {
      }
    }
/**
 * Estabish bluetooth connection to mission control
 */
   public void connect()
    {
      LCD.clear();
      LCD.drawString("Waiting", 0, 0);
      connection = Bluetooth.waitForConnection(); // this method is very patient. 
      LCD.clear();
      LCD.drawString("Connected", 0, 0);
      dataIn = connection.openDataInputStream();
      dataOut = connection.openDataOutputStream();
      Sound.beepSequence();
    }
/**
 * connect and wait for orders
 */
   private void go()
    {
      connect();
      while (true)readData();
    }
   
  private ArcRotateMoveController pilot;
  private OdometryPoseProvider poseProvider;
  private Pose pose = new Pose();
   BTConnection connection;
   DataInputStream dataIn;
   DataOutputStream dataOut;

   enum Command  // copied from GridNavControl project
{
  GOTO,TRAVEL,ROTATE;
}
}

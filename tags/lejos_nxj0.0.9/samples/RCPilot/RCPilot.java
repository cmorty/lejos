
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.OutputStream;
import lejos.nxt.comm.NXTInputStream;

import java.io.IOException;

import lejos.nxt.*;
import lejos.util.*;
import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.navigation.*;

/**
 * Enables remote control of a pilot object using Bluetooth. Communicates with
 * RemotePilotControl running on PC or other device
 * Receives commands from the controller and calls the corresponding methods
 * on the pilot; when the method returns, sends a reply to the controller.
 * 
 * You can run the PilotParams sample to create a property file which 
 * sets the parameters of the Pilot to the dimensions
 * and motor connections for your robot.
 * 
 * @author Roger Glassey
 */
public class RCPilot implements RemotePilotMethod
{

  /**
   * Constructor allocates the pilot object to be controlled
   * @param aPilot
   */
  public RCPilot(DifferentialPilot aPilot)
  {
    pilot = aPilot;
  }

  /**
 * connect and wait for orders from the controller
 */
  public  void go()
  {
    Sound.beepSequence();
    connect();
    while (true)
    {
      readData();
      executeCommand();
//      report();
    }
  }
/**
 * translates the received message and calls the corresponding pilot method
 */
  protected  void  executeCommand()
  {
  
    if(_code == RemotePilotMethod.FORWARD) pilot.forward();
    else if(_code == RemotePilotMethod.BACKWARD) pilot.backward();
    else if(_code == RemotePilotMethod.TRAVEL) pilot.travel(_param1,_immediate);
    else if(_code == RemotePilotMethod.ROTATE) pilot.rotate(_param1,_immediate);
    else if(_code == RemotePilotMethod.STEER) pilot.steer(_param1,_param2,_immediate);
    else if(_code == RemotePilotMethod.ARC) pilot.arc(_param1,_param2,_immediate);
    else if(_code == RemotePilotMethod.SETTRAVELSPEED )pilot.setTravelSpeed(_param1);
    else if(_code == RemotePilotMethod.SETROTATESPEED)pilot.setRotateSpeed(_param1);
    else if(_code == RemotePilotMethod.RESET) pilot.reset();
    else if(_code == RemotePilotMethod.STOP) pilot.stop();
     report();  // always send a response when the pilot method exits

  }

 /**
 * decodes incoming messages and stores in instance variables for use by executeCommand
 */
   protected void readData()
    {
      try
      {
         _code = dataIn.readInt();
         _param1 = dataIn.readFloat();
         _param2 = dataIn.readFloat();
         _immediate = dataIn.readBoolean();
 
      } catch (IOException e)
      {
      }
      if(_code!=RemotePilotMethod.REPORT)
        System.out.println("code "+_code + " "+_param1);
    }

/**
 * report pilot status to the controller.
 */
   protected  void report()
    {
      try
      {
         dataOut.writeBoolean(pilot.isMoving());
         dataOut.writeFloat(pilot.getMovementIncrement());
         dataOut.writeFloat(pilot.getAngleIncrement());
         dataOut.flush();
      } catch (IOException e)
      {
      }
    }
/**
 * Estabish bluetooth connection to mission control
 */
   protected void connect()
    {
     BTConnection connection;
     boolean fail = false;
      LCD.clear();
      LCD.drawString("Waiting", 0, 0);
      connection = Bluetooth.waitForConnection(); // this method is very patient.
      LCD.clear();
      try
      {
            byte [] hello = new byte[32];
            int len = connection.read(hello, hello.length);
            if (len != 3 || hello[0] != 'R' || hello[1] != 'C' || (hello[2] != 'P'))
            {
              fail = true;
                LCD.drawString("Console no h/s    ", 0, 0);
                connection.close();
                return;
            }
            else
            {
              os = connection.openOutputStream();
              os.write(hello);
              os.flush();
            }
      }
      		catch (Exception e)
		{
			LCD.drawString("connection error " + e.getMessage(), 0, 0);
		}
      if(!fail)
      {
      LCD.drawString("Connected", 0, 0);
      dataIn = connection.openDataInputStream();
      dataOut = connection.openDataOutputStream();
      Sound.beepSequence();
      }
    }
   
    /**
     * @param args the command line arguments
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException
    {
    	PilotProps pp = new PilotProps();
    	pp.loadPersistentValues();
    	float wheelDiameter = Float.parseFloat(pp.getProperty(PilotProps.KEY_WHEELDIAMETER, "2.2"));
    	float trackWidth = Float.parseFloat(pp.getProperty(PilotProps.KEY_TRACKWIDTH, "5.2"));
    	RegulatedMotor leftMotor = PilotProps.getMotor(pp.getProperty(PilotProps.KEY_LEFTMOTOR, "A"));
    	RegulatedMotor rightMotor = PilotProps.getMotor(pp.getProperty(PilotProps.KEY_RIGHTMOTOR, "C"));
    	boolean reverse = Boolean.parseBoolean(pp.getProperty(PilotProps.KEY_REVERSE,"false"));

      DifferentialPilot p = new DifferentialPilot(wheelDiameter, trackWidth, leftMotor, rightMotor, reverse);
      RCPilot pilot = new RCPilot(p);
      pilot.go();
    }
    
  protected DifferentialPilot pilot;
  protected DataInputStream dataIn;
  protected DataOutputStream dataOut;
  protected OutputStream os;
  protected int _code;  // set by readData() used by executeCommand
  protected float _param1; // set by readData() used by executeCommand
  protected float _param2;// set by readData() used by executeCommand
  protected boolean _immediate;// set by readData() used by executeCommand

}

package org.lejos.sample.rcnavmapper;

import java.io.IOException;

import lejos.geom.Point;
import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;
import lejos.nxt.UltrasonicSensor;
import lejos.robotics.RangeFinder;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.localization.OdometryPoseProvider;
import lejos.robotics.navigation.ArcRotateMoveController;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.robotics.navigation.Navigator;
import lejos.robotics.navigation.Pose;
import lejos.robotics.navigation.Waypoint;
import lejos.robotics.objectdetection.Feature;
import lejos.robotics.objectdetection.FeatureDetector;
import lejos.robotics.objectdetection.FeatureListener;
import lejos.robotics.objectdetection.RangeFeatureDetector;
import lejos.util.PilotProps;
import lejos.util.Stopwatch;

/**
 *Example of a navigating obstacle detecting  robot  operating under remote control   
 * It can accept additional destination coordinates as it is moving.
 * Reports its current pose twice per second as it move.  Stops when detecting an 
 * obstacle, and backs up. 
 * uses NXTCommunicator and  Command  enum  to decode incoming messages
 * @author Roger Glassey
 */
public class RCNavMapper  implements RCVehicle,   FeatureListener
{

  RCNavMapper (ArcRotateMoveController aPilot) 
  {
    
    pilot = aPilot;
    nav = new Navigator(pilot);
    pp = (OdometryPoseProvider) nav.getPoseProvider();
    RangeFinder rf = new UltrasonicSensor(SensorPort.S3);   
    detector = new RangeFeatureDetector(rf, 30, 100);
    detector.enableDetection(false);
    detector.addListener(this);
  }

/**
 * wheel diameter and track width in cm.
 * @param args
 */
   public static void main(String[] args) throws IOException, InterruptedException
    {
        PilotProps pp = new PilotProps();
    	pp.loadPersistentValues();
    	float wheelDiameter = Float.parseFloat(pp.getProperty(PilotProps.KEY_WHEELDIAMETER, "5.6"));
    	float trackWidth = Float.parseFloat(pp.getProperty(PilotProps.KEY_TRACKWIDTH, "14.3"));
    	RegulatedMotor leftMotor =  PilotProps.getMotor(pp.getProperty(PilotProps.KEY_LEFTMOTOR, "A"));
    	RegulatedMotor rightMotor = PilotProps.getMotor(pp.getProperty(PilotProps.KEY_RIGHTMOTOR, "C"));
    	boolean reverse = Boolean.parseBoolean(pp.getProperty(PilotProps.KEY_REVERSE,"false"));
    	
        DifferentialPilot pilot = new DifferentialPilot(wheelDiameter, trackWidth, leftMotor, rightMotor, reverse);
        
      RCNavMapper robot = new RCNavMapper(pilot);
      robot.go();   
    }
   
   public void go()
   {
      LCD.drawString("RC NAV ", 0, 5);
      pilot.setTravelSpeed(30);
      
   // TODO: This instanceof test will be unnecessary if setAcceleration() is added to ArcRotateMoveController
      if(pilot instanceof DifferentialPilot) 
    	  ((DifferentialPilot)pilot).setAcceleration(2000);
      System.out.println("connect");
      comm.connect();
      boolean more = true;
      while (more)
      {
         if (_report)
         {  // report every 500 ms while moving
//            System.out.println("REPORT");
            sw.reset();
            while ((nav.isMoving() || pilot.isMoving()))
            {
               if (sw.elapsed() >= 500)
               {
                  report(pp.getPose());
                  sw.reset();
               }
            }
            if (sw.elapsed() < 500)
               report(pp.getPose());
            _report = false;
         }
         more = !Button.ESCAPE.isDown();
         Thread.yield();
      }
      pilot.stop();
   }
  
/**
 * decode incoming messages and issue commands 
 */
   public  void execute(int code, float v0, float v1, float v2, boolean immediate)
    {
         NavCommand command = NavCommand.values()[code];  // convert int to enum     
         LCD.clear();
         LCD.drawString(command.toString(),0,0);
         Sound.playTone(800 + 100 * code, 200);;
         if(command == NavCommand.STOP)
         {
            pilot.stop();
            nav.stop();
            report(pp.getPose());
         }
         else if(command == NavCommand.GOTO ) 
         {
            nav.addWaypoint(new Waypoint(v0, v1));
            _report = true;
            detector.enableDetection(true);
         }
         else if(command == NavCommand.TRAVEL) 
         {
            float distance = v0;
            LCD.drawString("D " + Math.round(distance), 0, 2);
            pilot.travel(distance, true);
            _report = true;
            detector.enableDetection(true);
         }else if (command == NavCommand.ROTATE)
      {
         float angle = v0;
         pilot.rotate(angle,true);
         _report = true;
         LCD.drawString("A " + Math.round(angle), 0, 2);
      } else if (command == NavCommand.SETPOSE)
      {
         pp.setPose(new Pose(v0, v1, v2));
         report(pp.getPose());
      }
   }
    
/**
 * report x,y and heading to mission control
 */
   public void report(Pose pose)
    {
       int code = NavCommand.POSE.ordinal();
       comm.sendData( code,  pose.getX(),pose.getY(),pose.getHeading(),
               nav.isMoving());
System.out.println("Pose "+(int)pose.getX()+" "+(int)pose.getY()
             +" "+(int)pose.getHeading());
      }
   
   public void report(Point aPoint)
   {
      int code = NavCommand.OBSTACLE.ordinal();
      comm.sendData(code, aPoint.x, aPoint.y, 0, false);
   }
           
   public void featureDetected(Feature feature, FeatureDetector sonar )
   {
     pilot.stop();
     nav.stop();
     reportFeature(feature);    
     detector.enableDetection(false);  
     float backup =5+ detector.getMaxDistance() - feature.getRangeReading().getRange();
     pilot.travel(-backup);
     System.out.println("BAK "+backup);
  report(pp.getPose());
 }
   private void reportFeature(Feature feature)
   {
         float distance = feature.getRangeReading().getRange();
     float angle = feature.getRangeReading().getAngle();
     System.out.println("d, a "+(int)distance+" "+(int)angle);
     Pose pose = pp.getPose();
     System.out.println("Pose "+(int)pose.getX()+" "+(int)pose.getY()
             +" "+(int)pose.getHeading());
     Point obstacle = pose.pointAt(distance, angle+pose.getHeading());
      System.out.println("xy "+(int)obstacle.x+" "+(int)obstacle.y);
     report(obstacle);
   }
  

  NxtCommunicator comm = new NxtCommunicator(this);
  Navigator nav;
  ArcRotateMoveController pilot;
  OdometryPoseProvider pp ;
  RangeFeatureDetector detector;
  private boolean _report;
  Stopwatch sw = new Stopwatch(); 
}

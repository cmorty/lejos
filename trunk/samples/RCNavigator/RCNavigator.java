
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import lejos.nxt.*;
import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;
import lejos.robotics.navigation.*;

 

/**
 *Example of a navigating robot  operating under remote control
 * uses Command  enum  to decode incoming messages
 * @author Roger Glassey
 */
public class RCNavigator
{

   public RCNavigator(SimpleNavigator aNavigator)
    {
      navigator = aNavigator;
    }
/**
 * wheel diameter and track width in cm.
 * @param args
 */
   public static void main(String[] args)
    {
      Pilot p = new TachoPilot(5.6f, 14.3f, Motor.A, Motor.C);
      SimpleNavigator nav = new SimpleNavigator(p);
      new RCNavigator(nav).go();
    }
/**
 * decode incoming messages and issue commands to the SimpleNavigator
 */
   private void readData()
    {
      int code;
      try
      {
         code = dataIn.readInt();
         LCD.clear();
         LCD.drawInt(code,0,1);
         Sound.playTone(800 + 100 * code, 200);
         if (code == Command.GOTO.ordinal())// convert enum to int for comparison
         {
            float x = dataIn.readFloat();
            float y = dataIn.readFloat();
            navigator.goTo(x, y);
         } else if (code == Command.TRAVEL.ordinal())
         {
            float distance = dataIn.readFloat();
            LCD.drawString("D "+Math.round(distance),0 ,2);
            navigator.travel(distance);
         } else if (code == Command.ROTATE.ordinal())
         {
            float angle = dataIn.readFloat();
            LCD.drawString("A "+ Math.round(angle),0,2);
            navigator.rotate(angle);
         }
         report();
         Sound.pause(100);
      } catch (IOException e)
      {
      }
    }
/**
 * report x,y and heading to mission control
 */
   public void report()
    {
      try
      {
         dataOut.writeFloat(navigator.getX());
         dataOut.writeFloat(navigator.getY());
         dataOut.writeFloat(navigator.getAngle());
         dataOut.flush();
         LCD.drawInt(Math.round(navigator.getX()), 4,0,1);
         LCD.drawInt(Math.round(navigator.getY()), 4,5,1);
         LCD.drawInt(Math.round(navigator.getAngle()), 4,10,1);
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
   
   SimpleNavigator navigator;
   BTConnection connection;
   DataInputStream dataIn;
   DataOutputStream dataOut;

   enum Command  // copied from GridNavControl project
{
  GOTO,TRAVEL,ROTATE;
}
}

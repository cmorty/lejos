
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import lejos.nxt.*;
import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;
import lejos.navigation.*;



/**
 *Example of a navigating robot  operating under remote control
 * @author Roger Glassey
 */
public class RCNavigator
{

   public RCNavigator(TachoNavigator aNavigator)
    {
      navigator = aNavigator;
    }
/**
 * wheel diameter and track width in cm.
 * @param args
 */
   public static void main(String[] args)
    {
      Pilot p = new Pilot(5.6f, 14.3f, Motor.A, Motor.C);
      TachoNavigator nav = new TachoNavigator(p);
      new RCNavigator(nav).go();
    }
/**
 * decode incoming messages and issue commands to the tachoNavigator
 */
   private void readData()
    {
      int code;
      try
      {
         code = dataIn.readInt();
         LCD.clear();
         LCD.drawInt(code,0,1);
         LCD.refresh();
         Sound.playTone(800 + 100 * code, 200);
         if (code == 0)
         {
            float x = dataIn.readFloat();
            float y = dataIn.readFloat();
            LCD.drawInt((int)x,4,6,1);
            LCD.drawInt((int)y,4,10,1);
            navigator.goTo(x, y);
            report();
         } else if (code == 1)
         {
            float distance = dataIn.readFloat();
            LCD.drawInt((int)distance,4,6,1);
            navigator.travel(distance);
            report();
         } else if (code == 2)
         {
            float angle = dataIn.readFloat();
                    LCD.drawInt((int)angle,4,6,1);
            navigator.rotate(angle);
            report();
         }
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
   
   TachoNavigator navigator;
   BTConnection connection;
   DataInputStream dataIn;
   DataOutputStream dataOut;
}

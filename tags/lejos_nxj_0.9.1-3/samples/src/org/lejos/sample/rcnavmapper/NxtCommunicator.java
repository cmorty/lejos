package org.lejos.sample.rcnavmapper;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import lejos.nxt.LCD;
import lejos.nxt.Sound;
import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;



/**
 * Communications link with the PC.  can be used by any remotely controlled vehicle
 * that implements the RCVechicle interface
 * @author glassey
 */
public class NxtCommunicator
{

  /**
   * sets callback path to the vehicle that this object  works with
   * @param theVehicle
   */
 public NxtCommunicator(RCVehicle theVehicle)
  {
   this.vehicle = theVehicle;
 }
  public void setVehicle(RCVehicle  theVehicle )
  {
    vehicle = theVehicle;
  }

  /**
   * waits for BlueTooth connection from PC;  opens streams
   */
  public void connect()
 {
      Sound.playTone(1600,300);
      LCD.clear();
      LCD.drawString("waiting",0,0);
      BTConnection btc = Bluetooth.waitForConnection(); // this method is very patient. 
      LCD.clear();
      LCD.drawString("connected",0,0);
      try 
      {
         dataIn = btc.openDataInputStream();
         dataOut = btc.openDataOutputStream();
      } catch(Exception e) {};
      Sound.beepSequence();
      reader.start();
   }
 
/**
 * sends data to PC
 * @param code
 * @param v0
 * @param v1
 * @param v2
 * @param bit
 */
   public void sendData(int code, float v0, float v1, float v2, boolean bit)
   {
      try 
      {
         dataOut.writeInt(code);     
         dataOut.writeFloat(v0);
         dataOut.writeFloat(v1);
         dataOut.writeFloat(v2);
         dataOut.writeBoolean(bit);
         dataOut.flush();
      }catch (IOException e) { System.out.println("Send failure");}
   }

    /**
 * reads incoming message and uses it as parameters in vehicle.execute()
 */
   protected void readData()
    {
      try
      {
         vehicle.execute(dataIn.readInt(),
                 dataIn.readFloat(),
                 dataIn.readFloat(),
                 dataIn.readFloat(),
                 dataIn.readBoolean());
      } catch (IOException e)
      {
         System.out.println("Read failure");
      }
    }

   class Reader extends Thread
   {

      boolean isRunning = false;

      public void run()
      {
         isRunning = true;
         while (isRunning)
         {
            readData();
            Thread.yield();
         }
      }
   }
   RCVehicle vehicle;
   DataInputStream dataIn;
   DataOutputStream dataOut;
   Reader reader = new Reader();
}

package org.lejos.pcsample.navmapcontrol;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTConnector;

/**
 * Provides  Bluetooth communications services to RCNavitationControl:<br>
 * 1. connect to NXT
 * 2. send commands  using the Command emum
 * 3. receives robot position
 * @author Roger
 */
public class RCCommunicator
{

  /**
   * constructor establishes  call back path of the RCNavigationControl
   * @param control
   */
  public RCCommunicator(RemoteController control)
  {
    this.control = control;
    System.out.println(" RC Communicator constructed");
  }

  /**
   * connects to NXT using Bluetooth
   * @param name of NXT
   * @param address  bluetooth address
   */
  public boolean connect(String name, String address)
  {
    System.out.println(" connecting to " + name + " " + address);
    connector = new NXTConnector();
    boolean connected = connector.connectTo(name, address, NXTCommFactory.BLUETOOTH);
    System.out.println(" connect result " + connected);
    if (!connected)
    {
      return connected;
    }
    dataIn = new DataInputStream(connector.getInputStream());
    dataOut = new DataOutputStream(connector.getOutputStream());
    if (dataIn == null)
    {
      connected = false;
      return connected;
    }
    if (!reader.isRunning)
    {
      reader.start();
    }
    return connected;
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
      System.out.println("sent "+NavCommand.values()[code]+" "+v0+" "+v1);
    } catch (IOException e)
    {
      System.out.println(" send failed ");
    }
  }
  public void sendData(int code, int x, int y)
  {
      try
    {
      dataOut.writeInt(code);
      dataOut.writeInt(x);
      dataOut.writeInt(y);
      dataOut.flush();
    } catch (IOException e)
    {
      System.out.println(" send failed ");
    }
     
  }

  /**
   * inner class to monitor for an incoming message after a command has been sent <br>
   * calls showRobotPosition() on the controller
   */
  class Reader extends Thread
  {

    boolean isRunning = false;

    public void run()
    {
      isRunning = true;
      while (isRunning)
      {
        int code = 0;
        float v0 = 0, v1 = 0, v2 = 0;
        boolean bit = false;
        boolean ok = false;
        System.out.println("reading ");
        try
        {
          code = dataIn.readInt();
          v0 = dataIn.readFloat();
          v1 = dataIn.readFloat();
          v2 = dataIn.readFloat();
          bit = dataIn.readBoolean();
          ok = true;
//          System.out.println("data  " + v0 + " " + v1 + " " + v2);
        } catch (IOException e)
        {
          System.out.println("connection lost");
          ok = false;
        }
        if (ok)
        {
          control.execute(code, v0, v1, v2, bit);
        }
        try
        {
          Thread.sleep(50);
        } catch (InterruptedException ex)
        {
          Logger.getLogger(RCCommunicator.class.getName()).log(Level.SEVERE, null, ex);
        }
      }
    }// if reading
  }//while is running
  /**
   * used by reader
   */
  private DataInputStream dataIn;
  /**
   * used by send()
   */
  private DataOutputStream dataOut;
  private Reader reader = new Reader();
  private NXTConnector connector;
  private RemoteController control;
}

package lejos.util;


import lejos.nxt.comm.*;
import lejos.nxt.*;

import java.io.*;

/**
 * Datalogger class; stores float values then  then transmits  via bluetooth or usb<br>
 * works with DataViewer   in pctools.
 * Default size is 512.  Capacity is limited only by the available ram.
 * @author  Roger Glassey   - revised 1/12/08 for large arrays 
 */
public class Datalogger
{
  // overcome limitation of array size; created as needed

  private float[] log;
  private int _indx = 0;  //where the data will be written
  private int _size;

  /**
   * buld a Datalogger with default size  512
   */
  public Datalogger()
  {
    this(512);
  }

  /**
   * build a new Datalogger with capacity  = size;
   * @param size the capacity of the Datalogger
   */
  public Datalogger(int size)
  {
    _size = size;
    log = new float[size];
  }

  /**
   * write a float  value to the log
   * @param v
   */
  public void writeLog(float v)
  {
    if (_indx < _size)
    {
      log[_indx] = v;
      _indx++;
    }
  }

  /**
   * Clears the log; next write is at the beginning;
   *
   */
  public void reset()
  {
    _indx = 0;
  }

  /**
   * transmit the stored values to the PC via USB or bluetooth;<br>
   * Displays " ESC for BT". Press the escape key to use BlueTooth; any other to use USB. <br>
   * Then displays "wait for BT" or "wait for USB".  In DataViewer, click on "StartDownload"
   * When finished, displays the number values sent, and asks "Resend?".
   * Press ESC to exit the program, any other key to resend.  <br>Then start the download in
   * DataViewer.
   */
  public void transmit()
  {
    NXTConnection connection = null;
    DataOutputStream dataOut = null;
    InputStream is = null;
    String[] items ={" USB", " Bluetooth"};
    TextMenu tm = new TextMenu(items, 2, "Transmit using");
    int s = tm.select();
    LCD.clear();
    if (s == 0)
    {
      LCD.drawString("wait for USB", 0, 0);
      connection = USB.waitForConnection();

    } else
    {
      LCD.drawString("wait for BT", 0, 0);
      connection = Bluetooth.waitForConnection();
    }
    {
      try
      {
        is = connection.openInputStream();
        dataOut = connection.openDataOutputStream();
      } catch (Exception ie)
      {
      }
    }
    LCD.drawString("connected", 0, 1);
    boolean more = true;
    while (more)
    {
      try
      {
        LCD.clear();
        LCD.drawString("Wait for Viewer", 0, 0);
        int b = 0;
        b = is.read();
        LCD.drawInt(b, 8, 1);
      } catch (IOException ie)
      {
        LCD.drawString("no connection", 0, 0);
      }

      LCD.clear();
      LCD.drawString("sending ", 0, 0);
      LCD.drawInt(_indx,4, 8, 0);;
      try
      {
        dataOut.writeInt(_indx);
        dataOut.flush();
        for (int i = 0; i < _indx; i++)
        {
          dataOut.writeFloat(log[i]);
        }
        dataOut.flush();
        dataOut.close();
      } catch (IOException e)
      {
        LCD.drawString("write error", 0, 0);
        LCD.refresh();
      }
      LCD.clear();
      Sound.beepSequence();
      LCD.drawString("Sent " + _indx, 0, 0);
      tm.setTitle("Resend?         ");
      String[] itms ={"Yes", "No"};
      tm.setItems(itms);
      more = 0 == tm.select();
    }
    try
    {
      dataOut.close();
    } catch (IOException e)
    {
    }
  }

  /**
   * obsolete - older version API.  calls transmit();
   * @param useUSB
   */
  public void transmit(boolean useUSB)
  {
    transmit();
  }
}

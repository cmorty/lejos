         
package lejos.util;
import javax.microedition.io.StreamConnection;
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
   private float [] log;  

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
     if(_indx<_size)
     {
        log[_indx]= v;
        _indx ++ ;
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
  * Displays "waiting" , so then start the DataViewer. 
  * When finished, displays the number values sent, and asks "Resend?". 
  * Press ENTER for yes, ESC to exit the program.
  * @param useUSB  if false, uses Bluetooth
  *
  */
    public  void transmit(boolean useUSB)
    {
       StreamConnection connection= null;
       DataOutputStream dataOut = null;
       InputStream is = null;
       boolean more = true;
       while(more)
       {
          LCD.clear();
          LCD.drawInt(_indx, 0, 0);
          LCD.drawString("waiting",8,0);
          LCD.refresh();  
          if(useUSB)
          {
             connection = USB.waitForConnection();
          }
          else 
          {
             connection = Bluetooth.waitForConnection();
          }
          try 
             { 
                dataOut=  connection.openDataOutputStream(); 
                is = connection.openInputStream(); 
                LCD.drawString("connected", 0, 1);
                LCD.refresh();
                int b = 0; 
                b = is.read();
                LCD.drawInt(b, 8, 1);
                LCD.refresh();
             }
             catch(IOException ie){LCD.drawString("no connection",0,0); LCD.refresh();}
             LCD.clear();
             LCD.drawString("sending",0,0);
             LCD.drawInt(_indx, 12, 0);
             LCD.refresh();
             try
             {
               dataOut.writeFloat(_indx);
               dataOut.flush();
               for (int i = 0; i<_indx ; i++)   dataOut.writeFloat(log[i]);  
               dataOut.flush();
               dataOut.close();
          }
          catch(IOException e)  {LCD.drawString("write error",0,0); LCD.refresh();}
          Sound.beepSequence();
          LCD.clear();
          LCD.drawString("sent",0,0);
          LCD.drawInt(_indx, 8, 0);
          LCD.drawString("Resend?", 0, 1);
          LCD.refresh();
          more = Button.waitForPress()==1;
       }
       try{dataOut.close();}catch(IOException e){}
    }
}

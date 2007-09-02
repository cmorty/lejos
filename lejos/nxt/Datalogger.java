         
package lejos.nxt;
import javax.microedition.io.StreamConnection;
import lejos.nxt.comm.*;

import java.io.*;
/**
 * Datalogger class; stores float values then  then transmits  via bluetooth or usb<br>
 * works with DataViewer   in pctools.
 * A maximum of 2000 data values can be stored. 
 */
public class Datalogger 
{
   // overcome limitation of array size; created as needed
   private float [] log0;  
   private float [] log1;
   private float [] log2;
   private float [] log3;
   private int _indx = 0;  //where the data will be witten

   private final int  BLOCK = 510; // block size
   private int _blocks = 1; 



    public Datalogger()
    {
      log0 = new float[BLOCK];
     
    }
/**
 * write a float  value to the log
 * @param v
 */
  public void writeLog(float v)
  {   
     if(_indx>=_blocks * BLOCK)
     {
        if(_blocks == 1 && log1 == null)log1 = new float[BLOCK];
        else if(_blocks == 2 && log2 == null)log2 = new float[BLOCK];
        else if(_blocks == 3 && log3 == null)log3 = new float [BLOCK];
        else if (_blocks == 4) return;
        _blocks ++;
     }
    if(_blocks == 1) log0[_indx]= v;
    else  if(_blocks == 2)log1[_indx %BLOCK]= v;
    else  if(_blocks == 3)log2[_indx %BLOCK]= v;
    else  if(_blocks == 4)log3[_indx %BLOCK]= v;
    else return;
    _indx++;
  }
  
  /**
   * Clears the log; next write is at the beginning;
   *
   */
  public void reset()
  {
     _indx = 0;
     _blocks = 1;
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
             connection = new USBConnection();
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
               for (int i = 0; i<_indx ; i++) 
               {        
                  if(i<BLOCK)dataOut.writeFloat(log0[i]);   
                  else if(i<2*BLOCK)dataOut.writeFloat(log1[i%BLOCK]);
                  else if(i<3*BLOCK)dataOut.writeFloat(log2[i%BLOCK]);
                  else if(i<3*BLOCK)dataOut.writeFloat(log3[i%BLOCK]);
                  try{Thread.sleep(4);} catch (InterruptedException e ){}
               }
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

       
      
          
  /**
   * to test the DL object
   */  
    public static void main(String[] args)
    {
       int size =600;
       Datalogger dl = new Datalogger();
       boolean more = true;
       while(more)
       {
       for(int i = 0 ; i<size; i++)
          {
             float x = i*0.5f;
             dl.writeLog(x);
          }
       dl.transmit(true); 
       dl.reset();
       LCD.clear();
       LCD.drawString("more?",0,2);
       LCD.refresh();
       more = 1 == Button.waitForPress();
       }
    }
}

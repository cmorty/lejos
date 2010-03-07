import lejos.nxt.*;
import lejos.util.*;

/**
 * Test of the DataLogger class. Logs 600 floating point 
 * numbers and then transmits them to the PC.
 * 
 * Run lejos.pc.tools.DataViewer on the PC to view the logged data.
 * 
 * Once the program has transmitted data to the PC, it waits
 * for a button press. Press ENTER to log and transmit more data,
 * or ESCAPE to exit the program.
 * 
 * The sample is set up to transmit over Bluetooth. To use
 * USB, set the parameter to transmit to true;
 * 
 * @author Roger Glassey and Lawrie Griffiths
 *
 */
public class DLTest
{
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
         dl.transmit(); 
         LCD.clear();
         LCD.drawString("more?",0,2);
         LCD.refresh();
         more = 1 == Button.waitForPress();
      }
   }
}


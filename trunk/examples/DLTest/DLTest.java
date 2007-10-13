
import lejos.nxt.*;


public class DLTest
{

   /**
    * @param args
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
         dl.transmit(false); 
         dl.reset();
         LCD.clear();
         LCD.drawString("more?",0,2);
         LCD.refresh();
         more = 1 == Button.waitForPress();
      }
   }
 

}


import java.lang.System;
import lejos.nxt.*;

/**
 * This program tests the performance with and without Listeners,
 * speed as well as memory efficiency.
 * Press Run repeatedly to obtain 8 values
 * (the number of the value is shown in the program number):
 *
 * 1/3/5/7: time in ms for empty for loop of 10000 iterations
 * 2/4/6/8: free memory
 *
 * 1/2: no listeners
 * 3/4: one button listener
 * 5/6: two button listeners
 * 7/8: two button listeners and a sensor listener
 *
 *
 * Note that this program is similar to the leJOS RCX version
 * and can be used to compare the performance of leJOS on the
 * NXT with leJOS on the RCX.
 * 
 **/

public class PerformanceTest
{
  public static void main (String[] arg)
    throws InterruptedException
  {
    long t0;
    long t1;
    
    //showProgramNumber( 0);
    //showNumber( (int)(Runtime.getRuntime().freeMemory())-10000);
    //Button.ENTER.waitForPressAndRelease();
    
    t0 = System.currentTimeMillis();
    for( int i=0; i<10000; i++){};
    t1 = System.currentTimeMillis();

    showProgramNumber( 1);
    showNumber( (int)t1-(int)t0);
    Button.ENTER.waitForPressAndRelease();

    showProgramNumber( 2);
    showNumber( (int)(Runtime.getRuntime().freeMemory())-10000);
    Button.ENTER.waitForPressAndRelease();

    Button.LEFT.addButtonListener( new ButtonListener() {
        private int count = 0;

        public void buttonPressed( Button button) {
          count++;
          showProgramNumber( count);
        }
        public void buttonReleased( Button button) {
        }
      }
    );
    t0 = System.currentTimeMillis();
    for( int i=0; i<10000; i++){};
    t1 = System.currentTimeMillis();

    showProgramNumber( 3);
    showNumber( (int)t1-(int)t0);
    Button.ENTER.waitForPressAndRelease();

    showProgramNumber( 4);
    showNumber( (int)(Runtime.getRuntime().freeMemory())-10000);
    Button.ENTER.waitForPressAndRelease();

    Button.RIGHT.addButtonListener( new ButtonListener() {
        public void buttonPressed( Button button) {
        }
        public void buttonReleased( Button button) {
        }
      }
    );
    t0 = System.currentTimeMillis();
    for( int i=0; i<10000; i++){};
    t1 = System.currentTimeMillis();

    showProgramNumber( 5);
    showNumber( (int)t1-(int)t0);
    Button.ENTER.waitForPressAndRelease();

    showProgramNumber( 6);
    showNumber( (int)(Runtime.getRuntime().freeMemory())-10000);
    Button.ENTER.waitForPressAndRelease();

    SensorPort.S1.addSensorPortListener( new SensorPortListener() {
        public void stateChanged( SensorPort port, int oldValue, int newValue) {
        }
      }
    );
    t0 = System.currentTimeMillis();
    for( int i=0; i<10000; i++){};
    t1 = System.currentTimeMillis();

    showProgramNumber( 7);
    showNumber( (int)t1-(int)t0);
    Button.ENTER.waitForPressAndRelease();

    showProgramNumber( 8);
    showNumber( (int)(Runtime.getRuntime().freeMemory())-10000);
    Button.ENTER.waitForPressAndRelease();

  }
  
  static void showProgramNumber(int p)
  {
	  LCD.clear();
	  LCD.drawInt(p,0,0);
	  LCD.refresh();
  }
  
  static void showNumber(int n)
  {
	  LCD.drawInt(n,2,0);
	  LCD.refresh();
  }
}

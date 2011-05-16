import lejos.nxt.*;
import lejos.util.Delay;
import java.util.Random;
import java.io.IOException;
import lejos.robotics.Clock;
import lejos.nxt.addon.RealTimeClock;

public class RTCTest {

	/**
	 * Test of RealTimeClock class
	 */
	public static void main(String[] args) {
		UpDown[] dateVals = new UpDown[7];
		byte[] ramTest = new byte[56];
		Random r = new Random();
		int i;
		lejos.robotics.Clock clock = new RealTimeClock(SensorPort.S1);
		//Clock clock = new DummyClock();
		int cur = 0;

		try {
			clock.setHourMode(true);
		} catch (IOException e) {			
		}
		
		// Display date and time components
		try {
			dateVals[0] = new UpDown(0,0,clock.getMonth(),1,12,2);
			dateVals[1] = new UpDown(3,0,clock.getDay(),1,31,2);
			dateVals[2] = new UpDown(6,0,clock.getYear(),2000,2099,4);
			dateVals[3] = new UpDown(11,0,0,0,1,1);			
			dateVals[4] = new UpDown(0,2,clock.getHour(),0,23,2);
			dateVals[5] = new UpDown(3,2,clock.getMinute(),0,59,2);
			dateVals[6] = new UpDown(6,2,clock.getSecond(),0,59,2);
		} catch (IOException e) {
			  LCD.clear();
			  LCD.drawString("I/O Exception",0,0);
			  Button.ESCAPE.waitForPressAndRelease();
			  return;
		}

		// Set the month active
		dateVals[0].setActive(true);

		// RAM test
		for (i=0;i<56;i++)
			ramTest[i] = (byte)(r.nextInt(256));

		try {
			for (i=0;i<56;i++)
				clock.setByte(i+8, ramTest[i]);
				
			for (i=0;i<56;i++)
				if (clock.getByte(i+8) != ramTest[i])
					break;

		} catch (IOException e) {
			  LCD.clear();
			  LCD.drawString("I/O Exception RAM",0,0);
			  Button.ESCAPE.waitForPressAndRelease();
			  return;
		}

		if (i == 56)
			LCD.drawString("RAM Pass", 0, 7);
		else
			LCD.drawString("RAM Fail", 0, 7);

		// Incrementing 4th field terminates test
		while (dateVals[3].getVal() == 0) {
			// Show the current date and time
			try {
				LCD.drawString(clock.getDateString(), 0, 4);
				LCD.drawString(""+clock.getDayOfWeek()+" "+clock.getAMPM(), 0, 5);
				LCD.drawString(clock.getTimeString(), 0, 6);
			} catch (IOException e) {
				  LCD.clear();
				  LCD.drawString("I/O Exception",0,0);
				  Button.ESCAPE.waitForPressAndRelease();
				  return;
			}
			
			// Decrement the current component
			if (Button.LEFT.isPressed()) {
				dateVals[cur].decrement();
				while (Button.LEFT.isPressed()) Delay.msDelay(100);
			}

			// Increment the current component
			if (Button.RIGHT.isPressed()) {
				dateVals[cur].increment();				
				while (Button.RIGHT.isPressed()) Delay.msDelay(100);
			}

			// Move to next component
			if (Button.ENTER.isPressed()) {
				dateVals[cur].setActive(false);
				cur = (cur + 1) % 7;
				dateVals[cur].setActive(true);
				while (Button.ENTER.isPressed()) Delay.msDelay(100);
			}

			// Set the date and time
			if (Button.ESCAPE.isPressed()) {
				dateVals[cur].setActive(false);
				cur = 0;
				dateVals[cur].setActive(true);

				try {
					clock.setDate(dateVals[0].getVal(),
								  dateVals[1].getVal(),
								  dateVals[2].getVal());

					clock.setTime(dateVals[4].getVal(),
								  dateVals[5].getVal(),
								  dateVals[6].getVal());
				} catch (IOException e) {
					  LCD.clear();
					  LCD.drawString("I/O Exception",0,0);
					  Button.ESCAPE.waitForPressAndRelease();

					  return;
				} catch (IllegalArgumentException e) {
					  LCD.clear();
					  LCD.drawString("Illegal Argument",0,0);
					  Button.ESCAPE.waitForPressAndRelease();
					  return;
				}
				Button.ESCAPE.waitForPressAndRelease();
			}
			Delay.msDelay(100);
		}
	}
}


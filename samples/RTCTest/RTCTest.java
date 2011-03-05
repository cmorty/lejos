import lejos.nxt.*;
import lejos.util.Delay;
import java.util.Random;
import java.io.IOException;
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
		RealTimeClock clock = new RealTimeClock(SensorPort.S1);
		int cur = 0;

		try {
			clock.setHourMode(true);
		} catch (IOException e) {			
		}

		LCD.clear();

		while (Button.ENTER.isPressed())
			Delay.msDelay(100);
		
		try {
			dateVals[0] = new UpDown(0,0,clock.getMonth(),1,12,2);
			dateVals[1] = new UpDown(3*LCD.CELL_WIDTH,0,clock.getDay(),1,31,2);
			dateVals[2] = new UpDown(6*LCD.CELL_WIDTH,0,clock.getYear(),2000,2099,4);
			dateVals[3] = new UpDown(11*LCD.CELL_WIDTH,0,0,0,1,1);			
			dateVals[4] = new UpDown(0,2*LCD.CELL_HEIGHT,clock.getHour(),0,23,2);
			dateVals[5] = new UpDown(3*LCD.CELL_WIDTH,2*LCD.CELL_HEIGHT,clock.getMinute(),0,59,2);
			dateVals[6] = new UpDown(6*LCD.CELL_WIDTH,2*LCD.CELL_HEIGHT,clock.getSecond(),0,59,2);
		} catch (IOException e) {
			  LCD.clear();
			  LCD.drawString("I/O Exception",0,0);
			  while (!Button.LEFT.isPressed())
				  Delay.msDelay(100);
			  return;
		}

		dateVals[0].setActive(true);

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
			  while (!Button.LEFT.isPressed())
				  Delay.msDelay(100);
			  return;
		}

		if (i == 56)
			LCD.drawString("RAM Pass", 0, 7);
		else
			LCD.drawString("RAM Fail", 0, 7);

		while (dateVals[3].getVal() == 0) {
			try {
				LCD.drawString(clock.getDateString(), 0, 4);
				LCD.drawString(""+clock.getDayOfWeek()+" "+clock.getAMPM(), 0, 5);
				LCD.drawString(clock.getTimeString(), 0, 6);
				//clock.setYear(0);

			} catch (IOException e) {
				  LCD.clear();
				  LCD.drawString("I/O Exception",0,0);
				  while (!Button.LEFT.isPressed())
					  Delay.msDelay(100);
				  return;
			}
				
			if (Button.LEFT.isPressed()) {
				dateVals[cur].decrement();
				while (Button.LEFT.isPressed())
					Delay.msDelay(100);
			}

			if (Button.RIGHT.isPressed()) {
				dateVals[cur].increment();				
				while (Button.RIGHT.isPressed())
					Delay.msDelay(100);
			}

			if (Button.ENTER.isPressed()) {
				dateVals[cur].setActive(false);
				cur = (cur + 1) % 7;
				dateVals[cur].setActive(true);
				while (Button.ENTER.isPressed())
					Delay.msDelay(100);
			}

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
					  while (!Button.LEFT.isPressed())
						  Delay.msDelay(100);

					  return;
					} catch (IllegalArgumentException e) {
						  LCD.clear();
						  LCD.drawString("Illegal Argument",0,0);
						  while (!Button.LEFT.isPressed())
							  Delay.msDelay(100);
						  return;
					}

				while (Button.ESCAPE.isPressed())
					Delay.msDelay(100);
			}
			Delay.msDelay(100);
		}
	}
}


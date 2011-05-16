import java.io.IOException;
import lejos.robotics.Clock;

public class DummyClock implements Clock {
	private int year = 2000, month = 1, day =1, hour=0, minute=0, second = 0, dayOfWeek = 0;
	boolean mode24 = false;
	boolean isPM = false;

	public int getYear() throws IOException {
		return year;
	}

	public int getMonth() throws IOException {
		return month;
	}

	public int getDay() throws IOException {
		return day;
	}

	public int getHour() throws IOException {
		return hour;
	}

	public int getMinute() throws IOException {
		return minute;
	}

	public int getSecond() throws IOException {
		return second;
	}

	public int getDayOfWeek() throws IOException {
		return dayOfWeek;
	}

	public void setHourMode(boolean mode) throws IOException {
		mode24 = mode;
	}

	public String getDateString() throws IOException {
		return year + "/" + month + "/" + day;
	}

	public String getTimeString() throws IOException {
		return hour + ":" + minute + ":" + second;
	}

	public String getAMPM() throws IOException {
		return (isPM ? "pm" : "am");
	}

	public byte getByte(int loc) throws IndexOutOfBoundsException, IOException {
		return 0;
	}

	public void setByte(int loc, byte b) throws IndexOutOfBoundsException, IOException {
	}

	public void setDate(int m, int d, int y) throws IllegalArgumentException, IOException {
		year = y;
		month = m;
		day = d;		
	}

	public void setTime(int h, int m, int s) throws IllegalArgumentException, IOException {
		hour = h;
		minute = m;
		second = s;
	}
}

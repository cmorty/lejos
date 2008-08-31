package java.util;

/**
 * Class designed to manage Date and Time.
 * 
 * Note: some methods are deprecated.
 * 
 * @author Juan Antonio Brenha Moral
 */
public class Date {
	private int year = 2000;
	private int month = 1;
	private int day = 1;

	private int hours = 0;
	private int minutes = 0;
	private int seconds = 0;

	public Date(){
		//Empty
	}
	
	/*
	 * GETTERS & SETTERS
	 */
	
	/**
	 * Set Year
	 */
	public void setYear(int yyyy){ 
		if((yyyy >=0) && (yyyy <= 99)){
			year = 2000 + yyyy;
		}
	}
	
	/**
	 * Get year
	 * 
	 * @return
	 */
	public int getYear(){
		return year;
	}

	/**
	 * Set Month
	 * 
	 * @param mm
	 */
	public void setMonth(int mm){
		if((mm >= 1) && (mm<=12)){
			month = mm;
		}
	}
	
	/**
	 * Get Month
	 * 
	 * @return
	 */
	public int getMonth(){
		return month;
	}
	
	/**
	 * Set Day
	 * 
	 * @param dd
	 */
	public void setDay(int dd){
		if((dd>=1) && (dd<=31)){
			day = dd;
		}
	}

	/**
	 * Get Day
	 * 
	 * @return
	 */
	public int getDay(){
		return day;
	}

	/**
	 * Set hours
	 * 
	 * @param hh
	 */
	public void setHours(int hh){
		if((hh >= 0) && (hh<= 23)){
			hours = hh;
		}
	}

	/**
	 * Get Hours
	 * 
	 * @return
	 */
	public int getHours(){
		return hours;
	}

	/**
	 * Set Minutes
	 * 
	 * @param mm
	 */
	public void setMinutes(int mm){
		if((mm >= 0) && (mm <= 59)){
			minutes = mm;
		}
	}
	
	/**
	 * Get Minutes
	 * 
	 * @return
	 */
	public int getMinutes(){
		return minutes;
	}
	
	/**
	 * Set Seconds
	 * 
	 * @param ss
	 */
	public void setSeconds(int ss){
		if((ss >= 0) && (ss <= 59)){
			seconds = ss;
		}
	}

	/**
	 * Get Seconds
	 * 
	 * @return
	 */
	public int getSeconds(){
		return seconds;
	}

	/*
	 * UTILS
	 */
	
	/**
	 * Compare 2 Date objects to know if current Date object is before 
	 * than parameter
	 * 
	 * @param when
	 */
	public boolean before(Date when) {
		return getMillisOf(this) < getMillisOf(when);
	}

	/**
	 * Compare 2 Date objects to know if current Date object is after 
	 * than parameter
	 * 
	 * @param when
	 */
	public boolean after(Date when) {
		return getMillisOf(this) > getMillisOf(when);
	}

	/**
	 * Returns the millisecond value of this <code>Date</code> object
	 * without affecting its internal state.
	 * return the amount of time in milisecons from 1/1/1970
	 */
	private int getMillisOf(Date date){
		return 1;
	}
}

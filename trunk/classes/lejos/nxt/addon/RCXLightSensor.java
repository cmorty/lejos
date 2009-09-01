package lejos.nxt.addon;

import lejos.nxt.*;
import lejos.robotics.Colors;
import lejos.robotics.LightLampDetector;
import lejos.robotics.Colors.Color;

/*
 * WARNING: THIS CLASS IS SHARED BETWEEN THE classes AND pccomms PROJECTS.
 * DO NOT EDIT THE VERSION IN pccomms AS IT WILL BE OVERWRITTEN WHEN THE PROJECT IS BUILT.
 */

/**
 * Abstraction for an RCX light sensor.
 * 
 */
public class RCXLightSensor implements SensorConstants, LightLampDetector {
	LegacySensorPort port;
	
	private int _zero = 1023;
	private int _hundred = 0;
	
	private boolean floodlight = false;
	
	/**
	 * Create an RCX light sensor object attached to the specified port.
	 * The sensor will be activated, i.e. the LED will be turned on.
	 * @param port port, e.g. Port.S1
	 */
	public RCXLightSensor(LegacySensorPort port)
	{
		this.port = port;
		port.setTypeAndMode(TYPE_REFLECTION,
                            MODE_PCTFULLSCALE);
	}
	
	/**
	  * Activates an RCX light sensor. This method should be called
	  * if you want to get accurate values from an RCX
	  * sensor. In the case of RCX light sensors, you should see
	  * the LED go on when you call this method.
	  * @deprecated
	  */
	public void activate()
	{
		setFloodlight(true);
	}
	
	/**
     * Passivates an RCX light sensor.
     * @deprecated 
	 */
	public void passivate()
	{
		setFloodlight(false);
	}
	
	/**
	 * Read the current sensor value.
	 * @return Value as a percentage.
	 * @deprecated
	 */
	public int readValue()
	{
		return getLightLevel();
	}

	public Colors.Color getFloodlight() {
		if(this.floodlight == true)
			return Colors.Color.RED;
		else
			return Colors.Color.NONE;
	}

	public boolean isFloodlightOn() {
		return floodlight;
	}

	public void setFloodlight(boolean floodlight) {
		this.floodlight = floodlight;
		if(floodlight == true)
			port.activate();
		else
			port.passivate();
	}

	public boolean setFloodlight(Colors.Color color) {
		if(color == Colors.Color.RED) {
			setFloodlight(true);
			return true;
		} else if (color == Colors.Color.NONE) {
			setFloodlight(false);
			return true;
		} else return false;
	}
	
	public int getLightLevel() {
		return ((1023 - port.readRawValue()) * 100/ 1023); 
	}

	public int getRawLightLevel() {
		return 1023 - port.readRawValue();
	}
	
	/**
	 * call this method when the light sensor is reading the low value - used by readValue
	 **/
		public void calibrateLow()
		{
			_zero = port.readRawValue();
		}
	/** 
	 *call this method when the light sensor is reading the high value - used by readValue
	 */	
		public void calibrateHigh()
		{
			_hundred = port.readRawValue();
		}
		/** 
		 * set the normalized value corresponding to readValue() = 0
		 * @param low the low value
		 */
		public void setLow(int low) { _zero = 1023 - low;}
		  /** 
	     * set the normalized value corresponding to  readValue() = 100;
	     * @param high the high value
	     */
	    public void setHigh(int high) { _hundred = 1023 - high;}
	    /**
	    * return  the normalized value corresponding to readValue() = 0
	    */
	   public int getLow() { return 1023 - _zero;}
	    /** 
	    * return the normalized value corresponding to  readValue() = 100;
	    */
	   public int  getHigh() {return 1023 - _hundred;}

}

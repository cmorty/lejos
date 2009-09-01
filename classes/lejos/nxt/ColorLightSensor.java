package lejos.nxt;

import lejos.robotics.*;

/*
 * WARNING: THIS CLASS IS SHARED BETWEEN THE classes AND pccomms PROJECTS.
 * DO NOT EDIT THE VERSION IN pccomms AS IT WILL BE OVERWRITTEN WHEN THE PROJECT IS BUILT.
 */

/**
 * LEGO Color Sensor driver.
 * This driver provides access to the LEGO Color sensor. It allows the reading of
 * raw and processed color values. The sensor has a tri-color LED and this can
 * be set to output red/green/blue or off. It also has a full mode in which
 * four samples are read (off/red/green/blue) very quickly. These samples can
 * then be combined using the calibration data provided by the device to
 * determine the "LEGO" color currently being viewed.
 * @author andy
 */
public class ColorLightSensor implements LampLightDetector, ColorDetector, SensorConstants
{
    protected Colors.Color[] colorMap = Colors.Color.values();
    protected SensorPort port;
    protected int type;
    private int _zero = 1023;
	private int _hundred = 0;
    
    private Colors.Color lampColor = Colors.Color.NONE;

    /**
     * Create a new Color Sensor instance and bind it to a port.
     * @param port Port to use for the sensor.
     * @param type Initial operating mode.
     */
    // TODO: We need a default constructor that doesn't need type, which is just the lamp color it uses to scan.
    // NXT LightSensor and RCXLightSensor set default to red LED on. Perhaps this should too? Make it an interface requirement?
    public ColorLightSensor(SensorPort port, int type)
    {
        this.port = port;
        port.enableColorSensor();
        port.setTypeAndMode(type, SensorPort.MODE_RAW);
    }

    /**
     * Change the type of the sensor
     * @param type new sensor type.
     */
    // TODO: Type changes the lamp color, so maybe should use Colors.Color enum?
    // TODO: I'm aware the user can use ColorLightSensor to change type (lamp), and lampColor
    // won't be updated and it will give wrong value for getFloodlight(). Don't care until we work out the API.
    public void setType(int type)
    {
        port.setType(type);
        this.type = type;
    }

    /**
     * Return a single raw value from the device.
     * When in single color mode this returns the raw sensor reading.
     * Values range from 0 to 1023 but usually don't get over 600.
     * @return the raw value or < 0 if there is an error.
     */
    public int readRawValue()
    {
        return port.readRawValue();
    }

    /**
     * When in full color mode this returns all four raw color values from the
     * device by doing four very quick reads and flashing all colors.
     * The raw values theoretically range from 0 to 1023 but in practice they usually 
     * do not go higher than 600. You can access the index of each color 
     * using RGB_RED, RGB_GREEN, RGB_BLUE and RGB_BLANK. e.g. to retrieve the Blue value:
     * <code>vals[ColorLightSensor.RGB_BLUE]</code>
     *  
     * @param vals array of four color values.
     * @return < 0 if there is an error number of values returned if ok.
     */
    public int readRawValues(int [] vals)
    {
        return port.readRawValues(vals);
    }

    /**
     * Return a set of calibrated data.
     * If in single color mode the returned data is a simple percentage. If in
     * full color mode the data is a set of calibrated red/blue/green/blank
     * readings that range from 0 to 255. You can access the index of each color 
     * using RGB_RED, RGB_GREEN, RGB_BLUE and RGB_BLANK. e.g. to retrieve the Blue value:
     * <code>vals[ColorLightSensor.RGB_BLUE]</code>
     * 
     * @param vals 4 element array for the results
     * @return < 0 if error. Number of values if ok.
     */
    public int readValues(int [] vals)
    {
        return port.readValues(vals);
    }

    /**
     * Return a single processed value.
     * If in single color mode this returns a single reading as a percentage. If
     * in full color mode it returns a Lego color value that identifies the
     * color of the object in view.
     * @return processed color value.
     */
    public int readValue()
    {
        return port.readValue();
    }

    /**
     * Read the current color and return an enum value. This is usually only accurate at a distance
     * of about 1 cm.It is not very good at detecting yellow.
     * @return The color enumeration under the sensor.
     */
    public Colors.Color readColor()
    {
        int col = readValue();
        if (col <= 0) return Colors.Color.NONE;
        return colorMap[col];
    }

	public int getLightValue() {
		// TODO: Problem! If lamp is on for illumination, this shuts it down. 
		// So either turn on red lamp, then switch back, or turn off lamp (if passive mode) then switch back.
		int temp_type =  this.type;
		setType(TYPE_COLORNONE);
		int val = readValue();
		this.setType(temp_type);
		return val;
	}

	public int getNormalizedLightValue() {
		int temp_type =  this.type;
		setType(ColorLightSensor.TYPE_COLORNONE);
		int val = readRawValue();
		this.setType(temp_type);
		return val;
	}

	public void setFloodlight(boolean floodlight) {
		this.lampColor = (floodlight ? Colors.Color.RED : Colors.Color.NONE);
		setType(floodlight ? TYPE_COLORRED : TYPE_COLORNONE);
	}

	public int getBlueComponent() {
		int temp_type =  this.type;
		setType(ColorLightSensor.TYPE_COLORBLUE);
		int val = readRawValue();
		// TODO: Normalize?
		// Scale to max 255:
		val = val * 255 / 1023;
		this.setType(temp_type);
		return val;
	}

	public int getGreenComponent() {
		// TODO: Since lampColor state is included now, can we use that instead for all these temp_type?
		int temp_type =  this.type;
		setType(ColorLightSensor.TYPE_COLORGREEN);
		int val = readRawValue();
		// TODO: Normalize? Use _zero instead of 1023? (changes when calibrated)
		// TODO: What about scaling to max 255 using _hundred? (changes when calibrated) 
		val = val * 255 / 1023;
		this.setType(temp_type);
		return val;
	}

	public int[] getColor() {
		int temp_type =  this.type; 
		setType(ColorLightSensor.TYPE_COLORFULL);
		int [] all_vals = new int[4];
		readValues(all_vals);
		int [] rgb_vals = new int[3];
		System.arraycopy(all_vals, 0, rgb_vals, 0, 3);
		this.setType(temp_type);
		return rgb_vals;
		
	}

	// TODO: Three getXXComponent() methods share code. Could reuse code. Or Sven has suggestion of getColorComponent(RED).
	public int getRedComponent() {
		int temp_type =  this.type;
		setType(ColorLightSensor.TYPE_COLORRED);
		int val = readRawValue();
		// TODO: Normalize?
		// Scale to max 255:
		val = val * 255 / 1023;
		this.setType(temp_type);
		return val;
	}

	public Colors.Color getFloodlight() {
		return lampColor;
	}

	public boolean isFloodlightOn() {
		return (lampColor != Colors.Color.NONE);
	}

	public boolean setFloodlight(Colors.Color color) {
		if(color == Colors.Color.RED) {
			this.lampColor = color;
			setType(ColorLightSensor.TYPE_COLORRED);
			return true;
		} else if(color == Colors.Color.BLUE) {
			this.lampColor = color;
			setType(ColorLightSensor.TYPE_COLORBLUE);
			return true;
		} else if(color == Colors.Color.GREEN) {
			this.lampColor = color;
			setType(ColorLightSensor.TYPE_COLORGREEN);
			return true;
		} else if(color == Colors.Color.NONE) {
			this.lampColor = color;
			setType(ColorLightSensor.TYPE_COLORNONE);
			return true;
		} else 
			return false;
	}

	// TODO: Since this calibrate code (and other code) is the same for every sensor, perhaps we should consider abstract classes to inherit shared code from
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
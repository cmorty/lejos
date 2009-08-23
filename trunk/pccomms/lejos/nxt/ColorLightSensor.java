package lejos.nxt;

import lejos.robotics.LightLampDetector;
import lejos.robotics.ColorDetector;


/**
 * Lego Color Sensor driver.
 * This driver provides access to the Lego Color sensor. It allows the reading
 * raw and processed color values. The sensor has a tri-color led and this can
 * be set to output red/green/blue or off. It also has a full mode in which
 * four samples are read (off/red/green/blue) very quickly. These samples can
 * then be combined using the calibration data provided by the device to
 * determine the "Lego" color currently being viewed.
 * @author andy
 */
public class ColorLightSensor implements LightLampDetector, ColorDetector, SensorConstants
{
    protected Colors.Color[] colorMap = Colors.Color.values();
    protected SensorPort port;
    protected int type;


    /**
     * Create a new Color Sensor instance and bind it to a port.
     * @param port Port to use for the sensor.
     * @param type Initial operating mode.
     */
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

	public int getLightLevel() {
		// TODO: Problem! If lamp is on for illumination, this shuts it down. 
		// So either turn on red lamp, then switch back, or turn off lamp (if passive mode) then switch back.
		int temp_type =  this.type;
		setType(TYPE_COLORNONE);
		int val = readValue();
		this.setType(temp_type);
		return val;
	}

	// TODO: Remove this from here and interface
	public int getRawLightLevel() {
		// TODO: Problem! If lamp is on for illumination, this shuts it down. 
		// So either turn on red lamp, then switch back, or turn off lamp (if passive mode) then switch back.
		int temp_type =  this.type;
		setType(ColorLightSensor.TYPE_COLORNONE);
		int val = readRawValue();
		this.setType(temp_type);
		return val;
	}

	public void setFloodlight(boolean floodlight) {
		setType(floodlight ? TYPE_COLORRED : TYPE_COLORNONE);
	}

	public int getBlue() {
		int temp_type =  this.type;
		setType(ColorLightSensor.TYPE_COLORBLUE);
		int val = readValue();
		this.setType(temp_type);
		return val;
	}

	public int getGreen() {
		int temp_type =  this.type;
		setType(ColorLightSensor.TYPE_COLORGREEN);
		int val = readValue();
		this.setType(temp_type);
		return val;
		
	}

	public int[] getRGB() {
		int temp_type =  this.type;
		setType(ColorLightSensor.TYPE_COLORFULL);
		int [] all_vals = new int[4];
		readValues(all_vals);
		int [] rgb_vals = new int[3];
		System.arraycopy(all_vals, 0, rgb_vals, 0, 3);
		this.setType(temp_type);
		return rgb_vals;
		
	}

	public int getRed() {
		int temp_type =  this.type;
		setType(ColorLightSensor.TYPE_COLORRED);
		int val = readValue();
		this.setType(temp_type);
		return val;
	}
}


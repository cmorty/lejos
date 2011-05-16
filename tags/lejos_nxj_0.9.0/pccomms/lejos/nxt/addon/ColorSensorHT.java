package lejos.nxt.addon;

import lejos.nxt.I2CSensor;
import lejos.nxt.I2CPort;
import lejos.robotics.*;

/*
 * WARNING: THIS CLASS IS SHARED BETWEEN THE classes AND pccomms PROJECTS.
 * DO NOT EDIT THE VERSION IN pccomms AS IT WILL BE OVERWRITTEN WHEN THE PROJECT IS BUILT.
 */

/**
 * HiTechnic color sensor.<br>
 * www.hitechnic.com
 *
 *@author BB extended by A.T.Brask
 * 
 */
public class ColorSensorHT extends I2CSensor implements ColorDetector {
    byte[] buf = new byte[2];

    // TODO: Problem: The following table ignores pastels and other subtle colors HiTechnic can detect.
    // Converting to limited JSE color set means this generic interface isn't as rich at describing color
    // ID as it could be. 
    int [] colorMap = {Color.BLACK, Color.MAGENTA, Color.MAGENTA, Color.BLUE, Color.GREEN, Color.YELLOW, Color.YELLOW,
    		Color.ORANGE, Color.RED, Color.RED, Color.MAGENTA, Color.MAGENTA, Color.YELLOW, Color.PINK,
    		Color.PINK, Color.PINK, Color.MAGENTA, Color.WHITE};
    
    public ColorSensorHT(I2CPort port)
    {
        super(port);
    }

    // INDEX VALUES

    /**
     * Returns the color index detected by the sensor. 
     * @return Color index.<br>
     * <li> 0 = black
     * <li> 1 = violet
     * <li> 2 = purple
     * <li> 3 = blue
     * <li> 4 = green
     * <li> 5 = lime
     * <li> 6 = yellow
     * <li> 7 = orange
     * <li> 8 = red
     * <li> 9 = crimson
     * <li> 10 = magenta
     * <li> 11 to 16 = pastels 
     * <li> 17 = white
     */
    public int getColorID()
    {
        int ret = getData(0x42, buf, 1);
        if(ret != 0) return -1;
        int HT_val = (0xFF & buf[0]);
        return colorMap[HT_val];
    }

    /**
     * Essentially the same as getColorNumber() but with a resolution of 6 bits.
     * Red is bit 5-4, green is bit 3-2 and blue is bit 1-0.
     *
     * @return Color index number
     */
    public int getColorIndexNumber()
    {
        int ret = getData(0x4c, buf, 1);
        if(ret != 0) return -1;
        return (0x3F & buf[0]);
    }


    // 8 BIT RGB VALUES

    /**
	 * Returns a single color component, specified by using an enumeration constant as a parameter. e.g. Color.RED.
	 * @param color An integer obtained from Color, such as Color.RED, Color.GREEN or Color.BLUE
	 * @return The calibrated/normalized RGB value (0-255)
	 */
	public int getRGBComponent(int color)
    {
        // TODO: Check if color is 0-2
    	int ret = getData(0x43 + color, buf, 1);
        if(ret != 0) return -1;
        return (0xFF & buf[0]);
    }

    // NORMALIZED 8 BIT RGB VALUES
    
    /**
     * Returns the normalized color component. 
     * @return component value (0 to 255).
     */
    public int getRGBNormalized(int color)
    {
    	// TODO: Check if color is 0-2
    	int ret = getData(0x4d + color, buf, 1);
        if(ret != 0) return -1;
        return (0xFF & buf[0]);
    }


    // RAW 10 BIT RGB VALUES

    /**
     * Returns the raw saturation of the color. 
     * @return component value (0 to 1023).
     */
    public int getRGBRaw(int color)
    {
    	// TODO: Check if color is 0-2
    	int ret = getData(0x46 + (2 * color), buf, 2);
        if(ret != 0) return -1;
        return ((0xFF & buf[0]) << 8) | (0xFF & buf[1]);
    }

    // MODE CONTROL SETTINGS

    /**
     * Returns the value of the mode control register (0x41)
     *
     * @return The value of the register or -1 if the operation fails.
     */
    public int getMode()
    {
        int ret = getData(0x41, buf, 1);
        if(ret != 0) return -1;
        return (0xFF & buf[0]);
    } 

    /**
     * Puts the sensor into white balance calibration mode. For best results
     * the sensor should be pointed at a diffuse white surface at a distance
     * of approximately 15mm before calling this method. After a fraction of
     * a second the sensor lights will flash and the calibration is done. When
     * calibrated, the sensor keeps this information in non-volatile memory.
     *
     * @return 0 if it went well and -1 otherwise
     */
    public int initWhiteBalance()
    {
        return sendData(0x41, (byte)0x43);
    }

    /**
     * Puts the sensor into black/ambient level calibration mode. For best
     * results the sensor should be pointed in a direction with no obstacles
     * for 50cm or so. This reading the sensor will use as a base level for
     * other readings. After a fraction of a second the sensor lights will
     * flash and the calibration is done. When calibrated, the sensor keeps
     * this information in non-volatile memory.
     *
     * @return 0 if it went well and -1 otherwise.
     */
    public int initBlackLevel()
    {
        return sendData(0x41, (byte)0x42);
    }

    // TODO: Getting color ID causes another reading, not very efficient if just want RGB fast. Would be nice
    // to piggyback on colorID code using values already retrieved.
	public Color getColor() {
		return new Color(getRGBComponent(Color.RED), getRGBComponent(Color.GREEN), getRGBComponent(Color.BLUE), this.getColorID());
	}
}

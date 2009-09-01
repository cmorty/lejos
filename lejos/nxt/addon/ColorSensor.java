package lejos.nxt.addon;

import lejos.nxt.I2CSensor;
import lejos.nxt.I2CPort;
import lejos.robotics.ColorDetector;

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
public class ColorSensor extends I2CSensor implements ColorDetector {
    byte[] buf = new byte[2];

    public ColorSensor(I2CPort port)
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
    public int getColorNumber()
    {
        int ret = getData(0x42, buf, 1);
        if(ret != 0) return -1;
        return (0xFF & buf[0]);
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
     * Returns the red saturation of the color. 
     * @return red value (0 to 255).
     */
    public int getRedComponent()
    {
        int ret = getData(0x43, buf, 1);
        if(ret != 0) return -1;
        return (0xFF & buf[0]);
    }

    /**
     * Returns the green saturation of the color. 
     * @return green value (0 to 255).
     */
    public int getGreenComponent()
    {
        int ret = getData(0x44, buf, 1);
        if(ret != 0) return -1;
        return (0xFF & buf[0]);
    }

    /**
     * Returns the blue saturation of the color. 
     * @return blue value (0 to 255).
     */
    public int getBlueComponent()
    {
        int ret = getData(0x45, buf, 1);
        if(ret != 0) return -1;
        return (0xFF & buf[0]);
    }

    // NORMALIZED 8 BIT RGB VALUES

    /**
     * Returns the normalized red saturation of the color. 
     * @return red value (0 to 255).
     */
    public int getNormalizedRed()
    {
        int ret = getData(0x4d, buf, 1);
        if(ret != 0) return -1;
        return (0xFF & buf[0]);
    }

    /**
     * Returns the normalized green saturation of the color. 
     * @return green value (0 to 255).
     */
    public int getNormalizedGreen()
    {
        int ret = getData(0x4e, buf, 1);
        if(ret != 0) return -1;
        return (0xFF & buf[0]);
    }

    /**
     * Returns the normalized blue saturation of the color. 
     * @return blue value (0 to 255).
     */
    public int getNormalizedBlue()
    {
        int ret = getData(0x4f, buf, 1);
        if(ret != 0) return -1;
        return (0xFF & buf[0]);
    }

    // RAW 10 BIT RGB VALUES

    /**
     * Returns the raw red saturation of the color. 
     * @return red value (0 to 1023).
     */
    public int getRawRed()
    {
        int ret = getData(0x46, buf, 2);
        if(ret != 0) return -1;
        return ((0xFF & buf[0]) << 8) | (0xFF & buf[1]);
    }

    /**
     * Returns the raw green saturation of the color. 
     * @return green value (0 to 1023).
     */
    public int getRawGreen()
    {
        int ret = getData(0x48, buf, 2);
        if(ret != 0) return -1;
        return ((0xFF & buf[0]) << 8) | (0xFF & buf[1]);
    }

    /**
     * Returns the raw blue saturation of the color. 
     * @return blue value (0 to 1023).
     */
    public int getRawBlue()
    {
        int ret = getData(0x4a, buf, 2);
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

	public int[] getColor() {
		int [] vals = new int[3];
		vals[0] = getRedComponent();
		vals[1] = getGreenComponent();
		vals[2] = getBlueComponent();
		return vals;
	}
}

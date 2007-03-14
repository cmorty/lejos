package lejos.nxt;

/**
 * HiTechnic color sensor.<br>
 * www.hitechnic.com
 */
public class ColorSensor extends I2CSensor {
	byte[] buf = new byte[2];
	
	public ColorSensor(I2CPort port)
	{
		super(port);
	}
	
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
	public int getColorNumber() {
		int ret = getData(0x42, buf, 1);
		if(ret != 0) return -1;
		return (0xFF & buf[0]);
	}
	
	/**
	 * Returns the red saturation of the color. 
	 * @return red value (0 to 255).
	 */
	public int getRed() {
		int ret = getData(0x43, buf, 1);
		if(ret != 0) return -1;
		return (0xFF & buf[0]);
	}
	
	/**
	 * Returns the green saturation of the color. 
	 * @return green value (0 to 255).
	 */
	public int getGreen() {
		int ret = getData(0x44, buf, 1);
		if(ret != 0) return -1;
		return (0xFF & buf[0]);
	}
	
	/**
	 * Returns the blue saturation of the color. 
	 * @return blue value (0 to 255).
	 */
	public int getBlue() {
		int ret = getData(0x45, buf, 1);
		if(ret != 0) return -1;
		return (0xFF & buf[0]);
	}
}
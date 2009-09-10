package lejos.robotics;

public class Color {

	// TODO: PINK, GRAY, LIGHT_GRAY, DARK_GRAY, CYAN missing from JSE. Include?
    // TODO: PURPLE, VIOLET, LIME, CRIMSON, PASTEL are not part of JSE. Ignore?
    public static final int RED = 0;
	public static final int GREEN = 1;
	public static final int BLUE = 2;
	public static final int YELLOW = 3;
	public static final int MAGENTA = 4;
	public static final int CRIMSON = 5;
	public static final int VIOLET = 6;
	public static final int PURPLE = 7;
	public static final int ORANGE = 8;
	public static final int LIME = 9;
	public static final int PASTEL = 10;
    public static final int WHITE = 11;
    public static final int BLACK = 12;
    public static final int NONE = 99;
	
	private int red;
	private int blue;
	private int green;
	
	public Color(int red, int green, int blue) {
		this.red = red;
		this.green = green;
		this.blue = blue;
	}
	
	// TODO: Perhaps getRGBComponent(int color) method? Store three colors in int[3] in that case.
	
	// TODO: Could also include JSE API getRGB() which returns the RGB value representing the color in the 
	// default sRGB ColorModel. (Bits 24-31 are alpha, 16-23 are red, 8-15 are green, 0-7 are blue). 
	
	/**
	 * Returns the red component in the range 0-255 in the default sRGB space.
	 * @return the red component.
	 */
	public int getRed() {
		return red;
	}

	/**
	 * Returns the green component in the range 0-255 in the default sRGB space.
	 * @return the green component.
	 */
	public int getGreen() {
		return green;
	}

	/**
	 * Returns the blue component in the range 0-255 in the default sRGB space.
	 * @return the blue component.
	 */
	public int getBlue() {
		return blue;
	}
	
	// TODO: Perhaps set methods?
}

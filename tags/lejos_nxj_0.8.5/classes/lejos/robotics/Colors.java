package lejos.robotics;

/*
 * WARNING: THIS CLASS IS SHARED BETWEEN THE classes AND pccomms PROJECTS.
 * DO NOT EDIT THE VERSION IN pccomms AS IT WILL BE OVERWRITTEN WHEN THE PROJECT IS BUILT.
 */

/**
 * Values for standard Lego colors.
 * @author Andy Shaw
 */
public final class Colors {
    /**
     * Colors used as the output value when in full mode. Values are
     * compatible with LEGO firmware.
     */
    public static final int BLACK = 1;
    public static final int BLUE = 2;
    public static final int GREEN = 3;
    public static final int YELLOW = 4;
    public static final int RED = 5;
    public static final int WHITE = 6;
    /**
     * Index into the data returned by readRawValues and readValues when
     * using the Color sensor in full mode.
     */
    public static final int RGB_RED = 0;
    public static final int RGB_GREEN = 1;
    public static final int RGB_BLUE = 2;
    public static final int RGB_BLANK = 3;

    public enum Color {NONE, BLACK, BLUE, GREEN, YELLOW, RED, WHITE};
}

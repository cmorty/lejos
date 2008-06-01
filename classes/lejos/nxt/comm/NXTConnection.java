package lejos.nxt.comm;

import java.io.*;
import javax.microedition.io.*;


/**
 * Generic lejos nxt connection inetrface. Provide access to standard read/write
 * methods.
 * @author andy
 */
public interface NXTConnection extends StreamConnection {
    /* Connection modes */
    public static final int LCP = 1;
    public static final int PACKET = 0;
    public static final int RAW = 2;
    public int read(byte [] data, int len);
    public int write(byte [] data, int len);
    public int read(byte [] data, int len, boolean wait);
    public int write(byte [] data, int len, boolean wait);
}

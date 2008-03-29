package lejos.nxt.comm;

import java.io.*;
import javax.microedition.io.*;


/**
 * Generic lejos nxt connection inetrface. Provide access to standard read/write
 * methods.
 * @author andy
 */
public interface NXTConnection extends StreamConnection {
    public int read(byte [] data, int len);
    public int write(byte [] data, int len);
    public int read(byte [] data, int len, boolean wait);
    public int write(byte [] data, int len, boolean wait);
}

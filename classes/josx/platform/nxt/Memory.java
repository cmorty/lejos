/**
 * NXT access classes.
 */
package josx.platform.nxt;

/**
 * Provides access to memory.
 */
public class Memory
{
  /**
   * Should be used for all native memory accesses.
   */
  public static final Object MONITOR = new Object();
  
  static final byte[] iAuxData = new byte[7];
  static final int iAuxDataAddr = getDataAddress (iAuxData);

  public static native byte readByte (int aAddr);
  public static native void writeByte (int aAddr, byte aByte);

  public static native int  getDataAddress (Object obj);
  public static native void setBit(int aAddr, int bit, int value);

  public static short readShort (int aAddr)
  {
    int b1 = Memory.readByte (aAddr) & 0xff;
    int b2 = Memory.readByte (aAddr + 1) & 0xff;
    return (short)((b1 << 8) + b2);
  }

  public static void writeShort (int aAddr, short s)
  {
    Memory.writeByte (aAddr, (byte)((s >> 8) & 0xff));
    Memory.writeByte (aAddr + 1, (byte)(s & 0xff));
  }
}



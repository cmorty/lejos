
package js.tinyvm;

/**
 *
 * @author andy
 */
public enum DebugOptions {
    DebugMonitor(0x1), RemoteDebug(0x2);
    private int value;

    DebugOptions(int value)
    {
        this.value = value;
    }

    public int getValue()
    {
        return value;
    }
}
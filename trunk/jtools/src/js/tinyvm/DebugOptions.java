
package js.tinyvm;

/**
 *
 * @author andy
 */
public enum DebugOptions {
    LocalDebug(0x1), RConsoleDebug(0x2), RemoteDebug(0x4);
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
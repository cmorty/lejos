
package js.tinyvm;

/**
 *
 * @author andy
 */
public enum RunTimeOptions {
    EnableTypeChecks(0x1), EnableAssert(0x2);
    private int value;

    RunTimeOptions(int value)
    {
        this.value = value;
    }

    public int getValue()
    {
        return value;
    }
}
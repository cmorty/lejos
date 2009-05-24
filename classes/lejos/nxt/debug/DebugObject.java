package lejos.nxt.debug;

import java.util.*;
import lejos.nxt.*;

/**
 * Provide access to parts of a Java Object held within the VM
 * @author andy
 */
public class DebugObject
{

    private static final int OBJECT_HEADER = 4;
    private DebugInterface info;

    /**
     * Initialize access to the VM structures
     * @param info
     */
    public DebugObject(DebugInterface info)
    {
        this.info = info;
    }

    /**
     * Return the class index of the supplied object.
     * @param obj
     * @return class index
     */
    public int getClassIndex(Object obj)
    {
        return (DebugInterface.peekWord(obj, -OBJECT_HEADER) & 0xff);
    }
}

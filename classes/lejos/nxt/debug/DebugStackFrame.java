package lejos.nxt.debug;

import java.util.*;
import lejos.nxt.*;

/**
 * Provide access to the VM stack frame structure.
 * @author andy
 */
public class DebugStackFrame
{
    // Provide access to the VM stack frame. Will need to be changed if the
    // Stack frame layout is changed.
    private static final int FRAME_SIZE = 20;
    private static final int METHOD_SIZE = 12;
    private DebugInterface info;

    /**
     * Initialise the access to the VM
     * @param info vm access
     */
    public DebugStackFrame(DebugInterface info)
    {
        this.info = info;
    }

    /**
     * Return the index of the method that is currently associated with
     * the specified stack frame of the given thread.
     * @param thread
     * @param frame
     * @return method index
     */
    public int getMethodIndex(DebugThread thread, int frame)
    {
        // given a frame in the current stack return the associated method no.
        return (DebugInterface.peekWord(thread.stackFrameArray.hashCode() + frame * FRAME_SIZE) - info.methodBase) / METHOD_SIZE;
    }
}

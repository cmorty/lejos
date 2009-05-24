package lejos.nxt.debug;

import java.util.*;
import lejos.nxt.*;
/**
 * Provides a Java view of the VM thread structure.
 * @author andy
 */
public class DebugThread
{
    // NOTE This is very very iffy. This class does not really "exist"
    // The real class type is thread. However to make the private contents
    // availabe without having to change the thread class or add things to
    // java.lang, we cheat. Basically the firmware hands us a thread ref.
    // we pretend it is a DebugThread... Things seem to work. Do not add
    // methods or anything else to this class unless you really know what
    // you are doing!
    public DebugThread nextThread;
    public Object waitingOn;
    public int sleepUntil;
    public int[] stackFrameArray;
    public int[] stackArray;
    public byte stackFrameArraySize;
    public byte monitorCount;
    public byte threadId;
    public byte state;
    public byte priority;
    public byte interrupted;
    public byte daemon;
}

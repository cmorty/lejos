package lejos.nxt.debug;

import java.util.*;
import lejos.nxt.*;
/**
 * This class provides access to the VM threads structures. The VM holds the
 * thread structures as a set of circular linked lists of threads. There is
 * one list for each thread priority level and the head of the list is referred
 * to from the threads array.
 * @author andy
 */
public class DebugThreads
{

    private DebugInterface info;

    /**
     * Initialise access to the VM structures
     * @param info the interface to the VM
     */
    public DebugThreads(DebugInterface info)
    {
        this.info = info;
    }

    /**
     * Return the currently exsiting threads as an array. The threads are 
     * returned in priority order with the highest priority thread being first.
     * This call should probably not be used if other threads are active.
     * @param curThreads array to store the threads
     * @return the number of threads returned.
     */
    public int enumerate(DebugThread[] curThreads)
    {
        int cnt = 0;
        DebugThread[] threads = info.threads;
        for (int i = threads.length - 1; i >= 0; i--)
            if (threads[i] != null)
            {
                DebugThread start = threads[i];
                DebugThread th = threads[i];
                do
                {
                    if (cnt < curThreads.length)
                        curThreads[cnt++] = th;
                    th = th.nextThread;
                } while (th != start);
            }
        return cnt;
    }

    /**
     * Convert a DebugThread into a Thread to allow access to the normal
     * Thread interface.
     * @param dt the DebugThread
     * @return the Thread
     */
    public static Thread toThread(DebugThread dt)
    {
        Object o = (Object) dt;
        return (Thread) o;
    }

    /**
     * Suspend a thread. This places the specified thread into a suspended
     * state. If thread is null all threads except for the current thread will
     * be suspended.
     * @param thread
     */
    protected native static final void suspendThread(Object thread);

    /**
     * Resume a thread. A suspended thread will be resumed to it's previous 
     * state. If thread is null all suspended threads will be resumed.
     * @param thread
     */
    protected native static final void resumeThread(Object thread);
}


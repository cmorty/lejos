package lejos.nxt.debug;

import java.util.*;
import lejos.nxt.*;
import lejos.nxt.comm.*;
import java.io.PrintStream;

/**
 * Simple debug monitor that can be run alongside and nxj program. This class
 * catches unhandled excpetions and user interrupts (accept + escape key), it
 * displays information about the event (stack trace etc.). The user is then
 * abble to either perform a soft reset (Escape), a hard reset (Escape + Accept),
 * or continue running the program (any other key). All output is directed via
 * System.err.
 * @author andy
 */
public class DebugMonitor
{

    /**
     * Display information about the uncaught excpetion on System.err
     * @param info the current VM event
     */
    static void displayException(DebugInterface info)
    {
        DebugStackFrame fi = new DebugStackFrame(info);
        DebugObject oi = new DebugObject(info);
        System.err.println("Java Exception");
        System.err.println("Class: " + oi.getClassIndex(info.exception));
        System.err.println("Method: " + info.method + "(" + info.pc + ")");
        int sp = info.frame - 1;
        for (int i = 3; i < 8 && sp-- > 0; i++)
            System.err.println("Called from: " + fi.getMethodIndex(info.thread, sp));
    }
    static String[] states = {"N", "D", "I", "R", "E", "W", "S"};

    /**
     * Dump information about all of the active threads to System.err. The
     * threads are dumped in reverse order (low priority first), so that if
     * there are more then eight threads the most important 8 will still be
     * on the LCD display!
     * @param info
     */
    static void displayThreads(DebugInterface info)
    {
        DebugStackFrame fi = new DebugStackFrame(info);
        DebugThread[] threads = info.threads;
        for (int i = 0; i < threads.length; i++)
            if (threads[i] != null)
            {
                DebugThread start = threads[i];
                DebugThread th = threads[i];
                do
                {
                    String out = "";
                    out += th.threadId;
                    out += (th == info.thread ? "*" : states[th.state & 0x7f]);
                    int sp = th.stackFrameArraySize;
                    for (int j = 0; j < 3 && sp-- > 0; j++)
                        out += " " + fi.getMethodIndex(th, sp);
                    System.err.println(out);
                    th = th.nextThread;
                } while (th != start);
            }
    }

    public static void main(String[] args) throws Exception
    {
        // Setup the monitoring thread.
        DebugInterface monitor = DebugInterface.get();
        DebugInterface.eventOptions(DebugInterface.DBG_EXCEPTION, DebugInterface.DBG_EVENT_ENABLE);
        DebugInterface.eventOptions(DebugInterface.DBG_USER_INTERRUPT, DebugInterface.DBG_EVENT_ENABLE);

        // Start the real program in a new thread.
        Thread prog = new Thread()
        {

            public void run()
            {
                 DebugInterface.executeProgram(1);
            }
        };
        // Make sure we keep running when we start the program
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        prog.start();
        while (true)
        {
            // Allow exit if the program finishes normally.
            Thread.currentThread().setDaemon(true);
            // Wait for a debug event
            int event = monitor.waitEvent(0);
            Thread.currentThread().setDaemon(false);
            // Display the information
            LCD.clear();
            switch (event)
            {
                case DebugInterface.DBG_EXCEPTION:
                    displayException(monitor);
                    break;
                case DebugInterface.DBG_USER_INTERRUPT:
                    displayThreads(monitor);
                    break;
            }
            LCD.refresh();
            Sound.playTone(73, 150);
            Sound.pause(300);
            Sound.playTone(62, 500);
            // Wait for any buttons to be released
            while (Button.readButtons() != 0)
                Thread.yield();
            // Enable user interrupts again
            DebugInterface.eventOptions(DebugInterface.DBG_USER_INTERRUPT, DebugInterface.DBG_EVENT_ENABLE);
            // and wait to see what the user wants to do
            int pressed = Button.waitForPress();
            // If escape do soft-reboot
            if ((Button.ESCAPE.getId() & pressed) != 0)
                System.exit(1);
            // Otherwise try and continue gulp!
            LCD.clear();
            // Clear the event
            monitor.clear();
            DebugThreads.resumeThread(null);
        }
    }
}


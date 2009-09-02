package lejos.nxt.debug;

import lejos.nxt.*;

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
        VM vm = VM.getVM();
        int base = vm.getImage().getImageBase();
        System.err.println("Java Exception");
        System.err.println("Class: " + vm.getVMClass(info.exception).getClassNo());
        String msg = info.exception.getMessage();
        if (msg != null && msg.length() > 0)
            System.err.println("Msg: " + msg);
        System.err.println(" at: " + info.method + "(" + info.pc + ")");
        int cnt = 0;
        VM.VMStackFrames stack = vm.getVMThread(info.thread).getStackFrames(info.frame-1);
        for(VM.VMStackFrame sf : stack)
        {
            System.err.println(" at: " + sf.getVMMethod().getMethodNumber() + "(" + (sf.pc-base) + ")");
            if (cnt++ > 5) break;
        }
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
        VM vm = VM.getVM();
        VM.VMThreads threads = vm.getVMThreads();
        for(VM.VMThread thread : threads)
        {

            String out = "";
            out += thread.threadId;
            out += (thread.getJavaThread() == info.thread ? "*" : states[thread.state & 0x7f]);
            int cnt = 0;
            VM.VMStackFrames stack = thread.getStackFrames();
            for(VM.VMStackFrame frame : stack)
            {
                out += " " + frame.getVMMethod().getMethodNumber();
                if (++cnt >= 3) break;
            }
            System.err.println(out);
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
                 VM.executeProgram(1);
                 // This point will never be reached
            }
        };
        // Make sure we keep running when we start the program
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        // Enable stricter run time type checking
        VM.setVMOptions(VM.getVMOptions() | VM.VM_TYPECHECKS);
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
            VM.resumeThread(null);
        }
    }
}


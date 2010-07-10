package lejos.nxt.comm;

import lejos.nxt.*;
import java.io.*;
import lejos.util.Delay;
import lejos.nxt.debug.*;
import java.lang.InterruptedException;

/**
 * This class provides a simple way of sending output for viewing on a 
 * PC. The output is transmitted via the nxt USB connection or via Bluetooth.
 * If open is not called or if the connection to the PC is timed out, then
 * the output is discarded. The class may also be used to provide a remote view
 * of the NXT LCD display, and to capture various debug events. The use of these
 * facilities requires corresponding capabilities within the remote viewer and
 * is negotiated at connection time.
 *
 * Use of this class is normally initiated in two ways:<br>
 * 1. Explicit usage. The user program makes a call to one of the open methods
 *    and than uses the RConsole.println method to display output.<br>
 * 2. Implicit usage. In this mode the -gr (--remotedebug) is specified to the 
 *    leJOS linker and this arranges to start the user program via this class. 
 *    This class then hooks any required debug events, waits for a remote viewer
 *    to connect (via either Bluetooth or USB), and then routes the standard
 *    system output and err print streams to the remote display.
 *
 */
public class RConsole extends Thread
{
    /*
     * Developer notes
     * This code is used when the system is fully operational (normal mode) and
     * when it is running after a debug event has ocurred (debug mode). In this
     * later mode all users threads are suspended. This means that great care
     * must be taken to ensure that no locks can be held by user code that will
     * prevent the correct operation when in debug mode. In particular this
     * means that if Bluetooth is being use for the remote display, then the
     * user code should not also use Bluetooth.
     * 
     * To ensure correct operation we synchronize output functions using a 
     * sync variable. This variable normally uses the pront stream object
     * for synchronization. However when running in debug mode we point this
     * at a new object to ensure that any locks held on the pront stream will
     * not block debug mode I/O.
     */
    static final int OPT_LCD = 1;
    static final int OPT_EVENTS = 2;
    
    static final int MODE_SWITCH = 0xff;
    static final int MODE_LCD = 0x0;
    static final int MODE_EVENT = 0x1;
    
    static final int LCD_UPDATE_PERIOD = 100;
    
    static PrintStream ps;
    static OutputStream os;
    static Object debugLock = new Object();
    static Object sync = null;
    static volatile byte[] output;
    static volatile int outputLen;
    static NXTConnection conn;
    static RConsole ioThread;
    static boolean lcd = false;
    static boolean events = false;

    /**
     * This internal class is used to provide a print stream connection to the
     * remote console. Note that to avoid locking issues between normal and
     * debug mode operation it is not connected directly to the remote output
     * stream. Instead a simple busy/wait scheme is used to pass data between
     * this code and the main output thread.
     */
    static private class RConsoleOutputStream extends OutputStream
    {
        private byte[] buffer;
        private int numBytes = 0;

        /**
         * Create an internal print stream for use by the remote console.
         * @param buffSize
         */
        RConsoleOutputStream(int buffSize)
        {
            buffer = new byte[buffSize];
            output = null;
        }

        /**
         * Write data to the stream flush when full.
         * @param b the byte to write
         * @throws IOException
         */
        public void write(int b) throws IOException
        {
            synchronized(sync)
            {
                if (numBytes == buffer.length)
                {
                    flush();
                }
                buffer[numBytes] = (byte) b;
                numBytes++;
            }
        }

        /**
         * Flush the data to the remote console stream. 
         * @throws IOException
         */
        @Override
        public void flush() throws IOException
        {
            synchronized (sync)
            {
                if (numBytes > 0)
                {
                    // use busy wait synchronization.
                    outputLen = numBytes;
                    output = buffer;
                    ioThread.interrupt();
                    while (output != null)
                        Thread.yield();
                    numBytes = 0;
                }
            }
        }
    }

    /**
     * Ensure that this class is never instantiated.
     */
    private RConsole()
    {
    }

    /**
     * Setup the remote connection. Perform the standard handshake and setup
     * the connection ready to go.
     * @param c The connection to use for the remote console.
     */
    private static void init(NXTConnection c)
    {
        if (c == null)
            return;
        conn = c;
        try
        {
            LCD.drawString("Got connection  ", 0, 0);
            // Perfomr the handshake. This conists of 2 signature bytes 'RC'
            // followed by a single capability byte.
            byte[] hello = new byte[32];
            int len = conn.read(hello, hello.length);
            if (len != 3 || hello[0] != 'R' || hello[1] != 'C')
            {
                LCD.drawString("Console no h/s    ", 0, 0);
                conn.close();
                return;
            }
            LCD.drawString("Console open    ", 0, 0);
            if (conn == null)
                return;
            os = conn.openOutputStream();
            ps = new PrintStream(new RConsoleOutputStream(128));
            LCD.refresh();
            lcd = ((hello[2] & OPT_LCD) != 0);
            events = ((hello[2] & OPT_EVENTS) != 0);
            // Create the I/O thread and start it.
            ioThread = new RConsole();
            ioThread.setPriority(Thread.MAX_PRIORITY);
            ioThread.setDaemon(true);
            ioThread.start();
            // initially we are in normal mode.
            setDebugMode(false);
            println("Console open");
        } catch (Exception e)
        {
            LCD.drawString("Console error " + e.getMessage(), 0, 0);
            LCD.refresh();
        }
    }


    /**
     * Wait for a remote viewer to connect via USB.
     * @param timeout how long to wait, 0 waits for ever.
     */
    public static void openUSB(int timeout)
    {
        LCD.drawString("USB Console...  ", 0, 0);
        init(USB.waitForConnection(timeout, 0));

    }

    /**
     * Wait for a remote viewer to connect via Bluetooth.
     * @param timeout how long to wait, 0 waits for ever.
     */
    public static void openBluetooth(int timeout)
    {
        LCD.drawString("BT Console...   ", 0, 0);
        init(Bluetooth.waitForConnection(timeout, NXTConnection.PACKET, null));
    }

    /**
     * Internal thread used to wait for a connection.
     */
    private static class ConnectThread extends Thread
    {

        ConnectThread other;
        boolean finished = false;
    }

    /**
     * Wait for a remote viewer to connect using either USB or Bluetooth.
     * @param timeout time to wait for the connection, 0 waits for ever.
     */
    public static void openAny(final int timeout)
    {
        // Bluetooth connection thread
        ConnectThread btThread = new ConnectThread()
        {

            @Override
            public void run()
            {
                openBluetooth(timeout);
                // force the other connect to stop
                finished = true;
                while (!other.finished)
                    USB.cancelConnect();
            }
        };
        // USB connection thread.
        ConnectThread usbThread = new ConnectThread()
        {

            @Override
            public void run()
            {
                openUSB(timeout);
                finished = true;
                while (!other.finished)
                    Bluetooth.cancelConnect();
            }
        };
        btThread.other = usbThread;
        usbThread.other = btThread;
        btThread.start();
        usbThread.start();
        Delay.msDelay(10);
        LCD.drawString("Remote Console...   ", 0, 0);
        try
        {
            btThread.join();
            usbThread.join();
        } catch (InterruptedException e)
        {
        }
    }

    /**
     * Wait forever for a remote viewer to connect.
     */
    public static void open()
    {
        openAny(0);
    }

    /**
     * Send output to the remote viewer.
     * @param s
     */
    public static void print(String s)
    {
        if (ps == null)
            return;
        synchronized (sync)
        {
            ps.print(s);
            ps.flush();
        }
    }

    /**
     * Send a line to the remote viewer.
     * @param s
     */
    public static void println(String s)
    {
        if (ps == null)
            return;
        synchronized (sync)
        {
            ps.println(s);
        }
    }

    /**
     * Close the remote console connection.
     */
    public static void close()
    {
        if (conn == null)
            return;
        println("Console closed");
        synchronized (os)
        {
            try
            {
                conn.close();
                LCD.drawString("Console closed  ", 0, 0);
                LCD.refresh();
                Delay.msDelay(2000);
                ps = null;
                conn = null;
                os = null;
            } catch (Exception e)
            {
            }
        }
    }

    /**
     * Check to see if the remote console is available for use.
     * @return true if the console is open.
     */
    public static boolean isOpen()
    {
        return (ps != null);
    }

    /**
     * Return a print stream connected to the remote console.
     * @return the print stream
     */
    public static PrintStream getPrintStream()
    {
        return ps;
    }

    /**
     * Enter and leave debug mode. When in debug mode we use a different locking
     * mechanism to ensure that suspended user threads will not block I/O.
     * @param debug true to enter debug mode, false to leave.
     */
    public static void setDebugMode(boolean debug)
    {
            sync = (debug ? debugLock : ps);
    }
    /**
     * Main console I/O thread.
     */
    @Override
    public void run()
    {
        long nextUpdate = 0;
        while (conn != null)
        {
            long now = System.currentTimeMillis();
            synchronized (os)
            {
                try
                {
                    // First check to see if we have any "normal" output to go.
                    if (output != null)
                    {
                        os.write(output, 0, outputLen);
                        output = null;
                        os.flush();
                    }
                    // Are we mirroring the LCD display?
                    if (lcd)
                    {
                        if (now > nextUpdate)
                        {
                            os.write(MODE_SWITCH);
                            os.write(MODE_LCD);
                            os.write(LCD.getDisplay());
                            os.flush();
                            nextUpdate = now + LCD_UPDATE_PERIOD;
                        }
                    }
                    else
                        nextUpdate = now + LCD_UPDATE_PERIOD;
                } catch (Exception e)
                {
                    // Not really sure what do if we get an I/O error. Should
                    // probably have some way to report it to calling threads.
                    break;
                }
            }
            try {
                Thread.sleep(nextUpdate - now);
            }
            catch (InterruptedException e)
            {
                // We use interrupt to wake the threade from sleep early.
            }
            Delay.msDelay(1);
        }
    }

    /**
     * Send an exception event to the remote console.
     * @param classNo The exception class.
     * @param methodNo The method in which the exception occurred.
     * @param pc The location of the exception.
     * @param stackTrace An internal stack trace.
     * @param msg A text message associated with the exception.
     * @return True if OK.
     */
    public static boolean exception(int classNo, int methodNo, int pc, int[] stackTrace, String msg)
    {
        if (conn != null && events)
        {
            synchronized (os)
            {
                try
                {
                    os.write(MODE_SWITCH);
                    os.write(MODE_EVENT);
                    os.write(classNo);
                    if (msg == null)
                        msg = "";
                    os.write(msg.length());
                    os.write(0);
                    for (char ch : msg.toCharArray())
                        os.write(ch);
                    if (stackTrace == null)
                        os.write(0);
                    else
                    {
                        os.write(stackTrace.length);
                        for (int frame : stackTrace)
                        {
                            for (int i = 0; i < 4; i++)
                            {
                                os.write(frame);
                                frame >>>= 8;
                            }
                        }
                    }
                    os.flush();
                } catch (IOException e)
                {
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    /**
     * The following internal class provides an implicit remote console and
     * also hooks into the debug event system.
     */
    static public class Monitor extends DebugMonitor
    {

        /**
         * Exit the program
         */
        @Override
        protected void exit()
        {
            RConsole.close();
            super.exit();
        }

        /**
         * Handle a debug event.
         * @param event
         */
        @Override
        protected void processEvent(int event)
        {
            setDebugMode(true);
            switch (event)
            {
                case DebugInterface.DBG_EXCEPTION:
                    if (!exception(VM.getVM().getVMClass(monitor.exception).getClassNo(),
                            monitor.method, monitor.pc,
                            VM.getThrowableStackTrace(monitor.exception), monitor.exception.getMessage()))
                        displayException(monitor);
                    exit();
                    break;
                case DebugInterface.DBG_USER_INTERRUPT:
                    System.err.println("User interrupt");
                case DebugInterface.DBG_PROGRAM_EXIT:
                    System.err.println("Program exit");
                    exit();
                    break;
            }
            setDebugMode(false);
        }

        /**
         * Wait for the viewer to connect, and then run and monitor the user
         * program.
         * @param args
         * @throws Exception
         */
        public static void main(String[] args) throws Exception
        {
            // Open the console and re-direct standard channels to it.
            RConsole.open();
            System.setErr(RConsole.getPrintStream());
            System.setOut(RConsole.getPrintStream());
            // now create and run the monitor.
            new Monitor().monitorEvents();
        }
    }
}



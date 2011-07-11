package java.lang;
import lejos.nxt.NXTEvent;
import java.util.Vector;
import lejos.util.Delay;

/**
 * This is an internal system class that implements the system shutdown process.
 * Classes which require some form of cleanup when the program is about to exit
 * can register a system shutdown hook which will be run via this code when the
 * system shuts down. System shutdown can be triggered by one of the following
 * 1) A call to the exit function
 * 2) The last none daemon thread exiting
 * 3) A user generated shutdown interrupt (ENTER+ESCAPE)
 * Note: Some care has been taken to minimise the impact of this code on
 * programs that do not require shutdown hooks. In particular the event wait
 * thread is not created unless a shutdown hook is in place. This means that
 * the associated collection, and collection classes will not be loaded.
 * @author andy
 *
 */
class Shutdown
{
    private final static int SHUTDOWN_EVENT = 1;
    private volatile static ShutdownThread singleton;
    private volatile static boolean running = false;
    private static int exitCode = 0;
    private static NXTEvent event = null;

    static class ShutdownThread extends Thread
    {
        // Note we use a Vector, because other leJOS classes (like Bluetooth use Vector, which keeps our
        // code smaller. Arguably we should switch all of these uses to be an ArrayList
        private Vector<Thread> hooks = new Vector<Thread>();
        
        private ShutdownThread()
        {
            setPriority(Thread.MAX_PRIORITY-1);
            event = NXTEvent.allocate(NXTEvent.SYSTEM, SHUTDOWN_EVENT, 1000);
            setDaemon(true);
            this.start();
        }

        /**
         * This thread waits for the system shutdown event, and then proceeds to
         * start the actual hooks, it then waits for these hook threads to complete
         * before halting the program.
         */
        @Override
        public void run()
        {
            // We wait to be told to shut the system down
            try {
                event.waitEvent(NXTEvent.WAIT_FOREVER);
            }
            catch (InterruptedException e)
            {
                // If we get interrupted just give up...
                return;
            }
            running = true;
            // make sure we continue to run, even if other threads exit.
            setDaemon(false);
            event.free();
            // Call each of the hooks in turn
            for(int i = 0; i < hooks.size(); i++)
                hooks.elementAt(i).start();
            // and now wait for them to complete
            for(int i = 0; i < hooks.size(); i++)
                try {
                    hooks.elementAt(i).join();
                } catch (InterruptedException e)
                {
                    // ignore and retry
                    i--;
                }
            // Now make the system exit
            exit(exitCode);
        }
    }
    
    private Shutdown()
    {
        //empty
    }
    
    /**
     * Terminate the application. Does not trigger the calling of shutdown hooks.
     */
    public static native void exit(int code);
    

    
    /**
     * Called to shutdown the system. If any shutdown hooks have been installed 
     * these will be run before the system terminates. This function will never return.
     */
    public static void shutdown(int code)
    {
        // If no singleton then no hooks, so simply exit
        if (singleton == null)
            exit(code);
        // If not already running notify it it to run
        if (!running)
        {
            exitCode = code;
            event.notifyEvent(SHUTDOWN_EVENT);
        }
        // Now wait for ever for the system to shut down
        Delay.msDelay(NXTEvent.WAIT_FOREVER);
   }

    /**
     * Install a shutdown hook. Can only be called before system shutdown has
     * started.
     * @param hook
     */
    public static void addShutdownHook(Thread hook)
    {
        synchronized(Shutdown.class)
        {
            if (singleton == null)
                singleton = new ShutdownThread();
            if (running || hook.isAlive())
                throw(new IllegalStateException());
            if (singleton.hooks.indexOf(hook) >= 0)
                throw(new IllegalArgumentException());
            singleton.hooks.addElement(hook);            
        }
    }

    /**
     * Remove a shutdown hook.
     * @param hook item to be removed
     * @return true iff the hook is actually removed
     */
    public static boolean removeShutdownHook(Thread hook)
    {
        synchronized(Shutdown.class)
        {
            if (singleton == null) 
                return false;
            if (running)
                throw(new IllegalStateException());
            return singleton.hooks.removeElement(hook);
        }
    }

}

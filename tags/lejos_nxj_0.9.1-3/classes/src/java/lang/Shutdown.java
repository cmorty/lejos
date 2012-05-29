package java.lang;
import lejos.nxt.NXTEvent;

import java.util.ArrayList;
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
    //TODO running is only partly protected by synchronized(Shutdown.class){}, discuss with Andy 
    private volatile static boolean running = false;
    private static int exitCode = 0;

    static class ShutdownThread extends Thread
    {
        private final NXTEvent event;
        private ArrayList<Thread> hooks = new ArrayList<Thread>();
        
        private ShutdownThread()
        {
            this.setPriority(Thread.MAX_PRIORITY-1);
            event = NXTEvent.allocate(NXTEvent.SYSTEM, SHUTDOWN_EVENT, 1000);
            this.setDaemon(true);
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
                event.free();
                return;
            }
            // make sure we continue to run, even if other threads exit.
            setDaemon(false);
            running = true;
            // Call each of the hooks in turn
            for(int i = 0; i < hooks.size(); i++)
                hooks.get(i).start();
            // and now wait for them to complete
            for(int i = 0; i < hooks.size(); i++)
                try {
                    hooks.get(i).join();
                } catch (InterruptedException e)
                {
                    // ignore and retry
                    i--;
                }
            // Now make the system exit
            halt(exitCode);
        }
    }
    
    private Shutdown()
    {
        //empty
    }
    
    /**
     * Terminate the application. Does not trigger the calling of shutdown hooks.
     */
    public static native void halt(int code);
    
    /**
     * Tell the system that we want to shutdown. Calling this will trigger the running
     * of any shutdown hooks. If no hooks are installed the system will simply terminate.
     */
    private static native void shutdown();
    

    
    /**
     * Called to shutdown the system. If any shutdown hooks have been installed 
     * these will be run before the system terminates. This function will never return.
     */
    public static void shutdown(int code)
    {
        // If no singleton then no hooks, so simply exit
        if (singleton == null)
            halt(code);
        if (!running)
        {
            // Not already running the shutdown code, so go do it
            exitCode = code;
            shutdown();
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
            singleton.hooks.add(hook);            
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
            return singleton.hooks.remove(hook);
        }
    }

}

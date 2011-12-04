package java.lang;

/**
 * Minimalist version of the standard Java Runtime class.
 * @author Paul Andrews
 */
public class Runtime {
  private static Runtime singleton;

  /**
   * Private so no one but us can create one.
   */
  private Runtime()
  {
  }

  /**
   * Get the single instance of us.
   */
  public static Runtime getRuntime()
  {
    if (singleton == null) {
      singleton = new Runtime();
    }
    return singleton;
  }  
	
  /**
   * Return the amount of free memory.on the heap
   *
   * @return the free memory in bytes
   */
  public native long freeMemory();
  
  /**
   * Return the size of the heap in bytes.
   *
   * @return the free memory in bytes
   */
  public native long totalMemory();
  
  /**
   * Terminate the application.
   */
  public void exit(int code)
  {
      Shutdown.shutdown(code);
  }
  
  /**
   * Terminate the application immediately
   */
  public void halt(int code)
  {
      Shutdown.halt(code);
  }

  /**
   * Add a shutdown hook. Shutdown hooks are run just before the VM terminates.
   * @param hook
   */
  public void addShutdownHook(Thread hook)
  {
      Shutdown.addShutdownHook(hook);
  }

  /**
   * Remove a previously installed shutdown hook from the system.
   * @param hook
   * @return true if the hook was removed false if not
   */
  public boolean removeShutdownHook(Thread hook)
  {
      return Shutdown.removeShutdownHook(hook);
  }
}

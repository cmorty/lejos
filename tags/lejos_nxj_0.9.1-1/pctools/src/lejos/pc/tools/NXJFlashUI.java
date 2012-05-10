package lejos.pc.tools;

/**
 * Interface to provide a notification UI mechanism for the flash update process.
 * @author andy
 */
public interface NXJFlashUI
{
    /**
     * Report the progress of an ongoing operation.
     * @param msg Message about the operation
     * @param percent percentage complete.
     */
    public void progress(String msg, int percent);

    /**
     * Output a status message about an ongoing operation.
     * @param msg
     */
    public void message(String msg);
}

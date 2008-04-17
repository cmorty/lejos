package lejos.devices;
// package lejos.devices; // UNCOMMENT

/**
 * This interface is for classes that wish to receive keyboard events.
 *
 * @author BB
 * @see Keyboard
 * @see KeyEvent
 * @since 0.6
 * @status updated to 0.6
 */
public interface KeyListener {
  /**
   * This method is called when a key is typed.  A key is considered typed
   * when it and all modifiers have been pressed and released, mapping to
   * a single virtual key.
   *
   * @param event the <code>KeyEvent</code> indicating that a key was typed
   */
  void keyTyped(KeyEvent event);

  /**
   * This method is called when a key is pressed.
   *
   * @param event the <code>KeyEvent</code> indicating the key press
   */
  void keyPressed(KeyEvent event);

  /**
   * This method is called when a key is released.
   *
   * @param event the <code>KeyEvent</code> indicating the key release
   */
  void keyReleased(KeyEvent event);
}
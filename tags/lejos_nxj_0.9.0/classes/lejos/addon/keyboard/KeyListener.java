package lejos.addon.keyboard;
// package lejos.devices; // UNCOMMENT

/**
 * This interface is for classes that wish to receive keyboard events.
 *
 * @author BB
 * @see Keyboard
 * @see KeyEvent
 * @since 0.6
 */
public interface KeyListener {
  /**
   * This method is called when a key is typed.  A key is considered typed
   * when it and all modifiers have been pressed and released, mapping to
   * a single virtual key. If a key is held down, this event will repeat.
   *
   * @param event the <code>KeyEvent</code> indicating that a key was typed
   */
  void keyTyped(KeyEvent event);

  /**
   * This method is called when a key is pressed. A key is the unshifted/unmodified key
   * such as '/' (there can be no '?' key press). In standard Java it will repeat
   * if the key is held down, but in leJOS it is only called once when a key if pressed
   * as this seemed to be more useful.
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
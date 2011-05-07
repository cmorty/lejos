package lejos.robotics;

/*
 * WARNING: THIS CLASS IS SHARED BETWEEN THE classes AND pccomms PROJECTS.
 * DO NOT EDIT THE VERSION IN pccomms AS IT WILL BE OVERWRITTEN WHEN THE PROJECT IS BUILT.
 */

/**
 * Base motor interface. Contains basic movement commands.
 *
 */
public interface BaseMotor {

	/**
	   * Causes motor to rotate forward until stop is called.
	   */
	  void forward();

	  /**
	   * Causes motor to rotate backwards until stop is called.
	   */
	  void backward();

	  /**
	   * Causes motor to stop immediately. It will resist any further motion. Cancels any rotate() orders in progress.
	   */
	  void stop();

	  /**
	   * Motor loses all power, causing the rotor to float freely to a stop.
	   * This is not the same as stopping, which locks the rotor.
	   */
	  public void flt();

	  /**
	   * Return if the motor is moving.
	   *
	   * @return true if the motor is currently in motion
	   */
	  // TODO: Possibly part of Encoder interface? Depends if encoder used to determine this.
	  boolean isMoving();

}
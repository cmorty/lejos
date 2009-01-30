package lejos.localization;

import lejos.navigation.*;
import lejos.nxt.Motor;

/**
 * An abstract extension to TachoNavigator that uses a map and a set of particles
 * to implement the Monte Carlo Localization algorithm to estimate the pose
 * of the robot as it moves about.
 * 
 * Note that the navigator uses its own local coordinates relative to the robot's
 * starting position, whereas the estimated pose is in global coordinates, as used
 * by the map.
 * 
 * This class must be extended and the takeReadings method implemented.
 * 
 * Note that only travel and rotate methods update the particle set.
 * 
 * @author Lawrie Griffiths
 *
 */
public abstract class TachoLocalizer extends SimpleNavigator implements Localizer{

  public TachoLocalizer(Map map, int numParticles, float wheelDiameter, float trackWidth,
			Motor leftMotor, Motor rightMotor, float projection, boolean reverse
			) {
    super(wheelDiameter, trackWidth, leftMotor, rightMotor, reverse);
    this.projection = projection;
    this.numParticles = numParticles;
    this.map = map;
    particles = new ParticleSet(map, numParticles);
  }

  protected RangeReadings readings = new RangeReadings();
  protected float projection;
  protected Map map;
  protected int numParticles;
  protected ParticleSet particles;
  protected float angle, distance;
  protected Move mv = new Move(angle, distance);
  
  public abstract void takeReadings();
  
  public void rotate(float angle, boolean immediateReturn) {
	  this.angle = angle;
	  super.rotate(angle, immediateReturn);
  }
  
  public void travel(float distance, boolean immediateReturn) {
	  this.distance = distance;
	  super.travel(distance, immediateReturn);
  }
  
  /**
   * Update the robot position and apply it to all the particles.
   * Note that only travel and rotate methods update the particle set.
   * 
   */
  public void updatePosition() {
    super.updatePosition();
    if (angle != 0f || distance != 0f) {
    	mv.angle = angle;
    	mv.distance = distance;
        particles.applyMove(mv);
    }
    angle = 0f;
    distance = 0f;
  }

  /**
   * Get the forward projection of the robot
   * 
   * @return the distance from the range sensor to the front of the robot
   */
  public float getProjection() {
    return projection;
  }

  /**
   * Get the number of particles
   * 
   * @return the number of particles
   */
  public int numParticles() {
    return numParticles;
  }

  /**
   * Get the particle set
   * 
   * @return the particle set
   */
  public ParticleSet getParticles() {
    return particles;
  }

  /**
   * Get the map
   * 
   * @return the map
   */
  public Map getMap() {
    return map;
  }

  /**
   * Get the estimated position and angle of the robot.
   * 
   * @return the estimated pose
   */
  public Pose getEstimatedPose() {
    return particles.getEstimatedPose();
  }
  
  /**
   * Return readings 
   * 
   * @return the range readings
   */
  public RangeReadings getReadings() {
    return readings;
  }
}

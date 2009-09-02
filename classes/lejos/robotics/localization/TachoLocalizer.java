package lejos.robotics.localization;

import lejos.robotics.Pose;
import lejos.robotics.mapping.RangeMap;
import lejos.robotics.RangeReadings;
import lejos.robotics.navigation.*;
import lejos.robotics.Movement;
import lejos.robotics.Movement.MovementType;
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
public abstract class TachoLocalizer extends SimpleNavigator {	
  protected RangeReadings readings;
  protected float projection;
  protected RangeMap map;
  protected int numParticles;
  protected MCLParticleSet particles;
  protected float angle, distance;
  protected Movement mv;
  protected int numReadings;
  protected boolean isMoving;

  public TachoLocalizer(RangeMap map, int numParticles, int numReadings,
		    float wheelDiameter, float trackWidth,
			Motor leftMotor, Motor rightMotor, float projection, boolean reverse) {
    super(wheelDiameter, trackWidth, leftMotor, rightMotor, reverse);
    this.projection = projection;
    this.numParticles = numParticles;
    this.map = map;
    particles = new MCLParticleSet(map, numParticles, 0);
    this.numReadings = numReadings;
    readings = new RangeReadings(numReadings);
  }

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
    	mv = new Movement(MovementType.TRAVEL, angle, distance,isMoving);
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
  public MCLParticleSet getParticles() {
    return particles;
  }

  /**
   * Get the map
   * 
   * @return the map
   */
  public RangeMap getMap() {
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

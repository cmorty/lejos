package lejos.localization;

import lejos.ai.*;
import java.util.Random;

/**
 * Represents a particle for the particle filtering algorithm. The state of the
 * particle is the pose, which represents a possible pose of the robot.
 * 
 * The weight for a particle is set by taking a set of range readings using a
 * map of the environment, and comparing these ranges with those taken by the
 * robot. The weight represents the relative probability that the robot has this
 * pose. Weights are from 0 to 1.
 * 
 * @author Lawrie Griffiths
 * 
 */
public class Particle {
  // Constants
  private static final float TWO_SIGMA_SQUARED = 500f;
  private static final float DISTANCE_NOISE_FACTOR = 0.05f;
  private static final float ANGLE_NOISE_FACTOR = 0.01f;
  
  // Static variables
  private static int rangeReadingAngle = 45;
  private static float readings[] = new float[3];
  private static Random rand = new Random();
  
  // Instance variables
  private Pose pose;
  private float weight = 0;

  public Particle(Pose pose) {
    this.pose = pose;
  }

  /**
   * Set the weight for this particle
   * 
   * @param weight the weight of this particle
   */
  public void setWeight(float weight) {
    this.weight = weight;
  }

  /**
   * Return the weight of this particle
   * 
   * @return the weight
   */
  public float getWeight() {
    return weight;
  }

  /**
   * Return the pose of this particle
   * 
   * @return the pose
   */
  public Pose getPose() {
    return pose;
  }

  /**
   * Calculate the weight for this particle by comparing its readings with the
   * robot's readings
   * 
   * @param rr Robot readings
   */
  public void calculateWeight(RangeReadings rr, Map map) {
    weight = 1;

    takeReadings(map);

    for (int i = 0; i < RangeReadings.getNumReadings(); i++) {
      float myReading = readings[i];
      if (myReading < 0) { // Weight zero if any wall is out of range
        weight = 0;
        return;
      }
      float robotReading = rr.getRange(i);

      float diff = robotReading - myReading;
      weight *= (float) Math.exp(-(diff * diff) / TWO_SIGMA_SQUARED);
    }
  }
  
  /**
   * Calculate the theoretical readings
   * 
   * @param map the map of the environment
   */
  public void takeReadings(Map map) {
    // Take some theoretical readings
    readings[1] = map.range(pose);
    pose.angle = pose.angle - rangeReadingAngle;
    readings[0] = map.range(pose);
    pose.angle = pose.angle + rangeReadingAngle * 2;
    readings[2] = map.range(pose);
    pose.angle = pose.angle - rangeReadingAngle;
  }
  
  /**
   * Get a specific reading
   * 
   * @param i the index of the reading
   * @return the reading
   */
  public float getReading(int i) {
    return readings[i];
  }

  /**
   * Apply the robot's move to the particle with a bit of random noise
   * 
   * @param move the robot's move
   */
  public void applyMove(Move move) {
    float ym = (move.distance * ((float) Math.sin(Math.toRadians(pose.angle))));
    float xm = (move.distance * ((float) Math.cos(Math.toRadians(pose.angle))));

    pose.x += xm + (DISTANCE_NOISE_FACTOR * xm * rand.nextGaussian());
    pose.y += ym + (DISTANCE_NOISE_FACTOR * ym * rand.nextGaussian());
    pose.angle += move.angle
        + (ANGLE_NOISE_FACTOR * move.angle * rand.nextGaussian());
    pose.angle = (float) ((int) (pose.angle + 0.5f) % 360);
  }
}

package lejos.localization;

import java.util.Random;

/**
 * Represents a particle for the particle filtering algorithm. The state of the
 * particle is the pose, which represents a possible pose of the robot.
 * 
 * The weight for a particle is set by taking a set of theoretical range readings using a
 * map of the environment, and comparing these ranges with those taken by the
 * robot. The weight represents the relative probability that the robot has this
 * pose. Weights are from 0 to 1.
 * 
 * @author Lawrie Griffiths
 * 
 */
public class Particle {
  // Constants
  private static final float TWO_SIGMA_SQUARED = 250f;
  private static final float DISTANCE_NOISE_FACTOR = 0.02f;
  private static final float ANGLE_NOISE_FACTOR = 0.02f;
  
  // Static variables
  private static int numReadings = 3; // Must be odd so there is a forward reading
  private static float readings[];
  private static Random rand = new Random();
  private static int rangeReadingAngle = 45;
  private static int forwardReading = 1;
  
  // Instance variables (kept to minimum to allow maximum number of particles)
  private Pose pose;
  private float weight = 0;

  /**
   * Create a particle with a specific pose
   * 
   * @param pose the pose
   */
  public Particle(Pose pose) {
    this.pose = pose;
    readings = new float[numReadings];
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
    float startAngle = pose.angle;
    readings[forwardReading] = map.range(pose);
    for(int i=forwardReading-1;i>=0;i--) {
      pose.angle -= rangeReadingAngle;
      readings[i] = map.range(pose);
    }
    pose.angle = startAngle;
    for(int i=forwardReading+1;i<numReadings;i++) {
      pose.angle += rangeReadingAngle;
      readings[i] = map.range(pose);
    }
    pose.angle = startAngle;
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

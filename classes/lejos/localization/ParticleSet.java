package lejos.localization;
import java.awt.Rectangle;
import lejos.ai.*;

/**
 * Represents a particle set for the particle filtering algorithm.
 * 
 * @author Lawrie Griffiths
 * 
 */
public class ParticleSet {
  // Constants
  private static final int MAX_ITERATIONS = 1000;
  private static final float BIG_FLOAT = 10000f;
  
  /**
   * Minimum distance from a wall where the particle is placed.
   */
  public static final int BORDER = 20;
  
  // Instance variables
  private int numParticles;
  private Particle[] particles;
  private Map map;
  private float estimatedX, estimatedY, estimatedAngle;
  private float minX, maxX, minY, maxY;
  private float maxWeight;

  /**
   * Create a set of particles randomly distributed with the given map.
   * 
   * @param map the map of the enclosed environment
   */
  public ParticleSet(Map map, int numParticles) {
    this.map = map;
    this.numParticles = numParticles;
    particles = new Particle[numParticles];
    for (int i = 0; i < numParticles; i++) {
      particles[i] = generateParticle();
    }
  }

  /**
   * Generate a random particle within the mapped area.
   * 
   * @return the particle
   */
  private Particle generateParticle() {
    float x, y, angle;
    Rectangle bound = map.getBoundingRect();
    Rectangle innerRect = new Rectangle(bound.x + BORDER, bound.y + BORDER,
        bound.width - BORDER * 2, bound.height - BORDER * 2);

    // Generate x, y values in bounding rectangle
    for (;;) { // infinite loop that we break out of when we have
      // generated a particle within the mapped area
      x = innerRect.x + (((float) Math.random()) * innerRect.width);
      y = innerRect.y + (((float) Math.random()) * innerRect.height);

      if (map.inside(new Point(x, y))) break;
    }

    // Pick a random angle
    angle = ((float) Math.random()) * 360;

    return new Particle(new Pose(x, y, angle));
  }

  /**
   * Return the number of particles in the set
   * 
   * @return the number of particles
   */
  public int numParticles() {
    return numParticles;
  }

  /**
   * Get a specific particle
   * 
   * @param i the index of the particle
   * @return the particle
   */
  public Particle getParticle(int i) {
    return particles[i];
  }

  /**
   * Resample the set picking those with higher weights.
   * 
   * Note that the new set has multiple instances of the particles with higher
   * weights.
   * 
   * @return true iff lost
   */
  public boolean resample() {
    // Rename particles as oldParticles and create a new set
    Particle[] oldParticles = particles;
    particles = new Particle[numParticles];

    // Continually pick a random number and select the particles with
    // weights greater than or equal to it until we have a full
    // set of particles.
    int count = 0;
    int iterations = 0;
    resetEstimate();

    while (count < numParticles) {
      iterations++;
      if (iterations >= MAX_ITERATIONS) {
        System.out.println("Lost: count = " + count);
        if (count > 0) { // Set the rest to the first one
          for (int i = count; i < numParticles; i++) {
            particles[i] = new Particle(particles[0].getPose());
            particles[i].setWeight(0);
          }
          return false;
        } else { // Completely lost
          for (int i = 0; i < numParticles; i++) {
            particles[i] = generateParticle();
          }
          resetEstimate();
          return true;
        }
      }
      float rand = (float) Math.random();
      for (int i = 0; i < numParticles && count < numParticles; i++) {
        if (oldParticles[i].getWeight() >= rand) {
          Pose p = oldParticles[i].getPose();
          float x = p.x;
          float y = p.y;
          float angle = p.angle;

          estimatedX += x;
          estimatedY += y;
          estimatedAngle += angle;

          if (x < minX) minX = x;
          if (x > maxX) maxX = x;
          if (y < minY) minY = y;
          if (y > maxY) maxY = y;

          // Create a new instance of the particle and set its weight to zero
          particles[count] = new Particle(new Pose(x, y, angle));
          particles[count++].setWeight(oldParticles[i].getWeight());
        }
      }
    }
    estimatedX /= numParticles;
    estimatedY /= numParticles;
    estimatedAngle /= numParticles;
    return false;
  }

  /**
   * Calculate the weight for each particle
   * 
   * @param rr the robot range readings
   */
  public void calculateWeights(RangeReadings rr, Map map) {
    maxWeight = 0f;
    for (int i = 0; i < numParticles; i++) {
      particles[i].calculateWeight(rr, map);
      float weight = particles[i].getWeight();
      if (weight > maxWeight) {
        maxWeight = weight;
      }
    }
    System.out.println("Max weight = " + maxWeight);
  }

  /**
   * Apply a move to each particle
   * 
   * @param move the move to apply
   */
  public void applyMove(Move move) {
    resetEstimate();
    for (int i = 0; i < numParticles; i++) {
      particles[i].applyMove(move);
    }
  }

  /**
   * Get the estimated pose of the robot
   * 
   * @return the estimated pose
   */
  public Pose getEstimatedPose() {
    return new Pose(estimatedX, estimatedY, estimatedAngle);
  }

  /**
   * Get the minimum X value of the estimated position
   * 
   * @return the minimum X value
   */
  public float getMinX() {
    return minX;
  }

  /**
   * Get the maximum X value of the estimated position
   * 
   * @return the maximum X value
   */
  public float getMaxX() {
    return maxX;
  }

  /**
   * Get the minimum Y value of the estimated position
   * 
   * @return the minimum Y value
   */
  public float getMinY() {
    return minY;
  }

  /**
   * Get the maximum Y value of the estimated position
   * 
   * @return the maximum Y value
   */
  public float getMaxY() {
    return maxY;
  }

  /**
   * Reset the estimated position to unknown
   */
  public void resetEstimate() {
    estimatedX = 0;
    estimatedY = 0;
    estimatedAngle = 0;
    maxX = 0;
    maxY = 0;
    minX = BIG_FLOAT;
    minY = BIG_FLOAT;
  }
  
  /**
   * The highest weight of any particle
   * 
   * @return the highest weight
   */
  public float getMaxWeight() {
    return maxWeight;
  }
  
  /**
   * Find the index of the particle closest to a given co-ordinates
   * 
   * @param x the x-coordinate
   * @param y the y-coordinate
   * @return the index
   */
  public int findClosest(float x, float y) {
    float minDistance = BIG_FLOAT;
    int index = -1;
    for (int i = 0; i < numParticles; i++) {
      Pose pose = particles[i].getPose();
      float distance = (float) Math.sqrt((double) (
          (pose.x - x) * (pose.x - x)) + 
          ((pose.y - y) * (pose.y - y)));
      if (distance < minDistance) {
        minDistance = distance;
        index = i;
      }
    }
    return index;
  }
}

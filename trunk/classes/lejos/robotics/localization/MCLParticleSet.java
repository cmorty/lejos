package lejos.robotics.localization;

import java.awt.Rectangle;
import lejos.geom.*;
import java.io.*;
import lejos.robotics.*;
import lejos.robotics.mapping.RangeMap;
import lejos.robotics.proposal.Movement;

/*
 * WARNING: THIS CLASS IS SHARED BETWEEN THE classes AND pccomms PROJECTS.
 * DO NOT EDIT THE VERSION IN pccomms AS IT WILL BE OVERWRITTEN WHEN THE PROJECT IS BUILT.
 */

/**
 * Represents a particle set for the particle filtering algorithm.
 * 
 * @author Lawrie Griffiths
 * 
 */
public class MCLParticleSet {
  // Constants 
  private static final float BIG_FLOAT = 10000f;
  
  // Static variables
  public static int maxIterations = 1000;
  
  // Instance variables
  private float twoSigmaSquared = 250f;
  private float distanceNoiseFactor = 0.02f;
  private float angleNoiseFactor = 0.02f;
  private int numParticles;
  private MCLParticle[] particles;
  private RangeMap map;
  private float estimatedX, estimatedY, estimatedAngle;
  private float minX, maxX, minY, maxY;
  private boolean validEstimate;
  private float maxWeight;
  private int border = 10;	// The minimum distance from the edge of the map
  							// to generate a particle.
  /**
   * Create a set of particles randomly distributed with the given map.
   * 
   * @param map the map of the enclosed environment
   */
  public MCLParticleSet(RangeMap map, int numParticles, int border) {
    this.map = map;
    this.numParticles = numParticles;
    this.border = border;
    particles = new MCLParticle[numParticles];
    for (int i = 0; i < numParticles; i++) {
      particles[i] = generateParticle();
    }
    resetEstimate();
  }

  /**
   * Generate a random particle within the mapped area.
   * 
   * @return the particle
   */
  private MCLParticle generateParticle() {
    float x, y, angle;
    Rectangle bound = map.getBoundingRect();
    Rectangle innerRect = new Rectangle(bound.x + border, bound.y + border,
        bound.width - border * 2, bound.height - border * 2);

    // Generate x, y values in bounding rectangle
    for (;;) { // infinite loop that we break out of when we have
               // generated a particle within the mapped area
      x = innerRect.x + (((float) Math.random()) * innerRect.width);
      y = innerRect.y + (((float) Math.random()) * innerRect.height);

      if (map.inside(new Point(x, y))) break;
    }

    // Pick a random angle
    angle = ((float) Math.random()) * 360;

    return new MCLParticle(new Pose(x, y, angle));
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
  public MCLParticle getParticle(int i) {
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
    MCLParticle[] oldParticles = particles;
    particles = new MCLParticle[numParticles];

    // Continually pick a random number and select the particles with
    // weights greater than or equal to it until we have a full
    // set of particles.
    int count = 0;
    int iterations = 0;

    while (count < numParticles) {
      iterations++;
      if (iterations >= maxIterations) {
        System.out.println("Lost: count = " + count);
        if (count > 0) { // Duplicate the ones we have so far
          for (int i = count; i < numParticles; i++) {
            particles[i] = new MCLParticle(particles[i % count].getPose());
            particles[i].setWeight(particles[i % count].getWeight());
          }
          return false;
        } else { // Completely lost - generate a new set of particles
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
          float x = p.getX();
          float y = p.getY();
          float angle = p.getHeading();

          // Create a new instance of the particle and set its weight
          particles[count] = new MCLParticle(new Pose(x, y, angle));
          particles[count++].setWeight(oldParticles[i].getWeight());
        }
      }
    }
    estimatePose();
    return false;
  }
  
  /**
   * Estimate pose from weighted average of the particles
   */
  private void estimatePose() {
    resetEstimate();
    float totalWeights = 0;
    
    for (int i = 0; i < numParticles; i++) {
	  Pose p = particles[i].getPose();
	  float x = p.getX();
      float y = p.getY();
      float weight = particles[i].getWeight();
	  
      estimatedX += (x * weight);
      estimatedY += (y * weight);
      estimatedAngle += (p.getHeading() * weight);
      totalWeights += weight;

      if (x < minX) minX = x;
      if (x > maxX) maxX = x;
      if (y < minY) minY = y;
      if (y > maxY) maxY = y;
    }  
    estimatedX /= totalWeights;
    estimatedY /= totalWeights;
    estimatedAngle /= totalWeights;
    validEstimate = true;
  }

  /**
   * Calculate the weight for each particle
   * 
   * @param rr the robot range readings
   */
  public void calculateWeights(RangeReadings rr, RangeMap map) {
    maxWeight = 0f;
    for (int i = 0; i < numParticles; i++) {
      particles[i].calculateWeight(rr, map, twoSigmaSquared);
      float weight = particles[i].getWeight();
      if (weight > maxWeight) {
        maxWeight = weight;
      }
    }
  }
  
  public void printMaxWeight() {
    System.out.println("Max = " + maxWeight);
  }

  /**
   * Apply a move to each particle
   * 
   * @param move the move to apply
   */
  public void applyMove(Movement move) {
	maxWeight = 0f;
    for (int i = 0; i < numParticles; i++) {
      particles[i].applyMove(move, distanceNoiseFactor, angleNoiseFactor);
    }
    estimatePose();
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
    Rectangle bound = map.getBoundingRect();
    minX = bound.x + bound.width;
    minY = bound.y + bound.height;
    maxX = bound.x;
    maxY = bound.y;
    validEstimate = false;
  }
  
  /**
   * Return the minimum rectangle enclosing all the particles
   * 
   * @return the rectangle
   */
  public Rectangle getErrorRect() {
	  if (!validEstimate) return map.getBoundingRect();
	  else {
		  return new Rectangle((int) minX, (int) minY, 
				               (int) (maxX-minX), (int) (maxY-minY));
	  }
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
   * Set border where no particles should be generated
   * 
   * @param border the border 
   */
  public void setBorder(int border) {
    this.border = border;
  }
  
  /**
   * Set the standard deviation for the sensor probability model
   * @param sigma the standard deviation
   */
  public void setSigma(float sigma) {
    twoSigmaSquared = 2 * sigma * sigma;
  }
  
  /**
   * Set the distance noise factor
   * @param factor the distance noise factor
   */
  public void setDistanceNoiseFactor(float factor) {
    distanceNoiseFactor = factor;
  }
  
  /**
   * Set the distance angle factor
   * @param factor the distance angle factor
   */
  public void setAngleNoiseFactor(float factor) {
    angleNoiseFactor = factor;
  }
 
  /**
   * Set the maximum iterations for the resample algorithm
   * @param max the maximum iterations
   */
  public void setMaxIterations(int max) {
    maxIterations = max;
  }
  
  /**
   * Find the index of the particle closest to a given co-ordinates.
   * This is used for diagnostic purposes.
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
          (pose.getX() - x) * (pose.getX() - x)) + 
          ((pose.getY() - y) * (pose.getY() - y)));
      if (distance < minDistance) {
        minDistance = distance;
        index = i;
      }
    }
    return index;
  }
  
  /**
   * Serialize the particle set to a data output stream
   * 
   * @param dos the data output stream
   * @throws IOException
   */
  public void dumpParticles(DataOutputStream dos) throws IOException {
      dos.writeInt(numParticles());
      for (int i = 0; i < numParticles(); i++) {
          MCLParticle part = getParticle(i);
          Pose pose = part.getPose();
          float weight = part.getWeight();
          dos.writeFloat(pose.getX());
          dos.writeFloat(pose.getY());
          dos.writeFloat(pose.getHeading());
          dos.writeFloat(weight);
          dos.flush();
      }
  }
  
  /**
   * Load serialized particles from a data input stream
   * @param dis the data input stream
   * @throws IOException
   */
  public void loadParticles(DataInputStream dis) throws IOException {
	numParticles = dis.readInt();
    particles = new MCLParticle[numParticles];
    for (int i = 0; i < numParticles; i++) {
      float x = dis.readFloat();
      float y = dis.readFloat();
      float angle = dis.readFloat();
      Pose pose = new Pose(x, y, angle);
      particles[i] = new MCLParticle(pose);
      particles[i].setWeight(dis.readFloat());
    }  
  }
  
  /**
   * Dump the serialized estimate of pose to a data output stream
   * @param dos the data output stream
   * @throws IOException
   */
  public void dumpEstimation(DataOutputStream dos) throws IOException {
      Pose pose = getEstimatedPose();
      float minX = getMinX();
      float maxX = getMaxX();
      float minY = getMinY();
      float maxY = getMaxY();

      dos.writeFloat(pose.getX());
      dos.writeFloat(pose.getY());
      dos.writeFloat(pose.getHeading());
      dos.writeFloat(minX);
      dos.writeFloat(maxX);
      dos.writeFloat(minY);
      dos.writeFloat(maxY);
      dos.flush();
  }
  
  /**
   * Load serialized estimated pose from a data input stream
   * @param dis the data imput stream
   * @throws IOException
   */
  public void loadEstimation(DataInputStream dis) throws IOException {
      estimatedX = dis.readFloat();
      estimatedY = dis.readFloat();
      estimatedAngle = dis.readFloat();
      minX = dis.readFloat();
      maxX = dis.readFloat();
      minY = dis.readFloat();
      maxY = dis.readFloat();
  }
  
  /**
   * Find the closest particle to specified coordinates and dump its
   * details to a data output stream.
   * 
   * @param dos the data output stream
   * @param x the x-coordinate
   * @param y the y-coordinate
   * @throws IOException
   */
  public void dumpClosest(RangeReadings rr, RangeMap map, DataOutputStream dos, float x, float y) throws IOException {
      int closest = findClosest(x, y);
      MCLParticle p = getParticle(closest);
      dos.writeInt(closest);
      dos.writeFloat(p.getReading(0, rr, map));
      dos.writeFloat(p.getReading(1, rr, map));
      dos.writeFloat(p.getReading(2, rr, map));
      dos.writeFloat(p.getWeight());
      dos.flush();
  }
}

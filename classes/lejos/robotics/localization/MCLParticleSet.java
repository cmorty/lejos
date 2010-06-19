package lejos.robotics.localization;

import lejos.nxt.comm.RConsole;
//import lejos.util.Datalogger;
import lejos.nxt.*;

import java.awt.Rectangle;
import lejos.geom.*;
import java.io.*;
import lejos.robotics.*;
import lejos.robotics.mapping.RangeMap;
import lejos.robotics.Move;
import java.util.Random;



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
  private static float twoSigmaSquared = 200f; // was 250
  // Instance variables
  private float distanceNoiseFactor = 0.1f;
  private float angleNoiseFactor = 2.5f;
  private int numParticles;
  private MCLParticle[] particles;
  private RangeMap map;
  private float maxWeight, totalWeight;
  private int border = 10;	// The minimum distance from the edge of the map
  private Random random = new Random(); // to generate a particle.
  private Rectangle boundingRect;
  private boolean debug = false;
  
  /**
   * Create a set of particles randomly distributed with the given map.
   * 
   * @param map the map of the enclosed environment
   */
  public MCLParticleSet(RangeMap map, int numParticles, int border)
  {
    this.map = map;
    this.numParticles = numParticles;
    this.border = border;
    boundingRect = map.getBoundingRect();
    particles = new MCLParticle[numParticles];
    for (int i = 0; i < numParticles; i++) {
      particles[i] = generateParticle();
    }
//    resetEstimate();
  }
/**
 * generates a circular cloud of particles centered on initialPose with random 
 * radius  and angle
 * @param map
 * @param numParticles
 * @param initialPose
 * @param radiusNoise  standard deviation of radius
 * @param headingNoise standard deviation of heading noise
 */
  public MCLParticleSet(RangeMap map, int numParticles, Pose initialPose,
          float radiusNoise, float headingNoise)
  {
    this.map = map;
    this.numParticles = numParticles;
    border = 0;
    boundingRect = map.getBoundingRect();
    particles = new MCLParticle[numParticles];
    for (int i = 0; i < numParticles; i++)
    {
      float rad = radiusNoise * (float) random.nextGaussian();
      float theta = (float) (2 * Math.PI * Math.random());
      float x = initialPose.getX() + rad * (float) Math.cos(theta);
      float y = initialPose.getY() + rad * (float) Math.sin(theta);
      float heading = initialPose.getHeading() + headingNoise * (float) random.nextGaussian();
      particles[i] = new MCLParticle((new Pose(x,y,heading)));
    }
  }

  /**
   * Generate a random particle within the mapped area.
   * 
   * @return the particle
   */
  private MCLParticle generateParticle() {
    float x, y, angle;
    Rectangle innerRect = new Rectangle(boundingRect.x + border, boundingRect.y + border,
        boundingRect.width - border * 2, boundingRect.height - border * 2);

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
   * Set system out debugging on or off
   * 
   * @param debug true to set debug, false to set it off
   */
  public void setDebug(boolean debug) {
	  this.debug = debug;
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
      if (iterations >= maxIterations)
      {
        if (debug) System.out.println("Lost: count = " + count);
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
//          resetEstimate();
          return true;
        }
      }
      float rand = (float) Math.random();
      for (int i = 0; i < numParticles && count < numParticles; i++)
      {
        if (oldParticles[i].getWeight() >= rand)
        {
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
    return false;
  }

  /**
   * Calculate the weight for each particle
   * 
   * @param rr the robot range readings
   */
  public boolean  calculateWeights(RangeReadings rr, RangeMap map) {
  
   if(rr.incomplete()) return false;
    maxWeight = 0f;
    totalWeight = 0f;
    float minWeight = (float) .001f/numParticles;
    for (int i = 0; i < numParticles; i++) {
      particles[i].calculateWeight(rr, map, twoSigmaSquared);
      float weight = particles[i].getWeight();
      if(weight < minWeight) weight = minWeight;
      if (weight > maxWeight) {
        maxWeight = weight;
      }
      totalWeight += weight;
    }
    if(maxWeight < 0.1f )return false;
    return true;
  }
  
  /**
   * Apply a move to each particle
   * 
   * @param move the move to apply
   */
  public void applyMove(Move move) {
//    RConsole.println("particles applyMove "+move.getMoveType()+" NrP "+numParticles);
	maxWeight = 0f;
    for (int i = 0; i < numParticles; i++) {
      particles[i].applyMove(move, distanceNoiseFactor, angleNoiseFactor);
    }
//    RConsole.println("particles apply move est pose ");
  }
  
//  /**
//   * Return the minimum rectangle enclosing all the particles
//   *
//   * @return the rectangle
//   */
//  public Rectangle getErrorRect() {
//	  return new Rectangle((int) minX, (int) minY,
//			               (int) (maxX-minX), (int) (maxY-minY));
//  }
  
  /**
   * The highest weight of any particle
   * 
   * @return the highest weight
   */
  public float getMaxWeight() {
    return maxWeight;
  }
  
  /**
   * Get the border where particles should not be generated
   * 
   * @return the border
   */
  public float getBorder() {
	  return border;
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
  public static void setSigma(float sigma) {
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

//  public void logParticles(Datalogger log)
//  {
//    for (int i = 0; i < numParticles; i++)
//    {
//         MCLParticle part = getParticle(i);
//          Pose pose = part.getPose();
//          float weight = part.getWeight();
//          log.writeLog(pose.getX(),pose.getY(),pose.getHeading(),weight);
//    }
//    log.writeLog(99,99,99,99);
//  }

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

  public Pose getEstimatedPose()
  {
    float totalWeights = 0;
    float estimatedX = 0;
    float estimatedY = 0;
    float estimatedAngle = 0;
     for (int i = 0; i < numParticles; i++)
    {
      Pose p = getParticle(i).getPose();
      float x = p.getX();
      float y = p.getY();
      float weight = getParticle(i).getWeight();
       estimatedX += (x * weight);
      estimatedY += (y * weight);
      float head = p.getHeading();
      estimatedAngle += (head * weight);
      totalWeights += weight;
     }
    estimatedX /= totalWeights;
     estimatedY /= totalWeights;
     estimatedAngle /= totalWeights;
     return new Pose(estimatedX,estimatedY,estimatedAngle);
  }
//  /**
//   * Dump the serialized estimate of pose to a data output stream
//   * @param dos the data output stream
//   * @throws IOException
//   */
//  public void dumpEstimation(DataOutputStream dos) throws IOException {
//      Pose pose = getEstimatedPose();
//      float minX = getMinX();
//      float maxX = getMaxX();
//      float minY = getMinY();
//      float maxY = getMaxY();
//
//      dos.writeFloat(pose.getX());
//      dos.writeFloat(pose.getY());
//      dos.writeFloat(pose.getHeading());
//      dos.writeFloat(minX);
//      dos.writeFloat(maxX);
//      dos.writeFloat(minY);
//      dos.writeFloat(maxY);
//      dos.writeFloat((float)varX);
//      dos.writeFloat((float)varY);
//      dos.writeFloat((float)varH);
//      dos.flush();
//  }
  
  /**
   * Load serialized estimated pose from a data input stream
   * @param dis the data input stream
   * @throws IOException
   */
//  public void loadEstimation(DataInputStream dis) throws IOException {
//      estimatedX = dis.readFloat();
//      estimatedY = dis.readFloat();
//      estimatedAngle = dis.readFloat();
//      minX = dis.readFloat();
//      maxX = dis.readFloat();
//      minY = dis.readFloat();
//      maxY = dis.readFloat();
//  }
  
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

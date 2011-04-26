package lejos.robotics.localization;

import lejos.geom.*;
import java.io.*;
import lejos.robotics.*;
import lejos.robotics.mapping.RangeMap;
import lejos.robotics.navigation.Move;
import lejos.robotics.navigation.Pose;

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
  private float twoSigmaSquared = 200f; // was 250 200
  // Instance variables
  private float distanceNoiseFactor = 0.1f;
  private float angleNoiseFactor = 2f;
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
    normalize();
  }
/**
 * Generates a circular cloud of particles centered on initialPose with random 
 * radius  and angle
 * @param map the map
 * @param numParticles the number of particles
 * @param border the border
 * @param readings the set of readings
 * @param divisor the divisor
 * @param minWeight the minimum weight
 */
  public MCLParticleSet(RangeMap map, int numParticles, int border,
          RangeReadings readings, float divisor, float minWeight)
  {
     int k = 1;
    this.map = map;
    this.numParticles = numParticles;
    this.border = border;
    boundingRect = map.getBoundingRect();
    particles = new MCLParticle[numParticles];
    MCLParticle particle;
    int i = 0;
    while ( i < numParticles)
    {
      k++;
      particle = generateParticle();
      particle.calculateWeight(readings, map, divisor);
      if(minWeight < particle.getWeight())
      {
        particles[i]=particle;
        i++;
      }
    }
    normalize();
    System.out.println("particles generated " + k);
  }
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
    normalize();
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
        if (debug){
          System.out.println("Lost: count = " + count);
        }

        if (count > 0) { // Duplicate the ones we have so far
          for (int i = count; i < numParticles; i++) {
            particles[i] = new MCLParticle(particles[i % count].getPose());
            particles[i].setWeight(particles[i % count].getWeight());
          }
          normalize();
          return false;
        } else { // Completely lost - generate a new set of particles
          for (int i = 0; i < numParticles; i++) {
            particles[i] = generateParticle();
          }
//          resetEstimate();
          normalize();
          return true;
        }
      }
      float rand = maxWeight *(float) Math.random();
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
    Thrun Low variance resampling algorithm pg 110 table 4.4
   */
  public void resampleLowVar()
  {
    // does not work well ??

    MCLParticle[] oldParticles = particles; // line 2
    particles = new MCLParticle[numParticles];
    float np_1 = 1.0f/ numParticles;
    float r = random.nextFloat() * np_1;// line 3
    float c = oldParticles[0].getWeight()/totalWeight;//. line 4
    int k = 0; 
    int iold = 0;

    int i=  0; // line 5

    for (int m = 0; m < numParticles; m++)// line 6
    {
      float u = r + m * np_1; // line 7
      while (u > c)// line 8
      {
        i++;  // line 9
        if (i == numParticles)
        {
          i = 0;// avoid array out of bounds
        }
        float w = oldParticles[i].getWeight() / totalWeight;
        c += oldParticles[i].getWeight() / totalWeight; // line 10
      }// end while  // line 11
      particles[m] = oldParticles[i];// line 12
      if (i != iold)
      {
        iold = i;
        k++;
      }
    } //  end  for  line 13
  }


  /**
   * Calculate the weight for each particle
   *  Normalize to sum = 1.0
   * @param rr the robot range readings
   */
  public boolean  calculateWeights(RangeReadings rr, RangeMap map) {
   if(debug) System.out.print(" calc weights using ranges:  "+rr.getRange(0)+" "+rr.getRange(1)+" "
           +rr.getRange(2)+" A "
           +rr.getAngle(0)+" "+rr.getAngle(1)+" "+rr.getAngle(2));
   if(rr.incomplete())  
   {
     if(debug) System.out.println("range set incomplete");
     return false;
   }
    maxWeight = 0f;
    for (int i = 0; i < numParticles; i++) {
      particles[i].calculateWeight(rr, map, twoSigmaSquared);
    }
    normalize();
    for (int i = 0; i < numParticles; i++) {
      float weight = particles[i].getWeight();
      if (weight > maxWeight) maxWeight = weight;
      }

   if(debug) System.out.println("Calc Weights Max wt " +maxWeight);
     if(maxWeight < .1)return false;
// normalize so maximum weight = 1  for mor efficient resampling.
// TO DO   normalize to sum of weights = 1;    multiply new by old'
    for (int i = 0; i < numParticles; i++){
      particles[i].setWeight(particles[i].getWeight()/ maxWeight);
    } 
    return true;
  }
  
  /**
   * Apply a move to each particle
   * 
   * @param move the move to apply
   */
  public void applyMove(Move move) {
    if(debug)System.out.println("particles applyMove "+move.getMoveType());
	maxWeight = 0f;
    for (int i = 0; i < numParticles; i++) {
      particles[i].applyMove(move, distanceNoiseFactor, angleNoiseFactor);
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
  private void normalize()
  {
    totalWeight  = 0;
    for (int i = 0; i< numParticles; i++ )
    {
       totalWeight += particles[i].getWeight();
    }
     for (int i = 0; i< numParticles; i++ )
    {
       float w = particles[i].getWeight()/totalWeight;
       particles[i].setWeight(w);
    }
    totalWeight = 1;
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

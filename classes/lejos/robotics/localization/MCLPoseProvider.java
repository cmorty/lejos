package lejos.robotics.localization;


import lejos.robotics.mapping.RangeMap;
import lejos.robotics.navigation.Move;
import lejos.robotics.navigation.MoveListener;
import lejos.robotics.navigation.MoveProvider;
import lejos.robotics.navigation.Pose;
import lejos.robotics.RangeReadings;
import lejos.robotics.RangeScanner;
import java.awt.Rectangle;
import lejos.robotics.localization.PoseProvider;
import java.io.*;

import lejos.util.Stopwatch;

public class MCLPoseProvider implements PoseProvider, MoveListener
{

  private MCLParticleSet particles;
  private RangeScanner scanner;
  private RangeMap map;
  private int numParticles;
  private float _x, _y, _heading;
  private float minX, maxX, minY, maxY;
  private double varX, varY, varH;
  private boolean autoUpdate = true;
  private boolean updated;
  private Updater updater = new Updater();
  private int border;
  private boolean debug = false;
  boolean busy = false;
  Stopwatch sw = new Stopwatch();
  private float BIG_FLOAT = 1000000f;
  private RangeReadings readings;


  public MCLPoseProvider(MoveProvider mp, RangeScanner scanner,
          RangeMap map, int numParticles, int border)
  {
    this.numParticles = numParticles;
    this.border = border;
    particles = new MCLParticleSet(map, numParticles, border);
    this.scanner = scanner;
    this.map = map;
    if (mp != null) mp.addMoveListener(this);
    updated = false;
    updater.start();
  }

  /**
   * Sets the initial particle set to a circular cloud centered on  aPose
   * @param aPose
   * @param radiusNoise
   * @param headingNoise
   */
  public void setInitialPose(Pose aPose, float radiusNoise, float headingNoise)
  {
    _x = aPose.getX();
    _y = aPose.getY();
    _heading = aPose.getHeading();
    particles = new MCLParticleSet(map, numParticles, aPose, radiusNoise, headingNoise);
  }

  /**
   * Sets the initial pose using the range readings
   * @param readings
   * @param sigma  range reading noise
   */
  public void setInitialPose(RangeReadings readings,float sigma)
  {
    if(debug) System.out.println("MCLPP set Initial pose called ");
    float minWeight = 0.3f;
    particles = new MCLParticleSet(map, numParticles,border,readings, 2*sigma*sigma,minWeight  );
    updated = true; 
  }

  /**
   * Set debugging on or off
   * @param on true = on, false = off
   */
  public void setDebug(boolean on) {
	  debug = on;
  }
  public void setPose(Pose aPose)
  {
    setInitialPose(aPose, 1, 1);
    updated = true;
  }

  /**
   * Returns the particle set
   * @return the particle set
   */
  public MCLParticleSet getParticles()
  {
    return particles;
  }

  /**
  Required by MoveListener interface; does nothing
   */
  public void moveStarted(Move event, MoveProvider mp) {}


  /**
   * Required by MoveListener interface.
   * Applies the move to all particles; updates the estimated pose after Travel
   * @param event the event just completed
   * @param mp
   */
  public void moveStopped(Move event, MoveProvider mp)
  {
      updated = false;
      updater.moveStopped(event);
  }
public boolean  update()
{
  return updater.update();
}


  /**
   * Returns the difference between max and min x
   * @return the difference between min and max x
   */
  public float getXRange()
  {
    return getMaxX() - getMinX();
  }

  /**
   * Return difference between max and min y
   * @return difference between max and min y
   */
  public float getYRange()
  {
    return getMaxY() - getMinY();
  }

 
  /**
   * Returns the best best estimate of the current pose;
   * @return the estimated pose
   */
  public Pose getPose()
  {
    if(debug) System.out.println("MCLPP getPose updated "+updated);
    if (!updated)
    {
    while(busy)Thread.yield();
      if(debug) System.out.println("Mcl call update; updated? "+updated);
      if(!updated)updater.update();
    }
    estimatePose();
    return new Pose(_x, _y, _heading);

  }

  /**
   * Estimate pose from weighted average of the particles
   */
  private void estimatePose()
  {
//    if (scanner == null)
//    {
//      return;
//    }
    float totalWeights = 0;
    float estimatedX = 0;
    float estimatedY = 0;
    float estimatedAngle = 0;
    varX = 0;
    varY = 0;
    varH = 0;
    minX = BIG_FLOAT;
    minY = BIG_FLOAT;

    for (int i = 0; i < numParticles; i++)
    {
      Pose p = particles.getParticle(i).getPose();
      float x = p.getX();
      float y = p.getY();
      float weight = particles.getParticle(i).getWeight();
      estimatedX += (x * weight);
      varX += (x * x * weight);
      estimatedY += (y * weight);
      varY += (y * y * weight);
      float head = p.getHeading();
      estimatedAngle += (head * weight);
      varH += (head * head * weight);
      totalWeights += weight;

      if (x < minX)
      {
        minX = x;
      }
      if (x > maxX)
      {
        maxX = x;
      }
      if (y < minY)
      {
        minY = y;
      }
      if (y > maxY)
      {
        maxY = y;
      }
    }

    estimatedX /= totalWeights;
    varX /= totalWeights;
    varX -= (estimatedX * estimatedX);
    estimatedY /= totalWeights;
    varY /= totalWeights;
    varY -= (estimatedY * estimatedY);
    estimatedAngle /= totalWeights;
    varH /= totalWeights;
    varH -= (estimatedAngle * estimatedAngle);
    while (estimatedAngle > 180)
    {
      estimatedAngle -= 360;
    }
    while (estimatedAngle < -180)
    {
      estimatedAngle += 360;
    }
    _x = estimatedX;
    _y = estimatedY;
    _heading = estimatedAngle;
  }
  /**
   * Returns most recent range readings
   * @return the range readings
   */
  public RangeReadings getRangeReadings()
  {
    return readings;
  }
  /**
   * Returns the minimum rectangle enclosing all the particles
   * @return rectangle : the minimum rectangle enclosing all the particles
   */
  public Rectangle getErrorRect()
  {
    return new Rectangle((int) minX, (int) minY,
            (int) (maxX - minX), (int) (maxY - minY));
  }

  /**
   * Returns the maximum value of  X in the particle set
   * @return   max X
   */
  public float getMaxX()
  {
    return maxX;
  }

  /**
   * Returns the minimum value of   X in the particle set;
   * @return minimum X
   */
  public float getMinX()
  {
    return minX;
  }

  /**
   * Returns the maximum value of Y in the particle set;
   * @return max y
   */
  public float getMaxY()
  {
    return maxY;
  }

  /**
   * Returns the minimum value of Y in the particle set;
   * @return minimum Y
   */
  public float getMinY()
  {
    return minY;
  }

  /**
   * Returns the standard deviation of the X values in the particle set;
   * @return sigma X
   */
  public float getSigmaX()
  {
    return (float) Math.sqrt(varX);
  }

  /**
   * Returns the standard deviation of the Y values in the particle set;
   * @return sigma Y
   */
  public float getSigmaY()
  {
    return (float) Math.sqrt(varY);
  }

  /**
   * Returns the standard deviation of the Y values in the particle set;
   * @return sigma heading
   */
  public float getSigmaHeading()
  {
    return (float) Math.sqrt(varH);
  }
  
/**
 * Returns the range scanner
 * @return the range scanner
 */
public RangeScanner getScanner()
{
  return scanner;
}
  /**
   * Dump the serialized estimate of pose to a data output stream
   * @param dos the data output stream
   * @throws IOException
   */
  public void dumpEstimation(DataOutputStream dos) throws IOException
  {
    dos.writeFloat(_x);
    dos.writeFloat(_y);
    dos.writeFloat(_heading);
    dos.writeFloat(minX);
    dos.writeFloat(maxX);
    dos.writeFloat(minY);
    dos.writeFloat(maxY);
    dos.writeFloat((float) varX);
    dos.writeFloat((float) varY);
    dos.writeFloat((float) varH);
    dos.flush();
  }

  /**
   * Load serialized estimated pose from a data input stream
   * @param dis the data input stream
   * @throws IOException
   */
  public void loadEstimation(DataInputStream dis) throws IOException
  {
    _x = dis.readFloat();
    _y = dis.readFloat();
    _heading = dis.readFloat();
    minX = dis.readFloat();
    maxX = dis.readFloat();
    minY = dis.readFloat();
    maxY = dis.readFloat();
    varX = dis.readFloat();
    varY = dis.readFloat();
    varH = dis.readFloat();
    System.out.println("Estimate = " + minX + " , " + maxX + " , " + minY + " , " + maxY);
  }

  public void autoUpdate(boolean yes)
  {
    autoUpdate = yes;
  }

  public boolean isBusy() { return busy;}

  class Updater extends Thread
  {

    boolean keepGoing = true;
    boolean moveStopped = false;
    Move event;

    public void moveStopped(Move theEvent)
    {
      updater.event = theEvent;
      moveStopped = true;
      if(debug) System.out.println("Updater move stop "+theEvent.getMoveType());
    }
 /**
   * Updates the estimated pose using Monte Carlo method applied to particles.
   * Gets range readings from the scanner, calculates weights and resamples the
   * the particles.  Tries at most 4 times  range values are incomplete and/or
   * the probabilities of the sensor readings are too small;.
   * @return  true if the update was successful.  Otherwise, the dead reckoning pose
   * is the only one available.
   */
  private synchronized  boolean update()
  {
    if (scanner == null)
    {
      return false;
    }
    while(busy)Thread.yield();
    busy = true;
    readings = scanner.getRangeValues();
    if(debug) System.out.println("mcl Update: range readings " + readings.getNumReadings());
    if (readings.incomplete())
    {
      if (debug)
      {
        System.out.println("Readings incomplete");
      }
      busy = false;
      return false;
    }
    if (!particles.calculateWeights(readings, map))
    {
      if (debug)
      {
        System.out.println("Sensor data is too improbable from the current pose");
      }
      busy = false;
      return false;
    }
    particles.resample();
    updated = true;
    busy = false;
    return true;
  }

    public void run()
    {
      while (keepGoing)
      {
        if (moveStopped)
        {
         busy = true;
          moveStopped = false;
          if(debug)System.out.println("Update run move stopped "+event.getMoveType());
          sw.reset();
          particles.applyMove(event);      
          if (autoUpdate && event.getMoveType() == Move.MoveType.TRAVEL)
          {
            busy = false;
            update();
            updated = true;
            if (debug) System.out.println("MCL run update complete time "+sw.elapsed());
          }
          busy = false;
        }
      }
    }  // end run()
  }// end class Updater
}

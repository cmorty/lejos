package lejos.robotics.localization;

import lejos.robotics.Pose;
import lejos.robotics.mapping.RangeMap;
import lejos.robotics.MoveListener;
import lejos.robotics.MoveProvider;
import lejos.robotics.RangeReadings;
import lejos.robotics.RangeScanner;
import lejos.robotics.Move;
import java.awt.Rectangle;
import java.io.*;

public class MCLPoseProvider implements PoseProvider, MoveListener
{
  private MCLParticleSet particles;
  private RangeScanner scanner;
  private RangeMap map;
  private int numParticles;
  float _x,_y,_heading;
  private float minX, maxX, minY, maxY;
  double varX, varY, varH;
  private boolean autoUpdate = false;

  public MCLPoseProvider(MoveProvider mp, RangeScanner scanner,
          RangeMap map, int numParticles, int border)
  {
    this.numParticles = numParticles;
    particles = new MCLParticleSet(map, numParticles, border);
    this.scanner = scanner;
    this.map = map;
    mp.addMoveListener(this);
  }

  public MCLPoseProvider(MoveProvider mp, RangeScanner scanner,
          RangeMap map, int numParticles)
  {
    this.numParticles = numParticles;
    this.scanner = scanner;
    this.map = map;
    mp.addMoveListener(this);
  }
/**
 * Sets the inital particle set to a circular cloud centered on  aPose
 * @param aPose
 * @param radiusNoise
 * @param headingNoise
 */
  public void setInitialPose( Pose aPose,float radiusNoise,float headingNoise)
  {
    _x = aPose.getX();
    _y = aPose.getY();
    _heading = aPose.getHeading();
       particles = new MCLParticleSet(map, numParticles, aPose,
            radiusNoise,  headingNoise);
  }

  public void setPose(Pose aPose)
  {
    setInitialPose(aPose, 1,1);
  }
   
  /**
   * returns the particle set
   * @return the particle set
   */
  public MCLParticleSet getParticles()
  {
    return particles;
  }

  /**
    require by MoveListener interface; does nothing
   */
  public void moveStarted(Move event, MoveProvider mp)
  {
    
  }
  
  /**
   * required by MoveListener interface.
   * Applies the move to all particles; updates the estimated pose after Travel
   * @param event the event just completed
   * @param mp
   */
  public void moveStopped(Move event, MoveProvider mp)
  {
    particles.applyMove(event);
    if(autoUpdate && event.getMoveType() ==  Move.MoveType.TRAVEL)
    {
    update();
    }
  }

  /**
   * returns the difference between max and min x
   * @return the diference between min and max x
   */
  public float getXRange()
  {
    return getMaxX()-  getMinX();
  }
  /**
   * return difference between max and min y
   * @return difference between max and min y
   */
  public float getYRange()
  {
    return getMaxY()- getMinY();
  }
  
  /**
   * updates the estimated pose using monte carlo method applied to particles.
   * Gets range readings from the scanner, calculates weights and resamples the
   * the particles.  Tries at most 4 times  range values are incpmplete and/or
   * the probabilities of the sensor readings are too small;.
   * @return  true if the update was successful.  Otherwise, the dead reckoning pose
   * is the only one availabale.
   */
  public boolean update()
  {
    int maxTries = 4;
     int tries = 0;
    while (tries < maxTries)
    {

    RangeReadings rr = scanner.getRangeValues();
    if(rr.incomplete())rr = scanner.getRangeValues();
    if(rr.incomplete())
    {
      System.out.println("READINGS INCOMPOETE");
              tries = maxTries;
              break;
    }
       if (particles.calculateWeights(rr, map)) break;
      else  tries++;
    }
     if(tries == maxTries )
     {
       System.out.println(" sensor data is too improbable from the current pose ");
       return false;
     }

    particles.resample();
    estimatePose();
//        particles.logParticles(dl);
    return true;
  }
  
  /**
   * returns the best best estimate of the current pose;
   * @return the estimated pose
   */
  public Pose getPose()
  {
    estimatePose();
    return new Pose(_x, _y, _heading);

   }
  
  /**
   * Estimate pose from weighted average of the particles
   */
  private void estimatePose()
  {

    float totalWeights = 0;
    float estimatedX = 0;
    float estimatedY = 0;
    float estimatedAngle = 0;
    varX = 0;
    varY = 0;
    varH = 0;
    
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

      if (x < minX) minX = x;
      if (x > maxX) maxX = x;
      if (y < minY) minY = y;
      if (y > maxY) maxY = y;
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
     while(estimatedAngle > 180) estimatedAngle -=360;
     while(estimatedAngle < -180) estimatedAngle +=360;
     _x = estimatedX;
     _y = estimatedY;
     _heading = estimatedAngle;
  }
  
  /**
   * Returns the minimum rectangle enclosing all the particles
   * @return rectangle : the minimum rectangle enclosing all the particles
   */
  public Rectangle getErrorRect() {
	  return new Rectangle((int) minX, (int) minY,
			               (int) (maxX-minX), (int) (maxY-minY));
  }
  
  /**
   * returns the maximum value of  X in the particle set
   * @return   max X
   */
  public float getMaxX() { return maxX;}
  
  /**
   * returns the minimum value of   X in the particle set;
   * @return minimum X
   */
  public float getMinX() { return minX;}
  
  /**
   * Returns the maximum value of Y in the particle set;
   * @return max y
   */
  public float getMaxY() { return maxY;}
  
  /**
   * returns the minimum value of Y in the particle set;
   * @return minimum Y
   */
  public float getMinY() { return minY;}

  /**
   * returns the standard deviation of the X values in the particle set;
   * @return sigma X
   */
  public float getSigmaX() { return (float)Math.sqrt(varX);}

  /**
   * returns the standard deviation of the Y values in the particle set;
   * @return sigma Y
   */
  public float getSigmaY() { return (float)Math.sqrt(varY);}

  /**
   * returns the standard deviation of the Y values in the particle set;
   * @return sigma heading
   */
  public float getSigmaHeading() {return (float)Math.sqrt(varH);}


  /**
   * Dump the serialized estimate of pose to a data output stream
   * @param dos the data output stream
   * @throws IOException
   */
  public void dumpEstimation(DataOutputStream dos) throws IOException {
      Pose pose = getPose();

      dos.writeFloat(pose.getX());
      dos.writeFloat(pose.getY());
      dos.writeFloat(pose.getHeading());
      dos.writeFloat(minX);
      dos.writeFloat(maxX);
      dos.writeFloat(minY);
      dos.writeFloat(maxY);
      dos.writeFloat((float)varX);
      dos.writeFloat((float)varY);
      dos.writeFloat((float)varH);
      dos.flush();
  }
}

package org.lejos.sample.rcnavmapper;

/**
 * used by NXTCommunicator - implemented by a remotely controlled vehicle that
 * uses the NXTCommunicator 
 * @author roger
 */
public interface RCVehicle 
{
  public void execute(int code, float v0, float v1, float v2, boolean bit);

}

package org.lejos.pcsample.navmapcontrol;

/**
 *Interface used by RCCommunicator 
 * @author owner
 */
public interface RemoteController {

  public void execute(int code, float v0, float v1, float v2, boolean bit);

}

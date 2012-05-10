package org.lejos.pcsample.navmapcontrol;



/**
 * Use this enum in both the NavMappcontrol Control program on the PC
 * and the RCNavMapper that runs on the NXT
 * @author Roger Glassey
 */
public enum NavCommand
{
  STOP,ROTATE, TRAVEL, GOTO, FIX, POSE, SETPOSE, OBSTACLE,
  PINGFRONT, PINGLEFT, PINGRIGHT; 
}
package org.lejos.sample.rcnavmapper;




/**
 * Use this enum in both the RCNavigationControl program on the PC
 * and the RCNavigator that runs on the NXT
 * @author Roger Glassey
 */
public enum NavCommand
{
  STOP,ROTATE, TRAVEL, GOTO, FIX, POSE, SETPOSE, OBSTACLE,
  PINGFRONT, PINGLEFT, PINGRIGHT; 
}

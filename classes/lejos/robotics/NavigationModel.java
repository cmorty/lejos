package lejos.robotics;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import lejos.robotics.mapping.LineMap;

public abstract class NavigationModel {
	protected LineMap map;
	protected String nxtName;
	protected DataInputStream dis;
	protected DataOutputStream dos;
	protected int numReadings = 0;
	public enum NavEvent {LOAD_MAP, GOTO, TRAVEL, ROTATE, STOP, GET_POSE, 
		SET_POSE, RANDOM_MOVE, TAKE_READINGS, GET_READINGS, FIND_CLOSEST, ADD_WAYPOINT, PARTICLE_SET,
		RANGE_READINGS, MOVE_STARTED, MOVE_STOPPED, WAYPOINT_REACHED, CLOSEST_PARTICLE, GET_ESTIMATED_POSE,
		ESTIMATED_POSE, PATH_COMPLETE, FEATURE_DETECTED, FIND_PATH, PATH}
	
	public boolean hasMap() {
		return map != null;
	}
	
	public LineMap getMap() {
		return map;
	}
	
	public void setNumReadings(int number) {
		numReadings = number;
	}
}

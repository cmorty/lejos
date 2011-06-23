package lejos.robotics;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import lejos.robotics.mapping.LineMap;

public abstract class NavigationModel {
	protected LineMap map;
	protected String nxtName;
	protected DataInputStream dis;
	protected DataOutputStream dos;
	public enum NavEvent {LOAD_MAP, GOTO, TRAVEL, ROTATE, STOP, GET_POSE, 
		SET_POSE, RANDOM_MOVE, TAKE_READINGS, FIND_CLOSEST, ADD_WAYPOINT,
		MOVE_STARTED, MOVE_STOPPED}
	
	public boolean hasMap() {
		return map != null;
	}
	
	public LineMap getMap() {
		return map;
	}
}

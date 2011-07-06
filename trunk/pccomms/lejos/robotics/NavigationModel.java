package lejos.robotics;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import lejos.robotics.localization.MCLParticleSet;
import lejos.robotics.localization.MCLPoseProvider;
import lejos.robotics.mapping.LineMap;
import lejos.robotics.navigation.Move;
import lejos.robotics.navigation.Pose;
import lejos.robotics.navigation.Waypoint;
import lejos.robotics.pathfinding.Path;

/**
 * NavigationModel is an abstract class that has two implementations: NXTNavigationModel and PCNavigationModel.
 * 
 * It is used to hold all navigation data and transmit updates to the date and other events between a NXT
 * brick and the PC.
 * 
 * The abstract NAvigationModel class defines all the events and all data and methods that are common to the 
 * NXT and PC implementations.
 * 
 * The purpose of NavigationModel and the NXT and PC implementations is to to allow navigation tasks to be 
 * split between the NXT and the PC, to allow the PC to show a graphical display of the navigational data
 * and allow the user to interact with it.
 * 
 * This allows many different navigation applications to be developed which split processing between the PC
 * and the NXT.
 * 
 * @author Lawrie Griffiths
 *
 */
public abstract class NavigationModel {
	protected LineMap map;
	protected String nxtName;
	protected DataInputStream dis;
	protected DataOutputStream dos;
	protected int numReadings = 0;
	protected Pose currentPose = new Pose(0,0,0);
	protected Waypoint target = null;
	protected MCLParticleSet particles;
	protected MCLPoseProvider mcl;
	protected RangeReadings readings = new RangeReadings(0);
	protected RangeReadings featureReadings;
	protected Path path;
	protected Move lastMove = new Move(0,0,false);
	protected Move lastPlannedMove = new Move(0,0,false);
	
	public enum NavEvent {LOAD_MAP, GOTO, TRAVEL, ROTATE, STOP, GET_POSE, 
		SET_POSE, RANDOM_MOVE, TAKE_READINGS, GET_READINGS, FIND_CLOSEST, ADD_WAYPOINT, GET_PARTICLES, PARTICLE_SET,
		RANGE_READINGS, MOVE_STARTED, MOVE_STOPPED, WAYPOINT_REACHED, CLOSEST_PARTICLE, GET_ESTIMATED_POSE,
		ESTIMATED_POSE, PATH_COMPLETE, FEATURE_DETECTED, FIND_PATH, PATH, SET_TARGET, FOLLOW_ROUTE}
	
	public boolean hasMap() {
		return map != null;
	}
	
	public LineMap getMap() {
		return map;
	}
	
	public void setNumReadings(int number) {
		numReadings = number;
	}
	
	public Pose getRobotPose() {
		return currentPose;
	}
	
	public MCLParticleSet getParticles() {
		return particles;
	}
	
	public void setRobotPose(Pose p) {
		currentPose = p;
	}
	
	public void setParticleSet(MCLParticleSet particles) {
		this.particles = particles;
	}
	
	public RangeReadings getReadings() {
		return readings;
	}
	
	public void setTarget(Waypoint target) {
		this.target = target;
	}
	
	public Waypoint getTarget() {
		return target;
	}
	
	public Path getPath() {
		return path;
	}
}

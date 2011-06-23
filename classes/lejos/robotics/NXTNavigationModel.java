package lejos.robotics;

import java.io.IOException;

import lejos.nxt.Button;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.NXTCommConnector;
import lejos.nxt.comm.NXTConnection;
import lejos.robotics.localization.MCLParticleSet;
import lejos.robotics.localization.PoseProvider;
import lejos.robotics.mapping.LineMap;
import lejos.robotics.navigation.*;
import lejos.robotics.objectdetection.Feature;
import lejos.robotics.objectdetection.FeatureDetector;
import lejos.robotics.objectdetection.FeatureListener;
import lejos.robotics.pathfinding.PathFinder;

public class NXTNavigationModel extends NavigationModel implements MoveListener, WayPointListener, FeatureListener {
	protected WayPoint target = new WayPoint(0,0);
	protected Pose currentPose = new Pose(0,0,0);
	protected MCLParticleSet particles;
	protected PathController navigator;
	protected MoveController pilot;
	protected PoseProvider pp;
	protected FeatureDetector detector;
	protected PathFinder finder;
	protected RangeReadings readings = new RangeReadings(0);
	protected float projection = 10;
	protected float border = 0;
	protected float maxDistance = 40;
	protected boolean autoSendPose = true;
	
	public NXTNavigationModel() {
		Thread receiver = new Thread(new Receiver());
		receiver.start();
	}
	
	public void log(String message) {
		System.out.println(message);
	}

	public void error(String message) {
		System.out.println(message);
	}
	
	public void fatal(String message) {
		System.out.println(message);
		Button.waitForPress();
		System.exit(1);
	}
	
	public void addNavigator(PathController navigator) {
		this.navigator = navigator;
	}
	
	public void addPilot(MoveController pilot) {
		this.pilot = pilot;
		pilot.addMoveListener(this);
	}
	
	public void addPoseProvider(PoseProvider pp) {
		this.pp = pp;
	}

	class Receiver implements Runnable {
		public void run() {
			NXTCommConnector connector = Bluetooth.getConnector();
			NXTConnection conn = connector.waitForConnection(0, NXTConnection.PACKET);
			dis = conn.openDataInputStream();
			dos = conn.openDataOutputStream();
			log("Connected");
			
			while(true) {
				try {
					byte event = dis.readByte();
					log("Event:" +  NavEvent.values()[event].name());
					if (event ==  NavEvent.LOAD_MAP.ordinal()) {
						if (map == null) map = new LineMap();
						map.loadObject(dis);
					} else if (event == NavEvent.GOTO.ordinal()) {
						if (navigator != null) {
							target.loadObject(dis);
							navigator.goTo(target);
						}
					} else if (event == NavEvent.STOP.ordinal()) {
						if (pilot != null) {
							pilot.stop();
						}
					} else if (event == NavEvent.TRAVEL.ordinal()) {
						if (pilot != null) {
							float distance = dis.readFloat();
							pilot.travel(distance);
						}
					} else if (event == NavEvent.ROTATE.ordinal() && pilot != null && pilot instanceof RotateMoveController) {
						float angle = dis.readFloat();
						((RotateMoveController) pilot).rotate(angle);
					} else if (event == NavEvent.GET_POSE.ordinal() && pp != null) {
						System.out.println("setting pose");
						dos.writeByte(NavEvent.SET_POSE.ordinal());
						pp.getPose().dumpObject(dos);
					} else if (event == NavEvent.SET_POSE.ordinal() && pp != null) {
						currentPose.loadObject(dis);
						pp.setPose(currentPose);
					} else if (event == NavEvent.ADD_WAYPOINT.ordinal())  {
						if (navigator != null) {
							WayPoint wp = new WayPoint(0,0);
							wp.loadObject(dis);
							navigator.addWayPoint(wp);
						}
					} else if (event == NavEvent.FIND_CLOSEST.ordinal()) {
						if (particles != null) {
							float x = dis.readFloat();
							float y = dis.readFloat();
							int closest = particles.findClosest(x, y);
							dos.writeByte(NavEvent.CLOSEST_PARTICLE.ordinal());
							dos.writeInt(closest);
						}
					} else if (event == NavEvent.PARTICLE_SET.ordinal()) {
						if (particles != null) {
							particles.loadObject(dis);
						}
					} else if (event == NavEvent.TAKE_READINGS.ordinal()) {
						// TODO: How to do this?
					} else if (event == NavEvent.RANDOM_MOVE.ordinal() && pilot != null &&
							   pilot instanceof RotateMoveController) {
					    float angle = (float) Math.random() * 360;
					    float distance = (float) Math.random() * maxDistance;
					    
					    if (angle > 180f) angle -= 360f;

					    // Get forward range
					    float forwardRange = readings.getRange(1);

					    // Don't move forward if we are near a wall
					    if (forwardRange < 0
					        || distance + border + projection < forwardRange)
					      pilot.travel(distance);
					    
					    ((RotateMoveController) pilot).rotate(angle);
					}
				} catch (Exception ioe) {
					fatal("Exception in receiver");
				}
			}
			
		}	
	}

	public void moveStarted(Move event, MoveProvider mp) {
		try {
			dos.writeByte(NavEvent.MOVE_STARTED.ordinal());
			event.dumpObject(dos);
		} catch (IOException ioe) {
			fatal("IOException in moveStarted");	
		}	
	}

	public void moveStopped(Move event, MoveProvider mp) {
		try {
			dos.writeByte(NavEvent.MOVE_STOPPED.ordinal());
			event.dumpObject(dos);
			if (pp != null && autoSendPose) {
				dos.writeByte(NavEvent.SET_POSE.ordinal());
				pp.getPose().dumpObject(dos);
			}
		} catch (IOException ioe) {
			fatal("IOException in moveStarted");	
		}	
	}

	public void nextWaypoint(WayPoint wp) {	
	}

	public void pathComplete() {
	}

	public void featureDetected(Feature feature, FeatureDetector detector) {	
	}
}

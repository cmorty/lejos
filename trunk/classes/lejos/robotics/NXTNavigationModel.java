package lejos.robotics;

import java.io.IOException;

import lejos.nxt.Button;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.NXTCommConnector;
import lejos.nxt.comm.NXTConnection;
import lejos.robotics.localization.MCLParticleSet;
import lejos.robotics.localization.MCLPoseProvider;
import lejos.robotics.localization.PoseProvider;
import lejos.robotics.mapping.LineMap;
import lejos.robotics.navigation.*;
import lejos.robotics.objectdetection.Feature;
import lejos.robotics.objectdetection.FeatureDetector;
import lejos.robotics.objectdetection.FeatureListener;
import lejos.robotics.pathfinding.PathFinder;

public class NXTNavigationModel extends NavigationModel implements MoveListener, WaypointListener, FeatureListener {
	protected PathController navigator;
	protected MoveController pilot;
	protected PoseProvider pp;
	protected FeatureDetector detector;
	protected PathFinder finder;
	protected float projection = 10;
	protected float border = 0;
	protected float maxDistance = 40;
	protected boolean autoSendPose = true;
	protected RangeScanner scanner;
	protected MCLPoseProvider mcl;
	protected boolean sendMove = true;
	
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
	
	@SuppressWarnings("hiding")
	public void addNavigator(PathController navigator) {
		this.navigator = navigator;
	}
	
	@SuppressWarnings("hiding")
	public void addPilot(MoveController pilot) {
		this.pilot = pilot;
		pilot.addMoveListener(this);
	}
	
	@SuppressWarnings("hiding")
	public void addPoseProvider(PoseProvider pp) {
		this.pp = pp;
	}
	
	@SuppressWarnings("hiding")
	public void addRangeScanner(RangeScanner scanner) {
		this.scanner = scanner;
	}
	
	@SuppressWarnings("hiding")
	public void addMCL(MCLPoseProvider mcl) {
		this.mcl = mcl;
		mcl.setDebug(true);
	}
	
	public void setRandomMoveParameters(float maxDistance, float projection, float border) {
		this.maxDistance = maxDistance;
		this.projection = projection;
		this.border = border;
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
					//log("Free memory = " + System.getRuntime().freeMemory());
					if (event ==  NavEvent.LOAD_MAP.ordinal()) {
						if (map == null) map = new LineMap();
						map.loadObject(dis);
						if (mcl != null) mcl.setMap(map);
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
					} else if (event == NavEvent.GET_POSE.ordinal()) {
						PoseProvider poseProvider = (mcl != null ? mcl : pp);
						boolean saveSendMove = sendMove;
						sendMove = false;
						currentPose = poseProvider.getPose();
						sendMove = saveSendMove;
						dos.writeByte(NavEvent.SET_POSE.ordinal());
						currentPose.dumpObject(dos);
					} else if (event == NavEvent.SET_POSE.ordinal()) {
						currentPose.loadObject(dis);
						if (pp != null) pp.setPose(currentPose);
					} else if (event == NavEvent.ADD_WAYPOINT.ordinal())  {
						Waypoint wp = new Waypoint(0,0);
						wp.loadObject(dis);
						if (navigator != null) navigator.addWayPoint(wp);
					} else if (event == NavEvent.FIND_CLOSEST.ordinal()) {
						float x = dis.readFloat();
						float y = dis.readFloat();
						if (particles != null) {
							int closest = particles.findClosest(x, y);
							dos.writeByte(NavEvent.CLOSEST_PARTICLE.ordinal());
							dos.writeInt(closest);
							dos.flush();
						}
					} else if (event == NavEvent.PARTICLE_SET.ordinal()) {
						if (particles == null) particles = new MCLParticleSet(map,0,0);
						//log("Created MCL");
					    particles.loadObject(dis);
					    //log("Loaded particles");
					    mcl.setParticles(particles);
					} else if (event == NavEvent.TAKE_READINGS.ordinal() && scanner != null) {
						readings = scanner.getRangeValues();
						dos.writeByte(NavEvent.RANGE_READINGS.ordinal());
						readings.dumpObject(dos);						
					} else if (event == NavEvent.GET_READINGS.ordinal()) {
						dos.writeByte(NavEvent.RANGE_READINGS.ordinal());
						if (mcl != null) readings = mcl.getRangeReadings();
						readings.dumpObject(dos);						
					} else if (event == NavEvent.GET_PARTICLES.ordinal()) {
						dos.writeByte(NavEvent.PARTICLE_SET.ordinal());
						particles.dumpObject(dos);						
					} else if (event == NavEvent.GET_ESTIMATED_POSE.ordinal()) {
						dos.writeByte(NavEvent.ESTIMATED_POSE.ordinal());
						mcl.dumpObject(dos);						
					} else if (event == NavEvent.RANDOM_MOVE.ordinal() && pilot != null &&
							   pilot instanceof RotateMoveController) {
					    float angle = (float) Math.random() * 360;
					    float distance = (float) Math.random() * maxDistance;
					    
					    if (angle > 180f) angle -= 360f;

					    float forwardRange;
					    // Get forward range
					    try {
					    	forwardRange = readings.getRange(0f); // Range for angle 0 (forward)
					    } catch (Exception e) {
					    	forwardRange = 0;
					    }
					    
					    // Don't move forward if we are near a wall
					    if (forwardRange < 0
					        || distance + border + projection < forwardRange)
					      pilot.travel(distance);
					    
					    ((RotateMoveController) pilot).rotate(angle);
					}
				} catch (IOException ioe) {
					fatal("IOException in receiver:");
				}
			}
			
		}	
	}

	public void moveStarted(Move event, MoveProvider mp) {
		/*try {
			log("Sending move started");
			dos.writeByte(NavEvent.MOVE_STARTED.ordinal());
			event.dumpObject(dos);
			log("Finished move started");
		} catch (IOException ioe) {
			fatal("IOException in moveStarted");	
		}*/
	}

	public void moveStopped(Move event, MoveProvider mp) {
		if (!sendMove) return;
		try {
			log("Sending move stopped");
			dos.writeByte(NavEvent.MOVE_STOPPED.ordinal());
			event.dumpObject(dos);
			if (pp != null && autoSendPose) {
				log("Sending set pose");
				dos.writeByte(NavEvent.SET_POSE.ordinal());
				pp.getPose().dumpObject(dos);
			}
		} catch (IOException ioe) {
			fatal("IOException in moveStarted");	
		}	
	}

	public void nextWaypoint(Waypoint wp) {	
		try {
			dos.writeByte(NavEvent.WAYPOINT_REACHED.ordinal());
			wp.dumpObject(dos);
		} catch (IOException ioe) {
			fatal("IOException in nextWaypoint");	
		}
	}

	public void pathComplete() {
		try {
			dos.writeByte(NavEvent.PATH_COMPLETE.ordinal());
		} catch (IOException ioe) {
			fatal("IOException in pathComplete");	
		}
	}

	@SuppressWarnings("hiding")
	public void featureDetected(Feature feature, FeatureDetector detector) {
		try {
			dos.writeByte(NavEvent.FEATURE_DETECTED.ordinal());
		} catch (IOException ioe) {
			fatal("IOException in featureDetected");	
		}
	}
}

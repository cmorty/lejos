package lejos.robotics.mapping;

import java.io.IOException;
import lejos.nxt.comm.*;
import lejos.robotics.RangeScanner;
import lejos.robotics.localization.*;
import lejos.robotics.navigation.*;
import lejos.robotics.objectdetection.*;
import lejos.robotics.pathfinding.Path;
import lejos.robotics.pathfinding.PathFinder;
import lejos.util.Delay;

/**
 * NXT version of the navigation model.
 * 
 * All local navigation objects, including pilots, navigators, path finders,
 * feature detectors, and range scanners.
 * 
 * Where possible, the model registers itself as an event listener and when the event occurs,
 * updates the model and sends the event and the updates to the PC.
 * 
 * A receiver thread receives events from the PC, updates the local model, and uses the navigation
 * objects to implement the event if it involves robot behaviour.
 * 
 * There are set methods to set various navigation parameters.
 * 
 * 
 * 
 * @author Lawrie Griffiths
 *
 */
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
	protected boolean sendMoveStart = false, sendMoveStop = true;
	private Thread receiver;
	
	/**
	 * Create the model and start the receiver thread
	 */
	public NXTNavigationModel() {
		receiver = new Thread(new Receiver());
		receiver.start();
	}
	
	/**
	 * Log a message
	 * 
	 * @param message the message
	 */
	public void log(String message) {
		System.out.println(message);
	}

	/**
	 * Display an error message to the user
	 * 
	 * @param message the error message
	 */
	public void error(String message) {
		System.out.println(message);
	}
	
	/**
	 * Display a fatal error and shut down the program
	 * 
	 * @param message the error message
	 */
	public void fatal(String message) {
		System.out.println(message);
		Delay.msDelay(5000);
		System.exit(1);
	}
	
	/**
	 * Add a navigator to the model
	 * 
	 * @param navigator the path controller
	 */
	@SuppressWarnings("hiding")
	public void addNavigator(PathController navigator) {
		this.navigator = navigator;
	}
	
	/**
	 * Add a pilot to the model
	 * 
	 * @param pilot the move controller
	 */
	@SuppressWarnings("hiding")
	public void addPilot(MoveController pilot) {
		this.pilot = pilot;
		pilot.addMoveListener(this);
	}
	
	/**
	 * Add a pose provider (which might be MCL) to the model
	 * 
	 * @param pp the pose provider
	 */
	@SuppressWarnings("hiding")
	public void addPoseProvider(PoseProvider pp) {
		this.pp = pp;
		if (pp instanceof MCLPoseProvider) mcl = (MCLPoseProvider) pp;
	}
	
	/**
	 * Add a range scanner to the model
	 * 
	 * @param scanner the range scanner
	 */
	@SuppressWarnings("hiding")
	public void addRangeScanner(RangeScanner scanner) {
		this.scanner = scanner;
	}
	
	/**
	 * Set parameters for a random move
	 * 
	 * @param maxDistance the maximum distance of the move
	 * @param projection the projection of the robot forward from its mid point
	 * @param border the border around the wall that the robot should not move into
	 */
	public void setRandomMoveParameters(float maxDistance, float projection, float border) {
		this.maxDistance = maxDistance;
		this.projection = projection;
		this.border = border;
	}
	
	/**
	 * Set or unset automatic sending of the robot pose to the PC when a move stops
	 * 
	 * @param on true if the pose is to be sent, else false
	 */
	public void setAutoSendPose(boolean on) {
		this.autoSendPose = on;
	}
	
	/**
	 * Sets whether events are sent to the PC when a move stops
	 * 
	 * @param on true iff an event should be sent
	 */
	public void setSendMoveStart(boolean on) {
		sendMoveStart = on;
	}
	
	/**
	 * Sets whether events are sent to the PC when a move starts
	 * 
	 * @param on true iff an event should be sent
	 */
	public void setSendMoveStop(boolean on) {
		sendMoveStop = on;
	}
	

	/**
	 * The Receiver thread receives events from the PC
	 * 
	 * @author Lawrie Griffiths
	 *
	 */
	class Receiver implements Runnable {
		public void run() {
			NXTCommConnector connector = Bluetooth.getConnector();
			NXTConnection conn = connector.waitForConnection(0, NXTConnection.PACKET);
			dis = conn.openDataInputStream();
			dos = conn.openDataOutputStream();
			log("Connected");
			
			while(true) {
				try {
					synchronized(this) {
						byte event = dis.readByte();
						NavEvent navEvent = NavEvent.values()[event];
						log(navEvent.name());
						switch (navEvent) {
						case LOAD_MAP: // Map sent from POC
							if (map == null) map = new LineMap();
							map.loadObject(dis);
							if (mcl != null) mcl.setMap(map);
							break;
						case GOTO: // Update of target and request to go to the new target
							if (target == null) target = new Waypoint(0,0);
							target.loadObject(dis);
							if (navigator != null)navigator.goTo(target);
							break;
						case STOP: // Request to stop the robot
							if (pilot != null) pilot.stop();
							break;
						case TRAVEL: // Request to travel a given distance
							float distance = dis.readFloat();
							if (pilot != null) pilot.travel(distance);
							break;
						case ROTATE: // Request to rotate a given angle
							float angle = dis.readFloat();
							if (pilot != null && pilot instanceof RotateMoveController) ((RotateMoveController) pilot).rotate(angle);
							break;
						case GET_POSE: // Request to get the pose and return it to the PC
							if (pp == null) break;
							// Suppress sending moves to PC while taking readings
							boolean saveSendMoveStart = sendMoveStart;
							boolean saveSendMoveStop = sendMoveStop;
							sendMoveStart = false;
							sendMoveStop = false;
							currentPose = pp.getPose();
							sendMoveStart = saveSendMoveStart;
							sendMoveStop = saveSendMoveStop;
							dos.writeByte(NavEvent.SET_POSE.ordinal());
							currentPose.dumpObject(dos);
							break;
						case SET_POSE: // Request to set the current pose of the robot
							if (currentPose == null) currentPose = new Pose(0,0,0);
							currentPose.loadObject(dis);
							if (pp != null) pp.setPose(currentPose);
							break;
						case ADD_WAYPOINT: // Request to add a waypoint
							Waypoint wp = new Waypoint(0,0);
							wp.loadObject(dis);
							if (navigator != null) navigator.addWaypoint(wp);
							break;
						case FIND_CLOSEST: // Request to find particle by co-ordinates and
							               // send its details to the PC
							float x = dis.readFloat();
							float y = dis.readFloat();
							if (particles != null) {
								dos.writeByte(NavEvent.CLOSEST_PARTICLE.ordinal());
								particles.dumpClosest(readings, dos, x, y);
							}
							break;
						case PARTICLE_SET: // Particle set send from PC
							if (particles == null) particles = new MCLParticleSet(map,0,0);
						    particles.loadObject(dis);
						    mcl.setParticles(particles);
						    break;
						case TAKE_READINGS: // Request to take range readings and send them to the PC
							if (scanner != null) {
								readings = scanner.getRangeValues();
								dos.writeByte(NavEvent.RANGE_READINGS.ordinal());
								readings.dumpObject(dos);
							}
							break;
						case GET_READINGS: // Request to send current readings to the PC
							dos.writeByte(NavEvent.RANGE_READINGS.ordinal());
							if (mcl != null) readings = mcl.getRangeReadings();
							readings.dumpObject(dos);
							break;
						case GET_PARTICLES: // Request to send particles to the Pc
							if (particles == null) break;
							dos.writeByte(NavEvent.PARTICLE_SET.ordinal());
							particles.dumpObject(dos);
							break;
						case GET_ESTIMATED_POSE: // Request to send estimated pose to the PC
							if (mcl == null) break;
							dos.writeByte(NavEvent.ESTIMATED_POSE.ordinal());
							mcl.dumpObject(dos);
							break;
						case FIND_PATH:
							if (target == null) target = new Waypoint(0,0);
							target.loadObject(dis);
							if (finder != null) {
								dos.writeByte(NavEvent.PATH.ordinal());
								try {
									path = finder.findRoute(currentPose, target);
									path.dumpObject(dos);
								} catch (DestinationUnreachableException e) {
									dos.writeInt(0);
								}
							}
							break;
						case FOLLOW_ROUTE:
							if (path == null) path = new Path();
							path.loadObject(dis);
							if (navigator != null) navigator.followRoute(path, false);
						case RANDOM_MOVE: // Request to make a random move
							if (pilot != null && pilot instanceof RotateMoveController) {
							    angle = (float) Math.random() * 360;
							    distance = (float) Math.random() * maxDistance;
							    
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
							break;
						}
					}
				} catch (IOException ioe) {
					fatal("IOException in receiver:");
				}
			}
			
		}	
	}

	/**
	 * Called when the pilot starts a move
	 */
	public void moveStarted(Move event, MoveProvider mp) {
		if (!sendMoveStart) return;
		try {
			synchronized(receiver) {
				log("Sending move started");
				dos.writeByte(NavEvent.MOVE_STARTED.ordinal());
				event.dumpObject(dos);
				log("Finished move started");
			}
		} catch (IOException ioe) {
			fatal("IOException in moveStarted");	
		}
	}

	/**
	 * Calls when a move stops
	 */
	public void moveStopped(Move event, MoveProvider mp) {
		if (!sendMoveStop) return;
		try {
			synchronized(receiver) {
				log("Sending move stopped");
				dos.writeByte(NavEvent.MOVE_STOPPED.ordinal());
				event.dumpObject(dos);
				if (pp != null && autoSendPose) {
					log("Sending set pose");
					dos.writeByte(NavEvent.SET_POSE.ordinal());
					pp.getPose().dumpObject(dos);
				}
			}
		} catch (IOException ioe) {
			fatal("IOException in moveStarted");	
		}	
	}

	/**
	 * Called when a waypoint is reached
	 */
	public void nextWaypoint(Waypoint wp) {	
		try {
			synchronized(receiver) {
				dos.writeByte(NavEvent.WAYPOINT_REACHED.ordinal());
				wp.dumpObject(dos);
			}
		} catch (IOException ioe) {
			fatal("IOException in nextWaypoint");	
		}
	}

	/**
	 * Call when a path a completed
	 */
	public void pathComplete() {
		try {
			synchronized(receiver) {
				dos.writeByte(NavEvent.PATH_COMPLETE.ordinal());
				dos.flush();
			}
		} catch (IOException ioe) {
			fatal("IOException in pathComplete");	
		}
	}

	/**
	 * Called when a feature is detected
	 */
	@SuppressWarnings("hiding")
	public void featureDetected(Feature feature, FeatureDetector detector) {
		try {
			synchronized(receiver) {
				dos.writeByte(NavEvent.FEATURE_DETECTED.ordinal());
				dos.flush();
			}
		} catch (IOException ioe) {
			fatal("IOException in featureDetected");	
		}
	}
}

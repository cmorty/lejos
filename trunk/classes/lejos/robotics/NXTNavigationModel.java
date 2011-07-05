package lejos.robotics;

import java.io.IOException;
import lejos.nxt.comm.*;
import lejos.robotics.localization.*;
import lejos.robotics.mapping.LineMap;
import lejos.robotics.navigation.*;
import lejos.robotics.objectdetection.*;
import lejos.robotics.pathfinding.PathFinder;
import lejos.util.Delay;
import java.util.Collection;

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
	protected boolean sendMoveStart = false, sendMoveStop = true;
	private Thread receiver;
	
	public NXTNavigationModel() {
		receiver = new Thread(new Receiver());
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
		Delay.msDelay(5000);
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
		if (pp instanceof MCLPoseProvider) mcl = (MCLPoseProvider) pp;
	}
	
	@SuppressWarnings("hiding")
	public void addRangeScanner(RangeScanner scanner) {
		this.scanner = scanner;
	}
	
	public void setRandomMoveParameters(float maxDistance, float projection, float border) {
		this.maxDistance = maxDistance;
		this.projection = projection;
		this.border = border;
	}
	
	public void setAutoSendPose(boolean on) {
		this.autoSendPose = on;
	}
	
	public void setSendMoveStart(boolean on) {
		sendMoveStart = on;
	}
	
	public void setSendMoveStop(boolean on) {
		sendMoveStop = on;
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
					synchronized(this) {
						byte event = dis.readByte();
						NavEvent navEvent = NavEvent.values()[event];
						log("Event:" +  navEvent.name());
						switch (navEvent) {
						case LOAD_MAP: // Map sent from POC
							if (map == null) map = new LineMap();
							map.loadObject(dis);
							if (mcl != null) mcl.setMap(map);
							break;
						case GOTO: // Update of target and request to go to the new target
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
							PoseProvider poseProvider = (mcl != null ? mcl : pp);
							// Suppress sending moves to PC while taking readings
							boolean saveSendMoveStart = sendMoveStart;
							boolean saveSendMoveStop = sendMoveStop;
							sendMoveStart = false;
							sendMoveStop = false;
							currentPose = poseProvider.getPose();
							sendMoveStart = saveSendMoveStart;
							sendMoveStop = saveSendMoveStop;
							dos.writeByte(NavEvent.SET_POSE.ordinal());
							currentPose.dumpObject(dos);
							break;
						case SET_POSE: // Request to set the current pose of the robot
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
							dos.writeByte(NavEvent.PARTICLE_SET.ordinal());
							particles.dumpObject(dos);
							break;
						case GET_ESTIMATED_POSE: // Request to send estimated pose to the PC
							dos.writeByte(NavEvent.ESTIMATED_POSE.ordinal());
							mcl.dumpObject(dos);
							break;
						case FIND_PATH:
							target.loadObject(dis);
							if (finder != null) {
								dos.writeByte(NavEvent.PATH.ordinal());
								try {
									Collection<Waypoint> path = finder.findRoute(currentPose, target);
									// TODO: send the path
								} catch (DestinationUnreachableException e) {
									// nothing
								}
							}
							break;
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

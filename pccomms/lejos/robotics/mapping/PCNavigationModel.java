package lejos.robotics.mapping;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import lejos.geom.Point;
import lejos.nxt.remote.NXTCommand;
import lejos.pc.comm.*;
import lejos.robotics.*;
import lejos.robotics.navigation.*;
import lejos.robotics.pathfinding.*;
import lejos.robotics.localization.*;

/**
 * The PCNavigationModel holds all the navigation data that is transmitted as events,
 * to and from a NXT brick.
 * 
 * It has methods to generate events, and a Receiver thread to receive events from the NXT.
 * 
 * There is a NavigationPanel associated with the model. Whenever data in the model is updated,
 * the NavigationPanel is repainted with the new data.
 * 
 * @author Lawrie Griffiths
 *
 */
public class PCNavigationModel extends NavigationModel {
	protected MapApplicationUI panel;
	protected int closest = -1;
	protected boolean connected = false;
	protected RangeReadings particleReadings = new RangeReadings(0);
	protected float weight;
	protected FourWayGridMesh mesh;
	protected int gridSpace = 39;
	protected int clearance = 20;
	protected AstarSearchAlgorithm alg = new AstarSearchAlgorithm();
	protected Collection<Node> nodes;
	protected Node start, destination;
	protected NodePathFinder pf;
	protected ArrayList<Move> moves = new ArrayList<Move>();
	protected ArrayList<Pose> poses = new ArrayList<Pose>();
	protected ArrayList<Point> features = new ArrayList<Point>();
	protected ArrayList<Waypoint> waypoints = new ArrayList<Waypoint>();
	protected Waypoint reached;
	protected NXTCommand nxtCommand;
	protected float voltage = 0;
	
	private Thread receiver = new Thread(new Receiver());
	private boolean running = true;
	
	/**
	 * Create the model and associate the navigation panel with it
	 * 
	 * @param panel the NavigationPanel
	 */
	public PCNavigationModel(MapApplicationUI panel) {
		this.panel = panel;
	}
	
	/**
	 * Set the parameter for the 4-way mesh
	 * 
	 * @param gridSpace the spacing of the mesh
	 * @param clearance the clearance from the walls
	 */
	public void setMeshParams(int gridSpace, int clearance) {
		this.gridSpace = gridSpace;
		this.clearance = clearance;
	}
	
	/**
	 * Get the MCLPoseProvider associated with this model
	 * 
	 * @return the MCLPoseProvider
	 */
	public MCLPoseProvider getMCL() {
		return mcl;
	}
	
	/**
	 * Set an MCLPOseProvider for this model
	 * 
	 * @param mcl the MCLPoseProvider
	 */
	public void setMCL(MCLPoseProvider mcl) {
		this.mcl = mcl;
	}
	
	/**
	 * Get the last move made by the robot
	 * 
	 * @return the Move object
	 */
	public Move getLastMove() {
		return lastMove;
	}
	
	/**
	 * Get the moves made since the last setPose
	 * 
	 * @return an ArrayList of Move objects
	 */
	public ArrayList<Move> getMoves() {
		return moves;
	}
	
	/**
	 * Get the list of features
	 * 
	 * @return the features as an array of points
	 */
	public ArrayList<Point> getFeatures() {
		return features;
	}
	
	/**
	 * Get the list of waypoints
	 * 
	 * @return the list of waypoints
	 */
	protected ArrayList<Waypoint> getWaypoints() {
		return waypoints;
	}
	
	/**
	 * Get the sequence of poses of the robot from moves sent from the NXT,
	 * since the last call of setPose
	 * 
	 * @return an ArrayList of Pose objects
	 */
	public ArrayList<Pose> getPoses() {
		return poses;
	}
	
	public boolean lcpConnect(String nxtName) {
		try {
			NXTComm nxtComm = NXTCommFactory.createNXTComm(NXTCommFactory.BLUETOOTH);
			NXTInfo[] info = nxtComm.search(nxtName);
			if (info.length != 1) {
				panel.log("Failed to find " + nxtName);
				return false;
			}
			boolean open =  nxtComm.open(info[0], NXTComm.LCP);
			if (open) nxtCommand = new NXTCommand(nxtComm);
			return open;
		} catch (NXTCommException ioe) {
			panel.error("Failure to connect to " + nxtName);
			return false;
		}
	}
	
	public void lcpClose() {
		try {
			nxtCommand.close();
		} catch (IOException ioe) {
			// Ignore
		}
	}
	
	/**
	 * Upload the specified file
	 */
	private void uploadFile(File file) {
		if (file.getName().length() > 20) {
			panel.error("File name is more than 20 characters");
		} else {
			try {
				nxtCommand.uploadFile(file, file.getName());
			} catch (IOException ioe) {
				panel.log("IOException uploading file");
			}
		}
	}
	
	private void runFile(String fileName) {
		try {
			nxtCommand.startProgram(fileName);
		} catch (IOException ioe) {
			// Ignore as NXT disconnects
		}
	}
	
	public void connectAndUpload(String nxtName, File file) {
		boolean open = lcpConnect(nxtName);
		if (open) {
			uploadFile(file);
			if (nxtCommand != null) runFile(file.getName());
			lcpClose();
			try {
				Thread.sleep(1000); // Wait 1 seconds for program to start 
			} catch (InterruptedException ioe) {
				// Ignore
			}
		}
	}
	
	public void setDifferentialPilotParams(float wheelDiameter, float trackWidth,
			int leftMotor, int rightMotor, boolean reverse) {
		if (!connected) return;
		try {
			synchronized(receiver) {
				dos.writeByte(NavEvent.PILOT_PARAMS.ordinal());
				dos.writeFloat(wheelDiameter);
				dos.writeFloat(trackWidth);
				dos.writeInt(leftMotor);
				dos.writeInt(rightMotor);
				dos.writeBoolean(reverse);
				dos.flush();
			}
	    } catch (IOException ioe) {
			panel.error("IO Exception in setDifferentialPilotParams");
	    }		
	}
	
	public void setRangeFeatureParams(float maxDistance, int delay) {
		if (!connected) return;
		try {
			synchronized(receiver) {
				dos.writeByte(NavEvent.RANGE_FEATURE_DETECTOR_PARAMS.ordinal());
				dos.writeInt(delay);
				dos.writeFloat(maxDistance);
				dos.flush();
			}
	    } catch (IOException ioe) {
			panel.error("IO Exception in setRangeFeatureParams");
	    }	
	}
	
	public void setRotatingRangeScannerParams(int gearRatio, int headMotor) {
		if (!connected) return;
		try {
			synchronized(receiver) {
				dos.writeByte(NavEvent.RANGE_SCANNER_PARAMS.ordinal());
				dos.writeInt(gearRatio);
				dos.writeInt(headMotor);
				dos.flush();
			}
	    } catch (IOException ioe) {
			panel.error("IO Exception in setRotatingRangeScannerParams");
	    }	
	}
	
	public void setTravelSpeed(float speed) {
		if (!connected) return;
		try {
			synchronized(receiver) {
				dos.writeByte(NavEvent.TRAVEL_SPEED.ordinal());
				dos.writeFloat(speed);
				dos.flush();
			}
	    } catch (IOException ioe) {
			panel.error("IO Exception in setTravelSpeed");
	    }	
	}
	
	public void setRotateSpeed(float speed) {
		if (!connected) return;
		try {
			synchronized(receiver) {
				dos.writeByte(NavEvent.ROTATE_SPEED.ordinal());
				dos.writeFloat(speed);
				dos.flush();
			}
	    } catch (IOException ioe) {
			panel.error("IO Exception in setRotateSpeed");
	    }	
	}
	
	/**
	 * Send a GET_PARTICLES event to the NXT
	 */
	public void getRemoteParticles() {
		if (!connected) return;
		try {
			synchronized(receiver) {
				if (debug) panel.log("Sending GET_PARTICLES");
				dos.writeByte(NavEvent.GET_PARTICLES.ordinal());
				dos.flush();
			}
	    } catch (IOException ioe) {
			panel.error("IO Exception in getRemoteParticles");
	    }		
	}
	
	/**
	 * Send a FIND_CLOSEST event to the NXT
	 */
	public void findClosest(float x, float y) {
		if (!connected) return;
		try {
			synchronized(receiver) {
				dos.writeByte(NavEvent.FIND_CLOSEST.ordinal());
				dos.writeFloat(x);
				dos.writeFloat(y);
				dos.flush();
			}
	    } catch (IOException ioe) {
			panel.error("IO Exception in findClosest");
	    }		
	}
	
	/**
	 * Add a waypoint and send it to the NXT
	 * 
	 * @param wp the waypoint
	 */
	public void addWaypoint(Waypoint wp) {
		waypoints.add(wp);
		panel.repaint();
		if (!connected) return;
		try {
			synchronized(receiver) {
				dos.writeByte(NavEvent.ADD_WAYPOINT.ordinal());
				wp.dumpObject(dos);
			}
	    } catch (IOException ioe) {
			panel.error("IO Exception in findClosest");
	    }		
	}
	
	/**
	 * Generate particles for the MCLPoseProvider and send them to the NXT
	 */
	public void generateParticles() {
		mcl.generateParticles();
		particles = mcl.getParticles();
		panel.repaint();
		if (!connected) return;
		try {
			synchronized(receiver) {
				dos.writeByte(NavEvent.PARTICLE_SET.ordinal());
				particles.dumpObject(dos);
			}
	    } catch (IOException ioe) {
			panel.error("IO Exception in generateParticles");
		}
	}
	
	/**
	 * Connect to the NXT
	 */
	public void connect(String nxtName) {
		if (connected) panel.error("Already connected");
		NXTConnector conn = new NXTConnector();

		if (!conn.connectTo(nxtName, null, NXTCommFactory.BLUETOOTH)) {
		
			panel.error("NO NXT found");
			return;
		}
  
		if (debug) panel.log("Connected to " + nxtName);
  
		dis = new DataInputStream(conn.getInputStream());
		dos = new DataOutputStream(conn.getOutputStream());
		connected = true;
		
		// Start the receiver thread
		receiver.setDaemon(true);
		receiver.start();
		
		// Hook for actions required after connection
		panel.whenConnected();
	}
	
	/**
	 * Get the generated node
	 * 
	 * @return the collection of nodes
	 */
	public Collection<Node> getNodes() {
		return nodes;
	}
	
	/**
	 * Test if a NXT brick is currently connected
	 * 
	 * @return true iff a NXT brick is connected
	 */
	public boolean isConnected() {
		return connected;
	}
	
	/**
	 * Load a line map and send it to the PC
	 * 
	 * @param mapFileName the SVG map file
	 * @return the LineMap
	 */
	public LineMap loadMap(String mapFileName) {
		try {
			File mapFile = new File(mapFileName);
			if (debug) panel.log("Map file is " + mapFile.getAbsolutePath());
			if (!mapFile.exists()) {
				panel.error(mapFile.getAbsolutePath() + " does not exist");
				return null;
			}
			FileInputStream is = new FileInputStream(mapFile);
			SVGMapLoader mapLoader = new SVGMapLoader(is);
			map = mapLoader.readLineMap();
			//Rectangle r = map.getBoundingRect();
			//panel.log("Rect = " + r);
			//panel.mapPanelWidth = (int) Math.ceil(r.x + r.width); 
			//panel.mapPanelHeight = (int) Math.ceil(r.y + r.height); 
			//System.out.println("Setting panel size to " + panel.mapPanelWidth + "," + panel.mapPanelHeight);
			//panel.mapPanel.setPreferredSize(new Dimension((int) (panel.mapPanelWidth * panel.pixelsPerUnit), (int) (panel.mapPanelHeight  * panel.pixelsPerUnit)));
			
			//panel.mapPanelHeight = (int) r.height;
			//panel.mapPanel.revalidate();
			mesh = new FourWayGridMesh(map, gridSpace,clearance);
			nodes = mesh.getMesh();
			pf = new NodePathFinder(alg, mesh);
			panel.repaint();
			if (mcl != null) mcl.setMap(map);
			if (connected) {
				synchronized(receiver) {
					dos.writeByte(NavEvent.LOAD_MAP.ordinal());
					map.dumpObject(dos);
				}
			}
			return map;
		} catch (Exception ioe) {
			panel.error("Exception in loadMap:" + ioe);
			return null;
		}
		
	}
	
	/**
	 * Send a GOTO event to the NXT
	 * 
	 * @param wp the Waypoint to go to
	 */
	public void goTo(Waypoint wp) {
		if (!connected) return;
		try {
			synchronized(receiver) {
				dos.writeByte(NavEvent.GOTO.ordinal());
				target = wp;
				wp.dumpObject(dos);
			}
		} catch (IOException ioe) {
			panel.error("IO Exception in goTo");
		}		
	}
	
	/**
	 * Send a travel event to the NXT
	 * 
	 * @param distance the distance to travel
	 */
	public void travel(float distance) {
		if (!connected) return;
		try {
			synchronized(receiver) {
				dos.writeByte(NavEvent.TRAVEL.ordinal());
				dos.writeFloat(distance);
				dos.flush();
			}
		} catch (IOException ioe) {
			panel.error("IO Exception in travel");
		}
	}
	
	/**
	 * Send a ROTATE event to the NXT
	 * 
	 * @param angle the angle to rotate
	 */
	public void rotate(float angle) {
		if (!connected) return;
		try {
			synchronized(receiver) {
				dos.writeByte(NavEvent.ROTATE.ordinal());
				dos.writeFloat(angle);
				dos.flush();
			}
		} catch (IOException ioe) {
			panel.error("IO Exception in rotate");
		}
	}
	
	/**
	 * Send an ARC event to the NXT
	 * 
	 * @param radius the radius of the arc
	 * @param angle the angle to rotate
	 */
	public void arc(float radius, float angle) {
		if (!connected) return;
		try {
			synchronized(receiver) {
				dos.writeByte(NavEvent.ARC.ordinal());
				dos.writeFloat(radius);
				dos.writeFloat(angle);
				dos.flush();
			}
		} catch (IOException ioe) {
			panel.error("IO Exception in arc");
		}
	}
	
	/**
	 * Send a ROTATE_TO event to the NXT
	 * 
	 * @param angle the angle to rotate
	 */
	public void rotateTo(float angle) {
		if (!connected) return;
		try {
			synchronized(receiver) {
				dos.writeByte(NavEvent.ROTATE_TO.ordinal());
				dos.writeFloat(angle);
				dos.flush();
			}
		} catch (IOException ioe) {
			panel.error("IO Exception in rotateTo");
		}
	}
	
	/**
	 * Send a GET_POSE event to the NXT
	 */
	public void getPose() {
		if (dos == null) return;
		try {
			synchronized(receiver) {
				if (debug) panel.log("Sending GET_POSE");
				dos.writeByte(NavEvent.GET_POSE.ordinal());
				dos.flush();
			}
	    } catch (IOException ioe) {
			panel.error("IO Exception in getPose");
		}
	}
	
	/**
	 * Send a GET_ESTIMATED_POSE event to the NXT
	 */
	public void getEstimatedPose() {
		if (!connected) return;
		try {
			synchronized(receiver) {
				if (debug) panel.log("Sending GET_ESTIMATED_POSE");
				dos.writeByte(NavEvent.GET_ESTIMATED_POSE.ordinal());
				dos.flush();
			}
	    } catch (IOException ioe) {
			panel.error("IO Exception in getPose");
		}
	}
	
	/**
	 * Send a GET_READINGS event to the NXT
	 */
	public void getRemoteReadings() {
		if (!connected) return;
		try {
			synchronized(receiver) {
				if (debug) panel.log("Sending GET_READINGS");
				dos.writeByte(NavEvent.GET_READINGS.ordinal());
				dos.flush();
			}
	    } catch (IOException ioe) {
			panel.error("IO Exception in getReadings");
		}
	}
	
	/**
	 * Send a SET_POSE event to the NXT
	 * 
	 * @param p the robot pose
	 */
	public void setPose(Pose p) {
		path = null;
		currentPose = p;
		// Record poses to plot the moves on the map. Reset when pose is set on 
		poses.clear();
		poses.add(new Pose(currentPose.getX(), currentPose.getY(), currentPose.getHeading()));
		if (mesh != null) {
			if (start != null) mesh.removeNode(this.start);
			start = new Node(p.getX(), p.getY());
			mesh.addNode(start, 4);
		}
		// Send a SET_POSE to the PC application
		panel.eventReceived(NavEvent.SET_POSE);
		panel.repaint();
		if (connected) {
			try {
				synchronized(receiver) {
					if (debug) panel.log("Sending SET_POSE");
					dos.writeByte(NavEvent.SET_POSE.ordinal());
					currentPose.dumpObject(dos);
				}
		    } catch (IOException ioe) {
				panel.error("IO Exception in getPose");
			}	
		}
	}
	
	/**
	 * Set the target for a path finder
	 */
	public void setTarget(Waypoint target) {
		path = null;
		this.target = target;
		if (mesh != null) {
			if(destination != null) mesh.removeNode(destination);
			destination = new Node((float) target.getX(), (float) target.getY());
			if (mesh != null) mesh.addNode(destination, 4);
		}
		panel.repaint();
	}
	
	/**
	 * Send a STOP event to the NXT
	 */
	public void stop() {
		if (!connected) return;
		try {
			synchronized(receiver) {
				dos.writeByte(NavEvent.STOP.ordinal());
				dos.flush();
			}
	    } catch (IOException ioe) {
			panel.error("IO Exception in stop");
		}
	}
	
	/**
	 * Send a RANDOM_MOVE event to the NXT
	 */
	public void randomMove() {
		if (!connected) return;
		try {
			synchronized(receiver) {
				dos.writeByte(NavEvent.RANDOM_MOVE.ordinal());
				dos.flush();
			}
	    } catch (IOException ioe) {
			panel.error("IO Exception in randomMove");
		}
	}
	
	/**
	 * Tell the NXT to keep making random moves until the robot is localized
	 */
	public void localize() {
		if (!connected) return;
		try {
			synchronized(receiver) {
				dos.writeByte(NavEvent.LOCALIZE.ordinal());
				dos.flush();
			}
	    } catch (IOException ioe) {
			panel.error("IO Exception in localize");
		}
	}
	
	/**
	 * Send a TAKE_READINGS event to the NXT
	 */
	public void takeReadings() {
		if (!connected) return;
		try {
			synchronized(receiver) {
				dos.writeByte(NavEvent.TAKE_READINGS.ordinal());
				dos.flush();
			}
	    } catch (IOException ioe) {
			panel.error("IO Exception in takeReadings");
		}
	}
	
	/**
	 * Send a route to the NXT and follow it
	 */
	public void followPath() {
		if (path == null) return;
		if (!connected) return;
		try {
			if (debug) {
				panel.log("Sending path");
				panel.log("Pose:" + currentPose);
				for(Waypoint wp: path) {
					panel.log("Waypoint:" + wp.x + "," + wp.y + "," + wp.getHeading() + "," + wp.isHeadingRequired());
				}
			}
			synchronized(receiver) {
				dos.writeByte(NavEvent.FOLLOW_PATH.ordinal());
				path.dumpObject(dos);
			}
	    } catch (IOException ioe) {
			panel.error("IO Exception in followPath");
		}
	}
	
	/**
	 * Start the navigator following a path
	 */
	public void startNavigator() {
		if (!connected) return;
		try {
			synchronized(receiver) {
				dos.writeByte(NavEvent.START_NAVIGATOR.ordinal());
				dos.flush();
			}
	    } catch (IOException ioe) {
			panel.error("IO Exception in startNavigator");
		}
	}
	
	/**
	 * Send a FIND_PATH event to the NXT
	 */
	public void findPath(Waypoint wp) {
		if (!connected) return;
		try {
			synchronized(receiver) {
				dos.writeByte(NavEvent.FIND_PATH.ordinal());
				wp.dumpObject(dos);
			}
	    } catch (IOException ioe) {
			panel.error("IO Exception in findPath");
		}
	}
	
	/**
	 * Send a CLEAR_PATH event to the NXT
	 */
	public void clearPath() {
		if (!connected) return;
		try {
			synchronized(receiver) {
				dos.writeByte(NavEvent.CLEAR_PATH.ordinal());
				dos.flush();
			}
	    } catch (IOException ioe) {
			panel.error("IO Exception in clearPath");
		}
	}
	
	/**
	 * Calculate the path with the Node path finder
	 */
	public void calculatePath() {
		if (currentPose == null || target == null) return;
		if (pf == null) return;
		try {
			path = pf.findRoute(currentPose, target);
			panel.repaint();
		} catch (DestinationUnreachableException e) {
			path = null;
			panel.error("Destination unreachable");
		}
	}
	
	/**
	 * Send a system sound
	 */
	public void sendSound(int code) {
		if (!connected) return;
		try {
			synchronized(receiver) {
				dos.writeByte(NavEvent.SOUND.ordinal());
				dos.writeInt(code);
				dos.flush();
			}
	    } catch (IOException ioe) {
			panel.error("IO Exception in sendSound");
		}
	}
	
	/**
	 * Get the remote battery voltage
	 */
	public void getRemoteBattery() {
		if (!connected) return;
		try {
			synchronized(receiver) {
				dos.writeByte(NavEvent.GET_BATTERY.ordinal());
				dos.flush();
			}
	    } catch (IOException ioe) {
			panel.error("IO Exception in getRemoteBattery");
		}
	}
		
	/**
	 * Shut down the receiver thread
	 */
	public void shutDown() {
		running = false;
	}
	
	/**
	 * Clear all variable data
	 */
	public void clear() {
		moves = new ArrayList<Move>();
		poses = new ArrayList<Pose>();
		features = new ArrayList<Point>();
		waypoints = new ArrayList<Waypoint>();
		target = null;
		path = null;
		start = null;
		destination = null;
		if (map != null){
			mesh = new FourWayGridMesh(map, gridSpace,clearance);
			nodes = mesh.getMesh();
			pf = new NodePathFinder(alg, mesh);
		}
		closest = -1;
	}
	
	/**
	 * Runnable class to receive events from the NXT
	 * 
	 * @author Lawrie Griffiths
	 *
	 */
	class Receiver implements Runnable {
		public void run() {
			while(running) {
				try {
					byte event = dis.readByte();
					NavEvent navEvent = NavEvent.values()[event];
					if (debug) panel.log("Event received:" +  navEvent.name());
					synchronized(this) {
						switch (navEvent) {
						case MOVE_STARTED: // Get planned move
							lastPlannedMove.loadObject(dis);
							break;
						case MOVE_STOPPED: // Get executed move
							lastMove.loadObject(dis);
							moves.add(lastMove);
							break;
						case SET_POSE: // Get a new pose from the NXT
							currentPose.loadObject(dis);
							if (debug) panel.log(currentPose.toString());
							poses.add(new Pose(currentPose.getX(), currentPose.getY(), currentPose.getHeading()));
							break;
						case PARTICLE_SET: // Get a particle set from the NXT
							particles.loadObject(dis);
							if (mcl != null) mcl.estimatePose();
							break;
						case RANGE_READINGS: // Get the range readings from the NXT
							readings.loadObject(dis);
							break;
						case WAYPOINT_REACHED: // Get the waypoint reached
							if (reached == null) reached = new Waypoint(0,0);
							reached.loadObject(dis);
							break;
						case CLOSEST_PARTICLE: // Get details of a specific particle
							closest = dis.readInt();
							//System.out.println("Closest: " + closest);
							particleReadings.loadObject(dis);
							weight = dis.readFloat();
							
							if (debug) {
								for(RangeReading r:particleReadings) {
									panel.log(r.getAngle() + ":" + r.getRange());
								}
							
								panel.log("weight = " + weight);
							}
							break;
						case ESTIMATED_POSE: // Get the MCL estimated pose data
							mcl.loadObject(dis);
							break;
						case FEATURE_DETECTED: // Get feature detected
							feature.loadObject(dis);
							float range = feature.getRangeReading().getRange();	
							Pose pose = feature.getPose();
							if (debug) panel.log("Pose = " + pose);
							if (debug) panel.log("Range = " + range);
							Point p = new Point((float) (pose.getX() + (range  * Math.cos(Math.toRadians(pose.getHeading())))),
												(float) (pose.getY() + (range  * Math.sin(Math.toRadians(pose.getHeading())))));
							if (debug) panel.log("Point = " + p);
							features.add(p);
							break;
						case PATH: // Get a path generated on the NXT
							path.loadObject(dis);
							break;
						case BATTERY:
							voltage = dis.readFloat();
							break;
						}
						// Signal that an event has been received
						panel.eventReceived(navEvent);
						// Refresh the navigation panel with the updated model data
						panel.repaint();
					}
				} catch (IOException ioe) {
					panel.fatal("IOException in receiver: " + ioe);
				}
			}		
		}	
	}
}

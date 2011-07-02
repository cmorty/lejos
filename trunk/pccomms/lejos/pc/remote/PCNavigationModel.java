package lejos.pc.remote;

import java.awt.Dimension;
import java.io.*;
import lejos.geom.Rectangle;
import lejos.pc.comm.*;
import lejos.robotics.NavigationModel;
import lejos.robotics.mapping.*;
import lejos.robotics.navigation.*;
import lejos.robotics.localization.*;

/**
 * The PCNavigationModel holds all the navigation data that is transmitted as events,
 * to and from a NXT brick.
 * 
 * It has methods to generate events, and a Receiver thread to reeive events from the NXT.
 * 
 * There is a NavigationPanel associated with the model. Whenever data in the model is updated,
 * the NavigationPanel is repainted with the new data.
 * 
 * @author Lawrie Grifiths
 *
 */
public class PCNavigationModel extends NavigationModel {
	protected NavigationPanel panel;
	protected MCLPoseProvider mcl;
	protected Move lastMove = new Move(0,0,false);
	protected int closest = -1;
	protected boolean connected = false;
	
	/**
	 * Create the model and associate the navigation panel with it
	 * 
	 * @param panel the NavigationPanel
	 */
	public PCNavigationModel(NavigationPanel panel) {
		this.panel = panel;
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
	 * Send a GET_PARTICLES event to the NXT
	 */
	public void getRemoteParticles() {
		if (!connected) return;
		try {
			panel.log("Getting particles");
			dos.writeByte(NavEvent.GET_PARTICLES.ordinal());
			dos.flush();
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
			dos.writeByte(NavEvent.FIND_CLOSEST.ordinal());
			dos.writeFloat(x);
			dos.writeFloat(y);
			dos.flush();
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
			dos.writeByte(NavEvent.PARTICLE_SET.ordinal());
			particles.dumpObject(dos);
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
  
		panel.log("Connected to " + nxtName);
  
		dis = new DataInputStream(conn.getInputStream());
		dos = new DataOutputStream(conn.getOutputStream());
		connected = true;
		
		// Startr the receiver thread
		Thread receiver = new Thread(new Receiver());
		receiver.setDaemon(true);
		receiver.start();
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
			System.out.println("Map file is " + mapFile.getAbsolutePath());
			if (!mapFile.exists()) {
				panel.log(mapFile.getAbsolutePath() + " does not exist");
				return null;
			}
			FileInputStream is = new FileInputStream(mapFile);
			SVGMapLoader mapLoader = new SVGMapLoader(is);
			map = mapLoader.readLineMap();
			Rectangle boundingRect = map.getBoundingRect();
			panel.setMapSize(new Dimension((int) (boundingRect.width * 2), (int) (boundingRect.height * 2)));
			panel.repaint();
			if (mcl != null) mcl.setMap(map);
			if (connected) {
				dos.writeByte(NavEvent.LOAD_MAP.ordinal());
				map.dumpObject(dos);
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
			dos.writeByte(NavEvent.GOTO.ordinal());
			target = wp;
			wp.dumpObject(dos);
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
			dos.writeByte(NavEvent.TRAVEL.ordinal());
			dos.writeFloat(distance);
			dos.flush();
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
			dos.writeByte(NavEvent.ROTATE.ordinal());
			dos.writeFloat(angle);
			dos.flush();
		} catch (IOException ioe) {
			panel.error("IO Exception in rotate");
		}
	}
	
	/**
	 * Send a GET_POSE event to the NXT
	 */
	public void getPose() {
		if (dos == null) return;
		try {
			dos.writeByte(NavEvent.GET_POSE.ordinal());
			dos.flush();
	    } catch (IOException ioe) {
			panel.error("IO Exception in getPose");
		}
	}
	
	/**
	 * SEnd a GET_ESTIMATED_POSE event to the NXT
	 */
	public void getEstimatedPose() {
		if (!connected) return;
		try {
			dos.writeByte(NavEvent.GET_ESTIMATED_POSE.ordinal());
			dos.flush();
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
			dos.writeByte(NavEvent.GET_READINGS.ordinal());
			dos.flush();
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
		if (!connected) return;
		currentPose = p;
		try {
			dos.writeByte(NavEvent.SET_POSE.ordinal());
			currentPose.dumpObject(dos);
	    } catch (IOException ioe) {
			panel.error("IO Exception in getPose");
		}
	}
	
	/**
	 * Send a RANDOM_MOVE event to the NXT
	 */
	public void randomMove() {
		if (!connected) return;
		try {
			dos.writeByte(NavEvent.RANDOM_MOVE.ordinal());
			dos.flush();
	    } catch (IOException ioe) {
			panel.error("IO Exception in randomMove");
		}
	}
	
	/**
	 * Send a TAKE_READINGS event to the NXT
	 */
	public void takeReadings() {
		if (!connected) return;
		try {
			dos.writeByte(NavEvent.TAKE_READINGS.ordinal());
			dos.flush();
	    } catch (IOException ioe) {
			panel.error("IO Exception in takeReadings");
		}
	}
	
	/**
	 * Runnable class to receive events from the NXT
	 * 
	 * @author Lawrie Griffiths
	 *
	 */
	class Receiver implements Runnable {
		public void run() {
			while(true) {
				try {
					byte event = dis.readByte();
					panel.log("Event received:" +  NavEvent.values()[event].name());
					if (event == NavEvent.MOVE_STARTED.ordinal() || event == NavEvent.MOVE_STOPPED.ordinal()) {
						panel.log("Reading Move object");
						lastMove.loadObject(dis);
					} else if (event == NavEvent.SET_POSE.ordinal()) {
						currentPose.loadObject(dis);
					} else if (event == NavEvent.PARTICLE_SET.ordinal()) {
						particles.loadObject(dis);
					} else if (event == NavEvent.RANGE_READINGS.ordinal()) {
						readings.loadObject(dis);
					} else if (event == NavEvent.WAYPOINT_REACHED.ordinal()) {
						target.loadObject(dis);
					} else if (event == NavEvent.CLOSEST_PARTICLE.ordinal()) {
						closest = dis.readInt();
					} else if (event == NavEvent.ESTIMATED_POSE.ordinal()) {
						mcl.loadObject(dis);
					}
					panel.repaint();
				} catch (Exception ioe) {
					panel.fatal("Exception in receiver: " + ioe);
				}
			}		
		}	
	}
}

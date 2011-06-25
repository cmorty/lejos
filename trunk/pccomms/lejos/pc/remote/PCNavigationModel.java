package lejos.pc.remote;

import java.awt.Dimension;
import java.io.*;
import lejos.geom.Rectangle;
import lejos.pc.comm.*;
import lejos.robotics.NavigationModel;
import lejos.robotics.mapping.*;
import lejos.robotics.navigation.*;
import lejos.robotics.localization.*;

public class PCNavigationModel extends NavigationModel {
	protected NavigationPanel panel;
	protected MCLPoseProvider mcl;
	protected Move lastMove = new Move(0,0,false);
	protected int closest = -1;
	protected boolean connected = false;
	
	public PCNavigationModel() {
		Thread receiver = new Thread(new Receiver());
		receiver.setDaemon(true);
		receiver.start();
	}
	
	public void setPanel(NavigationPanel panel) {
		this.panel = panel;
	}
	
	public MCLPoseProvider getMCL() {
		return mcl;
	}
	
	public void setMCL(MCLPoseProvider mcl) {
		this.mcl = mcl;
	}
	
	public void generateParticles() {
		mcl.generateParticles();
		particles = mcl.getParticles();
		panel.repaint();
		if (dos == null) return;
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
		NXTConnector conn = new NXTConnector();

		if (!conn.connectTo(nxtName, null, NXTCommFactory.BLUETOOTH)) {
			panel.error("NO NXT found");
			return;
		}
  
		panel.log("Connected to " + nxtName);
  
		dis = new DataInputStream(conn.getInputStream());
		dos = new DataOutputStream(conn.getOutputStream());
		connected = true;
	}
	
	public boolean isConnected() {
		return connected;
	}
	
	public LineMap loadMap(String mapFileName) {
		try {
			File mapFile = new File(mapFileName);
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
			if (dos != null) {
				dos.writeByte(NavEvent.LOAD_MAP.ordinal());
				map.dumpObject(dos);
			}
			return map;
		} catch (Exception ioe) {
			panel.error("Exception in loadMap:" + ioe);
			return null;
		}
		
	}
	
	public void goTo(Waypoint wp) {
		if (dos == null) return;
		try {
			dos.writeByte(NavEvent.GOTO.ordinal());
			target = wp;
			wp.dumpObject(dos);
		} catch (IOException ioe) {
			panel.error("IO Exception in goTo");
		}		
	}
	
	public void travel(float distance) {
		if (dos == null) return;
		try {
			dos.writeByte(NavEvent.TRAVEL.ordinal());
			dos.writeFloat(distance);
			dos.flush();
		} catch (IOException ioe) {
			panel.error("IO Exception in travel");
		}
	}
	
	public void rotate(float angle) {
		if (dos == null) return;
		try {
			dos.writeByte(NavEvent.ROTATE.ordinal());
			dos.writeFloat(angle);
			dos.flush();
		} catch (IOException ioe) {
			panel.error("IO Exception in rotate");
		}
	}
	
	public void getPose() {
		if (dos == null) return;
		try {
			dos.writeByte(NavEvent.GET_POSE.ordinal());
			dos.flush();
	    } catch (IOException ioe) {
			panel.error("IO Exception in getPose");
		}
	}
	
	public void setPose(Pose p) {
		if (dos == null) return;
		currentPose = p;
		try {
			dos.writeByte(NavEvent.SET_POSE.ordinal());
			currentPose.dumpObject(dos);
	    } catch (IOException ioe) {
			panel.error("IO Exception in getPose");
		}
	}
	
	public void randomMove() {
		if (dos == null) return;
		try {
			dos.writeByte(NavEvent.RANDOM_MOVE.ordinal());
			dos.flush();
	    } catch (IOException ioe) {
			panel.error("IO Exception in randomMove");
		}
	}
	
	public void takeReadings() {
		if (dos == null) return;
		try {
			dos.writeByte(NavEvent.TAKE_READINGS.ordinal());
			dos.flush();
	    } catch (IOException ioe) {
			panel.error("IO Exception in takeReadings");
		}
	}
	
	class Receiver implements Runnable {
		public void run() {
			while(true) {
				try {
					if (dis == null) {
						Thread.sleep(1000);
						continue;
					}
					byte event = dis.readByte();
					panel.log("Event received:" +  NavEvent.values()[event].name());
					if (event == NavEvent.MOVE_STARTED.ordinal() || event == NavEvent.MOVE_STOPPED.ordinal()) {
						panel.log("Reading Move object");
						lastMove.loadObject(dis);
					} else if (event == NavEvent.SET_POSE.ordinal()) {
						currentPose.loadObject(dis);
					} else if (event == NavEvent.PARTICLE_SET.ordinal()) {
						if (particles != null) {
							particles.loadObject(dis);
						}
					} else if (event == NavEvent.RANGE_READINGS.ordinal()) {
						readings.loadObject(dis);
					} else if (event == NavEvent.WAYPOINT_REACHED.ordinal()) {
						target.loadObject(dis);
					} else if (event == NavEvent.CLOSEST_PARTICLE.ordinal()) {
						if (particles != null) {
							closest = dis.readInt();
						}
					} else if (event == NavEvent.ESTIMATED_POSE.ordinal()) {
						if (mcl != null) {
							mcl.loadObject(dis);
						}
					}
					panel.repaint();
				} catch (Exception ioe) {
					panel.fatal("Exception in receiver: " + ioe);
				}
			}		
		}	
	}
}

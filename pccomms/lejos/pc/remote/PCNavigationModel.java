package lejos.pc.remote;

import java.awt.Dimension;
import java.io.*;

import lejos.geom.Rectangle;
import lejos.pc.comm.*;
import lejos.robotics.NavigationModel;
import lejos.robotics.RangeReadings;
import lejos.robotics.mapping.*;
import lejos.robotics.navigation.*;
import lejos.robotics.localization.*;

public class PCNavigationModel extends NavigationModel {
	protected Pose currentPose = new Pose(0,0,0);
	protected WayPoint target = new WayPoint(0,0);
	protected NavigationPanel panel;
	protected MCLParticleSet particles;
	protected MCLPoseProvider mcl;
	protected Move lastMove = new Move(0,0,false);
	protected RangeReadings readings = new RangeReadings(0);
	protected int closest = -1;
	
	public PCNavigationModel() {
		Thread receiver = new Thread(new Receiver());
		receiver.setDaemon(true);
		receiver.start();
	}
	
	public void setPanel(NavigationPanel panel) {
		this.panel = panel;
	}
	
	public void setRobotPose(Pose p) {
		currentPose = p;
	}
	
	public Pose getRobotPose() {
		return currentPose;
	}
	
	public MCLParticleSet getParticles() {
		return particles;
	}
	
	public void setParticleSet(MCLParticleSet particles) {
		this.particles = particles;
	}
	
	public MCLPoseProvider getMCL() {
		return mcl;
	}

	/**
	 * Connect to the NXT
	 */
	public void connect(String nxtName) {
		NXTConnector conn = new NXTConnector();

		if (!conn.connectTo(nxtName, null, NXTCommFactory.BLUETOOTH)) {
			panel.error("NO NXT found");;
		}
  
		panel.log("Connected to " + nxtName);
  
		dis = conn.getDataIn();
		dos = conn.getDataOut();
	}
	
	public void loadMap(String mapFileName) {
		try {
			File mapFile = new File(mapFileName);
			if (!mapFile.exists()) {
				panel.log(mapFile.getAbsolutePath() + " does not exist");
				return;
			}
			FileInputStream is = new FileInputStream(mapFile);
			SVGMapLoader mapLoader = new SVGMapLoader(is);
			map = mapLoader.readLineMap();
			Rectangle boundingRect = map.getBoundingRect();
			panel.setMapSize(new Dimension((int) (boundingRect.width * 2), (int) (boundingRect.height * 2)));
			panel.repaint();
			sendEvent(NavEvent.LOAD_MAP);
			if (dos != null) map.dumpObject(dos);
		} catch (Exception ioe) {
			panel.error("Exception in loadMap:" + ioe);
		} 
	}
	
	protected void sendEvent(NavEvent e) {
		if (dos == null) return;
		try {
			dos.writeByte(e.ordinal());
		} catch (IOException ioe) {
			panel.error("IO Exception in sendByte");
		}
	}
	
	public void goTo(WayPoint wp) {
		try {
			dos.writeByte(NavEvent.GOTO.ordinal());
			target = wp;
			wp.dumpObject(dos);
		} catch (IOException ioe) {
			panel.error("IO Exception in goTo");
		}		
	}
	
	public void travel(float distance) {
		System.out.println("Sending travel " + distance);
		try {
			dos.writeByte(NavEvent.TRAVEL.ordinal());
			dos.writeFloat(distance);
			dos.flush();
		} catch (IOException ioe) {
			panel.error("IO Exception in travel");
		}
	}
	
	public void rotate(float angle) {
		System.out.println("Sending rotate " + angle);
		try {
			dos.writeByte(NavEvent.ROTATE.ordinal());
			dos.writeFloat(angle);
			dos.flush();
		} catch (IOException ioe) {
			panel.error("IO Exception in rotate");
		}
	}
	
	public void getPose() {
		try {
			dos.writeByte(NavEvent.GET_POSE.ordinal());
	    } catch (IOException ioe) {
			panel.error("IO Exception in getPose");
		}
	}
	
	public void setPose(Pose p) {
		currentPose = p;
		try {
			dos.writeByte(NavEvent.SET_POSE.ordinal());
			currentPose.dumpObject(dos);
	    } catch (IOException ioe) {
			panel.error("IO Exception in getPose");
		}
	}
	
	public void randomMove() {
		try {
			dos.writeByte(NavEvent.RANDOM_MOVE.ordinal());
			dos.flush();
	    } catch (IOException ioe) {
			panel.error("IO Exception in randomMove");
		}
	}
	
	public void takeReadings() {
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

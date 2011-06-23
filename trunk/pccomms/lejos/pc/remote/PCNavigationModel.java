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
	protected Pose targetPose = new Pose(0,0,0), currentPose = new Pose(0,0,0);
	protected NavigationPanel panel;
	protected MCLParticleSet particles;
	protected MCLPoseProvider mcl;
	protected Move lastMove = new Move(0,0,false);
	
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
			sendEvent(Event.LOAD_MAP);
			if (dos != null) map.dumpObject(dos);
		} catch (Exception ioe) {
			panel.error("Exception in loadMap:" + ioe);
		} 
	}
	
	protected void sendEvent(Event e) {
		if (dos == null) return;
		try {
			dos.writeByte(e.ordinal());
		} catch (IOException ioe) {
			panel.error("IO Exception in sendByte");
		}
	}
	
	public void goTo(Pose p) {
		try {
			dos.writeByte(Event.GOTO.ordinal());
			targetPose = p;
			p.dumpObject(dos);
		} catch (IOException ioe) {
			panel.error("IO Exception in goTo");
		}		
	}
	
	public void travel(float distance) {
		System.out.println("Sending travel " + distance);
		try {
			dos.writeByte(Event.TRAVEL.ordinal());
			dos.writeFloat(distance);
			dos.flush();
		} catch (IOException ioe) {
			panel.error("IO Exception in travel");
		}
	}
	
	public void rotate(float angle) {
		System.out.println("Sending rotate " + angle);
		try {
			dos.writeByte(Event.ROTATE.ordinal());
			dos.writeFloat(angle);
			dos.flush();
		} catch (IOException ioe) {
			panel.error("IO Exception in rotate");
		}
	}
	
	public void getPose() {
		try {
			dos.writeByte(Event.GET_POSE.ordinal());
	    } catch (IOException ioe) {
			panel.error("IO Exception in getPose");
		}
	}
	
	public void setPose(Pose p) {
		currentPose = p;
		try {
			dos.writeByte(Event.SET_POSE.ordinal());
			currentPose.dumpObject(dos);
	    } catch (IOException ioe) {
			panel.error("IO Exception in getPose");
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
					panel.log("Event received:" +  event);
					if (event == Event.MOVE_STARTED.ordinal() || event == Event.MOVE_STOPPED.ordinal()) {
						panel.log("Reading Move object");
						lastMove.loadObject(dis);
					}
				} catch (Exception ioe) {
					panel.fatal("Exception in receiver: " + ioe);
				}
			}
			
		}	
	}
}

package lejos.robotics;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.NXTCommConnector;
import lejos.nxt.comm.NXTConnection;
import lejos.robotics.localization.MCLParticleSet;
import lejos.robotics.localization.PoseProvider;
import lejos.robotics.mapping.LineMap;
import lejos.robotics.navigation.*;

public class NXTNavigationModel implements MoveListener{
	protected LineMap map;
	protected String nxtName;
	protected DataInputStream dis;
	protected DataOutputStream dos;
	protected enum Event {LOAD_MAP, GOTO, TRAVEL, ROTATE, STOP, GET_POSE, SET_POSE, RANDOM_MOVE, TAKE_READINGS, MOVE_STARTED, MOVE_STOPPED}
	protected Pose targetPose, currentPose;
	protected MCLParticleSet particles;
	protected PathController navigator;
	protected MoveController pilot;
	protected PoseProvider pp; 
	
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
	
	public boolean hasMap() {
		return map != null;
	}
	
	public LineMap getMap() {
		return map;
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
					log("Event received:" +  event);
					
					if (event ==  Event.LOAD_MAP.ordinal()) {
						if (map == null) map = new LineMap();
						map.loadObject(dis);
					} else if (event == Event.GOTO.ordinal()) {
						if (navigator != null) {
							float x = dis.readFloat();
							float y = dis.readFloat();
							float heading = dis.readFloat();
							navigator.goTo(new WayPoint(new Pose(x,y,heading)));
						}
					} else if (event == Event.STOP.ordinal()) {
						if (pilot != null) {
							pilot.stop();
						}
					} else if (event == Event.TRAVEL.ordinal()) {
						if (pilot != null) {
							float distance = dis.readFloat();
							pilot.travel(distance);
						}
					} else if (pilot != null && pilot instanceof RotateMoveController) {
						float angle = dis.readFloat();
						((RotateMoveController) pilot).rotate(angle);
					}
				} catch (Exception ioe) {
					error("Exception in receiver");
				}
			}
			
		}	
	}

	public void moveStarted(Move event, MoveProvider mp) {
		try {
			dos.writeByte(Event.MOVE_STARTED.ordinal());
			dos.writeByte(event.getMoveType().ordinal());
			dos.writeFloat(event.getTravelSpeed());
			dos.writeFloat(event.getRotateSpeed());
			dos.writeFloat(event.getDistanceTraveled());
			dos.writeFloat(event.getAngleTurned());
			dos.flush();
		} catch (IOException ioe) {
			System.out.println("IOException in moveStarted");	
		}	
	}

	public void moveStopped(Move event, MoveProvider mp) {
		try {
			dos.writeByte(Event.MOVE_STOPPED.ordinal());
			dos.writeByte(event.getMoveType().ordinal());
			dos.writeFloat(event.getTravelSpeed());
			dos.writeFloat(event.getRotateSpeed());
			dos.writeFloat(event.getDistanceTraveled());
			dos.writeFloat(event.getAngleTurned());
			dos.flush();
		} catch (IOException ioe) {
			System.out.println("IOException in moveStarted");	
		}	
	}
}

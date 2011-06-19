package lejos.robotics;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.NXTCommConnector;
import lejos.nxt.comm.NXTConnection;
import lejos.robotics.localization.MCLParticleSet;
import lejos.robotics.mapping.LineMap;
import lejos.robotics.navigation.*;

public class NXTNavigationModel implements MoveListener{
	protected LineMap map;
	protected String nxtName;
	protected DataInputStream dis;
	protected DataOutputStream dos;
	protected enum Event {LOAD_MAP, GOTO, MOVE_STARTED, MOVE_STOPPED}
	protected Pose targetPose, currentPose;
	protected MCLParticleSet particles;
	protected PathController navigator;
	
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
					}
				} catch (Exception ioe) {
					error("Exception in receiver");
				}
			}
			
		}	
	}


	public void moveStarted(Move event, MoveProvider mp) {
		// TODO Auto-generated method stub
		
	}

	public void moveStopped(Move event, MoveProvider mp) {
		// TODO Auto-generated method stub
		
	}
}

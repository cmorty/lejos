package lejos.robotics;

import java.io.IOException;

import lejos.nxt.Button;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.NXTCommConnector;
import lejos.nxt.comm.NXTConnection;
import lejos.robotics.localization.MCLParticleSet;
import lejos.robotics.localization.PoseProvider;
import lejos.robotics.mapping.LineMap;
import lejos.robotics.navigation.*;

public class NXTNavigationModel extends NavigationModel implements MoveListener{
	protected Pose targetPose = null, currentPose = new Pose(0,0,0);
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
	
	public void fatal(String message) {
		System.out.println(message);
		Button.waitForPress();
		System.exit(1);
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
					
					if (event ==  NavEvent.LOAD_MAP.ordinal()) {
						if (map == null) map = new LineMap();
						map.loadObject(dis);
					} else if (event == NavEvent.GOTO.ordinal()) {
						if (navigator != null) {
							targetPose = new Pose(0,0,0);
							targetPose.loadObject(dis);
							navigator.goTo(new WayPoint(targetPose));
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
					} else if (pilot != null && pilot instanceof RotateMoveController) {
						float angle = dis.readFloat();
						((RotateMoveController) pilot).rotate(angle);
					} else if (event == NavEvent.GET_POSE.ordinal() && pp != null) {
						dos.writeByte(NavEvent.SET_POSE.ordinal());
						pp.getPose().dumpObject(dos);
					} else if (event == NavEvent.SET_POSE.ordinal() && pp != null) {
						currentPose.loadObject(dis);
						pp.setPose(currentPose);
					}
				} catch (Exception ioe) {
					fatal("Exception in receiver");
				}
			}
			
		}	
	}

	public void moveStarted(Move event, MoveProvider mp) {
		try {
			dos.writeByte(NavEvent.MOVE_STARTED.ordinal());
			event.dumpObject(dos);
		} catch (IOException ioe) {
			fatal("IOException in moveStarted");	
		}	
	}

	public void moveStopped(Move event, MoveProvider mp) {
		try {
			dos.writeByte(NavEvent.MOVE_STOPPED.ordinal());
			event.dumpObject(dos);
		} catch (IOException ioe) {
			fatal("IOException in moveStarted");	
		}	
	}
}

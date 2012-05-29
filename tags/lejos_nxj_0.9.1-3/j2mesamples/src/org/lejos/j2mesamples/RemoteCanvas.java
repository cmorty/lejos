package org.lejos.j2mesamples;
import javax.microedition.lcdui.*;

import lejos.j2me.comm.*;
import lejos.nxt.*;
import lejos.nxt.remote.*;
import java.io.*;

public class RemoteCanvas extends Canvas{
	
	private final byte motorPower = 50;
	private String message = "Use arrow or number keys";
	private String detail = "detail";
	private NXTCommand nxtCommand = NXTCommand.getSingleton();
	UltrasonicSensor sonic;
	LightSensor light;
	TouchSensor touch;
	SoundSensor sound;
	
	public RemoteCanvas(NXTComm nxtComm, NXTInfo nxt) throws NXTCommException {
		nxtComm.open(nxt, NXTComm.LCP);
		nxtCommand.setNXTComm(nxtComm);
		sonic = new UltrasonicSensor(SensorPort.S1);
		light = new LightSensor(SensorPort.S2);
		sound = new SoundSensor(SensorPort.S3);
		touch = new TouchSensor(SensorPort.S4);
	}
	
	protected void paint(Graphics g) {
		g.setColor(224,229,233); // Lt. Gray
	    g.fillRect(0, 0, this.getWidth(), this.getHeight());
	    g.setColor(204,88,39); // Dk. Orange
	    g.drawString(message, 10, 10, Graphics.TOP|Graphics.LEFT);
	    g.drawString(detail, 10, 30, Graphics.TOP|Graphics.LEFT);
	}

	private void controlRobot(int gameKey){
		try {
			switch (gameKey) {
				case Canvas.UP:
					setPower(1, -motorPower);
					setPower(2, -motorPower);
					break;
				case Canvas.DOWN:
					setPower(1, motorPower);
					setPower(2, motorPower);
					break;
				case Canvas.RIGHT:
					setPower(1, motorPower);
					setPower(2, -motorPower);
					break;
				case Canvas.LEFT:
					setPower(1,- motorPower);
					setPower(2, motorPower);
					break;
				case Canvas.FIRE:
					Sound.playTone(500,1000);
					int milliVolts = Battery.getVoltageMilliVolt();
					detail = "Battery: " + milliVolts;
					break;
				case Canvas.GAME_A:
					int dist = sonic.getDistance();
					detail = "Distance: " + dist;
					break;
				case Canvas.GAME_B:
					int l = light.readValue();
					detail = "Light: " + l;
					break;
				case Canvas.GAME_C:
					int s = sound.readValue();
					detail = "Sound: " + s;
					break;
				case Canvas.GAME_D:
					boolean p = touch.isPressed();
					detail = "Touch: " + p;
					break;
			}
			repaint();
		} catch (IOException ioe) {
			message = ioe.getMessage();
		}
	}
	
	protected void keyPressed(int key) {
		int gameKey = getGameAction(key);
		message = "Game key code: " + gameKey;
		repaint();
		controlRobot(gameKey);
	}
	
	protected void keyReleased(int key){
		try {
			setPower(1, 0);
			setPower(2, 0);
		} catch (IOException ioe) {
			message = ioe.getMessage();
		}
	}
	
	private void setPower(int port, int power) throws IOException {
		nxtCommand.setOutputState((byte) port, (byte) power, 0x05, 0, 0, 0x20, 0);
	}
}

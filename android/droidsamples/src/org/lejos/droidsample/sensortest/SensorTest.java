package org.lejos.droidsample.sensortest;

import org.lejos.droidsamples.DroidSamples;

import lejos.nxt.LightSensor;
import lejos.nxt.SensorPort;
import lejos.nxt.SoundSensor;
import lejos.nxt.TouchSensor;
import lejos.nxt.UltrasonicSensor;
import lejos.nxt.remote.NXTCommand;
import lejos.pc.comm.NXTCommandConnector;
import lejos.pc.comm.NXTConnector;

public class SensorTest extends Thread {
	protected static final String TAG = "SensorTest";
	NXTConnector conn;

	@Override
	public void run() {
		conn = DroidSamples.connect(DroidSamples.CONN_TYPE.LEGO_LCP);
		NXTCommandConnector.setNXTCommand(new NXTCommand(conn.getNXTComm()));

		LightSensor light = new LightSensor(SensorPort.S1);
		SoundSensor sound = new SoundSensor(SensorPort.S2);
		TouchSensor touch = new TouchSensor(SensorPort.S3);
		UltrasonicSensor sonic = new UltrasonicSensor(SensorPort.S4);
		
		while(sound.readValue() < 90) {		
			DroidSamples.displayMessage("light = " + light.readValue()
			+ " sound = " + sound.readValue()
			+ " touch = " + touch.isPressed()
			+ " distance = " + sonic.getDistance());
			try {
				Thread.sleep(1000);
			} catch (InterruptedException ie) {}
		}
		closeConnection();
		DroidSamples.displayToast("SensorTest finished");
	}

	public void closeConnection() {
		try {
			conn.getNXTComm().close();
		} catch (Exception e) {
		} finally {
			conn = null;
		}
	}
}

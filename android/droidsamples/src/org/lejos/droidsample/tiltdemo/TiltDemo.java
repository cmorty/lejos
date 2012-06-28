package org.lejos.droidsample.tiltdemo;

import org.lejos.droidsamples.DroidSamples;
import org.lejos.droidsamples.DroidSamples.CONN_TYPE;

import lejos.nxt.SensorPort;
import lejos.nxt.addon.AccelMindSensor;
import lejos.nxt.remote.NXTCommand;
import lejos.pc.comm.NXTCommandConnector;
import lejos.pc.comm.NXTConnector;
import android.util.Log;

public class TiltDemo extends Thread {
	protected static final String TAG = "TiltDemo";
	NXTConnector conn;

	@Override
	public void run() {
		conn = DroidSamples.connect(DroidSamples.CONN_TYPE.LEGO_LCP);
		NXTCommandConnector.setNXTCommand(new NXTCommand(conn.getNXTComm()));

		AccelMindSensor a = new AccelMindSensor(SensorPort.S1);
		
		System.out.println("Prod ID " + a.getProductID());
		System.out.println("Vendor ID " + a.getVendorID());
		System.out.println("Version " + a.getVersion());
				
		while(!DroidSamples.canceled()) {
			DroidSamples.displayMessage("TILT: x=" + a.getXTilt() + "  y=" + a.getYTilt() + " z=" + a.getZTilt()
			+ " ACCEL: x=" + a.getXAccel() + "  y=" + a.getYAccel() + " z=" + a.getZAccel());
		}
		
		closeConnection();
		DroidSamples.displayToast("TiltDemo finished");
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

package org.lejos.droidsample.acceldemo;

import org.lejos.droidsamples.DroidSamples;
import org.lejos.droidsamples.DroidSamples.CONN_TYPE;

import lejos.nxt.SensorPort;
import lejos.nxt.addon.SensorSelector;
import lejos.nxt.addon.SensorSelectorException;
import lejos.nxt.remote.NXTCommand;
import lejos.pc.comm.NXTCommandConnector;
import lejos.pc.comm.NXTConnector;
import lejos.robotics.Accelerometer;
import android.util.Log;

public class AccelDemo extends Thread {
	protected static final String TAG = "AccelDemo";
	NXTConnector conn;

	@Override
	public void run() {
		conn = DroidSamples.connect(DroidSamples.CONN_TYPE.LEGO_LCP);
		NXTCommandConnector.setNXTCommand(new NXTCommand(conn.getNXTComm()));

		try {
			Accelerometer a = SensorSelector.createAccelerometer(SensorPort.S1);
			while(!DroidSamples.canceled()) {
				DroidSamples.displayMessage("ACCEL: x=" + a.getXAccel() + "  y=" + a.getYAccel() + " z=" + a.getZAccel());
			}
		} catch (SensorSelectorException e) {
			Log.e(TAG, "Error selecting sensor");
		}

		DroidSamples.clearMessage();
		closeConnection();
		DroidSamples.displayToast("AccelDemo finished");
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

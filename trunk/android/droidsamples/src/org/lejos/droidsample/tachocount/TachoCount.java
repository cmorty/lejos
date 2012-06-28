package org.lejos.droidsample.tachocount;

import java.io.IOException;

import org.lejos.droidsamples.DroidSamples;
import org.lejos.droidsamples.DroidSamples.CONN_TYPE;

import lejos.nxt.Motor;
import lejos.nxt.Sound;
import lejos.nxt.remote.NXTCommand;
import lejos.pc.comm.NXTCommandConnector;
import lejos.pc.comm.NXTConnector;
import android.util.Log;

public class TachoCount extends Thread {
	protected static final String TAG = "TachoCount";
	NXTConnector conn;

	@Override
	public void run() {
		conn = DroidSamples.connect(DroidSamples.CONN_TYPE.LEGO_LCP);
		NXTCommandConnector.setNXTCommand(new NXTCommand(conn.getNXTComm()));

		Motor.A.rotate(500);
		Motor.C.rotate(-500);
		DroidSamples.displayMessage("T.A:" + Motor.A.getTachoCount() + " -- " + "T.C:" + Motor.C.getTachoCount());
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			Log.e(TAG, "Thread.sleep error", e);
		}
		DroidSamples.clearMessage();
		Sound.playTone(1000, 1000);

		if (conn != null) {
			try {
				conn.close();
			} catch (IOException e) {
				Log.e(TAG, "Error closing connection", e);
			}
		}
		closeConnection();
		DroidSamples.displayToast("TachoCount finished");
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

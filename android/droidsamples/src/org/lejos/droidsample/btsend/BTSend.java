package org.lejos.droidsample.btsend;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.lejos.droidsamples.DroidSamples;
import org.lejos.droidsamples.DroidSamples.CONN_TYPE;

import lejos.pc.comm.NXTConnector;
import android.util.Log;

public class BTSend extends Thread {
	static final String TAG = "BTSend";
	private NXTConnector conn;
	private DataOutputStream dos;
	private DataInputStream dis;

	@Override
	public void run() {		
		Log.d(TAG, "BTSend run");
		
		conn = DroidSamples.connect(DroidSamples.CONN_TYPE.LEJOS_PACKET);
		
		if (conn.getOutputStream() == null) {
			DroidSamples.displayToast("Connection failed");
			return;
		}
		
		dos = new DataOutputStream(conn.getOutputStream());
		dis = new DataInputStream(conn.getInputStream());
		
		int x;
		for (int i = 0; i < 100; i++) {
			try {
				dos.writeInt((i * 30000));
				dos.flush();
				yield();
				x = dis.readInt();
				Log.d(TAG, "sent:" + i * 30000 + " got:" + x);
				DroidSamples.displayMessage("sent:" + i * 30000 + " got:" + x);
				yield();
			} catch (IOException e) {
				Log.e(TAG, "Error in BTSend ", e);
			}
		}

		closeConnection();
		DroidSamples.clearMessage(); 
		DroidSamples.displayToast("BTSend finished");
	}
	
	/** 
	 * Close the Bluetooth connection
	 */
	public void closeConnection() {
		try {
			Log.d(TAG, "BTSend run loop finished and closing");

			dis.close();
			dos.close();
			conn.getNXTComm().close();
		} catch (Exception e) {
		} finally {
			dis = null;
			dos = null;
			conn = null;
		}
	}
}
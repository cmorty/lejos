package org.lejos.droidsamples;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import lejos.pc.comm.NXTComm;
import lejos.pc.comm.NXTCommLogListener;
import lejos.pc.comm.NXTConnector;
import org.lejos.droidsample.R;
import org.lejos.droidsample.acceldemo.AccelDemo;
import org.lejos.droidsample.btsend.BTSend;
import org.lejos.droidsample.rcnavigationcontrol.RCNavigationControl;
import org.lejos.droidsample.remotepilot.RemotePilot;
import org.lejos.droidsample.sensortest.SensorTest;
import org.lejos.droidsample.tachocount.TachoCount;
import org.lejos.droidsample.tiltdemo.TiltDemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class DroidSamples extends Activity {

	public static enum CONN_TYPE {
		LEJOS_PACKET, LEGO_LCP
	}
	
	public static final String MESSAGE_CONTENT = "String_message";
	public static final int MESSAGE = 1000;
	public static final int TOAST = 2000;
	public static boolean canceled = false;
	private BTSend btSend;
	private TachoCount tachoCount;
	private AccelDemo accelDemo;
	private TiltDemo tiltDemo;
	private SensorTest sensorTest;
	private RemotePilot remotePilot;
	private RCNavigationControl rcNavigationControl;
	
	private Toast reusableToast;
	private TextView _message;

	//static final String START_MESSAGE = "Please make sure you NXT is on and both it and your Android handset have bluetooth enabled";
	private static final String GO_AHEAD = "Choose one!";

	public static UIMessageHandler mUIMessageHandler;

	private final static String TAG = "DroidSamples";

	public static NXTConnector connect(final CONN_TYPE connection_type) {
		Log.d(TAG, " about to add LEJOS listener ");

		NXTConnector conn = new NXTConnector();
		conn.setDebug(true);
		conn.addLogListener(new NXTCommLogListener() {

			public void logEvent(String arg0) {
				Log.d(TAG + " NXJ log:", arg0);
			}

			public void logEvent(Throwable arg0) {
				Log.e(TAG + " NXJ log:", arg0.getMessage(), arg0);
			}
		});

		switch (connection_type) {
			case LEGO_LCP:
				conn.connectTo("btspp://", NXTComm.LCP);
				break;
			case LEJOS_PACKET:
				conn.connectTo("btspp://");
				break;
		}

		return conn;

	}

	public static void displayToast(String message) {
		Message message_holder = formMessage(message);
		message_holder.what = DroidSamples.TOAST;
		mUIMessageHandler.sendMessage(message_holder);
	}

	private static Message formMessage(String message) {
		Bundle b = new Bundle();
		b.putString(DroidSamples.MESSAGE_CONTENT, message);
		Message message_holder = new Message();
		message_holder.setData(b);
		return message_holder;
	}

	public static void displayMessage(String message) {
		Message message_holder = formMessage(message);
		message_holder.what = DroidSamples.MESSAGE;
		mUIMessageHandler.sendMessage(message_holder);
	}
	
	public static void clearMessage() {
		displayMessage("");
	}
	
	public static boolean canceled() {
		if (canceled) {
			canceled = false;
			return true;
		} else return false;
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		mUIMessageHandler = new UIMessageHandler();
		setContentView(R.layout.main);
		_message = (TextView) findViewById(R.id.messageText);
		_message.setText("Testing");
		seupNXJCache();

		setupAccelDemo(this);
		setupBTSend(this);
		setupRemotePilot(this);
		setupRCNavigationControl(this);
		setupSensorTest(this);
		setupTachoCount(this);
		setupTiltDemo(this);

		reusableToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
	}

	@Override
	protected void onPause() {
		super.onPause();

		if (btSend != null) {
			Log.d(TAG, "onPause() closing btSend ");
			btSend.closeConnection();
			btSend = null;
		}
		
		if (tachoCount != null) {
			Log.d(TAG, "onPause() closing TachoCount ");
			tachoCount.closeConnection();
			tachoCount = null;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
	}
	
	private void setupAccelDemo(final DroidSamples samples) {
		Button button = (Button) findViewById(R.id.button1);
		
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				if (accelDemo != null) {
					canceled = true;
					accelDemo = null;
					return;
				}
				
				try {
					accelDemo = new AccelDemo();
					accelDemo.start();
				} catch (Exception e) {
					Log.e(TAG, "failed to run AccelDemo:" + e.getMessage(), e);
				}

			}
		});
	}
	
	private void setupBTSend(final DroidSamples samples) {
		Button button = (Button) findViewById(R.id.button2);
		
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				try {
					btSend = new BTSend();
					btSend.start();
				} catch (Exception e) {
					Log.e(TAG, "failed to run BTSend:" + e.getMessage(), e);
				}

			}
		});
	}
	
	private void setupRCNavigationControl(final DroidSamples samples) {
		Button button = (Button) findViewById(R.id.button3);
		
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				Intent RCNavigationControl = new Intent(samples, RCNavigationControl.class);
				startActivity(RCNavigationControl);
			}
		});
	}
	
	private void setupRemotePilot(final DroidSamples samples) {
		Button button = (Button) findViewById(R.id.button4);
		
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				try {
					remotePilot = new RemotePilot();
					remotePilot.start();
				} catch (Exception e) {
					Log.e(TAG, "failed to run Remote Pilot:" + e.getMessage(), e);
				}

			}
		});
	}
	
	private void setupSensorTest(final DroidSamples samples) {
		Button button = (Button) findViewById(R.id.button5);
		
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {		
				if (sensorTest != null) {
					canceled = true;
					sensorTest = null;
					return;
				}
				
				try {
				    sensorTest = new SensorTest();
					sensorTest.start();
				} catch (Exception e) {
					Log.e(TAG, "failed to run SensorTest" + e.getMessage(), e);
				}
			}
		});
	}
	
	private void setupTachoCount(final DroidSamples samples) {
		Button button = (Button) findViewById(R.id.button6);
		
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				try {
				    tachoCount = new TachoCount();
					tachoCount.start();
				} catch (Exception e) {
					Log.e(TAG, "failed to run TachoCount:" + e.getMessage(), e);
				}

			}
		});
	}
	
	private void setupTiltDemo(final DroidSamples samples) {
		Button button = (Button) findViewById(R.id.button7);
		
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				
				if (tiltDemo != null) {
					canceled = true;
					tiltDemo = null;
					return;
				}
				
				try {
				    tiltDemo = new TiltDemo();
					tiltDemo.start();
				} catch (Exception e) {
					Log.e(TAG, "failed to run TiltDemo:" + e.getMessage(), e);
				}

			}
		});
	}

	private void seupNXJCache() {
		File root = Environment.getExternalStorageDirectory();

		try {
			String androidCacheFile = "nxj.cache";
			File mLeJOS_dir = new File(root + "/leJOS NXJ");
			if (!mLeJOS_dir.exists()) {
				mLeJOS_dir.mkdir();

			}
			File mCacheFile = new File(root + "/leJOS/", androidCacheFile);

			if (root.canWrite() && !mCacheFile.exists()) {
				FileWriter gpxwriter = new FileWriter(mCacheFile);
				BufferedWriter out = new BufferedWriter(gpxwriter);
				out.write("");
				out.flush();
				out.close();
				_message.setText("nxj.cache (record of connection addresses) written to: " + mCacheFile.getName() + GO_AHEAD);
			} else {
				_message.setText(GO_AHEAD);

			}
		} catch (IOException e) {
			Log.e(TAG, "Could not write nxj.cache " + e.getMessage(), e);
		}
		_message.setVisibility(View.VISIBLE);
		_message.requestLayout();
	}

	private void showToast(String textToShow) {
		reusableToast.setText(textToShow);
		reusableToast.show();
	}
		
	class UIMessageHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case MESSAGE:
					_message.setText((String) msg.getData().get(MESSAGE_CONTENT));
					break;
				case TOAST:
					showToast((String) msg.getData().get(MESSAGE_CONTENT));
					break;
			}
			_message.setVisibility(View.VISIBLE);
			_message.requestLayout();
		}
	}
}

package org.lejos.droidsample.remotepilot;

import org.lejos.droidsamples.DroidSamples;

public class RemotePilot extends Thread {
	protected static final String TAG = "RemotePilot";

	@Override
	public void run() {
	    RemotePilotControl pilot = new RemotePilotControl();
	    pilot.connect("NOISY", "");
	    pilot.setTurnSpeed(90);
	    pilot.setMoveSpeed(8);
	    for(int i = 0 ; i < 4 ; i++)
	    {
	      pilot.travel(10);
	      DroidSamples.displayMessage("TRAV 10 "+pilot.getTravelDistance());
	      pilot.rotate(-90);
	      DroidSamples.displayMessage("ROT -90 " + pilot.getAngle());
	    }
	    pilot.reset();
	    pilot.arc(10, 90);
	    DroidSamples.displayMessage("arc(10,90) distance: " + pilot.getTravelDistance()
	            +" angle "+pilot.getAngle());
	    pilot.reset();
	    pilot.steer(50,-45);
	    pilot.steer(-50,45);
	    DroidSamples.displayMessage("steer " + pilot.getTravelDistance()
	              +" angle "+pilot.getAngle());

		DroidSamples.displayToast("RemotePilot finished");
	}
}

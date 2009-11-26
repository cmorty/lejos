import java.io.IOException;
import java.io.PrintStream;

import javax.microedition.io.Connector;
import javax.microedition.sensor.Channel;
import javax.microedition.sensor.ChannelInfo;
import javax.microedition.sensor.Condition;
import javax.microedition.sensor.ConditionListener;
import javax.microedition.sensor.Data;
import javax.microedition.sensor.LimitCondition;
import javax.microedition.sensor.SensorConnection;
import javax.microedition.sensor.SensorInfo;

import lejos.nxt.Button;
import lejos.nxt.Motor;
import lejos.nxt.comm.RConsole;
import lejos.robotics.navigation.TachoPilot;

public class JSR256Test implements ConditionListener {
	TachoPilot pilot = new TachoPilot(5.6f,16.0f,Motor.B, Motor.C,true);
	
	public void run() throws InterruptedException {
		RConsole.openBluetooth(0);	
		System.setOut(RConsole.getPrintStream());
		SensorConnection sonic = null;
		try {
			sonic =(SensorConnection)Connector.open("sensor:proximity");
		} catch (IOException e) {
			System.err.println("No such sensor");
			Button.waitForPress();
			System.exit(1);
		}

		SensorInfo sonicInfo = sonic.getSensorInfo();

		Condition condition = new LimitCondition(100,Condition.OP_LESS_THAN);

		ChannelInfo channelInfo = sonic.getSensorInfo().getChannelInfos()[0];
		System.out.println("Got channelInfo: " + (channelInfo == null ? "null" : channelInfo));
		Channel channel = sonic.getChannel(channelInfo);
		System.out.println("Got channel: " + (channel == null ? "null" : channel));
		channel.addCondition(this, condition);
		System.out.println("Added condition");
		pilot.forward();
		

		while (pilot.isMoving()) {
			//System.out.println("Vendor: " + sonicInfo.getProperty(SensorInfo.PROP_VENDOR));
			//System.out.println("Version: " + sonicInfo.getProperty(SensorInfo.PROP_VERSION));
			try {
				Data[] data = sonic.getData(1);
				System.out.println("Range = " + data[0].getIntValues()[0]);
				Thread.sleep(100);
			} catch (IOException ioe) {
				System.err.println("Failed to read sensor");
			}
		}
		Button.waitForPress();
	}

	public static void main(String[] args) throws InterruptedException {
		new JSR256Test().run();
	}

	public void conditionMet(SensorConnection sensor, Data data, Condition condition) {
		System.out.println("Condition met: " + data.getIntValues()[0] + condition);
		pilot.stop();	
	}	
}

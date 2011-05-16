import java.io.IOException;
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
import lejos.nxt.comm.RConsole;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.navigation.DifferentialPilot;
import lejos.util.PilotProps;

/**
 * 
 * Test of JSR256 - javax.microedition.sensor.
 * 
 * You can run the PilotParams sample to create a property file which 
 * sets the parameters of the Pilot to the dimensions
 * and motor connections for your robot.
 * 
 * @author Lawrie Griffiths
 *
 */
public class JSR256Test implements ConditionListener {
	
	private DifferentialPilot pilot;
	
	public JSR256Test(DifferentialPilot pilot)
	{
		this.pilot = pilot;
	}

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

	public static void main(String[] args) throws Exception {
    	PilotProps pp = new PilotProps();
    	pp.loadPersistentValues();
    	float wheelDiameter = Float.parseFloat(pp.getProperty(PilotProps.KEY_WHEELDIAMETER, "5.6"));
    	float trackWidth = Float.parseFloat(pp.getProperty(PilotProps.KEY_TRACKWIDTH, "16.0"));
    	RegulatedMotor leftMotor = PilotProps.getMotor(pp.getProperty(PilotProps.KEY_LEFTMOTOR, "B"));
    	RegulatedMotor rightMotor = PilotProps.getMotor(pp.getProperty(PilotProps.KEY_RIGHTMOTOR, "C"));
    	boolean reverse = Boolean.parseBoolean(pp.getProperty(PilotProps.KEY_REVERSE,"false"));
    	
    	DifferentialPilot pilot = new DifferentialPilot(wheelDiameter,trackWidth,leftMotor,rightMotor,reverse);
    	
		new JSR256Test(pilot).run();
	}

	public void conditionMet(SensorConnection sensor, Data data, Condition condition) {
		System.out.println("Condition met: " + data.getIntValues()[0] + condition);
		pilot.stop();	
	}	
}

import javax.microedition.io.Connector;
import javax.microedition.sensor.*;

import lejos.nxt.Button;
import lejos.nxt.comm.RConsole;

/**
 * JSR256 Sniffer example. This example should be run using nxjconsole
 * or nxjconsoleviewer on the PC. You can use the corresponding targets in the build.xml file.
 * 
 * Tests features of the JSR 256 javax.microedition.sensor API.
 * Use it with a variety of I2C sensors.
 * 
 * @author Lawrie Griffiths
 *
 */
public class Sniffer implements SensorListener, ConditionListener, DataListener {
	private static final LimitCondition COND1 =
		new LimitCondition(0, Condition.OP_GREATER_THAN_OR_EQUALS);
		private static final LimitCondition COND2 =
		new LimitCondition(0, Condition.OP_LESS_THAN_OR_EQUALS);
	public SensorInfo[] infos;
	public SensorConnection sensor;
	
	public void run() {		
		
		// Listen for a proximity sensor like the ultrasonic
		SensorManager.addSensorListener(this, "proximity");
		
		// Find all the sensors that are currently connected
		System.out.println("Searching for sensors...");
		infos = SensorManager.findSensors(null, null);
		int numSensors = infos.length;
		System.out.println("Found " + numSensors + " sensors");
		
		// print values and listen for them to be unplugged
		for(int i=0;i<numSensors;i++) {
			readSensor(infos[i]);
			SensorManager.addSensorListener(this, infos[i]);
		}
		
		// wait around for sensors to be connected and disconnected
		Button.waitForPress();
		
		if (sensor == null) return;
		
		// Add conditions
		addConditions();
		System.out.println("Added conditions");		
		Button.waitForPress();
		
		// Set a Data Listener
		sensor.setDataListener(this, 1);		
		System.out.println("Set data listener");		
		Button.waitForPress();
		
		// Remove the data listener
		sensor.removeDataListener();		
		System.out.println("Removed data listener");	
		Button.waitForPress();
	}
	
	public static void main(String[] args) {
		new Sniffer().run();
	}
	
	// Connect to a sensor and print the values
	public void readSensor(SensorInfo info){
		String url = info.getUrl();
		System.out.println("Sensor: " + url);
		try{
			sensor = (SensorConnection)Connector.open(url);
			info = sensor.getSensorInfo();
			System.out.println("Model: " + info.getModel());
		} catch(Exception e){
			System.out.println("Failed to connect to sensor: " + e.getClass());
			return;		
		}
		
		try {
			Data[] data = sensor.getData(1);
			print(data);
		} catch (Exception e) {
			System.out.println("Exception getting data:" + e.getClass());
		}
	}
	
	private String resolved(ChannelInfo channelInfo, double value){
		int scalingFactor = channelInfo.getScale();
		double resolver = 1;
		for (int i=0,l=Math.abs(scalingFactor); i<l; i++){
		resolver = scalingFactor>0? resolver*10 : resolver/10 ;
		}
		return "" + value * resolver + " " + channelInfo.getUnit();
	}
	
	// Print all the data in the Data object
	private void print(Data[] d){
		StringBuffer channelBuffer = new StringBuffer();
		for (int i=0, l=d.length; i<l; i++){
			ChannelInfo cInfo = d[i].getChannelInfo();
			int dataType = cInfo.getDataType();
			String valueString="";
			switch(dataType){
			case ChannelInfo.TYPE_DOUBLE:
				{
					double[] values = d[i].getDoubleValues();
					int l2 = values.length;
					valueString = l2 > 0 ?
							resolved(cInfo, values[l2-1]) : null;
					break;
				}
			case ChannelInfo.TYPE_INT:
				{
					int[] values = d[i].getIntValues();
					int l2 = values.length;
					valueString = l2 > 0 ?	resolved(cInfo, values[l2-1]) : null;
					break;
				}
			case ChannelInfo.TYPE_OBJECT:
				{
					Object[] values = d[i].getObjectValues();
					int l2 = values.length;
					valueString = l2 > 0 ? values[l2-1].toString() : null;
					break;
				}
			}
			String channelString =
				cInfo.getName()+": "+valueString + "\n";
			channelBuffer.append(channelString);
			//System.out.println(" "+channelString);
		}
		System.out.println(channelBuffer);
	}


	public void sensorAvailable(SensorInfo info) {
		System.out.println("Thanks for the " + info.getQuantity() + " sensor");
		
		// Take a reading, now we have it		
		readSensor(info);		
	}

	public void sensorUnavailable(SensorInfo info) {
		System.out.println("Goodbye " + info.getModel());
		//SensorManager.removeSensorListener(this);
	}

	public void conditionMet(SensorConnection sensor, Data data,
			Condition condition) {
		System.out.println("Condition met: " + condition);
		print(new Data[]{data});
		
	}

	public void dataReceived(SensorConnection sensor, Data[] data,
			boolean isDataLost) {
		System.out.println("We have data:");
		print(data);
		
	}
	
	private void addConditions(){
		ChannelInfo[] cInfos = sensor.getSensorInfo().getChannelInfos();
		for (int i=0,l=cInfos.length; i<l; i++){
			Channel channel = sensor.getChannel(cInfos[i]);
			channel.addCondition(this, COND1);
			channel.addCondition(this, COND2);
		}
	}
}

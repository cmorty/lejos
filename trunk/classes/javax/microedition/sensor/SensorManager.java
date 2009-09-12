package javax.microedition.sensor;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import lejos.nxt.I2CSensor;
import lejos.nxt.SensorPort;
import lejos.util.Delay;

/** 
 * JSR256 SensorManager implementation for leJOS NXJ I2C sensors
 * 
 * @author Lawrie Griffiths
 *
 */
public class SensorManager {
	// Registry of all known sensors
	private static final I2CSensorInfo[] sensors = {
		new UltrasonicSensorInfo(),
		new MindsensorsAccelerationSensorInfo(),
		new HiTechnicCompassSensorInfo(),
		new HiTechnicColorSensorInfo()
	};
	// Hashtable of listeners and a quantity or SensorInfo object
	private static Hashtable listeners = new Hashtable();
	// List of currently attached sensors
	private static ArrayList<I2CSensorInfo> availableSensors;
	private static ArrayList<I2CActiveCondition> conditions = new ArrayList<I2CActiveCondition>();
	private static ArrayList<I2CActiveData> dataListeners = new ArrayList<I2CActiveData>();
	static {
		availableSensors = getSensors();
		Thread listener = new Thread(new Listener());
		listener.setDaemon(true);
		listener.start();
		Thread conditionListener = new Thread(new CondListener());
		conditionListener.setDaemon(true);
		conditionListener.start();
	}
	
	/**
	 * Register a listener to monitor the change of availability of a specific sensor
	 * 
	 * @param listener the sensor listener
	 * @param info a SensorListener object returned from findSensors or SensorConnection.getSensorInfo
	 */
	public static void addSensorListener(SensorListener listener, SensorInfo info) {
		addSensorListenerObject(listener, info);
	}
	
	/**
	 * Register a listener to monitor the change of availability of a sensor for a specific quantity
	 * 
	 * @param listener the sensor listener
	 * @param quantity the required quantity
	 */
	public static void addSensorListener(SensorListener listener, String quantity) {
		addSensorListenerObject(listener, quantity);
	}
	
	/**
	 * Find all available sensors that match a specific URL
	 * @param url the specified URL
	 * @return an array of SensorInfo objects
	 */
	public static synchronized SensorInfo[] findSensors(String url) {
		SensorURL sensorURL = SensorURL.parseURL(url);		
		if (sensorURL == null) throw new IllegalArgumentException();
		
		checkSensors();
		return getSensors(sensorURL);
	}
	
	/**
	 * Find all available sensors that match a specific context
	 * 
	 * @param quantity the required quantity or null for any
	 * @param contextType the required context type or null for any
	 * @return an array of SensorInfo objects
	 */
	public static synchronized SensorInfo[] findSensors(String quantity, String contextType) {			
		checkSensors();
		return getSensors(quantity, contextType);
	}
	
	/**
	 * Remove the specified sensor listener
	 * 
	 * @param listener the sensor listener
	 */
	public static synchronized void removeSensorListener(SensorListener listener) {
		if (listeners.get(listener) != null) {
			listeners.put(listener, null);
		}
	}
	
	// Add either a SensorInfo or a quantity to a listener
	private synchronized static void addSensorListenerObject(SensorListener listener, Object obj) {
		// Get existing objects that the listener is monitoring
		ArrayList<Object> value = (ArrayList<Object>) listeners.get(listener);
		
		// If no entry for the listener, create an ArrayList for the entries
		if (value == null) value = new ArrayList<Object>();
		
		if (!value.contains(obj)) { // Don't add the same object twice
			// Add the object to the ArrayList
			value.add(obj);
			
			// Add the ArrayList to the listeners
			listeners.put(listener, value);
		}	
	}
	
	// Get the available sensors that match a specific URL
	static synchronized I2CSensorInfo[] getSensors(SensorURL searchURL) {
		int count = 0;
		//searchURL.printURL();
		
		// Count matching sensors
		for(I2CSensorInfo avail: availableSensors) {
			SensorURL targetURL = SensorURL.parseURL((avail.getUrl()));
			//targetURL.printURL();
			if (searchURL.matches(targetURL)) count++;
		}
		
		// Put them in an array
		I2CSensorInfo[] infoArray = new I2CSensorInfo[count];	
		int i=0;
		for(I2CSensorInfo avail: availableSensors) {
			SensorURL targetURL = SensorURL.parseURL((avail.getUrl()));
			if (searchURL.matches(targetURL)) {
				infoArray[i++] = avail;
			}
		}	
		//System.out.println("Found " + infoArray.length + " sensors");
		return infoArray;
	}
	
	// Get the available sensors that match the given quantity and context type
	static synchronized I2CSensorInfo[] getSensors(String quantity, String contextType) {		
		int count = 0;
		
		// Count matching sensors
		for(I2CSensorInfo avail: availableSensors) {
			if ((quantity == null || avail.getQuantity().equals(quantity) &&
			    (contextType == null || avail.getContextType().equals(contextType)))) count++;
		}
		
		// Put them in an array
		I2CSensorInfo[] infoArray = new I2CSensorInfo[count];	
		int i=0;
		for(I2CSensorInfo avail: availableSensors) {
			if ((quantity == null || avail.getQuantity().equals(quantity) &&
				    (contextType == null || avail.getContextType().equals(contextType)))) {
				infoArray[i++] = avail;
			}
		}			
		return infoArray;
	}
	
	// Poll for the currently attached sensors, compare with
	// previous set and generate available and unavailable events
	private synchronized static void checkSensors() {
		ArrayList<I2CSensorInfo> oldSensors = availableSensors;
		availableSensors = getSensors();
		
		// Check for missing sensors
		for(I2CSensorInfo old: oldSensors) {
			boolean stillThere = false;
			for(I2CSensorInfo current : availableSensors) {
				if (old.equals(current)) stillThere = true;
			}
			if (!stillThere) notify(old, false);
		}
		
		// Check for new sensors
		for(I2CSensorInfo current: availableSensors) {
			boolean wasThere = false;
			for(I2CSensorInfo old : oldSensors) {
				if (old.equals(current)) wasThere = true;
			}
			if (!wasThere) notify(current, true);
		}		
	}

	// Get the currently attached sensors and fill in SensorInfo structures with dynamic information
	private static synchronized ArrayList<I2CSensorInfo> getSensors() {
		ArrayList<I2CSensorInfo> current = new ArrayList<I2CSensorInfo>();
		
		for(int i=0;i<SensorPort.PORTS.length;i++) {
			I2CSensor i2cSensor = new I2CSensor(SensorPort.PORTS[i]);		
			String type = null;
			
			// Try a few times as Ultrasonic sensor is unreliable
			for(int j=0;j<10;j++) {
				type = i2cSensor.getSensorType();
				if (type.length() > 0) break;
			}
			if (type.length()== 0) continue;
			
			I2CSensorInfo info = findSensorInfo(type);

			// Fill in details from the attached sensor
			if (info != null) {
				info.setPort(i);
				info.setType(type);
				info.setVendor(i2cSensor.getProductID());
				info.setVersion(i2cSensor.getVersion());
					
				current.add(info);
			}
		}
		return current;
	}
	
	/*
	 * Get the sensor information for a sensor of  given type
	 */
	private synchronized static I2CSensorInfo findSensorInfo(String type) {
		for(int i=0;i<sensors.length;i++) {
			String[] models = sensors[i].getModelNames();
			for(int j=0;j<models.length;j++) {
				if (models[j].equals(type)) return sensors[i];
			}
		}
		return null;
	}
	
	// Notify listeners of available or unavailable events
	private synchronized static void notify(I2CSensorInfo sensor, boolean available) {
		Enumeration quantityKeys = listeners.keys();
		
		while(quantityKeys.hasMoreElements()) {
			SensorListener listener = (SensorListener) quantityKeys.nextElement();		
			ArrayList<Object> values = (ArrayList<Object>) listeners.get(listener);
			if (values != null) {
				for(Object obj: values) {
					if (obj instanceof String) {
						String quantity = (String) obj;
						if (sensor.getQuantity().equals(quantity)) {
							if (available) listener.sensorAvailable(sensor);
							else listener.sensorUnavailable(sensor);
							break; // Only inform sensor listener once
						}
					} else if (obj != null) {
						SensorInfo info = (SensorInfo) obj;
						if (sensor == info) {
							if (available) listener.sensorAvailable(sensor);
							else listener.sensorUnavailable(sensor);
							break;
						}
					}
				}
			}			
		}
	}
	
	/*
	 * Add a condition for a specific condition listener on a channel
	 */
	static synchronized void addCondition(I2CChannel channel, ConditionListener conditionListener, Condition condition) {
		// Check if the condition already set
		for(I2CActiveCondition cond: conditions) {
			if (cond.getChannel()  == channel && cond.getCondition() == condition && 
				cond.getConditionListener() == conditionListener) return;
		}
		conditions.add(new I2CActiveCondition(channel, condition, conditionListener));
	}
	
	/*
	 * Remove a condition (on all condition listeners) on a channel
	 */
	static synchronized void removeCondition(I2CChannel channel, Condition condition) {
		 for (Iterator<I2CActiveCondition> it = conditions.iterator(); it.hasNext();) {
			 I2CActiveCondition cond = it.next();
				if (cond.getCondition() == condition && cond.getChannel() == channel) {
					it.remove();
				}
		 }
	}
	
	/*
	 * Remove a specific condition on a specific condition listener on a channel
	 */
	static synchronized void removeCondition(I2CChannel channel, ConditionListener listener, Condition condition) {
		 for (Iterator<I2CActiveCondition> it = conditions.iterator(); it.hasNext();) {
			 I2CActiveCondition cond = it.next();
				if (cond.getCondition() == condition && cond.getChannel() == channel && 
					cond.getConditionListener() == listener) {
					it.remove();
				}
		 }
	}
	
	/*
	 * Get all the conditions for a given condition listener on a channel.
	 * Note that there can be no duplicates
	 */
	static synchronized Condition[] getConditions(Channel channel, ConditionListener listener) {
		int count = 0;
		for (I2CActiveCondition cond: conditions) {
			if (cond.getChannel() == channel && cond.getConditionListener() == listener) {
				count++;
			}
		}
		Condition[] cc = new Condition[count];
		int i=0;
		for (I2CActiveCondition cond: conditions) {
			if (cond.getChannel() == channel && cond.getConditionListener() == listener) {
				cc[i++] = cond.getCondition();
			}
		}
		return cc;
	}
	
	/*
	 * Remove all conditions on a channel
	 */
	static synchronized void removeAllConditions(Channel channel) {
		 for (Iterator<I2CActiveCondition> it = conditions.iterator(); it.hasNext();) {
			 I2CActiveCondition cond = it.next();
				if (cond.getChannel() == channel) {
					it.remove();
				}
		 }
	}
	
	/*
	 * Remove a condition listener on a channel
	 */
	static synchronized void removeConditionListener(Channel channel, ConditionListener listener) {
		 for (Iterator<I2CActiveCondition> it = conditions.iterator(); it.hasNext();) {
			 I2CActiveCondition cond = it.next();
				if (cond.getChannel() == channel && cond.getConditionListener() == listener) {
					it.remove();
				}
		 }
	}
	
	/*
	 * Add a data listener for a sensor
	 */
	static void addDataListener(I2CSensorConnection sensor, int bufferSize, DataListener listener, int samplingInterval) {
		// Remove any existing data listener
		removeDataListener(sensor);
		dataListeners.add(new I2CActiveData(sensor, bufferSize, listener, samplingInterval));
	}
	
	/*
	 * Remove the data listeners for a given sensor
	 */
	static void removeDataListener(SensorConnection sensor) {
		 for (Iterator<I2CActiveData> it = dataListeners.iterator(); it.hasNext();) {
			 I2CActiveData active = it.next();
				if (active.getSensor() == sensor) {
					it.remove();
				}
		 }
	}
	
	/*
	 * Check which conditions are met. If they are met, generate the event and
	 * delete the condition.
	 */
	private static synchronized void checkConditions() {
		for (Iterator<I2CActiveCondition> it = conditions.iterator(); it.hasNext();) {
			I2CActiveCondition cond = it.next();
			I2CChannel channel = cond.getChannel();
			I2CSensorConnection sensor = channel.getSensor();
			int reading = sensor.getChannelData(channel.getChannelInfo());
			if(cond.getCondition().isMet((double) reading)) {
				I2CData data = new I2CData(channel.getChannelInfo(),1);
				data.setIntData(0, reading);
				cond.getConditionListener().conditionMet(sensor, data, cond.getCondition());
				it.remove();
			}
		}		
	}
	
	/*
	 * Process all active data listeners
	 */
	private static synchronized void processData() {
		for(I2CActiveData active: dataListeners) {
			active.process();
		}
	}
	
	/**
	 * Thread to monitor availability of I2C sensors
	 */
	static class Listener implements Runnable {
		public void run() {		
			for(;;) {	
				Delay.msDelay(1000);			
				checkSensors();
			}
		}
	}
	/**
	 * Thread to monitor conditions on channels
	 */
	static class CondListener implements Runnable {
		public void run() {
			for(;;) {
				// Check for conditions being met
				checkConditions();
				// Process data transfers
				processData();
				Delay.msDelay(10);
			}
		}
	}
}

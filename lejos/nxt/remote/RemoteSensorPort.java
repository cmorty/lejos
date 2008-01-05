package lejos.nxt.remote;

import lejos.nxt.*;
import lejos.nxt.comm.*;
import java.io.*;

/**
 * Emulates a Sensor Port using LCP
 */
public class RemoteSensorPort implements NXTProtocol, ADSensorPort {
	
	private int id;
	private int type, mode;
	private NXTCommand nxtCommand;

	public RemoteSensorPort(NXTCommand nxtCommand, int id) {
		this.nxtCommand = nxtCommand;
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	
	public int getType() {
		return type;
	}
	
	public int getMode() {
		return mode;
	}
	
	public void setTypeAndMode(int type, int mode) {
		this.type = type;
		this.mode = mode;
		try {
			nxtCommand.setInputMode(id, type, mode);
		} catch (IOException ioe) {}
	}
	
	public void setType(int type) {
		this.type = type;
		setTypeAndMode(type, mode);
	}
	
	public void setMode(int mode) {
		this.mode = mode;
		setTypeAndMode(type, mode);
	}
	
	/**
	 * Reads the boolean value of the sensor.
	 * @return Boolean value of sensor.
	 */
	public boolean readBooleanValue() {
		try {
			InputValues vals = nxtCommand.getInputValues(id);
			return (vals.rawADValue<600);			
		} catch (IOException ioe) {
			return false;
		}
	}
	
    /**
     * Reads the raw value of the sensor.
     * @return Raw sensor value. Range is device dependent.
     */
	public int readRawValue() {
		try {
			InputValues vals = nxtCommand.getInputValues(id);
			return vals.rawADValue;
		} catch (IOException ioe) {
			return 0;
		}
	}
	
	/**
	 * Returns value compatible with Lego firmware. 
	 */
	public int readValue()
	  {
	    int rawValue = readRawValue();
	    
	    if (mode == MODE_BOOLEAN)
	    {
	    	return (rawValue < 600 ? 1 : 0);
	    }
	    
	    if (mode == MODE_PCTFULLSCALE)
	    {
	    	return ((1023 - rawValue) * 100/ 1023);
	    }
	    
	    return rawValue;
	}
}


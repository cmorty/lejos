package lejos.nxt.remote;

import lejos.nxt.*;
import java.io.*;

/**
 * Emulates a Sensor Port using LCP
 */
public class RemoteSensorPort implements NXTProtocol, ADSensorPort, I2CPort {
	private int id;
	private int type, mode;
	private NXTCommand nxtCommand;

	public RemoteSensorPort(NXTCommand nxtCommand, int id) {
		this.nxtCommand = nxtCommand;
		this.id = id;
	}
	
	/**
	 * Get the port number
	 * @return the port number
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * Get the sensor type
	 * @return the sensor type
	 */
	public int getType() {
		return type;
	}
	
	/**
	 * Get the sensor mode
	 * @return the sensor mode
	 */
	public int getMode() {
		return mode;
	}
	
	/**
	 * Set the sensor type and mode
	 * @param type the sensor type
	 * @param mode the sensor mode
	 */
	public void setTypeAndMode(int type, int mode) {
		this.type = type;
		this.mode = mode;
		try {
			nxtCommand.setInputMode(id, type, mode);
		} catch (IOException ioe) {}
	}
	
	/**
	 * Set the sensor type
	 * @param type the sensor type
	 */
	public void setType(int type) {
		this.type = type;
		setTypeAndMode(type, mode);
	}
	
	/**
	 * Set the sensor mode
	 * @param mode the sensor mode
	 */
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
	public int readValue() {
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
	
	/**
	 * Get the NXTCommand object used for remote access
	 * @return the NXTCommand object
	 */
	public NXTCommand getNXTCommand() {
		return nxtCommand;
	}

	/**
	 * Test if I2C is busy
	 * @return the status value (see ErrorMessages)
	 */
	public int i2cBusy() {
		try {
			byte[] status = nxtCommand.LSGetStatus((byte) id);
			return (int) status[0];
		} catch (IOException ioe) {
			return -1;
		}
	}

	/**
	 * Disable I2C on the port - null for remote ports
	 */
	public void i2cDisable() {		
	}

	/**
	 * Enable I2C on the port - null for remote ports
	 */
	public void i2cEnable(int mode) {
		
	}

	/**
	 * Start an I2C transaction. The remote implementation is synchronous.
	 * @param address the I2C address (x01 - x7F)
	 * @param internalAddress the register or internal address
	 * @param numInternalBytes not used
	 * @param buffer the buffer for reading or writing data
	 * @param numBytes the number of bytes to read or write
	 * @param transferType 0 for read, 1 for write
	 * @return the status value
	 */
	public int i2cStart(int address, int internalAddress, int numInternalBytes,
			byte[] buffer, int numBytes, int transferType) {
		byte [] txData = {(byte) (address << 1), (byte) internalAddress};
		int status;
		try {
			nxtCommand.LSWrite((byte) id, txData, (byte) numBytes);
		} catch (IOException ioe) {
			return -1;
		}
		
		do {
			status = i2cBusy();		
		} while (status == ErrorMessages.PENDING_COMMUNICATION_TRANSACTION_IN_PROGRESS || 
				 status == ErrorMessages.SPECIFIED_CHANNEL_CONNECTION_NOT_CONFIGURED_OR_BUSY);
		
		if (status != 0) return status;
		
		return 0;
	}

    public int i2cComplete(byte[] buffer, int numBytes)
    {
		try {
			byte [] ret = nxtCommand.LSRead((byte) id);
            if (ret == null) return -1;
            if (numBytes > ret.length) numBytes = ret.length;
			System.arraycopy(ret, 0, buffer, 0, numBytes);
		} catch (IOException ioe) {
			return -1;
		}

		return numBytes;

    }
}


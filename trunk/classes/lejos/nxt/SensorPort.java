package lejos.nxt;

/**
 * Abstraction for a NXT input port.
 * 
 */
public class SensorPort implements LegacySensorPort, I2CPort, ListenerCaller
{
  private int iPortId;
  private short iNumListeners = 0;
  private SensorPortListener[] iListeners;
  private int iPreviousValue;
  private int type, mode;
  
  public static final int POWER_STD = 0;
  public static final int POWER_RCX9V = 1;
  public static final int POWER_9V = 2;
  
   
  private static final byte[]  powerType = {
      POWER_STD,   // NO_SENSOR
      POWER_STD,   // SWITCH
      POWER_RCX9V, // TEMPERATURE
      POWER_RCX9V, // REFLECTION
      POWER_RCX9V, // ANGLE
      POWER_STD,   // LIGHT_ACTIVE
      POWER_STD,   // LIGHT_INACTIVE
      POWER_STD,   // SOUND_DB
      POWER_STD,   // SOUND_DBA
      POWER_STD,   // CUSTOM
      POWER_STD,   // LOWSPEED,
      POWER_9V     // LOWSPEED_9V
  };

  // Sensor port I/O pin ids
  public static final int SP_DIGI0 = 0;
  public static final int SP_DIGI1 = 1;
  public static final int SP_ANA = 2;

  // Sensor port pin modes
  public static final int SP_MODE_OFF = 0;
  public static final int SP_MODE_INPUT = 1;
  public static final int SP_MODE_OUTPUT = 2;
  public static final int SP_MODE_ADC = 3;

  // Digital I/O pins used to control the sensor operation
  public static final int DIGI_UNUSED = -1;
  public static final int DIGI_OFF = 0;
  public static final int DIGI_0_ON = (1 << SP_DIGI0);
  public static final int DIGI_1_ON = (1 << SP_DIGI1);
  private static final byte[]  controlPins = {
      DIGI_UNUSED, // NO_SENSOR
      DIGI_UNUSED, // SWITCH
      DIGI_UNUSED, // TEMPERATURE
      DIGI_UNUSED, // REFLECTION
      DIGI_UNUSED, // ANGLE
      DIGI_0_ON,   // LIGHT_ACTIVE
      DIGI_OFF,    // LIGHT_INACTIVE
      DIGI_0_ON,   // SOUND_DB
      DIGI_1_ON,   // SOUND_DBA
      DIGI_UNUSED, // CUSTOM
      DIGI_UNUSED, // LOWSPEED,
      DIGI_UNUSED  // LOWSPEED_9V
  };
  /**
   * Port labeled 1 on NXT.
   */
  public static final SensorPort S1 = new SensorPort (0);

  /**
   * Port labeled 2 on NXT.
   */   
  public static final SensorPort S2 = new SensorPort (1);
  
  /**
   * Port labeled 3 on NXT.
   */
  public static final SensorPort S3 = new SensorPort (2);

  /**
   * Port labeled 4 on NXT.
   */
  public static final SensorPort S4 = new SensorPort (3);

  /**
   * Array containing all three ports [0..3].
   */
  public static final SensorPort[] PORTS = { SensorPort.S1, SensorPort.S2, SensorPort.S3, SensorPort.S4 };

  /**
   * Reads the raw value of the sensor.
   * @return the raw sensor value
   */
  public final int readRawValue()
  {
    return readSensorValue (iPortId);
  }

  /**
   * Reads the boolean value of the sensor.
   * Do not use - currently returns the raw value.
   * @return the boolean state of the sensor
   */
  public final boolean readBooleanValue()
  {
	int rawValue = readSensorValue(iPortId);
    return (rawValue < 600);
  }

  protected SensorPort (int aId)
  {
    iPortId = aId;
    type = TYPE_NO_SENSOR;
    mode = MODE_RAW;
  }

  /**
   * Return the ID of the port. One of 0, 1, 2 or 3.
   * @return The Id of this sensor
   */
  public final int getId()
  {
    return iPortId;
  }
    
  /**
   * Adds a port listener.
   * <p>
   * <b>
   * NOTE 1: You can add at most 8 listeners.<br>
   * NOTE 2: Synchronizing inside listener methods could result
   * in a deadlock.
   * </b>
   * @param aListener Listener for call backs
   * @see lejos.nxt.SensorPortListener
   */
  public synchronized void addSensorPortListener (SensorPortListener aListener)
  {
    if (iListeners == null)
    {
        iListeners = new SensorPortListener[8];
    }
    iListeners[iNumListeners++] = aListener;
    ListenerThread.get().addSensorToMask(iPortId, this);
  }

  /**
   * Activates an RCX sensor. This method should be called
   * if you want to get accurate values from an RCX
   * sensor. In the case of RCX light sensors, you should see
   * the LED go on when you call this method.
   */
  public final void activate()
  {
    setPowerType(1);
  }

  /**
   * Passivates an RCX sensor. 
   */
  public final void passivate()
  {
    setPowerType(0);
  }
  
  /**
   * Returns mode compatible with Lego firmware. 
   * @return the current mode
   */
  public int getMode()
  {
    return mode;
  }
  
  /**
   * Returns type compatible with Lego firmware. 
   * @return The type of the sensor
   */
  public int getType()
  {
    return type;
  }
  
  /**
   * Sets type and mode compatible with Lego firmware. 
   * @param type the sensor type
   * @param mode the sensor mode
   */
  public void setTypeAndMode(int type, int mode)
  {
    setType(type);
    setMode(mode);
  }
  
  /**
   * Sets type compatible with Lego firmware. 
   * @param type the sensor type
   */
  public void setType(int type)
  {
	if (type < powerType.length)
	{
	    this.type = type;
	    int control = controlPins[type];
	    
	    setPowerType(powerType[type]);
	    if (control != DIGI_UNUSED)
        {
            setSensorPin(SP_DIGI0, ((control & DIGI_0_ON) != 0 ? 1 : 0));
            setSensorPin(SP_DIGI1, ((control & DIGI_1_ON) != 0 ? 1 : 0));
        }
	}
  }
  
  /**
   * Sets mode compatible with Lego firmware. 
   * @param mode the mode to set.
   */
  public void setMode(int mode)
  {
    this.mode = mode;
  }
  
  /**
   * Returns value compatible with Lego firmware. 
   * @return the computed value
   */
  public int readValue()
  {
    int rawValue = readSensorValue(iPortId);
    
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
   * <i>Low-level API</i> for reading sensor values.
   * Currently always returns the raw ADC value.
   * @param aPortId Port ID (0..4).
   * @param aRequestType ignored.
   */
  static native int readSensorValue (int aPortId);
  
  
  /**
   * Low-level method to set the input power setting for a sensor.
   * Values are: 0 - no power, 1 RCX active power, 2 power always on.
   *
   * @param type Power type to use
   */
  public void setPowerType(int type)
  {
	  setPowerTypeById(iPortId,type);
  }
  
  
  /**
   * Low-level method to set the input power setting for a sensor.
   * Values are: 0 - no power, 1 RCX active power, 2 power always on.
   **/
  static native void setPowerTypeById(int aPortId, int aPortType);
  
  /**
   * Call Port Listeners. Used by ListenerThread.
   */
  public synchronized void callListeners() {
    int newValue = readSensorValue( iPortId);
    for (int i = 0; i < iNumListeners; i++) {
      iListeners[i].stateChanged( this, iPreviousValue, newValue);
    }
   iPreviousValue = newValue;
  }
  
  /**
   * Low-level method to enable I2C on the port.
   * @param aPortId The port number for this device
   * @param mode I/O mode to use
   */
  public static native void i2cEnableById(int aPortId, int mode);
  
  /**
   * Low-level method to disable I2C on the port.
   * 
   * @param aPortId The port number for this device
   */
  public static native void i2cDisableById(int aPortId);
  
  /**
   * Low-level method to test if I2C connection is busy.
   * @param aPortId The port number for this device
   * @return > 0 if busy 0 if not
   */
  public static native int i2cBusyById(int aPortId);
  
  /**
   * Low-level method to start an I2C transaction.
   * @param aPortId The port number for this device
   * @param address The I2C address of the device
   * @param internalAddress The internal address to use for this operation
   * @param numInternalBytes The number of bytes in the internal address
   * @param buffer The buffer for write operations
   * @param numBytes Number of bytes to write or read
   * @param transferType 1==write 0==read
   * @return < 0 if there is an error
   */
  public static native int i2cStartById(int aPortId, int address,
		                            int internalAddress, int numInternalBytes,
		                            byte [] buffer, int numBytes, int transferType);
  
  /**
   * Complete and I2C operation and retrieve any data read.
   * @param aPortId The Port number for the device
   * @param buffer The buffer to be used for read operations
   * @param numBytes Number of bytes to read
   * @return < 0 if the is an error, or number of bytes transferred
   */
  public static native int i2cCompleteById(int aPortId, byte[] buffer, int numBytes);
  
  /**
   * Low-level method to enable I2C on the port.
   * @param mode The operating mode for the device
   */
  public void i2cEnable(int mode) {
	  i2cEnableById(iPortId, mode);
  }
  
  /**
   * Low-level method to disable I2C on the port.
   * 
   */
  public void i2cDisable() {
	  i2cDisableById(iPortId);
  }
  
  /**
   * Low-level method to test if I2C connection is busy.
   * @return > 0 if the device is busy 0 if it is not
   */
  public int i2cBusy() {
	  return i2cBusyById(iPortId);
  }
  
  /**
   * Low-level method to start an I2C transaction.
   * @param address Address of the device
   * @param internalAddress Internal register address for this operation
   * @param numInternalBytes Size of the internal address
   * @param buffer Buffer for write operations
   * @param numBytes Number of bytes to read/write
   * @param transferType 1==write 0 ==read
   * @return < 0 error
   */
  public int i2cStart(int address, int internalAddress,
		              int numInternalBytes, byte[] buffer,
		              int numBytes, int transferType) {
	  
	  return i2cStartById(iPortId, address, internalAddress,
			              numInternalBytes, buffer,
			              numBytes, transferType);
  }

  /**
   * Complete an I2C operation and transfer any read bytes
   * @param buffer Buffer for read data
   * @param numBytes Number of bytes to read
   * @return < 0 error otherwise number of bytes read.
   */
  public int i2cComplete(byte[] buffer, int numBytes)
  {
      return i2cCompleteById(iPortId, buffer, numBytes);
  }
  

  /**
   * Low level method to set the operating mode for a sensor pin.
   * @param port The port number to use
   * @param pin The pin id
   * @param mode The new mode
   */
  static native void setSensorPinMode(int port, int pin, int mode);

  /**
   * Set the output state of a sensor pin
   * @param port The port to use
   * @param pin The pin id
   * @param val The new output value (0/1)
   */
  static native void setSensorPin(int port, int pin, int val);

  /**
   * Read the current state of a sensor port pin
   * @param port The port to read
   * @param pin The pin id.
   * @return The current pin state (0/1)
   */
  static native int getSensorPin(int port, int pin);

  /**
   * Read the current ADC value from a sensor port pin
   * @param port The port to use.
   * @param pin The id of the pin to read (SP_DIGI1/SP_ANA)
   * @return The return from the ADC
   */
  static native int readSensorPin(int port, int pin);


  /**
   * Low level method to set the operating mode for a sensor pin.
   * @param pin The pin id
   * @param mode The new mode
   */
  public void setSensorPinMode(int pin, int mode)
  {
      setSensorPinMode(iPortId, pin, mode);
  }

  /**
   * Set the output state of a sensor pin
   * @param pin The pin id
   * @param val The new output value (0/1)
   */
  public void setSensorPin(int pin, int val)
  {
      setSensorPin(iPortId, pin, val);
  }

  /**
   * Read the current state of a sensor port pin
   * @param pin The pin id.
   * @return The current pin state (0/1)
   */
  public int getSensorPin(int pin)
  {
      return getSensorPin(iPortId, pin);
  }

  /**
   * Read the current ADC value from a sensor port pin
   * @param pin The id of the pin to read (SP_DIGI1/SP_ANA)
   * @return The return from the ADC
   */
  public int readSensorPin(int pin)
  {
      return readSensorPin(iPortId, pin);
  }

}



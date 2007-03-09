package lejos.nxt;

/**
 * Abstraction for a NXT input port.
 * 
 */
public class Port implements ListenerCaller
{
  private int iPortId;
  private short iNumListeners = 0;
  private PortListener[] iListeners;
  private int iPreviousValue;
  int type, mode;
  
  public static final int TYPE_NO_SENSOR = 0x00;
  public static final int TYPE_SWITCH = 0x01;
  public static final int TYPE_TEMPERATURE = 0x02;
  public static final int TYPE_REFLECTION = 0x03;
  public static final int TYPE_ANGLE = 0x04;
  public static final int TYPE_LIGHT_ACTIVE = 0x05;
  public static final int TYPE_LIGHT_INACTIVE = 0x06;
  public static final int TYPE_SOUND_DB = 0x07; 
  public static final int TYPE_SOUND_DBA = 0x08;
  public static final int TYPE_CUSTOM = 0x09;
  public static final int TYPE_LOWSPEED = 0x0A;
  public static final int TYPE_LOWSPEED_9V = 0x0B;
  
  public static final int MODE_RAW = 0x00;
  public static final int MODE_BOOLEAN = 0x20;
  public static final int MODE_TRANSITIONCNT = 0x40;
  public static final int MODE_PERIODCOUNTER = 0x60;
  public static final int MODE_PCTFULLSCALE = 0x80;
  public static final int MODE_CELSIUS = 0xA0;
  public static final int MODE_FARENHEIT = 0xC0;
  public static final int MODE_ANGLESTEP = 0xE0;

  /**
   * Port labeled 1 on NXT.
   */
  public static final Port S1 = new Port (0);

  /**
   * Port labeled 2 on NXT.
   */   
  public static final Port S2 = new Port (1);
  
  /**
   * Port labeled 3 on NXT.
   */
  public static final Port S3 = new Port (2);

  /**
   * Port labeled 4 on NXT.
   */
  public static final Port S4 = new Port (3);

  /**
   * Array containing all three ports [0..3].
   */
  public static final Port[] PORTS = { Port.S1, Port.S2, Port.S3, Port.S4 };

  /**
   * Reads the raw value of the sensor.
   */
  public final int readRawValue()
  {
    return readSensorValue (iPortId, 0);
  }

  /**
   * Reads the boolean value of the sensor.
   * Do not use - currently returns the raw value.
   */
  public final boolean readBooleanValue()
  {
	int rawValue = readSensorValue(iPortId,0);
    return (rawValue < 600);
  }

  private Port (int aId)
  {
    iPortId = aId;
    type = TYPE_NO_SENSOR;
    mode = MODE_RAW;
  }

  /**
   * Return the ID of the port. One of 0, 1, 2 or 3.
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
   * @see lejos.nxt.PortListener
   */
  public synchronized void addPortListener (PortListener aListener)
  {
    if (iListeners == null)
    {
        iListeners = new PortListener[8];
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
   * Passivates an RCX sensor sensor. 
   */
  public final void passivate()
  {
    setPowerType(0);
  }
  
  /**
   * Returns mode compatible with Lego firmware. 
   */
  public int getMode()
  {
    return mode;
  }
  
  /**
   * Returns type compatible with Lego firmware. 
   */
  public int getType()
  {
    return type;
  }
  
  /**
   * Sets type and mode compatible with Lego firmware. 
   */
  public void setTypeAndMode(int type, int mode)
  {
    this.type = type;
    this.mode = mode;
  }
  
  /**
   * Sets type compatible with Lego firmware. 
   */
  public void setType(int type)
  {
    this.type = type;
  }
  
  /**
   * Sets mode compatible with Lego firmware. 
   */
  public void setMode(int mode)
  {
    this.mode = mode;
  }
  
  /**
   * Returns value compatible with Lego firmware. 
   */
  public int readValue()
  {
    int rawValue = readSensorValue(iPortId,0);
    
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
   * @param aRequestType 0 = raw value, 1 = canonical value, 2 = boolean value.
   */
  public static native int readSensorValue (int aPortId, int aRequestType);
  
  /**
   * Low-level method to set the type of an A/D sensor.
   * A value of 1 will set pin 5, 2 will set pin 6, 3 will set both.
   * For example, a value of 1 sets floodlighting on a LightSensor,
   * a value of 1 sets DB mode and a value of 2 sets DBA mode, on a SoundSensor. 
   */
  public void setADType(int type)
  {
	  setADTypeById(iPortId,type);
  }
  
  /**
   * Low-level method to set the input power setting for a sensor.
   * Values are: 0 - no power, 1 RCX active power, 2 power always on.
   **/
  public void setPowerType(int type)
  {
	  setPowerTypeById(iPortId,type);
  }
  
  /**
   * Low-level method to set the type of an A/D sensor.
   * A value of 1 will set pin 5, 2 will set pin 6, 3 will set both.
   * For example, a value of 1 sets floodlighting on a LightSensor,
   * a value of 1 sets DB mode and a value of 2 sets DBA mode, on a SoundSensor. 
   */
  public static native void setADTypeById(int aPortId, int aADType);
  
  /**
   * Low-level method to set the input power setting for a sensor.
   * Values are: 0 - no power, 1 RCX active power, 2 power always on.
   **/
  public static native void setPowerTypeById(int aPortId, int aPortType);
  
  /**
   * Call Port Listeners. Used by ListenerThread.
   */
  public synchronized void callListeners() {
    int newValue = readSensorValue( iPortId, 0);
    for (int i = 0; i < iNumListeners; i++) {
      iListeners[i].stateChanged( this, iPreviousValue, newValue);
    }
   iPreviousValue = newValue;
  }
  
  public static native void i2cEnableById(int aPortId);
  
  public static native void i2cDisableById(int aPortId);
  
  public static native int i2cBusyById(int aPortId);
  
  public static native int i2cStartById(int aPortId, int address,
		                            int internalAddress, int numInternalBytes,
		                            byte [] buffer, int numBytes, int transferType);
  
  public void i2cEnable() {
	  i2cEnableById(iPortId);
  }
  
  public void i2cDisable() {
	  i2cDisableById(iPortId);
  }
  
  public int i2cBusy() {
	  return i2cBusyById(iPortId);
  }
  
  public int i2cStart(int address, int internalAddress,
		              int numInternalBytes, byte[] buffer,
		              int numBytes, int transferType) {
	  
	  return i2cStartById(iPortId, address, internalAddress,
			              numInternalBytes, buffer,
			              numBytes, transferType);
  }
}


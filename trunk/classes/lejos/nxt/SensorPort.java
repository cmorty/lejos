package lejos.nxt;
import lejos.addon.*;

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
  
   
  private static final byte[]  powerType = {0,0,1,1,1,0,0,0,0,0,0,2};
  private static final byte[]  adType = {-1,-1,-1,-1,-1,1,0,1,2,-1,-1,-1};

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
   */
  public final int readRawValue()
  {
    return readSensorValue (iPortId);
  }

  /**
   * Reads the boolean value of the sensor.
   * Do not use - currently returns the raw value.
   */
  public final boolean readBooleanValue()
  {
	int rawValue = readSensorValue(iPortId);
    return (rawValue < 600);
  }

  private SensorPort (int aId)
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
    setType(type);
    setMode(mode);
  }
  
  /**
   * Sets type compatible with Lego firmware. 
   */
  public void setType(int type)
  {
	if (type < powerType.length)
	{
	    this.type = type;
	    int adt = adType[type];
	    
	    setPowerType(powerType[type]);
	    if (adt >= 0) setADType(adt);
	}
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
   * Low-level method to set the type of an A/D sensor.
   * A value of 1 will set pin 5, 2 will set pin 6, 3 will set both.
   * For example, a value of 1 sets floodlighting on a LightSensor,
   * a value of 1 sets DB mode and a value of 2 sets DBA mode, on a SoundSensor. 
   */
  private void setADType(int type)
  {
	  setADTypeById(iPortId,type);
  }
  
  /**
   * Low-level method to set the input power setting for a sensor.
   * Values are: 0 - no power, 1 RCX active power, 2 power always on.
   **/
  private void setPowerType(int type)
  {
	  setPowerTypeById(iPortId,type);
  }
  
  /**
   * Low-level method to set the type of an A/D sensor.
   * A value of 1 will set pin 5, 2 will set pin 6, 3 will set both.
   * For example, a value of 1 sets floodlighting on a LightSensor,
   * a value of 1 sets DB mode and a value of 2 sets DBA mode, on a SoundSensor. 
   */
  static native void setADTypeById(int aPortId, int aADType);
  
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
   */
  public static native void i2cEnableById(int aPortId);
  
  /**
   * Low-level method to disable I2C on the port.
   * 
   */
  public static native void i2cDisableById(int aPortId);
  
  /**
   * Low-level method to test if I2C connection is busy.
   */
  public static native int i2cBusyById(int aPortId);
  
  /**
   * Low-level method to start an I2C transaction.
   */
  public static native int i2cStartById(int aPortId, int address,
		                            int internalAddress, int numInternalBytes,
		                            byte [] buffer, int numBytes, int transferType);
  
  /**
   * Low-level method to enable I2C on the port.
   */
  public void i2cEnable() {
	  i2cEnableById(iPortId);
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
   */
  public int i2cBusy() {
	  return i2cBusyById(iPortId);
  }
  
  /**
   * Low-level method to start an I2C transaction.
   */
  public int i2cStart(int address, int internalAddress,
		              int numInternalBytes, byte[] buffer,
		              int numBytes, int transferType) {
	  
	  return i2cStartById(iPortId, address, internalAddress,
			              numInternalBytes, buffer,
			              numBytes, transferType);
  }
}



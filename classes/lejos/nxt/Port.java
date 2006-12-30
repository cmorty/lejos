package lejos.nxt;

public class Port implements ListenerCaller
{
  private int iPortId;
  private short iNumListeners = 0;
  private PortListener[] iListeners;
  private int iPreviousValue;
  
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
   * Port labeled 3 on NXT.
   */
  public static final Port S4 = new Port (3);

  /**
   * Array containing all three ports [0..3].
   */
  public static final Port[] PORTS = { Port.S1, Port.S2, Port.S3, Port.S4 };

  /**
   * Reads the canonical value of the sensor.
   */
  public final int readValue()
  {
    return readSensorValue (iPortId, 1);
  }

  /**
   * Reads the raw value of the sensor.
   */
  public final int readRawValue()
  {
    return readSensorValue (iPortId, 0);
  }

  /**
   * Reads the boolean value of the sensor.
   */
  public final boolean readBooleanValue()
  {
    return readSensorValue (iPortId, 2) != 0;
  }

  private Port (int aId)
  {
    iPortId = aId;
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
   * @see josx.platform.rcx.SensorListener
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
   * Activates the sensor. This method should be called
   * if you want to get accurate values from the
   * sensor. In the case of light sensors, you should see
   * the led go on when you call this method.
   */
  public final void activate()
  {
    setPowerType(1);
  }

  /**
   * Passivates the sensor. 
   */
  public final void passivate()
  {
    setPowerType(0);
  }

  /**
   * <i>Low-level API</i> for reading sensor values.
   * @param aSensorId Sensor ID (0..2).
   * @param aRequestType 0 = raw value, 1 = canonical value, 2 = boolean value.
   */
  public static native int readSensorValue (int aPortId, int aRequestType);
  
  public void setADType(int type)
  {
	  setADTypeById(iPortId,type);
  }
  
  public void setPowerType(int type)
  {
	  setPowerTypeById(iPortId,type);
  }
  
  public static native void setADTypeById(int aPortId, int aADType);
  
  public static native void setPowerTypeById(int aPortId, int aPortType);
  
  public synchronized void callListeners() {
    int newValue = readSensorValue( iPortId, 0);
    for (int i = 0; i < iNumListeners; i++) {
      iListeners[i].stateChanged( this, iPreviousValue, newValue);
    }
   iPreviousValue = newValue;
  }
}


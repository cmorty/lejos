package javax.microedition.sensor;

/**
 * Abstract class that provides default methods for leJOS NXJ I2C sensors
 * 
 * @author Lawrie Griffiths
 */
public abstract class I2CSensorInfo implements SensorInfo {
	private String vendor, version, type;
	private int port;
	protected I2CChannelInfo[] infos;
	// Default reading rate, once every 20 milliseconds
	protected static final int MAX_RATE = 50;

	public int getConnectionType() {
		return SensorInfo.CONN_WIRED;
	}

	public String getContextType() {
		return SensorInfo.CONTEXT_TYPE_DEVICE;
	}

	public int getMaxBufferSize() {
		return 256;
	}

	public boolean isAvailabilityPushSupported() {
		return true;
	}

	public boolean isAvailable() {
		return SensorManager.findSensors(getUrl()) != null;
	}

	public boolean isConditionPushSupported() {
		return true;
	}
	
	public void setVersion(String version) {
		this.version = version;
	}
	
	public void setVendor(String vendor) {
		this.vendor = vendor;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getVendor() {
		return vendor;
	}
	
	public String getVersion() {
		return version;
	}
	
	public String getModel() {
		return vendor + "." + type + "." + version;
	}
	
	public Object getProperty(String name) {
		if (name.equals(SensorInfo.PROP_VENDOR)) return getVendor();;
		if (name.equals(SensorInfo.PROP_VERSION)) return getVersion();
		if (name.equals(SensorInfo.PROP_MAX_RATE)) return MAX_RATE;
		return null;
	}
	
	public String[] getPropertyNames() {
		return new String[]{SensorInfo.PROP_VENDOR, SensorInfo.PROP_VERSION, SensorInfo.PROP_MAX_RATE};
	}
	
	public String getDescription() {
		return getVendor() + " " + getModel() + " " + getVersion();
	}
	
	public void setPort(int port) {
		this.port = port;
	}
	
	public String getUrl() {
		// Port is a leJOS NXJ extension
		return "sensor:" + getQuantity() + ";contextType=" + getContextType() +
		       ";model=" + getModel() + ";port=" + port;
	}
		
	public I2CChannelInfo[] getChannelInfos() {
		return infos;
	}
	
	/**
	 * Return the names of all the models that implement this channel
	 * 
	 * @return the model names
	 */
	public abstract String[] getModelNames();
}

package javax.microedition.sensor;

import java.io.IOException;
import java.util.Hashtable;
import lejos.nxt.I2CSensor;
import lejos.nxt.SensorPort;

/**
 * Implementation of the SensorConnection interface for leJOS NXJ I2C sensors
 * 
 * @author Lawrie Griffiths
 */
public class I2CSensorConnection implements SensorConnection {
	private I2CChannelInfo[] channelInfos;
	private Hashtable channels = new Hashtable();
	private I2CSensor i2cSensor;
	private byte[] buf = new byte[2];
	private I2CSensorInfo info;
	private int state = SensorConnection.STATE_CLOSED;
	
	/**
	 * Create a sensor connection
	 * 
	 * @param name the sensor ID
	 * @param i2cSensor a generic I2C sensor object for the port
	 * @throws IOException
	 */
	public I2CSensorConnection(String url) throws IOException {	
		// Get the Sensor Info for available sensors
		SensorURL sensorURL = SensorURL.parseURL(url);
		I2CSensorInfo[] infos = SensorManager.getSensors(sensorURL);
		if (infos == null || infos.length == 0) throw new IOException();
		
		// If there is a choice, use the first one
		info = infos[0];
		
		// Get the port number and create the I2CSensor object
		sensorURL = SensorURL.parseURL(info.getUrl());
		i2cSensor = new I2CSensor(SensorPort.PORTS[sensorURL.getPortNumber()]);
	
		// Create the channels		
		channelInfos = (I2CChannelInfo[]) info.getChannelInfos();
		for(int i=0;i<channelInfos.length;i++) {
			channels.put(channelInfos[i],new I2CChannel(this, channelInfos[i]));
		}
		
		// Set the state of the connection
		state = SensorConnection.STATE_OPENED;
	}
	
	public Channel getChannel(ChannelInfo channelInfo) {
		return (Channel) channels.get(channelInfo);
	}

	public Data[] getData(int bufferSize) throws IOException {
		if (bufferSize > info.getMaxBufferSize()) 
			throw new IllegalArgumentException("Buffer size too large");
		
		I2CData[] data = new I2CData[channelInfos.length];
		for(int i=0;i<channelInfos.length;i++) {
			data[i] = new I2CData(channelInfos[i], bufferSize);
		}
		
		for(int i=0;i<bufferSize;i++) {		
			for(int j=0;j<channelInfos.length;j++) {
				data[j].setIntData(i, getChannelData(channelInfos[j]));
			}
		}
		return data;
	}
	
	public int getChannelData(I2CChannelInfo channelInfo) {
		int dataLength = channelInfo.getDataLength(); // in bits
		i2cSensor.getData(channelInfo.getRegister(), buf, (dataLength+7) / 8);
		int reading = 0;
		if (dataLength == 6) {
			reading = (buf[0] & 0x3F);
		} else if (dataLength == 8) {
			reading = (buf[0] & 0xFF);
		} else if (dataLength == 9) {
			reading = ((buf[0] & 0xff)<< 1) + buf[1];
		} else if (dataLength == 16) {
			reading = (buf[0] & 0xFF) | ((buf[1]) << 8);
		}
		return reading - channelInfo.getOffset();
	}

	public Data[] getData(int bufferSize, long bufferingPeriod,
			boolean isTimestampIncluded, boolean isUncertaintyIncluded,
			boolean isValidityIncluded) throws IOException {
		return getData(bufferSize);
	}

	public SensorInfo getSensorInfo() {
		return info;
	}
	
	public int getState() {
		return state;
	}

	public void removeDataListener() {
		SensorManager.removeDataListener(this);
		state = SensorConnection.STATE_OPENED;
	}

	public void setDataListener(DataListener listener, int bufferSize) {
		SensorManager.addDataListener(this, bufferSize, listener, 1000 / (Integer) info.getProperty(SensorInfo.PROP_MAX_RATE));
		state = SensorConnection.STATE_LISTENING;
	}

	public void setDataListener(DataListener listener, int bufferSize,
			long bufferingPeriod, boolean isTimestampIncluded,
			boolean isUncertaintyIncluded, boolean isValidityIncluded) {
		setDataListener(listener, bufferSize);
	}

	public void close() throws IOException {
		state = SensorConnection.STATE_CLOSED;
	}
}

package javax.microedition.sensor;

/**
 * Represents active asynchronous transfers
 * 
 * @author Lawrie
 *
 */
public class I2CActiveData {
	private I2CSensorConnection sensor;
	private int bufferSize;
	private DataListener listener;
	private int samplingInterval;
	private I2CData[] data;
	private int position = 0;
	private long lastSampleMillis=0;
	
	public I2CActiveData(I2CSensorConnection sensor, int bufferSize, DataListener listener, int samplingInterval) {
		this.sensor = sensor;
		this.bufferSize =bufferSize;
		this.listener = listener;
		this.samplingInterval = samplingInterval;
		data = createData();
	}
	
	public SensorConnection getSensor() {
		return sensor;
	}
	
	public int getBufferSize() {
		return bufferSize;
	}
	
	public DataListener getListener() {
		return listener;
	}
	
	public int getSamplingInterval() {
		return samplingInterval;
	}
	
	/**
	 * Process the entry. Check if we are ready to read a new sample and if so, read it.
	 * When the buffer is full, call the data listener and start a new buffer
	 */
	public void process() {
		if ((System.currentTimeMillis() - lastSampleMillis) >= samplingInterval) {
			//Read all the channels
			I2CChannelInfo[] channelInfos = (I2CChannelInfo[]) sensor.getSensorInfo().getChannelInfos();				
			for(int i=0;i<channelInfos.length;i++) {
				data[i].setIntData(position, sensor.getChannelData(channelInfos[i]));
			}			
			if (++position == bufferSize) {
				listener.dataReceived(sensor, data, false);
				data = createData();
				position = 0;
			}
			lastSampleMillis = System.currentTimeMillis();
		}	
	}
	
	private I2CData[] createData() {
		I2CChannelInfo[] channelInfos = (I2CChannelInfo[]) sensor.getSensorInfo().getChannelInfos();
		I2CData[] data = new I2CData[channelInfos.length];
		for(int i=0;i<channelInfos.length;i++) {
			data[i] = new I2CData(channelInfos[i], bufferSize);
		}
		return data;
	}
}

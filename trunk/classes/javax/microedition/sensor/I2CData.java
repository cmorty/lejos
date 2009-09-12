package javax.microedition.sensor;

/** 
 * Implementation of the Data interface for I2CSensors 
 * which contains default methods for implementations of channel data.
 * 
 * @author Lawrie Griffiths
 */
public class I2CData implements Data {
	protected int[] values;
	protected long timeStamp = System.currentTimeMillis();
	protected ChannelInfo info;
	
	public I2CData(ChannelInfo info, int bufferSize) {
		this.info = info;
		values = new int[bufferSize];
	}
	
	void setIntData(int index, int value) {
		values[index] = value;
	}
	
	public ChannelInfo getChannelInfo() {
		return info;
	}

	public double[] getDoubleValues() {
		throw new IllegalStateException();
	}

	public int[] getIntValues() {
		return values;
	}

	public Object[] getObjectValues() {
		throw new IllegalStateException();
	}

	public long getTimestamp(int index) {
		return timeStamp;
	}

	public float getUncertainty(int index) {
		return 0;
	}

	public boolean isValid(int index) {
		return true;
	}
}

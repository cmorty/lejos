package javax.microedition.sensor;

/**
 * Implementation of the channel interface for leJOS NXJ I2C channels
 * 
 * @author Lawrie Griffiths
 */
public class I2CChannel implements Channel {
	private I2CChannelInfo channelInfo;
	private I2CSensorConnection sensor;
	
	public I2CChannel(I2CSensorConnection sensor, I2CChannelInfo channelInfo) {
		this.sensor = sensor;
		this.channelInfo = channelInfo;
	}
	
	public void addCondition(ConditionListener listener, Condition condition) {
		SensorManager.addCondition(this, listener, condition);
	}

	public I2CChannelInfo getChannelInfo() {
		return channelInfo;
	}

	public String getChannelUrl() {
		// TODO: Add unique conditions
		return channelInfo.getName();
	}

	public Condition[] getConditions(ConditionListener listener) {
		return SensorManager.getConditions(this, listener);
	}

	public void removeAllConditions() {
		SensorManager.removeAllConditions(this);
	}

	public void removeCondition(ConditionListener listener, Condition condition) {
		SensorManager.removeCondition(this, listener, condition);
	}

	public void removeConditionListener(ConditionListener listener) {
		SensorManager.removeConditionListener(this, listener);
	}
	
	public I2CSensorConnection getSensor() {
		return sensor;
	}
}

package javax.microedition.sensor;

/**
 * Represents an active condition on a specific channel associated with a 
 * condition listener
 * 
 * @author Lawrie Griffiths
 */
public class I2CActiveCondition {
	private I2CChannel channel;
	private Condition condition;
	private ConditionListener conditionListener;
	
	public I2CActiveCondition(I2CChannel channel, Condition condition, 
			               ConditionListener conditionListener) {
		this.channel = channel;
		this.condition = condition;
		this.conditionListener = conditionListener;
	}
	
	public I2CChannel getChannel() {
		return channel;
	}
	
	public Condition getCondition() {
		return condition;
	}
	
	public ConditionListener getConditionListener() {
		return conditionListener;
	}
}

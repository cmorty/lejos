package javax.microedition.sensor;

/**
 * Abstract implementation ChannelInfo with extra methods needed for leJOS NXJ I2C sensors
 * 
 * @author Lawrie Griffiths
 *
 */
public abstract class I2CChannelInfo implements ChannelInfo {
	public int getRegister() {
		return 0x42;
	}

	public float getAccuracy() {
		return 0;
	}

	public int getDataType() {
		return ChannelInfo.TYPE_INT;
	}

	public int getScale() {
		return 0;
	}

	public int getDataLength() {
		return 8;
	}
	
	public Unit getUnit() {
		return Unit.getUnit("");
	}
	
	public int getOffset() {
		return 0;
	}
}

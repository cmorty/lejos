package lejos.nxt.addon;

import lejos.nxt.I2CPort;
import lejos.nxt.I2CSensor;
import lejos.util.EndianTools;

public class MindSensorPressureSensor extends I2CSensor {

	private static final int ADDRESS = 0x18; 
	private final byte[] buf = new byte[4];
	
	public MindSensorPressureSensor(I2CPort port) {
		// also works with high speed mode
		super(port, ADDRESS, I2CPort.LEGO_MODE, TYPE_LOWSPEED);
	}

	public int GetPressureInPascal() {
		this.getData(0x53, buf, 0, 4);

		return EndianTools.decodeIntLE(buf, 0);
	}
}

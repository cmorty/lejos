package lejos.nxt;

import lejos.util.EndianTools;

/** 
 * Lego Education Temperature Sensor 9749
 * 
 * @author Michael Mirwaldt
 */
public class TemperatureSensor extends I2CSensor
{
	/*
	 * Documentation of the chip can be found here: http://focus.ti.com/docs/prod/folders/print/tmp275.html
	 * Some details from LEGOTMP-driver.h, http://rdpartyrobotcdr.sourceforge.net/
	 */	

	protected final static int I2C_ADDRESS 		= 0x98;
	protected final static int REG_TEMPERATURE 	= 0x00;
	protected final static int REG_CONFIG 	= 0x01;
	
	public enum Accuracy {
		/** 0.5 C° accuracy */
		C0_5(0x00, 28),
		/** 0.25 C° accuracy */
		C0_25(0x20, 55),
		/** 0.125 C° accuracy */
		C0_125(0x40, 110),
		/** 0.0625 C° accuracy */
		C0_0625(0x60, 220);
		
		final int bitmask;
		final int delay;

		private Accuracy(int index, int wait) {
			this.bitmask = index;
			this.delay = wait;
		}
		
		/**
		 * How many milli seconds each measurement takes.
		 * @return the delay in ms
		 */
		public int getDelay()
		{
			return this.delay;
		}
		
		static Accuracy toAccuracy(int index) {
			for (Accuracy tempAccuracy : values()) {
				if(tempAccuracy.bitmask==index) {
					return tempAccuracy;
				}
			}
			throw new IllegalArgumentException("index(='"+index+"') cannot be associated with a TempAccuracy!");
		}		 
		
	}

	private final byte[] buf = new byte[2];
	
	public TemperatureSensor(I2CPort port) {
		super(port, I2C_ADDRESS, I2CPort.LEGO_MODE, TYPE_LOWSPEED);
	}

	public float getTemperature() {
		getData(REG_TEMPERATURE, buf, 2);
		return EndianTools.decodeShortBE(buf, 0)  * 0x1p-8f;
	}
	
	public Accuracy getAccuracy() {
		getData(REG_CONFIG, buf, 1);
		return Accuracy.toAccuracy(buf[0] & 0x60);
	}
	
	public void setAccuracy(Accuracy ta) {
		//TODO preserve other bits
		sendData(REG_CONFIG, (byte) ta.bitmask);
	}
	
	/**
	 * Sensor does not support Lego standard I2C layout.
	 */
	@Override
	public String getProductID() {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Sensor does not support Lego standard I2C layout.
	 */
	@Override
	public String getSensorType() {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Sensor does not support Lego standard I2C layout.
	 */
	@Override
	public String getVersion() {
		throw new UnsupportedOperationException();
	}
	
	// TODO sensor can be turned off and on, supports single-shot and continous mode
}

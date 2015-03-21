package lejos.nxt.addon;

import lejos.nxt.I2CPort;
import lejos.nxt.I2CSensor;
import lejos.nxt.SensorPort;
import lejos.util.Delay;
import lejos.util.EndianTools;

/**
 * Support for the Dexter Industries Thermal Infrared Sensor.
 * <p>
 * The Dexter Industries Thermal Infrared Sensor reads surface temperatures of objects. 
 * It is a non-contact thermometer based on the Melexis MLX90614xCC. Detecting the infrared radiation 
 * from an object, the sensor can read object temperatures between -90\u00b0F and 700\u00b0F 
 * (-70\u00b0C and +380\u00b0C).  The sensor has a high accuracy of 0.5\u00b0C and a resolution of 0.02\u00b0C.
 * <p>
 * The Thermal Infrared Sensor reads both the ambient temperature (the temperature of the air 
 *around the sensor) and the surface temperature of the object that the sensor is pointed towards.
 *
 * @author Kirk P. Thompson
 *
 */
public class DThermalIR extends I2CSensor {
	private static final int I2C_ADDRESS = 0x0E;
	private static final int REG_GET_OBJECT = 0x01;
	private static final int REG_GET_AMBIENT = 0x00;
	private static final int REG_GET_EMISSIVITY = 0x03;
	private static final int REG_SET_EMISSIVITY = 0x02;
	private static final int MAX_EMISSIVITY = 10000;
	private static final int I2C_RETRIES = 8;
	
	private byte [] buf = new byte[2];
	// The I2C slave address of the sensor is 0x0E 
	//0×00 – This register stores the Ambient Temperature read by the sensor.
	//0×01 - This register stores the Object Temperature read by the sensor.
	//0×02 – Write the emissivity, in two bytes, to this register.
	//0×03 – This register stores the current emissivity value.
	
	/**
	 * Construct a sensor instance that is connected to <code>port</code>.
	 * @param port The NXT port to use
	 */
	public DThermalIR(SensorPort port) {
		super(port, I2C_ADDRESS, I2CPort.LEGO_MODE, TYPE_LOWSPEED);
		Delay.msDelay(100);
	}
	
	private int readRawInt(int register){
		return EndianTools.decodeUShortLE(readRaw(register), 0);
	}
	
	private synchronized byte[] readRaw(int register){
		int retryCount=0;
		buf = new byte[2];
		int retval;
		retval = getData(register, buf, 2);
		while (retval<0) {
			Delay.msDelay(10);
			retval = getData(register, buf, 2);
			if (++retryCount> I2C_RETRIES) {
				buf[0]=buf[1]=0;
//				System.out.println("bad i2c read");
				break;
			}
		}
		return buf;
	}
	
	/**
	 * Read the object temperature
	 * @return degrees Celsius
	 */
	public float readObject(){
		float retval = .02f * readRawInt(REG_GET_OBJECT) - 273.15f;
		//retval = retval * 1.8f + 32; //TODO remove fahrenheit conversion after testing
		return retval;
	}
	
	/**
	 * Read the ambient temperature
	 * @return degrees Celsius
	 */
	public float readAmbient(){
		float retval = .02f * readRawInt(REG_GET_AMBIENT) - 273.16f;
		//retval = retval * 1.8f + 32; //TODO remove fahrenheit conversion after testing
		return retval;
	}
	
	/**
	 * Read the current emissivity value. 
	 * <p>
	 * Caveat Emptor: The sensor appears to only return the emissivity value
	 * after it was intially set after power-up with <code>setEmissivity()</code>. It doesn't seem 
	 * to retrieve it from EEPROM.
	 * 
	 * @return The emissivity value from 0.01 to 1.0
	 */
	public float readEmissivity(){
		byte[] tbuf = readRaw(REG_GET_EMISSIVITY);
//		LCD.drawString("" + (buf[0] & 0xff) + " " + (buf[1] & 0xff), 0,5);
		float e= EndianTools.decodeUShortLE(tbuf, 0) / 65535f;
		return e;
	}
	
	
	/**
	 * Set the sensor's emissivity value. Valid values are 
	 * 0.01-1.0. The emissivity is stored in non-volatile memory of the sensor and will be 
	 * retained even after power-off.
	 * 	 <p> 
	 * < 0.01 returns with no action. > 1.0 sets to 1.0
	 * @param emissivity The value to set emissivity coefficient to
	 */
	public synchronized void setEmissivity(float emissivity){
		int intEmissivity = Math.round(emissivity * MAX_EMISSIVITY);
		if (intEmissivity<100) return;
		if (intEmissivity>MAX_EMISSIVITY) intEmissivity=MAX_EMISSIVITY;
		int retval;
		
		EndianTools.encodeShortBE(intEmissivity, buf, 0);
//		LCD.drawString("e=" + EndianTools.decodeUShortBE(buf, 0) + "  ", 0,4);
		retval = sendData(REG_SET_EMISSIVITY, buf, 2);
		if (retval<0) {
			 // TODO need error notice here?
//			System.out.println("setemsty err");
		}
		Delay.msDelay(500);
	}
	
	@Override
	public String getProductID() {
		return "dTIR";
	}

	@Override
	public String getVendorID() {
		return "Dexter";
	}
	
}

package lejos.nxt;
/**
 * Supports Mindsensors DIST-Nx series of Optical Distance Sensor.
 * This sensor is used for greater precision and accuracy than the Ultrasonic Sensor.
 * 
 * @author Michael Smith <mdsmitty@gmail.com>
 * 
 */

public class OpticalDistanceSensor extends I2CSensor{
	private byte[] buf = new byte[1];
	
	//Registers
	private final static byte COMMAND = 0x41;
	private final static byte DIST_DATA_LSB = 0x42;
	private final static byte DIST_DATA_MSB = 0x43;
	private final static byte VOLT_DATA_LSB = 0x44;
	private final static byte VOLT_DATA_MSB = 0x45;
	private final static byte SENSOR_MOD_TYPE = 0x50;
	private final static byte CUSTOM_CURVE = 0x51;
	private final static byte DIST_MIN_DATA_LSB = 0x52;
	private final static byte DIST_MIN_DATA_MSB = 0x53;
	private final static byte DIST_MAX_DATA_LSB = 0x54;
	private final static byte DIST_MAX_DATA_MSB = 0x55;
	private final static int VOLT_DATA_POINT_LSB = 0x52;
	private final static int VOLT_DATA_POINT_MSB = 0x53;
	private final static int DIST_DATA_POINT_LSB = 0x54;
	private final static int DIST_DATA_POINT_MSB = 0x55;
	
	//Sensor Modules
	public final static byte GP2D12 = 0x31;
	
	/**
	 * DIST-Nx-Short
	 */
	public final static byte GP2D120 = 0x32;
	
	/**
	 * DIST-Nx-Medium
	 */
	public final static byte GP2YA21 = 0x33;
	
	/**
	 * DIST-Nx-Long
	 */
	public final static byte GP2YA02 = 0x34;
	public final static byte CUSTOM = 0x35;
	
	//Commands
	private final static byte DE_ENERGIZED = 0x44;
	private final static byte ENERGIZED = 0x45;
	private final static byte ARPA_ON = 0x4E;
	private final static byte ARPA_OFF = 0x4F; //(default)
	
	/**
	 * 
	 * @param port NXT sensor port 1-4
	 */
	public OpticalDistanceSensor(I2CPort port){
		super(port);
		powerOn();
	}
	
	/**
	 * This only needs the be run if you are changing the sensor
	 * @param module changes the sensor module attached the he board.
	 * 
	 */
	public void setModule(byte module){
		sendData(COMMAND, module);
	}
	
	/**
	 * Returns the distance from the object. This returns the same value as getDistLSB.
	 * @return int
	 */
	public int getDistance(){
		return getDistLSB();
	}
	/**
	 * Turns the sensor module on.  
	 * Power is turned on by the constuctor method.
	 *
	 */
	public void powerOn(){
		sendData(COMMAND, ENERGIZED);
	}
	
	/**
	 * Turns power to the sensor module off.
	 *
	 */
	public void powerOff(){
		sendData(COMMAND, DE_ENERGIZED);
	}
	
	/**
	 * Enables (ADPA) Auto Detecting Parallel Architecture. Once you have enabled it you dont have to enable again.
	 *
	 */
	public void setAPDAOn() {
		sendData(COMMAND, ARPA_ON);
	}
	
	/**
	 * Disables (ADPA) Auto Detecting Parallel Architecture, Disabled by default.
	 *
	 */
	public void setAPDAOff() {
		sendData(COMMAND, ARPA_OFF);
	}
	
	/**
	 * Returns the current distance for LSB based on the current curve.
	 * @return int
	 */
	public int getDistLSB(){
		getData(DIST_DATA_LSB, buf, 1);
		return buf[0] & 0xFF;
	}
	
	/**
	 * Returns the current distance for MSB based on the current curve.
	 * @return int
	 */
	public int getDistMSB(){
		getData(DIST_DATA_MSB, buf, 1);
		return buf[0] & 0xFF;
	}
	
	/**
	 *  Returns the current voltage level for LSB
	 * @return int
	 */
	public int getVoltLSB(){
		getData(VOLT_DATA_LSB, buf, 1);
		return buf[0] & 0xFF;
	}
	
	/**
	 * Returns the current voltage level for MSB
	 * @return int
	 */
	public int getVoltMBS(){
		getData(VOLT_DATA_MSB, buf, 1);
		return buf[0] & 0xFF;
	}
	
	/**
	 * Used to determin the sensore module that is configured. 
	 * This can be helpful if the sensor is not working properly.
	 * @return int
	 */
	public int getSensorModule(){
		getData(SENSOR_MOD_TYPE, buf, 1);
		return buf[0] & 0xFF;
	}
	
	/**
	 * Gets the number of points that will be in the curve.
	 * This corisponds with the set/get Vold and Distance points methods.
	 * Used for recalibrating the sensor. 
	 * @return int
	 */
	public int getCustomCurveCount(){
		getData(CUSTOM_CURVE, buf, 1);
		return buf[0] & 0xFF;
	}
	
	/**
	 * Set the number of points that will be in the curve.
	 * This corisponds with the set/get Vold and Distance points methods.
	 * Used for recalibrating the sensor. 
	 * @param value max 39
	 */
	public void setCustomCurveCount(int value){
		sendData(CUSTOM_CURVE, (byte)value);
	}	
	
	/**
	 * Gets the min value for LSB that will be in the curve.
	 * Used for recalibrating the sensor. 
	 * @return int
	 */
	public int getDistMinLSB(){
		getData(DIST_MIN_DATA_LSB, buf, 1);
		return buf[0] & 0xFF;
	}
	
	/**
	 * Sets the min value for LSB that will be in the curve.
	 * Used for recalibrating the sensor. 
	 * @param value int
	 */
	public void setDistMinLSB(int value){
		sendData(DIST_MIN_DATA_LSB, (byte)value);
	}
	
	/**
	 * Gets the min value for MSB that will be in the curve.
	 * Used for recalibrating the sensor. 
	 * @return int
	 */
	public int getDistMinMSB(){
		getData(DIST_MIN_DATA_MSB, buf, 1);
		return buf[0] & 0xFF;
	}
	
	/**
	 * Sets the min value for MSB that will be in the curve.
	 * Used for recalibrating the sensor. 
	 * @param value int
	 */
	public void setDistMinMSB(int value){
		sendData(DIST_MIN_DATA_MSB, (byte)value);
	}
	
	/**
	 * Gets the max value for LSB that will be in the curve.
	 * Used for recalibrating the sensor. 
	 * @return int
	 */
	public int getDistMaxLSB(){
		getData(DIST_MAX_DATA_LSB, buf, 1);
		return buf[0] & 0xFF;
	}
	
	/**
	 * Sets the max value for LSB that will be in the curve.
	 * Used for recalibrating the sensor. 
	 * @param value int
	 */
	public void setDistMaxLSB(int value){
		sendData(DIST_MAX_DATA_LSB, (byte)value);
	}
	
	/**
	 * Gets the max value for MSB that will be in the curve.
	 * Used for recalibrating the sensor. 
	 * @return int
	 */	
	public int getDistMaxMSB(){
		getData(DIST_MAX_DATA_MSB, buf, 1);
		return buf[0] & 0xFF;
	}
	
	/**
	 * Sets the max value for MSB that will be in the curve.
	 * Used for recalibrating the sensor. 
	 * @param value int
	 */
	public void setDistMaxMSB(int value){
		sendData(DIST_MAX_DATA_MSB, (byte)value);
	}
	
	/**
	 * Gets volt value with in the curve for LSB. These will corispond with the point methods index value.
	 * Used for recalibrating the sensor.
	 * @param index max 39
	 * @return int
	 */
	public int getVoltPointLSB(int index){
		if(index == 0) index = 1;
		index = VOLT_DATA_POINT_LSB + 4 * index;
		getData(index, buf, 1);
		return buf[0] & 0xFF;
	}
	
	/**
	 * Sets volt value with in the curve for LSB. These will corispond with the point methods index value.
	 * Used for recalibrating the sensor.
	 * @param index max 39
	 * @param value int
	 */
	public void setVoltPointLSB(int index, int value){
		if(index == 0) index = 1;
		index = VOLT_DATA_POINT_LSB + 4 * index;
		sendData(index, (byte)value);
	}
	
	/**
	 * Gets volt value with in the curve for MSB. These will corispond with the point methods index value.
	 * Used for recalibrating the sensor.
	 * @param index max 39
	 * @return Returns int
	 */
	public int getVoltPointMSB(int index){
		if(index == 0) index = 1;
		index = VOLT_DATA_POINT_MSB + 4 * index;
		getData(index, buf, 1);
		return buf[0] & 0xFF;
	}
	
	/**
	 * Sets volt value with in the curve for MSB. These will corispond with the point methods index value.
	 * Used for recalibrating the sensor.
	 * @param index max 39
	 * @param value int
	 */
	public void setVoltPointMSB(int index, int value){
		if(index == 0) index = 1;
		index = VOLT_DATA_POINT_MSB + 4 * index;
		sendData(index, (byte)value);
	}
	
	/**
	 * Gets points with in the curve for LSB. Used for recalibrating the sensor.
	 * @param index max 39
	 * @return Returns int
	 */
	public int getDistPointLSB(int index){
		if(index == 0) index = 1;
		index = DIST_DATA_POINT_LSB + 4 * index;
		getData(index, buf, 1);
		return buf[0] & 0xFF;
	}
	
	/**
	 * Sets points with in the curve for LSB. Used for recalibrating the sensor.
	 * @param index max 39
	 * @param value int
	 */
	public void setDistPointLSB(int index, int value){
		if(index == 0) index = 1;
		index = DIST_DATA_POINT_LSB + 4 * index;
		sendData(index, (byte)value);
	}
	
	/**
	 * Gets points with in the curve for MSB. Used for recalibrating the sensor.
	 * @param index max 39
	 * @return int
	 */
	public int getDistPointMSB(int index){
		if(index == 0) index = 1;
		index = DIST_DATA_POINT_MSB + 4 * index;
		getData(index, buf, 1);
		return buf[0] & 0xFF;
	}
	
	/**
	 * Sets points with in the curve for MSB. Used for recalibrating the sensor.
	 * @param index max 39.
	 * @param value int
	 */
	public void setDistPointMSB(int index, int value){
		if(index == 0) index = 1;
		index = DIST_DATA_POINT_MSB + 4 * index;
		sendData(index, (byte)value);
	}
}
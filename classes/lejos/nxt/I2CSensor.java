package lejos.nxt;

public abstract class I2CSensor implements SensorConstants {
	I2CPort port;
	int address = 1;
	String version = "        ";
	String productID = "        ";
	String sensorType = "        ";
	char [] versionChars = StringUtils.getCharacters(version);
	char [] productIDChars = StringUtils.getCharacters(productID);
	char [] sensorTypeChars = StringUtils.getCharacters(sensorType);
	byte[] byteBuff = new byte[8]; 
	
	public I2CSensor(I2CPort port)
	{
		this.port = port;
		port.i2cEnable();
		port.setType(TYPE_LOWSPEED);
	}
	
	public int getData(int register, byte [] buf, int len) {	
		int ret = port.i2cStart(address, register, len, buf, len, 0);
		
		if (ret != 0) return ret;
		
		while (port.i2cBusy() != 0) {
			Thread.yield();
		}
		
		return 0;
	}
	
	public int sendData(int register, byte [] buf, int len) {	
		int ret = port.i2cStart(address, register, len, buf, len, 1);
		
		if (ret != 0) return ret;
		
		while (port.i2cBusy() != 0) {
			Thread.yield();
		}
		
		return 0;
	}
	
	public String getVersion() {
		int ret = getData(0x00, byteBuff, 8);

		for(int i=0;i<8;i++) {
			versionChars[i] = (ret == 0 ? (char) byteBuff[i] : ' ');
		}	
		return version;
	}
	
	public String getProductID() {
		int ret = getData(0x08, byteBuff, 8);

		for(int i=0;i<8;i++) {
			productIDChars[i] = (ret == 0 ? (char) byteBuff[i] : ' ');
		}	
		return productID;
	}
	
	public String getSensorType() {
		int ret = getData(0x10, byteBuff, 8);

		for(int i=0;i<8;i++) {
			sensorTypeChars[i] = (ret == 0 ? (char) byteBuff[i] : ' ');
		}	
		return sensorType;
	}
}

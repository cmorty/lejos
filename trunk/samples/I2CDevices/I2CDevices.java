import lejos.nxt.*;

/**
 * Display the type and address of any I2C sensors attached to the NXT
 * @author andy
 */
public class I2CDevices {


    /**
     * Provides low level access to a sensor.
     */
    static class RawI2CSensor extends I2CSensor {

        RawI2CSensor(SensorPort p)
        {
            super(p);
        }

        /**
         * Set the address of the port
         * Addresses use the standard Lego/NXT format and are in the range 0x2-0xfe.
         * The low bit must always be zero. Some data sheets (and older versions
         * of leJOS) may use i2c 7 bit format (0x1-0x7f) in which case this address
         * must be shifted left one place to be used with this function.
         *
         * @param addr 0x02 to 0xfe
         */
            @Override
        public void setAddress(int addr)
        {
            super.address = addr;
        }

    }


	public static void main(String[] args) throws Exception {
		RawI2CSensor[] sensors = {
				new RawI2CSensor(SensorPort.S1),
				new RawI2CSensor(SensorPort.S2),
				new RawI2CSensor(SensorPort.S3),
		        new RawI2CSensor(SensorPort.S4)};

		LCD.drawString("P Type     Addr", 0, 0);
		LCD.setAutoRefresh(false);
		while (!Button.ESCAPE.isPressed()) {
			for(int i=0;i<sensors.length;i++) {
                LCD.drawInt(i+1, 1, 0, i+1);
                String sensorType = "";
                int address;
                for(address=2; address < 256; address += 2)
                {
                    sensors[i].setAddress(address);
                    sensorType = sensors[i].getSensorType();
                    if (!sensorType.isEmpty()) break;
                }
                if (address < 256)
                {
                    LCD.drawString(sensorType, 2, i+1);
                    LCD.drawInt(address, 3, 12, i+1);
                }
                else
                    LCD.drawString("              ", 2, i+1);
				LCD.refresh();
			}
		}
	}

}

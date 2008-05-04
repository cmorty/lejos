package lejos.nxt;

import lejos.nxt.*;
import java.util.ArrayList;

/**
*
* Abstraction for a  Lattebox NXT Extension Kit with  Lattebox 10-Axis Servo Kit
* http://www.lattebox.com
* 
* The physical design  is:
* 
*  *********************************
*  * NXT PORT                      *
*  * NXT PORT      SC4 SC3 SC2 SC1 *
*  * NXT PORT  PIN SC4 SC3 SC2 SC1 *
*  * NXT PORT      SC4 SC3 SC2 SC1 *
*  *               SC4 SC3 SC2 SC1 * 
*  *                               *
*  * PIN                           *
*  *                               *
*  *********************************
*
* @author Juan Antonio Brenha Moral
*/
public class NXTe  extends I2CSensor{
	//LSC
	private ArrayList arrLSC;
	private final int MAXIMUM_LSC = 4;
	
	//Exception handling
	private final String ERROR_SERVO_DEFINITION =  "Error with Servo Controller definition";
	private final String ERROR_SPI_CONFIGURATION = "Error in SPI Configuration";
	
	//I2C
	private SensorPort portConnected;
	private final byte SPI_PORT[] = {0x01,0x02,0x04,0x08};//SPI Ports where you connect LSC
	public static final byte NXTE_ADDRESS = 0x28;
	private final byte REGISTER_IIC = (byte)0xF0;//NXTe IIC address
	private int I2C_Response;
	
	/**
	 * Constructor
	 * 
	 * @param port
	 */
	public NXTe(SensorPort port) throws Exception{
		super(port);

		port.setType(TYPE_LOWSPEED_9V);
		port.setMode(MODE_RAW);
		
		portConnected = port;
		
		arrLSC = new ArrayList();
		
		this.setAddress((int) NXTE_ADDRESS);

		I2C_Response = this.sendData((int)this.REGISTER_IIC, (byte)0x0c);

		if(I2C_Response != 0){
			throw new Exception(this.ERROR_SPI_CONFIGURATION); 
		}
	}
	
	/**
	 * Add a LSC
	 * 
	 * @param SPI_PORT
	 * @throws Exception
	 */
	public void addLSC(int SPI_PORT) throws Exception{
		if(arrLSC.size() <= MAXIMUM_LSC){
			LSC LSCObj = new LSC(this.portConnected,this.SPI_PORT[SPI_PORT]);
			arrLSC.add(LSCObj);
		}else{
			throw new Exception(ERROR_SERVO_DEFINITION);
		}		
	}	
	
	/**
	 * Get a LSC
	 * 
	 * @param index
	 * @return LSC
	 */
	public LSC getLSC(int index){
		return (LSC) arrLSC.get(index);
	}
}
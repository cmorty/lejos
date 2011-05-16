
import lejos.nxt.rcxcomm.*;
import java.io.*;
import lejos.nxt.*;

/**
 * Test of NXT emulation of the leJOS RCX RCXPort class.
 * 
 * Run the LLCSensorReader sample on the RCX, and the NXT 
 * will read the raw sensor value of the sensor connected to
 * the RCX S2 port.
 * 
 * Requires a Mindsensors NRLink adapter connected to sensor
 * port S1 on the NXT.
 * 
 * @author Lawrie Griffiths
 *
 */
public class RCXPortTest {

	public static void main(String[] args) throws Exception {
	    try {
	      RCXPort port = new RCXPort(SensorPort.S1);
	      String reading = "Reading Sensor";
	      String rcvd = "Received ";

	      InputStream is = port.getInputStream();
	      OutputStream os = port.getOutputStream();
	      DataInputStream dis = new DataInputStream(is);
	      DataOutputStream dos = new DataOutputStream(os);

	      LCD.drawString(reading,0,0);
	      LCD.refresh();
	      int sendTime = (int)System.currentTimeMillis();
	      for(int i=0;i<20;i++) {
	        dos.writeByte(1);
	        dos.flush();

	        int n = dis.readShort();

	        LCD.drawString(rcvd,0,1);
	        LCD.drawInt(n,4,10,1);
	        LCD.refresh();
	      }
	      LCD.drawString("Time: ", 0, 2);
	      LCD.drawInt((int)System.currentTimeMillis() - sendTime,6,6,2);
	      LCD.refresh();
	      Button.waitForPress();
	    }
	    catch (Exception e) {
	      LCD.drawString("Exception",0,7);
	      LCD.refresh();
	      Button.waitForPress();
	    }
	}
}

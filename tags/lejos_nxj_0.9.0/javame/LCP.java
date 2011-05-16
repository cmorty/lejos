import java.io.*;
import javax.microedition.io.*;

/**
 * LEGO Communications Protocol for JavaME (partial at present)
 * Used to communicate with an NXT brick with either standard firmware or leJOS NXJ
 * 
 */
public class LCP {
	private InputStream in;
	private OutputStream out;
	private int dataPacket[];
	boolean connected;	
	private String btName = null;
	private StreamConnection con;	
		
	public LCP(String brickName){
		this.btName = brickName;
		connected = false;
	}
		
	public void connect(){
	    if(!connected){
			try{
				con =(StreamConnection)Connector.open(btName);
				in = con.openInputStream();
				out = con.openOutputStream();
				System.out.println("Device successfuly connected to: " + btName);
				connected = true;
			} catch (IOException e) {
				System.err.println("Can't connect to: " + btName);
				connected = false;
			}
		}
	}
	
	synchronized void writePacket(int packet[]) throws IOException {
		out.write(packet.length & 0xFF);
		out.write(packet.length >> 8);
		
		for(int i = 0; i < packet.length; i++){
			out.write(packet[i]);
		}
		out.flush();
	}
	
	synchronized void readPacket() throws IOException{
		int data;
		
		data = in.read();
		data += in.read() << 8;
		
		dataPacket = new int[data]; 
		// Wait until a package has been fully received
		for(int i = 0; i < data; i++){
			dataPacket[i] = (int)in.read();
		}
	}
	
	synchronized void lsWrite(int port, int packet[]){
		try{
			out.write(packet.length + 5);
			out.write(0x00);
			out.write(0x80);
			out.write(0x0F);
			out.write(port);
			out.write(packet.length);
			out.write(1);
			for(int i = 0; i < packet.length; i++){
			    out.write(packet[i]);
			}
			out.flush();
		} catch (IOException e) {
			System.err.println("Can't get value from LS device on port: " + (port + 1));
			connected = false;
		}
	}	
	
	
	public boolean setOutputState(int port, int power, int regMode, int ratio){
		int packet[] = {0x80, 0x04, port, power, 0x05, regMode, ratio, 0x20, 0x00, 0x00, 0x00, 0x00};
		try{
			writePacket(packet);		
			return true;
		} catch (IOException e) {
			System.err.println("Couldn't configure output" + (port + 1));
			connected = false;
			return false;
		}		
	}
	
	public boolean setInputMode(int port, int type, int mode){
	    int packet[] = {0x80, 0x05, port, type, mode};
		try{
			writePacket(packet);
			return true;
		} catch (IOException e) {
			System.err.println("Couldn't configure sensor port" + (port + 1));
			connected = false;
			return false;
		}		
	}
		
	public int getInputValue(int sensor){
		int packet[] = {0x00, 0x07, sensor};
		
		try{
			writePacket(packet);
			readPacket();		
			return (dataPacket[11]*256 + dataPacket[10]);

		} catch (IOException e) {
			System.err.println("Can't get value from sensor: " + (sensor + 1));
			connected = false;
			return 0;
		}
	}

	public int lsGetStatus(int port){
		int packet[] = { 0x00, 0x0E, port};
		int anz;
		try{
			writePacket(packet);
			readPacket();
			return (dataPacket[3]);

		} catch (IOException e) {
			System.err.println("Can't get value from LS device on port: " + (port + 1));
			connected = false;
			return 0;
		}
	}	
	
	int lsRead(int port){
		int packet[] = {0x00, 0x10, port};
		int anz;
		
		try{
			writePacket(packet);		
			readPacket();
			return (dataPacket[4]);

		} catch (IOException e) {
			System.err.println("Can't get value from LS device on port: " + (port + 1));
			connected = false;
			return 0;
		}
	}	
		
	public int getBatteryLevel(){
		int packet[] = {0x00, 0x0B};
		
		try{
			writePacket(packet);
			readPacket();
			return ((dataPacket[4] << 8) + dataPacket[3]);

		} catch (IOException e) {
			System.err.println("Can't get battery value");
			connected = false;
			return 0;
		}
	}
	
	public void close(){
		try{
			connected = false;
			in.close();
			out.close();
			con.close();
		} catch (IOException e) {
			System.err.println("Can't close connection");
		}
	}
}

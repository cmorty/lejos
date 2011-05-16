package org.lejos.j2mesamples;

import java.io.IOException;

import javax.microedition.lcdui.*;
import javax.microedition.midlet.*;
import java.io.*;
import lejos.j2me.comm.*;

/**
 * @author Juan Antonio Brenha Moral
 *
 */
public class Form1 extends Form{
	private String strAppName = "BTConnectTest for Java ME";
	private ImageItem leJOSLogo;
	MIDlet midlet;
	DataInputStream dis;
	DataOutputStream dos;
	NXTComm nxtComm;

	public Form1(MIDlet m){
		super("BTConnectTest");
		this.designForm1();
		this.midlet = m;
	}
	
	private void designForm1(){
		this.append(strAppName);
		try {
			leJOSLogo = new ImageItem(
				"Developed by: ",
				Image.createImage("/leJOS.png"),
				ImageItem.LAYOUT_DEFAULT,
				"leJOS Logo");
			this.append(leJOSLogo);
		} catch(Exception e) {}
		this.append("\n");
	}
	
	public void connect() {		
		NXTInfo[] nxtInfos = null;
		
		try {
			nxtComm = NXTCommFactory.createNXTComm(NXTCommFactory.BLUETOOTH);
			do {
				append("Searching...");
				nxtInfos = nxtComm.search(null, NXTCommFactory.BLUETOOTH);
				append("Found " + nxtInfos.length + " NXTs");
				if (nxtInfos.length == 0) {
					append("Please switch your NXT on");
				}
			} while (nxtInfos.length == 0);
			nxtComm.open(nxtInfos[0],NXTComm.RAW);
			dis = new DataInputStream(nxtComm.getInputStream());
			dos = new DataOutputStream(nxtComm.getOutputStream());

		} catch (NXTCommException e) {
			append(e.getMessage());
		}		
	}
	
	public void sendDemoData(){
		int j = 0;
		int x = 0;
		for(int i=0;i<100;i++) {
			try {
				x = i*30000;
				append("Sending " + x + "\n");
				dos.writeInt(x);
				dos.flush();
			} catch (IOException ioe) {
				append("Write Exception");
			}
			
			try {
				j = dis.readInt();
				append("Read " + j + "\n");
			} catch (IOException ioe) {
				append("Read Exception");
			}
		}
		try {
			dis.close();
			dos.close();
			nxtComm.close();
		} catch (IOException ioe) {
			append("Close Exception");
		}
	}
}

package lejos.nxt.addon;

import lejos.nxt.I2CPort;
import lejos.nxt.I2CSensor;
import java.util.*;

/*
 * WARNING: THIS CLASS IS SHARED BETWEEN THE classes AND pccomms PROJECTS.
 * DO NOT EDIT THE VERSION IN pccomms AS IT WILL BE OVERWRITTEN WHEN THE PROJECT IS BUILT.
 */

/**
 * Supports for HiTechnic IRLink - see  http://www.hitechnic.com/.
 * 
 * @author Lawrie Griffiths
 *
 */
public class IRLink extends I2CSensor {
	
	//Registers
	private final static byte TX_BUFFER = 0x40; // 40 to 4C
	private final static byte TX_BUFFER_LEN = 0x4D;
	private final static byte TX_MODE = 0x4E;
	private final static byte TX_BUFFER_FLAG = 0x4F;
	
	private final static byte TX_MAX_BUFFER_LEN = 13;
	
	// IRLink transmission modes
	private final static byte TX_MODE_RCX = 0;
	private final static byte TX_MODE_TRAIN = 1;
	private final static byte TX_MODE_PF = 2;
	
	// PF Modes
	public final static byte PF_MODE_COMBO_DIRECT = 1;
	
	// IR PF signal encoding parameters
	private final byte MAX_BITS = TX_MAX_BUFFER_LEN * 8;
	private final byte STOP_START_PAUSE = 7;
	private final byte LOW_BIT_PAUSE = 2;
	private final byte HIGH_BIT_PAUSE = 4;
	
	// PF motor operations
	public static final byte PF_FLOAT = 0;
	public static final byte PF_FORWARD = 1;
	public static final byte PF_BACKWARD = 2;
	public static final byte PF_BRAKE = 3;
	
	private byte toggle = 0;
	
	private BitSet bits = new BitSet(MAX_BITS);
	private int nextBit = 0;

	public IRLink(I2CPort port) {
		super(port);
	}
	
	/**
	 * Send commands to both motors.
	 * Uses PF Combo direct mode.
	 * 
	 * @param channel the channel number (0-3)
	 * @param opA Motor A operation
	 * @param opB Motor B operation
	 */
	public void sendPFComboDirect(int channel, int opA, int opB) {
		sendPFCommand(channel, PF_MODE_COMBO_DIRECT, opB << 2 | opA);
	}

	private void sendPFCommand(int channel, int mode, int data) {
		byte nibble1 = (byte) ((toggle << 3) | channel);
		byte lrc = (byte) (0xF ^ nibble1 ^ mode ^ data);
		int pfData = (nibble1 << 12) | (mode << 8) | (data << 4) | lrc;

		clearBits();
		nextBit = 0;
		setBit(STOP_START_PAUSE); // Start
		for(int i=15;i>=0;i--) {
			setBit(((pfData >> i) & 1) == 0 ? LOW_BIT_PAUSE : HIGH_BIT_PAUSE);
		}
		setBit(STOP_START_PAUSE); // Stop
		toggle ^= 1;
		byte [] pfCommand = new byte[16];
		
		for(int i =0;i<MAX_BITS;i++) {
			boolean bit = bits.get(i);
			int byteIndex = i/8;
			int bitVal = (bit ? 1 : 0);
			pfCommand[byteIndex] |= (bitVal << (7 - i%8));
		}
		
		pfCommand[13] = TX_MAX_BUFFER_LEN;
	    pfCommand[14] = TX_MODE_PF;
	    pfCommand[15] = 1;

		sendData(TX_BUFFER, pfCommand, TX_MAX_BUFFER_LEN+3);
	}
	
	private void setBit(int pause) {
		bits.set(nextBit++);
		nextBit += pause;
	}
	
	private void clearBits() {
		for(int i=0;i<MAX_BITS;i++) bits.clear(i);
	}
}

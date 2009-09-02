import java.io.*;
import java.util.StringTokenizer;
import javax.bluetooth.BluetoothStateException;

import lejos.addon.keyboard.*;
import lejos.nxt.*;

/**
 * This is some sample code to demonstrate the Keyboard class if you have a Bluetooth 
 * SPP Keyboard {@link lejos.addon.keyboard#Keyboard}. A simple command line app that lets you execute files or enter
 * some common DOS commands like dir, mem, etc... 
 * It allows you to connect and display typing on the NXT LCD.
 * Only works with SPP Bluetooth keyboards. Will not work with
 * HID BT keyboards. See <code>Keyboard</code> Javadocs for more information.
 * @author BB
 */
/*
 * DEVELOPER NOTES
 * TODO: To run executables, it should ignore extensions (.nxj) same as DOS.
 * TODO: File.exec() - need one that passes arguments into main(String [] args)
 * TODO: It would be nice if the File.exec() command kept track of the chain of 
 * programs that were executed, rather than going back to the menu every time. 
 * That way after an exec is run it returns to CommandLine. 
 * TODO: Would be very cool to somehow get streams working with console commands, similar
 * to how Unix and DOS does it. 
 */

public class CommandLine implements KeyListener {
	
	private static final String LINUX_PROMPT = "root@nxj:";
	private static final String DOS_PROMPT = "C:" + (char)92 + ">";
	private static String prompt = DOS_PROMPT;
	
	/**
	 * Commands
	 */
	private static final String LS = "ls";
	private static final String DIR = "dir";
	private static final String EXIT = "exit";
	private static final String QUIT = "quit";
	private static final String HELP = "help";
	private static final String QUERIE = "?";
	private static final String DEL = "del";
	private static final String RM = "rm";
	private static final String MEM = "mem";
	private static final String DEFRAG = "defrag";
	private static final String LINUX = "linux";
	
	String [] commands = {LS, DIR, EXIT, QUIT, HELP, QUERIE, DEL, RM, MEM, DEFRAG, LINUX}; 
	
	private StringBuffer buf = new StringBuffer();
	
	public static void main(String [] args) {
		KeyListener kl = new CommandLine();
		System.out.println("NXJDOS 1.0");
		System.out.println("Connecting...");
		try {
			Keyboard k = new Keyboard();
			k.addKeyListener(kl);
			System.out.print(prompt);
		} catch(BluetoothStateException bt) {
			System.err.println(bt.getMessage());
		}
		
		Button.waitForPress();
	}
	
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_ENTER) {
			System.out.println(" ");
			// Get string from StringBuffer
			String inputString = buf.toString();
			if(inputString.length() <= 0) {
				System.out.print(prompt);
				return;
			}
			
			// Delete all chars in StringBuffer
			buf.delete(0, buf.length());
			
			// Parse out the command using ' ' with StringTokenizer
			StringTokenizer st = new StringTokenizer(inputString, " ");
			String command = st.nextToken();
			
			// Check for command line arguments 
			String [] args = new String[st.countTokens()];
			for(int i=0;i<args.length;i++) {
				args[i] = st.nextToken();
			}
			
			// Select from list: 
			if(command.equalsIgnoreCase(QUERIE)|command.equalsIgnoreCase(HELP)) {
				for(int i=0;i<commands.length;i++) {
					System.out.print(commands[i] + " ");
					System.out.println(" ");
				}
			} else if(command.equalsIgnoreCase(EXIT)|command.equalsIgnoreCase(QUIT)) {
				System.exit(0);
			} else if(command.equalsIgnoreCase(LINUX)) {
				prompt = LINUX_PROMPT;
			} else if(command.equalsIgnoreCase(DEFRAG)) {
				try {
					File.defrag();
					System.out.println("Defrag done.");
				} catch(IOException ioe) {
					System.out.println("Defrag error: " + ioe.getMessage());
				}
			} else if(command.equalsIgnoreCase(MEM)) {
				System.out.println(Runtime.getRuntime().freeMemory() + " free");
				System.out.println(Runtime.getRuntime().totalMemory() + " total");
				System.out.println(File.freeMemory() + " disk");
			} else if(command.equalsIgnoreCase(DEL)|command.equalsIgnoreCase(RM)) {
				File f = new File(args[0]);
				if(f.exists())  
					f.delete();
				else
					System.out.println(args[0] + " not exist");
				
			} else if(command.equalsIgnoreCase(DIR)|command.equalsIgnoreCase(LS)) {
				File [] files = File.listFiles();
				
				for(int i=0;i<files.length;i++) {
					System.out.println(files[i].getName() + " " + files[i].length());
				}
				System.out.println(File.totalFiles + " files");
				System.out.println(File.freeMemory() + " free");
			} else {	
				// 4.5 Check if it is a filename:
				File f = new File(command);
				if (f.exists()) {
					System.err.println(command + " exists");
					f.exec();
				} else {
					// Unrecognized command output error message
					System.err.println(command + " unrecognized");
				}
			}			
			System.out.print(prompt);
		}
	}
	
	public void keyReleased(KeyEvent e) {}
	
	public void keyTyped(KeyEvent e) {
		if(e.getKeyChar() == KeyEvent.VK_ENTER) return;
		if(e.getKeyChar() == KeyEvent.VK_BACK_SPACE) buf.delete(buf.length()-1, buf.length());
		buf.append(e.getKeyChar());
		System.out.print(e.getKeyChar());
	}
}
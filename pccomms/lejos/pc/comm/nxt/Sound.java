package lejos.pc.comm.nxt;

import lejos.pc.comm.*;
import java.io.*;

/**
 * Sound class.
 * Usage: SoundSensor.playTone(500, 1000);
 * @author <a href="mailto:bbagnall@mts.net">Brian Bagnall</a>
 * @version 0.1  10-August-2006 
 */
public class Sound {
	
	private static final NXTCommand nxtCommand = NXTCommand.getSingleton();
	
	// Make sure no one tries to instantiate this.
	private Sound() {}
	
	public static int playTone(int frequency, int duration) {
		try {
			return nxtCommand.playTone(frequency, duration);
		} catch (IOException ioe) {
			return 0;
		}
	}
	
	/**
	 * Plays a sound file from the NXT. SoundSensor files use the 
	 * .rso extension. The filename is not case sensitive.
	 * Filenames on the NXT Bricks display do now show the filename extension.
	 * @param fileName e.g. "Woops.rso"
	 * @param repeat true = repeat, false = play once.
	 * @return If you receive a non-zero number, the filename is probably wrong
	 * or the file is not uploaded to the NXT brick.
	 */
	public static byte playSoundFile(String fileName, boolean repeat) {
		try {
			return nxtCommand.playSoundFile(fileName, repeat);
		} catch (IOException ioe) {
			return 0;
		}
	}
	
	/**
	 * Plays a sound file once from the NXT. SoundSensor files use the 
	 * .rso extension. The filename is not case sensitive.
	 * Filenames on the NXT Bricks display do now show the filename extension.
	 * @param fileName e.g. "Woops.rso"
	 * @return If you receive a non-zero number, the filename is probably wrong
	 * or the file is not uploaded to the NXT brick.
	 */
	public static byte playSoundFile(String fileName) {
		return Sound.playSoundFile(fileName, false);
	}
		
	/**
	 * Stops a sound file that has been playing/repeating.
	 * @return Error code.
	 */
	public static int stopSoundPlayback() {
		try {
			return nxtCommand.stopSoundPlayback();
		} catch (IOException ioe) {
			return 0;
		}
	}
}

import lejos.nxt.*;
import java.io.*;

/**
 * Simple example to demonstrate playing 8-bit WAV files.
 * 
 * Use nxjbrowse to upload the wav file. On Windows XP,
 * rining.wav can be found in the Media subfolder of
 * the Windows folder.
 * 
 * @author Lawrie Griffiths
 *
 */
public class SoundSample {
	public static void main(String [] options) throws Exception {
		File f = new File("ringin.wav");
		Sound.playSample(f,10000,500);
		Thread.sleep(2000);
	}

}

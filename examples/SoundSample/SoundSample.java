import lejos.nxt.*;
import java.io.*;


public class SoundSample {
	public static void main(String [] options) throws Exception {
		File f = new File("ringin.wav");
		Sound.playSample(f,1500,1000);
		Thread.sleep(2000);
	}

}

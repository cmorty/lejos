
import lejos.nxt.*;
public class SoundTest {

	public static void main(String[] args) throws Exception
	{
		for(int i = 3; i<20; i++) {
			Sound.playTone(i*100,1000);
			Thread.sleep(1000);
		}
		Button.ESCAPE.waitForPressAndRelease();
	}
}

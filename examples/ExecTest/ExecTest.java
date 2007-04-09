
import lejos.nxt.*;
public class ExecTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		LCD.drawString("About to exec", 0, 0);
		LCD.refresh();
		Button.ENTER.waitForPressAndRelease();
		LCD.clear();
		LCD.refresh();
		Flash.exec(0,12000);

	}

}

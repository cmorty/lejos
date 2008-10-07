import lejos.nxt.*;

/**
 * @author juanantonio.breña
 *
 */
public class SimpleTest {

	public static void main(String[] args) throws Exception{
		OneTest ot = new OneTest("Simple");
		//DoubleTest dt = new DoubleTest("Double");
		try{
			ot.run();
			//dt.run();
		}catch(Throwable  e){
			LCD.drawString("E", 0, 0);
		}
		LCD.drawString("Test finished", 0, 0);
		try {Thread.sleep(5000);} catch (Exception e2) {}
	}
}

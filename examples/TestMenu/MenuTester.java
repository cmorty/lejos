
package lejos.nxt;
import lejos.nxt.LCD;
import lejos.nxt.Button;

public class MenuTester 
{
		public static void main(String[] args)
	{

		String [] items = new String[12];
		items[0] = "zero";
		items[1] =  "one";
		items[2] = "two";
		items[3] = "three";
		items[4] = "four";
		items[5] =  "five";
		items[6] = "six";
		items[7] = "seven";
		items[8] = "eight";
		items[9] =  "nine";
		items[10] = "ten";
		items[11] = "eleven";

//		SimpleMenu menu = new SimpleMenu(items);
//		SimpleMenu menu = new SimpleMenu(items, 1,4);
		SimpleMenu menu = new SimpleMenu(items, 7,"Start Menu");
		
//		int sel = 	menu.selectFrom(_items);
		int sel = menu.select();
		LCD.clear();
		LCD.drawInt(sel,6,0);
		LCD.refresh();

		while(Button.readButtons()== 0);
	}

	
	
}

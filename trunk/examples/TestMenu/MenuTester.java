
import lejos.nxt.*;

public class MenuTester 
{
	public static void main(String[] args)
	{

		String [] items = {
		  "zero", "one","two","three","four", "five",
		  "six","seven","eight","nine","ten","eleven"};

		TextMenu menu = new TextMenu(items, 7,"Start Menu");

		int sel = menu.select();
		LCD.clear();
		LCD.drawInt(sel,2,6,0);
		LCD.refresh();

		while(Button.readButtons()== 0);
	}

}

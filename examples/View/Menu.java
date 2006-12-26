import lejos.nxt.*;

public class Menu 
{
	String name;
	String options[];
	int numOptions, current;
	final static String star = "*";
	final static String blank = " ";
	
	public Menu(String name) 
	{
		this.name = name;
		options = new String[6];
		numOptions = 0;
		current = 0;		
	}

	public void add(String option)
	{
		if (numOptions < 6)
		{
			options[numOptions++] = option;
		}
	}
	
	public int process()
	{
		int b;
		
		LCD.clear();
		
		LCD.drawString(name,0,0);
		
		for(int i = 0;i<numOptions;i++)
		{
			LCD.drawString(options[i],2,i+2);
		}
		
		LCD.drawString(star,0,current+2);
		
		LCD.refresh();
		
		for(;;)
		{
			b = Button.readButtons();
			
			if ((b & 2) != 0) // Left
			{
				LCD.drawString(blank,0,current+2);
				if (--current < 0) current = numOptions-1;
				LCD.drawString(star, 0, current+2);
			}
			
			if ((b & 4) != 0) // Right
			{
				LCD.drawString(blank,0,current+2);
				if (++current >= numOptions) current = 0;
				LCD.drawString(star, 0, current+2);
			}

			if ((b & 1) != 0) return current; //Enter
			
			if ((b & 8) != 0) return -1;  // Escape
			
			LCD.refresh();
			
			try
			{
				Thread.sleep(300);
			}
			catch (InterruptedException ie)
			{				
			}
		}
	
	}
	
	public String getValue()
	{
		return options[current];
	}
}

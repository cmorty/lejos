import lejos.nxt.LCD;

/**
 * Debug Monitor is a useful class designed to help
 * leJOS NXJ Developers in debugging tasks. 
 * 
 * @author Juan Antonio Brenha Moral
 *
 */

public class DebugMonitor {
	private int LINE_COUNTER = 0;
	private int STEP = 0;
	private final int MAXIMUM_LCD_LINES = 7;
	private int LCD_LINES = 7;
	private boolean show_step = false;
	private int DELAY_MS = 250;
	private boolean delayEnabled = false;
	
	//Constructors
	public DebugMonitor(){

	}

	/**
	 * Another constructor to set Line which start to 
	 * show Debug Data
	 * 
	 * @param INIT
	 * @author JAB
	 */
	public DebugMonitor(int INIT){
		LINE_COUNTER = INIT;
	}
	
	/**
	 * Set the delay measured in MS.
	 * 
	 * @param DELAY_MS
	 * @author JAB
	 */	
	public void setDelay(int DELAY_MS){
		this.DELAY_MS = DELAY_MS;
	}
	
	/**
	 * Show in NXT Screen a message
	 * 
	 * @param message
	 * @author JAB
	 */
	public void echo(String message) throws Exception{
		if(LINE_COUNTER > this.LCD_LINES){
			LINE_COUNTER = 0;
			LCD.clear();			
		}else{
			LCD.drawString(message, 0, LINE_COUNTER);
			LCD.refresh();
			LINE_COUNTER++;
		}
		if(delayEnabled){
			Thread.sleep(this.DELAY_MS);
		}		
	}
	
	/**
	 * Show in NXT Screen a message
	 * 
	 * @param message
	 * @author JAB
	 */	
	public void echo(int message) throws Exception{
		if(LINE_COUNTER > this.LCD_LINES){
			LINE_COUNTER = 0;
			LCD.clear();
		}else{
			LCD.drawInt(message, 0, LINE_COUNTER);
			LCD.refresh();			
			LINE_COUNTER++;
		}
		if(delayEnabled){
			Thread.sleep(this.DELAY_MS);
		}		
	}
	
	public void setLCDLines(int lines){
		this.LCD_LINES = lines;
	}
	
	public void setDelayEnabled(boolean delayEnabled){
		this.delayEnabled = delayEnabled;
	}
}

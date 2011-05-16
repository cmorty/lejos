

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Gauge;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemCommandListener;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.Spacer;
import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;
import javax.microedition.lcdui.Ticker;
import javax.microedition.lcdui.Font;
import lejos.util.Timer;
import lejos.util.TimerListener;
import lejos.nxt.LCD;

/**
 * 
 * @author Andre Nijholt
 */
class Splasher extends Alert
{
	Image img;
	boolean startUp;
	String text;
	
	Splasher(Image img, String text, boolean start)
	{
		super("");
		this.img = img;
		this.text = text;
		setStart(start);
	}
	
	public void setStart(boolean start)
	{
		startUp = start;
	}
	
	protected void showNotify() {
		// Start painting alert screen
		repaint();
	}
	
	public void paint(Graphics g)
	{
        g.setFont(Font.getFont(0, 0, Font.SIZE_LARGE));
		int iw = img.getWidth();
		int ih = img.getHeight();
		int dx = (Display.SCREEN_WIDTH - iw)/2;
		int dy = (Display.SCREEN_HEIGHT - ih)/2 - 10;
		int tdy = dy + ih + 5;
		int tw = g.getFont().stringWidth(text);
        int tdx = (Display.SCREEN_WIDTH - tw)/2;
		int th = g.getFont().getHeight();
		if (startUp)
		{
			for(int i = 32; i >=0; i--)
			{
				LCD.clear();
				g.drawRegionRop(img, 0, 0, iw, ih, dx + i, dy, 0, 0xaaffaa00);
				g.drawRegionRop(img, 0, 0, iw, ih, dx - i, dy, 0, 0x55ff5500);
				LCD.refresh();
				try{Thread.sleep(50);}catch(Exception e){}
			}
			int old = g.getColor();
			g.setColor(Graphics.WHITE);
			for(int i = (Display.SCREEN_HEIGHT - tdy); i >= 0; i--)
			{
				g.fillRect(tdx, tdy + i + th + 1, tw, th);
				g.drawString(text, tdx, tdy + i, 0, true);
				LCD.refresh();
				try{Thread.sleep(50);}catch(Exception e){}
			}
			g.setColor(old);
		}
		else
		{
			int old = g.getColor();
			g.setColor(Graphics.WHITE);
			g.drawRegionRop(img, 0, 0, iw, ih, dx, dy, 0, LCD.ROP_COPY);
			for(int i = 0; i < (Display.SCREEN_HEIGHT - tdy); i++)
			{
				g.fillRect(tdx, tdy + i - 1, tw, th);
				g.drawString(text, tdx, tdy + i, 0, true);
				LCD.refresh();
				try{Thread.sleep(50);}catch(Exception e){}
			}
			g.setColor(old);
			for(int i = 0; i <= iw/2; i++)
			{
				LCD.clear();
				g.drawRegionRop(img, i, i, iw - 2*i, ih - 2*i, dx + i, dy + i, 0, LCD.ROP_COPY);
				LCD.refresh();
				try{Thread.sleep(50);}catch(Exception e){}
			}
		}
        g.setFont(Font.getDefaultFont());
	}
	
}

public class LCDUI implements CommandListener {
	private static final int CMDID_BACK_TO_MAIN 	= 1;
	private static final int CMDID_EXIT_APP 		= 2;

	private static final Command BACK_COMMAND = new Command(CMDID_BACK_TO_MAIN, Command.BACK, 0);
    private static final Command EXIT_COMMAND = new Command(CMDID_EXIT_APP, Command.STOP, 2);

	private List 	menu 		= new List("Test Components", Choice.IMPLICIT);
	private Ticker 	ticker 		= new Ticker("Test GUI Components");
	
	// Main menu items
	private TextBox input 		= new TextBox("Enter Some Text:", "", 16, TextField.ANY);
	private List 	choose 		= new List("Choose Items", Choice.MULTIPLE);
	private Alert 	soundAlert 	= new Alert("Sound Alert");
	private Form 	form1 		= new Form("Testing form");
	private Form 	form2 		= new Form("Form for Stuff");
	private Alert 	exitAlert 	= new Alert("Exit");
	
	// Gauge on soundAlert
	private Gauge	alertGauge  = new Gauge(null, false, 20, 0);
	private Timer 	gaugeTimer	= new Timer(100, new TimerListener() {
		public void timedOut() {
			int curValue = alertGauge.getValue();
			if (curValue >= alertGauge.getMaxValue()) {
				gaugeTimer.stop();
				alertGauge.setValue(0);
			} else {
				alertGauge.setValue(curValue + 1);
			}
			soundAlert.repaint();
		}
	});
	
	// Items on form1
	private ChoiceGroup choiceGroup1 = new ChoiceGroup("Popup 1", Choice.POPUP);
	private ChoiceGroup choiceGroup2 = new ChoiceGroup("Popup 2", Choice.POPUP);
	private ChoiceGroup radioButtons = new ChoiceGroup(null, Choice.EXCLUSIVE);
	private Image img = new Image(32, 32, new byte[] {
			(byte) 0xff, (byte) 0x03, (byte) 0x05, (byte) 0x09, (byte) 0x11, (byte) 0x21, (byte) 0x41, (byte) 0x81,
			(byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x01,
			(byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x01,
			(byte) 0x81, (byte) 0x41, (byte) 0x21, (byte) 0x11, (byte) 0x09, (byte) 0x05, (byte) 0x03, (byte) 0xff,
			(byte) 0xff, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x01, (byte) 0x02, (byte) 0x04, (byte) 0x08, (byte) 0x10, (byte) 0x20, (byte) 0x40, (byte) 0x80,
			(byte) 0x80, (byte) 0x40, (byte) 0x20, (byte) 0x10, (byte) 0x08, (byte) 0x04, (byte) 0x02, (byte) 0x01,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xff,
			(byte) 0xff, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x80, (byte) 0x40, (byte) 0x20, (byte) 0x10, (byte) 0x08, (byte) 0x04, (byte) 0x02, (byte) 0x01,
			(byte) 0x01, (byte) 0x02, (byte) 0x04, (byte) 0x08, (byte) 0x10, (byte) 0x20, (byte) 0x40, (byte) 0x80,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xff,
			(byte) 0xff, (byte) 0xc0, (byte) 0xa0, (byte) 0x90, (byte) 0x88, (byte) 0x84, (byte) 0x82, (byte) 0x81,
			(byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x80,
			(byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x80, (byte) 0x80,
			(byte) 0x81, (byte) 0x82, (byte) 0x84, (byte) 0x88, (byte) 0x90, (byte) 0xa0, (byte) 0xc0, (byte) 0xff
	});
	private Image splash = new Image(26, 32, new byte[] {
			(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
			(byte)0x00, (byte)0x3F, (byte)0x3F, (byte)0x3F, (byte)0x3F, (byte)0x3F, (byte)0x3F, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFE, (byte)0xFC,
			(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0xA0, (byte)0x40, (byte)0xA0, (byte)0x40,
			(byte)0xA0, (byte)0x40, (byte)0xA0, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF,
			(byte)0xF0, (byte)0xF0, (byte)0xF0, (byte)0xF0, (byte)0xF0, (byte)0xF0, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x0A, (byte)0x05, (byte)0x0A, (byte)0x05,
			(byte)0x0A, (byte)0x05, (byte)0x0A, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF,
			(byte)0x0F, (byte)0x1F, (byte)0x3F, (byte)0x3F, (byte)0x7F, (byte)0x7F, (byte)0xFC, (byte)0xF8, (byte)0xF0, (byte)0xF0, (byte)0xF0, (byte)0xF0, (byte)0xF0,
			(byte)0xF0, (byte)0xF0, (byte)0xF0, (byte)0xF0, (byte)0xF0, (byte)0xF8, (byte)0xFC, (byte)0x7F, (byte)0x7F, (byte)0x3F, (byte)0x3F, (byte)0x1F, (byte)0x0F,});
	private Splasher startUp		= new Splasher(splash, "leJOS", true);

    // Items on form2
    private Gauge 	volGauge 	= new Gauge("Volume: ", true, 8, 6);
    private Gauge 	gauge 		= new Gauge("Progress Bar", true, 20, 9);
    TextField 		textfield 	= new TextField("TextField label", "abc", 16, TextField.ANY);
    
	
    private Display display;

	public LCDUI() {}
	
	/**
	 * Start application
	 * 
	 * @param polling Select method for button handling
	 */
	public void startApp(boolean polling) {
		// Create main menu
		menu = new List("Test Components", Choice.IMPLICIT);
		menu.append("Test TextBox", null);
		menu.append("Test List", null);
	    menu.append("Test Alert", null);
	    menu.append("Test Form 1", null);
	    menu.append("Test Form 2", null);
	    //menu.setSelectedIndex(0, true);
	    menu.addCommand(EXIT_COMMAND);
	    menu.setCommandListener(this);
	    menu.setTicker(ticker);

	    // Set textbox properties
//		input.setTicker(new Ticker("Testing TextBox")); Very slow!
    	input.addCommand(BACK_COMMAND);
    	input.setCommandListener(this);

	    // Set list properties and fill list
//    	choose.setTicker(new Ticker("Testing List"));  Very slow!
        choose.addCommand(BACK_COMMAND);
        choose.setCommandListener(this);
        choose.append("Item 1", null);
        choose.append("Item 2", null);
        choose.append("Item 3", null);
        choose.append("Item 4", null);
        choose.append("Item 5", null);
        choose.append("Item 6", null);
        choose.append("Item 7", null);
        choose.append("Item 8", null);
        choose.append("Item 9", null);
        choose.append("Item 10", null);

        // Set alert properties
		soundAlert.setType(Alert.ALERT_TYPE_ERROR);
	    soundAlert.setTimeout(5000);
		soundAlert.setString("** ERROR **");
		soundAlert.setIndicator(alertGauge);

	    // Create form1 and set command listener
	    form1.append(choiceGroup1);
	    form1.append(choiceGroup2);
	    form1.append("Left");
	    form1.append(img);
	    form1.append(new Spacer(8, 8));
	    form1.append("Right");
	    form1.append(radioButtons);
		form1.addCommand(BACK_COMMAND);
	    form1.setCommandListener(this);
	    
	    // Fill popup menus and radiobox
	    choiceGroup1.append("Menu 1", null);
	    choiceGroup1.append("Menu 2", null);
	    choiceGroup1.append("Menu 3", null);
	    choiceGroup1.append("Menu 4", null);
	    choiceGroup1.append("Menu 5", null);
	    choiceGroup1.append("Menu 6", null);
	    choiceGroup1.append("Menu 7", null);
	    choiceGroup1.append("Menu 8", null);
	    choiceGroup1.append("Menu 9", null);
	    choiceGroup1.append("Menu 10", null);
	    
	    choiceGroup2.append("Select 1", null);
	    choiceGroup2.append("Select 2", null);
	    choiceGroup2.append("Select 3", null);
	    choiceGroup2.append("Select 4", null);
	    choiceGroup2.setScrollWrap(false);
	    choiceGroup2.setItemCommandListener(new ItemCommandListener() {
	    	public void commandAction(Command c, Item d) {
	    		radioButtons.setSelectedIndex(choiceGroup2.getSelectedIndex() % 2, true);
	    	}
	    });
	    choiceGroup2.addCommand(new Command(1, Command.SCREEN, 0));
	    
	    radioButtons.append("Selection 1", null);
	    radioButtons.append("Selection 2", null);
	    radioButtons.setSelectedIndex(0, true);
	    radioButtons.setPreferredSize(Display.SCREEN_WIDTH, 2 * Display.CHAR_HEIGHT);

	    // Create form2 and set command listener
	    form2.append(volGauge);
	    form2.append(gauge);
	    form2.append(textfield);
		form2.addCommand(BACK_COMMAND);
	    form2.setCommandListener(this);
	    
		// Make the system active
		display = Display.getDisplay();
	    display.setCurrent(menu);
		// Show the splash screen
	    startUp.setTimeout(4000);
		display.setCurrent(startUp);
	    display.show(polling);
	}
	
	/**
	 * Handle events.
	 */  
	public void commandAction(Command c, Displayable d) {
		if (c.getCommandId() == CMDID_BACK_TO_MAIN) {
			// Display main menu again
			display.setCurrent(menu);
		} else if (c.getCommandId() == CMDID_EXIT_APP) {
			// Request to exit application
			exitAlert.setType(Alert.ALERT_TYPE_CONFIRMATION);
			exitAlert.setString("Exit Lejos?");
			exitAlert.setCommandListener(this);
			display.setCurrent(exitAlert);
		} else {
			// Handle system commands
			if (d == exitAlert) {
				if (exitAlert.getConfirmation()) {
					startUp.setStart(false);
					display.setCurrent(startUp);
					display.quit();
				} else {
					display.setCurrent(menu);
				}
			} else if (d == menu) {
				List list = (List) display.getCurrent();
				if (list.getSelectedIndex() == 0) {
					display.setCurrent(input);
				} else if (list.getSelectedIndex() == 1) {
					display.setCurrent(choose);
				} else if (list.getSelectedIndex() == 2) {
					display.setCurrent(soundAlert);
					alertGauge.setValue(0);
					gaugeTimer.start();
				} else if (list.getSelectedIndex() == 3) {
					display.setCurrent(form1);
				} else if (list.getSelectedIndex() == 4) {
					display.setCurrent(form2);
				}
			}
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new LCDUI().startApp(true);
	}

}

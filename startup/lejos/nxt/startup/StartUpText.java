package lejos.nxt.startup;

import java.io.File;
import java.util.ArrayList;

import javax.bluetooth.RemoteDevice;

import lejos.nxt.Battery;
import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.NXT;
import lejos.nxt.SensorPort;
import lejos.nxt.Settings;
import lejos.nxt.Sound;
import lejos.nxt.SystemSettings;
import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.LCP;
import lejos.nxt.comm.LCPResponder;
import lejos.nxt.comm.NXTCommConnector;
import lejos.nxt.comm.NXTCommDevice;
import lejos.nxt.comm.USB;
import lejos.util.TextMenu;

/**
 * This class provides the leJOS system menu.
 * The code in this class is held in resrved part of flash memory on the NXT.
 * When the firmware starts to run it loads and executes this program. The
 * system menu provides a number of functions, many can be accessed by the
 * simple text based UI.
 * 1. Access to the leJOS file system. Allowing programs to be run, deleted etc.
 * 2. Control over the NXT Bluetooth system. Pairing, power on off etc.
 * 3. Setting of sound volume levels for playbac, keyclick etc.
 * 4. System settings, format the file system, set startup program etc.
 * 5. Display system version information.
 * In addition the menu runs a number of background threads, that allow for
 * remote access and monitor battery state etc.
 * @author the leJOS team.
 */
public class StartUpText
{
    static final String defaultProgramProperty = "lejos.default_program";
    static final String defaultProgramAutoRunProperty = "lejos.default_autoRun";
    static final String sleepTimeProperty = "lejos.sleep_time";
    static final String pinProperty = "lejos.bluetooth_pin";
    static final int defaultSleepTime = 2;
    static final int maxSleepTime = 10;
    
    static final String REVISION = "$Revision$";
    static final int VERSION = 0x000900;

    private int timeout;
    private boolean btPowerOn;
    private boolean btVisibility;
    IndicatorThread ind = new IndicatorThread();
    BatteryIndicator indiBA = new BatteryIndicator();
    IconIndicator indiUSB = new IconIndicator(Config.ICON_USB_POS, Config.ICON_DISABLE_X, Config.ICON_USB_WIDTH);
    IconIndicator indiBT = new IconIndicator(Config.ICON_BT_POS, Config.ICON_DISABLE_X, Config.ICON_BT_WIDTH);
    private Responder usb = new Responder(USB.getConnector(), indiUSB);
    private Responder bt = new Responder(Bluetooth.getConnector(), indiBT);
    private TextMenu curMenu = null;
    
	static class TuneThread extends Thread
	{
        int stState = 0;
    	
		@Override
		public void run()
		{
			Utils.fadeIn();
			this.waitState(1);
			playTune();
			// Tell others, that tune is complete
			this.setState(2);
            // Wait for init to complete
            this.waitState(3);
            // Fade in
            Utils.fadeIn();
		}
		
        public synchronized void setState(int s)
        {
        	this.stState = s;
        	this.notifyAll();
        }
        
        public synchronized void waitState(int s)
        {
        	while (this.stState < s)
        	{
        		try
        		{
        			this.wait();
        		}
        		catch (InterruptedException e)
        		{
        			// nothing
        		}
        	}
        }
	}
    
    static class InitThread extends Thread
    {
    	StartUpText menu;
    	
        /**
         * Startup the menu system.
         * Play the greeting tune.
         * Run the default program if auto-run is requested.
         * Initialize I/O etc.
         */            
        @Override
        public void run()
        {
            setAddress();            
        	menu = new StartUpText();        	
        }
    }
    
    /**
     * Manage the top line of the display.
     * The top line of the display shows battery state, menu titles, and I/O
     * activity.
     */
	class IndicatorThread extends Thread
    {
		public IndicatorThread()
    	{
    		super();
            setDaemon(true);
    	}
    	
    	@Override
		public synchronized void run()
    	{
    		try
    		{
	    		while (true)
	    		{
	    			long time = System.currentTimeMillis();
	    			int x = (USB.usbStatus() & USB.USB_CONFIGURED) != 0 ? Config.ICON_USB_X : Config.ICON_DISABLE_X;
	    			indiUSB.setIconX(x);
	    			
	    			byte[] buf = LCD.getDisplay();
	    			// clear not necessary, pixels are always overwritten
	    			//for (int i=0; i<LCD.SCREEN_WIDTH; i++)
	    			//	buf[i] = 0;	    			
	    			indiBA.draw(time, buf);
	    			indiUSB.draw(time, buf);
	    			indiBT.draw(time, buf);
	    			LCD.asyncRefresh();
    			
    				// wait until next tick
    				time = System.currentTimeMillis();
    				this.wait(Config.ANIM_DELAY - (time % Config.ANIM_DELAY));
    			}
    		}
    		catch (InterruptedException e)
    		{
    			//just terminate
    		}
    	}
    	
    	public synchronized void updateNow()
    	{
    		this.notifyAll();
    	}
    }
    
    
    /**
     * Class to handle commands from USB/Bluetooth connections.
     * @author andy
     */
    class Responder extends LCPResponder
    {
    	IconIndicator indi;

        /**
         * Create an object for the required connection type.
         * @param con Connector object for the underlying protocol.
         */
        public Responder(NXTCommConnector con, IconIndicator indi)
        {
            super(con);            
            setDaemon(true);
            
            this.indi = indi; 
        }

        /**
         * We over-ride the pre command stage of processing to provide activity
         * indication.
         * @param inMsg
         * @param len
         * @return
         */
        @Override
        protected int preCommand(byte[] inMsg, int len)
        {
            if (len > 0)
            {
            	this.indi.pulse();
            	resetMenuTimeout();
            }
            return super.preCommand(inMsg, len);
        }

        /**
         * We over-ride the post command processing to allow usDelay to do menu
         * specific processing of some commands.
         * @param inMsg
         * @param inLen
         * @param replyMsg
         * @param replyLen
         */
        @Override
        protected void postCommand(byte[] inMsg, int inLen, byte[] replyMsg, int replyLen)
        {
			switch (inMsg[1])
			{
				case LCP.DELETE:
					Utils.defragFilesystem();
				case LCP.CLOSE:
					Sound.beepSequenceUp();
					redisplayMenu();
					break;
				case LCP.SET_BRICK_NAME:
					indiBA.setDefaultTitle(Bluetooth.getFriendlyName());
					ind.updateNow();
					break;
				case LCP.BOOT:
	                // Reboot into firmware update mode. Only supported
	                NXT.boot();	                
			}        	
            super.postCommand(inMsg, inLen, replyMsg, replyLen);
        }
    }
    
    public StartUpText()
    {
    	timeout = SystemSettings.getIntSetting(sleepTimeProperty, defaultSleepTime);
        btPowerOn = setBluetoothPower(Bluetooth.getStatus() == 0);
    	btVisibility = (Bluetooth.getVisibility() == 1);
    	
        indiBA.setDefaultTitle(Bluetooth.getFriendlyName());
        updateBTIcon();
    }

    /**
     * Play the leJOS startup tune.
     */
    static void playTune()
    {
        int[] freq = { 523, 784, 659 };
        for (int i = 0; i < 3; i++)
            Sound.playNote(Sound.XYLOPHONE, freq[i], 300);
        Sound.pause(300);
    }

    /**
     * Start a new screen display.
     * Clear the screen and set the screen title.
     * @param title
     */
    private void newScreen(String title)
    {
        indiBA.setTitle(title);
        newScreen();
    }

    /**
     * Start a new screen display using the current title.
     */
    private void newScreen()
    {
        LCD.clear();
        ind.updateNow();
    }

    /**
     * Turn Bluetooth power on/off.
     * Record this new setting in the status bytes held by the Bluetooth module.
     * @param powerOn
     * @return The new power state.
     */
    static boolean setBluetoothPower(boolean powerOn)
    {
        // Set the state of the Bluetooth power to be powerOn. Also record the
        // current state of this in the BT status bytes.                                                      
        // If power is not on we need it on to check things
        if (!Bluetooth.getPower())
            Bluetooth.setPower(true);
        // First update the status bytes if needed
        int status = Bluetooth.getStatus();
        if (powerOn != (status == 0))
            Bluetooth.setStatus((powerOn ? 0 : 1));
        Bluetooth.setPower(powerOn);
        return powerOn;
    }

    /**
     * Format a string for use when displaying the volume.
     * @param vol Volume setting 0-10
     * @return String version.
     */
    private static String formatVol(int vol)
    {
        if (vol == 0)
            return "mute";
        if (vol == 10)
            return "10";        
        return new StringBuilder().append(' ').append(vol).toString();
    }

    /**
     * Run the default program (if set).
     */
    private void mainRunDefault()
    {
    	File f = getDefaultProgram();
        if (f == null)
        {
       		msg("No default set");
        }
        else
        {
            f.exec();
        }
    }

    /**
     * Read a button press.
     * If the read timesout then exit the system.
     * @return The bitcode of the button.
     */
    private int getButtonPress()
    {
        int value = Button.waitForAnyPress(timeout*60000);
        if (value == 0)
            NXT.shutDown();
        return value;
    }
    
    /**
     * Clears the screen, displays a number and allows user to change
     * the digits of the number individually using the NXT buttons.
     * Note the array of bytes represent ASCII characters, not actual numbers.
     * 
     * @param digits Number of digits in the PIN.
     * @param title The text to display above the numbers.
     * @param defaultNumber Start with a default PIN. Array of bytes up to 8 length.
     * @return
     */
    private boolean enterNumber(String title, byte[] number, int digits)
    {
        // !! Should probably check to make sure defaultNumber is digits in size
        int curDigit = 0;

        while (true)
        {
            newScreen();
            LCD.drawString(title, 0, 2);
            for (int i = 0; i < digits; i++)
                LCD.drawChar((char)number[i], i * 2 + 1, 4);
            
            if (curDigit >= digits)
            	return true;
            
            Utils.drawRect(curDigit * 12 + 3, 30, 10, 10);

            int ret = getButtonPress();
            switch (ret)
            {
                case Button.ID_ENTER:
                { // ENTER
                    curDigit++;
                    break;
                }
                case Button.ID_LEFT:
                { // LEFT
                    number[curDigit]--;
                    if (number[curDigit] < '0')
                        number[curDigit] = '9';
                    break;
                }
                case Button.ID_RIGHT:
                { // RIGHT
                    number[curDigit]++;
                    if (number[curDigit] > '9')
                        number[curDigit] = '0';
                    break;
                }
                case Button.ID_ESCAPE:
                { // ESCAPE
                    curDigit--;
                    // Return false if user backs out
                    if (curDigit < 0)
                        return false;
                    break;
                }
            }
        }
    }

    /**
     * Set the address of the NXT.
     * Ensure that we are using the same name for Bluetooth and USB access to
     * the NXT. The USB (and RS485) address is stored in flash memory.
     */
    static void setAddress()
    {
        // Ensure the USB address property is set correctly. We use the
        // Bluetooth address as our serial number.
        String addr = Bluetooth.getLocalAddress();
        if (!addr.equals(NXTCommDevice.getAddress()))
        {
            Settings.setProperty(NXTCommDevice.SERIAL_NO, addr);
            NXTCommDevice.setAddress(addr);
        }
        String name = Bluetooth.getFriendlyName();
        if (!name.equals(NXTCommDevice.getName()))
        {
            Settings.setProperty(NXTCommDevice.NAME, name);
            NXTCommDevice.setName(name);
        }
    }

    /**
     * Obtain a menu item selection
     * Allow the user to make a selection from the specified menu item. If a
     * power off timeout has been specified and no choice is made within this
     * time power off the NXT.
     * @param menu Menu to display.
     * @param cur Initial item to select.
     * @return Selected item or < 0 for escape etc.
     */
    private int getSelection(TextMenu menu, int cur)
    {
    	this.setCurMenu(menu);
    	
        int selection;
        // If the menu is interrupted by another thread, redisplay
        do {
            selection = menu.select(cur, timeout*60000);
        } while (selection == -2);
        
        if (selection == -3)
            NXT.shutDown();
        
    	this.setCurMenu(menu);
    	
        return selection;
    }
    
    synchronized void setCurMenu(TextMenu menu)
    {
    	this.curMenu = menu;
    }
    
    synchronized void redisplayMenu()
    {
		if (curMenu != null)
		{
			curMenu.quit();
		}
    }
    
    synchronized void resetMenuTimeout()
    {
        if (curMenu != null)
        {
            curMenu.resetTimeout();
        }
    }

    void updateBTIcon()
    {
    	int x;
    	if (btPowerOn)
	    	if (btVisibility) 
	    		x = Config.ICON_BT_VISIBLE_X;
	    	else
	    		x = Config.ICON_BT_HIDDEN_X;
    	else
    		x = Config.ICON_DISABLE_X;
    	
    	indiBT.setIconX(x);
    }
    
    /**
     * Display a status message
     * @param msg
     */
    private void msg(String msg)
    {
        newScreen();
        LCD.drawString(msg, 0, 2);
        long start = System.currentTimeMillis();
        int button;
        int buttons = Button.readButtons();
        do
        {
        	Thread.yield();
        	
        	int buttons2 = Button.readButtons();
        	button = buttons2 & ~buttons;
        } while (button != Button.ID_ESCAPE && System.currentTimeMillis() - start < 2000);
    }

    /**
     * Perform the Bluetooth search operation
     * Search for Bluetooth devices
     * Present those that are found
     * Allow pairing
     */
    private void bluetoothSearch()
    {
        newScreen("Searching");
        ArrayList<RemoteDevice> devList; 
        indiBT.incCount();
        try
        {
        	// 0 means "search for all"
	        devList = Bluetooth.inquire(5, 10, 0);
        }
	    finally
	    {
	    	indiBT.decCount();
	    }
        if (devList.size() <= 0)
            msg("No devices found");
        else
        {
            String[] names = new String[devList.size()];
            for (int i = 0; i < devList.size(); i++)
            {
                RemoteDevice btrd = devList.get(i);
                names[i] = btrd.getFriendlyName(false);
            }
            TextMenu searchMenu = new TextMenu(names, 1);
            TextMenu subMenu = new TextMenu(new String[]{"Pair"}, 4);
            int selected = 0;
            do
            {
                newScreen("Found");
                selected = getSelection(searchMenu, selected);
                if (selected >= 0)
                {
                    RemoteDevice btrd = devList.get(selected);
                    newScreen();
                    LCD.drawString(names[selected], 0, 1);
                    LCD.drawString(btrd.getBluetoothAddress(), 0, 2);
                    int subSelection = getSelection(subMenu, 0);
                    if (subSelection == 0)
                    {
                        newScreen("Pairing");
                        Bluetooth.addDevice(btrd);
                        // !! Assuming 4 length
                        byte[] pin = { '0', '0', '0', '0' };
                        if (!enterNumber("PIN for " + btrd.getFriendlyName(false), pin, pin.length))
                        	break;
                        LCD.drawString("Please wait...", 0, 6);
                        BTConnection connection = Bluetooth.connect(btrd.getDeviceAddr(), 0, pin);
                        // Indicate Success or failure:
                        if (connection != null)
                        {
                            LCD.drawString("Paired!      ", 0, 6);
                            connection.close();
                        }
                        else
                        {
                            LCD.drawString("UNSUCCESSFUL  ", 0, 6);
                            Bluetooth.removeDevice(btrd);
                        }
                        LCD.drawString("Press any key", 0, 7);
                        getButtonPress();
                    }
                }
            } while (selected >= 0);
        }
    }

    /**
     * Display all currently known Bluetooth devices.
     */
    private void bluetoothDevices()
    {
        ArrayList<RemoteDevice> devList = Bluetooth.getKnownDevicesList();
        newScreen("Devices");
        if (devList.size() <= 0)
            msg("No known devices");
        else
        {
            String[] names = new String[devList.size()];
            for (int i = 0; i < devList.size(); i++)
            {
                RemoteDevice btrd = devList.get(i);
                names[i] = btrd.getFriendlyName(false);
            }

            TextMenu deviceMenu = new TextMenu(names, 1);
            TextMenu subMenu = new TextMenu(new String[] {"Remove"}, 6);
            int selected = 0;
            do
            {
                newScreen();
                selected = getSelection(deviceMenu, selected);
                if (selected >= 0)
                {
                    newScreen();
                    RemoteDevice btrd = devList.get(selected);
                    LCD.drawString(btrd.getFriendlyName(false), 0, 2);
                    LCD.drawString(btrd.getBluetoothAddress(), 0, 3);
                    LCD.drawString("0x"+Integer.toHexString(btrd.getDeviceClass()), 0, 4);
                    int subSelection = getSelection(subMenu, 0);
                    if (subSelection == 0)
                    {
                        Bluetooth.removeDevice(btrd);
                        break;
                    }
                }
            } while (selected >= 0);
        }
    }

    /**
     * Allow the user to turn Bluetooth power on/off
     * @param on New power setting
     */
    private void bluetoothPower(boolean on)
    {
        newScreen();
        
        LCD.drawString("Powering " + (on ? "on" : "off") +" ...", 0, 2);
        
        indiBT.incCount();
        try
        {
        	btPowerOn = setBluetoothPower(on);
        }
        finally
        {
        	indiBT.decCount();
        }
        updateBTIcon();
        ind.updateNow();
    }

    /**
     * Allow the user to change the Bluetooth PIN.
     */
    private void bluetoothChangePIN()
    {
        // 1. Retrieve PIN from System properties
        String pinStr = SystemSettings.getStringSetting(pinProperty, "1234");
        int len = pinStr.length();
        byte[] pin = new byte[len];
        for (int i = 0; i < len; i++)
            pin[i] = (byte) pinStr.charAt(i);

        // 2. Call enterNumber() method
        if (enterNumber("Enter NXT PIN", pin, 4))
        {
            // 3. Set PIN in system memory.
        	StringBuilder sb = new StringBuilder();
            for (int i = 0; i < pin.length; i++)
                sb.append((char) pin[i]);
            Settings.setProperty(pinProperty, sb.toString());
            Bluetooth.setPin(pin);
        }
    }


    /**
     * Present the Bluetooth menu to the user.
     */
    private void bluetoothMenu()
    {
        int selection = 0;
        TextMenu menu = new TextMenu(null, 3);
        boolean visible;
        do
        {
            newScreen("Bluetooth");
            LCD.drawString("Power", 0, 1);
            LCD.drawString(btPowerOn ? "on" : "off", 11, 1);
            visible = Bluetooth.getVisibility() == 1;
            if (btPowerOn)
            {
                LCD.drawString("Visibility", 0, 2);
                LCD.drawString(visible ? "on" : "off", 11, 2);
                menu.setItems(new String[]
                        {
                            "Power off", "Search and Pair", "Devices", "Visibility", "Change PIN"
                        });
            }
            else
                menu.setItems(new String[]
                        {
                            "Power on"
                        });
            selection = getSelection(menu, selection);
            switch (selection)
            {
                case 0:
                    bluetoothPower(!btPowerOn);
                    break;
                case 1:
                    bluetoothSearch();
                    break;
                case 2:
                    bluetoothDevices();
                    break;
                case 3:
                    Bluetooth.setVisibility((byte) (visible ? 0 : 1));
                    btVisibility = !visible;
                    updateBTIcon();
                    ind.updateNow();
                    break;
                case 4:
                    bluetoothChangePIN();
                    break;
            }
        } while (selection >= 0);
    }

    /**
     * Present the menu for a single file.
     * @param file
     */
    private void fileMenu(File file)
    {
        String fileName = file.getName();
        String ext = Utils.getExtension(fileName);
        int selectionAdd;
        String[] items;
        if (ext.equals("nxj") || ext.equals("bin"))
        {
        	selectionAdd = 0;
            items = new String[]{"Execute program", "Set as Default", "Delete file"};            
        }
        else if (ext.equals("wav"))
        {
        	selectionAdd = 10;
        	items = new String[]{"Play sample", "Delete file"};
        }
        else
        {
        	selectionAdd = 20;
        	items = new String[]{"Delete file"};
        }
        newScreen();
        TextMenu menu = new TextMenu(items, 2, fileName);
        int selection = getSelection(menu, 0);
        if (selection >= 0)
        {
	        switch(selection + selectionAdd)
	        {
	            case 0:
	                usb.shutdown();
	                bt.shutdown();
	                file.exec();
	                break;
	            case 1:
	                Settings.setProperty(defaultProgramProperty, fileName);
	                break;
	            case 10:
	                Sound.playSample(file);
	                break;
	            case 2:
	            case 11:
	            case 20:
	                file.delete();
	                Utils.defragFilesystem();
	                break;
	        }
        }
    }

    /**
     * Display the files in the file system.
     * Allow the user to choose a file for further operations.
     */
    private void filesMenu()
    {
        TextMenu menu = new TextMenu(null, 1);
        int selection = 0;
        do {
            newScreen("Files");
            File[] files = File.listFiles();
            int len = 0;
            for (int i = 0; i < files.length && files[i] != null; i++)
                len++;
            if (len == 0)
            {
                msg("No files found");
                return;
            }
            String fileNames[] = new String[len];
            for (int i = 0; i < len; i++)
                fileNames[i] = files[i].getName();
            menu.setItems(fileNames);
            selection = getSelection(menu, selection);
            if (selection >= 0)
                fileMenu(files[selection]);
        } while (selection >= 0);
    }

    /**
     * Display the sound menu.
     * Allow the user to change volume and key click volume.
     */
    private void soundMenu()
    {
        String[] soundMenuData = new String[]{"Volume:    ", "Key click: "};
        String[] soundMenuData2 = new String[soundMenuData.length];
        TextMenu menu = new TextMenu(soundMenuData2, 2);
        int[][] Volumes =
        {
            {
                Sound.getVolume() / 10, 784, 250, 0
            },
            {
                Button.getKeyClickVolume() / 10, Button.getKeyClickTone(1), Button.getKeyClickLength(), 0
            }
        };
        int selection = 0;
        // Make a note of starting volumes so we know if it changes
        for (int i = 0; i < Volumes.length; i++)
            Volumes[i][3] = Volumes[i][0];
        // remember and Turn of tone for the enter key
        int tone = Button.getKeyClickTone(Button.ID_ENTER);
        Button.setKeyClickTone(Button.ID_ENTER, 0);
        do {
            newScreen("Sound");
            for (int i = 0; i < Volumes.length; i++)
                soundMenuData2[i] = soundMenuData[i] + formatVol(Volumes[i][0]);
            menu.setItems(soundMenuData2);
            selection = getSelection(menu, selection);
            if (selection >= 0)
            {
                Volumes[selection][0]++;
                Volumes[selection][0] %= 11;
                if (selection == 0)
                {
                    Sound.setVolume(Volumes[0][0] * 10);
                    Sound.playNote(Sound.XYLOPHONE, Volumes[selection][1], Volumes[selection][2]);
                }
                else
                    Sound.playTone(Volumes[selection][1], Volumes[selection][2], -Volumes[selection][0] * 10);
            }
        } while (selection >= 0);
        // Make sure key click is back on and has new volume
        Button.setKeyClickTone(Button.ID_ENTER, tone);
        Button.setKeyClickVolume(Volumes[1][0] * 10);
        // Save in settings
        if (Volumes[0][0] != Volumes[0][3])
            Settings.setProperty(Sound.VOL_SETTING, String.valueOf(Volumes[0][0] * 10));
        if (Volumes[1][0] != Volumes[1][3])
            Settings.setProperty(Button.VOL_SETTING, String.valueOf(Volumes[1][0] * 10));
    }

    /**
     * Ask the user for confirmation of an action.
     * @param prompt A description of the action about to be performed
     * @return 1=yes 0=no < 0 escape
     */
    private int getYesNo(String prompt, boolean yes)
    {
        TextMenu menu = new TextMenu(new String[]{"No", "Yes"}, 6, prompt);
        return getSelection(menu, yes ? 1 : 0);
    }

    /**
     * Present details of the default program
     * Allow the user to specifiy run on system start etc.
     */
    private void systemAutoRun()
    {
    	newScreen("Auto Run");
    	File f = getDefaultProgram();
    	if (f == null)
    	{
       		msg("No default set");
    	}
    	else
    	{
        	LCD.drawString("Default Program:", 0, 2);
        	LCD.drawString(f.getName(), 1, 3);
        	
        	String current = Settings.getProperty(defaultProgramAutoRunProperty, "");
            int selection = getYesNo("Run at power up?", current.equals("ON"));
            if (selection >= 0)
            	Settings.setProperty(defaultProgramAutoRunProperty, selection == 0 ? "OFF" : "ON");
    	}
    }

    /**
     * Present the system menu.
     * Allow the user to format the filesystem. Change timeouts and control
     * the default program usage.
     */
    private void systemMenu()
    {
        String[] menuData = {"Format", "", "Auto Run", "Unset default"};
        TextMenu menu = new TextMenu(menuData, 4);
        int selection = 0;
        do {
            newScreen("System");
            LCD.drawString("Flash", 0, 1);
            LCD.drawInt(File.freeMemory(), 6, 10, 1);
            LCD.drawString("RAM", 0, 2);
            LCD.drawInt((int) (Runtime.getRuntime().freeMemory()), 11, 2);
            LCD.drawString("Battery", 0, 3);
            int millis = Battery.getVoltageMilliVolt() + 50;
            LCD.drawInt((millis - millis % 1000) / 1000, 11, 3);
            LCD.drawString(".", 12, 3);
            LCD.drawInt((millis % 1000) / 100, 13, 3);
            if (Battery.isRechargeable())
                LCD.drawString("R", 15, 3);
            menuData[1] = "Sleep time: " + (timeout == 0 ? "off" : String.valueOf(timeout));
            File f = getDefaultProgram();
            if (f == null)
            	menuData[3] = null;
            menu.setItems(menuData);
            selection = getSelection(menu, selection);
            switch (selection)
            {
                case 0:
                    if (getYesNo("Delete all files?", false) == 1)
                        File.format();
                    break;
                case 1:
                    timeout++;
                    if (timeout > maxSleepTime)
                        timeout = 0;
                    Settings.setProperty(sleepTimeProperty, String.valueOf(timeout));
                    break;
                case 2:
                    systemAutoRun();
                    break;
                case 3:
                    Settings.setProperty(defaultProgramProperty, "");
                    Settings.setProperty(defaultProgramAutoRunProperty, "");
                    selection = 0;
                    break;
            }
        } while (selection >= 0);
    }

    /**
     * Display system version information.
     */
    private void displayVersion()
    {
        newScreen("Version");
        LCD.drawString("Firmware version", 0, 2);
        LCD.drawString(Utils.versionToString(NXT.getFirmwareRawVersion()) + "(rev." +
                NXT.getFirmwareRevision() + ")", 1, 3);
        LCD.drawString("Menu version", 0, 4);
        LCD.drawString(Utils.versionToString(VERSION) + "(rev." +
                REVISION.substring(11, REVISION.length() - 2) + ")", 1, 5);
        getButtonPress();
    }

    /**
     * Display the main system menu.
     * Allow the user to select File, Bluetooth, Sound, System operations.
     */
    private void mainMenu()
    {
        TextMenu menu = new TextMenu(new String[]
                {
                    "Run Default", "Files", "Bluetooth", "Sound", "System", "Version"
                }, 1);
        int selection = 0;
        do
        {
            newScreen(null);
            selection = getSelection(menu, selection);
            switch (selection)
            {
                case 0:
                    mainRunDefault();
                    break;
                case 1:
                    filesMenu();
                    break;
                case 2:
                    bluetoothMenu();
                    break;
                case 3:
                    soundMenu();
                    break;
                case 4:
                    systemMenu();
                    break;
                case 5:
                    displayVersion();
                    break;
            }
        } while (selection >= 0);
    }

    private void start()
    {
        bt.start();
        usb.start();
    	ind.start();
    }
    
    private static File getDefaultProgram()
    {
    	String file = Settings.getProperty(defaultProgramProperty, "");
    	if (file.length() > 0)
    	{
    		File f = new File(file);
        	if (f.exists())
        		return f;
        	
           	Settings.setProperty(defaultProgramProperty, "");
           	Settings.setProperty(defaultProgramAutoRunProperty, "OFF");
    	}
    	return null;
    }
    
    /**
     * Main entry point.
     * Startup the system.
     * @param args Not used.
     * @throws Exception
     */
    public static void main(String[] args) throws Exception
    {
        // Run default program if required
        if (NXT.getProgramExecutionsCount() == 1)
        {
            // First time we have run
        	File f = getDefaultProgram();
        	if (f != null)
        	{
            	String auto = Settings.getProperty(defaultProgramAutoRunProperty, "");        	
        		if (auto.equals("ON") && !Button.LEFT.isDown())
	            {
            		f.exec();
	            }
        	}
        }
        
        LCD.setAutoRefresh(false);
        LCD.setContrast(0);
        
        byte[] logo_data = Utils.stringToBytes8(Config.LOGO_DATA);
    	byte[] text_data = Utils.textToBytes("leJOS "+Utils.versionToString(VERSION));
    	byte[] display = LCD.getDisplay();
    	
    	int logo_x = (LCD.SCREEN_WIDTH - Config.LOGO_WIDTH)/2;
    	int text_x = (LCD.SCREEN_WIDTH - text_data.length)/2;
    	int logo_y = (LCD.SCREEN_HEIGHT - Config.LOGO_HEIGHT - Config.LOGO_TEXT_SEP - LCD.FONT_HEIGHT)/2;
    	int text_y = logo_y + Config.LOGO_HEIGHT + Config.LOGO_TEXT_SEP;
    	
    	LCD.bitBlt(logo_data, Config.LOGO_WIDTH, Config.LOGO_HEIGHT, 0, 0,
    			display, LCD.SCREEN_WIDTH, LCD.SCREEN_HEIGHT, logo_x, logo_y,
    			Config.LOGO_WIDTH, Config.LOGO_HEIGHT, LCD.ROP_COPY);
    	LCD.bitBlt(text_data, text_data.length, LCD.FONT_HEIGHT, 0, 0,
    			display, LCD.SCREEN_WIDTH, LCD.SCREEN_HEIGHT, text_x, text_y,
    			text_data.length, LCD.FONT_HEIGHT, LCD.ROP_COPY);
    	
    	LCD.asyncRefresh();
        
        TuneThread tuneThread = new TuneThread();
        //Fade in
        tuneThread.start();
        
        Utils.defragFilesystem();
        
        // Tell thread to play tune
        tuneThread.setState(1);
        
        InitThread initThread = new InitThread();
        initThread.start();
        
        // Make sure color sensor can be used remotely.
        // This will also reset the sensors
        for (int i=0; i<SensorPort.NUMBER_OF_PORTS; i++)
            SensorPort.getInstance(i).enableColorSensor();
        
        // Wait for init to complete
        initThread.join();      
        // Wait until tune is complete
        tuneThread.waitState(2);
        Utils.fadeOut();
        
        initThread.menu.start();
        // Tell thread to fade in again
        tuneThread.setState(3);
        initThread.menu.mainMenu();

        Utils.fadeOut();
        NXT.shutDown();
    }
}

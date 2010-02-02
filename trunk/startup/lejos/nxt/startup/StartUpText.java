package lejos.nxt.startup;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.bluetooth.RemoteDevice;
import javax.microedition.lcdui.Graphics;

import lejos.nxt.Battery;
import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.Settings;
import lejos.nxt.Sound;
import lejos.nxt.SystemSettings;
import lejos.nxt.SensorPort;
import lejos.util.TextMenu;
import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.LCP;
import lejos.nxt.comm.LCPResponder;
import lejos.nxt.comm.NXTCommConnector;
import lejos.nxt.comm.NXTCommDevice;
import lejos.nxt.comm.USB;
import lejos.nxt.NXT;
import lejos.util.Delay;
import lejos.nxt.FlashError;

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

    Graphics g = new Graphics();
    boolean btPowerOn = false;
    Indicators ind = new Indicators();
    Responder usb = new Responder(USB.getConnector(), Indicators.IO_USB);
    Responder bt = new Responder(Bluetooth.getConnector(), Indicators.IO_BT);
    int timeout;
    TextMenu curMenu = null;
    static final String blank = "                ";
    static final String defaultProgramProperty = "lejos.default_program";
    static final String defaultProgramAutoRunProperty = "lejos.default_autoRun";
    static final String sleepTimeProperty = "lejos.sleep_time";
    static final String pinProperty = "lejos.bluetooth_pin";
    static final int defaultSleepTime = 2;
    static final int maxSleepTime = 10;
    static final String revision = "$Revision$";
    static final int MAJOR_VERSION = 0;
    static final int MINOR_VERSION = 85;

    /**
     * Manage the top line of the display.
     * The top line of the display shows battery state, menu titles, and I/O
     * activity.
     */
    class Indicators extends Thread
    {
        // Types of IO activity.
        public static final int IO_NONE = 3;
        public static final int IO_BT = 0;
        public static final int IO_USB = 1;
        public static final int IO_SEARCH = 2;
        // Battery state information
        private static final int STD_MIN = 6100;
        private static final int STD_OK = 6500;
        private static final int STD_MAX = 8000;
        private static final int RECHARGE_MIN = 7100;
        private static final int RECHARGE_OK = 7200;
        private static final int RECHARGE_MAX = 8200;
        // Animations used for I/O state.
        private final String[][] ioDisplay = new String[][]
        {
            {
                ".  ", " . ", "  ."
            },
            {
                "+  ", " + ", "  +"
            },
            {
                " I ", "(I)"
            }
        };
        private int batteryLow;
        private int batteryOk;
        private int batteryRange;
        private boolean lowVoltage;
        private int tick = 0;
        private int ioMode = IO_NONE;
        private int ioStart;
        private String title = "";

        public Indicators()
        {
            // Init battery parameters
            if (Battery.isRechargeable())
                initBattery(RECHARGE_MIN, RECHARGE_OK, RECHARGE_MAX);
            else
                initBattery(STD_MIN, STD_OK, STD_MAX);
            setDaemon(true);
        }

        /**
         * Display the battery icon.
         * @param level Current charge level 0-100%
         */
        private void drawBattery(int level)
        {
            g.drawRect(1, 1, 11, 5);  // battery icon
            g.drawRect(13, 3, 1, 1);
            g.fillRect(2, 2, level / 10, 4);
            g.setColor(Graphics.WHITE);
            g.fillRect(level / 10 + 2, 2, 10 - (level / 10), 4);
            g.setColor(Graphics.BLACK);
        }

        /**
         * Erase a specified area of the top line.
         * @param start
         * @param len
         */
        private void erase(int start, int len)
        {
            g.setColor(Graphics.WHITE);
            g.fillRect(start, 0, len, 8);
            g.setColor(Graphics.BLACK);
        }

        /**
         * Calc the state of the battery charge.
         * @param mV Current reading
         * @return current charge level 0-100%
         */
        private int calcBatteryLevel(int mV)
        {
            int val = ((mV - batteryLow) * 100 + batteryRange / 2) / batteryRange;
            if (val < 0)
                val = 0;
            if (val > 100)
                val = 100;
            return val;
        }

        /**
         * Update the battery status display.
         * Low battery state is shown by flashing the icon.
         */
        private void updateBattery()
        {
            // Handle the battery display.
            int mV = Battery.getVoltageMilliVolt();
            if (mV <= batteryLow)
                lowVoltage = true;
            else if (mV >= batteryOk)
                lowVoltage = false;
            if (lowVoltage)
            {
                if ((tick & 1) == 0)
                    drawBattery(0);
            }
            else
                drawBattery(calcBatteryLevel(mV));
        }

        /**
         * Initialize the battery state
         * @param low Low voltage limit
         * @param ok Voltage that is OK (not low).
         * @param high 100% charged level.
         */
        private void initBattery(int low, int ok, int high)
        {
            batteryLow = low;
            batteryOk = ok;
            batteryRange = high - low;
        }

        /**
         * Update the I/O status.
         * We display Bluetooth state, plus small animations showing I/O
         * activity.
         */
        private void updateIO()
        {
            if (ioMode == IO_NONE)
                g.drawString(" BT", 82, 0, 0, !btPowerOn);  // invert when power is off
            else
            {
                g.drawString(ioDisplay[ioMode][tick % ioDisplay[ioMode].length], 82, 0, 0);
                // Data activity is auto reset
                if (ioMode <= IO_USB && tick > ioStart + 2)
                    setIOMode(IO_NONE);
            }
        }

        /**
         * Update the title part of the display.
         * We centre the title string.
         */
        private void updateTitle()
        {
            g.drawString(title, LCD.SCREEN_WIDTH / 2, 0, Graphics.HCENTER);
        }

        /**
         * Set a new title.
         * @param title
         */
        public void setTitle(String title)
        {
            this.title = title;
            update();
        }

        /**
         * Update all of the status display.
         */
        public void update()
        {
            LCD.setAutoRefresh(false);
            erase(0, 100);
            updateTitle();
            updateIO();
            updateBattery();
            LCD.refresh();
            LCD.setAutoRefresh(true);
        }

        /**
         * Set an I/O mode.
         * If this is a new mode we update the display. Data animations (IO_BT
         * and IO_USB), will automatically reset.
         * @param mode
         */
        public void setIOMode(int mode)
        {
            if (mode != ioMode)
            {
                ioStart = tick;
                ioMode = mode;
                if (mode != IO_NONE)
                    update();
            }
        }

        /**
         * Main status thread.
         * Update the display every second.
         */
        @Override
        public void run()
        {
            for (;;)
            {
                Delay.msDelay(1000);
                tick++;
                update();
            }
        }
    }

    /**
     * Class to handle commands from USB/Bluetooth connections.
     * @author andy
     */
    class Responder extends LCPResponder
    {

        int mode;

        /**
         * Create an object for the required connection type.
         * @param con Connector object for the underlying protocol.
         */
        public Responder(NXTCommConnector con, int mode)
        {
            super(con);
            this.mode = mode;
            setDaemon(true);
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
                ind.setIOMode(mode);
                if (curMenu != null)
                    curMenu.resetTimeout();
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
            if (inMsg[1] == LCP.CLOSE || inMsg[1] == LCP.DELETE)
            {
                if (inMsg[1] == LCP.DELETE)
                    try
                    {
                        File.defrag();
                    }
                    catch (IOException ioe)
                    {
                        File.reset();
                    }
                Sound.beepSequenceUp();
                if (curMenu != null)
                    curMenu.quit();
            }
            if (inMsg[1] == LCP.BOOT)
                // Reboot into firmware update mode. Only supported
                NXT.boot();
            super.postCommand(inMsg, inLen, replyMsg, replyLen);
        }
    }

    /**
     * Play the leJOS startup tune.
     */
    private void playTune()
    {
        int[] freq =
        {
            523, 784, 659
        };
        for (int i = 0; i < 3; i++)
            Sound.playNote(Sound.XYLOPHONE, freq[i], (i == 3 ? 500 : 300));
        Sound.pause(300);
    }

    /**
     * Start a new screen display.
     * Clear the screen and set the screen title.
     * @param title
     */
    private void newScreen(String title)
    {
        LCD.clear();
        ind.setTitle(title);
    }

    /**
     * Start a new screen display using the current title.
     */
    private void newScreen()
    {
        LCD.clear();
        ind.update();
    }

    /**
     * Turn Bluetooth power on/off.
     * Record this new setting in the status bytes held by the Bluetooth module.
     * @param powerOn
     * @return The new power state.
     */
    private boolean setBluetoothPower(boolean powerOn)
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
    private String formatVol(int vol)
    {
        if (vol == 0)
            return "mute";
        if (vol == 10)
            return "10";
        return " " + vol;
    }

    /**
     * Return the extension part of a filename
     * @param fileName
     * @return the file extension
     */
    private String getExtension(String fileName)
    {
        int dot = fileName.lastIndexOf(".");
        if (dot < 0)
            return "";

        return fileName.substring(dot + 1, fileName.length());
    }

    /**
     * Return the base part (no extension) of a filename
     * @param fileName
     * @return the base part of the name
     */
    private String getBaseName(String fileName)
    {
        int dot = fileName.lastIndexOf(".");
        if (dot < 0)
            return fileName;

        return fileName.substring(0, dot);
    }

    /**
     * Run the default program (if set).
     */
    private void runDefaultProgram()
    {
        String defaultProgram = Settings.getProperty(defaultProgramProperty, "");
        if (defaultProgram != null && defaultProgram.length() > 0)
        {
            String progName = defaultProgram + ".nxj";
            File f = new File(progName);
            if (f.exists())
                f.exec();
            else
                Settings.setProperty(defaultProgramProperty, "");
        }
    }

    /**
     * Read a button press.
     * If the read timesout then exit the system.
     * @return The bitcode of the button.
     */
    private int getButtonPress()
    {
        int value = Button.waitForPress(timeout*60000);
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
    private byte[] enterNumber(int digits, String title, byte[] defaultNumber)
    {
        // !! Should probably check to make sure defaultNumber is digits in size
        char spacer = ' ';
        byte[] number = new byte[digits];
        int curDigit = 0;
        boolean done = false;

        if (defaultNumber != null)
            number = defaultNumber;


        while (!done)
        {
            newScreen();
            LCD.drawString(title, 0, 2);
            String str = "";
            for (int i = 0; i < digits; i++)
                str = str + spacer + (char) number[i];
            LCD.drawString(str, 0, 4);
            g.drawRect(curDigit * 12 + 3, 30, 10, 10);

            int ret = getButtonPress();
            switch (ret)
            {
                case 0x01:
                { // ENTER
                    curDigit++;
                    if (curDigit >= digits)
                        done = true;
                    break;
                }
                case 0x02:
                { // LEFT
                    number[curDigit]--;
                    if (number[curDigit] < '0')
                        number[curDigit] = '9';
                    break;
                }
                case 0x04:
                { // RIGHT
                    number[curDigit]++;
                    if (number[curDigit] > '9')
                        number[curDigit] = '0';
                    break;
                }
                case 0x08:
                { // ESCAPE
                    curDigit--;
                    // Return null if user backs out
                    if (curDigit < 0)
                        return null;
                    break;
                }
            }
        }
        return number;
    }

    /**
     * Set the address of the NXT.
     * Ensure that we are using the same name for Bluetooth and USB access to
     * the NXT. The USB (and RS485) address is stored in flash memory.
     */
    private void setAddress()
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
        curMenu = menu;
        int selection;
        // If the menu is interrupted by another thread, redisplay
        do {
            selection = menu.select(cur, timeout*60000);
        } while (selection == -2);
        if (selection == -3)
            NXT.shutDown();
        curMenu = null;
        return selection;
    }

    /**
     * Make the LCD display fade into view.
     */
    private void fadeIn()
    {
        for(int i = 20; i < 0x60; i++)
        {
            Delay.msDelay(5);
            LCD.setContrast(i);
        }
    }

    /**
     * Make the LCD display fade out of view.
     */
    private void fadeOut()
    {
        for(int i = 0x60; i >= 20; i--)
        {
            Delay.msDelay(5);
            LCD.setContrast(i);
        }
    }
    
    /**
     * Startup the menu system.
     * Play the greeting tune.
     * Run the default program if auto-run is requested.
     * Initialize I/O etc.
     */
    volatile int stState = 0;
    private void startup()
    {
        LCD.setContrast(0);
        Thread tuneThread = new Thread()
        {

            @Override
            public void run()
            {
                playTune();
                //fadeIn();
            }
        };
        Thread initThread = new Thread()
        {

            @Override
            public void run()
            {
                File.listFiles();
                // Defrag the file system
                try
                {
                    File.defrag();
                }
                catch (IOException ioe)
                {
                    File.reset();
                }
                stState = 1;
                setAddress();
                timeout = SystemSettings.getIntSetting(sleepTimeProperty, defaultSleepTime);
                btPowerOn = setBluetoothPower(Bluetooth.getStatus() == 0);
                usb.start();
                bt.start();
                stState = 2;
                // Wait for the screen to be dim
                while (stState != 3)
                    Thread.yield();
                // Give time for the menu to be displayed
                Delay.msDelay(250);
                // and make it visible.
                fadeIn();
            }
        };
        // Make sure color sensor can be used remotely, this will also reset
        // the sensors
        for (int i=0; i<SensorPort.NUMBER_OF_PORTS; i++)
            SensorPort.getInstance(i).enableColorSensor();
        // Run default program if required
        if (NXT.getProgramExecutionsCount() == 1)
        {
            // First time we have run
            if (!Button.LEFT.isPressed() &&
                Settings.getProperty(defaultProgramAutoRunProperty, "").equals("ON"))
            {
                tuneThread.start();
                runDefaultProgram();
            }
        }
        initThread.start();
        fadeIn();
        // Wait for defrag to complete
        while (stState != 1)
            Thread.yield();
        tuneThread.start();
        // Wait for init to complete
        while (stState != 2)
            Thread.yield();
        fadeOut();
        ind.start();
        LCD.clear();
        // Tell the background thread we are done and to fade in the menu.
        stState = 3;
    }

    /**
     * Display a status message
     * @param msg
     */
    private void msg(String msg)
    {
        newScreen();
        LCD.drawString(msg, 0, 2);
        Delay.msDelay(2000);
    }

    /**
     * Perform the Bluetooth search operation
     * Search for Bluetooth devices
     * Present those that are found
     * Allow pairing
     */
    private void bluetoothSearch()
    {
        byte[] cod =
        {
            0, 0, 0, 0
        }; // All
        newScreen("Searching");
        ind.setIOMode(Indicators.IO_SEARCH);
        Vector devList = Bluetooth.inquire(5, 10, cod);
        if (devList.size() > 0)
        {
            String[] names = new String[devList.size()];
            for (int i = 0; i < devList.size(); i++)
            {
                RemoteDevice btrd = ((RemoteDevice) devList.elementAt(i));
                names[i] = btrd.getFriendlyName(false);
            }
            TextMenu searchMenu = new TextMenu(names, 1);
            TextMenu subMenu = new TextMenu(new String[]{"Pair"}, 4);
            int selected;
            do
            {
                newScreen("Found");
                ind.setIOMode(Indicators.IO_NONE);
                selected = getSelection(searchMenu, 0);
                if (selected >= 0)
                {
                    RemoteDevice btrd = ((RemoteDevice) devList.elementAt(selected));
                    newScreen();
                    LCD.drawString(names[selected], 0, 1);
                    LCD.drawString(btrd.getBluetoothAddress(), 0, 2);
                    int subSelection = getSelection(subMenu, 0);
                    if (subSelection == 0)
                    {
                        newScreen("Pairing");
                        Bluetooth.addDevice(btrd);
                        byte[] tempPin =
                        {
                            '0', '0', '0', '0'
                        };
                        byte[] pin = enterNumber(4, "PIN for " + btrd.getFriendlyName(false), tempPin); // !! Assuming 4 length
                        if (pin == null) break;
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
        else
            msg("No devices found");
    }

    /**
     * Display all currently known Bluetooth devices.
     */
    private void bluetoothDevices()
    {
        Vector devList = Bluetooth.getKnownDevicesList();
        newScreen("Devices");
        if (devList.size() > 0)
        {
            String[] names = new String[devList.size()];
            for (int i = 0; i < devList.size(); i++)
            {
                RemoteDevice btrd = ((RemoteDevice) devList.elementAt(i));
                names[i] = btrd.getFriendlyName(false);
            }

            TextMenu deviceMenu = new TextMenu(names, 1);
            TextMenu subMenu = new TextMenu(new String[] {"Remove"}, 6);
            int selected;
            do
            {
                newScreen();
                selected = getSelection(deviceMenu, 0);
                if (selected >= 0)
                {
                    newScreen();
                    RemoteDevice btrd = ((RemoteDevice) devList.elementAt(selected));
                    LCD.drawString(btrd.getFriendlyName(false), 0, 2);
                    LCD.drawString(btrd.getBluetoothAddress(), 0, 3);
                    for (int i = 0; i < 4; i++)
                        LCD.drawInt(btrd.getDeviceClass()[i], 3, i * 4, 4);
                    int subSelection = getSelection(subMenu, 0);
                    if (subSelection == 0)
                    {
                        Bluetooth.removeDevice(btrd);
                        break;
                    }
                }
            } while (selected >= 0);
        }
        else
            msg("No known devices");
    }

    /**
     * Allow the user to turn Bluetooth power on/off
     * @param on New power setting
     */
    private void bluetoothPower(boolean on)
    {
        newScreen("Power " + (on ? "on" : "off"));
        ind.setIOMode(Indicators.IO_BT);
        btPowerOn = setBluetoothPower(on);
    }

    /**
     * Allow the user to change the Bluetooth PIN.
     */
    private void bluetoothChangePIN()
    {
        // 1. Retrieve PIN from System properties
        String pinStr = SystemSettings.getStringSetting(pinProperty, "1234");
        byte[] pin = new byte[pinStr.length()];
        for (int i = 0; i < pinStr.length(); i++)
            pin[i] = (byte) pinStr.charAt(i);

        // 2. Call enterNumber() method
        pin = enterNumber(4, "Enter NXT PIN", pin);

        // 3. Set PIN in system memory.
        String pinSet = "";
        if (pin != null)
        {
            for (int i = 0; i < pin.length; i++)
                pinSet += (char) pin[i];
            Settings.setProperty(pinProperty, pinSet);
            Bluetooth.setPin(pin);
        }
    }


    /**
     * Present the Bluetooth menu to the user.
     */
    private void bluetoothMenu()
    {
        int selection;
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
            selection = getSelection(menu, 0);
            switch (selection)
            {
                case 0:
                    if (btPowerOn)
                        bluetoothPower(false);
                    else
                        bluetoothPower(true);
                    break;
                case 1:
                    bluetoothSearch();
                    break;
                case 2:
                    bluetoothDevices();
                    break;
                case 3:
                    Bluetooth.setVisibility((byte) (visible ? 0 : 1));
                    break;
                case 4:
                    bluetoothChangePIN();
                    break;
            }
        } while (selection >= 0);
    }

    /**
     * Delete a file from the file system.
     * Once the file has been deleted we may need to defrag.
     * @param file
     */
    private void deleteFile(File file)
    {
        file.delete();
        try
        {
            File.defrag();
        }
        catch (IOException ioe)
        {
            File.reset();
        }
    }

    /**
     * Present the menu for a single file.
     * @param file
     */
    private void fileMenu(File file)
    {
        String fileName = file.getName();
        String ext = getExtension(fileName);
        newScreen();
        TextMenu menu = new TextMenu(null, 2);
        menu.setTitle(fileName);
        if (ext.equals("nxj") || ext.equals("bin"))
        {
            menu.setItems(new String[]{"Execute program", "Set as Default", "Delete file"});
            switch(getSelection(menu, 0))
            {
                case 0:
                    usb.shutdown();
                    bt.shutdown();
                    file.exec();
                    break;
                case 1:
                    Settings.setProperty(defaultProgramProperty, getBaseName(fileName));
                    break;
                case 2:
                    deleteFile(file);
                    break;
            }
        }
        else if (ext.equals("wav"))
        {
            menu.setItems(new String[]{"Play sample", "Delete file"});
            switch (getSelection(menu, 0))
            {
                case 0:
                    Sound.playSample(file);
                    break;
                case 1:
                    deleteFile(file);
                    break;
            }
        }
        else
        {
            menu.setItems(new String[]{"Delete file"});
            switch (getSelection(menu, 0))
            {
                case 0:
                    deleteFile(file);
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
        int selection;
        do {
            newScreen("Files");
            File[] files = File.listFiles();
            int len = 0;
            for (int i = 0; i < files.length && files[i] != null; i++)
                len++;
            String fileNames[] = new String[len];
            for (int i = 0; i < len; i++)
                fileNames[i] = files[i].getName();
            TextMenu menu = new TextMenu(fileNames, 1);
            selection = getSelection(menu, 0);
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
    private int getYesNo(String prompt)
    {
        TextMenu menu = new TextMenu(new String[]{"No", "Yes"}, 6, prompt);
        return getSelection(menu, 0);
    }

    /**
     * Present details of the default program
     * Allow the user to specifiy run on system start etc.
     */
    private void defaultProgram()
    {
        String defaultPrgm = Settings.getProperty(defaultProgramProperty, "");
        if (defaultPrgm != null && defaultPrgm.length() > 0)
        {
            LCD.drawString("Auto Run: " + Settings.getProperty(defaultProgramAutoRunProperty, "") + blank, 0, 2);
            LCD.drawString("Default Program:    ", 0, 3);
            LCD.drawString(" " + defaultPrgm + blank, 0, 4);
            int selection = getYesNo("Run at power up?");
            if (selection == 0)
                Settings.setProperty(defaultProgramAutoRunProperty, "OFF");
            else if (selection == 1)
                Settings.setProperty(defaultProgramAutoRunProperty, "ON");
        }
        else
            msg("No default set");
    }

    /**
     * Present the system menu.
     * Allow the user to format the filesystem. Change timeouts and control
     * the default program usage.
     */
    private void systemMenu()
    {
        String[] menuData = {"Format", "", "Auto Run"};
        TextMenu menu = new TextMenu(null, 5);
        int selection = 0;
        do {
            newScreen("System");
            LCD.drawString("Flash", 0, 2);
            LCD.drawInt(File.freeMemory(), 6, 10, 2);
            LCD.drawString("RAM", 0, 3);
            LCD.drawInt((int) (Runtime.getRuntime().freeMemory()), 11, 3);
            LCD.drawString("Battery", 0, 4);
            int millis = Battery.getVoltageMilliVolt() + 50;
            LCD.drawInt((millis - millis % 1000) / 1000, 11, 4);
            LCD.drawString(".", 12, 4);
            LCD.drawInt((millis % 1000) / 100, 13, 4);
            if (Battery.isRechargeable())
                LCD.drawString("R", 15, 4);
            menuData[1] = "Sleep time: " + timeout;
            menu.setItems(menuData);
            selection = getSelection(menu, selection);
            switch (selection)
            {
                case 0:
                    if (getYesNo("Delete all files?") == 1)
                        File.format();
                    break;
                case 1:
                    timeout++;
                    if (timeout > maxSleepTime)
                        timeout = 0;
                    Settings.setProperty(sleepTimeProperty, "" + timeout);
                    break;
                case 2:
                    defaultProgram();
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
        LCD.drawString(NXT.getFirmwareMajorVersion() + "." +
                NXT.getFirmwareMinorVersion() + "(rev. " +
                NXT.getFirmwareRevision() + ")", 1, 3);
        LCD.drawString("Menu version", 0, 4);
        LCD.drawString(MAJOR_VERSION + "." +
                MINOR_VERSION + "(rev." +
                revision.substring(10, revision.length() - 2) + ")", 1, 5);
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
        int selection;
        do
        {
            newScreen(Bluetooth.getFriendlyName());
            selection = getSelection(menu, 0);
            switch (selection)
            {
                case 0:
                    runDefaultProgram();
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
        } while (selection > 0);
    }

    /**
     * Main entry point.
     * Startup the system.
     * @param args Not used.
     * @throws Exception
     */
    public static void main(String[] args) throws Exception
    {
        StartUpText sysMenu = new StartUpText();
        sysMenu.startup();
        sysMenu.mainMenu();
        sysMenu.fadeOut();
        NXT.shutDown();
    }
}

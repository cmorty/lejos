package lejos.nxt;
import lejos.robotics.Colors;
import lejos.util.Delay;

/**
 * Abstraction for a NXT input port.
 * 
 */
public class SensorPort implements LegacySensorPort, I2CPort, ListenerCaller
{

    /**
     * Power types.
     */
    public static final int POWER_STD = 0;
    public static final int POWER_RCX9V = 1;
    public static final int POWER_9V = 2;
    // Sensor port I/O pin ids
    public static final int SP_DIGI0 = 0;
    public static final int SP_DIGI1 = 1;
    public static final int SP_ANA = 2;
    // Sensor port pin modes
    public static final int SP_MODE_OFF = 0;
    public static final int SP_MODE_INPUT = 1;
    public static final int SP_MODE_OUTPUT = 2;
    public static final int SP_MODE_ADC = 3;
    // Digital I/O pins used to control the sensor operation
    public static final int DIGI_UNUSED = -1;
    public static final int DIGI_OFF = 0;
    public static final int DIGI_0_ON = (1 << SP_DIGI0);
    public static final int DIGI_1_ON = (1 << SP_DIGI1);
    private static final byte[] powerType =
    {
        POWER_STD, // NO_SENSOR
        POWER_STD, // SWITCH
        POWER_RCX9V, // TEMPERATURE
        POWER_RCX9V, // REFLECTION
        POWER_RCX9V, // ANGLE
        POWER_STD, // LIGHT_ACTIVE
        POWER_STD, // LIGHT_INACTIVE
        POWER_STD, // SOUND_DB
        POWER_STD, // SOUND_DBA
        POWER_STD, // CUSTOM
        POWER_STD, // LOWSPEED,
        POWER_9V,  // LOWSPEED_9V
        POWER_STD, // Unused
        POWER_STD, // COLOR_FULL
        POWER_STD, // COLOR_RED
        POWER_STD, // COLOR_GREEN
        POWER_STD, // COLOR_BLUE
        POWER_STD, // COLOR_NONE
    };
    private static final byte[] controlPins =
    {
        DIGI_UNUSED, // NO_SENSOR
        DIGI_UNUSED, // SWITCH
        DIGI_UNUSED, // TEMPERATURE
        DIGI_UNUSED, // REFLECTION
        DIGI_UNUSED, // ANGLE
        DIGI_0_ON, // LIGHT_ACTIVE
        DIGI_OFF, // LIGHT_INACTIVE
        DIGI_0_ON, // SOUND_DB
        DIGI_1_ON, // SOUND_DBA
        DIGI_UNUSED, // CUSTOM
        DIGI_UNUSED, // LOWSPEED,
        DIGI_UNUSED,  // LOWSPEED_9V
        DIGI_UNUSED,  // Unused
        DIGI_OFF, // COLOR_FULL
        DIGI_OFF, // COLOR_RED
        DIGI_OFF, // COLOR_GREEN
        DIGI_OFF, // COLOR_BLUE
        DIGI_OFF, // COLOR_NONE
    };
    
    /**
     * Port labeled 1 on NXT.
     */
    public static final SensorPort S1 = new SensorPort(0);
    /**
     * Port labeled 2 on NXT.
     */
    public static final SensorPort S2 = new SensorPort(1);
    /**
     * Port labeled 3 on NXT.
     */
    public static final SensorPort S3 = new SensorPort(2);
    /**
     * Port labeled 4 on NXT.
     */
    public static final SensorPort S4 = new SensorPort(3);
    /**
     * Array containing all three ports [0..3].
     */
    public static final SensorPort[] PORTS =
    {
        SensorPort.S1, SensorPort.S2, SensorPort.S3, SensorPort.S4
    };


    private int iPortId;
    private short iNumListeners = 0;
    private SensorPortListener[] iListeners;
    private int iPreviousValue;
    private int type, mode;

    /**
     * The SensorReader class provides a way of performing type dependent way
     * to obtain data froma sensor. This base class simply returns no data.
     */
    protected class SensorReader
    {
        /**
         * Used to notify the reader that the type of the sensor has changed.
         * @param type
         */
        public void setType(int type){}
        /**
         * Used to notify the reader that the operating mode of the sensor has
         * changed.
         * @param mode
         */
        public void setMode(int mode){}
        /**
         * Read a normalised/calibrated value from the sensor.
         * @return < 0 error, >= 0 sensor value
         */
        public int readValue(){ return -1; }
        /**
         * Read a raw value from the sensor.
         * @return < 0 error >= 0 Raw sensor value.
         */
        public int readRawValue() { return -1; }
        /**
         * Return a variable number of sensor values
         * @param values An array in which to return the sensor values.
         * @return The number of values returned.
         */
        public int readValues(int[] values) { return -1; }
        /**
         * Return a variable number of raw sensor values
         * @param values An array in which to return the sensor values.
         * @return The number of values returned.
         */
        public int readRawValues(int[] values) { return -1; }

        /**
         * Reset the sensor.
         */
        public void reset() {}

    }

    protected class StandardReader extends SensorReader
    {
        /**
         * Returns value compatible with Lego firmware.
         * @return the computed value
         */
        @Override
        public int readValue()
        {
            int rawValue = readSensorValue(iPortId);

            if (mode == MODE_BOOLEAN)
                return (rawValue < 600 ? 1 : 0);

            if (mode == MODE_PCTFULLSCALE)
                return ((1023 - rawValue) * 100 / 1023);

            return rawValue;
        }
        
        /**
         * Reads the raw value of the sensor.
         * @return the raw sensor value
         */
        @Override
        public final int readRawValue()
        {
            return readSensorValue(iPortId);
        }
    }
    
    
    /**
     * Lego Color Sensor driver.
     * This driver provides access to the Lego Color sensor. It allows the reading
     * raw and processed color values. The sensor has a tri-color led and this can
     * be set to output red/green/blue or off. It also has a full mode in which
     * four samples are read (off/red/green/blue) very quickly. These samples can
     * then be combined using the calibration data provided by the device to
     * determine the "Lego" color currently being viewed.
     * @author andy
     */
    protected class ColorSensorReader extends SensorReader
    {
        /**
         * Sensor types supported by this driver. The type is used to control the
         * operation of the tri color led.
         */
        /**
         * Indexes into the output arrays for specific color values.
         */
        public static final int RGB_RED = 0;
        public static final int RGB_GREEN = 1;
        public static final int RGB_BLUE = 2;
        public static final int RGB_BLANK = 3;
        protected Colors.Color[] colorMap = Colors.Color.values();
        // pin usage for clock and data lines.
        protected static final int CLOCK = SensorPort.SP_DIGI0;
        protected static final int DATA = SensorPort.SP_DIGI1;
        protected boolean initialized = false;
        protected int type = TYPE_NO_SENSOR;
        // data ranges and limits
        protected static final int ADVOLTS = 3300;
        protected static final int ADMAX = 1023;
        protected static final int MINBLANKVAL = (214 / (ADVOLTS / ADMAX));
        protected static final int SENSORMAX = ADMAX;
        protected int[][] calData = new int[3][4];
        protected int[] calLimits = new int[2];
        protected int[] rawValues = new int[RGB_BLANK + 1];
        protected int[] values = new int[RGB_BLANK + 1];

        /**
         * Create a new Color Sensor instance and bind it to a port.
         */
        public ColorSensorReader()
        {
            initialized = false;
        }

        /**
         * initialize the raw and processed RGB values
         */
        protected void initValues()
        {
            for (int i = 0; i < values.length; i++)
            {
                values[i] = 0;
                rawValues[i] = 0;
            }
        }

        /**
         * Change the type of the sensor
         * @param type new sensor type.
         */
        @Override
        public void setType(int type)
        {
            if (type != TYPE_NO_SENSOR)
            {
                if (this.type != type)
                {
                    this.type = type;
                    initialized = false;
                    checkInitialized();
                }
            }
            else
                reset();
        }

        /**
         * Reset the sensor.
         */
        @Override
        public void reset()
        {
            // It would seem that the only way to reset the sensor is to either
            // power it off, or set it to the color none type.
            setType(TYPE_COLORNONE);
            type = TYPE_NO_SENSOR;
        }



        /**
         * Set the clock pin to the specified value
         * @param val the new value(0/1) for the pin.
         */
        protected void setClock(int val)
        {
            setSensorPin(CLOCK, val);
        }

        /**
         * Set the data pin to the specified value
         * @param val new value(0/1) for the pin.
         */
        protected void setData(int val)
        {
            setSensorPin(DATA, val);
        }

        /**
         * get the current digital value from the data pin.
         * @return current pin value
         */
        protected boolean getData()
        {
            return getSensorPin(DATA) != 0;
        }

        /**
         * Read the current analogue value from the data pin
         * @return current value of the pin.
         */
        protected int readData()
        {
            return readSensorPin(DATA);
        }

        /**
         * perform a reset of the device.
         */
        protected void resetSensor()
        {
            // Set both ports to 1
            setClock(1);
            setData(1);
            setSensorPinMode(CLOCK, SensorPort.SP_MODE_OUTPUT);
            setSensorPinMode(DATA, SensorPort.SP_MODE_OUTPUT);
            Delay.msDelay(1);
            // Take clock down
            setClock(0);
            Delay.msDelay(1);
            // Raise it
            setClock(1);
            Delay.msDelay(1);
            // Take clock down for 100ms
            setClock(0);
            Delay.msDelay(100);
        }

        /**
         * Send the new operating mode to the sensor.
         * The value is sent to the sensor by using the clock pin to clock a series
         * of 8 bits out to the device.
         * @param mode
         */
        protected void sendMode(int mode)
        {
            for (int i = 0; i < 8; i++)
            {
                // Raise clock
                setClock(1);
                // Set the data
                setData(mode & 1);
                Delay.usDelay(30);
                // Drop the clock
                setClock(0);
                mode >>= 1;
                Delay.usDelay(30);
            }
        }

        /**
         * Read a data byte from the sensor.
         * The data is read by reading the digital value of the data pin while
         * using the clock pin to request each of the 8 bits.
         * @return The read byte.
         */
        protected int readByte()
        {
            int val = 0;
            for (int i = 0; i < 8; i++)
            {
                setClock(1);
                Delay.usDelay(4);
                val >>= 1;
                if (getData())
                    val |= 0x80;
                setClock(0);
                Delay.usDelay(4);
            }
            return val;
        }

        /**
         * Incrementally calculate the CRC value of the read data.
         * @param crc current crc
         * @param val new value
         * @return new crc
         */
        protected int calcCRC(int crc, int val)
        {
            for (int i = 0; i < 8; i++)
            {
                if (((val ^ crc) & 1) != 0)
                    crc = ((crc >>> 1) ^ 0xa001);
                else
                    crc >>>= 1;
                val >>>= 1;
            }
            return crc & 0xffff;
        }

        /**
         * Read the calibration data from the sensor.
         * This consists of two tables. The first contians 3 rows of data with
         * each row having 4 columns. The data is sent one row at a time. Each
         * row contains a calibration constant for red/green/blue/blank readings.
         * The second table contains 2 threshold values that are used (based on the
         * background light reading) to select the row to use from the first table.
         * Finally there is a CRC value which is used to ensure correct reading
         * of the data.
         * @return true if ok false if error
         */
        protected boolean readCalibration()
        {
            setSensorPinMode(DATA, SensorPort.SP_MODE_INPUT);
            int crcVal = 0x5aa5;
            int input;
            for (int i = 0; i < calData.length; i++)
                for (int col = 0; col < calData[i].length; col++)
                {
                    int val = 0;
                    int shift = 0;
                    for (int k = 0; k < 4; k++)
                    {
                        input = readByte();
                        crcVal = calcCRC(crcVal, input);
                        val |= input << shift;
                        shift += 8;
                    }
                    calData[i][col] = val;
                    //RConsole.println("entry " + i + " col " + col + " value " + val);
                }
            for (int i = 0; i < calLimits.length; i++)
            {
                int val = 0;
                int shift = 0;
                for (int k = 0; k < 2; k++)
                {
                    input = readByte();
                    crcVal = calcCRC(crcVal, input);
                    val |= input << shift;
                    shift += 8;
                }
                //RConsole.println("limit " + i + " value " + val);
                calLimits[i] = val;
            }
            int crc = (short) (readByte() << 8);
            crc += (short) readByte();
            setSensorPinMode(DATA, SensorPort.SP_MODE_ADC);
            Delay.msDelay(1);
            return crc == crcVal;
        }

        /**
         * Initialize the sensor and set the operating mode.
         * @param mode Operating mode.
         * @return true if ok false if error.
         */
        protected boolean initSensor(int mode)
        {
            resetSensor();
            sendMode(mode);
            return readCalibration();
        }

        /**
         * Check to see if a sensor is attached.
         * Read the standard sensor analogue pin to see if a the sensor is
         * present. If it is it will pull this pin down.
         * @return true if sensor is connected false otherwise.
         */
        protected boolean checkPresent()
        {
            int ANAValue = readSensorPin(SensorPort.SP_ANA);
            return (ANAValue <= 50);
        }


        /**
         * Check to see if a sensor is attached and working,
         * Read the standard sensor analogue pin to see if a the sensor is
         * present. If it is it will pull this pin down. If the sensor is
         * detected but it has not been initialized then initialize it.
         * @return true if sensor is connected and working false otherwise.
         */
        protected boolean checkInitialized()
        {
            // is there a sensor attached?
            int ANAValue = readSensorPin(SensorPort.SP_ANA);
            if (ANAValue > 50)
                initialized = false;
            else if (!initialized)
                initialized = initSensor(type);
            return initialized;
        }

        /**
         * Check the state of an initialized sensor.
         * Once initialized this method will check that the sensor is not reporting
         * an error state. The sensor can do this by pulling the clock pin high
         * @return true if ok false if error.
         */
        protected boolean checkSensor()
        {
            setSensorPinMode(CLOCK, SensorPort.SP_MODE_INPUT);
            Delay.msDelay(2);
            if (getSensorPin(CLOCK) != 0)
                initialized = false;
            return initialized;
        }

        /**
         * Read a value from the sensor when in fill color mode.
         * When in full color mode the readings are taken by toggling the clock
         * line to move from one reading to the next. This method performs this
         * operation. It also samples the analogue value twice and returns the
         * average reading.
         * @param newClock New value for the clock pin
         * @return the new reading
         */
        protected int readFullColorValue(int newClock)
        {
            //delayUS(40);
            int val = readSensorPin(DATA);//readData();
            //delayUS(40);
            int val2 = readSensorPin(DATA);//readData();
            //val = (val + readData())/2;
            setClock(newClock);
            return (val + val2) / 2;
        }

        /**
         * Read the device
         * @return true if ok false if error
         */
        protected boolean readSensor()
        {
            if (!checkInitialized())
                return false;
            if (type == TYPE_COLORFULL)
            {
                if (!checkSensor())
                    return false;
                setSensorPinMode(CLOCK, SensorPort.SP_MODE_OUTPUT);
                rawValues[RGB_BLANK] = readFullColorValue(1);
                rawValues[RGB_RED] = readFullColorValue(0);
                rawValues[RGB_GREEN] = readFullColorValue(1);
                rawValues[RGB_BLUE] = readFullColorValue(0);
                return true;
            }
            else
            {
                if (!checkSensor())
                    return false;
                rawValues[type - TYPE_COLORRED] = readData();
                return true;
            }
        }

        /**
         * Return a single raw value from the device.
         * When in single color mode this returns the raw sensor reading.
         * Values range from 0 to 1023 but usually don't get over 600.
         * @return the raw value or < 0 if there is an error.
         */
        @Override
        public int readRawValue()
        {
            if (type < TYPE_COLORRED)
                return -1;
            if (!readSensor())
                return -1;
            return rawValues[type - TYPE_COLORRED];
        }

        /**
         * When in full color mode this returns all four raw color values from the
         * device by doing four very quick reads and flashing all colors.
         * The raw values theoretically range from 0 to 1023 but in practice they usually
         * do not go higher than 600. You can access the index of each color
         * using RGB_RED, RGB_GREEN, RGB_BLUE and RGB_BLANK. e.g. to retrieve the Blue value:
         * <code>vals[ColorLightSensor.RGB_BLUE]</code>
         *
         * @param vals array of four color values.
         * @return < 0 if there is an error the number of values if ok
         */
        @Override
        public int readRawValues(int[] vals)
        {
            if (type != TYPE_COLORFULL)
                return -1;
            if (!readSensor())
                return -1;
            System.arraycopy(rawValues, 0, vals, 0, rawValues.length);
            return rawValues.length;
        }

        /**
         * This method accepts a set of raw values (in full color mode) and processes
         * them using the calibration data to return standard RGB values between 0 and 255
         * @param vals array to return the newly calibrated data.
         */
        protected void calibrate(int[] vals)
        {
            // First select the calibration table to use...
            int calTab;
            int blankVal = rawValues[RGB_BLANK];
            if (blankVal < calLimits[1])
                calTab = 2;
            else if (blankVal < calLimits[0])
                calTab = 1;
            else
                calTab = 0;
            // Now adjust the raw values
            for (int col = RGB_RED; col <= RGB_BLUE; col++)
                if (rawValues[col] > blankVal)
                    vals[col] = ((rawValues[col] - blankVal) * calData[calTab][col]) >>> 16;
                else
                    vals[col] = 0;
            // finally adjust the blank value
            if (blankVal > MINBLANKVAL)
                blankVal -= MINBLANKVAL;
            else
                blankVal = 0;
            blankVal = (blankVal * 100) / (((SENSORMAX - MINBLANKVAL) * 100) / ADMAX);
            vals[RGB_BLANK] = (blankVal * calData[calTab][RGB_BLANK]) >>> 16;
        }

        /**
         * Return a set of calibrated data.
         * If in single color mode the returned data is a simple percentage. If in
         * full color mode the data is a set of calibrated red/blue/green/blank
         * readings that range from 0 to 255. You can access the index of each color
         * using RGB_RED, RGB_GREEN, RGB_BLUE and RGB_BLANK. e.g. to retrieve the Blue value:
         * <code>vals[ColorLightSensor.RGB_BLUE]</code>
         *
         * @param vals 4 element array for the results
         * @return < 0 of error, the number of values if ok
         */
        @Override
        public int readValues(int[] vals)
        {
            if (type != TYPE_COLORFULL)
                return -1;
            if (!readSensor())
                return -1;
            calibrate(vals);
            return RGB_BLANK+1;
        }

        /**
         * Return a single processed value.
         * If in single color mode this returns a single reading as a percentage. If
         * in full color mode it returns a Lego color value that identifies the
         * color of the object in view.
         * @return processed color value.
         */
        @Override
        public int readValue()
        {
            if (!readSensor())
                return -1;
            if (type >= TYPE_COLORRED)
                return (rawValues[type - TYPE_COLORRED] * 100) / SENSORMAX;
            else
            {
                calibrate(values);
                int red = values[RGB_RED];
                int blue = values[RGB_BLUE];
                int green = values[RGB_GREEN];
                int blank = values[RGB_BLANK];
                // we have calibrated values, now use them to determine the color
                if ((red < 55 && green < 55 && blue < 55) ||
                        (blank < 30 && red < 100 && green < 100 && blue < 100))
                    return Colors.BLACK;
                if (red > blue && red > green)
                {
                    // red dominant color
                    if (((blue >> 1) + (blue >> 2) + blue < green) &&
                            (green << 1) + green > red)
                        return Colors.YELLOW;
                    if ((green << 1) < red)
                        return Colors.RED;
                    if (blue < 70 || green < 70 || (blank < 100 && red < 100))
                        return Colors.BLACK;
                    return Colors.WHITE;
                }
                else if (green > blue)
                {
                    // green dominant
                    if ((blue << 1) < red)
                        return Colors.YELLOW;
                    if ((red + (red >> 2) + (red >> 3) < green) ||
                            (blue + (blue >> 2) + (blue >> 3) < green))
                        return Colors.GREEN;
                    if (red < 70 || blue < 70 || (blank < 100 && green < 100))
                        return Colors.BLACK;
                    return Colors.WHITE;
                }
                else
                {
                    // Blue is dominant
                    if ((red + (red >> 3) + (red >> 4) < blue) ||
                            (green + green >> 3) + (green >> 4) < blue)
                        return Colors.BLUE;
                    if (red < 70 || green < 70 || (blank < 100 && blue < 100))
                        return Colors.BLACK;
                    return Colors.WHITE;
                }
            }
        }
    }


    private SensorReader offReader = new SensorReader();
    private SensorReader standardReader = new StandardReader();
    private SensorReader colorReader = null;
    private SensorReader curReader = offReader;


    /**
     * Enable the use of the Color Light Sensor on this port.
     * The code for this sensor is relatively large, so it is not presnt by
     * default. Calling this function will enable this code.
     * NOTE: Calling this function will reset the port. If you are using higher
     * level inetrfaces (like the ColorLightSensor class, then this call will
     * be made automatically.).
     */
    public void enableColorSensor()
    {
        if (colorReader != null) return;
        colorReader = new ColorSensorReader();
        reset();
    }

    protected SensorPort(int aId)
    {
        iPortId = aId;
        reset();
    }


    /**
     * Reset this port and attempt to reset any attached device.
     */
    public void reset()
    {
        // reset all known sensor types
        standardReader.reset();
        if (colorReader != null) colorReader.reset();
        // force re-selection of reader
        type = -1;
        mode = MODE_RAW;
        curReader = offReader;
        setType(TYPE_NO_SENSOR);
    }

    /**
     * Return the ID of the port. One of 0, 1, 2 or 3.
     * @return The Id of this sensor
     */
    public final int getId()
    {
        return iPortId;
    }

    /**
     * Adds a port listener.
     * <p>
     * <b>
     * NOTE 1: You can add at most 8 listeners.<br>
     * NOTE 2: Synchronizing inside listener methods could result
     * in a deadlock.
     * </b>
     * @param aListener Listener for call backs
     * @see lejos.nxt.SensorPortListener
     */
    public synchronized void addSensorPortListener(SensorPortListener aListener)
    {
        if (iListeners == null)
            iListeners = new SensorPortListener[8];
        iListeners[iNumListeners++] = aListener;
        ListenerThread.get().addSensorToMask(iPortId, this);
    }

    /**
     * Activates an RCX sensor. This method should be called
     * if you want to get accurate values from an RCX
     * sensor. In the case of RCX light sensors, you should see
     * the LED go on when you call this method.
     */
    public final void activate()
    {
        setPowerType(1);
    }

    /**
     * Passivates an RCX sensor.
     */
    public final void passivate()
    {
        setPowerType(0);
    }

    /**
     * Returns mode compatible with Lego firmware.
     * @return the current mode
     */
    public int getMode()
    {
        return mode;
    }

    /**
     * Returns type compatible with Lego firmware.
     * @return The type of the sensor
     */
    public int getType()
    {
        return type;
    }

    /**
     * Sets type and mode compatible with Lego firmware.
     * @param type the sensor type
     * @param mode the sensor mode
     */
    public void setTypeAndMode(int type, int mode)
    {
        setType(type);
        setMode(mode);
    }

    /**
     * Sets type compatible with Lego firmware.
     * @param newType the sensor type
     */
    public void setType(int newType)
    {
        if (newType == type) return;
        if (newType < powerType.length)
        {
            // Work out what reader we need for the new type
            SensorReader newReader;
            // Determine what reader to use.
            if (newType >= TYPE_COLORFULL)
                newReader = colorReader;
            else if (newType >= TYPE_SWITCH)
                newReader = standardReader;
            else
                newReader = offReader;
            if (newReader == null)
                newReader = offReader;
            // if we are changing readers tell the old one we are done.
            if (newReader != curReader)
                curReader.setType(TYPE_NO_SENSOR);
            // Set the power and pins for the new type.
            int control = controlPins[newType];
            setPowerType(powerType[newType]);
            // Set the state of the digital I/O pins
            setSensorPinMode(SP_DIGI0, SP_MODE_OUTPUT);
            setSensorPinMode(SP_DIGI1, SP_MODE_OUTPUT);
            if (control == DIGI_UNUSED)
                control = DIGI_OFF;
            setSensorPin(SP_DIGI0, ((control & DIGI_0_ON) != 0 ? 1 : 0));
            setSensorPin(SP_DIGI1, ((control & DIGI_1_ON) != 0 ? 1 : 0));
            // Switch to the new type
            this.type = newType;
            curReader = newReader;
            newReader.setType(newType);
            newReader.setMode(mode);
        }
    }

    /**
     * Sets mode compatible with Lego firmware.
     * @param mode the mode to set.
     */
    public void setMode(int mode)
    {
        this.mode = mode;
        curReader.setMode(mode);
    }
    
    /**
     * Reads the raw value of the sensor.
     * @return the raw sensor value
     */
    public final int readRawValue()
    {
        return curReader.readRawValue();
    }

    /**
     * Returns value compatible with Lego firmware.
     * @return the computed value
     */
    public int readValue()
    {
        return curReader.readValue();
    }

    /**
     * Return a variable number of sensor values
     * @param values An array in which to return the sensor values.
     * @return The number of values returned.
     */
    public int readValues(int[] values)
    {
        return curReader.readValues(values);
    }

    /**
     * Return a variable number of raw sensor values
     * @param values An array in which to return the sensor values.
     * @return The number of values returned.
     */
    public int readRawValues(int[] values)
    {
        return curReader.readRawValues(values);
    }
    
    /**
     * Reads the boolean value of the sensor.
     * @return the boolean state of the sensor
     */
    public final boolean readBooleanValue()
    {
        int rawValue = readRawValue();
        return (rawValue < 600);
    }


    /**
     * <i>Low-level API</i> for reading sensor values.
     * Currently always returns the raw ADC value.
     * @param aPortId Port ID (0..4).
     * @param aRequestType ignored.
     */
    static native int readSensorValue(int aPortId);

    /**
     * Low-level method to set the input power setting for a sensor.
     * Values are: 0 - no power, 1 RCX active power, 2 power always on.
     *
     * @param type Power type to use
     */
    public void setPowerType(int type)
    {
        setPowerTypeById(iPortId, type);
    }

    /**
     * Low-level method to set the input power setting for a sensor.
     * Values are: 0 - no power, 1 RCX active power, 2 power always on.
     **/
    static native void setPowerTypeById(int aPortId, int aPortType);

    /**
     * Call Port Listeners. Used by ListenerThread.
     */
    public synchronized void callListeners()
    {
        int newValue = readSensorValue(iPortId);
        for (int i = 0; i < iNumListeners; i++)
            iListeners[i].stateChanged(this, iPreviousValue, newValue);
        iPreviousValue = newValue;
    }

    /**
     * Low-level method to enable I2C on the port.
     * @param aPortId The port number for this device
     * @param mode I/O mode to use
     */
    public static native void i2cEnableById(int aPortId, int mode);

    /**
     * Low-level method to disable I2C on the port.
     *
     * @param aPortId The port number for this device
     */
    public static native void i2cDisableById(int aPortId);

    /**
     * Low-level method to test if I2C connection is busy.
     * @param aPortId The port number for this device
     * @return > 0 if busy 0 if not
     */
    public static native int i2cBusyById(int aPortId);

    /**
     * Low-level method to start an I2C transaction.
     * @param aPortId The port number for this device
     * @param address The I2C address of the device
     * @param internalAddress The internal address to use for this operation
     * @param numInternalBytes The number of bytes in the internal address
     * @param buffer The buffer for write operations
     * @param numBytes Number of bytes to write or read
     * @param transferType 1==write 0==read
     * @return < 0 if there is an error
     */
    public static native int i2cStartById(int aPortId, int address,
            int internalAddress, int numInternalBytes,
            byte[] buffer, int numBytes, int transferType);

    /**
     * Complete and I2C operation and retrieve any data read.
     * @param aPortId The Port number for the device
     * @param buffer The buffer to be used for read operations
     * @param numBytes Number of bytes to read
     * @return < 0 if the is an error, or number of bytes transferred
     */
    public static native int i2cCompleteById(int aPortId, byte[] buffer, int numBytes);

    /**
     * Low-level method to enable I2C on the port.
     * @param mode The operating mode for the device
     */
    public void i2cEnable(int mode)
    {
        i2cEnableById(iPortId, mode);
    }

    /**
     * Low-level method to disable I2C on the port.
     *
     */
    public void i2cDisable()
    {
        i2cDisableById(iPortId);
    }

    /**
     * Low-level method to test if I2C connection is busy.
     * @return > 0 if the device is busy 0 if it is not
     */
    public int i2cBusy()
    {
        return i2cBusyById(iPortId);
    }

    /**
     * Low-level method to start an I2C transaction.
     * @param address Address of the device
     * @param internalAddress Internal register address for this operation
     * @param numInternalBytes Size of the internal address
     * @param buffer Buffer for write operations
     * @param numBytes Number of bytes to read/write
     * @param transferType 1==write 0 ==read
     * @return < 0 error
     */
    public int i2cStart(int address, int internalAddress,
            int numInternalBytes, byte[] buffer,
            int numBytes, int transferType)
    {

        return i2cStartById(iPortId, address, internalAddress,
                numInternalBytes, buffer,
                numBytes, transferType);
    }

    /**
     * Complete an I2C operation and transfer any read bytes
     * @param buffer Buffer for read data
     * @param numBytes Number of bytes to read
     * @return < 0 error otherwise number of bytes read.
     */
    public int i2cComplete(byte[] buffer, int numBytes)
    {
        return i2cCompleteById(iPortId, buffer, numBytes);
    }

    /**
     * Low level method to set the operating mode for a sensor pin.
     * @param port The port number to use
     * @param pin The pin id
     * @param mode The new mode
     */
    static native void setSensorPinMode(int port, int pin, int mode);

    /**
     * Set the output state of a sensor pin
     * @param port The port to use
     * @param pin The pin id
     * @param val The new output value (0/1)
     */
    static native void setSensorPin(int port, int pin, int val);

    /**
     * Read the current state of a sensor port pin
     * @param port The port to read
     * @param pin The pin id.
     * @return The current pin state (0/1)
     */
    static native int getSensorPin(int port, int pin);

    /**
     * Read the current ADC value from a sensor port pin
     * @param port The port to use.
     * @param pin The id of the pin to read (SP_DIGI1/SP_ANA)
     * @return The return from the ADC
     */
    static native int readSensorPin(int port, int pin);

    /**
     * Low level method to set the operating mode for a sensor pin.
     * @param pin The pin id
     * @param mode The new mode
     */
    public void setSensorPinMode(int pin, int mode)
    {
        setSensorPinMode(iPortId, pin, mode);
    }

    /**
     * Set the output state of a sensor pin
     * @param pin The pin id
     * @param val The new output value (0/1)
     */
    public void setSensorPin(int pin, int val)
    {
        setSensorPin(iPortId, pin, val);
    }

    /**
     * Read the current state of a sensor port pin
     * @param pin The pin id.
     * @return The current pin state (0/1)
     */
    public int getSensorPin(int pin)
    {
        return getSensorPin(iPortId, pin);
    }

    /**
     * Read the current ADC value from a sensor port pin
     * @param pin The id of the pin to read (SP_DIGI1/SP_ANA)
     * @return The return from the ADC
     */
    public int readSensorPin(int pin)
    {
        return readSensorPin(iPortId, pin);
    }
}



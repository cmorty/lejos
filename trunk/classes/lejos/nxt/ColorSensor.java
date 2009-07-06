package lejos.nxt;
/**
 * Lego Color Sensor driver.
 * This driver provides access to the Lego Color sensor. It allows the reading
 * raw and processed color values. The sensor has a tri-color led and this can
 * be set to output red/green/blue or off. It also has a full mode in which
 * four samples are red (off/red/green/blue) very quickly. These samples can
 * then be combined using the claibration data provided by the device to
 * determine the "Lego" color currently being viewed.
 * @author andy
 */
public class ColorSensor implements SensorConstants
{
    /**
     * Sensor types supported by this driver. The type is used to control the
     * operation of the tri color led.
     */
    /* TODO: These should probably go along with the other TYPE_ constants */
    public static final int TYPE_COLORFULL = 13;
    public static final int TYPE_COLORRED = 14;
    public static final int TYPE_COLORGREEN = 15;
    public static final int TYPE_COLORBLUE = 16;
    public static final int TYPE_COLORNONE = 17;

    /**
     * Indexes into the output arrays for specific color values.
     */
    public static final int RGB_RED = 0;
    public static final int RGB_GREEN = 1;
    public static final int RGB_BLUE = 2;
    public static final int RGB_BLANK = 3;

    /**
     * Colors used as the output value when in full mode. Values are
     * compatible with LEgo firmware.
     */
    public static final int BLACKCOLOR = 1;
    public static final int BLUECOLOR = 2;
    public static final int GREENCOLOR = 3;
    public static final int YELLOWCOLOR = 4;
    public static final int REDCOLOR = 5;
    public static final int WHITECOLOR = 6;

    public enum Color {NONE, BLACK, BLUE, GREEN, YELLOW, RED, WHITE};
    protected Color[] colorMap = Color.values();

    // pin usage for clock and data lines.
    protected static final int CLOCK = SensorPort.SP_DIGI0;
    protected static final int DATA = SensorPort.SP_DIGI1;

    protected boolean initialized = false;
    protected int type = TYPE_NO_SENSOR;

    protected SensorPort port;

    // data ranges and limits
    protected static final int ADVOLTS = 3300;
    protected static final int ADMAX = 1023;
    protected static final int MINBLANKVAL = (214/(ADVOLTS/ADMAX));
    protected static final int SENSORMAX = ADMAX;

    protected int [][]calData = new int[3][4];
    protected int []calLimits = new int[2];

    protected int []rawValues = new int[RGB_BLANK+1];
    protected int [] values = new int[RGB_BLANK+1];

    /**
     * Create a new Color Sensor instance and bind it to a port.
     * @param port Port to use for the sensor.
     * @param type Initial operating mode.
     */
    public ColorSensor(SensorPort port, int type)
    {
        this.port = port;
        //port.setTypeAndMode(type, 0);
        port.setTypeAndMode(TYPE_NO_SENSOR, 0);
        initialized = false;
        // initialize the I/O lines
        port.setSensorPin(CLOCK, 1);
        port.setSensorPinMode(CLOCK, SensorPort.SP_MODE_OUTPUT);
        port.setSensorPinMode(DATA, SensorPort.SP_MODE_INPUT);
        setType(type);
        checkSensorPresent();
    }

    /**
     * Wait for the specified number of milliseconds
     * @param ms number of ms to delay
     */
    protected void delayMS(int ms)
    {
        try {
            Thread.sleep(ms);
        } catch (Exception e){}

    }


    /**
     * wait for the specified number of microseconds
     * @param us number of us to delay for
     */
    protected void delayUS(int us)
    {
        long end = System.nanoTime() + us*1000;
        while (System.nanoTime() < end)
        {

        }
    }

    /**
     * initialize the raw and processed RGB values
     */
    protected void initValues()
    {
        for(int i = 0; i < values.length; i++)
        {
            values[i] = 0;
            rawValues[i] = 0;
        }
    }


    /**
     * Change the type of the sensor
     * @param type new sensor type.
     */
    public void setType(int type)
    {
        if (this.type != type)
        {
            this.type = type;
            initialized = false;
            checkSensorPresent();
        }
    }

    /**
     * Set the clock pin to the specified value
     * @param val the new value(0/1) for the pin.
     */
    protected void setClock(int val)
    {
        port.setSensorPin(CLOCK, val);
    }

    /**
     * Set the data pin to the specified value
     * @param val new value(0/1) for the pin.
     */
    protected void setData(int val)
    {
        port.setSensorPin(DATA, val);
    }

    /**
     * get the current digital value from the data pin.
     * @return current pin value
     */
    protected boolean getData()
    {
        return port.getSensorPin(DATA) != 0;
    }

    /**
     * Read the current analogue value from the data pin
     * @return current value of the pin.
     */
    protected int readData()
    {
        return port.readSensorPin(DATA);
    }


    /**
     * perform a reset of the device.
     */
    protected void resetSensor()
    {
        // Set both ports to 1
        setClock(1);
        setData(1);
        port.setSensorPinMode(CLOCK, SensorPort.SP_MODE_OUTPUT);
        port.setSensorPinMode(DATA, SensorPort.SP_MODE_OUTPUT);
        delayMS(1);
        // Take clock down
        setClock(0);
        delayMS(1);
        // Raise it
        setClock(1);
        delayMS(1);
        // Take clock down for 100ms
        setClock(0);
        delayMS(100);
    }

    /**
     * Send the new operating mode to the sensor.
     * The value is sent to the sensor by using the clock pin to clock a series
     * of 8 bits out to the device.
     * @param mode
     */
    protected void sendMode(int mode)
    {
        for(int i = 0; i < 8; i++)
        {
            // Raise clock
            setClock(1);
            // Set the data
            setData(mode & 1);
            delayUS(30);
            // Drop the clock
            setClock(0);
            mode >>= 1;
            delayUS(30);
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
        for(int i = 0; i < 8; i++)
        {
            setClock(1);
            delayUS(4);
            val >>= 1;
            if (getData())
                val |= 0x80;
            setClock(0);
            delayUS(4);
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
        for(int i = 0; i < 8; i++)
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
        port.setSensorPinMode(SensorPort.SP_DIGI1, SensorPort.SP_MODE_INPUT);
        int crcVal = 0x5aa5;
        int input;
        for(int i = 0; i < calData.length; i++)
            for(int col = 0; col < calData[i].length; col++)
            {
                int val = 0;
                int shift = 0;
                for(int k = 0; k < 4; k++)
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
            for(int k = 0; k < 2; k++)
            {
                input = readByte();
                crcVal = calcCRC(crcVal, input);
                val |= input << shift;
                shift += 8;
            }
            //RConsole.println("limit " + i + " value " + val);
            calLimits[i] = val;
        }
        int crc = (short)(readByte() << 8);
        crc += (short)readByte();
        port.setSensorPinMode(SensorPort.SP_DIGI1, SensorPort.SP_MODE_ADC);
        delayMS(1);
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
     * present. If it is it will pull this pin down. If the sensor is
     * detected but it has not been initialized then initialize it.
     * @return true if sensor is connected and working false otherwise.
     */
    protected boolean checkSensorPresent()
    {
        int ANAValue = port.readSensorPin(SensorPort.SP_ANA);
        if (ANAValue > 50)
            initialized = false;
        else if(!initialized)
        {
            initialized = initSensor(type);
        }
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
        port.setSensorPinMode(CLOCK, SensorPort.SP_MODE_INPUT);
        delayMS(2);
        if (port.getSensorPin(CLOCK) != 0)
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
        delayUS(500);
        int val = readData();
        delayUS(500);
        int val2 = readData();
        val = (val + readData())/2;
        setClock(newClock);
        return (val + val2)/2;
    }

    /**
     * Read the device
     * @return true if ok false if error
     */
    protected boolean readSensor()
    {
        if (!checkSensorPresent()) return false;
        if (type == TYPE_COLORFULL)
        {
            if (!checkSensor()) return false;
            port.setSensorPinMode(SensorPort.SP_DIGI0, SensorPort.SP_MODE_OUTPUT);
            rawValues[RGB_BLANK]= readFullColorValue(1);
            rawValues[RGB_RED] = readFullColorValue(0);
            rawValues[RGB_GREEN] = readFullColorValue(1);
            rawValues[RGB_BLUE] = readFullColorValue(0);
            return true;
        }
        else
        {
            if (!checkSensor()) return false;
            rawValues[type - TYPE_COLORRED] = readData();
            return true;
        }
    }

    /**
     * Return a single raw value from the device.
     * When in single color mode this returns the raw sensor reading.
     * @return the raw value or < 0 if there is an error.
     */
    public int readRawValue()
    {
        if (type < TYPE_COLORRED) return -1;
        if (!readSensor()) return -1;
        return rawValues[type - TYPE_COLORRED];
    }

    /**
     * Read raw values
     * When in full color mode this returns all four raw color values from the
     * device.
     * @param vals array of four color values.
     * @return true if ok false if error
     */
    public boolean readRawValues(int [] vals)
    {
        if (type != TYPE_COLORFULL) return false;
        if (!readSensor()) return false;
        System.arraycopy(rawValues, 0, vals, 0, rawValues.length);
        return true;
    }

    /**
     * Take the most recent set of raw values (in full color mode) and process
     * them using the calibration data.
     * @param vals array to return the newly calibrated data.
     */
    protected void calibrate(int []vals)
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
        for(int col = RGB_RED; col <= RGB_BLUE; col++)
        {
            if (rawValues[col] > blankVal)
                vals[col] = ((rawValues[col] - blankVal)*calData[calTab][col]) >>> 16;
            else
                vals[col] = 0;
        }
        // finally adjust the blank value
        if (blankVal > MINBLANKVAL)
            blankVal -= MINBLANKVAL;
        else
            blankVal = 0;
        blankVal = (blankVal*100)/SENSORMAX;
        vals[RGB_BLANK] = (blankVal*calData[calTab][RGB_BLANK]) >>> 16;
    }

    /**
     * Return a set of calibrated data.
     * If in single color mode the returned data is a simple percentage. If in
     * full color mode the data is a set of calibrated red/blue/green/blank
     * readings.
     * @param vals 4 element array for the results
     * @return true if ok false if there is an error.
     */
    public boolean readValues(int [] vals)
    {
        if (type != TYPE_COLORFULL) return false;
        if (!readSensor()) return false;
        calibrate(vals);
        return true;
    }

    /**
     * Return a single processed value.
     * If in single color mode this returns a single reading as a percentage. If
     * in full color mode it returns a Lego color value that identifies the
     * color of the object in view.
     * @return processed color value.
     */
    public int readValue()
    {
        if (!readSensor()) return -1;
        if (type >= TYPE_COLORRED)
           return (rawValues[type - TYPE_COLORRED]*100)/SENSORMAX;
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
                return BLACKCOLOR;
            if (red > blue && red > green)
            {
                // red dominant color
                if (((blue >> 1) + (blue >> 2) + blue < green) &&
                        (green << 1) + green > red)
                    return YELLOWCOLOR;
                if ((green << 1) < red)
                    return REDCOLOR;
                if (blue < 70 || green < 70 || (blank < 100 && red < 100))
                    return BLACKCOLOR;
                return WHITECOLOR;
            }
            else if (green > blue)
            {
                // green dominant
                if ((blue << 1) < red)
                    return YELLOWCOLOR;
                if ((red + (red >> 2) + (red >> 3) < green) ||
                        (blue + (blue >> 2) + (blue >> 3) < green))
                    return GREENCOLOR;
                if (red < 70 || blue < 70 || (blank < 100 && green < 100))
                    return BLACKCOLOR;
                return WHITECOLOR;
            }
            else
            {
                // Blue is dominant
                if ((red + (red >> 3) + (red >> 4) < blue) ||
                        (green + green >> 3) + (green >> 4) < blue)
                    return BLUECOLOR;
                if (red < 70 || green < 70 || (blank < 100 && blue < 100))
                    return BLACKCOLOR;
                return WHITECOLOR;
            }
        }
    }

    /**
     * Read the current color and return an enum value.
     * @return The color under the sensor.
     */
    public Color readColor()
    {
        int col = readValue();
        if (col <= 0) return Color.NONE;
        return colorMap[col];
    }
}


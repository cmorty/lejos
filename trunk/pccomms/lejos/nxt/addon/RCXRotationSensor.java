package lejos.nxt.addon;

import lejos.nxt.SensorConstants;
import lejos.nxt.LegacySensorPort;

/**
 * WARNING: THIS CLASS IS SHARED BETWEEN THE classes AND pccomms PROJECTS.
 * DO NOT EDIT THE VERSION IN pccomms AS IT WILL BE OVERWRITTEN WHEN THE PROJECT IS BUILT.
 * 
 * Provide access to the Lego RCX Rotation Sensor.
 *
 * The sensor records the direction and degree of rotation. A full rotation
 * will result in a count of +/-16. Thus each count is 22.5 degrees.
 *
 * @author andy
 */
public class RCXRotationSensor extends Thread implements SensorConstants
{
    public static final int ONE_ROTATION = 16;
    protected static final int UPDATE_TIME = 2;
    protected LegacySensorPort port;
    protected int count;
    protected final Reader reader;

    /**
     * Create an RCX rotation sensor object attached to the specified port.
     * @param port port, e.g. Port.S1
     */
    public RCXRotationSensor(LegacySensorPort port)
    {
        this.port = port;
        port.setTypeAndMode(TYPE_ANGLE, MODE_RAW);
        reader = new Reader();
        reader.setDaemon(true);
        reader.setPriority(MAX_PRIORITY);
        reader.start();
        count = 0;
    }


    /**
     * Returns the current phase of the sensor.
     *
     * The sensor returns four distinct values read by the ADC port. Each value
     * represents a phase in the rotation. The sequence of the phases is:
     * 0 1 3 2 and 0 2 3 1
     * The transition from one phase to another can be used to identify the
     * direction of rotation.
     * @return the current rotation phase.
     */
    protected int getPhase()
    {
        int val = port.readRawValue();
        if (val < 450) return 0;
        if (val < 675) return 1;
        if (val < 911) return 2;
        return 3;
    }

    /**
     * The following table when indexed by [previous phase][current phase]
     * provides the current direction of rotation. Invalid phase combinations
     * result in zero.
     */
    protected static final int [][]inc =   {{0, 1, -1, 0},
                                            {-1, 0, 0, 1},
                                            {1, 0, 0, -1},
                                            {0, -1, 1, 0}};


    protected class Reader extends Thread
    {
        /**
         * Sensor reader thread.
         * Reads the current phase of the sensor and computes the new count.
         * NOTE: There is a problem with this sensor when a read spans the
         * point at which the sensor output changes from one value to another.
         * The result of this can be a "ghost value". For instance if the read
         * occurs when moving from state 2 to state 0 then a false reading of
         * state 1 may be read. To reduce this problem a new state is not
         * accepted until two consecutive reads return the same state.
         */
        public void run()
        {
            int prev = getPhase();
            int cur1 = prev;
            while (true)
            {
                int cur2 = getPhase();
                if (cur1 == cur2)
                {
                    if (cur2 != prev)
                    {
                        synchronized(this)
                        {
                            count += inc[prev][cur2];
                        }
                        prev = cur2;
                    }
                }
                cur1 = cur2;
                try {Thread.sleep(UPDATE_TIME);}catch(Exception e){}
            }
        }
    }

    /** Returns the current count.
     *
     * @return the current count
     */
    public int getCount()
    {
        return count;
    }

    /**
     * Resets the current count to zero.
     */
    public void resetCount()
    {
        synchronized(reader)
        {
            count = 0;
        }
    }

}
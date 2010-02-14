package lejos.nxt;

/**
 * Utility class for dispatching events to button, sensor and serial listeners.
 *
 * @author Paul Andrews
 */
class ListenerThread extends Thread
{

    static ListenerThread singleton = null;
    private static final int MAX_BUTTONS = 4;
    private static final int MAX_SENSORS = 4;
    private NXTEvent[] events =
    {
        NXTEvent.allocate(NXTEvent.BUTTONS, 0, 10),
        NXTEvent.allocate(NXTEvent.ANALOG_PORTS, 0, 3)
    };
    private ListenerCaller[] buttonCallers = new ListenerCaller[MAX_BUTTONS];
    private ListenerCaller[] sensorCallers = new ListenerCaller[MAX_SENSORS];

    static synchronized ListenerThread get()
    {
        if (singleton == null)
        {
            //System.out.println("Create s/t");
            singleton = new ListenerThread();
            singleton.setDaemon(true);
            singleton.setPriority(Thread.MAX_PRIORITY);
            //singleton.events[1].unregisterEvent();
            singleton.start();
        }
        return singleton;
    }

    void addCaller(int mask, ListenerCaller lc, ListenerCaller[] callers, NXTEvent event)
    {
        //System.out.println("Add listener " + mask);
        int bit = 1;
        for (int i = 0; i < callers.length; i++, bit <<= 1)
            if ((bit & mask) != 0)
            {
                //System.out.println("Set bit " + bit);
                callers[i] = lc;
                event.setFilter(event.getFilter() | bit);
            }
    }

    void addButtonToMask(int id, ListenerCaller lc)
    {
        addCaller(id, lc, buttonCallers, events[0]);
    }

    void addSensorToMask(int id, ListenerCaller lc)
    {
        addCaller(1 << id, lc, sensorCallers, events[1]);
    }

    void callListeners(int events, ListenerCaller[] callers)
    {
        int bit = 1;
        for (int i = 0; i < callers.length; i++, bit <<= 1)
            if ((bit & events) != 0 && callers[i] != null)
                callers[i].callListeners();
    }

    @Override
    public void run()
    {
        for (;;)
        {
            setPriority(Thread.MAX_PRIORITY);
            //System.out.println("before wait " + events[1].getSubType());
            if (NXTEvent.waitEvent(events, NXTEvent.WAIT_FOREVER))
            {
                //System.out.println("after wait");
                // Run events at normal priority so they can use Thread.yield()
                setPriority(Thread.NORM_PRIORITY);
                callListeners(events[0].getEventData(), buttonCallers);
                callListeners(events[1].getEventData(), sensorCallers);
            }
            //else
                //System.out.println("Returns no event");
        }
    }
}

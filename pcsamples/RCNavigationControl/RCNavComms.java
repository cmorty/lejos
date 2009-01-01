

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import lejos.pc.comm.*;

/**
 * Provides  Bluetooth communications services to RCNavitationControl:<br>
 * 1. connect to NXT
 * 2. send commands
 * 3. receives robot position
 * @author Roger
 */
public class RCNavComms
{


    /**
     * constructor establixhes  call back path ot the RCNavigationControl
     * @param control
     */
    public RCNavComms(RCNavigationControl control)
    {
        this.control = control;
        System.out.println(" RCNavComms start");
    }

    /**
     * connects to NXT using Bluetooth
     * @param name of NXT
     * @param address  bluetooth address
     */
    public  boolean connect(String name, String address)
    {

        System.out.println(" connecting to " + name + " " + address);
        connector = new NXTConnector();
        int res =  connector.connectTo(name, address, NXTCommFactory.BLUETOOTH, false);
        System.out.println(" connect result "+res);
       boolean connected = res == 0;
      if(!connected) return connected;
        dataIn = connector.getDataIn();
        dataOut = connector.getDataOut();
        if (dataIn == null)
        {
            connected = false;
            return connected;
        }
        if (!reader.isRunning)
        {
            reader.start();
        }
        return connected;
    }

    /**
     * inner claSS to montior for an incoming message after a command has been sent <br>
     * calls showRobotPosition() on the conrtroller
     */
    class Reader extends Thread
    {
        public boolean reading = false;
        int count = 0;
        boolean isRunning = false;

        public void run()
        {
            isRunning = true;
            while (isRunning)
            {
                if (reading )  //reads one message at a time
                {
                  System.out.println("reading ");
                    float x = 0;
                    float y = 0;
                    float h = 0;
                    boolean ok = false;
                    try
                    {
                        x = dataIn.readFloat();
                        y = dataIn.readFloat();
                        h = dataIn.readFloat();
                        ok = true;
                        System.out.println("data  "+x+" "+y+" "+h);
                    } catch (IOException e)
                    {
                        System.out.println("connection lost");
                        count++;
                        isRunning = count < 20;// give up
                        ok = false;
                    }
                    if (ok)
                    {
                        control.showtRobotPosition(x, y, h);
                        reading = false;
                    }
                    Thread.yield();
                }// if reading
            }//while is running
        }
    }

    /**
     * sends goTo(x,y)  to the NXT
     * @param x
     * @param y
     */
    public void sendGoTo(float x, float y)
    {
        float[] data =
        {
            100 * x, 100 * y
        };  // converts to cm
        send(0, data);// code 0 -> goTo
    }

    /**
     * send travel(distance) command to NXT
     * @param dist
     */
    public void sendTravel(float dist)
    {
        float[] data =
        {
            100 * dist
        };
        send(1, data);//code 1 -> travel
    }

    /**
     * send rotateTo(andle) command to NXT
     * @param angle
     */
    public void sendRotate(float angle)
    {
        float[] data =
        {
            angle
        };
        send(2, data);  // code 2 ->rotate
    }

    public void send(int code, float[] data)
    {
        try
        {
            dataOut.writeInt(code);
            for (int i = 0; i < data.length; i++)
            {
                dataOut.writeFloat(data[i]);
            }
            dataOut.flush();
        } catch (IOException e)
        {
            System.out.println("send problem " + e);
        }
        reader.reading = true;  //reader: listen for response
    }
    /**
     * used by reader
     */
    private DataInputStream dataIn;
    /**
     * used by send()
     */
    private DataOutputStream dataOut;

    private Reader reader = new Reader();
    private NXTConnector connector;
    private RCNavigationControl control;

}

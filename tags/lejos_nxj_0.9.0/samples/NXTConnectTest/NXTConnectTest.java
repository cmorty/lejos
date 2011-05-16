import lejos.nxt.*;
import lejos.nxt.comm.*;
import lejos.util.TextMenu;

import java.io.*;


/**
 * 
 * Test of NXT to NXT Bluetooth/RS485 communications.
 * 
 * Allows the user to choose the connection type and mode.
 * 
 * Connects to another NXT, sends 100 ints, and receives the 
 * replies. Then closes the connection and shuts down.
 * 
 * Works with the NXTReceive sample running on the slave NXT.
 * 
 * Change the name string to the name of your slave NXT. For Bluetooth
 * you need to make sure it is in the known devices list of the master NXT.
 * To do this, turn on the slave NXT and make sure Bluetooth is on and the
 * device is visible. Use the Bluetooth menu on the slave for this. Then,
 * on the master, select the Bluetooth menu and then select Search.
 * The name of the slave NXT should appear. Select Add to add
 * it to the known devices of the master. You can check this has
 * been done by selecting Devices from the Bluetooth menu on the
 * master.
 * 
 * @author Lawrie Griffiths/Andy Shaw
 *
 */
public class NXTConnectTest
{

    public static void main(String[] args) throws Exception
    {
        String name = "NXT";
        String[] connectionStrings = {"Bluetooth", "RS485"};
        TextMenu connectionMenu = new TextMenu(connectionStrings, 1, "Connection");
        String[] modeStrings = {"Packet", "Raw"};
        TextMenu modeMenu = new TextMenu(modeStrings, 1, "Mode");
        NXTCommConnector[] connectors = {Bluetooth.getConnector(), RS485.getConnector()};
        int[] modes = {NXTConnection.PACKET, NXTConnection.RAW};

        int connectionType = connectionMenu.select();
        LCD.clear();
        int mode = modeMenu.select();

        LCD.clear();
        LCD.drawString("Name: " + name, 0, 0);
        LCD.drawString("Type: " + connectionStrings[connectionType], 0, 1);
        LCD.drawString("Mode: " + modeStrings[mode], 0, 2);
        LCD.drawString("Connecting...", 0, 3);

        NXTConnection con = connectors[connectionType].connect(name, modes[mode]);

        if (con == null)
        {
            LCD.drawString("Connect fail", 0, 5);
            Thread.sleep(2000);
            System.exit(1);
        }

        LCD.drawString("Connected       ", 0, 3);
        LCD.refresh();

        DataInputStream dis = con.openDataInputStream();
        DataOutputStream dos = con.openDataOutputStream();

        for (int i = 0; i < 100; i++)
        {
            try
            {
                LCD.drawString("write: ", 0, 6);
                LCD.drawInt(i * 30000, 8, 6, 6);
                dos.writeInt(i * 30000);
                dos.flush();
            }
            catch (IOException ioe)
            {
                LCD.drawString("Write Exception", 0, 5);
            }

            try
            {
                LCD.drawString("Read: ", 0, 7);
                LCD.drawInt(dis.readInt(), 8, 6, 7);
            }
            catch (IOException ioe)
            {
                LCD.drawString("Read Exception ", 0, 5);
            }
        }

        try
        {
            LCD.drawString("Closing...    ", 0, 3);
            dis.close();
            dos.close();
            con.close();
        }
        catch (IOException ioe)
        {
            LCD.drawString("Close Exception", 0, 5);
            LCD.refresh();
        }
        LCD.drawString("Finished        ", 0, 3);
        Thread.sleep(2000);
    }
}

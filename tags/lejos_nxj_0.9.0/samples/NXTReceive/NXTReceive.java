
import lejos.nxt.*;
import lejos.nxt.comm.*;
import lejos.util.TextMenu;

import java.io.*;

/**
 * Receive data from another NXT, a PC, a phone, 
 * or another device. Allow the use of Bluetooth,
 * USB or RS485. Allow either packet based or RAW
 * connections
 * 
 * Waits for a connection, receives an int and returns
 * its negative as a reply, continues until the remote
 * system closes the connection.
 * 
 * @author Andy Shaw
 *
 */
public class NXTReceive
{

    public static void main(String[] args) throws Exception
    {
        String[] connectionStrings = new String[]{"Bluetooth", "USB", "RS485"};
        TextMenu connectionMenu = new TextMenu(connectionStrings, 1, "Connection");
        String[] modeStrings = new String[] {"Packet", "Raw"};
        TextMenu modeMenu = new TextMenu(modeStrings, 1, "Mode");
        NXTCommConnector[] connectors = {Bluetooth.getConnector(), USB.getConnector(), RS485.getConnector()};
        int[] modes = {NXTConnection.PACKET, NXTConnection.RAW};

        int connectionType = connectionMenu.select();
        LCD.clear();
        int mode = modeMenu.select();
        while (true)
        {
            LCD.clear();
            LCD.drawString("Type: " + connectionStrings[connectionType], 0, 0);
            LCD.drawString("Mode: " + modeStrings[mode], 0, 1);
            LCD.drawString("Waiting...", 0, 2);

            NXTConnection con = connectors[connectionType].waitForConnection(0, modes[mode]);

            LCD.drawString("Connected...", 0, 2);

            DataInputStream dis = con.openDataInputStream();
            DataOutputStream dos = con.openDataOutputStream();

            while(true)
            {
                int n;
                try{
                    n = dis.readInt();
                } catch (EOFException e)
                {
                    break;
                }
                LCD.drawString("Read: ", 0, 4);
                LCD.drawInt(n, 7, 6, 4);
                dos.writeInt(-n);
                dos.flush();
            }

            LCD.drawString("Closing...  ", 0, 2);
            dis.close();
            dos.close();
            con.close();
        }
    }
}


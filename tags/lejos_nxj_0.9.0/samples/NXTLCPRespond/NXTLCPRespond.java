
import lejos.nxt.*;
import lejos.nxt.comm.*;
import lejos.util.TextMenu;

/**
 * Create an LCP responder to handle LCP requests. Allow the
 * User to choose between Bluetooth, USB and RS485 protocols.
 * 
 * @author Andy Shaw
 *
 */
public class NXTLCPRespond
{
    /**
     * Our local Responder class so that we can over-ride the standard
     * behaviour. We modify the disconnect action so that the thread will
     * exit.
     */
    static class Responder extends LCPResponder
    {
        Responder(NXTCommConnector con)
        {
            super(con);
        }

        protected void disconnect()
        {
            super.disconnect();
            super.shutdown();
        }
    }

    public static void main(String[] args) throws Exception
    {
        String[] connectionStrings = new String[]{"Bluetooth", "USB", "RS485"};
        TextMenu connectionMenu = new TextMenu(connectionStrings, 1, "Connection");
        NXTCommConnector[] connectors = {Bluetooth.getConnector(), USB.getConnector(), RS485.getConnector()};

        int connectionType = connectionMenu.select();
        LCD.clear();
        LCD.clear();
        LCD.drawString("Type: " + connectionStrings[connectionType], 0, 0);
        LCD.drawString("Running...", 0, 1);
        Responder resp = new Responder(connectors[connectionType]);
        resp.start();
        resp.join();
        LCD.drawString("Closing...  ", 0, 1);
    }
}


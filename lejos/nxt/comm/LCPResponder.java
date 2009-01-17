package lejos.nxt.comm;

/**
 * Support for LCP commands
 * 
 * @author Lawrie Griffiths/Andy Shaw
 *
 */
public class LCPResponder extends Thread {
    protected NXTCommConnector connector = null;
    protected NXTConnection conn = null;

    /**
     * Create a Responder using the provided connector
     * The connector is used to create the listening connection used to accept
     * remote commands.
     * @param connector
     */
    public LCPResponder(NXTCommConnector connector)
    {
        this.connector = connector;
    }

    /**
     * Method called whrn the responder is waiting for a new connection.
     * Default action is to wait for the new connection and return.
     */
    protected void waitConnect()
    {
        while ((conn = connector.waitForConnection(0, NXTConnection.LCP)) == null)
            try{Thread.sleep(500);}catch(Exception e){}
    }
    
    /**
     * Method called to disconnect the reponder connect.
     * Default action is to close the underlying connection object.
     */
    protected void disconnect()
    {
        conn.close();
        conn = null;
    }
    
    /**
     * Method called with a newly read command, before it is processed.
     * Default action is to detect invalid commands and if detected to drop the
     * connection.
     * @param inMsg Newly read command
     * @param len length of the command
     * @return the length of the command
     */
    protected int preCommand(byte[] inMsg, int len)
    {
        if (len < 0) disconnect();
        return len;
    }

    /**
     * Process the actual command
     * Default action is to call the LCP object to emulate the command
     * @param inMsg The command bytes
     * @param len length of the command
     * @param reply bytes to send back in response to the command
     * @return length of the reply
     */
    protected int command(byte[] inMsg, int len, byte[] reply)
    {
        return LCP.emulateCommand(inMsg, len, reply);
    }
    
    protected void postCommand(byte[] inMsg, int inLen, byte[] replyMsg, int replyLen)
    {
        if (inMsg[1] == LCP.NXJ_DISCONNECT)
            disconnect();
    }
    
	public void run() 
	{
		byte[] inMsg = new byte[64];
		byte [] reply = new byte[64];
		int len;
		
		while (true)
		{
            waitConnect();
            while (conn != null)
            {
                len = conn.read(inMsg,64);
                len = preCommand(inMsg, len);
                if (len > 0)
                {
                    int replyLen = command(inMsg,len, reply);
                    if ((inMsg[0] & 0x80) == 0 && replyLen > 0) conn.write(reply, replyLen);
                    postCommand(inMsg, len, reply, replyLen);
                }
                Thread.yield();
            }
		}
	}
}

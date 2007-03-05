package lejos.nxt;

public class Bluetooth {

	public static  final int MSG_BEGIN_INQUIRY = 0;
	public static  final int MSG_CANCEL_INQUIRY = 1;
	public static  final int MSG_CONNECT = 2;
	public static  final int MSG_OPEN_PORT = 3;
	public static  final int MSG_LOOKUP_NAME = 4;
	public static  final int MSG_ADD_DEVICE = 5;
	public static  final int MSG_REMOVE_DEVICE = 6;
	public static  final int MSG_DUMP_LIST = 7;
	public static  final int MSG_CLOSE_CONNECTION = 8;
	public static  final int MSG_ACCEPT_CONNECTION = 9;
	public static  final int MSG_PIN_CODE = 10;
	public static  final int MSG_OPEN_STREAM = 11;
	public static  final int MSG_START_HEART = 12;
	public static  final int MSG_HEARTBEAT = 13;
	public static  final int MSG_INQUIRY_RUNNING = 14;
	public static  final int MSG_INQUIRY_RESULT = 15;
	public static  final int MSG_INQUIRY_STOPPED = 16;
	public static  final int MSG_LOOKUP_NAME_RESULT = 17;
	public static  final int MSG_LOOKUP_NAME_FAILURE = 18;
	public static  final int MSG_CONNECT_RESULT = 19;
	public static  final int MSG_RESET_INDICATION = 20;
	public static  final int MSG_REQUEST_PIN_CODE = 21;
	public static  final int MSG_REQUEST_CONNECTION = 22;
	public static  final int MSG_LIST_RESULT = 23;
	public static  final int MSG_LIST_ITEM = 24;
	public static  final int MSG_LIST_DUMP_STOPPED = 25;
	public static  final int MSG_CLOSE_CONNECTION_RESULT = 26;
	public static  final int MSG_PORT_OPEN_RESULT = 27;
	public static  final int MSG_SET_DISCOVERABLE = 28;
	public static  final int MSG_CLOSE_PORT = 29;
	public static  final int MSG_CLOSE_PORT_RESULT = 30;
	public static  final int MSG_PIN_CODE_ACK = 31;
	public static  final int MSG_DISCOVERABLE_ACK = 32;
	public static  final int MSG_SET_FRIENDLY_NAME = 33;
	public static  final int MSG_SET_FRIENDLY_NAME_ACK = 34;
	public static  final int MSG_GET_LINK_QUALITY = 35;
	public static  final int MSG_LINK_QUALITY_RESULT = 36;
	public static  final int MSG_SET_FACTORY_SETTINGS = 37;
	public static  final int MSG_SET_FACTORY_SETTINGS_ACK = 38;
	public static  final int MSG_GET_LOCAL_ADDR = 39;
	public static  final int MSG_GET_LOCAL_ADDR_RESULT = 40;
	public static  final int MSG_GET_FRIENDLY_NAME = 41;
	public static  final int MSG_GET_DISCOVERABLE = 42;
	public static  final int MSG_GET_PORT_OPEN = 43;
	public static  final int MSG_GET_FRIENDLY_NAME_RESULT = 44;
	public static  final int MSG_GET_DISCOVERABLE_RESULT = 45;
	public static  final int MSG_GET_PORT_OPEN_RESULT = 46;
	public static  final int MSG_GET_VERSION = 47;
	public static  final int MSG_GET_VERSION_RESULT = 48;
	public static  final int MSG_GET_BRICK_STATUSBYTE_RESULT = 49;
	public static  final int MSG_SET_BRICK_STATUSBYTE_RESULT = 50;
	public static  final int MSG_GET_BRICK_STATUSBYTE = 51;
	public static  final int MSG_SET_BRICK_STATUSBYTE = 52;
	
	static byte[] sendBuf = new byte[256];
	static byte[] receiveBuf = new byte[128];
	
	private Bluetooth()
	{	
	}
	
	public static native void btSend(byte[] buf, int len);
	public static native void btReceive(byte[] buf);
	public static native void btSetCmdMode(int mode);
	public static native int btGetCmdMode();
	public static native void btStartADConverter();
	
	public static void sendCommand(byte[] cmd, int len)
	{
		int checkSum = 0;
		
		sendBuf[0] = (byte) (len + 2);
		
		for(int i=0;i<len;i++)
		{
			sendBuf[i+1] = cmd[i];
			checkSum += cmd[i];
		}
		
	    checkSum = -checkSum;
	    sendBuf[len+2] = (byte) ((checkSum >> 8) & 0xff);
	    sendBuf[len+3] = (byte) (checkSum & 0xff);
	    				
		btSend(sendBuf,len+3);
	}
	
	public static int receiveReply(byte[] buf, int bufLen)
	{
		int checkSum, negSum, len;
		btReceive(receiveBuf);
		len = receiveBuf[0];
		buf[0] = (byte) len;
		
		if (len == 0) return 0;
		
		checkSum = len;
		
		if (len-1 <= bufLen)
		{
			for(int i=1;i<len-1;i++) 
			{
				buf[i] = receiveBuf[i];
				checkSum += (buf[i] & 0xff);
			}
			negSum = (receiveBuf[len-1] & 0xff) << 8;
			negSum += (receiveBuf[len] & 0xff);
			if (checkSum + negSum == 65536) return len-1;
			else return 0;
		}
		return 0;
	}
	
	public static int receiveData(byte[] buf, int bufLen)
	{
		int checkSum = 0, negSum, len, skip=0;
		btReceive(receiveBuf);
		len = receiveBuf[0];

		buf[0] = (byte) len;
		buf[1] = 0;
		
		if (len+1 <= bufLen)
		{
			for(int i=1;i<len+1;i++) buf[i+1] = receiveBuf[i+1];			
			return len+2;
		}
		buf[0] = 0;
		return 0;
	}
}

package lejos.pc.comm;

public class NXTCommFactory {
	
    public static final int USB = 1;
    public static final int BLUETOOTH = 2;
	
	public static NXTComm createNXTComm(int protocol) {
		
		String os = System.getProperty("os.name");
    	boolean windows = false;
    	
    	if (os.length() >= 7 && os.substring(0,7).equals("Windows"))
    		windows = true;
    	
		if (protocol == BLUETOOTH) {
			if (windows) return new NXTCommBluecove();
			else return new NXTCommBluez();
		} else return new NXTCommLibnxt();
			
	}

}

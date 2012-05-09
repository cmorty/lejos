package lejos.pc.tools;

public interface ConsoleViewerUI {
	public void append(String value);

    public void updateLCD(byte[] buffer);

	public void setStatus(String msg);
	
	public void logMessage(String msg);
	
	public void connectedTo(String name, String address);
}

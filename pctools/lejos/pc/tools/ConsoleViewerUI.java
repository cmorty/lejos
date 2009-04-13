package lejos.pc.tools;

public interface ConsoleViewerUI {
	public void append(String value);
	
	public void setStatus(String msg);
	
	public void connectedTo(String name, String address);
}

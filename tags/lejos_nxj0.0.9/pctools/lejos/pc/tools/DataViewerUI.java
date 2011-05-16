package lejos.pc.tools;

public interface DataViewerUI {
	public void append(float value);
	
	public void showMessage(String msg);
	
	public void logMessage(String msg);
	
	public void setStatus(String msg);
	
	public void connectedTo(String name, String address);	
}

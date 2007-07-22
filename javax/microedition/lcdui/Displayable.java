package javax.microedition.lcdui;

import lejos.nxt.ArrayList;

/**
 * 
 * @author Andre Nijholt
 */
public class Displayable {
	public static final int KEY_LEFT 	= 37;	// Left key
	public static final int KEY_ENTER 	= 38;	// Up key
	public static final int KEY_RIGHT 	= 39;	// Right key
	public static final int KEY_BACK 	= 40;	// Down key

	private boolean paintRequest;

	protected ArrayList commands = new ArrayList();
	protected CommandListener cmdListener;

	protected String title;
	protected int height;
	protected int width;
	protected boolean shown;
		
	public int getHeight() {
		return height;
	}
	
	public int getTicker() {
		return 0;
	}
	
	public String getTitle() {
		return title;
	}
	
	public int getWidth() {
		return width;
	}
	
	public boolean isShown() {
		return shown;
	}
	
	public void addCommand(Command cmd) {
		commands.add(cmd);
	}

	public void removeCommand(Command cmd) {
		commands.remove(commands.indexOf(cmd));
	}
	
	public void setCommandListener(CommandListener l) {
		cmdListener = l;
	}
	
	protected void callCommandListener() {
		for (int i = 0; (i < commands.size()) && (cmdListener != null); i++) {
			cmdListener.commandAction((Command) commands.get(i), this);
		}
	}
	
	public void setTicker(int ticker) {
		
	}
	
	public void setTitle(String s) {
		this.title = s;
	}
	
	protected void sizeChanged(int w, int h) {
		width = w;
		height = h;
	}
	
	public boolean getPaintRequest() {
		return paintRequest;
	}
	
	protected void repaint() {
		paintRequest = true;
	}
}

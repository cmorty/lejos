package javax.microedition.lcdui;

/**
 * 
 * @author Andre Nijholt
 */
public class Alert extends Screen {
	/** Default command for alert */
	private final Command DISMISS_COMMAND = new Command(0, Command.SCREEN, 0);

	public static final int ALERT_TYPE_INFO			= 0;
	public static final int ALERT_TYPE_WARNING		= 1;
	public static final int ALERT_TYPE_ERROR		= 2;
	public static final int ALERT_TYPE_ALARM		= 3;
	public static final int ALERT_TYPE_CONFIRMATION	= 4;
	
	public static final String STR_CONFIRM = "Yes";
	public static final String STR_DENY = "No";
	
	String text;
	Image image;
	int type;
	int time;
	boolean confirm = false;
	
	public Alert(String title) {
		this.title = title;
		commands.add(DISMISS_COMMAND);
	}

	public Alert(String title, String alertText, Image alertImage, int alertType) {
		this.title = title;
		this.text = alertText;
		this.image = alertImage;
		this.type = alertType;
		commands.add(DISMISS_COMMAND);
	}
	
	public void setType(int alertType) {
		this.type = alertType;
	}
	
	public void setString(String alertText) {
		this.text = alertText;
	}

	public void setTimeout(int time) {
		this.time = time;
	}
	
	public boolean getConfirmation() {
		return confirm;
	}
	
	protected void keyPressed(int keyCode) {
		if ((keyCode == KEY_ENTER) && (cmdListener != null)) {
			cmdListener.commandAction(DISMISS_COMMAND, this);
		} else if (type == ALERT_TYPE_CONFIRMATION) {
			if (keyCode == KEY_LEFT) {
				confirm = false;
			} else if (keyCode == KEY_RIGHT) {
				confirm = true;
			}
		}
	}

	public void paint(Graphics g) {
		g.clear();
		
		// Draw frame with title
		g.drawRoundRect(0, 0, 98, 63, 45, 45);
		g.fillArc(0, 0, 34, 34, 90, 90);
		g.fillArc(64, 0, 34, 34, 0, 90);
		g.fillRect(16, 0, 66, 18);
		g.drawString(title, g.getCenteredX(title), 1, true);
		
		// Draw centered text
		g.drawString(text, g.getCenteredX(text), 3);
		if (type == ALERT_TYPE_CONFIRMATION) {
			g.drawString(confirm ? STR_CONFIRM : STR_DENY, g.getCenteredX(STR_CONFIRM), 4, true);
		}
	}
}

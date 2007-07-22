package javax.microedition.lcdui;

import lejos.nxt.Button;
import lejos.nxt.ButtonListener;

/**
 * 
 * @author Andre Nijholt
 */
public class Display {
	public static final int SCREEN_WIDTH 	= 100;
	public static final int SCREEN_HEIGHT 	= 64;
	public static final int CHAR_WIDTH 		= 6;
	public static final int CHAR_HEIGHT 	= 8;

	private static Display display;
	
	private Screen current;
	private Screen alertBackup;
	
	protected Graphics graphics;
	
	private Display() {
		graphics = new Graphics();
		
	    Button.ENTER.addButtonListener(new ButtonListener() {
	    	public void buttonReleased (Button b) {}
	    	public void buttonPressed (Button b) {
	    		if (current != null) {
	    			if (current instanceof Alert) {
	    				// Hide alert screen and replace backup without notify
	    				current.hideNotify();
	    				current = alertBackup;
	    			} else {
	    				current.keyPressed(Displayable.KEY_ENTER);
	    			}
	    			update();
	    		}
	    	}
	    });
	    Button.ESCAPE.addButtonListener(new ButtonListener() {
	    	public void buttonReleased (Button b) {}
	    	public void buttonPressed (Button b) {
	    		if (current != null) {
	    			current.keyPressed(Displayable.KEY_BACK);
	    			update();
	    		}
	    	}
	    });
	    Button.LEFT.addButtonListener(new ButtonListener() {
	    	public void buttonReleased (Button b) {}
	    	public void buttonPressed (Button b) {
	    		if (current != null) {
	    			current.keyPressed(Displayable.KEY_LEFT);
	    			update();
	    		}
	    	}
	    });
	    Button.RIGHT.addButtonListener(new ButtonListener() {
	    	public void buttonReleased (Button b) {}
	    	public void buttonPressed (Button b) {
	    		if (current != null) {
	    			current.keyPressed(Displayable.KEY_RIGHT);
	    			update();
	    		}
	    	}
	    });

	}
	
	public static Display getDisplay() {
		if (display == null) {
			display = new Display();
		}
		
		return display;
	}
	
	public void setCurrent(Screen nextDisplayable) {
		if (nextDisplayable != null) {
			if (nextDisplayable instanceof Alert) {
				alertBackup = current;
			}
			if (current != null) {
				current.hideNotify();
			}
			current = nextDisplayable;
			current.showNotify();
			current.repaint();
		}
		
		// TODO: Set repaint semaphore?
		update();
	}
	
	public void setCurrent(Alert alert, Screen nextDisplayable) {
		if ((alert != null) && (nextDisplayable != null)) {
			alertBackup = nextDisplayable;
			if (current != null) {
				current.hideNotify();
			}
			current = alert;
			current.showNotify();
			current.repaint();
		}
		
		// TODO: Set repaint semaphore?
		update();
	}
	
	public Displayable getCurrent() {
		return current;
	}
	
	public void update() {
		if (current.getPaintRequest()) {
			current.paint(graphics);
			graphics.refresh();
		}
	}
}

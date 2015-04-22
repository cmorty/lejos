package lejos.nxt;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * Graphical representation of the NXT on the PC, used for emulation of LCD and Button.
 * 
 * @author Lawrie Griffiths
 *
 */
public class NXTFrame extends JFrame  {
	private static final long serialVersionUID = 1L;
	private static int buttonsPressed = 0;
	// Monitor for button presses
	private static Object monitor = new Object();
	private static NXTFrame singleton = null;
	
	//FIXME buttonsPressed is often access outside monitor

	/**
	 * Create the frame
	 */
	public NXTFrame() {
		buildGUI();
	}
	
	/**
	 * Create and lay out the controls
	 */
	private void buildGUI() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Remote NXT");
		JButton enter = new JButton("ENTER");
		JButton escape = new JButton("ESCAPE");
		JButton left = new JButton("<");
		JButton right = new JButton(">");
		
		enter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonsPressed |= Button.ID_ENTER;
				buttonNotify();
			}
		});
		
		escape.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonsPressed |= Button.ID_ESCAPE;
				buttonNotify();
			}
		});
		
		left.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonsPressed |= Button.ID_LEFT;
				buttonNotify();
			}
		});
		
		right.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonsPressed |= Button.ID_RIGHT;
				buttonNotify();
			}
		});
		JPanel content = new JPanel();
		JPanel buttonPanel = new JPanel(new BorderLayout());
		
		buttonPanel.add(enter, BorderLayout.CENTER);
		buttonPanel.add(escape, BorderLayout.SOUTH);
		buttonPanel.add(left, BorderLayout.WEST);
		buttonPanel.add(right, BorderLayout.EAST);

		setSize(250, 300);
	
		LCD lcd = LCD.getSingleton();
        lcd.setMinimumSize(new Dimension(LCD.LCD_WIDTH*2, LCD.LCD_HEIGHT*2));
        lcd.setEnabled(true);
        lcd.setPreferredSize(lcd.getMinimumSize());
        lcd.setBorder(BorderFactory.createEtchedBorder());
		LCD.clear();
		content.add(lcd);
		content.add(buttonPanel);
		
		getContentPane().add(content);
	}
	
	/**
	 * Return the singleton frame.
	 * Create it and set it visible if not already done.
	 * 
	 * @return the NXT frame
	 */
	public static NXTFrame getSingleton() {
		if (singleton == null) {
			singleton = new NXTFrame();
			singleton.setVisible(true);
		}
		return singleton;
	}
	
	/**
	 * Test if a button or set of buttons is pressed
	 * If they are unset them, and return true
	 * 
	 * @param code the button identifier
	 * @return true iff the buttons are pressed
	 */
	public static boolean isPressed(int code) {
		boolean pressed = (buttonsPressed & code) != 0;
		buttonsPressed &= ~code; // unset bits
		// System.out.println("Code:" + code  + ", pressed:" + pressed);
		return pressed;
	}
	
	/**
	 * Get the button mask, which indicates which buttons have been pressed,
	 * but not consumed.
	 * 
	 * @return the button mask
	 */
	public static int getButtons() {
		//System.out.println("getButtons:" + buttonPressed);
		return buttonsPressed;
	}
	
	// Notify all listeners of a button press
	private static void buttonNotify() {
		synchronized(monitor) {
			monitor.notifyAll();
		}
	}
	
	public static int waitForButtons(int timeout) {
		//TODO respect timeout
		synchronized(monitor) {
			try {
				NXTFrame.monitor.wait();
			} catch (InterruptedException e) {
				// Ignore
			}
		}
		//TODO only return the buttons that were pressed since last time
		// not all that are pressed at the moment
		return buttonsPressed;
	}
}

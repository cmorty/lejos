package lejos.nxt;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
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
	private static int buttonPressed = 0;
	public static Object anyButton = new Object();
	private static NXTFrame singleton = null;

	public NXTFrame() {
		buildGUI();
	}
	
	private void buildGUI() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Remote NXT");
		JButton enter = new JButton("ENTER");
		JButton escape = new JButton("ESCAPE");
		JButton left = new JButton("LEFT");
		JButton right = new JButton("RIGHT");
		
		enter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonPressed |= Button.ID_ENTER;
				buttonNotify();
			}
		});
		
		escape.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonPressed |= Button.ID_ESCAPE;
				buttonNotify();
			}
		});
		
		left.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonPressed |= Button.ID_LEFT;
				buttonNotify();
			}
		});
		
		right.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buttonPressed |= Button.ID_RIGHT;
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
	
	public static NXTFrame getSingleton() {
		if (singleton == null) {
			singleton = new NXTFrame();
			singleton.setVisible(true);
		}
		return singleton;
	}
	
	public static boolean isPressed(int code) {
		boolean pressed = (buttonPressed & code) != 0;
		buttonPressed &= ~code; // unset bits
		return pressed;
	}
	
	public static int getButtons() {
		return buttonPressed;
	}
	
	private static void buttonNotify() {
		synchronized(anyButton) {
			anyButton.notifyAll();
		}
	}
}

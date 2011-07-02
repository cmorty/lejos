package lejos.pc.remote;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import lejos.robotics.NavigationModel;

public class NavigationPanel extends JPanel implements MouseListener, MouseMotionListener {
	private static final long serialVersionUID = 1L;
	protected float xOffset = 0f, yOffset = 0f, pixelsPerUnit = 2f;
	protected PCNavigationModel model = new PCNavigationModel(this);
	protected MapPanel mapPanel = new MapPanel(model, new Dimension(600,700), this);
	protected JPanel commandPanel = new JPanel();
	protected JPanel connectPanel = new JPanel();
	protected JPanel statusPanel = new JPanel();
	protected JLabel xLabel = new JLabel("Mouse X:");
	protected JTextField xField = new JTextField(5);
	protected JLabel yLabel = new JLabel("Y:");
	protected JTextField yField = new JTextField(5);
	protected JPanel controlPanel = new JPanel();
	protected JLabel zoomLabel = new JLabel("Zoom:");
	protected JSlider slider = new JSlider(100,500,200);
	protected JLabel gridLabel = new JLabel("Grid:");
	protected JCheckBox gridCheck = new JCheckBox();
	protected JLabel nxtLabel = new JLabel("NXT name:");
	protected JTextField nxtName = new JTextField(10);
	protected JButton connectButton = new JButton("Connect");
	protected boolean showConnectPanel = true, showStatusPanel = true, 
	                  showControlPanel = true, showCommandPanel = true;
	
	public NavigationPanel() {
		buildGUI();
	}
	
	protected void buildGUI() {
		if (showConnectPanel) {
			connectPanel.add(nxtLabel);
			connectPanel.add(nxtName);
			connectPanel.add(connectButton);
			connectPanel.setBorder(BorderFactory.createTitledBorder("Connect"));
			add(connectPanel);
			
			connectButton.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent event) {
					model.connect(nxtName.getText());
				}
			});
		}
		
		if (showControlPanel) {
			controlPanel.add(zoomLabel);
			controlPanel.add(slider);
			controlPanel.add(gridLabel);
			controlPanel.add(gridCheck);
			controlPanel.setBorder(BorderFactory.createTitledBorder("Controls"));
			add(controlPanel);
					
			gridCheck.setSelected(true);
			
			slider.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					JSlider source = (JSlider)e.getSource();
					pixelsPerUnit = source.getValue() / 100f;
					mapPanel.repaint();
				}
			});
			
			gridCheck.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {;
					mapPanel.repaint();
				}
			});	
		}
		
		if (showStatusPanel) {
			statusPanel.add(xLabel);
			statusPanel.add(xField);
			statusPanel.add(yLabel);
			statusPanel.add(yField);
			statusPanel.setBorder(BorderFactory.createTitledBorder("Status"));
			add(statusPanel);
		}
		
		if (showCommandPanel) {
			commandPanel.setBorder(BorderFactory.createTitledBorder("Commands"));
			add(commandPanel);
		}

		mapPanel.addMouseMotionListener(this);
		mapPanel.addMouseListener(this);
		add(mapPanel);		
	}
	
	/**
	 * Print the error message and exit
	 */
	public void fatal(String msg) {
		System.err.println(msg);
		System.exit(1);
	}
  
	/**
	 * Show a pop-up message
	 */
	public void error(String msg) {
		JOptionPane.showMessageDialog(this, msg);
	}
	
	public void setMapSize(Dimension size) {
		mapPanel.setSize(size);
	}
	
	/**
	 * Create a frame to display the panel in
	 */
	public static JFrame openInJFrame(Container content, int width, int height,
                                    String title, Color bgColor) {
		JFrame frame = new JFrame(title);
		frame.setBackground(bgColor);
		content.setBackground(bgColor);
		frame.setSize(width, height);
		frame.setContentPane(content);
		
    	frame.addWindowListener(new WindowAdapter() {
    		public void windowClosing(WindowEvent event) {
    			System.exit(0);
    		}
    	});
    	frame.setVisible(true);
    	return (frame);
	}
  
	public void log(String message) {
		System.out.println(message);
	}

	public void mouseDragged(MouseEvent e) {
	}

	public void mouseMoved(MouseEvent e) {
		xField.setText("" + e.getX());
		yField.setText("" + e.getY());
	}
	
	public void mouseClicked(MouseEvent e) {
		popupMenu(e);
	}

	public void mouseEntered(MouseEvent e) {
		//System.out.println("Mouse entered at " + e);		
	}

	public void mouseExited(MouseEvent e) {
		xField.setText("");
		yField.setText("");
	}

	public void mousePressed(MouseEvent e) {
		//System.out.println("Mouse pressed at " + e);		
	}

	public void mouseReleased(MouseEvent e) {
		//System.out.println("Mouse released at " + e);	
	}
	
	protected void popupMenu(MouseEvent me) {
	}
}

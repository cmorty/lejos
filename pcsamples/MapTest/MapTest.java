import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

import lejos.pc.remote.*;
import lejos.robotics.navigation.Pose;

public class MapTest extends NavigationPanel {
  private static final long serialVersionUID = 1L;

  // GUI Window size
  private static final int FRAME_WIDTH = 1000;
  private static final int FRAME_HEIGHT = 800;
  
  private JLabel xLabel = new JLabel("X:");
  private JTextField xField = new JTextField(6);
  private JLabel yLabel = new JLabel("Y:");
  private JTextField yField = new JTextField(6);
  private JLabel headingLabel = new JLabel("Heading:");
  private JTextField headingField = new JTextField(6);
  private JButton gotoButton = new JButton("Go to");
  private JLabel nxtLabel = new JLabel("NXT name:");
  private JTextField nxtName = new JTextField(10);
  private JButton connectButton = new JButton("Connect");
  private JLabel distanceLabel = new JLabel("Distance:");
  private JTextField distanceField = new JTextField(10);
  private JButton travelButton = new JButton("Travel");
  private JLabel angleLabel = new JLabel("Angle:");
  private JTextField angleField = new JTextField(10);
  private JButton rotateButton = new JButton("Rotate");
  
  /**
   * Create a MapTest object and display it in a GUI frame.
   * Then connect to the NXT.
   */
  public static void main(String[] args) throws Exception {
	  (new MapTest()).run();
  }
  
  public MapTest() {
		connectPanel.add(nxtLabel);
		connectPanel.add(nxtName);
		connectPanel.add(connectButton);
		formPanel.add(xLabel);
		formPanel.add(xField);
		formPanel.add(yLabel);
		formPanel.add(yField);
		formPanel.add(headingLabel);
		formPanel.add(headingField);
		formPanel.add(gotoButton);
		formPanel.add(distanceLabel);
		formPanel.add(distanceField);
		formPanel.add(travelButton);
		formPanel.add(angleLabel);
		formPanel.add(angleField);
		formPanel.add(rotateButton);
		
		gotoButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				try {
					model.goTo(new Pose(Float.parseFloat(xField.getText()), 
						Float.parseFloat(yField.getText()), Float.parseFloat(headingField.getText())));
				} catch (NumberFormatException e) {}
			}
		});
		
		connectButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent event) {
				model.connect(nxtName.getText());
			}
		});
		
		travelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				try {
					model.travel(Float.parseFloat(distanceField.getText()));
				} catch (NumberFormatException e) {}
			}
		});
		
		rotateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				try {
					model.rotate(Float.parseFloat(angleField.getText()));
				} catch (NumberFormatException e) {}
			}
		});
  }
  
  public void run() throws Exception {   
	model.setPanel(this);
	model.loadMap("Room.svg");;
	
    openInJFrame(this, FRAME_WIDTH, FRAME_HEIGHT, "Map Test", Color.white);;
  }
 
}
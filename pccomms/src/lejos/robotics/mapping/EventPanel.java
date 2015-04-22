package lejos.robotics.mapping;

import java.awt.Dimension;
import java.awt.event.*;
import javax.swing.*;
import lejos.robotics.mapping.NavigationModel.NavEvent;
import lejos.robotics.navigation.Pose;
import lejos.robotics.navigation.Waypoint;

/**
 * A panel to send navigation events to a PC running NXTNavigationModel.
 * 
 * @author Lawrie Griffiths
 */
public class EventPanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1L;
	protected JLabel eventLabel = new JLabel("Event:");
	protected JButton sendButton = new JButton("Send");
	protected JLabel label1 = new JLabel();
	protected JLabel label2 = new JLabel();
	protected JLabel label3 = new JLabel();
	protected JTextField param1 = new JTextField(8);
	protected JTextField param2 = new JTextField(8);
	protected JTextField param3 = new JTextField(8);
	protected PCNavigationModel model;
	protected NavEvent[] events = {NavEvent.ADD_WAYPOINT, NavEvent.ARC, NavEvent.CALCULATE_PATH, 
			  NavEvent.CLEAR_PATH, NavEvent.EXIT, NavEvent.FOLLOW_PATH, NavEvent.GET_BATTERY, NavEvent.GET_ESTIMATED_POSE, NavEvent.GET_PARTICLES, NavEvent.GET_POSE, 
			  NavEvent.GET_READINGS, NavEvent.GOTO, NavEvent.LOCALIZE, NavEvent.RANDOM_MOVE, NavEvent.ROTATE, NavEvent.ROTATE_SPEED, NavEvent.ROTATE_TO, NavEvent.SET_POSE, NavEvent.SET_TARGET,
			  NavEvent.SOUND, NavEvent.START_NAVIGATOR, NavEvent.STOP, NavEvent.TAKE_READINGS, NavEvent.TRAVEL, NavEvent.TRAVEL_SPEED};
	protected JComboBox eventCombo = new JComboBox(events);
	protected NavigationPanel panel;
	
	public EventPanel(PCNavigationModel model, NavigationPanel panel, NavEvent[] events) {
		if (events != null) this.events = events;
		this.panel = panel;
		this.model = model;
		
		add(eventLabel);
		add(eventCombo);
		add(label1);
		add(param1);
		add(label2);
		add(param2);
		add(label3);
		add(param3);
		add(sendButton);
		label1.setVisible(false);
		label2.setVisible(false);
		label3.setVisible(false);
		param1.setVisible(false);
		param2.setVisible(false);
		param3.setVisible(false);
		
		sendButton.addActionListener(this);
		eventCombo.addActionListener(this);
		setPreferredSize(new Dimension(650,35));
		actionPerformed(new ActionEvent(eventCombo, 0, null)); // Activate first event
	}

	public void actionPerformed(ActionEvent e) {
		//System.out.println("Source = " + e.getSource());
		if (e.getSource() == eventCombo) {
			NavEvent event = events[eventCombo.getSelectedIndex()];
			//System.out.println(event.name());
			label1.setVisible(false);
			label2.setVisible(false);
			label3.setVisible(false);
			param1.setVisible(false);
			param2.setVisible(false);
			param3.setVisible(false);
			param1.setText("");
			param2.setText("");
			param3.setText("");
			switch (event) {
			case TRAVEL:
				label1.setText("Distance:");
				label1.setVisible(true);
				param1.setVisible(true);
				break;
			case ROTATE:
			case ROTATE_TO:
				label1.setText("Angle:");
				label1.setVisible(true);
				param1.setVisible(true);
				break;
			case ARC:
				label1.setText("Radius:");
				label1.setVisible(true);
				param1.setVisible(true);
				label2.setText("Angle:");
				label2.setVisible(true);
				param2.setVisible(true);
				break;	
			case ADD_WAYPOINT:
			case GOTO:				
			case SET_POSE:
			case SET_TARGET:
				label1.setText("X:");
				label1.setVisible(true);
				param1.setVisible(true);
				label2.setText("Y:");
				label2.setVisible(true);
				param2.setVisible(true);				
				label3.setText("Heading:");
				label3.setVisible(true);
				param3.setVisible(true);
				break;
			case SOUND:
				label1.setText("Code:");
				label1.setVisible(true);
				param1.setVisible(true);
				break;
			case TRAVEL_SPEED:
			case ROTATE_SPEED:
				label1.setText("Speed:");
				label1.setVisible(true);
				param1.setVisible(true);
				break;
			}
			eventCombo.revalidate();
		} else if (e.getSource() == sendButton) {
			NavEvent event = events[eventCombo.getSelectedIndex()];
			try {
				switch (event) {
				case TRAVEL:
					model.travel(Float.parseFloat(param1.getText()));
					break;
				case ROTATE:
					model.rotate(Float.parseFloat(param1.getText()));
					break;
				case ROTATE_TO:
					model.rotateTo(Float.parseFloat(param1.getText()));
					break;
				case ARC:
					model.arc(Float.parseFloat(param1.getText()), Float.parseFloat(param2.getText()));
					break;
				case GOTO:
					if (param3.getText().length() > 0) {
						model.goTo(new Waypoint(Float.parseFloat(param1.getText()), 
							Float.parseFloat(param2.getText()), Float.parseFloat(param3.getText())));
					} else {
						model.goTo(new Waypoint(Float.parseFloat(param1.getText()), 
								Float.parseFloat(param2.getText())));						
					}
					break;
				case ADD_WAYPOINT:
					if (param3.getText().length() > 0) {
						model.addWaypoint(new Waypoint(Float.parseFloat(param1.getText()), 
							Float.parseFloat(param2.getText()), Float.parseFloat(param3.getText())));
					} else {
						model.addWaypoint(new Waypoint(Float.parseFloat(param1.getText()), 
								Float.parseFloat(param2.getText())));
					}
					break;
				case STOP:
					model.stop();
					break;
				case GET_POSE:
					model.getPose();
					break;
				case RANDOM_MOVE:
					model.randomMove();
					break;
				case TAKE_READINGS:
					model.takeReadings();
					break;
				case START_NAVIGATOR:
					model.startNavigator();
					break;
				case CLEAR_PATH:
					model.clearPath();
					break;
				case SET_POSE:
					model.setPose(new Pose(Float.parseFloat(param1.getText()), 
							Float.parseFloat(param2.getText()), Float.parseFloat(param3.getText())));
					panel.repaint();
					break;
				case SET_TARGET:
					if (param3.getText().length() > 0) {
						model.setTarget(new Waypoint(Float.parseFloat(param1.getText()), 
							Float.parseFloat(param2.getText()), Float.parseFloat(param3.getText())));
					} else {
						model.setTarget(new Waypoint(Float.parseFloat(param1.getText()), 
								Float.parseFloat(param2.getText())));
					}
					panel.repaint();
					break;
				case CALCULATE_PATH:
					model.calculatePath();
					panel.repaint();
					break;
				case FOLLOW_PATH:
					model.followPath();
					break;
				case SOUND:
					model.sendSound(Integer.parseInt(param1.getText()));
					break;
				case GET_BATTERY:
					model.getRemoteBattery();
					break;
				case TRAVEL_SPEED:
					model.setTravelSpeed(Float.parseFloat(param1.getText()));
					break;
				case ROTATE_SPEED:
					model.setRotateSpeed(Float.parseFloat(param1.getText()));
					break;
				case EXIT:
					model.sendExit();
					break;
				case LOCALIZE:
					model.localize();
					break;
				case GET_ESTIMATED_POSE:
					model.getEstimatedPose();
					break;
				case GET_PARTICLES:
					model.getRemoteParticles();
					break;
				case GET_READINGS:
					model.getRemoteReadings();
					break;
				}
			} catch (NumberFormatException nfe) {
				panel.error("Invalid parameter");
			}
		}
	}
}

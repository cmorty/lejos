package lejos.robotics.mapping;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import lejos.robotics.mapping.NavigationModel.NavEvent;
import lejos.robotics.navigation.Pose;

public class SliderPanel extends JPanel implements ActionListener, ChangeListener {
	private static final long serialVersionUID = 1L;
	protected PCNavigationModel model;
	protected String sliderLabelText;
	protected NavEvent event;
	protected JButton button;
	protected JLabel label;
	protected JSlider slider;
	
	public SliderPanel(PCNavigationModel model, NavEvent event, String sliderLabel, String buttonLabel, int maxValue) {
		slider  = new JSlider(0,maxValue);
		this.model = model;
		this.event = event;
		sliderLabelText = sliderLabel;
		button = new JButton(buttonLabel);
		button.addActionListener(this);
		label = new JLabel(sliderLabel + " " + maxValue/2);
		slider.addChangeListener(this);
		
		slider.setMajorTickSpacing(maxValue/4);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		
		label.setPreferredSize(new Dimension(100,20));
		
		add(label);
		add(slider);
		add(button);
		setBorder(BorderFactory.createLineBorder(Color.BLACK));
	}
	
	public void setMaxValue(int value) {
		slider.setMaximum(value);
		slider.setMajorTickSpacing(value/4);		
	}

	public void actionPerformed(ActionEvent e) {
		if (event == NavEvent.ROTATE_TO) {
			model.rotateTo(slider.getValue());
		} else if (event == NavEvent.SET_POSE) {
			Pose p = model.getRobotPose();
			model.setPose(new Pose(p.getX(),p.getY(),slider.getValue()));
		} else if (event == NavEvent.TRAVEL_SPEED) {
			model.setTravelSpeed(slider.getValue());
		} else if (event == NavEvent.ROTATE_SPEED) {
			model.setRotateSpeed(slider.getValue());
		}
	}

	public void stateChanged(ChangeEvent e) {
		label.setText(sliderLabelText +  " " + slider.getValue());	
	}
	
	public void setValue(int value) {
		slider.setValue(value);
	}
}

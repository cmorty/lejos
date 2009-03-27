package lejos.pc.tools;

import java.awt.Dimension;
import javax.swing.*;

import javax.swing.BorderFactory;
import javax.swing.JLabel;

public class SensorPanel  extends JPanel {
	private static final long serialVersionUID = 3592127880184905255L;
	LabeledGauge rawGauge, scaledGauge;
	String name;
	JLabel nameLabel;
	JLabel typeLabel = new JLabel("No Sensor");
	
	public SensorPanel(String name) {
		this.name = name;
		nameLabel = new JLabel(name);
		rawGauge = new LabeledGauge("Raw", 1024);
		scaledGauge = new LabeledGauge("Scaled", 100);
		
		add(nameLabel);
		add(rawGauge);
		add(scaledGauge);		
		add(typeLabel);

		Dimension size = new Dimension(120,350);
		setSize(size);
		setMaximumSize(size);
		setPreferredSize(size);
		setBorder(BorderFactory.createEtchedBorder());
	}
	
	public void setRawVal(int val) {
		rawGauge.setVal(val);
	}
	
	public void setRawMaxVal(int val) {
		rawGauge.setMaxVal(val);
	}
	
	public void setScaledVal(int val) {
		scaledGauge.setVal(val);
	}
	
	public void setScaledMaxVal(int val) {
		scaledGauge.setMaxVal(val);
	}
	
	public void setType(String type) {
		typeLabel.setText(type);
	}
}

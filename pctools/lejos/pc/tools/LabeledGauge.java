package lejos.pc.tools;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Panel;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class LabeledGauge  extends Panel {
	private static final long serialVersionUID = -9123004104811474687L;
	Gauge gauge;
	String name;
	JLabel nameLabel;
	
	public LabeledGauge(String name, int maxVal) {
		this.name = name;
		nameLabel = new JLabel(name);
		gauge = new Gauge();
		gauge.setMaxVal(maxVal);

		JPanel p1 = new JPanel();
		p1.add(nameLabel);
		add(gauge,BorderLayout.NORTH);
		add(p1,BorderLayout.SOUTH);
		Dimension size = new Dimension(110,140);
		setSize(size);
		setMaximumSize(size);
		setPreferredSize(size);
	}
	
	public void setVal(int val) {
		gauge.setVal(val);
		gauge.repaint();
	}
	
	public void setMaxVal(int val) {
		gauge.setMaxVal(val);
	}
}
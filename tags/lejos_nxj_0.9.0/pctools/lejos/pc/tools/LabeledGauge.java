package lejos.pc.tools;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * A Swing JPanel that displays a gauge with a label
 * 
 * @author Lawrie Griffiths
 */
public class LabeledGauge extends JPanel {
	private static final long serialVersionUID = -9123004104811474687L;
	private final Dimension SIZE = new Dimension(110,140);
	private Gauge gauge;
	private JLabel nameLabel;
	
	/**
	 * Create the labeled gauge
	 * 
	 * @param name the sensor name
	 * @param maxVal the maximum value for the sensor
	 */
	public LabeledGauge(String name, int maxVal) {
		nameLabel = new JLabel(name);
		gauge = new Gauge();
		gauge.setMaxVal(maxVal);

		JPanel p1 = new JPanel();
		p1.add(nameLabel);
		add(gauge,BorderLayout.NORTH);
		add(p1,BorderLayout.SOUTH);

		setSize(SIZE);
		setMaximumSize(SIZE);
		setPreferredSize(SIZE);
	}
	
	/**
	 * Set the value
	 * 
	 * @param val the value
	 */
	public void setVal(int val) {
		gauge.setVal(val);
		gauge.repaint();
	}
	
	/**
	 * Set the maximum value
	 * 
	 * @param val the maximum value
	 */
	public void setMaxVal(int val) {
		gauge.setMaxVal(val);
	}
}
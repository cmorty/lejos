package lejos.pc.tools;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JComponent;

public class Gauge extends JComponent {
	private static final long serialVersionUID = -4319426278542773674L;
	private int value = 0, MAX_VALUE = 1024;
	private Dimension size;
	private double gaugeWidth, gaugeHeight;
	private int    centerX,  centerY;
	private double zeroAngle = 225.0;
	private double maxAngle  = -45; 
	private double range = zeroAngle - maxAngle;
	private double offsetX, offsetY;
	
	public Gauge() {
		size = new Dimension(100,100);
		gaugeWidth 	= size.width  * 0.8;
		gaugeHeight = size.height * 0.8;
		offsetX = size.width  * 0.1;
		offsetY = size.width  * 0.1;
		centerX = (int) offsetX + (int)(gaugeWidth/2.0);
		centerY = (int) offsetY + (int)(gaugeHeight/2.0);

		setSize(size);
		setMaximumSize(size);
		setPreferredSize(size);
	}
	
	public void setVal( int i ){ value = i; }
	public void setMaxVal( int i) { MAX_VALUE = i; }	
	
	public void paint(Graphics g){
		int x1 = centerX, y1 = centerY,
	    x2 = x1, y2 = y1;
		double angle = zeroAngle - 1.0 * range *( value * 1.0 / MAX_VALUE * 1.0);
		x2 += (int)( Math.cos(Math.toRadians(angle))*(gaugeWidth/2));
		y2 -= (int)( Math.sin(Math.toRadians(angle))*(gaugeHeight/2));

		g.setColor(Color.black);
		g.fillRect(0, 0, size.width, size.height);
		g.setColor(Color.white);
		g.fillOval((int) offsetX, (int) offsetY, (int)gaugeWidth, (int)gaugeHeight);
		g.setColor( Color.blue);
		g.drawArc( (int) offsetX+10, (int) offsetY+10, (int)gaugeWidth-20, (int)gaugeHeight-20, -45, 270);
		g.setColor(Color.red);
		g.drawLine(x1, y1, x2, y2 );
		g.setColor(Color.black);
		g.drawString(""+ value, centerX - 10, centerY + 30);
	}
}


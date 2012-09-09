package lejos.pc.charting;

import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.WindowConstants;


/**
 * Swing application entry point for NXT Charting Logger GUI client
 * 
 * @author Kirk P. Thompson
 */
public class ChartingLogger {
    private LogChartFrame frame;
    private MyWindowListener myWindowListener = new MyWindowListener();
    
    public ChartingLogger() {
        frame = new LogChartFrame();
        Dimension frameSize = frame.getSize();
        // Toolkit.getDefaultToolkit().getScreenSize() doesn't work for multi monitor setups
        Point center = GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint();
        frame.setLocation(center.x - frameSize.width / 2, 
                          center.y - frameSize.height / 2);
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(myWindowListener);
        //frame.setIconImage(Toolkit.getDefaultToolkit().getImage("graph.png"));
        Image appIcon=null;
        try {
            appIcon = new ImageIcon(getClass().getResource("graph.png")).getImage();
        } catch (Exception e){
            appIcon = frame.getIconImage();
        }
        
        frame.setIconImage(appIcon);
        frame.pack();
        frame.setVisible(true);
        frame.requestFocus();
    }
    
    private class MyWindowListener extends WindowAdapter {
        @Override
		public void windowClosed(WindowEvent e) {
            System.out.println("windowClosed");
            System.exit(0);
        }

        @Override
		public void windowClosing(WindowEvent e) {
            System.out.println("windowClosing");
            frame.closeCurrentConnection();
            frame.dispose();
        }
    }
}

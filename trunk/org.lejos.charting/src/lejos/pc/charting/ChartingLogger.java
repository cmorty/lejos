package lejos.pc.charting;

import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;

import javax.swing.JFrame;


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
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(myWindowListener);
        frame.setVisible(true);
    }
    
    private class MyWindowListener extends WindowAdapter {
        public void windowClosed(WindowEvent e) {
            System.out.println("windowClosed");
            System.exit(0);
        }

        public void windowClosing(WindowEvent e) {
            System.out.println("windowClosing");
            frame.closeCurrentConnection();
            frame.dispose();
        }
    }
}

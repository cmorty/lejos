package lejos.pc.charting;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.jfree.chart.ChartFrame;


/** Swing application entry point for NXTChartingLogger client
 * 
 * 
 *  @author Kirk P. Thompson
 */
public class ChartingLogger {
    DataLogger dataLogger;
    private LogChartFrame frame;
    private ChartFrame pOCFrame;
    private MyWindowListener myWindowListener = new MyWindowListener();
    
    public ChartingLogger() {
        frame = new LogChartFrame();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = frame.getSize();
        if (frameSize.height > screenSize.height) {
            frameSize.height = screenSize.height;
        }
        if (frameSize.width > screenSize.width) {
            frameSize.width = screenSize.width;
        }
        frame.setLocation((screenSize.width - frameSize.width) / 2, 
                          (screenSize.height - frameSize.height) / 2);
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

    public static void main(String[] args) {
        try {
//            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(new Runnable(){
            public void run() {
                new ChartingLogger();
            }
        });
        
        //        
    }
}

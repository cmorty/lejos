package lejos.pc.charting;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
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
    private MyWindowSizeListener wsl = new MyWindowSizeListener();
    
    public ChartingLogger() {
        frame = new LogChartFrame(new LoggerComms());
        frame.pack();
        // restore previous window state, size, location if saved
        String[] stateItems = 
        		ConfigurationManager.getConfigItem(ConfigurationManager.CONFIG_WINDOWSTATE, "none").split(",");
        
     // Toolkit.getDefaultToolkit().getScreenSize() doesn't work for multi monitor setups
        Point center = GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint();
        Dimension frameSize = frame.getSize();
        if (stateItems[0].equals("none") || stateItems.length<5) {
	        
	        frame.setLocation(center.x - frameSize.width / 2, 
	                          center.y - frameSize.height / 2);
        } else {
        	int x= Integer.parseInt(stateItems[0]);
        	int y= Integer.parseInt(stateItems[1]);
        	int width= Integer.parseInt(stateItems[2]);
        	int height= Integer.parseInt(stateItems[3]);
        	frame.setBounds(x, y, width, height);
        
        	//  check bounds of saved NCL window and move within current device context if out of bounds
        	boolean inSceenArea=false;
        	for (GraphicsDevice gd : GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()) {
    		  for (GraphicsConfiguration graphicsConfiguration : gd.getConfigurations()) {
    		    if (graphicsConfiguration.getBounds().outcode(x, y)==0 ||
    		    		graphicsConfiguration.getBounds().outcode(x+width, y+height)==0) {
    		    	inSceenArea = true;
    		    }
    		  }
    		}
        	if (!inSceenArea){
		    	frame.setLocation(center.x - frameSize.width / 2, center.y - frameSize.height / 2);
        	}
        	// maximize if flagged
        	if (stateItems[4].equals("Max")) frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        }
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(myWindowListener);
        frame.addComponentListener(wsl);
        
        //frame.setIconImage(Toolkit.getDefaultToolkit().getImage("graph.png"));
        Image appIcon=null;
        try {
            appIcon = new ImageIcon(getClass().getResource("graph.png")).getImage();
        } catch (Exception e){
            appIcon = frame.getIconImage();
        }
        
        frame.setIconImage(appIcon);
//        frame.pack();
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
            String winState="Norm";
            if ((frame.getExtendedState() & Frame.MAXIMIZED_BOTH) ==Frame.MAXIMIZED_BOTH) {
            	winState = "Max";
            }
            winState = wsl.x + "," + wsl.y + "," + wsl.w + "," + wsl.h + "," + winState;
            ConfigurationManager.setConfigItem(ConfigurationManager.CONFIG_WINDOWSTATE, winState);
            if (ConfigurationManager.saveConfig()) {
            	System.out.println("config saved");
            }
            frame.dispose();
        }
    }
    
    private class MyWindowSizeListener extends ComponentAdapter{
    	int x,y,h,w;
    	
    	@Override
		public void componentResized(ComponentEvent e){
    		if ((frame.getExtendedState() & Frame.MAXIMIZED_BOTH) ==Frame.MAXIMIZED_BOTH) return;
    		if ((frame.getExtendedState() & Frame.ICONIFIED) ==Frame.ICONIFIED) return;
    		Component c = e.getComponent();
    		if (c.getHeight() !=this.h || c.getWidth() !=this.w) {
    			this.h = c.getHeight();
    			this.w = c.getWidth();
//    			System.out.println(e.toString());
    		}
    	}
    	
    	@Override
		public void componentMoved(ComponentEvent e){
    		if ((frame.getExtendedState() & Frame.MAXIMIZED_BOTH) ==Frame.MAXIMIZED_BOTH) return;
    		if ((frame.getExtendedState() & Frame.ICONIFIED) ==Frame.ICONIFIED) return;
    		Component c = e.getComponent();
    		if (c.getX()!=this.x || c.getY()!=this.y) {
    			this.x = c.getX();
    			this.y = c.getY();
//    			System.out.println(e.toString());
    		}
    	}
    }
}

package org.lejos.pcsample.navmapcontrol;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;



/**
 To avoid maintaing a data record of robot movements, the path is drawn and recorded on the off screen image. <br>
 the paint() method of an Image is does not clear it (unlike the GUI objects)<br> 
 <br>displays updated image on command <br>
 Image coordinates are in pixels ; public method parameters use grid coordinates <br>
 Uses  Image for drawing updating the robot path.
  by Karthik, modified by R. Glassey Sept 2004
  for mars explorer
*/
//public class OffScreenCanvas extends Canvas 
public class OffScreenDrawingM extends javax.swing.JPanel implements ActionListener
{
/**
 *The robot path is drawn and updated on this object. <br>
 *created by makeImage which is called by paint(); guarantees image always exists before used; 
 */
   Image offScreenImage;  	
/**
 *width of the dawing area;set by makeImage,used by clearImage
 */  
   int imageWidth;
/**
 *height of the dawing are; set by  makeImage,used by clearImage
 */  
	int imageHeight;  	
/**
 * set by paint, clear; used by paint; indicates first plot of robot position 
 */
   boolean first = true;
 /** 
  *the graphics context of the image; set by makeImage, used by all methods that draw on the image
  */
   private Graphics2D osGraphics;
 /**
  *robot position ; used by checkContinuity, drawRobotPath
  */  
   private int robotPrevX = 50;
/**
 * robot position; used by checkContinuity, drawRobotPath
 */
   private int  robotPrevY =400; 
   int gridSpacing = 25;
   	int orig = 75 ;
   	int xOrig;
/**
 * y origin in pixels
 */
   private int y0;
   private int x0;
/**
 * node status - true if blocked; set by drawObstacle, used by drawRobotPath
 */
 
   public TextField textX;
   public TextField textY;
   boolean block = false;
 
   
/**
 *simple constructor
 */
  public  OffScreenDrawingM() 
   {			
      setBackground(Color.white); 
      System.out.println(" Mars map constructor " );
      JButton clear = new JButton("Clear");
      add( clear);      
      clear.addActionListener(this);
      addMouseListener(new MouseHandler());  
   }
   private class MouseHandler extends MouseAdapter
   {
      public void mouseClicked(MouseEvent event)
      {       
         Point p = event.getPoint();
         float  x  =  (float)p.getX();
         x = ( x - xOrig)/(2*gridSpacing);
         textX.setText(x+"");
         float  y =  (float)p.getY();
         y = (y0 - y)/(2*gridSpacing);
         textY.setText(y+"");
         System.out.println(" mouse clicked "+x+" "+y);
      }
   }   
   /**
    *clear the screen and draw a new grid
    */  
       public void clear()
       {
           osGraphics.setColor(getBackground());
           osGraphics.fillRect(0, 0, imageWidth, imageHeight);// clear the image
           drawGrid();
           repaint();
       }   
/**
 * Initialize the off screen canvas<br>
 * Create the offScreenImage, or make a new one if applet size has changed.
*/				   
   public void makeImage() 
   {	
		if (offScreenImage == null || first) 
		{    

	 		imageWidth = getSize().width;
		   imageHeight = getSize().height;
   			y0 = 20+imageHeight-orig ;	
//   			x0 = imageWidth/2;
   			xOrig = imageWidth/2;
		   System.out.println(imageWidth +" "+imageHeight);
		   offScreenImage = createImage(imageWidth, imageHeight);
		   osGraphics = (Graphics2D) offScreenImage.getGraphics(); 
		   osGraphics.setColor(getBackground()); 
		   osGraphics.fillRect(0, 0, imageWidth, imageHeight);// erase everything
		   drawGrid();
      }
   }
 
	public void actionPerformed(ActionEvent e) { clear(); }
/**
 *clear the screen and draw a new grid
 */  

   
/**
 *Copy off screen canvas to the screen.
 **/
   public void paintComponent(Graphics g) 
   {   	
	   	super.paintComponent(g);
	   	if(offScreenImage == null) makeImage();
		g.drawImage(offScreenImage, 0, 0, this);  //Writes the Image to the screen
   }
/**
 *draws the grid with labels
 */
   public void drawGrid()
	{ 
		osGraphics.setColor(Color.green); // Set the line color
		
		int xmax = orig+32*gridSpacing;// pixels
//		int ymax = orig+15*gridSpacing;// pixels
//		for(int y = 0; y < 16; y ++) osGraphics.drawLine(orig,orig+gridSpacing*y,xmax,orig+gridSpacing*y);
		for(float y = 0; y <8; y +=0.5f) 
		  osGraphics.drawLine(xpixel(-8),ypixel(y),xpixel(8),ypixel(y));
//		for (int x=0; x<=32; x++ )osGraphics.drawLine(orig+gridSpacing*x,orig,orig+gridSpacing*x,ymax);
		for (float x=-8; x<8.1; x+=0.5f)
		    osGraphics.drawLine(xpixel(x),ypixel(0),xpixel(x),ypixel(7.5f));
		osGraphics.setColor(Color.black); //set number color 	
	   	for(int y=0; y<8;y++) // for y axis
		osGraphics.drawString(y+"", xpixel(-8.3f),ypixel(y-0.1f));
		for (int x=-8; x<9; x++ ) // x axis
		osGraphics.drawString(x+"", xpixel(x-0.1f),ypixel(-0.4f)); 
//		osGraphics.drawLine(xpixel(0),ypixel(0),xpixel(16),ypixel(7.5f));
		robotPrevX = xpixel(0);
		robotPrevY = ypixel(4);
		System.out.println("x0 " +x0+" "+xOrig+" "+xmax);
		repaint();	    	
   } 

/**
 *Obstacles shown as magenta dot
 */ 	
   public void drawObstacle(float xx, float yy) 
   {
		int x = xpixel(xx); // coordinates of intersection
  		int y = ypixel(yy);
  		block=true;
  		if(x >0 && y > 0 ) 
  		{
			osGraphics.setColor(Color.magenta); 
			osGraphics.fillOval(x-2,y-2,5,5);//bounding rectangle is 10 x 10
   	}
   	repaint();
   }
/**
 *blue line connects current robot position to last position if adjacent to current position
 */  
  public void drawRobotPath(float xx, float yy) 
  {
  		int x = xpixel(xx);
  		int y = ypixel(yy) ; 		 		
  		if(x >0 && y > 0 ) 
  	   	osGraphics.setColor(Color.blue);
		osGraphics.fillOval(x-2,y-2,4,4);  //show robot position
	  	repaint();
	  	osGraphics.drawLine(robotPrevX,robotPrevY,x,y);
	  	robotPrevX = x;
	  	robotPrevY = y;
  }
/**
 *convert grid coordinates to pixels
 */    
  public int xpixel(float x)
  {
  	 return  xOrig+(int)(x*2*gridSpacing);
  }
  public int ypixel(float y)
  {
   return y0-(int)(y*2*gridSpacing);
  }

                     
} 
package org.lejos.pcsample.navmapcontrol;
                                                                                    
import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *Controls a RCNavPapper via bluetooth.  Maps the robot position and obstacles found.
 * Uses RCCommunicator,  enum Command, and  OffScreenDrawingM. Map units are feet
 * assumes robot dimensins are in cm. 
 *
 *

 *R. Glassey  June 16 2011;
 */
public class NavMapControl extends JFrame implements ActionListener, RemoteController
{
   /**  
    * Controls a 
    */
//   private NXTConnector con = new NXTConnector();


  private RCCommunicator com = new RCCommunicator( this );
  private TextField nameField = new TextField(12);

  private TextField angleField = new TextField(4); 
  private TextField distanceField = new TextField(4);
  private TextField xField = new TextField(5);	
  private TextField yField= new TextField(5);
  private TextField headingField = new TextField(5);
  private TextField posetXField = new TextField(5);	
  private TextField poseYField= new TextField(5);
  
  private TextField textStatus=new TextField(35);

   /**
    * off screen canvas:  updated when robot moves
    */
   private OffScreenDrawingM canvas; 
 
   /*
    *used in textStatus (status)
    */
   private String message= "Ready for X and Y ";
   
   JButton connectButton = new JButton("connect");
   JButton stopButton = new JButton("STOP");
   JButton goToButton = new JButton("Go XY");
   JButton setPoseButton  = new JButton("Set pose");
   JButton rotateButton = new JButton("Rotate:");
   JButton travelButton = new JButton("Travel:");
   private String address = "";
   private float  _x,_y,_heading,_angle,_distance, _Rx,_Ry;

   
   /**
    *constructor does the screen layout
    */
   public NavMapControl() 
   {
      buildGUI();
      setVisible(true);
      Thread.yield();

   }
   private void buildGUI()
   {
      setTitle("Navigation  Controller ");
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      int WIDTH = 900;
      int HEIGHT = 750;
      setSize(WIDTH,HEIGHT);
     // build connect panel
      JPanel connectPanel = new JPanel();
      connectPanel.add(new JLabel("NXT name or address"));    
      nameField.setText(address);
      connectPanel.add(nameField);   
      connectPanel.add(connectButton);
      connectButton.addActionListener(this);
      
      JPanel commandPanel = new JPanel(); //upper panel in northPanel
      commandPanel.add(stopButton);
      stopButton.addActionListener(this);
      commandPanel.add(rotateButton);
      rotateButton.addActionListener(this);
      commandPanel.add(new JLabel("angle"));
      commandPanel.add(angleField);
      commandPanel.add(travelButton);
      travelButton.addActionListener(this);
      commandPanel.add(new JLabel("distance"));
      commandPanel.add(distanceField);         
      commandPanel.add(goToButton);
      goToButton.addActionListener(this);
      commandPanel.add(new JLabel("x:"));
      commandPanel.add(xField);
      commandPanel.add(new JLabel ("y:"));
      commandPanel.add(yField);	
      commandPanel.setBorder(BorderFactory.createTitledBorder("Commands"));
      
      JPanel posePanel = new JPanel();
      posePanel.add(new JLabel("x:"));;
      posePanel.add(posetXField);
      posePanel.add(new JLabel("y:"));
      posePanel.add(poseYField);    
      posePanel.add(new JLabel("heading"));
      posePanel.add(headingField); 
      posePanel.add(setPoseButton);
      setPoseButton.addActionListener(this); 
      posePanel.setBorder(BorderFactory.createTitledBorder("Robot Pose"));

      JPanel statusPanel = new JPanel(); //South panel
      statusPanel.add(new JLabel("Status"));
      statusPanel.add(textStatus);
    
      JPanel northPanel = new JPanel();
      northPanel.setLayout(new GridLayout(3,1));
      northPanel.add(connectPanel);
      northPanel.add(commandPanel);
      northPanel.add(posePanel) ; 

      add(northPanel,BorderLayout.NORTH);
      canvas = new OffScreenDrawingM();	
      canvas.textX = this.xField;
      canvas.textY = this.yField;
      add(canvas,BorderLayout.CENTER);	
      
      add(statusPanel,BorderLayout.SOUTH);
   }
   /**
    *creates new GridBotControl, initializes the frame, starts  com
    */
   public static void main(String args[])
   {
      System.out.println("Starting Navigation Control...");
      new NavMapControl();
   }
   /**
    *only 1 button event to monitor
    */
   public void actionPerformed(ActionEvent e)
   {		
      if (e.getSource() == stopButton) com.sendData(NavCommand.STOP.ordinal(),
               0,0,0,true);
      else if(e.getSource() == goToButton){ 
         readXY();
         com.sendData(NavCommand.GOTO.ordinal(),30*_x,30*_y,0,true);
      }
      else if(e.getSource() == setPoseButton) {
         readRXY();
         readHeading(); 
         com.sendData( NavCommand.SETPOSE.ordinal(),30*_Rx,30*_Ry,_heading,true);
   }
      else if (e.getSource() == rotateButton)
      { 
         
         readAngle();
          com.sendData(NavCommand.ROTATE.ordinal(),_angle,0,0,true);
      }
      else if (e.getSource() == travelButton){
         readDistance();
         com.sendData(NavCommand.TRAVEL.ordinal(),30*_distance,0,0,true);
      }
      else if(e.getSource()== connectButton)
      {
         String name = nameField.getText();
         System.out.println(" Connect Button "+name);              
         boolean ok = com.connect(name, address);
         if(!ok)System.out.println("No connection ");
      }
   }
   /**
    *called by actionPerformed; passes x,y data to  com 
    *error checking of x and y inputs <br.
    *calls com.send()  x and y are OK
    */		
   
   public void readAngle()
   {     
      try {_angle = Float.parseFloat(angleField.getText());}
      catch(Exception e)
      {
         message = "Problem  with angle  field";
         System.out.println(e);
      }
   }
     public void readDistance()
   {     
      try {_distance = Float.parseFloat(distanceField.getText());}
      catch(Exception e)
      {
         message = "Problem  with distance  field";
         System.out.println(e);
      }
   }
   public void readXY()
   {
      boolean error = false;
    
      try {_x = Float.parseFloat(xField.getText());	}
      catch(Exception e)
      { 
         message = "Problem with X field";
         error =true;
         System.out.println(e);
      }
      try { _y = Float.parseFloat(yField.getText());}
      catch(Exception e)
      {
         message = "Problem  with Y field";
         error =true;
         System.out.println(e);
      }
      if(error)
      {
         repaint();
         textStatus.setText(message);
         return;  // no send
      }
   }
     public void readRXY()
   {
      boolean error = false;
    
      try {_Rx = Float.parseFloat(posetXField.getText());	}
      catch(Exception e)
      { 
         message = "Problem with Robot X field";
         error =true;
         System.out.println(e);
      }
      try { _Ry = Float.parseFloat(poseYField.getText());}
      catch(Exception e)
      {
         message = "Problem  with Y field";
         error =true;
         System.out.println(e);
      }
      if(error)
      {
         repaint();
         textStatus.setText(message);
         return;  // no send
      }
   }
   public void readHeading()
           {
      try {_heading = Float.parseFloat(headingField.getText());	}
      catch(Exception e)
      { 
         message = "Problem with Heading field";
         System.out.println(e);
      }
   }

 

   /**
    *updates status field display
    */
   public void paintComponent(Graphics g)
   {
      textStatus.setText(message);
   }
    public void setMessage(String msg)	
   {
      textStatus.setText(msg);
      repaint();
//      System.out.println(" message = "+message);
   }
      public void execute(int code, float v0, float v1, float v2, boolean bit)
  {
     
     NavCommand action = NavCommand.values()[code];
             message = "received  code:"+action  +" "+ v0+"  "+v1+" ="+v2+bit;
             System.out.println(message);
              setMessage(message);
              textStatus.setText( message);//inform user /  
              if(action  == NavCommand.POSE)
              {              
                 _Rx = v0/30;
                 _Ry = v1/30;
                 _heading = v2;
                 canvas.drawRobotPath(_Rx,_Ry);
                 posetXField.setText(" "+_Rx);
                 poseYField.setText(" "+_Ry);
                 headingField.setText(" "+_heading);
                  System.out.println(" pose "+_x+" "+_y+" "+_heading);
              }     
              else if(action  == NavCommand.OBSTACLE) 
                 canvas.drawObstacle(v0/30,v1/30);
  }

}

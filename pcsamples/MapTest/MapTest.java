import java.awt.*;
import lejos.pc.remote.MapPanel;
import lejos.pc.remote.MessageElement.ElementType;
import lejos.robotics.mapping.LineMap;
import lejos.robotics.mapping.SVGMapLoader;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

import lejos.pc.remote.*;


public class MapTest extends MapPanel {
  private static LineMap map;
  private static final long serialVersionUID = 1L;

  // GUI Window size
  private static final int FRAME_WIDTH = 800;
  private static final int FRAME_HEIGHT = 800;
	
  public MapTest(LineMap map, float xOffset, float yOffset, float pixelsPerUnit) {
	super(map, xOffset, yOffset, pixelsPerUnit);
  }
  
  /**
   * Create a MapTest object and display it in a GUI frame.
   * Then connect to the NXT.
   */
  public static void main(String[] args) throws Exception {   
	File f = new File(args[0]);
	FileInputStream in = new FileInputStream(f);
	
	map = (new SVGMapLoader(in)).readLineMap();
	
	MessageElement x = new MessageElement("x",ElementType.FLOAT);
	MessageElement y = new MessageElement("y",ElementType.FLOAT);
	MessageElement heading = new MessageElement("heading",ElementType.FLOAT);
	ArrayList<MessageElement> elements = new ArrayList<MessageElement>(); 
	elements.add(x);
	elements.add(y);
	elements.add(heading);
	Message reply = new Message("getPose", elements);
	
	lejos.pc.remote.Command getPose = new lejos.pc.remote.Command((byte) 0, null, reply);
	CommandButton poseButton = new CommandButton("Get Pose", getPose);
	
	
	MapTest mapTest = new MapTest(map, 0,0,2);
	
    openInJFrame(mapTest, FRAME_WIDTH, FRAME_HEIGHT, "Map Test", Color.white);
    
    mapTest.createButton(poseButton);
    //mapTest.connect(args[1]);
  }
}
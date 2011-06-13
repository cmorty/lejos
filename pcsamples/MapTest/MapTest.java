import java.awt.*;
import lejos.pc.remote.MapPanel;
import lejos.robotics.mapping.LineMap;
import lejos.robotics.mapping.SVGMapLoader;
import java.io.*;

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
	
	MapTest mapTest = new MapTest(map, 0,0,2);
	
    openInJFrame(mapTest, FRAME_WIDTH, FRAME_HEIGHT, "Map Test", Color.white);
    
    mapTest.connect(args[1]);
  }
}
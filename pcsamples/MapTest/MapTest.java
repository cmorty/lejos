import java.awt.*;
import lejos.pc.remote.*;

public class MapTest extends NavigationPanel {
  private static final long serialVersionUID = 1L;

  // GUI Window size
  private static final int FRAME_WIDTH = 800;
  private static final int FRAME_HEIGHT = 800;
  
  /**
   * Create a MapTest object and display it in a GUI frame.
   * Then connect to the NXT.
   */
  public static void main(String[] args) throws Exception {
	  (new MapTest()).run();
  }
  
  public void run() throws Exception {   
	model.setPanel(this);
	model.loadMap("Room.svg");;
	
    openInJFrame(this, FRAME_WIDTH, FRAME_HEIGHT, "Map Test", Color.white);
    
    model.connect("NOISY");
  }
 
}
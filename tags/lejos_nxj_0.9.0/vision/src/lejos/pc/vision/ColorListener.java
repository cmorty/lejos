package lejos.pc.vision;

/**
 * Interface for color listeners
 */
public interface ColorListener
{
   /**
    * Triggered when the color is detected in the region
    */
   void colorDetected (int r, int tc);
}


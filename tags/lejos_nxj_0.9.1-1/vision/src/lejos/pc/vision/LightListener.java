package lejos.pc.vision;

/**
 * Interface for light listeners
 */
public interface LightListener
{
   /**
    * Triggered when bright light detected in the region
    */
   void lightDetected (int region);
}


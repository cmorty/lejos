import lejos.nxt.*;
import lejos.robotics.objectdetection.*;

/**
 * This class demonstrates the object detection package. For this example you will need a single ultrasonic
 * sensor plugged into port 4. When you run the program, move the sensor closer and farther from objects. 
 * It will notify the listener and display the range on the LCD. Instructions are provided on the LCD display.
 *  
 * @author BB
 *
 */
public class ObjectDetect implements FeatureListener {

	/**
	 * Maximum distance to report a detected object. Default is 50 cm.
	 */
	public static int MAX_DETECT = 80;
	
	public static void main(String[] args) throws Exception {
		
		// Instructions:
		System.out.println("Autodetect ON");
		System.out.println("Max dist: " + MAX_DETECT);
		System.out.println("ENTER = do scan");
		System.out.println("RIGHT = on/off");
		System.out.println("ESCAPE = exit");
				
		// Initialize the detection objects:
		ObjectDetect listener = new ObjectDetect();
		UltrasonicSensor us = new UltrasonicSensor(SensorPort.S4);
		RangeFeatureDetector fd = new RangeFeatureDetector(us, MAX_DETECT, 500);
		fd.addListener(listener);
		
		Button.setKeyClickVolume(0); // Disable default button sound
		
		// Button inputs:
		while(!Button.ESCAPE.isPressed()) {
			
			// Perform a single scan:
			if(Button.ENTER.isPressed()) {
				Feature res = fd.scan();
				if(res == null) System.out.println("Nothing detected");
				else {
					// This is unorthodox--easier to piggy-back on listener's display code:
					listener.featureDetected(res, fd);
				}
				Thread.sleep(500);
			}
			
			// Enable/disable detection using buttons:
			if(Button.RIGHT.isPressed()) {
				if(fd.isEnabled()) {
					Sound.beepSequence();
					System.out.println("Autodetect OFF");
				} else {
					Sound.beepSequenceUp();
					System.out.println("Autodetect ON");
				}
				fd.enableDetection(!fd.isEnabled());
				Thread.sleep(500);
			}
			Thread.yield();		
		}
	}
	
	/**
	 * Output data about the detected object to LCD.
	 * Plays a tone corresponding to range.
	 */
	public void featureDetected(Feature feature, FeatureDetector detector) {
		int range = (int)feature.getRangeReading().getRange();
		Sound.playTone(1200 - (range * 10), 100);
		System.out.println("Range:" + range);
	}
}

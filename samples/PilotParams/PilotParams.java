import java.io.IOException;

/**
 * This sample creates a property file with parameters for your robot.
 * The property file is used by all the samples that use DifferentialPilot.
 */
import lejos.util.PilotProps;

public class PilotParams {
	
	public static void main(String[] args) throws IOException {
		// Change this to match your robot
		PilotProps.storeProperties(5.6f,16f,"A","C",true);
	}
}

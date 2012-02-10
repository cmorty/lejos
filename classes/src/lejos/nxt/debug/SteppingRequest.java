package lejos.nxt.debug;

/**
 * Class for thread requests (currently only the stepping request is implemented).
 * This is only a tinyVM struckt mapping.
 * @author Felix Treede
 *
 */
class SteppingRequest {
	int stepDepth;
	int method;
	int[] stepPCs;
	
	SteppingRequest(int stepDepth, int method, int[] stepPCs) {
		super();
		this.stepDepth = stepDepth;
		this.method = method;
		this.stepPCs = stepPCs;
	}
}

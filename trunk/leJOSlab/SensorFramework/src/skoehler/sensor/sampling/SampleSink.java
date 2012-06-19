package skoehler.sensor.sampling;

import skoehler.sensor.api.VectorData;

public class SampleSink implements VectorData {

	//TODO implement
	
	/* This class will spawn a Thread and will drain a chain of filters originating at a
	 * SamplingThread. It will always provide the last samples computed of that chain.
	 * Dataflow will be like this:
	 * Accelerometer -> SamplingThread -> AveragingFilter -> SampleSink -> user application 
	 */

	public int getQuantity() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getAxisCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void fetchSamples(float[] dst, int off) {
		// TODO Auto-generated method stub
		
	}
	
}

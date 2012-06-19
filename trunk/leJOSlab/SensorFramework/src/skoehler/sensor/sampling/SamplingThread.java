package skoehler.sensor.sampling;

import skoehler.sensor.api.VectorData;

public class SamplingThread implements VectorData {

	//TODO implement
	
	/* This class will spawn a Thread. The purpose of the Thread is to provide a stream
	 * of samples in taken regular intervals. This a requirement, in order to actually use filters
	 * like the AveragingFilter and other IIR and FIR filter, as their frequency response is
	 * only defined if the samples are equally spaced in time.
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

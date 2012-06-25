package skoehler.sensor.examples;

import skoehler.sensor.api.VectorData;
import skoehler.sensor.device.DummySensor2;
import skoehler.sensor.filter.AveragingFilter;
import skoehler.sensor.filter.OmissionFilter;
import skoehler.sensor.sampling.SamplingThread;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// create a sensor
		VectorData x1 = new DummySensor2();
		// select the sample rate (one sample each 100ms = 10Hz)
		VectorData x2 = new SamplingThread(x1, 10, 100);
		// average the samples with a sliding window of 40 samples
		VectorData x3 = new AveragingFilter(x2, 40);
		// since we averaging is a low-pass filter, we can now safely omit 2 of 3 samples with (almost) no aliasing
		VectorData x4 = new OmissionFilter(x3, 2);
		
		// samples should drop out of x4 roughly every 300ms (3.33Hz)
		float[] buf = new float[1];
		while (true)
		{
			x4.fetchSamples(buf, 0);
			System.out.println(buf[0]);
		}
	}

}

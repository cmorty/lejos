package lejos.nxt.sensor.sensor;

import lejos.nxt.sensor.api.SampleProvider;

public class DummySensor implements SampleProvider{

	public int getMinimumFetchInterval() {
		return 4;
	}

	public float fetchSample() {
		return (float)Math.random()*15.0f;
	}

}

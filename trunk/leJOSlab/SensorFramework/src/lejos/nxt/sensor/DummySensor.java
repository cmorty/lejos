package lejos.nxt.sensor;

public class DummySensor implements SensorDataProvider{

	public int getMinimumFetchInterval() {
		return 4;
	}

	public float fetchData() {
		return (float)Math.random()*15.0f;
	}

}

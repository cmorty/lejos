package lejos.nxt.sensor;

public class DummySensor implements SensorDataProvider{

	public int getRefreshRate() {
		// TODO Auto-generated method stub
		return 4;
	}

	public float fetchData() {
		// TODO Auto-generated method stub
		return (float)Math.random()*15.0f;
	}

}

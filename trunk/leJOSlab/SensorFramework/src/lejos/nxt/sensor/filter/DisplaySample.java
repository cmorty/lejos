package lejos.nxt.sensor.filter;

import lejos.nxt.LCD;

import lejos.nxt.sensor.api.SampleProvider;

public class DisplaySample extends AbstractFilter {
	int x=0,y=0;

	public DisplaySample(SampleProvider source) {
		super(source);
	}
	public DisplaySample(SampleProvider source,int x) {
		super(source);
		this.x=x;
	}
	public DisplaySample(SampleProvider source,int x,int y) {
		super(source);
		this.x=x;
		this.y=y;
	}

	public void fetchSample(float[] dst, int off) {
		source.fetchSample(dst, off);
		for (int i=0;i<elements;i++)
			LCD.drawString(fmt(dst[i+off]), x+5*(i%3),y+ i/3);
	}
	

}

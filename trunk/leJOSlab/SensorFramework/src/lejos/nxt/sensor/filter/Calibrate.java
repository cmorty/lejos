package lejos.nxt.sensor.filter;

import lejos.nxt.Button;
import lejos.nxt.sensor.api.*;

/**
 * This filter applies calibration on a sample based on a stored calibration set
 * @author Aswin
 *
 */
public class Calibrate extends AbstractFilter{
	float[] offset, scale;

	public Calibrate(SampleProvider source, String calibrationSet) {
		super(source);
		CalibrationManager calMan=new CalibrationManager();
		if (calMan.setCurrent(calibrationSet)==false) throw new IllegalArgumentException("No such calibration set");
		if (calMan.getElements()!=elements) throw new IllegalArgumentException("Invalid calibration set");
		offset=calMan.getOffset();
		scale=calMan.getScale();
	}



	public void fetchSample(float[] dst, int off) {
	source.fetchSample(dst, off);
	for (int i=0;i<elements;i++)
		dst[i+off]=(dst[i+off]-offset[i])/scale[i];
	}

}

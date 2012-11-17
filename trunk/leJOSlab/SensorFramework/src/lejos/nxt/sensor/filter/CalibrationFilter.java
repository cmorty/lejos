package lejos.nxt.sensor.filter;

import java.io.IOException;

import lejos.nxt.Button;
import lejos.nxt.sensor.api.*;

/**
 * This filter applies calibration on a sample based on a stored set of calibration parameters
 * @author Aswin
 *
 */
public class CalibrationFilter extends AbstractFilter{
	float[] offset, scale;

	public CalibrationFilter(SampleProvider source, String calibrationSet) {
		super(source);
		float[] defaultOffset=new float[elements];
		float[] defaultScale=new float[elements];
		for (int i=0;i<elements;i++) {
			defaultOffset[i]=0;
			defaultScale[i]=1;
		}
		
		FilterProperties props=this.getFilterProperties();
		try{
			props.load(calibrationSet);
			offset=props.getPropertyArray("offset",defaultOffset);
			scale=props.getPropertyArray("scale",defaultScale);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}



	public void fetchSample(float[] dst, int off) {
	source.fetchSample(dst, off);
	for (int i=0;i<elements;i++)
		dst[i+off]=(dst[i+off]-offset[i])/scale[i];
	}

}

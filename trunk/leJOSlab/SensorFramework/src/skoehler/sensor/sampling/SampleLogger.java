package skoehler.sensor.sampling;

import lejos.util.LogColumn;
import lejos.util.NXTDataLogger;
import skoehler.sensor.api.VectorData;
import skoehler.sensor.filter.AbstractFilter;

/**
 * Sampler class that outputs sample data to the NXT charting logger.
 * 
 * @author Aswin
 *
 */
public class SampleLogger extends AbstractFilter{
	int axisCount=0;
	NXTDataLogger log;

	/**
	 * Constructor of the SampleLogger
	 * @param source
	 * The source of sample data of type VectorData
	 * @param log
	 * The identifier of the NXT Charting Logger instance
	 * @param legend
	 * The description of the line(s) in the NXT Charting logger. 
	 * If there are multiple axes then the descriptions will have their axis number appended
	 */
	public SampleLogger(VectorData source, NXTDataLogger log, String legend) {
		super(source);
		LogColumn col;
		axisCount=source.getAxisCount();
		this.log=log;
		if (axisCount==1) {
			col=new LogColumn(legend,LogColumn.DT_FLOAT);
			log.appendColumn(col);
		}
		else for (int axis=0;axis<axisCount;axis++) {
			col=new LogColumn(legend + " " + axis,LogColumn.DT_FLOAT);
			log.appendColumn(col);
		}
	}

	@Override
	public int getAxisCount() {
		return axisCount;
	}

	/** 
	 * This method fetches a sample from the source and writes its value(s) to the log
	 */
	@Override
	public void fetchSample(float[] dst, int off) {
		source.fetchSample(dst, off);
		for (int axis=0;axis<axisCount;axis++) {
			log.writeLog(dst[off+axis]);
		}
		
	}
	
	

}

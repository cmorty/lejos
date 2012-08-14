package lejos.nxt.sensor.filter;

import lejos.nxt.sensor.api.SampleProvider;
import lejos.util.LogColumn;
import lejos.util.NXTDataLogger;

/**
 * Sampler class that outputs sample data to the NXT charting logger.
 * 
 * @author Aswin
 * 
 */
public class SampleLogger extends AbstractFilter {
	NXTDataLogger		log;
	private boolean	finishLine	= false;

	/**
	 * Constructor of the SampleLogger
	 * 
	 * @param source
	 *          The source of sample data of type VectorData
	 * @param log
	 *          The identifier of the NXT Charting Logger instance
	 * @param legend
	 *          The description of the line(s) in the NXT Charting logger. If
	 *          there are multiple axes then the descriptions will have their axis
	 *          number appended
	 */
	public SampleLogger(SampleProvider source, NXTDataLogger log, String legend) {
		super(source);
		LogColumn col;
		this.log = log;
		if (log != null) {
			if (elements == 1) {
				col = new LogColumn(legend, LogColumn.DT_FLOAT);
				log.appendColumn(col);
			}
			else
				for (int i = 0; i < elements; i++) {
					col = new LogColumn(legend + " " + i, LogColumn.DT_FLOAT);
					log.appendColumn(col);
				}
		}
	}

	public SampleLogger(SampleProvider source, NXTDataLogger log, String legend, boolean finishLine) {
		this(source, log, legend);
		this.finishLine = finishLine;
	}

	/**
	 * This method fetches a sample from the source and writes its value(s) to the
	 * log
	 */
	public void fetchSample(float[] dst, int off) {
		source.fetchSample(dst, off);
		if (log != null) {
			for (int i = 0; i < elements; i++) {
				log.writeLog(dst[off + i]);
			}
			if (finishLine)
				log.finishLine();
		}
	}

}

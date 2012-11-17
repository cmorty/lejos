package lejos.nxt.sensor.filter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.StringTokenizer;

import lejos.nxt.sensor.api.*;

/**
 * Common base for SampleProvider implementations
 * 
 * @author Kirk P. Thompson
 *
 */
public abstract class AbstractFilter implements SampleProvider{
	protected final SampleProvider source;
	protected int elements;
	private float[] buf;
	private FilterProperties filterProperties=null;

	/**
	 * Create a filter passing a source to be decorated
	 * @param source The source sensor/filter to be used
	 */
	public AbstractFilter(SampleProvider source){
		this.source = source;
		elements=source.getElementsCount();
	}
	
	public int getQuantity() {
		return source.getQuantity();
	}

	public  int getElementsCount() {
		return elements;
	}

	public float fetchSample() {
		if (buf==null)
			buf=new float[elements];
		fetchSample(buf,0);
		return buf[0];
	}
	
	/**
	 * Utility method to format floats to 4 characters, used for testing
	 * @param in
	 * @return
	 * Formatted float value
	 */
	protected String fmt(float in) {
		//TODO: remove method 
		String tmp=Float.toString(in)+"00000";
		return tmp.substring(0, 4);
	}
	
	/**
	 * Returns an instance of a properties file optimized for use in filters. 
	 * All properties are assumed to be floats or arrays of floats. <p>
	 * Filter properties are stored on the NXT file system as Filter.properties. 
	 * @return
	 * Instance of a filter properties class.
	 */
	@SuppressWarnings("synthetic-access")
	public FilterProperties getFilterProperties() {
		if (filterProperties == null) {
			try {
				filterProperties=new FProperties();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		return filterProperties;
	}
	
	 /**
	 * Wrapper for properties class.<p>
	 * <ul>
	 * This class is different from the properties class in two aspects:
	 * <li>The properties are grouped logically, a group is identified by a name. 
	 * This allows this class to store groups of properties belonging to one (instance of) a filter in a single file.
	 * Grouping is done by concatenating the group name to the key.
	 * </li>
	 * <li>
	 * The property values are cast to floats or arrays of floats as it is assumed that filters work with float values.
	 * </li>
	 * </ul>
	 * <p>
	 * <font color="FF0000">Nb: This class cannot handle two instances storing properties! This will lead to unpredictable results.</font> <p>
	 * @author Aswin
	 *
	 */
	private class FProperties implements FilterProperties{
		private static final String PROPFILE="Filter.properties";
		private final Properties props= new Properties();
		private String prefix;
		private String raw;
		
		private FProperties() throws IOException {
			// create an empty filter properties file if it does not exist
			File propFile=new File(PROPFILE);
			if (!propFile.exists()) propFile.createNewFile();
		}

		
		public void load(String name) throws IOException {
			this.prefix=name+".";
			FileInputStream in = new FileInputStream(new File(PROPFILE));
			props.load(in);
			in.close();
		}
		
		public float getProperty(String key) {
			return getProperty(key, Float.NaN);
		}

		public float getProperty(String key, float defaultValue) {
			raw=props.getProperty(prefix+key);
			if (raw==null) 
				return defaultValue;
			return Float.parseFloat(raw);
		}

		public float[] getPropertyArray(String key) {
			return getPropertyArray(key, new float[0]);
		}

		public float[] getPropertyArray(String key, float[] defaultValues) {
			raw=props.getProperty(prefix+key);
			if (raw==null) return defaultValues;
			StringTokenizer tokenizer= new StringTokenizer(raw);
			int n=tokenizer.countTokens();
			float[] values=new float[n];
			for (int i=0;i<n;i++) {
				values[i]=Float.parseFloat(tokenizer.nextToken());
			}
			return values;
		}

		public void setProperty(String key, float value) {
			props.setProperty(prefix+key, Float.toString(value));
		}

		public void setPropertyArray(String key, float[] values) {
			StringBuilder builder = new StringBuilder();
			int n =values.length;
			for (int i =0;i<n;i++) {
				if (i!=0) builder.append(',');
				builder.append(values[i]);
			}
			props.setProperty(prefix+key, builder.toString());
		}

		public void store() throws IOException {
			FileOutputStream out = new FileOutputStream(new File(PROPFILE));
			props.store(out,"FilterProperties");
			out.close();
		}
	}
	
}

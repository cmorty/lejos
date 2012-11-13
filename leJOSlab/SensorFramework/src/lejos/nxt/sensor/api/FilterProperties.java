package lejos.nxt.sensor.api;

import java.io.IOException;

/**
 * Interface to filter properties. <p>
 * Filter properties are grouped by the name provided during load. Only one group of properties can be loaded at the same time.
 * As filters work with float values all filterproperties are float by definition.
 * @author Aswin
 *
 */
public interface FilterProperties {
	
	/**
	 * Returns the value of a filter property
	 * @param key
	 * @return
	 * Float.NaN if the filter property does not exist
	 */
	public float getProperty(String key);
	
	/**
	 * Returns the value of a filter property, or a supplied default value if the filter property dioes not exist.
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public float getProperty(String key, float defaultValue);
	
	/**
	 * Returns an array of values of a filter property
	 * @param key
	 * @return
	 * If the filter property does not exist a null will be returned
	 */
	public float[] getPropertyArray(String key);
	
	/** 
	 * Returns an array of values of a filter property or the supplied default array if it does not exist
	 * @param key
	 * @param defaultValues
	 * @return
	 */
	public float[] getPropertyArray(String key, float[] defaultValues);
	
	/**
	 * Loads the filter properties file and sets the group name
	 * @param name
	 * @throws IOException
	 * when loading the file fails
	 */
	public void load(String name) throws IOException;
	
	/**
	 * Sets a filter property to the specified value
	 * @param key
	 * @param value
	 */
	public void setProperty(String key, float value);
	
	/**
	 * Sets a filter property to the specified array
	 * @param key
	 * @param values
	 */
	public void setPropertyArray(String key, float[] values);
	
	/** Saves the filter properties to the file system
	 * @throws IOException
	 */
	public void store() throws IOException;
}

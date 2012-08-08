package lejos.nxt.sensor.filter;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * The CalibrationManager manages parmanent calibration settings for sensors.
 * <P>
 * The CalibrationManager handles multiple sets of calibration settings called calibration sets.
 * A calibration set is identified by its name and contains values for offset and scale correction. Where trueValue=(measuredValue-offset)/scale. A calibration set can hold multiple offset and scale values to support more complex sensors.
 * The calibration manager stores calibration sets to file and retreives these from file. This makes it possible to hold and reuse calibration values over time. 
 * <p>
 * A calibration set is created using the add(name, numberOfElements) method. 
 * Then one gets the offset array and/or the scale array of the new set using the getOffset() and getScale() methods. Changes made to
 * these arrays are stored to file using the save() method. Delete() cleans up old calibration sets and frees memory.
 * 
 * @author Aswin
 * 
 */
public class CalibrationManager {
	private static CalibrationSet[]	calibrationSets;
	static int											sets			= -1;
	static String										FILENAME	= "Calibrate.dat";
	private static File							store			= new File(FILENAME);

	private CalibrationSet					current		= null;

	/**
	 * load all stored calirations from file to memory
	 */
	public CalibrationManager() {
		if (sets == -1) {
			FileInputStream in = null;
			sets = 0;
			if (store.exists()) {
				try {
					in = new FileInputStream(store);
					DataInputStream din = new DataInputStream(in);
					try {
						sets = din.readInt();
						calibrationSets = new CalibrationSet[sets];
						for (int i = 0; i < sets; i++) {
							calibrationSets[i] = new CalibrationSet(din);
						}
						din.close();
					}
					catch (IOException e) {
						e.printStackTrace();
					}
				}
				catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
			else {
				calibrationSets = new CalibrationSet[sets];
			}
				
		}
	}

	/**
	 * Sets a current calibration (offset and scale can only be retreived from the
	 * current calibration) to:
	 * 
	 * @param name
	 *          The name of the current calibration
	 */
	public boolean setCurrent(String name) {
		current = find(name);
		if (current == null)
			return false;
		return true;
	}

	/**
	 * Returns the name of the current calibration
	 * 
	 * @return The name
	 */
	public String getCurrent() {
		return current.name;
	}

	/**
	 * Returns the array of offset values
	 * 
	 * @return type float[]
	 */
	public float[] getOffset() {
		return current.offset;
	}

	/**
	 * Returns the array off scale values
	 * 
	 * @return type float[]
	 * 
	 */
	public float[] getScale() {
		return current.scale;
	}
	
	public int getElements() {
		return current.elements;
	}

	/**
	 * Saves all changes made to calibration(s) to file
	 */
	public void save() {
		if (store.exists())
			store.delete();
		FileOutputStream out;
		try {
			out = new FileOutputStream(store);
			DataOutputStream dataOut = new DataOutputStream(out);
			try {
				dataOut.writeInt(sets);
				for (int i = 0; i < sets; i++) {
					calibrationSets[i].save(dataOut);
				}
				dataOut.close();
				out.close();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Creates a new calibration file.
	 * <p>
	 * Name and number of elements can not be changed afterwards
	 * 
	 * @param name
	 *          Name of the calibration. Must be unique.
	 * @param elements
	 *          Nuumber of offset and scale values for the calibratio
	 * @return methods returns a false when there already exists a calibration
	 *         under this name. Otherwise true
	 */
	public boolean add(String name, int elements) {
		CalibrationSet existing = find(name);
		if (existing != null )
			return false;
		CalibrationSet set = new CalibrationSet(name, elements);
		CalibrationSet[] s = new CalibrationSet[sets + 1];
		if (sets>0) System.arraycopy(calibrationSets, 0, s, 0, sets);
		sets++;
		s[sets-1]=set;
		calibrationSets = s;
		current = set;
		return true;
	}

	/**
	 * Deletes the current calibration
	 * 
	 * @return False if nu current calibration is set.
	 */
	public boolean delete() {
		if (current == null)
			return false;
		int found = 0;
		for (int i = 0; i < sets-1 ; i++) {
			if (calibrationSets[i] == current)
				found = 1;
			calibrationSets[i] = calibrationSets[i + found];
		}
		sets--;
		calibrationSets[sets]=null;
		return true;
	}

	/**
	 * Finds a calibration by name
	 * 
	 * @param name
	 * @return Instance of the calibration or Null if not found.
	 */
	private CalibrationSet find(String name) {
		for (int i = 0; i < sets; i++) {
			if (name.equals(calibrationSets[i].name))
				return calibrationSets[i];
		}
		return null;
	}

	public String[] getNames() {
		String a[]=new String[sets];
		for (int i=0;i<sets;i++) a[i]=calibrationSets[i].name;
		return a;
	}
	
	public void print() {
		for (int i=0;i<sets;i++) 
			calibrationSets[i].print();
	}
	
	/**
	 * Internal class that holds one calibration
	 * 
	 * @author Aswin
	 * 
	 */
	private class CalibrationSet {
		String	name;
		int			elements;
		float[]	offset;
		float[]	scale;

		/**
		 * Loads the calibration from file
		 * 
		 * @param din
		 */
		CalibrationSet(DataInputStream din) {
			try {
				name = din.readUTF();
				elements = din.readInt();
				offset = new float[elements];
				scale = new float[elements];

				for (int i = 0; i < elements; i++) {
					offset[i] = din.readFloat();
					scale[i] = din.readFloat();
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}

		CalibrationSet(String name, int elements) {
			this.name = name;
			this.elements = elements;
			offset = new float[elements];
			scale = new float[elements];
			for (int i = 0; i < elements; i++)
				scale[i] = 1;
		}

		/**
		 * Writes the calibration to file
		 * 
		 * @param dataOut
		 */
		void save(DataOutputStream dataOut) {
			try {
				dataOut.writeUTF(name);
				dataOut.writeInt(elements);
				for (int i = 0; i < elements; i++) {
					dataOut.writeFloat(offset[i]);
					dataOut.writeFloat(scale[i]);
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		void print() {
			System.out.println(name);
			for (int i = 0; i < elements; i++) {
				System.out.print(fmt(offset[i]));
				System.out.print(" ");
				System.out.println(fmt(scale[i]));
			}
		}
		
		private String fmt(float in) {
			String tmp=Float.toString(in)+"00000";
			return tmp.substring(0, 4);
		}
	}

}

package lejos.pc.comm;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * 
 * Creates a version of NTComm appropriate to the OS in use and protocol
 * (Bluetooth or USB) that is requested.
 * 
 */
public class NXTCommFactory {

	public static final int USB = 1;
	public static final int BLUETOOTH = 2;
	public static final int ALL_PROTOCOLS = USB | BLUETOOTH;

	private static String os = System.getProperty("os.name");
	private static final String SEP = System.getProperty("file.separator");
	private static final String USER_HOME = System.getProperty("user.home");
	private static String propFile;
	private static String cacheFile = USER_HOME + SEP + "nxj.cache";

	/**
	 * Load a comms driver for a protocol (USB or Bluetooth)
	 * 
	 * @param protocol
	 *            the protocol
	 * 
	 * @return a driver that supports the nxtComm interface
	 * @throws NXTCommException
	 */
	public static NXTComm createNXTComm(int protocol) throws NXTCommException {
		boolean fantom = false;
		Properties props = getNXJProperties();

		if ((os.length() >= 7 && os.substring(0, 7).equals("Windows"))||(os.toLowerCase().startsWith("mac os x"))) {
			fantom = true;
		}

		// Look for USB comms driver first
		if ((protocol & NXTCommFactory.USB) != 0) {
			String nxtCommName = props.getProperty("NXTCommUSB",
					(fantom ? "lejos.pc.comm.NXTCommFantom"
							: "lejos.pc.comm.NXTCommLibnxt"));
			try {
				Class<?> c = Class.forName(nxtCommName);
				return (NXTComm) c.newInstance();
			} catch (Throwable t) {
				throw new NXTCommException("Cannot load USB driver");
			}
		}

		// Look for a Bluetooth one
		String defaultDriver = "lejos.pc.comm.NXTCommBluecove";

		if ((protocol & NXTCommFactory.BLUETOOTH) != 0) {
			String nxtCommName = props.getProperty("NXTCommBluetooth",
					defaultDriver);
			try {
				Class<?> c = Class.forName(nxtCommName);
				return (NXTComm) c.newInstance();
			} catch (Throwable t) {
				throw new NXTCommException("Cannot load Bluetooth driver");
			}
		}
		return null;
	}

	/**
	 * Form the leJOS NXJ properties file name Get NXJ_HOME from a system
	 * property, if set, else the environment variable,
	 */
	private static void setPropsFile() {
		String home = System.getProperty("nxj.home");

		// try environment variable if system property not set
		if (home == null) {
			home = System.getenv("NXJ_HOME");
		}
		if (home != null) {
			propFile = home + SEP + "bin" + SEP + "nxj.properties";
		}
	}

	/**
	 * Load the leJOS NXJ properties
	 * 
	 * @return the Properties object
	 * @throws NXTCommException
	 */
	public static Properties getNXJProperties() throws NXTCommException {
		Properties props = new Properties();
		setPropsFile();

		if (propFile != null) {
			try {
				props.load(new FileInputStream(propFile));
			} catch (FileNotFoundException e) {
			} catch (IOException e) {
				throw new NXTCommException("Cannot read nxj.properties file");
			}
		}
		return props;
	}
	
	/**
	 * Load the Bluetooth name cache as properties
	 * 
	 * @return the Properties object
	 * @throws NXTCommException
	 */
	public static Properties getNXJCache() throws NXTCommException {
		Properties props = new Properties();

		try {
			props.load(new FileInputStream(cacheFile));
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
			throw new NXTCommException("Cannot read nxj.cache file");
		}
		return props;
	}

	/**
	 * Save the leJOS NXJ Properties
	 * 
	 * @param props
	 *            the complete set of properties
	 * @param comment
	 *            a comment that is written to the file
	 * @throws IOException
	 */
	public static void saveNXJProperties(Properties props, String comment)
			throws IOException {
		FileOutputStream fos;
		setPropsFile();
		fos = new FileOutputStream(propFile);
		props.store(fos, comment);
	}
	
	/**
	 * Save the leJOS NXJ Properties
	 * 
	 * @param props
	 *            the complete set of properties
	 * @param comment
	 *            a comment that is written to the file
	 * @throws IOException
	 */
	public static void saveNXJCache(Properties props, String comment)
			throws IOException {
		FileOutputStream fos;
		fos = new FileOutputStream(cacheFile);
		props.store(fos, comment);
	}
}

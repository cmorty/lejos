package lejos.pc.comm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import lejos.internal.jni.JNIClass;
import lejos.internal.jni.JNILoader;
import lejos.internal.jni.OSInfo;

/**
 * 
 * Creates a version of <code>{@link NXTComm}</code> appropriate to the OS in use and protocol
 * (Bluetooth or USB) that is requested.
 *  
 */
public class NXTCommFactory {

    /** Use USB for the connection
     */
    public static final int USB = 1;
    /** Use Bluetooth for the connection
     */
	public static final int BLUETOOTH = 2;
    /** Use either USB or Bluetooth for the connection. Normally, USB is tested first since it fails faster.
     */
	public static final int ALL_PROTOCOLS = USB | BLUETOOTH;

	private static final OSInfo osinfo;
	private static final JNILoader jniloader;
	
	static {
		try {
			osinfo = new OSInfo();
			jniloader = new JNILoader("native", osinfo);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}		
	}

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
		Properties props = getNXJProperties();
		
		String nxtCommName;
		switch (protocol)
		{
			case NXTCommFactory.USB:
			{
				boolean fantom = osinfo.isOS(OSInfo.OS_WINDOWS) || osinfo.isOS(OSInfo.OS_MACOSX);
				String defaultName = fantom ? "lejos.pc.comm.NXTCommFantom" : "lejos.pc.comm.NXTCommLibnxt";
				nxtCommName = props.getProperty("NXTCommUSB", defaultName);				
				break;
			}
			case NXTCommFactory.BLUETOOTH:
			{
				// Look for a Bluetooth one
				String defaultName = isAndroid() ? "lejos.pc.comm.NXTCommAndroid" : "lejos.pc.comm.NXTCommBluecove";
				nxtCommName = props.getProperty("NXTCommBluetooth", defaultName);
				break;
			}
			default:
				throw new NXTCommException("unknown protocol");
		}

		return newNXTCommInstance(nxtCommName);
	}
	
	private static NXTComm newNXTCommInstance(String classname) throws NXTCommException
	{
		try
		{
			Class<?> c = Class.forName(classname);
			Object o = c.newInstance();
			
			if (o instanceof JNIClass)
			{
				((JNIClass) o).initialize(jniloader);
			}
			
			return (NXTComm) o;
		}
		catch (Exception e)
		{
			throw new NXTCommException("Cannot load NXTComm driver", e);
		}
	}

	/**
	 * Form the leJOS NXJ properties file name Get NXJ_HOME from a system
	 * property, if set, else the environment variable,
	 */
	private static String getPropsFile() {
		String home = SystemContext.getNxjHome();
		if (home == null)
			return null;
		
		return home + File.separatorChar + "bin" + File.separatorChar + "nxj.properties";
	}

	private static String getCacheFile() {
		if (isAndroid())
			return "sdcard/leJOS/nxj.cache";
		
		String userHome = System.getProperty("user.home");
		if (userHome == null)
			return null;
		
		return userHome + File.separatorChar + "nxj.cache";
	}

	/**
	 * Load the leJOS NXJ properties
	 * 
	 * @return the Properties object
	 * @throws NXTCommException
	 */
	public static Properties getNXJProperties() throws NXTCommException {
		Properties props = new Properties();
		String propFile = getPropsFile();

		if (propFile != null) {
			try {
				FileInputStream fis = new FileInputStream(propFile);
				try {
					props.load(fis);
				} finally {
					fis.close();
				}
			} catch (FileNotFoundException e) {
				//ignore
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
		String cacheFile = getCacheFile();

		if (cacheFile != null)
		{
			try {
				FileInputStream fis = new FileInputStream(cacheFile); 
				try {
					props.load(fis);	
				} finally {
					fis.close();
				}
			} catch (FileNotFoundException e) {
				//ignore
			} catch (IOException e) {
				throw new NXTCommException("Cannot read nxj.cache file");
			}
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
		String propFile = getPropsFile();
		
		if (propFile != null)
		{
			FileOutputStream fos = new FileOutputStream(propFile);
			try {
				props.store(fos, comment);
			} finally {
				fos.close();
			}
		}
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
		String cacheFile = getCacheFile();
		
		if (cacheFile != null)
		{
			FileOutputStream fos = new FileOutputStream(cacheFile);
			try	{
				props.store(fos, comment);
			} finally {
				fos.close();
			}
		}
	}

	private static boolean isAndroid() {
		String javaRuntimeName = System.getProperty("java.runtime.name");
		return osinfo.isOS(OSInfo.OS_LINUX) && javaRuntimeName != null &&
			javaRuntimeName.toLowerCase().indexOf("android runtime") != -1;
	}
}

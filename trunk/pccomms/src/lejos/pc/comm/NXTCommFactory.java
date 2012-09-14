package lejos.pc.comm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import lejos.internal.config.ConfigManager;
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

    private static final String ANDROID_CACHEFILE = "sdcard/leJOS NXJ/nxj.cache";
	/** Use USB for the connection
     */
    public static final int USB = 1;
    /** Use Bluetooth for the connection
     */
	public static final int BLUETOOTH = 2;
    /** Use either USB or Bluetooth for the connection. Normally, USB is tested first since it fails faster.
     */
	public static final int ALL_PROTOCOLS = USB | BLUETOOTH;

	// initialized lazy, use methods below
	// lazy initialization fixed bug on OS X with JDK 1.5
	private static OSInfo osinfo;
	private static JNILoader jniloader;
	
	private static synchronized OSInfo getOSInfo() throws IOException
	{
		if (osinfo == null)
			osinfo = new OSInfo();
		return osinfo;
	}
	private static synchronized JNILoader getJNILoader() throws IOException
	{ 
		OSInfo osi = getOSInfo();
		if (jniloader == null)
			jniloader = new JNILoader("native", osi);
		return jniloader;
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
		
		JNILoader jnil;
		try	{
			jnil = getJNILoader();
		} catch (IOException e) {
			throw new NXTCommException(e);
		}
		OSInfo osi = jnil.getOSInfo();
		
		String nxtCommName;
		switch (protocol)
		{
			case NXTCommFactory.USB:
			{
				boolean fantom = osi.isOS(OSInfo.OS_WINDOWS) || osi.isOS(OSInfo.OS_MACOSX);
				String defaultName = fantom ? "lejos.pc.comm.NXTCommFantom" : "lejos.pc.comm.NXTCommLibnxt";
				nxtCommName = props.getProperty("NXTCommUSB", defaultName);				
				break;
			}
			case NXTCommFactory.BLUETOOTH:
			{
				// Look for a Bluetooth one
				String defaultName = isAndroid(osi) ? "lejos.pc.comm.NXTCommAndroid" : "lejos.pc.comm.NXTCommBluecove";
				nxtCommName = props.getProperty("NXTCommBluetooth", defaultName);
				break;
			}
			default:
				throw new NXTCommException("unknown protocol");
		}

		return newNXTCommInstance(nxtCommName, jnil);
	}
	
	private static NXTComm newNXTCommInstance(String classname, JNILoader jnil) throws NXTCommException
	{
		try
		{
			Class<?> c = Class.forName(classname);
			Object o = c.newInstance();
			
			if (o instanceof JNIClass)
			{
				((JNIClass) o).initialize(jnil);
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
	 * Load the Bluetooth name cache as properties
	 * 
	 * @return the Properties object
	 * @throws NXTCommException
	 */
	public static Properties getNXJCache() throws NXTCommException {
		OSInfo osi;
		try	{
			osi = getOSInfo();
		} catch (IOException e) {
			throw new NXTCommException("failed to create OSInfo", e);
		}
		
		Properties props = new Properties();
		try {
			if (isAndroid(osi)) {
				FileInputStream fis = new FileInputStream(ANDROID_CACHEFILE); 
				try {
					props.load(fis);	
				} finally {
					fis.close();
				}
			} else {
				ConfigManager.loadPropertiesFile(ConfigManager.CONFIG_BTCACHE, props);
			}
		} catch (IOException e) {
			throw new NXTCommException("Cannot read nxj.cache file: " + e.getMessage(), e);
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
	public static void saveNXJCache(Properties props, String comment) throws IOException {
		OSInfo osi = getOSInfo();
		if (isAndroid(osi)) {
			FileOutputStream fos = new FileOutputStream(ANDROID_CACHEFILE);
			try	{
				if (props != null) props.store(fos, comment);
			} finally {
				fos.close();
			}
		} else {
			ConfigManager.savePropertiesFile(ConfigManager.CONFIG_BTCACHE, props);
		}
	}

	private static boolean isAndroid(OSInfo osi) {
		String javaRuntimeName = System.getProperty("java.runtime.name");
		return osi.isOS(OSInfo.OS_LINUX) && javaRuntimeName != null &&
			javaRuntimeName.toLowerCase().indexOf("android runtime") != -1;
	}
}

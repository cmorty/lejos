/* $Id: OSInfo.java,v 1.2 2006/04/08 18:03:12 sven Exp $
 * Created on 02.10.2004
 */
package lejos.util.jni;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.Properties;

/**
 * Performs normalization of os.name and os.arch system properties.
 */
public class OSInfo
{
	public static final String ARCH_X86 = "x86";
	public static final String ARCH_X86_64 = "x86_64";
	public static final String ARCH_PPC = "ppc";
	public static final String ARCH_PPC64 = "ppc64";
	public static final String ARCH_SPARC = "sparc";

	public static final String OS_WINDOWS = "windows";
	public static final String OS_MACOS = "macos";
	public static final String OS_MACOSX = "macosx";
	public static final String OS_SOLARIS = "solaris";
	public static final String OS_LINUX = "linux";

	private static final Properties ALIAS;
	
	static
	{
		try
		{
			ALIAS = loadProperties();
		}
		catch (IOException e)
		{
			throw new RuntimeException("couldn't determine os/arch", e);
		}
	}
	
	private static Properties loadProperties() throws IOException
	{
		String path = OSInfo.class.getName().replace('.', '/') + ".properties";
		URL u = OSInfo.class.getClassLoader().getResource(path);
		InputStream is = u.openStream();
		try
		{
			Properties r = new Properties();
			r.load(is);
			return r;
		}
		finally
		{
			is.close();
		}
	}
	
	private static String probe(Properties p, String prefix, String value)
	{
		value = value.toLowerCase();
		for (Map.Entry<Object, Object> e : p.entrySet())
		{
			String key = e.getKey().toString();
			String pattern = e.getValue().toString();
			if (key.startsWith(prefix) && value.matches(pattern))
			{			
				return key.substring(prefix.length());
			}
		}
		return value;
	}

	private static String getDefaultOS()
	{
		return System.getProperty("os.name");
	}

	private static String getDefaultArch()
	{
		return System.getProperty("os.arch");
	}

	private static int getDefaultModel()
	{
		try
		{
			return Integer.parseInt(System.getProperty("sun.arch.data.model", "0"));
		}
		catch (NumberFormatException e)
		{
			return 0;
		}
	}

	private final String os;
	private final String arch;

	/**
	 * Creates OSInfo-Object with Infos about the current System according to the
	 * System-properties.
	 */
	public OSInfo()
	{
		this(getDefaultOS(), getDefaultArch(), getDefaultModel());
	}

	/**
	 * TODO_IO javadoc
	 * @param osname
	 * @param arch
	 * @param datamodel
	 */
	public OSInfo(String osname, String arch, int datamodel)
	{
		osname = probe(ALIAS, "os.", osname);
		arch = probe(ALIAS, "arch.", arch);

		if (arch.equals(ARCH_PPC) && datamodel > 32)
		{
			arch += datamodel;
		}

		this.os = osname;
		this.arch = arch;
	}

	/**
	 * Get the name of the OS.
	 * @return the name of the OS
	 */
	public String getOS()
	{
		return this.os;
	}

	/**
	 * Get the name of the architecture.
	 * @return the name of the architecture
	 */
	public String getArch()
	{
		return this.arch;
	}

	public boolean isOS(String name)
	{
		return this.os.equalsIgnoreCase(name);
	}

	public boolean isArch(String name)
	{
		return this.arch.equalsIgnoreCase(name);
	}
}
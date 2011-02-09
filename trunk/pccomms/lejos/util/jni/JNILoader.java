package lejos.util.jni;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class JNILoader
{
	private final OSInfo osinfo;
	private final String subdir;
	
	public JNILoader() throws IOException
	{
		this(null, new OSInfo());
	}

	public JNILoader(String subdir, OSInfo info) throws IOException
	{
		this.osinfo = info;
		this.subdir = subdir;
	}

	private File getBaseFolder(Class<?> caller) throws JNIException, URISyntaxException
	{
		// getName also works as expected for nested classes (returns package.Outer$Inner)
		String clname = caller.getName();
		String clpath = clname.replace('.', '/') + ".class";
		URL url = caller.getClassLoader().getResource(clpath);
		if (url == null)
			throw new JNIException(clpath + " not found in classpath");
			
		File tmp;
		URI uri = url.toURI();
		if ("jar".equalsIgnoreCase(uri.getScheme()))
		{
			String jarpath = uri.getRawSchemeSpecificPart();
			int i = jarpath.indexOf('!');
			if (i < 0)
				throw new RuntimeException("no ! in JAR path");

			jarpath = jarpath.substring(0, i);
			tmp = new File(new URI(jarpath));
		}
		else
		{
			tmp = new File(uri);
			for (int i = clname.indexOf('.'); i >= 0; i = clname.indexOf('.', i + 1))
			{
				tmp = tmp.getParentFile();
			}
		}
		return tmp.getParentFile();
	}
	
	public OSInfo getOSInfo()
	{
		return this.osinfo;
	}

	public void loadLibrary(Class<?> caller, String libname) throws JNIException
	{
		File basefolder;
		try
		{
			basefolder = getBaseFolder(caller);
		}
		catch (URISyntaxException e)
		{
			throw new JNIException("internal error", e);
		}

		if (this.subdir != null)
			basefolder = new File(basefolder, this.subdir);

		String libfile = System.mapLibraryName(libname);
		String arch = osinfo.getArch();
		String os = osinfo.getOS();
		File folder = new File(new File(basefolder, os), arch);

		// try to find libfile in basefolder/os/arch, basefolder/os, and basefolder
		for (int i = 0; i < 3; i++)
		{
			File libpath = new File(folder, libfile);
			if (libpath.exists())
			{
				String libpath2 = libpath.getAbsolutePath();
				try
				{
					System.load(libpath2);
					return;
				}
				catch (Exception e)
				{
					throw new JNIException("cannot load library " + libpath2, e);
				}
				catch (UnsatisfiedLinkError e)
				{
					throw new JNIException("cannot load library " + libpath2, e);
				}
			}
			folder = folder.getParentFile();
		}
		throw new JNIException("library " + libfile + " (" + os + "/" + arch + ") was not found in " + basefolder);
	}
}

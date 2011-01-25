package lejos.util.jni;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class JNILoader
{
	private final OSInfo osinfo;
	private final String subdir;
	
	public JNILoader() throws IOException
	{
		this(null);
	}

	public JNILoader(String subdir) throws IOException
	{
		this.osinfo = new OSInfo();
		this.subdir = subdir;
	}

	private File getBaseFolder(Class<?> caller) throws URISyntaxException
	{
		String clname = caller.getName();
		String clpath = clname.replace('.', '/') + ".class";
		URI u = caller.getClassLoader().getResource(clpath).toURI();

		File tmp;
		if ("jar".equalsIgnoreCase(u.getScheme()))
		{
			String jarpath = u.getRawSchemeSpecificPart();
			int i = jarpath.indexOf('!');
			if (i < 0)
				throw new RuntimeException("no ! in JAR path");

			jarpath = jarpath.substring(0, i);
			tmp = new File(new URI(jarpath));
		}
		else
		{
			tmp = new File(u);
			for (int i = clname.indexOf('.'); i >= 0; i = clname.indexOf('.', i + 1))
			{
				tmp = tmp.getParentFile();
			}
		}
		return tmp.getParentFile();
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

		// try to find libfile in basefolder/os/arch, basefolder/os, and
		// basefolder
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

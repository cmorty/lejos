package lejos.util.jni;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class JNILoader
{
	private final OSInfo osinfo;
	private JNIException lastFault; 
	
	public JNILoader() throws IOException
	{
		this.osinfo = new OSInfo();		
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
			for (int i=clname.indexOf('.'); i>=0; i=clname.indexOf('.', i+1))
			{
				tmp = tmp.getParentFile();
			}
		}
		return tmp.getParentFile();
	}
	
	public void loadLibrary(Class<?> caller, String libname) throws URISyntaxException
	{
		String libfile = System.mapLibraryName(libname);
		String arch = osinfo.getArch();
		String os = osinfo.getOS();
		File folder = getBaseFolder(caller);
		File f = new File(new File(folder, os), arch);
		
		// try to find libfile in ./os/arch, ./os, and .
		for (int i=0; i<3; i++)
		{			
			File f2 = new File(f, libfile);
			if (f2.exists())
			{
				try
				{
					System.load(f2.getPath());
					this.lastFault = null;
					return;
				}
				catch (Exception e)
				{
					this.lastFault = new JNIException("could not load library "+f2.getPath(), e);
				}
			}
			f = f.getParentFile();
		}
		this.lastFault = new JNIException("library "+libfile+" has not been found ("+os+"/"+arch+")");
	}
	
	public JNIException getLastFault()
	{
		return this.lastFault;
	}
}

package lejos.util.jni;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class JNILoader
{
	private final OSInfo osinfo;
	private final File folder;
	private JNIException lastFault; 
	
	public JNILoader(Class<?> caller) throws URISyntaxException, IOException
	{
		this.osinfo = new OSInfo();
		
		String name = caller.getName();
		String path = name.replace('.', '/') + ".class";
		URL u = caller.getClassLoader().getResource(path);
		
		File tmp;
		if (u.getProtocol().equalsIgnoreCase("jar"))
		{
			path = u.getPath();
			int i = path.indexOf('!');
			if (i >= 0)
				path = path.substring(0, i);
						
			tmp = new File(new URI(path)).getParentFile();
		}
		else
		{
			tmp = new File(u.toURI());
			for (int i=0; i>=0; i=name.indexOf('.', i+1))
			{
				tmp = tmp.getParentFile();
			}
		}
		this.folder = tmp;
	}
	
	public void loadLibrary(String name)
	{
		name = System.mapLibraryName(name);
		String os = osinfo.getOS();
		String arch = osinfo.getArch();
		File f = new File(new File(folder, os), arch);
		for (int i=0; i<3; i++)
		{
			File f2 = new File(f, name);
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
		this.lastFault = new JNIException("library "+name+" has not been found ("+os+"/"+arch+")");
	}
	
	public JNIException getLastFault()
	{
		return this.lastFault;
	}
}

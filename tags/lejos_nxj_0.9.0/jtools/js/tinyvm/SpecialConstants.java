package js.tinyvm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

public class SpecialConstants
{
	//TODO use static methods to access databases		
	public static final String[] CLASSES; 
	public static final String[] SIGNATURES;
	
	private static String[] loadDB(String name) throws IOException
	{
		URL url = SpecialConstants.class.getResource(name);
		InputStream is = url.openStream();
		try
		{		
			InputStreamReader isr = new InputStreamReader(is, "utf8");
			BufferedReader br = new BufferedReader(isr);

			ArrayList<String> entries = new ArrayList<String>();
			while (true)
			{
				String line = br.readLine();
				if (line == null)
					break;
				
				line = line.trim();
				if (line.length() > 0 && line.charAt(0) != '#')
				{
					entries.add(line);
				}
			}
			
			//TODO use more efficient data structures than arrays 
			String[] r = new String[entries.size()];
			entries.toArray(r);
			return r;
		}
		finally
		{
			is.close();
		}
	}
	
	static
	{
		try
		{
			CLASSES = loadDB("specialclasses.db");
			SIGNATURES = loadDB("specialsignatures.db");
		}
		catch (Exception e)
		{
			throw new RuntimeException("could not load databases", e);
		}
	}
}

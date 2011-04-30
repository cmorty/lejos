package lejos.pc.tools;

import java.io.OutputStream;
import java.io.PrintWriter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

public abstract class AbstractCommandLineParser
{
	protected static String getLastOptVal(CommandLine cmdline, String key)
	{
		return getLastOptVal(cmdline, key, null);
	}

	protected static String getLastOptVal(CommandLine cmdline, String key, String def)
	{
		String[] vals = cmdline.getOptionValues(key);
		if (vals == null || vals.length <= 0)
			return def;
	
		return vals[vals.length - 1];
	}

	protected final Options options = new Options();
	protected final Class<?> caller;
	protected final String params;
	protected CommandLine result;
	
	public AbstractCommandLineParser(Class<?> caller, String params)
	{
		this.caller = caller;
		this.params = params;
	}

	public void printHelp(OutputStream u)
	{
		this.printHelp(u, null);
	}
	
	public void printHelp(OutputStream u, Throwable e)
	{
		PrintWriter out = new PrintWriter(u, false);
		if (e != null)
		{
			out.println();
			out.println("Error: " + e.getMessage());
		}
		
		String usage = System.getProperty("COMMAND_NAME");
		if (usage == null)
			usage = "java " + caller.getName();
		if (params != null)
			usage += " " + params;
	
		String header = "options:";
		String footer = "";
	
		out.println();
		new HelpFormatter().printHelp(out, 80, usage, header, options, 0, 2, footer);
		out.println();
		out.flush();
	}

}

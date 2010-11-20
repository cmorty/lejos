package lejos.pc.tools;

import java.io.OutputStream;
import java.io.PrintWriter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

public abstract class AbstractCommandLineParser
{
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
		String usage = System.getProperty("COMMAND_NAME");
		if (usage == null)
			usage = "java " + caller.getName();
		if (params != null)
			usage += " " + params;
	
		String header = "options:";
		String footer = "";
	
		PrintWriter out = new PrintWriter(u, false);
		out.println();
		new HelpFormatter().printHelp(out, 80, usage, header, options, 0, 2, footer);
		out.println();
		out.flush();
	}

}

package js.tinyvm.util;

import java.io.File;
import java.io.PrintWriter;

import js.tinyvm.TinyVMException;

import js.tinyvm.RunTimeOptions;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * CommandLineParser
 */
public class TinyVMCommandLineParser 
{
	protected final Options options = new Options();
	protected CommandLine result;
	
	private final boolean reqoutput; 
	private boolean bigendian;
	private String bp;
	private String cp;
	
	public TinyVMCommandLineParser(boolean reqoutput)
	{
		this.reqoutput = reqoutput;
		
		options.addOption("h", "help", false, "show this help");
		options.addOption("a", "all", false, "do not filter classes");
		options.addOption("g", "debug", false, "include debug monitor");
		options.addOption("v", "verbose", false,
				"print class and signature information");
        options.addOption("ea", "enableassert", false, "enable assertions");
        options.addOption("ec", "enablechecks", false, "enable run time checks");
		
		Option bclasspathOption = new Option("bp", "bootclasspath", true,
				"where to find leJOS classes");
		bclasspathOption.setArgName("classpath");
		options.addOption(bclasspathOption);
		
		Option classpathOption = new Option("cp", "classpath", true,
				"where to find user's classes");
		classpathOption.setArgName("classpath");
		options.addOption(classpathOption);
		
		Option outputOption = new Option("o", "output", true,
				"dump binary to file");
		outputOption.setArgName("path to file");
		options.addOption(outputOption);
		
		Option writeOrderOption = new Option("wo", "writeorder", true,
				"endianness (BE or LE)");
		writeOrderOption.setArgName("write order");
		options.addOption(writeOrderOption);

		//Option deviceOption = new Option("tty", "device", true,"device used (USB, COM1, etc)");
		//deviceOption.setArgName("device");
		//options.addOption(deviceOption);
	}
	
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
	
	private static String mangleClassPath(String cp) throws ParseException
	{
		StringBuilder sb = new StringBuilder();
		
		int start = 0;
		int len = cp.length();
		
		while (start < len)
		{
			int end = cp.indexOf(File.pathSeparatorChar, start);
			if (end < 0)
				end = len;
			
			String file = cp.substring(start, end);
			File f = new File(file);
			
			if (!f.exists())
				throw new ParseException("File does not exist: "+file);
			
			if (start > 0)
				sb.append(File.pathSeparatorChar);
			
			//sb.append(f.getAbsolutePath());
			//sb.append(f.getCanonicalPath());
			sb.append(file);
			
			start = end+1;
		}
		
		return sb.toString();
	}
	
	public boolean isHelp()
	{
		return this.result.hasOption("h");
	}

	public boolean isAll()
	{
		return this.result.hasOption("a");
	}

	public boolean isDebug()
	{
		return this.result.hasOption("g");
	}

	public boolean isVerbose()
	{
		return this.result.hasOption("v");
	}

    public boolean isEnableAssert()
    {
        return this.result.hasOption("ea");
    }

    public boolean isEnableChecks()
    {
        return this.result.hasOption("ec");
    }

	public String getOutput()
	{
		return getLastOptVal(this.result, "o");
	}

	public String getBP()
	{
		return this.bp;
	}

	public String getCP()
	{
		return this.cp;
	}
	
	public boolean isBigEndian()
	{
		return this.bigendian;
	}
	
	public String[] getRestArgs()
	{
		return this.result.getArgs();
	}

    public int getRunTimeOptions()
    {
        int opt = 0;
        if (isEnableAssert())
            opt |= RunTimeOptions.EnableAssert.getValue();
        if (isEnableChecks())
            opt |= RunTimeOptions.EnableTypeChecks.getValue();
        return opt;
    }

	/**
	 * Parse commandline.
	 * 
	 * @param args command line
	 * @throws TinyVMException
	 */
	public void parse (String[] args) throws ParseException
	{
		assert args != null: "Precondition: args != null";

		result = new GnuParser().parse(options, args);

		if (!result.hasOption("bp"))
		{
			//throw new ParseException("No bootclasspath defined");
			System.err.println("No bootclasspath specified. Update your build scripts.");
			System.err.println("The bootclasspath parameter will be required in future releases.");
		}
		
		if (!result.hasOption("cp"))
			throw new ParseException("No classpath specified");
		
		if (reqoutput && !result.hasOption("o"))
			throw new ParseException("No output file specified");
		
		if (!result.hasOption("wo"))
			throw new ParseException("No write order specified");
		
		String[] args2 = result.getArgs(); 
		if (args2.length == 0)
			throw new ParseException("No classes specified");
		
		String writeOrder = getLastOptVal(result, "wo").toLowerCase();
		this.bigendian = "be".equals(writeOrder); 
		if (!this.bigendian && !"le".equals(writeOrder))			
			throw new ParseException("Invalid write order: " + writeOrder);
		
		this.bp = mangleClassPath(getLastOptVal(result, "bp", ""));
		this.cp = mangleClassPath(getLastOptVal(result, "cp"));
	}
	
	public boolean parseOrHelp(Class<?> mainclass, String[] args)
	{
		try
		{
			this.parse(args);
		}
		catch (ParseException e)
		{
			System.out.println(e.getMessage());
			this.printHelp(mainclass);
			return false;
		}
		
		if (this.isHelp())
		{
			this.printHelp(mainclass);
			return false;
		}
		
		return true;
	}

	public void printHelp(Class<?> mainclass)
	{
        String commandName = System.getProperty("COMMAND_NAME");
        if (commandName == null)
        	commandName = "java "+mainclass.getName();
        
        //String linesep = System.getProperty("line.separator", "\n\r");
        String header = "options:";
        String footer = "";
        
        String usage = commandName + " [options] class1 [class2 ...]";
        PrintWriter out = new PrintWriter(System.out, false);
        out.println();
        new HelpFormatter().printHelp(out, 80, usage, header,
           options, 0, 2, footer);
        out.println();
        out.flush();
	}
}
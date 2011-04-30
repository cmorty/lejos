package lejos.pc.tools;

import java.io.File;

import js.tinyvm.RunTimeOptions;
import js.tinyvm.DebugOptions;

import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;

/**
 * CommandLineParser
 */
class NXJLinkCommandLineParser extends AbstractCommandLineParser
{
	private final boolean reqoutput;
	private boolean bigendian;
	private boolean help;
	private String bp;
	private String cp;

	public NXJLinkCommandLineParser(Class<?> caller, boolean reqoutput)
	{
		super(caller, "[options] main-class [more classes]");
		
		this.reqoutput = reqoutput;

		options.addOption("h", "help", false, "show this help");
		options.addOption("a", "all", false, "do not filter classes");
		options.addOption("g", "debug", false, "include debug monitor");
		options.addOption("gr", "remotedebug", false, "include remote debug monitor");
		options.addOption("v", "verbose", false, "print class and signature information");
		options.addOption("ea", "enableassert", false, "enable assertions");
		options.addOption("ec", "enablechecks", false, "enable run time checks");
		options.addOption("dm", "disablememcompact", false, "disable memory compaction");

		Option bclasspathOption = new Option("bp", "bootclasspath", true,
			"where to find leJOS classes");
		bclasspathOption.setArgName("classpath");
		options.addOption(bclasspathOption);

		Option classpathOption = new Option("cp", "classpath", true, "where to find user's classes");
		classpathOption.setArgName("classpath");
		options.addOption(classpathOption);

		Option outputOption = new Option("o", "output", true, "dump binary to file");
		outputOption.setArgName("path to file");
		options.addOption(outputOption);

		Option debugFileOption = new Option("od", "outputdebug", true, "dump debug info to file");
		debugFileOption.setArgName("path to file");
		options.addOption(debugFileOption);

		Option writeOrderOption = new Option("wo", "writeorder", true, "endianness (BE or LE)");
		writeOrderOption.setArgName("write order");
		options.addOption(writeOrderOption);

		// Option deviceOption = new Option("tty", "device",
		// true,"device used (USB, COM1, etc)");
		// deviceOption.setArgName("device");
		// options.addOption(deviceOption);
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
				throw new ParseException("File does not exist: " + file);

			if (start > 0)
				sb.append(File.pathSeparatorChar);

			// sb.append(f.getAbsolutePath());
			// sb.append(f.getCanonicalPath());
			sb.append(file);

			start = end + 1;
		}

		return sb.toString();
	}

	public boolean isHelp()
	{
		return this.help;
	}

	public boolean isAll()
	{
		return this.result.hasOption("a");
	}

	public boolean isDebug()
	{
		return this.result.hasOption("g");
	}

	public boolean isRemoteDebug()
	{
		return this.result.hasOption("gr");
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

	public boolean isEnableCompact()
	{
		return !this.result.hasOption("dm");
	}

	public String getOutput()
	{
		return getLastOptVal(this.result, "o");
	}

	public String getDebugFile()
	{
		return getLastOptVal(this.result, "od");
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

	public String[] getClassNames()
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
		if (isEnableCompact())
			opt |= RunTimeOptions.EnableCompact.getValue();
		return opt;
	}

	public int getDebugOptions()
	{
		int opt = 0;
		if (isDebug())
			opt |= DebugOptions.DebugMonitor.getValue();
		if (isRemoteDebug())
			opt |= DebugOptions.RemoteDebug.getValue();
		return opt;
	}

	/**
	 * Parse commandline.
	 * 
	 * @param args command line
	 * @throws ParseException if an illegal argument is found
	 */
	public void parse(String[] args) throws ParseException
	{
		assert args != null : "Precondition: args != null";

		result = new GnuParser().parse(options, args);
		
		this.help = result.hasOption("h");
		if (this.help)
			return;

		if (!result.hasOption("bp"))
		{
			// throw new ParseException("No bootclasspath defined");
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
}
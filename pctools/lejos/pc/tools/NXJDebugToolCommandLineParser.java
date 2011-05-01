package lejos.pc.tools;

import js.tinyvm.TinyVMException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;

/**
 * CommandLineParser
 */
class NXJDebugToolCommandLineParser extends AbstractCommandLineParser
{
	public NXJDebugToolCommandLineParser(Class<?> caller, String params)
	{
		super(caller, params);
		
		options.addOption("h", "help", false, "help");
		Option debugOption = new Option("di", "debuginfo", true, "use the specified debug file");
		Option classOption = new Option("c", "class", false, "resolve class number");
		Option methodOption = new Option("m", "class", false, "resolve method number");
		Option dumpOption = new Option(null, "dump", false, "dump class and method table");
		debugOption.setArgName("debugfile");
		options.addOption(debugOption);
		options.addOption(dumpOption);
		options.addOption(classOption);
		options.addOption(methodOption);
	}
	
	/**
	 * Parse commandline.
	 * 
	 * @param args command line
	 * @throws TinyVMException
	 */
	public CommandLine parse(String[] args) throws ParseException
	{
		assert args != null : "Precondition: args != null";

		result = new GnuParser().parse(options, args);

		assert result != null : "Postconditon: result != null";
		return result;
	}
}
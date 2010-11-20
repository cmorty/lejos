package lejos.pc.tools;

import js.tinyvm.TinyVMException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.ParseException;

/**
 * CommandLineParser
 */
class NXJFlashCommandLineParser extends AbstractCommandLineParser
{
	public NXJFlashCommandLineParser(Class<?> caller, String params)
	{
		super(caller, params);
		
		options.addOption("h", "help", false, "help");
		options.addOption("f", "format", false, "format file system");
		options.addOption("v", "verify", false, "backward compatibility switch (verify is now default)");
		options.addOption("q", "quiet", false,
				"quiet mode - do not report progress");
	}

	/**
	 * Parse commandline.
	 * 
	 * @param args
	 *            command line
	 * @return the parsed commands
	 * @throws TinyVMException
	 */
	public CommandLine parse(String[] args) throws ParseException
	{
		result = new GnuParser().parse(options, args);

		if (result.getArgs().length == 1)
			throw new ParseException("You must provide both firmware and menu file");

		if (result.getArgs().length > 2)
			throw new ParseException("Too many files");
		
		return result;
	}
}
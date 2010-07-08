package lejos.pc.tools;

import java.io.PrintWriter;
import java.io.StringWriter;

import js.tinyvm.TinyVMException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * CommandLineParser
 */
class NXJFlashCommandLineParser {

	/**
	 * Parse commandline.
	 * 
	 * @param args
	 *            command line
	 * @return the parsed commands
	 * @throws TinyVMException
	 */
	public CommandLine parse(String[] args) throws TinyVMException {
		Options options = new Options();
		options.addOption("h", "help", false, "help");
		options.addOption("f", "format", false, "format file system");
		options.addOption("v", "verify", false, "backward compatibility switch (verify is now default)");
		options.addOption("q", "quiet", false,
				"quiet mode - do not report progress");

		CommandLine result;
		try {
			try {
				result = new GnuParser().parse(options, args);
			} catch (ParseException e) {
				System.out.println("Parse error " + e);
				throw new TinyVMException(e.getMessage(), e);
			}

			if (result.hasOption("h"))
				throw new TinyVMException("Help:");

			if (result.getArgs().length == 1)
				throw new TinyVMException(
						"Must provide firmware and menu files");

			if (result.getArgs().length > 2)
				throw new TinyVMException("Too many files");
		} catch (TinyVMException e) {
			StringWriter writer = new StringWriter();
			PrintWriter printWriter = new PrintWriter(writer);
			printWriter.println(e.getMessage());

			String commandName = System.getProperty("COMMAND_NAME",
					"java lejos.pc.tools.NXJFlash");

			String usage = commandName + " [options] [firmware menu]";
			new HelpFormatter().printHelp(printWriter, 80, usage.toString(),
					null, options, 0, 2, null);

			throw new TinyVMException(writer.toString());
		}
		return result;
	}
}
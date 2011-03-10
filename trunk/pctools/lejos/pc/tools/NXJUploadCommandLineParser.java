package lejos.pc.tools;

import js.tinyvm.TinyVMException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;

/**
 * CommandLineParser
 */
class NXJUploadCommandLineParser extends AbstractCommandLineParser
{
	public NXJUploadCommandLineParser(Class<?> caller, String params)
	{
		super(caller, params);
		
		options.addOption("h", "help", false, "help");
		options.addOption("b", "bluetooth", false, "use bluetooth");
		options.addOption("u", "usb", false, "use usb");
		options.addOption("r", "run", false, "start program (last file)");

		Option nameOption = new Option("n", "name", true, "look for named NXT");
		nameOption.setArgName("name");
		options.addOption(nameOption);

		Option addressOption = new Option("d", "address", true, "look for NXT with given address");
		addressOption.setArgName("address");
		options.addOption(addressOption);
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
		
		if (result.getArgs().length == 0)
			throw new ParseException("No file name specified");
		
		assert result != null : "Postconditon: result != null";
		return result;
	}
}

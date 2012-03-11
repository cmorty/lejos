package lejos.pc.tools;

import js.tinyvm.TinyVMException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;

public class NXJDebugProxyCommandLineParser extends AbstractCommandLineParser {

	public NXJDebugProxyCommandLineParser(Class<?> caller, String params) {
		super(caller, params);
		options.addOption("h", "help", false, "help");
		options.addOption("b", "bluetooth", false, "use bluetooth");
		options.addOption("u", "usb", false, "use usb");

		Option nameOption = new Option("n", "name", true, "look for named NXT");
		nameOption.setArgName("name");
		options.addOption(nameOption);

		Option addressOption = new Option("d", "address", true, "look for NXT with given address");
		addressOption.setArgName("address");
		options.addOption(addressOption);
		
		Option connectOption = new Option("c", "connect", true, "connect to a debugger listening on the specified address");
		connectOption.setArgName("inetaddress");
		options.addOption(connectOption);
		
		Option listenOption = new Option("l", "listen", true, "listen for a debugger on the specified address");
		listenOption.setArgName("inetaddress");
		listenOption.setOptionalArg(true);
		options.addOption(listenOption);
		
		Option debugDataOption = new Option("di","debuginfo", true, "use the specified debug data file");
		debugDataOption.setArgName("file");
		options.addOption(debugDataOption);
		
		options.addOption("v", "verbose", false, "print debug outputs. This is used for debugging the proxy and not for debugging the target program!");
		
	}

	/**
	 * Parse commandline.
	 * 
	 * @param args command line
	 */
	public CommandLine parse(String[] args) throws ParseException
	{
		assert args != null : "Precondition: args != null";

		result = new GnuParser().parse(options, args);

		assert result != null : "Postconditon: result != null";
		return result;
	}
}

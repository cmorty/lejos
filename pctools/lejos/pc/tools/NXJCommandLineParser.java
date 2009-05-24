package lejos.pc.tools;

import js.tinyvm.TinyVMException;
import js.tinyvm.util.TinyVMCommandLineParser;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;

/**
 * CommandLineParser
 */
public class NXJCommandLineParser extends TinyVMCommandLineParser
{
	public NXJCommandLineParser()
	{
		super(false);
		
		options.addOption("b", "bluetooth", false, "use bluetooth");		
		options.addOption("u", "usb", false, "use usb");	  
		options.addOption("r", "run", false, "run program");
		
		Option nameOption = new Option("n", "name", true,
				"look for named NXT");
		nameOption.setArgName("name");
		options.addOption(nameOption);
		
		Option addressOption = new Option("d", "address", true,
				"look for NXT with given address");
		addressOption.setArgName("address");
		options.addOption(addressOption);		
	}
	
	public boolean isBluetooth()
	{
		return result.hasOption("b");
	}
	
	public boolean isUSB()
	{
		return result.hasOption("u");
	}
	
	public boolean isRun()
	{
		return result.hasOption("r");
	}
	
	public String getName()
	{
		return getLastOptVal(result, "n");
	}
	
	public String getAddress()
	{
		return getLastOptVal(result, "d");
	}
	
	/**
	 * Parse commandline.
	 * 
	 * @param args command line
	 * @throws TinyVMException
	 */
	@Override
	public void parse (String[] args) throws ParseException
	{
		super.parse(args);
	}
}

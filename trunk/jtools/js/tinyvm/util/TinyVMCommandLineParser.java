package js.tinyvm.util;

import java.io.File;
import java.io.PrintWriter;

import js.tinyvm.TinyVM;
import js.tinyvm.TinyVMException;

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
	private Options options = new Options();
	
	public TinyVMCommandLineParser()
	{
		options.addOption("h", "help", false, "show this help");
		options.addOption("a", "all", false, "do not filter classes");
		options.addOption("g", "debug", false, "include debug monitor");
		options.addOption("v", "verbose", false,
				"print class and signature information");
		
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
	
	/**
	 * Parse commandline.
	 * 
	 * @param args command line
	 * @throws TinyVMException
	 */
	public CommandLine parse (String[] args) throws ParseException
	{
		assert args != null: "Precondition: args != null";

		CommandLine result;
		result = new GnuParser().parse(options, args);

		if (result.hasOption("h"))
			return null;

		if (!result.hasOption("bp"))
			throw new ParseException("No bootclasspath defined");
		
		if (!result.hasOption("cp"))
			throw new ParseException("No classpath defined");
		
		if (!result.hasOption("o"))
			throw new ParseException("No output file defined");
		
		if (!result.hasOption("wo"))
			throw new ParseException("No write order specified");
		
		String writeOrder = result.getOptionValue("wo").toLowerCase();
		if (!"be".equals(writeOrder) && !"le".equals(writeOrder))
			throw new ParseException("Invalid write order: " + writeOrder);

		if (result.getArgs().length == 0)
			throw new ParseException("No classes specified");

		assert result != null: "Postconditon: result != null";
		return result;
	}

	public void printHelp()
	{
        String commandName = System.getProperty("COMMAND_NAME");
        if (commandName == null)
        	commandName = "java "+TinyVM.class.getName();
        
        String linesep = System.getProperty("line.separator", "\n\r");
        String header = linesep+"options:";
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
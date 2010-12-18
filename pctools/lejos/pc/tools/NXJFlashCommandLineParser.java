package lejos.pc.tools;

import java.io.File;

import js.tinyvm.TinyVMException;

import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.ParseException;

/**
 * CommandLineParser
 */
class NXJFlashCommandLineParser extends AbstractCommandLineParser
{
	private File firmwareFile;
	private File menuFile;
	private boolean binary;
	private boolean format;
	private boolean quiet;
	private boolean help;
	
	public NXJFlashCommandLineParser(Class<?> caller, String params)
	{
		super(caller, params);
		
		options.addOption("h", "help", false, "help");
		options.addOption("f", "format", false, "format file system");
		options.addOption("b", "binary", false, "flash non-leJOS binary firmware image");
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
	public void parse(String[] args) throws ParseException
	{
		result = new GnuParser().parse(options, args);
		String[] files = result.getArgs();

		this.help = result.hasOption("h");
		if (this.help)
			return;

		this.quiet = result.hasOption("q");
		this.binary = result.hasOption("b");
		this.format = result.hasOption("f");
		
		if (this.binary)
		{
			if (files.length > 1)
				throw new ParseException("Too many files");
			if (files.length < 1)
				throw new ParseException("No file specified");
			
			if (this.format)
				throw new ParseException("Formatting filesystem not supported in binary mode");
			
			this.firmwareFile = new File(files[0]);
		}
		else
		{
			if (files.length > 0)
			{
				if (files.length > 2)
					throw new ParseException("Too many files");
				if (files.length < 2)
					throw new ParseException("You must provide both firmware and menu file");
				
				this.firmwareFile = new File(files[0]);
				this.menuFile = new File(files[1]);
			}			
		}
		
		testFile(this.firmwareFile);
		testFile(this.menuFile);
	}

	private void testFile(File file) throws ParseException
	{
		if (file != null && !file.exists())
			throw new ParseException("File does not exist: " + file);
	}

	public boolean isHelp()
	{
		return this.help;
	}
	
	public boolean isQuiet()
	{
		return this.quiet;
	}
	
	public boolean isBinary()
	{
		return this.binary;
	}
	
	public boolean doFormat()
	{
		return this.format;
	}
	
	public File getFirmwareFile()
	{
		return this.firmwareFile;
	}
	
	public File getMenuFile()
	{
		return this.menuFile;
	}
}
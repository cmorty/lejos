package lejos.pc.tools;

import java.io.*;
import js.tinyvm.TinyVMException;
import org.apache.commons.cli.*;
import lejos.pc.comm.*;

/**
 * Console output monitor class.
 * This class provides access to console output from a NXT program. The program
 * simply writes strings using the NXT RConsole class. These are sent to the
 * PC via the USB (or Bluetooth) connection.
 *
 */ 
public class Console implements ConsoleViewerUI {
	public static void main(String[] args){
		try {
			(new Console()).run(args);
		} catch (Throwable t) {
			System.err.println("An error has occurred: " + t.getMessage());
		}
	}
	
	private void run(String[] args) throws TinyVMException {
		int protocols = 0;
		ConsoleCommandLineParser fParser = new ConsoleCommandLineParser();
		CommandLine commandLine = fParser.parse(args);
		boolean blueTooth = commandLine.hasOption("b");
		boolean usb = commandLine.hasOption("u");
		String name = commandLine.getOptionValue("n");
		String address = commandLine.getOptionValue("d");
		ConsoleViewComms comm = new ConsoleViewComms(this, false, false);
		if (blueTooth) protocols |= NXTCommFactory.BLUETOOTH;
		if (usb) protocols |= NXTCommFactory.USB;
		if (protocols == 0) protocols = NXTCommFactory.ALL_PROTOCOLS;
		boolean connected = comm.connectTo(name, address, protocols);
		if (!connected) {
			logMessage("Failed to connect to NXT");
			System.exit(1);
		}
	}

	public void append(String value) {
		System.out.print(value);
	}
    
    public void updateLCD(byte[] buffer)
    {
    }

	public void connectedTo(String name, String address) {
	}

	public void logMessage(String msg) {
		System.out.println(msg);		
	}

	public void setStatus(String msg) {
	}
}

/**
 * CommandLineParser
 */
class ConsoleCommandLineParser 
{
   /**
    * Parse commandline.
    * 
    * @param args command line
    * @throws TinyVMException
    */
   public CommandLine parse (String[] args) throws TinyVMException
   {
      assert args != null: "Precondition: args != null";

      Options options = new Options();
      options.addOption("h", "help", false, "help");
      options.addOption("b", "bluetooth", false, "use bluetooth");
      options.addOption("u", "usb", false, "use usb");
      
      Option nameOption = new Option("n", "name", true,"look for named NXT");
      nameOption.setArgName("name");
      options.addOption(nameOption);
      
      Option addressOption = new Option("d", "address", true,
    		 "look for NXT with given address");
      addressOption.setArgName("address");
      options.addOption(addressOption);
      
      CommandLine result;
      try
      {
         try
         {
            result = new GnuParser().parse(options, args);
         }
         catch (ParseException e)
         {
            throw new TinyVMException(e.getMessage(), e);
         }

         if (result.hasOption("h"))
         {
            throw new TinyVMException("Help:");
         }
      }
      catch (TinyVMException e)
      {
         StringWriter writer = new StringWriter();
         PrintWriter printWriter = new PrintWriter(writer);
         printWriter.println(e.getMessage());
         
         String commandName = System.getProperty("COMMAND_NAME", "lejos.pc.tools.Console");

         String usage = commandName + " [options]";
         new HelpFormatter().printHelp(printWriter, 80, usage.toString(), null,
            options, 0, 2, null);

         throw new TinyVMException(writer.toString());
      }

      assert result != null: "Postconditon: result != null";
      return result;
   }
}

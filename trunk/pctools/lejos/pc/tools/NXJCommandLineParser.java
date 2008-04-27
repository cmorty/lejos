package lejos.pc.tools;

import java.io.PrintWriter;
import java.io.StringWriter;

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
public class NXJCommandLineParser 
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
      options.addOption("v", "verbose", false,
         "print class and signature information");
      options.addOption("h", "help", false, "help");
      Option classpathOption = new Option("cp", "classpath", true, "classpath");
      classpathOption.setArgName("classpath");
      options.addOption(classpathOption);
      Option outputOption = new Option("o", "output", true,
         "dump binary to file");
      outputOption.setArgName("binary");
      options.addOption(outputOption);
      options.addOption("a", "all", false, "do not filter classes");
      Option writeOrderOption = new Option("wo", "writeorder", true,
         "write order (BE or LE)");
      writeOrderOption.setArgName("write order");
      options.addOption(writeOrderOption);
      
      options.addOption("b", "bluetooth", false,
      "use bluetooth");
      
      options.addOption("u", "usb", false,
      "use usb");
     
      options.addOption("r", "run", false,
      "run program");
      
      Option nameOption = new Option("n", "name", true,
      "look for named NXT");
      nameOption.setArgName("name");
      options.addOption(nameOption);
      
      Option addressOption = new Option("d", "address", true,
		 "look for NXT with given address");
      addressOption.setArgName("address");
      options.addOption(addressOption);
      
      options.addOption("g", "debug", false, "Include debug monitor");

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

         if (!result.hasOption("cp"))
         {
            throw new TinyVMException("No classpath defined");
         }

         if (!result.hasOption("wo"))
         {
            throw new TinyVMException("No write order specified");
         }
         String writeOrder = result.getOptionValue("wo").toLowerCase();
         if (!"be".equals(writeOrder) && !"le".equals(writeOrder))
         {
            throw new TinyVMException("Invalid write order: " + writeOrder);
         }

         if (result.getArgs().length == 0)
         {
            throw new TinyVMException("No classes specified");
         }
      }
      catch (TinyVMException e)
      {
         StringWriter writer = new StringWriter();
         PrintWriter printWriter = new PrintWriter(writer);
         printWriter.println(e.getMessage());
         
         String commandName = System.getProperty("COMMAND_NAME", "java lejos.pc.tools.NXJLinkAndUpload");

         String usage = commandName + " [options] class1[,class2,...]";
         new HelpFormatter().printHelp(printWriter, 80, usage.toString(), null,
            options, 0, 2, null);

         throw new TinyVMException(writer.toString());
      }

      assert result != null: "Postconditon: result != null";
      return result;
   }
}

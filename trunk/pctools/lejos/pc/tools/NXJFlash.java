/**
 * Command to write the leJOS Virtual Machine and Menu system to the NXT Flash.
 */
package lejos.pc.tools;

import lejos.pc.comm.*;
import java.io.*;

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
 *
 * @author andy
 */
/**
 * CommandLineParser
 */
class CommandLineParser
{

    /**
     * Parse commandline.
     * 
     * @param args command line
     * @return the parsed commands
     * @throws TinyVMException
     */
    public CommandLine parse(String[] args) throws TinyVMException
    {
        Options options = new Options();
        options.addOption("h", "help", false, "help");
        options.addOption("f", "format", false, "format file system");

        CommandLine result;
        try
        {
            try
            {
                result = new GnuParser().parse(options, args);
            }
            catch (ParseException e)
            {
                System.out.println("Parse error " + e);
                throw new TinyVMException(e.getMessage(), e);
            }

            if (result.hasOption("h"))
                throw new TinyVMException("Help:");

            if (result.getArgs().length == 1)
                throw new TinyVMException("Must provide firmware and menu files");

            if (result.getArgs().length > 2)
                throw new TinyVMException("Too many files");
        }
        catch (TinyVMException e)
        {
            StringWriter writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            printWriter.println(e.getMessage());

            String commandName = System.getProperty("COMMAND_NAME", "java lejos.pc.tools.NXJFlash");

            String usage = commandName + " [options] [firmware menu]";
            new HelpFormatter().printHelp(printWriter, 80, usage.toString(), null,
                    options, 0, 2, null);

            throw new TinyVMException(writer.toString());
        }
        return result;
    }
}

public class NXJFlash
{

    private static final int MAX_FIRMWARE_PAGES = 320;
    private static final String VM = "lejos_nxt_rom.bin";
    private static final String MENU = "StartUpText.bin";

    /**
     * Format and store a 32 bit value into a memory image.
     * @param mem The image in which to store the value
     * @param offset The location in bytes in the image
     * @param val The value to be stored.
     */
    void storeWord(byte[] mem, int offset, int val)
    {
        mem[offset++] = (byte) (val & 0xff);
        mem[offset++] = (byte) ((val >> 8) & 0xff);
        mem[offset++] = (byte) ((val >> 16) & 0xff);
        mem[offset++] = (byte) ((val >> 24) & 0xff);
    }

    /**
     * Create the memory image ready to be flashed to the device. Load the
     * firmware and menu images into memory ready for flashing. The command
     * line provides details of the location of the image files to be used.
     * @param commandLine Options for the location of the firmware and menu.
     * @return Memory image ready to be flashed to the device.
     */
    byte[] createImage(CommandLine commandLine) throws IOException, FileNotFoundException
    {
        byte[] memoryImage = new byte[MAX_FIRMWARE_PAGES * NXTSamba.PAGE_SIZE];
        String vmName = null;
        String menuName = null;
        if (commandLine.getArgs().length == 0)
        {
            // No files specified on the command line. Use defaults.
            String home = System.getProperty("nxj.home");
            if (home == null)
                home = System.getenv("NXJ_HOME");
            if (home == null)
                home = "";
            String SEP = System.getProperty("file.separator");
            vmName = home + SEP + "bin" + SEP + VM;
            menuName = home + SEP + "bin" + SEP + MENU;
        }
        else
        {
            vmName = commandLine.getArgs()[0];
            menuName = commandLine.getArgs()[1];
        }
        System.out.println("VM file: " + vmName);
        System.out.println("Menu file: " + menuName);
        FileInputStream vm = new FileInputStream(vmName);
        FileInputStream menu = new FileInputStream(menuName);
        int vmLen = vm.read(memoryImage, 0, memoryImage.length);
        // Round up to page and use as base for the menu location
        int menuStart = ((vmLen + NXTSamba.PAGE_SIZE - 1) / NXTSamba.PAGE_SIZE) * NXTSamba.PAGE_SIZE;
        // Read the menu. Note we may read less than the full size of the menu.
        // If so this will be caught by the overall size check below.
        int menuLen = menu.read(memoryImage, menuStart, memoryImage.length - menuStart);
        // We store the length and location of the Menu in the last page.
        storeWord(memoryImage, memoryImage.length - 4, menuLen);
        storeWord(memoryImage, memoryImage.length - 8, menuStart);
        // Check overall size allow for size/length markers in last block.
        if (menuStart + menuLen + 8 > memoryImage.length)
        {
            System.out.println("Combined size of VM and Menu > " + memoryImage.length);
            return null;
        }
        System.out.println("VM size: " + vmLen + " bytes.");
        System.out.println("Menu size: " + menuLen + " bytes.");
        System.out.println("Total image size " + (menuStart + menuLen) + "/" + memoryImage.length + " bytes.");
        return memoryImage;
    }

    int getChoice() throws IOException
    {
        // flush any old input
        while (System.in.available() > 0)
            System.in.read();
        char choice = (char) System.in.read();
        // flush any old input
        while (System.in.available() > 0)
            System.in.read();
        if (choice >= '0' && choice <= '9')
            return (int) (choice - '0');
        return -1;
    }

    /**
     * Locate and open an NXT device ready for the firmware to be updated. First
     * we look for devices that are already in SAM-BA mode. If we do not find
     * any we look for devices in normal mode and attempt to re-boot them into
     * SAM-BA mode.
     * @param commandLine
     * @return
     */
    NXTSamba openDevice(CommandLine commandLine) throws NXTCommException, IOException
    {
        NXTSamba samba = new NXTSamba();

        // Look for devices in SAM-BA mode
        NXTInfo[] nxts = samba.search();
        if (nxts.length == 0)
        {
            System.out.println("No devices in firmware update mode were found.\nSearching for other NXT devices...");
            NXTCommand cmd = NXTCommand.getSingleton();
            nxts = cmd.search(null, NXTCommFactory.USB);
            if (nxts.length <= 0)
            {
                System.out.println("No NXT found. Please check that the device is turned on and connected.");
                return null;
            }
            int devNo = 0;
            do
            {
                System.out.println("The following NXT devices have been found:");
                for (int i = 0; i < nxts.length; i++)
                    System.out.println("  " + (i + 1) + ":  " + nxts[i].name + "  " + nxts[i].deviceAddress);
                System.out.println("Select the device to update, or enter 0 to exit.");
                System.out.print("Device number to update: ");
                devNo = getChoice();
            } while (devNo < 0 || devNo > nxts.length);
            if (devNo == 0)
                return null;
            if (!cmd.open(nxts[devNo - 1]))
            {
                System.err.println("Failed to open device in command mode.");
                return null;
            }
            // Force into firmware update mode.
            cmd.boot();
            System.out.println("Waiting for device to re-boot...");
            for (int i = 0; i < 30; i++)
            {
                nxts = samba.search();
                if (nxts.length > 0)
                    break;
                try
                {
                    Thread.sleep(1000);
                }
                catch (Exception e)
                {
                }
            }
        }
        if (nxts.length > 1)
        {
            System.err.println("Too many devices in firmware update mode.");
            return null;
        }
        if (nxts.length == 0)
        {
            System.err.println("Unable to locate the device in firmware update mode.\nPlease place the device in reset mode and try again.");
            return null;
        }
        // Must be just the one. Try and open it!
        if (!samba.open(nxts[0]))
        {
            System.out.println("Failed to open device in SAM-BA mode.");
            return null;
        }
        return samba;
    }

    /**
     * Update the NXT with the new memory image.
     * @param nxt Device to update, must be open in SAM-BA mode.
     * @param memoryImage New image for the device
     * @param commandLine Update options
     */
    void updateDevice(NXTSamba nxt, byte[] memoryImage, CommandLine commandLine) throws IOException
    {
        System.out.println("NXT now open in firmware update mode.");
        System.out.println("Unlocking pages.");
        nxt.unlockAllPages();
        System.out.println("Writing memory image...");
        nxt.writePages(0, memoryImage, 0, memoryImage.length);
        if (commandLine.hasOption("f"))
        {
            System.out.println("Formatting...");
            byte[] zeroPage = new byte[NXTSamba.PAGE_SIZE];
            for (int i = 0; i < 3; i++)
                nxt.writePage(MAX_FIRMWARE_PAGES + i, zeroPage, 0);
        }
        System.out.println("Starting new image.");
        nxt.jump(0x00100000);
        nxt.close();
    }

    /**
     * Run the flash command.
     * @param args the command line arguments
     * @throws Exception 
     */
    public void run(String[] args) throws Exception
    {
        CommandLineParser parser = new CommandLineParser();
        CommandLine commandLine = parser.parse(args);

        byte[] memoryImage = createImage(commandLine);
        NXTSamba nxt = openDevice(commandLine);
        if (nxt != null)
            updateDevice(nxt, memoryImage, commandLine);
    }

    /**
     * @param args the command line arguments
     * @throws Exception 
     */
    public static void main(String[] args) throws Exception
    {
        try
        {
            NXJFlash instance = new NXJFlash();
            instance.run(args);
        }
        catch (Throwable t)
        {
            System.err.println("an error occurred: " + t.getMessage());
        }
    }
}

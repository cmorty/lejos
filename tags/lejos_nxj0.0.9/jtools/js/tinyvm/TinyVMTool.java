package js.tinyvm;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import js.common.AbstractTool;
import js.common.ToolProgressMonitor;
import js.tinyvm.io.BEByteWriter;
import js.tinyvm.io.IByteWriter;
import js.tinyvm.io.LEByteWriter;

/**
 * Tiny VM.
 */
public class TinyVMTool extends AbstractTool
{
//   /**
//    * Constructor.
//    */
//   public TinyVMTool (ToolProgressMonitor monitor)
//   {
//      super(monitor);
//   }

   /**
    * Execute tiny vm.
    * 
    * @param classpath classpath
    * @param classes main classes to compile
    * @param all
    * @param stream output stream to write binary to
    * @param bigEndian write big endian output?
    * @param options
    * @param debug true to add debug monitor
    * @param debugStream output stream for debug data
    * @throws TinyVMException
    */
   public void link (String classpath, String[] classes, boolean all,
      OutputStream stream, boolean bigEndian, int options, int debug, OutputStream debugStream) throws TinyVMException
   {
      assert classpath != null: "Precondition: classpath != null";
      assert classes != null: "Precondition: classes != null";
      assert stream != null: "Precondition: stream != null";

      Binary binary = link(classpath, classes, all, options, debug);
      for(ToolProgressMonitor monitor : _monitors) {
    	  binary.log(monitor);
      }
      //binary.log(getProgressMonitor());
      dump(binary, stream, bigEndian, debugStream);

   
   }

   /**
    * Link classes.
    * 
    * @param classpath class path
    * @param entryClassNames entry class names to link
    * @param all do not filter classes?
    * @param options
    * @param debug
    * @return binary
    * @throws TinyVMException
    */
   public Binary link (String classpath, String[] entryClassNames, boolean all, int options, int debug)
      throws TinyVMException
   {
      assert classpath != null: "Precondition: classpath != null";
      assert entryClassNames != null: "Precondition: entryClassNames != null";
      if (entryClassNames.length >= 255)
      {
         throw new TinyVMException("Too many entry classes (max is 254!)");
      }

      if (debug != 0)
      {
         // Insert the debug monitor class as the first entry class
         int names = entryClassNames.length;
         String [] newNames = new String[names+1];
         System.arraycopy(entryClassNames, 0, newNames, 1, names);
         entryClassNames = newNames;
         if ((debug & DebugOptions.RemoteDebug.getValue()) != 0)
            entryClassNames[0] = "lejos.nxt.comm.RConsole$Monitor";
         else
            entryClassNames[0] = "lejos.nxt.debug.DebugMonitor";
      }

      ClassPath computedClasspath = new ClassPath(classpath);
      for (int i = 0; i < entryClassNames.length; i++)
      {
         entryClassNames[i] = entryClassNames[i].replace('.', '/');
      }
      Binary result = Binary.createFromClosureOf(entryClassNames,
         computedClasspath, all);
      for (int i = 0; i < entryClassNames.length; i++)
      {
         if (!result.hasMain(entryClassNames[i]))
         {
            throw new TinyVMException("Class " + entryClassNames[i]
               + " doesn't have a static void main(String[]) method");
         }
      }
      result.setRunTimeOptions(options);
      assert result != null: "Postconditon: result != null";
      return result;
   }

   /**
    * Dump binary to stream.
    * 
    * @param binary binary
    * @param stream stream to write to
    * @param bigEndian use big endian encoding?
    * @param debug stream to write debug data to.
    * @throws TinyVMException
    */
   public void dump (Binary binary, OutputStream stream, boolean bigEndian, OutputStream debug)
      throws TinyVMException
   {
      assert binary != null: "Precondition: binary != null";
      assert stream != null: "Precondition: stream != null";

      try
      {
         BufferedOutputStream bufferedStream = new BufferedOutputStream(stream, 4096);
         IByteWriter byteWriter = bigEndian
            ? (IByteWriter) new BEByteWriter(bufferedStream)
            : (IByteWriter) new LEByteWriter(bufferedStream);
         binary.dump(byteWriter);
         bufferedStream.flush();
         
         if (debug != null)
         {
        	 BufferedOutputStream bufdebug = new BufferedOutputStream(debug, 4096);
        	 binary.dumpDebug(bufdebug);
        	 bufdebug.flush();
         }
      }
      catch (IOException e)
      {
         throw new TinyVMException(e.getMessage(), e);
      }
   }

   private static final Logger _logger = Logger.getLogger("TinyVM");
   static
   {
      _logger.setLevel(Level.OFF);
   }
}
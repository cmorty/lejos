package lejos.pc.tools;

import java.io.File;
import java.io.IOException;

import js.tinyvm.DebugData;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.ParseException;

public class NXJDebugTool {

	public static void main(String[] args){
		ToolStarter.startTool(NXJDebugTool.class, args);
	}

	public static int start(String[] args) throws IOException
	{
		String debugFile;
		String[] restArgs;
		boolean doClass, doMethod, doDump, doHelp;
		
		NXJDebugToolCommandLineParser fParser = new NXJDebugToolCommandLineParser(NXJDebugTool.class, "[options] [classNr] [methodNr [PC]]");
		try
		{
			CommandLine commandLine = fParser.parse(args);
			
	        debugFile = AbstractCommandLineParser.getLastOptVal(commandLine, "di");
	        doClass = commandLine.hasOption("c");
	        doMethod = commandLine.hasOption("m");
	        doDump = commandLine.hasOption("dump");
	        doHelp = commandLine.hasOption("h");
	        restArgs = commandLine.getArgs();
	        
			if (doHelp)
			{
				fParser.printHelp(System.out);
				return 0;
			}
			
	        if (debugFile == null)
	        	throw new ParseException("no debug file specified");	        
		}
		catch (ParseException e)
		{
			fParser.printHelp(System.err, e);
			return 1;
		}
		
		
        DebugData dd = DebugData.load(new File(debugFile));
        boolean didSomething = false;
        
        if (doDump)
        {
        	doDump(dd);
        	didSomething = true;
        }
        
        int i = 0;
        if (doClass)
        {
        	doClass(dd, restArgs, i++);
        	didSomething = true;
        }
        
        if (doMethod)
        {
        	doMethod(dd, restArgs, i);
        	didSomething = true;
        }
        
        
		if (!didSomething)
		{
			System.out.println("Nothing to do!");
			return 1;
		}
		
		return 0;
	}


	private static void doMethod(DebugData dd, String[] args, int i)
	{
		if (args.length <= i)
			throw new RuntimeException("no method number given");
		
		int method = Integer.parseInt(args[i]);
		
		if (method < 0 || method >= dd.getMethodCount())
			throw new IllegalArgumentException("method number is out of range");
		
		String mname = dd.getMethodName(method);
		String clname = dd.getMethodClass(method);
		String filename = dd.getMethodFilename(method);
		String signature = dd.getMethodSignature(method);
		
		System.out.println();
		System.out.println("The method number "+method+" refers to:");
		System.out.println("  "+clname+"."+mname+signature+" ("+filename+")");
		
		if (args.length > i + 1)
		{
			int pc = Integer.parseInt(args[i + 1]);
			int line = dd.getLineNumber(method, pc);

			System.out.println();
			if (line < 0)
			{
				System.out.println("PC "+pc+" is invalid.");
			}
			else
			{
				System.out.println("PC "+pc+" refers to:");
				System.out.println("  line "+line+" in "+filename);
			}
		}
	}

	private static void doClass(DebugData dd, String[] args, int i)
	{
		if (args.length <= i)
			throw new RuntimeException("no class number given");
		
		int classNr = Integer.parseInt(args[i]);
		
		if (classNr < 0 || classNr >= dd.getClassNameCount())
			throw new IllegalArgumentException("class number is out of range");
		
		System.out.println();
		System.out.println("The class number "+classNr+" refers to:");
		System.out.println("  "+dd.getClassName(classNr)+ " ("+dd.getClassFilename(classNr)+")");
	}

	private static void doDump(DebugData dd)
	{
		int clen = dd.getClassNameCount();
		for (int i=0; i<clen; i++)
		{
			//TODO convert classnames to canonical notation
			//TODO show [V as reserved
			//TODO pretty print classnames like [B
			//TODO pretty print signature, like in Binary.log()
			System.out.println("Class "+i+": "+dd.getClassName(i)+" ("+dd.getClassFilename(i)+")");
		}
		
		int mlen = dd.getMethodCount();
		for (int i=0; i<mlen; i++)
		{
			//TODO convert classnames to canonical notation
			//TODO pretty print signature, like in Binary.log()
			System.out.println("Method "+i+": "+dd.getMethodClass(i)+"."+dd.getMethodName(i)+dd.getMethodSignature(i));
		}
	}
}

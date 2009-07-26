package org.jejos.nxt.benchmark;

import java.io.PrintStream;

import lejos.nxt.comm.RConsole;

/**
 * @author Bruce Boyes, based on code from Imsys (www.imsys.se)
 * 
 * @version 1.1a
 * 
 *          <ul>
 *          <li>1.1a 2003 Feb 19 bboyes adding byte math
 *          <li>1.0f 2003 Feb 18 bboyes making float and double more realistic,
 *          with actual float and double operands
 *          <li>1.0e 2003 Feb 18 bboyes total time and operations reporting
 *          <li>1.0d 2003 Feb 18 bboyes made int test better by using an int
 *          operand, added ops per second to report method.
 *          <li>1.0c 2003 Feb 17 bboyes attempting to improve math tests.
 *          Integer divide was just 3/3 then 1/1 forever, not very interesting.
 *          Using the iteration index in the test.
 *          <li>1.0 2003 Feb 08 bboyes Cleaning up this code a bit and fixing
 *          some problems such as a bug in arrayPerformance() which made byte
 *          and int array copies identical. Running on JStamp.
 *          <ul>
 *          <hr>
 *          This code is based on BenchMark.java from the Imsys SNAP examples.
 */
public class GeneralBench
{
	public GeneralBench()
	{
		// nothing
	}

	private void dummy()
	{
		// dummy method
		return;
	}

	private final static String VERSION = "1.1a bboyes";
	private final static Runtime rt = Runtime.getRuntime();
	private final static boolean VERBOSE = false;
	private static int countAll = 0;
	
	private static int[] PADVEC = { 8, 30, 6, 10 };

	/**
	 * Print out the time it took to complete a task Also calculate the rate per
	 * second = (count * 1000) / time <br>
	 * where time is in msec, hence the factor of 1000
	 * 
	 * @param count
	 *            How many iterations
	 * @param time
	 *            How many msec it took
	 * @param task
	 *            String description of the task
	 */
	public void report(long count, long time, String task, long count2, String unit)
	{
		if (0 == time)
			time = 1; // on the PC time is often zero
		
		StringBuilder sb = new StringBuilder();
		appendPadR(sb, count, PADVEC[0]);
		sb.append(' ');
		appendPadL(sb, task + ": ", PADVEC[1]);
		appendPadR(sb, time, PADVEC[2]);
		sb.append(" ms ");
		appendPadR(sb, count2 * 1000 / time, PADVEC[3]);
		sb.append(' ');
		sb.append(unit);
		sb.append("/sec");

		System.out.println(sb.toString());
	}
	
	private void appendPadR(StringBuilder sb, long val, int pad)
	{
		appendPadR(sb, String.valueOf(val), pad);
	}

	private void appendPadR(StringBuilder sb, String val, int pad)
	{
		for (int i=val.length(); i < pad; i++)
			sb.append(' ');
		sb.append(val);
	}

	private void appendPadL(StringBuilder sb, String val, int pad)
	{
		sb.append(val);
		for (int i=val.length(); i < pad; i++)
			sb.append(' ');
	}

	/**
	 * Count the time it takes to iterate through a loop so that we can deduct
	 * this time from the total loop + operation time to get just the operation
	 * time.
	 * 
	 * @param count
	 *            - the number of iterations
	 */
	long getIterationTime(int count)
	{
		long start = System.currentTimeMillis();
		for (int i = 0; i < count; i++)
		{
			// do nothing
		}
		long end = System.currentTimeMillis();
		if (VERBOSE)
			System.out.println("IterationTime= " + (end - start) + " msec");		
		return (end - start);
	}

	public void cleanUpAndShowMemory(String comment)
	{
		System.gc();
		
		if (VERBOSE)
		{
			if (comment != null && comment.length() > 0)
				System.out.println(comment + ": ");
			System.out.println("Memory total=0x" + Long.toString(rt.totalMemory(), 16) + "/" + rt.totalMemory());
			System.out.println(" free=0x" + Long.toString(rt.freeMemory(), 16) + "/" + rt.freeMemory());
		}
	}

	public static void main(String args[])
	{
		RConsole.open();
		// USB.usbEnable(2);
		System.setOut(new PrintStream(RConsole.openOutputStream()));
		System.out.println("Benchmark " + VERSION);

		GeneralBench b = new GeneralBench();

		b.cleanUpAndShowMemory("At start");
		int chunkSize = 0x4000; // Must be power of 2

		int iterate = 200000;
		int tests = iterate / 10;

		long startAll = System.currentTimeMillis();

		countAll += b.arrayPerformanceByte(iterate / chunkSize, chunkSize);
		b.cleanUpAndShowMemory("");

		countAll += b.arrayCopyByte(iterate * 100 / chunkSize, chunkSize);
		b.cleanUpAndShowMemory("");

		countAll += b.arrayPerformanceInt(iterate / chunkSize, chunkSize / 4);
		b.cleanUpAndShowMemory("");

		countAll += b.arrayCopyInt(iterate * 100 / chunkSize, chunkSize / 4);
		b.cleanUpAndShowMemory("");

		countAll += b.bytePerformance(iterate);
		b.cleanUpAndShowMemory("");

		countAll += b.intPerformance(iterate);
		b.cleanUpAndShowMemory("");

		countAll += b.longPerformance(iterate);
		b.cleanUpAndShowMemory("");

		countAll += b.floatPerformance(iterate);
		b.cleanUpAndShowMemory("");

		countAll += b.doublePerformance(iterate);
		b.cleanUpAndShowMemory("");

		countAll += b.callPerformance(iterate);
		b.cleanUpAndShowMemory("");

		countAll += b.stringConcat(tests / 10);
		b.cleanUpAndShowMemory("");

		countAll += b.stringCompare(tests / 20);
		b.cleanUpAndShowMemory("");

		countAll += b.objectPerformance(tests);
		b.cleanUpAndShowMemory("");

		long endAll = System.currentTimeMillis();
		b.report(countAll, endAll - startAll, "Total Loop Executions", countAll, "loops");
		System.out.println("Note: each Loop Execution includes multiple Java operations");
		System.out.flush();
		RConsole.close();
	}

	/**
	 * 
	 * @param count
	 * @param chunkSize
	 */
	public int arrayPerformanceByte(int count, int chunkSize)
	{
		byte b1[] = new byte[chunkSize];
		byte b2[] = new byte[chunkSize];

		long nullTime = getIterationTime(chunkSize);

		// byte array copy
		long start = System.currentTimeMillis();		
		for (int i = 0; i < count; i++)
			for (int j = 0; j < chunkSize; j++)
				b1[j] = b2[j];
		long end = System.currentTimeMillis();

		report(count, end - start - nullTime, "byte["+chunkSize+"] manual copies", count * chunkSize, "bytes");
		
		return count * chunkSize;
	}

	/**
	 * 
	 * @param count
	 * @param chunkSize
	 */
	public int arrayPerformanceInt(int count, int chunkSize)
	{
		int i1[] = new int[chunkSize];
		int i2[] = new int[chunkSize];

		long nullTime = getIterationTime(chunkSize);

		// int array access/copy
		long start = System.currentTimeMillis();
		for (int i = 0; i < count; i++)
			for (int j = 0; j < chunkSize; j++)
				i1[j] = i2[j];
		long end = System.currentTimeMillis();

		report(count, end - start - nullTime, "int["+chunkSize+"] manual copies", count * chunkSize * 4, "bytes");
		
		return count * chunkSize;
	}

	/**
	 * We want to "touch" the same number of array elements as in the array
	 * access test, but using System.arraycopy
	 * 
	 * @param count
	 * @param chunkSize
	 */
	public int arrayCopyByte(int count, int chunkSize)
	{
		byte b1[] = new byte[chunkSize];
		byte b2[] = new byte[chunkSize];
		
		long nullTime = getIterationTime(count);

		long start = System.currentTimeMillis();
		for (int i = 0; i < count; i++)
			System.arraycopy(b1, 0, b2, 0, chunkSize);
		long end = System.currentTimeMillis();

		report(count, end - start - nullTime, "byte["+chunkSize+"] arraycopies", count * chunkSize, "bytes");
		return count;
	}

	public int arrayCopyInt(int count, int chunkSize)
	{
		int i1[] = new int[chunkSize];
		int i2[] = new int[chunkSize];

		long nullTime = getIterationTime(count);

		long start = System.currentTimeMillis();
		for (int i = 0; i < count; i++)
			System.arraycopy(i1, 0, i2, 0, chunkSize);
		long end = System.currentTimeMillis();

		report(count, end - start - nullTime, "int["+chunkSize+"] arraycopies", count * chunkSize * 4, "bytes");
		return count;
	}

	public int stringConcat(int count) 
	{
		String s1 = "abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz";
		String s2 = "abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz";
		String s;

		long nullTime = getIterationTime(count);
		long start, end;

		start = System.currentTimeMillis();
		for (int i = 0; i < count; i++)
			s = s1 + s2;
		end = System.currentTimeMillis();

		report(count, end - start - nullTime, "string concats", count, "ops");
		return count;
	}

	public int stringCompare(int count) 
	{
		String s1 = "abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz";
		String s2 = "abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz";
		boolean b;

		long nullTime = getIterationTime(count);
		long start, end;

		start = System.currentTimeMillis();
		for (int i = 0; i < count; i++)
			b = s1.equals(s2);
		end = System.currentTimeMillis();

		report(count, end - start - nullTime, "string compares", count, "ops");
		return count;
	}

	public int objectPerformance(int count)
	{
		long nullTime = getIterationTime(count);
		long start = 0, end = 0;

		// Object creations
		try
		{
			start = System.currentTimeMillis();
			for (int i = 0; i < count; i++)
				new GeneralBench();
			end = System.currentTimeMillis();
		}
		catch (Exception e)
		{
			cleanUpAndShowMemory(e.toString());
		}

		report(count, end - start - nullTime, "object creations", count, "ops");
		return count;
	}

	public int callPerformance(int count)
	{
		long nullTime = getIterationTime(count);

		// Function calls
		long start = System.currentTimeMillis();
		for (int i = 0; i < count; i++)
			dummy();
		long end = System.currentTimeMillis();

		report(count, end - start - nullTime, "method calls", count, "ops");
		return count;
	}

	public int doublePerformance(int count)
	{
		long nullTime = getIterationTime(count);
		long start, end;

		double d = 3.14e12;
		double e = 111.1313131313e10;
		double f;

		// Double Add
		start = System.currentTimeMillis();
		for (int i = 0; i < count; i++)
			f = d + e;
		end = System.currentTimeMillis();
		report(count, end - start - nullTime, "double add", count, "ops");

		// Double sub
		start = System.currentTimeMillis();
		for (int i = 0; i < count; i++)
			f = d - e;
		end = System.currentTimeMillis();
		report(count, end - start - nullTime, "double sub", count, "ops");

		// Double Mul
		start = System.currentTimeMillis();
		for (int i = 0; i < count; i++)
			f = d * e;
		end = System.currentTimeMillis();
		report(count, end - start - nullTime, "double mul", count, "ops");

		// Double Div
		start = System.currentTimeMillis();
		for (int i = 0; i < count; i++)
			f = d / e;
		end = System.currentTimeMillis();
		report(count, end - start - nullTime, "double div", count, "ops");
		
		return count * 4;
	}

	public int floatPerformance(int count)
	{
		long nullTime = getIterationTime(count);

		float f = 3.14e12f;
		float g = 111.1313131313f;
		float h;

		// Float Add
		long start = System.currentTimeMillis();
		for (int i = 0; i < count; i++)
			h = f + g;
		long end = System.currentTimeMillis();
		report(count, end - start - nullTime, "float add", count, "ops");

		// Float sub
		start = System.currentTimeMillis();
		for (int i = 0; i < count; i++)
			h = f - g;
		end = System.currentTimeMillis();
		report(count, end - start - nullTime, "float sub", count, "ops");

		// Float Mul
		start = System.currentTimeMillis();
		for (int i = 0; i < count; i++)
			h = f * g;
		end = System.currentTimeMillis();
		report(count, end - start - nullTime, "float mul", count, "ops");

		// Float Div
		start = System.currentTimeMillis();
		for (int i = 0; i < count; i++)
			h = f / g;
		end = System.currentTimeMillis();
		report(count, end - start - nullTime, "float div", count, "ops");
		
		return count * 4;
	}

	/**
	 * int primitive performance: add, sub, mul, div. I recoded this to use the
	 * index in the calculation and an odd 32-bit constant as the other operand.
	 * 
	 * @param count
	 *            the number of iterations of each operation
	 */
	public int intPerformance(int count)
	{
		final int J = 0x11223344;
		int k;

		long nullTime = getIterationTime(count);
		long start, end;

		// Integer Add
		start = System.currentTimeMillis();
		for (int i = 0; i < count; i++)
			// j = j + j;
			k = J + i;
		end = System.currentTimeMillis();
		report(count, end - start - nullTime, "int add", count, "ops");

		// Integer sub
		start = System.currentTimeMillis();
		for (int i = 0; i < count; i++)
			// j = j - i;
			k = J - i;
		end = System.currentTimeMillis();
		report(count, end - start - nullTime, "int sub", count, "ops");

		// Integer Mul
		start = System.currentTimeMillis();
		for (int i = 0; i < count; i++)
			// This will quickly reach MAX_VALUE? j = j * j;
			// actually it clips at '1' after 28 iterations
			k = J * i;
		end = System.currentTimeMillis();
		report(count, end - start - nullTime, "int mul", count, "ops");

		// Integer Div
		start = System.currentTimeMillis();
		for (int i = 0; i < count; i++)
			// Imsys original code: j = j / j;
			// With seed of 3, this is 3/3, then 1/1 forever
			// Now divide the index by three
			k = i / J;		
		end = System.currentTimeMillis();
		report(count, end - start - nullTime, "int div", count, "ops");
		
		return count * 4;
	}

	/**
	 * int primitive performance: add, sub, mul, div. I recoded this to use the
	 * index in the calculation and an odd 32-bit constant as the other operand.
	 * 
	 * @param count
	 *            the number of iterations of each operation
	 */
	public int longPerformance(int count)
	{
		final long J = 0x11223344;
		long k;

		long nullTime = getIterationTime(count);
		long start, end;

		// Integer Add
		start = System.currentTimeMillis();
		for (int i = 0; i < count; i++)
			// j = j + j;
			k = J + i;
		end = System.currentTimeMillis();
		report(count, end - start - nullTime, "long add", count, "ops");

		// Integer sub
		start = System.currentTimeMillis();
		for (int i = 0; i < count; i++)
			// j = j - i;
			k = J - i;
		end = System.currentTimeMillis();
		report(count, end - start - nullTime, "long sub", count, "ops");

		// Integer Mul
		start = System.currentTimeMillis();
		for (int i = 0; i < count; i++)
			// This will quickly reach MAX_VALUE? j = j * j;
			// actually it clips at '1' after 28 iterations
			k = J * i;
		end = System.currentTimeMillis();
		report(count, end - start - nullTime, "long mul", count, "ops");

		// Integer Div
		start = System.currentTimeMillis();
		for (int i = 0; i < count; i++)
			// Imsys original code: j = j / j;
			// With seed of 3, this is 3/3, then 1/1 forever
			// Now divide the index by three
			k = i / J;
		end = System.currentTimeMillis();
		report(count, end - start - nullTime, "long div", count, "ops");
		
		return count * 4;
	}

	/**
	 * byte primitive performance: add, sub, mul, div.
	 * 
	 * @param count
	 *            the number of iterations of each operation
	 */
	public int bytePerformance(int count)
	{
		byte a = (byte) 0x77;
		byte b = (byte) 0x11;
		byte c;

		long nullTime = getIterationTime(count);
		long start, end;

		// Add
		start = System.currentTimeMillis();
		for (int i = 0; i < count; i++)
			c = (byte) (a + b);
		end = System.currentTimeMillis();
		report(count, end - start - nullTime, "byte add", count, "ops");

		// sub
		a = (byte) 0xff;
		start = System.currentTimeMillis();
		for (int i = 0; i < count; i++)
			c = (byte) (a - b);
		end = System.currentTimeMillis();
		report(count, end - start - nullTime, "byte sub", count, "ops");

		// Mul
		a = (byte) 0x0f;
		b = (byte) 0x11;
		start = System.currentTimeMillis();
		for (int i = 0; i < count; i++)
			c = (byte) (a * b);
		end = System.currentTimeMillis();
		report(count, end - start - nullTime, "byte mul", count, "ops");

		// Div
		a = (byte) 0xfe;
		b = (byte) 0x0e;
		start = System.currentTimeMillis();
		for (int i = 0; i < count; i++)
			c = (byte) (a / b);
		end = System.currentTimeMillis();
		report(count, end - start - nullTime, "byte div", count, "ops");
		
		return count * 4;
	}
}

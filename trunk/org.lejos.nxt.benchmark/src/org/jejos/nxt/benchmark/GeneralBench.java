package org.jejos.nxt.benchmark;

import java.io.PrintStream;

import lejos.nxt.comm.RConsole;

/**
 * @author Bruce Boyes, based on code from Imsys (www.imsys.se)
 * 
 * @version 1.2
 * 
 *          <ul>
 *          <li>1.2 2009 Jul 27 skoehler: changes all over the place
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

	private void dummy1()
	{
		// dummy method
		return;
	}

	private static void dummy2()
	{
		// dummy method
		return;
	}

	private static final String VERSION = "1.2";
	private static final int[] PADVEC = { 8, 30, 6, 10 };

	/**
	 * Print out the time it took to complete a task Also calculate the rate per
	 * second = (count * 1000) / time <br>
	 * where time is in msec, hence the factor of 1000
	 * 
	 * @param count
	 *            How many iterations of the task
	 * @param task
	 *            String description of the task
	 * @param count2
	 *            How many items of unit
	 * @param unit
	 *            String description of the unit
	 * @param time
	 *            How many msec it took
	 */
	private static void report(long count, String task, long count2, String unit, long time)
	{
		BenchUtils.report(count, task, count2, unit, time, PADVEC);
	}
	
	public static void main(String args[])
	{
		RConsole.open();
		// USB.usbEnable(2);
		System.setOut(new PrintStream(RConsole.openOutputStream()));
		System.out.println("Benchmark " + VERSION);

		GeneralBench b = new GeneralBench();

		BenchUtils.cleanUp("At start");
		int chunkSize = 0x4000; // Must be power of 2

		int iterate = 200000;
		int tests = iterate / 10;

		int countAll = 0;
		long startAll = System.currentTimeMillis();

		countAll += b.benchArrayMCopyByte(iterate / chunkSize, chunkSize);
		BenchUtils.cleanUp("");

		countAll += b.benchArraySCopyByte(iterate * 100 / chunkSize, chunkSize);
		BenchUtils.cleanUp("");

		countAll += b.benchArrayMCopyInt(iterate / chunkSize, chunkSize / 4);
		BenchUtils.cleanUp("");

		countAll += b.benchArraySCopyInt(iterate * 100 / chunkSize, chunkSize / 4);
		BenchUtils.cleanUp("");

		countAll += b.benchArithByte(iterate);
		BenchUtils.cleanUp("");

		countAll += b.benchArithInt(iterate);
		BenchUtils.cleanUp("");

		countAll += b.benchArithLong(iterate);
		BenchUtils.cleanUp("");

		countAll += b.benchArithFloat(iterate);
		BenchUtils.cleanUp("");

		countAll += b.benchArithDouble(iterate);
		BenchUtils.cleanUp("");

		countAll += b.benchMethod(iterate);
		BenchUtils.cleanUp("");

		countAll += b.benchMethodStatic(iterate);
		BenchUtils.cleanUp("");

		countAll += b.benchMethodStaticNative(iterate);
		BenchUtils.cleanUp("");

		countAll += b.benchStringConcat(tests / 10);
		BenchUtils.cleanUp("");

		countAll += b.benchStringCompare(tests / 20);
		BenchUtils.cleanUp("");

		countAll += b.benchNewOp(tests);
		BenchUtils.cleanUp("");

		long endAll = System.currentTimeMillis();
		b.report(countAll, "Total Loop Executions", countAll, "loops", endAll - startAll);
		System.out.println("Note: each Loop Execution includes multiple Java operations");
		System.out.flush();
		RConsole.close();
	}

	/**
	 * 
	 * @param count
	 * @param chunkSize
	 */
	public int benchArrayMCopyByte(int count, int chunkSize)
	{
		byte b1[] = new byte[chunkSize];
		byte b2[] = new byte[chunkSize];

		long nullTime = BenchUtils.getIterationTime(chunkSize);

		// byte array copy
		long start = System.currentTimeMillis();		
		for (int i = 0; i < count; i++)
			for (int j = 0; j < chunkSize; j++)
				b1[j] = b2[j];
		long end = System.currentTimeMillis();

		report(count, "byte["+chunkSize+"] manual copies", count * chunkSize, "bytes", end - start - nullTime);
		
		return count * chunkSize;
	}

	/**
	 * 
	 * @param count
	 * @param chunkSize
	 */
	public int benchArrayMCopyInt(int count, int chunkSize)
	{
		int i1[] = new int[chunkSize];
		int i2[] = new int[chunkSize];

		long nullTime = BenchUtils.getIterationTime(chunkSize);

		// int array access/copy
		long start = System.currentTimeMillis();
		for (int i = 0; i < count; i++)
			for (int j = 0; j < chunkSize; j++)
				i1[j] = i2[j];
		long end = System.currentTimeMillis();

		report(count, "int["+chunkSize+"] manual copies", count * chunkSize * 4, "bytes", end - start - nullTime);
		
		return count * chunkSize;
	}

	/**
	 * We want to "touch" the same number of array elements as in the array
	 * access test, but using System.arraycopy
	 * 
	 * @param count
	 * @param chunkSize
	 */
	public int benchArraySCopyByte(int count, int chunkSize)
	{
		byte b1[] = new byte[chunkSize];
		byte b2[] = new byte[chunkSize];
		
		long nullTime = BenchUtils.getIterationTime(count);

		long start = System.currentTimeMillis();
		for (int i = 0; i < count; i++)
			System.arraycopy(b1, 0, b2, 0, chunkSize);
		long end = System.currentTimeMillis();

		report(count, "byte["+chunkSize+"] arraycopies", count * chunkSize, "bytes", end - start - nullTime);
		return count;
	}

	public int benchArraySCopyInt(int count, int chunkSize)
	{
		int i1[] = new int[chunkSize];
		int i2[] = new int[chunkSize];

		long nullTime = BenchUtils.getIterationTime(count);

		long start = System.currentTimeMillis();
		for (int i = 0; i < count; i++)
			System.arraycopy(i1, 0, i2, 0, chunkSize);
		long end = System.currentTimeMillis();

		report(count, "int["+chunkSize+"] arraycopies", count * chunkSize * 4, "bytes", end - start - nullTime);
		return count;
	}

	public int benchStringConcat(int count) 
	{
		String s1 = "abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz";
		String s2 = "abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz";
		String s;

		long nullTime = BenchUtils.getIterationTime(count);
		long start, end;

		start = System.currentTimeMillis();
		for (int i = 0; i < count; i++)
			s = s1 + s2;
		end = System.currentTimeMillis();

		report(count, "string concats", count, "ops", end - start - nullTime);
		return count;
	}

	public int benchStringCompare(int count) 
	{
		String s1 = "abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz";
		String s2 = "abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz";
		boolean b;

		long nullTime = BenchUtils.getIterationTime(count);
		long start, end;

		start = System.currentTimeMillis();
		for (int i = 0; i < count; i++)
			b = s1.equals(s2);
		end = System.currentTimeMillis();

		report(count, "string compares", count, "ops", end - start - nullTime);
		return count;
	}

	public int benchNewOp(int count)
	{
		long nullTime = BenchUtils.getIterationTime(count);
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
			BenchUtils.cleanUp(e.toString());
		}

		report(count, "object creations", count, "ops", end - start - nullTime);
		return count;
	}

	public int benchMethod(int count)
	{
		long nullTime = BenchUtils.getIterationTime(count);

		// Function calls
		long start = System.currentTimeMillis();
		for (int i = 0; i < count; i++)
			dummy1();
		long end = System.currentTimeMillis();

		report(count, "method calls", count, "ops", end - start - nullTime);
		return count;
	}

	public int benchMethodStatic(int count)
	{
		long nullTime = BenchUtils.getIterationTime(count);

		// Function calls
		long start = System.currentTimeMillis();
		for (int i = 0; i < count; i++)
			dummy2();
		long end = System.currentTimeMillis();

		report(count, "static method calls", count, "ops", end - start - nullTime);
		return count;
	}

	public int benchMethodStaticNative(int count)
	{
		long nullTime = BenchUtils.getIterationTime(count);

		// Function calls
		long start = System.currentTimeMillis();
		for (int i = 0; i < count; i++)
			System.getFirmwareRevision();
		long end = System.currentTimeMillis();

		report(count, "native static method calls", count, "ops", end - start - nullTime);
		return count;
	}

	public int benchArithDouble(int count)
	{
		long nullTime = BenchUtils.getIterationTime(count);
		long start, end;

		double d = 3.14e12;
		double e = 111.1313131313e10;
		double f;

		// Double Add
		start = System.currentTimeMillis();
		for (int i = 0; i < count; i++)
			f = d + e;
		end = System.currentTimeMillis();
		report(count, "double add", count, "ops", end - start - nullTime);

		// Double sub
		start = System.currentTimeMillis();
		for (int i = 0; i < count; i++)
			f = d - e;
		end = System.currentTimeMillis();
		report(count, "double sub", count, "ops", end - start - nullTime);

		// Double Mul
		start = System.currentTimeMillis();
		for (int i = 0; i < count; i++)
			f = d * e;
		end = System.currentTimeMillis();
		report(count, "double mul", count, "ops", end - start - nullTime);

		// Double Div
		start = System.currentTimeMillis();
		for (int i = 0; i < count; i++)
			f = d / e;
		end = System.currentTimeMillis();
		report(count, "double div", count, "ops", end - start - nullTime);
		
		return count * 4;
	}

	public int benchArithFloat(int count)
	{
		long nullTime = BenchUtils.getIterationTime(count);

		float f = 3.14e12f;
		float g = 111.1313131313f;
		float h;

		// Float Add
		long start = System.currentTimeMillis();
		for (int i = 0; i < count; i++)
			h = f + g;
		long end = System.currentTimeMillis();
		report(count, "float add", count, "ops", end - start - nullTime);

		// Float sub
		start = System.currentTimeMillis();
		for (int i = 0; i < count; i++)
			h = f - g;
		end = System.currentTimeMillis();
		report(count, "float sub", count, "ops", end - start - nullTime);

		// Float Mul
		start = System.currentTimeMillis();
		for (int i = 0; i < count; i++)
			h = f * g;
		end = System.currentTimeMillis();
		report(count, "float mul", count, "ops", end - start - nullTime);

		// Float Div
		start = System.currentTimeMillis();
		for (int i = 0; i < count; i++)
			h = f / g;
		end = System.currentTimeMillis();
		report(count, "float div", count, "ops", end - start - nullTime);
		
		return count * 4;
	}

	/**
	 * int primitive performance: add, sub, mul, div. I recoded this to use the
	 * index in the calculation and an odd 32-bit constant as the other operand.
	 * 
	 * @param count
	 *            the number of iterations of each operation
	 */
	public int benchArithInt(int count)
	{
		final int J = 0x11223344;
		int k;

		long nullTime = BenchUtils.getIterationTime(count);
		long start, end;

		// Integer Add
		start = System.currentTimeMillis();
		for (int i = 0; i < count; i++)
			// j = j + j;
			k = J + i;
		end = System.currentTimeMillis();
		report(count, "int add", count, "ops", end - start - nullTime);

		// Integer sub
		start = System.currentTimeMillis();
		for (int i = 0; i < count; i++)
			// j = j - i;
			k = J - i;
		end = System.currentTimeMillis();
		report(count, "int sub", count, "ops", end - start - nullTime);

		// Integer Mul
		start = System.currentTimeMillis();
		for (int i = 0; i < count; i++)
			// This will quickly reach MAX_VALUE? j = j * j;
			// actually it clips at '1' after 28 iterations
			k = J * i;
		end = System.currentTimeMillis();
		report(count, "int mul", count, "ops", end - start - nullTime);

		// Integer Div
		start = System.currentTimeMillis();
		for (int i = 0; i < count; i++)
			// Imsys original code: j = j / j;
			// With seed of 3, this is 3/3, then 1/1 forever
			// Now divide the index by three
			k = i / J;		
		end = System.currentTimeMillis();
		report(count, "int div", count, "ops", end - start - nullTime);
		
		return count * 4;
	}

	/**
	 * int primitive performance: add, sub, mul, div. I recoded this to use the
	 * index in the calculation and an odd 32-bit constant as the other operand.
	 * 
	 * @param count
	 *            the number of iterations of each operation
	 */
	public int benchArithLong(int count)
	{
		final long J = 0x11223344;
		long k;

		long nullTime = BenchUtils.getIterationTime(count);
		long start, end;

		// Integer Add
		start = System.currentTimeMillis();
		for (int i = 0; i < count; i++)
			// j = j + j;
			k = J + i;
		end = System.currentTimeMillis();
		report(count, "long add", count, "ops", end - start - nullTime);

		// Integer sub
		start = System.currentTimeMillis();
		for (int i = 0; i < count; i++)
			// j = j - i;
			k = J - i;
		end = System.currentTimeMillis();
		report(count, "long sub", count, "ops", end - start - nullTime);

		// Integer Mul
		start = System.currentTimeMillis();
		for (int i = 0; i < count; i++)
			// This will quickly reach MAX_VALUE? j = j * j;
			// actually it clips at '1' after 28 iterations
			k = J * i;
		end = System.currentTimeMillis();
		report(count, "long mul", count, "ops", end - start - nullTime);

		// Integer Div
		start = System.currentTimeMillis();
		for (int i = 0; i < count; i++)
			// Imsys original code: j = j / j;
			// With seed of 3, this is 3/3, then 1/1 forever
			// Now divide the index by three
			k = i / J;
		end = System.currentTimeMillis();
		report(count, "long div", count, "ops", end - start - nullTime);
		
		return count * 4;
	}

	/**
	 * byte primitive performance: add, sub, mul, div.
	 * 
	 * @param count
	 *            the number of iterations of each operation
	 */
	public int benchArithByte(int count)
	{
		byte a = (byte) 0x77;
		byte b = (byte) 0x11;
		byte c;

		long nullTime = BenchUtils.getIterationTime(count);
		long start, end;

		// Add
		start = System.currentTimeMillis();
		for (int i = 0; i < count; i++)
			c = (byte) (a + b);
		end = System.currentTimeMillis();
		report(count, "byte add", count, "ops", end - start - nullTime);

		// sub
		a = (byte) 0xff;
		start = System.currentTimeMillis();
		for (int i = 0; i < count; i++)
			c = (byte) (a - b);
		end = System.currentTimeMillis();
		report(count, "byte sub", count, "ops", end - start - nullTime);

		// Mul
		a = (byte) 0x0f;
		b = (byte) 0x11;
		start = System.currentTimeMillis();
		for (int i = 0; i < count; i++)
			c = (byte) (a * b);
		end = System.currentTimeMillis();
		report(count, "byte mul", count, "ops", end - start - nullTime);

		// Div
		a = (byte) 0xfe;
		b = (byte) 0x0e;
		start = System.currentTimeMillis();
		for (int i = 0; i < count; i++)
			c = (byte) (a / b);
		end = System.currentTimeMillis();
		report(count, "byte div", count, "ops", end - start - nullTime);
		
		return count * 4;
	}
}

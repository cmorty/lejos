package org.lejos.nxt.benchmark;

import java.io.PrintStream;

import lejos.nxt.comm.RConsole;


public final class MathBench
{
	private static final int[] PADVEC = { 8, 30, 6, 10 };
	private static final String VERSION = "1.2";

	private static final double MIN_NORMAL = 0x1.0p-1022;
	
	/**
	 * Square root.
	 */
	public static double sqrt(double x)
	{
		// also catches NaN
		if (!(x > 0))
			return (x == 0) ? 0 : Double.NaN;
		if (x == Double.POSITIVE_INFINITY)
			return Double.POSITIVE_INFINITY;

		// magic constant invsqrt
		// according to http://www.lomont.org/Math/Papers/2003/InvSqrt.pdf
		// also look at http://en.wikipedia.org/wiki/Fast_inverse_square_root
		// modified to work with subnormal values
		double isqrt;
		if (x < MIN_NORMAL)
			isqrt = Double.longBitsToDouble(0x5fe6ec85e7de30daL - (Double.doubleToRawLongBits(x * 0x1p100) >> 1)) * 0x1p50;
		else
			isqrt = Double.longBitsToDouble(0x5fe6ec85e7de30daL - (Double.doubleToRawLongBits(x) >> 1));

		double xhalf = 0.5 * x;
		
		//three steps of newton iteration for the inverse sqrt
		isqrt = isqrt * (1.5 - xhalf * isqrt * isqrt);
		isqrt = isqrt * (1.5 - xhalf * isqrt * isqrt);
		isqrt = isqrt * (1.5 - xhalf * isqrt * isqrt);
		
		//one step of newton iteration for the sqrt
		return xhalf * isqrt + 0.5 / isqrt;
	}

	private static int benchSqrt(int count, String comment, double x)
	{
		long nullTime = BenchUtils.getIterationTime(count);
	
		// Function calls
		long start = System.currentTimeMillis();
		for (int i = 0; i < count; i++)
			Math.sqrt(x);
		long end = System.currentTimeMillis();
	
		report(count, "sqrt ("+comment+")", count, "ops", end - start - nullTime);
		return count;
	}
	
	private static int benchSqrtNew(int count, String comment, double x)
	{
		long nullTime = BenchUtils.getIterationTime(count);
	
		// Function calls
		long start = System.currentTimeMillis();
		for (int i = 0; i < count; i++)
			sqrt(x);
		long end = System.currentTimeMillis();
	
		report(count, "sqrt (new, "+comment+")", count, "ops", end - start - nullTime);
		return count;
	}
	
	public static void main(String args[])
	{
		RConsole.open();
		System.setOut(new PrintStream(RConsole.openOutputStream()));
		System.out.println("MathBench " + VERSION);
	
		BenchUtils.cleanUp("At start");
	
		int iterate = 2000;
	
		int countAll = 0;
		long startAll = System.currentTimeMillis();
	
		countAll += benchSqrt(iterate, "subnormal", Math.PI * 0x1p-1060);
		BenchUtils.cleanUp(null);
		countAll += benchSqrt(iterate, "normal", Math.PI);
		BenchUtils.cleanUp(null);
	
		countAll += benchSqrtNew(iterate, "subnormal", Math.PI * 0x1p-1060);
		BenchUtils.cleanUp(null);	
		countAll += benchSqrtNew(iterate, "normal", Math.PI);
		BenchUtils.cleanUp(null);
	
		long endAll = System.currentTimeMillis();
		report(countAll, "Total Loop Executions", countAll, "loops", endAll - startAll);
		System.out.println("Note: each Loop Execution includes multiple Java operations");
		System.out.flush();
		RConsole.close();
	}

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

}

package org.lejos.nxt.benchmark;

import java.io.PrintStream;

import org.lejos.nxt.benchmark.workbench.BetaMath;
import org.lejos.nxt.benchmark.workbench.HistoricMath;

import lejos.nxt.comm.RConsole;


public final class MathBench
{
	private static final int[] PADVEC = { 8, 40, 6, 6 };
	private static final String VERSION = "1.2";

	private static int benchSqrtHistoric1(int count, String comment, double x)
	{
		long nullTime = BenchUtils.getIterationTime(count);
	
		// Function calls
		long start = System.currentTimeMillis();
		for (int i = 0; i < count; i++)
			HistoricMath.sqrtSimple(x);
		long end = System.currentTimeMillis();
	
		report(count, "sqrt (historic, simple, "+comment+")", count, "ops", end - start - nullTime);
		return count;
	}
	
	private static int benchSqrtHistoric2(int count, String comment, double x)
	{
		long nullTime = BenchUtils.getIterationTime(count);
	
		// Function calls
		long start = System.currentTimeMillis();
		for (int i = 0; i < count; i++)
			HistoricMath.sqrtLinear(x);
		long end = System.currentTimeMillis();
	
		report(count, "sqrt (historic, linear, "+comment+")", count, "ops", end - start - nullTime);
		return count;
	}
	
	private static int benchSqrtCurrent(int count, String comment, double x)
	{
		long nullTime = BenchUtils.getIterationTime(count);
	
		// Function calls
		long start = System.currentTimeMillis();
		for (int i = 0; i < count; i++)
			Math.sqrt(x);
		long end = System.currentTimeMillis();
	
		report(count, "sqrt (current, "+comment+")", count, "ops", end - start - nullTime);
		return count;
	}
	
	private static int benchSqrtNewF(int count, String comment, float x)
	{
		long nullTime = BenchUtils.getIterationTime(count);
	
		// Function calls
		long start = System.currentTimeMillis();
		for (int i = 0; i < count; i++)
			BetaMath.sqrtF(x);
		long end = System.currentTimeMillis();
	
		report(count, "sqrt (new, float, "+comment+")", count, "ops", end - start - nullTime);
		return count;
	}
	
	private static int benchLog(int count, String comment, double x)
	{
		long nullTime = BenchUtils.getIterationTime(count);
	
		// Function calls
		long start = System.currentTimeMillis();
		for (int i = 0; i < count; i++)
			Math.log(x);
		long end = System.currentTimeMillis();
	
		report(count, "log ("+comment+")", count, "ops", end - start - nullTime);
		return count;
	}
	
	private static int benchLogNew(int count, String comment, double x)
	{
		long nullTime = BenchUtils.getIterationTime(count);
	
		// Function calls
		long start = System.currentTimeMillis();
		for (int i = 0; i < count; i++)
			BetaMath.log(x);
		long end = System.currentTimeMillis();
	
		report(count, "log (new, "+comment+")", count, "ops", end - start - nullTime);
		return count;
	}
	
	private static int benchDoubleToStr(int count, String comment, double x)
	{
		long nullTime = BenchUtils.getIterationTime(count);
		
		// Function calls
		long start = System.currentTimeMillis();
		for (int i = 0; i < count; i++)
			String.valueOf(x);
		long end = System.currentTimeMillis();
	
		report(count, "D2STR ("+comment+")", count, "ops", end - start - nullTime);
		return count;
	}

	private static int benchDoubleToStrNew(int count, String comment, double x)
	{
		long nullTime = BenchUtils.getIterationTime(count);
		
		// Function calls
		long start = System.currentTimeMillis();
		for (int i = 0; i < count; i++)
			BetaMath.doubleToString(x);
		long end = System.currentTimeMillis();
	
		report(count, "D2STR (new3, "+comment+")", count, "ops", end - start - nullTime);
		return count;
	}

	private static int benchFloatToStrNew(int count, String comment, double y)
	{
		float x = (float)y;
		long nullTime = BenchUtils.getIterationTime(count);
		
		// Function calls
		long start = System.currentTimeMillis();
		for (int i = 0; i < count; i++)
			BetaMath.floatToString(x);
		long end = System.currentTimeMillis();
	
		report(count, "F2STR (new3, "+comment+")", count, "ops", end - start - nullTime);
		return count;
	}

	public static void main(String[] args)
	{
		RConsole.open();
		System.setOut(new PrintStream(RConsole.openOutputStream()));
		System.out.println("MathBench " + VERSION);
	
		BenchUtils.cleanUp("At start");
	
		int iterate = 4000;
	
		int countAll = 0;
		long startAll = System.currentTimeMillis();
	
		countAll += benchSqrtHistoric1(iterate / 5, "subnormal", Math.PI * 0x1p-1060);
		BenchUtils.cleanUp(null);
		countAll += benchSqrtHistoric1(iterate, "normal", Math.PI);
		BenchUtils.cleanUp(null);
	
		countAll += benchSqrtHistoric2(iterate, "subnormal", Math.PI * 0x1p-1060);
		BenchUtils.cleanUp(null);
		countAll += benchSqrtHistoric2(iterate, "normal", Math.PI);
		BenchUtils.cleanUp(null);
	
		countAll += benchSqrtCurrent(iterate, "subnormal", Math.PI * 0x1p-1060);
		BenchUtils.cleanUp(null);
		countAll += benchSqrtCurrent(iterate, "normal", Math.PI);
		BenchUtils.cleanUp(null);
	
		countAll += benchSqrtNewF(iterate * 2, "subnormal", (float)(Math.PI * 0x1p-140));
		BenchUtils.cleanUp(null);	
		countAll += benchSqrtNewF(iterate * 2, "normal", (float)Math.PI);
		BenchUtils.cleanUp(null);

		//infinite loop for subnormal values
		countAll += benchLog(iterate / 100, "small", Math.PI * 0x1p-1000);
		BenchUtils.cleanUp(null);
		countAll += benchLog(iterate / 2, "medium", Math.PI);
		BenchUtils.cleanUp(null);
		countAll += benchLog(iterate / 100, "large", Math.PI * 0x1p+1000);
		BenchUtils.cleanUp(null);
		
		countAll += benchLogNew(iterate / 2, "subnormal", Math.PI * 0x1p-1060);
		BenchUtils.cleanUp(null);	
		countAll += benchLogNew(iterate / 2, "normal", Math.PI);
		BenchUtils.cleanUp(null);
		countAll += benchLogNew(iterate * 2, "1.0", 1.0);
		BenchUtils.cleanUp(null);
		countAll += benchLogNew(iterate, "1.0001", 1.01);
		BenchUtils.cleanUp(null);
		countAll += benchLogNew(iterate, "1.001", 1.01);
		BenchUtils.cleanUp(null);
		countAll += benchLogNew(iterate, "1.01", 1.01);
		BenchUtils.cleanUp(null);
		countAll += benchLogNew(iterate, "1.3", 1.1);
		BenchUtils.cleanUp(null);
		countAll += benchLogNew(iterate / 2, "1.9", 1.9);
		BenchUtils.cleanUp(null);
	
		countAll += benchDoubleToStr(iterate / 200, "PI*1E+300", Math.PI*1E+300);
		BenchUtils.cleanUp(null);
		countAll += benchDoubleToStr(iterate / 100, "PI*1E+100", Math.PI*1E+100);
		BenchUtils.cleanUp(null);
		countAll += benchDoubleToStr(iterate / 10, "PI*1E0", Math.PI*1E0);
		BenchUtils.cleanUp(null);
		countAll += benchDoubleToStr(iterate / 100, "PI*1E-100", Math.PI*1E-100);
		BenchUtils.cleanUp(null);
		countAll += benchDoubleToStr(iterate / 200, "PI*1E-300", Math.PI*1E-300);
		BenchUtils.cleanUp(null);
	
		countAll += benchDoubleToStrNew(iterate / 8, "PI*1E+300", Math.PI*1E+300);
		BenchUtils.cleanUp(null);
		countAll += benchDoubleToStrNew(iterate / 8, "PI*1E+100", Math.PI*1E+100);
		BenchUtils.cleanUp(null);
		countAll += benchDoubleToStrNew(iterate / 8, "PI*1E0", Math.PI*1E0);
		BenchUtils.cleanUp(null);
		countAll += benchDoubleToStrNew(iterate / 8, "PI*1E-100", Math.PI*1E-100);
		BenchUtils.cleanUp(null);
		countAll += benchDoubleToStrNew(iterate / 8, "PI*1E-300", Math.PI*1E-300);
		BenchUtils.cleanUp(null);
	
		countAll += benchFloatToStrNew(iterate / 4, "PI*1E+30", Math.PI*1E+30);
		BenchUtils.cleanUp(null);
		countAll += benchFloatToStrNew(iterate / 4, "PI*1E+10", Math.PI*1E+10);
		BenchUtils.cleanUp(null);
		countAll += benchFloatToStrNew(iterate / 4, "PI*1E0", Math.PI*1E0);
		BenchUtils.cleanUp(null);
		countAll += benchFloatToStrNew(iterate / 4, "PI*1E-10", Math.PI*1E-10);
		BenchUtils.cleanUp(null);
		countAll += benchFloatToStrNew(iterate / 4, "PI*1E-30", Math.PI*1E-30);
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

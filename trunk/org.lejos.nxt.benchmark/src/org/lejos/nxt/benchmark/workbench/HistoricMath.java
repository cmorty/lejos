package org.lejos.nxt.benchmark.workbench;

public class HistoricMath
{
	/**
	 * Square root author Paulo Costa
	 */
	public static double sqrt(double x)
	{
		double root = x, guess = 0;

		if (x < 0)
			return Double.NaN;

		// the accuracy test is percentual
		for (int i = 0; (i < 22) && ((guess > x * (1 + 5e-7f)) || (guess < x * (1 - 5e-7f))); i++)
		{
			root = (root + x / root) * 0.5f; // a multiplication is faster than a division
			guess = root * root; // cache the square to the test
		}
		return root;
	}

}

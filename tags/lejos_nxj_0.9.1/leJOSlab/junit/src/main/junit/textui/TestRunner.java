package junit.textui;

import junit.framework.Test;
import junit.framework.TestResult;

/**
 * A TestRunner running JUnit tests against leJOS runtime class library.
 * 
 * This TestRunner has to be started through a main program of the test cases.
 * Example:
 * 
 * <pre>
 * public class MyTest {
 * 	public static void main(String[] args) {
 * 		junit.textui.TestRunner.main(suite());
 * 	}
 * 
 * 	public static Test suite() {
 * 		TestSuite suite = new TestSuite();
 * 		suite.addTest(new OneTest(&quot;testOne&quot;));
 * 		suite.addTest(new OneTest(&quot;testTwo&quot;));
 * 		return suite;
 * 	}
 * }
 * </pre>
 * 
 * @author <a href="mailto:jochen.hiller@t-online.de">Jochen Hiller</a>
 */
public class TestRunner {

	// static methods

	/**
	 * Main program to start the test runner. Will exit the program after
	 * completion.
	 * 
	 * @param suite
	 *            the test or test suite to run
	 * @throws RuntimeException
	 *             will be raised through an internal error at TestRunner
	 */
	public static void main(Test suite) throws RuntimeException {
		try {
			TestRunner runner = new TestRunner();

			TestResult result = runner.doRun(suite);
			if (!result.wasSuccessful()) {
				System.exit(-1);
			}
			// otherwise all OK
			System.exit(0);
		} catch (Exception ex) {
			// throw the exception as a runtime exception,
			// to make it visible
			throw new RuntimeException();
		}
	}

	// public methods

	/**
	 * Run a test or test suite.
	 * 
	 * @param suite
	 *            the tests to run
	 * @return the test result of the run
	 */
	public TestResult doRun(Test suite) {
		ResultPrinter printer = new ResultPrinter(System.out);
		TestResult testResult = new TestResult();
		testResult.setListener(printer);

		long startTime = System.currentTimeMillis();
		suite.run(testResult);
		long endTime = System.currentTimeMillis();
		long runTime = endTime - startTime;

		printer.print(testResult, runTime);
		return testResult;
	}
}
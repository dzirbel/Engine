package tests;

/**
 * Represents an individual test that can be executed by the Tester.
 * This test must have the methods to initialize, run, and exit,
 *  as well as any that the Tester must call to find test-specific commands, such as whether or not to create a window.
 * 
 * @author Dominic
 */
public abstract class Test
{
	/**
	 * Initializes the Test.
	 * 
	 * @param tester - the Tester that was used to execute this test.
	 */
	public abstract void init(Tester tester);
	
	/**
	 * Runs the Test.
	 */
	public abstract void run();
	
	/**
	 * Exits the Test.
	 */
	public abstract void exit();
	
	/**
	 * Returns an integer code determining what type of window should be created, if any.<br>
	 * 0: no window should be created<br>
	 * 1: a full-screen window should be created<br>
	 * 2: a windowed window should be created<br>
	 * 
	 * @return window - 0 for no window, 1 for a full-screen window, and 2 for a windowed window
	 */
	public abstract int createWindow();
	
	/**
	 * Returns the name of this test, used for printing and identification purposes.
	 * 
	 * @return name - the name of this test
	 */
	public abstract String getName();
}

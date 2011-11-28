package tests;

import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferStrategy;

import javax.swing.JFrame;

/**
 * This class runs Tests and performs test-common tasks such as creating a full-screen window.
 * In each Test's main() method, a command creates a new Tester and runs the test() method.
 * This method uses the methods specified in the Test abstract class to initialize, run, and then exit the Test.
 * 
 * @author Dominic
 */
public class Tester
{
	public static final int NONE = 0;
	public static final int FSEM = 1;
	public static final int WINDOWED = 2;
	
	private JFrame frame;
	
	private GraphicsDevice device;
	private GraphicsEnvironment environment;
	
	/**
	 * Runs the given test.
	 * First, this method creates a window depending on the return of the test's createWindow() method.
	 * Then, the test is executed by calling the Test's init() (with this object as the parameter), run(), and exit() methods.
	 * Finally, the full-screen window is closed and if the given exit flag is true, the program exits.
	 * 
	 * @param test - the test to run
	 * @param exit - true for System.exit(0) to be called upon completion of the test, false otherwise
	 */
	public void test(Test test, boolean exit)
	{
		environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
		device = environment.getDefaultScreenDevice();
		if (test.createWindow() == FSEM)
		{
			frame = new JFrame("Test");
			frame.setUndecorated(true);
			frame.setResizable(false);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.requestFocus();
			device.setFullScreenWindow(frame);
			frame.createBufferStrategy(2);
		}
		else if (test.createWindow() == WINDOWED)
		{
			frame = new JFrame("Test");
			frame.setSize(640, 640);
			frame.setVisible(true);
		}
		else
		{
			frame = null;
		}
		test.init(this);
		test.run();
		test.exit();
		if (test.createWindow() == FSEM || test.createWindow() == WINDOWED)
		{
			frame.setVisible(false);
		}
		if (exit)
		{
			System.exit(0);
		}
	}
	
	/**
	 * Runs the given test.
	 * First, this method creates a window depending on the return of the test's createWindow() method.
	 * Then, the test is executed by calling the Test's init() (with this object as the parameter), run(), and exit() methods.
	 * Finally, the full-screen window is closed, but this will not call System.exit(0) and thus will not end the program's execution.
	 * 
	 * @param test - the test to run
	 */
	public void test(Test test)
	{
		test(test, false);
	}
	
	/**
	 * Returns the BufferStrategy that the full-screen window uses to control flicker.
	 * 
	 * @return strategy - the window's BufferStrategy, null if the window has not been created
	 */
	public BufferStrategy getStrategy()
	{
		if (frame != null)
		{
			return frame.getBufferStrategy();
		}
		return null;
	}
	
	/**
	 * Returns the default graphics device, possibly used to set the full-screen window.
	 * 
	 * @return device - the device that set the full-screen window, null if it was not set
	 */
	public GraphicsDevice getDevice()
	{
		return device;
	}
	
	/**
	 * Returns the window.
	 * 
	 * @return frame - the full-screen window, null if it was not created
	 */
	public JFrame getFrame()
	{
		return frame;
	}
	
	/**
	 * Returns the graphics environment that was used to find the graphics device.
	 * 
	 * @return environment - the default graphics environment, null if it was not used
	 */
	public GraphicsEnvironment getEnvironment()
	{
		return environment;
	}
	
	/**
	 * Returns the display configuration's current display mode.
	 * 
	 * @return mode - the display configuration's current display mode.
	 */
	public DisplayMode getDisplayMode()
	{
		return device.getDisplayMode();
	}
}

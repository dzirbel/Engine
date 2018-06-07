package graphics;

import io.Listener;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import javax.swing.JFrame;

/**
 * This convenience class can be used to monitor the display configuration and find the best
 *  display mode.
 */
public class DisplayMonitor
{
    /**
     * The current size of the screen, in pixels.
     */
    public static Dimension screen;

    private static GraphicsEnvironment environment;
    private static GraphicsDevice device;

    /**
     * Returns the best (highest resolution) display mode for the given GraphicsDevice.
     *
     * @param device - the device used to find the optimal display mode
     * @return the best display mode for the given device
     */
    public static DisplayMode getBestDisplayMode(GraphicsDevice device)
    {
        DisplayMode[] modes = device.getDisplayModes();
        int maxWidth = 0;
        int maxHeight = 0;
        int maxBitDepth = 0;
        int maxRefreshRate = 0;
        for (int i = 0; i < modes.length; i++)
        {
            if (modes[i].getWidth() > maxWidth || modes[i].getHeight() > maxHeight ||
                    (modes[i].getWidth() == maxWidth && modes[i].getHeight() == maxHeight &&
                    (modes[i].getBitDepth() > maxBitDepth ||
                            modes[i].getRefreshRate() > maxRefreshRate)))
            {
                maxHeight = modes[i].getHeight();
                maxWidth = modes[i].getWidth();
                maxBitDepth = modes[i].getBitDepth();
                maxRefreshRate = modes[i].getRefreshRate();
            }
        }
        return new DisplayMode(maxWidth, maxHeight, maxBitDepth, maxRefreshRate);
    }

    /**
     * Sets the display mode of the current screen configuration to the optimal display mode found
     *  by {@link #getBestDisplayMode(GraphicsDevice)} (if possible) and updates the screen size.
     *
     * @param device - the device used to set the display mode of the screen
     */
    public static void setDisplayMode()
    {
        if (device == null)
        {
            environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
            device = environment.getDefaultScreenDevice();
        }

        DisplayMode displayMode = DisplayMonitor.getBestDisplayMode(device);
        if (device.isDisplayChangeSupported())
        {
            device.setDisplayMode(displayMode);
        }
        screen = new Dimension(device.getDisplayMode().getWidth(),
                device.getDisplayMode().getHeight());
    }

    /**
     * Gets the current size of the screen in pixels.
     *
     * @return the screen size, equivalent to {@link #screen}.
     */
    public static Dimension getScreenSize()
    {
        return screen;
    }

    /**
     * Creates a {@link JFrame} with the given title and initializes it fully.
     * First, the frame is created and the {@link Listener} is initialized to listen to it.
     * Then the optimal display mode is found and, if possible, the screen size is set; the screen
     *  size is then measured.
     * Finally, the frame is placed on the screen as a full-screen window in full-screen exclusive
     *  mode (FSEM) and a double-buffered strategy is created.
     * Note that the icon image of the frame is not set and the content pane of the frame is empty.
     *
     * @param title -  the title of the frame
     * @param content - the content pane for the frame to be created, set with
     *  {@link JFrame#setContentPane(java.awt.Container)}, or null to set nothing as the content
     *  pane
     * @return the frame created and set as the full-screen window
     */
    public static JFrame createFrame(String title, Container content)
    {
        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        if (content != null)
        {
            frame.setContentPane(content);
        }
        frame.setUndecorated(true);
        frame.setResizable(false);
        Listener.init(frame);

        environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        device = environment.getDefaultScreenDevice();

        setDisplayMode();

        device.setFullScreenWindow(frame);
        frame.createBufferStrategy(2);

        return frame;
    }

    /**
     * Determines whether the current display is display change supported, as given by
     *  {@link GraphicsDevice#isDisplayChangeSupported()}.
     *
     * @return true if the device is display change supported, false otherwise
     * @see GraphicsDevice#isDisplayChangeSupported()
     */
    public static boolean isDisplayChangeSupported()
    {
        if (device == null)
        {
            environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
            device = environment.getDefaultScreenDevice();
        }
        return device.isDisplayChangeSupported();
    }
}

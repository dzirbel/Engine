package graphics;

import java.awt.DisplayMode;
import java.awt.GraphicsDevice;

/**
 * This convenience class can be used to monitor the display configuration and find the best
 *  display mode.
 * 
 * @author zirbinator
 */
public class DisplayMonitor
{
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
}

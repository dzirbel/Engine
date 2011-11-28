package graphics;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;

/**
 * This convenience class can be used to monitor the display configuration and find the best display mode.
 * 
 * @author Dominic
 */
public class DisplayMonitor 
{	
	/**
	 * Returns the best (highest resolution) display mode for the given GraphicsDevice.
	 * 
	 * @param device - the device used to find the optimal display mode
	 * @return best - the best display mode for the given device
	 */
	public static DisplayMode getBestDisplayMode(GraphicsDevice device)
	{
		DisplayMode[] goodModes = device.getDisplayModes();
		int maxWidth = 0;
		int maxHeight = 0;
		int maxBitDepth = 0;
		int maxRefreshRate = 0;
		for (int i = 0; i < goodModes.length; i++)
		{
			int width = goodModes[i].getWidth();
			int height = goodModes[i].getHeight();
			int bitDepth = goodModes[i].getBitDepth();
			int refreshRate = goodModes[i].getRefreshRate();
			if ((width > maxWidth || height > maxHeight) ||
					(width == maxWidth && height == maxHeight && (bitDepth > maxBitDepth || refreshRate > maxRefreshRate)))
			{
				maxHeight = height;
				maxWidth = width;
				maxBitDepth = bitDepth;
				maxRefreshRate = refreshRate;
			}
		}
		return new DisplayMode(maxWidth, maxHeight, maxBitDepth, maxRefreshRate);
	}
}

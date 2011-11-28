package graphics;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.awt.image.VolatileImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import utils.ExceptionHandler;

/**
 * Gives a way of drawing hardware-accelerated images which can have significant performance boosts.
 * When created, an Image is stored as a BufferedImage which can be slow to draw.
 * When the drawing method is called, a new VolitileImage, a fast to draw Image which can lose its contents, is drawn to using the drawToVolitleImage method.
 * Every time the draw method is called, the contents of the VolatileImage's contents are checked.
 * If they are lost, the VolitleImage is drawn to again and then once the VolatileImage has contents, the VolatileImage itself is drawn.
 */
public class AcceleratedImage extends Image
{
	private AffineTransform transform;
	
	private BufferedImage bi = null;
	
	private int quality;
	public static final int OPAQUE = Transparency.OPAQUE;
	public static final int BITMASK = Transparency.BITMASK;
	public static final int TRANSLUCENT = Transparency.TRANSLUCENT;
	
	private VolatileImage vi = null;
	
	/**
	 * Creates a new AcceleratedImage with the given BufferedImage and quality.
	 * 
	 * @param bi - the Image that should be drawn
	 * @param quality - the quality: opaque, bitmask-transparent, or translucent
	 */
	public AcceleratedImage(BufferedImage bi, int quality)
	{
		this.bi = bi;
		this.quality = quality;
		transform = new AffineTransform();
	}
	
	/**
	 * Creates a new AcceleratedImage with the given BufferedImage and the BufferedImage's quality.
	 * 
	 * @param bi - the Image that should be drawn
	 */
	public AcceleratedImage(BufferedImage bi)
	{
		this.bi = bi;
		quality = bi.getTransparency();
		transform = new AffineTransform();
	}
	
	/**
	 * Creates a new AcceleratedImage with the Image found at the filename and quality.
	 * 
	 * @param filename - the location of the Image that should be drawn
	 * @param quality - the quality: opaque, bitmask-transparent, or translucent
	 */
	public AcceleratedImage(String filename, int quality)
	{
		try
		{
			bi = ImageIO.read(new File(filename));
		}
		catch (IOException ex) 
		{
			ExceptionHandler.receive(ex, filename, "The specified quality was " + quality + ".");
		}
		bi.getTransparency();
		this.quality = quality;
		transform = new AffineTransform();
	}
	
	/**
	 * Creates a new AcceleratedImage with the Image found at the filename with the detected quality.
	 * 
	 * @param filename - the location of the Image that should be drawn
	 */
	public AcceleratedImage(String filename)
	{
		try
		{
			bi = ImageIO.read(new File(filename));
		}
		catch (IOException ex) 
		{
			ExceptionHandler.receive(ex, filename);
		}
		quality = bi.getTransparency();
		transform = new AffineTransform();
	}
	
	/**
	 * Loads the given Image found at the filename, with the given quality.
	 * 
	 * @param filename - the location of the Image to be loaded
	 * @param quality - the quality: opaque, bitmask-transparent, or translucent
	 */
	public void loadImage(String filename, int quality)
	{
		try
		{
			bi = ImageIO.read(new File(filename));
		}
		catch (IOException ex) 
		{
			ExceptionHandler.receive(ex, filename, "The specified quality was " + quality + ".");
		}
		this.quality = quality;
	}
	
	/**
	 * Loads the given Image found at the filename, with the detected quality.
	 * Returns a flag that determines whether or not the Image was properly loaded.
	 * If the image was not correctly loaded, the BufferedImage will remain the same as the one given at instantiation.
	 * 
	 * @param filename - the location of the Image to be loaded
	 * @return loaded - true if the the image was properly loaded, false otherwise
	 */
	public void loadImage(String filename)
	{
		try
		{
			bi = ImageIO.read(new File(filename));
		}
		catch (IOException ex) 
		{
			ExceptionHandler.receive(ex, filename, "The specified quality was " + quality + ".");
		}
		quality = bi.getTransparency();
	}
	
	/**
	 * Sets the BufferedImage to the given one.
	 * Note: this method keeps the prespecified quality, even if the given image has a different quality.
	 * 
	 * @param bi - the new BufferedImage;
	 */
	public void setImage(BufferedImage bi)
	{
		this.bi = bi;
	}
	
	/**
	 * Sets the current transformation matrix to the given one.
	 * 
	 * @param tranform - the new transformation matrix
	 */
	public void setTransform(AffineTransform tranform)
	{
		transform = new AffineTransform(transform);
	}
	
	/**
	 * Sets the current scale of the transformation matrix to the given values.
	 * Note: this method resets the previous scale before scaling.
	 * 
	 * @param x - the x-component of the scale
	 * @param y - the y-component of the scale
	 */
	public void setScale(double x, double y)
	{
		transform.setToScale(x, y);
	}
	
	/**
	 * Sets the current rotation of the transformation matrix to the given theta.
	 * Note: this method resets the previous rotation before rotating.
	 * 
	 * @param theta - the rotation amount in radians (positive rotations rotates from the positive x-axis toward the positive y-axis)
	 */
	public void setRotation(double theta)
	{
		transform.setToRotation(theta);
	}
	
	/**
	 * Creates a blank VolatileImage with the width and height of the given Image and accessing the given GraphicsConfiguration 
	 * 
	 * @param bi - the Image that the VolatileImage's size should be based on
	 * @param config - the current GraphicsConfiguration
	 * @return VolatileImage - the blank, accelerated VolatileImage with the same size as the given Image
	 */
	private VolatileImage createVolatileImage(BufferedImage bi, GraphicsConfiguration config)
	{
		return config.createCompatibleVolatileImage(bi.getWidth(), bi.getHeight(), quality);
	}
	
	/**
	 * Draws the Image that this AcceleratedImage was constructed with at a significantly faster speed.
	 * First, the current GraphicsConfiguration is found using the given Graphics.
	 * If the VolatileImage is null (this is the first time that the VolatileImage has been drawn),
	 *  the VolatileImage is created using createVolatileImage and then is drawn to.
	 * Next, the common drawing sequence is initiated.
	 * First, if the VolatileImage has been restored the contents have been lost and the VolatileImage is drawn to.
	 * Next, if the VolatileImage's state is incompatible with the current GraphicsConfiguration it is completely remade and redrawn.
	 * Finally, the VolatileImage is drawn using the originally given Graphics context.
	 * If, at the end of the common drawing sequence, the VolatileImage has lost its contents, the common drawing sequence is looped.
	 * 
	 * @param x - the x-coordinate where the AcceleratedImage should be drawn
	 * @param y - the y-coordinate where the AcceleratedImage should be drawn
	 * @param g - the Graphics context
	 */
	public void draw(int x, int y, Graphics2D g)
	{
		GraphicsConfiguration config = g.getDeviceConfiguration();
		if (vi == null)
		{
			vi = createVolatileImage(bi, config);
			drawToVolatileImage(config);
		}
		do
		{
			int returnCode = vi.validate(config);
			if (returnCode == VolatileImage.IMAGE_RESTORED)
			{
				drawToVolatileImage(config);
			}
			else if (returnCode == VolatileImage.IMAGE_INCOMPATIBLE)
			{
				vi = createVolatileImage(bi, config);
				drawToVolatileImage(config);
			}
			if (transform.isIdentity())
			{
				g.drawImage(vi, x, y, null);
			}
			else
			{
				g.setTransform(transform);
				g.drawImage(vi, x, y, null);
				g.setTransform(new AffineTransform());
			}
		} 
		while (vi.contentsLost());
	}
	
	/**
	 * Draws the Image that this AcceleratedImage was constructed with at a significantly faster speed.
	 * First, the current GraphicsConfiguration is found using the given Graphics.
	 * If the VolatileImage is null (this is the first time that the VolatileImage has been drawn),
	 *  the VolatileImage is created using createVolatileImage and then is drawn to.
	 * Next, the common drawing sequence is initiated.
	 * First, if the VolatileImage has been restored the contents have been lost and the VolatileImage is drawn to.
	 * Next, if the VolatileImage's state is incompatible with the current GraphicsConfiguration it is completely remade and redrawn.
	 * Finally, the VolatileImage is drawn using the originally given Graphics context within the Rectangle of the given width and height.
	 * The VolatileImage will not be scaled outside of the scaling used by the AffineTransform to fit this rectangle, rather it will be cropped.
	 * If, at the end of the common drawing sequence, the VolatileImage has lost its contents, the common drawing sequence is looped.
	 * 
	 * @param x - the x-coordinate where the AcceleratedImage should be drawn
	 * @param y - the y-coordinate where the AcceleratedImage should be drawn
	 * @param width - the width of the rectangle in which the AccleratedImage should be drawn
	 * @param height - the height of the rectangle in which the AccleratedImage should be drawn
	 * @param g - the Graphics context
	 */
	public void draw(int x, int y, int width, int height, Graphics2D g)
	{
		GraphicsConfiguration config = g.getDeviceConfiguration();
		if (vi == null)
		{
			vi = createVolatileImage(bi, config);
			drawToVolatileImage(config);
		}
		do
		{
			int returnCode = vi.validate(config);
			if (returnCode == VolatileImage.IMAGE_RESTORED)
			{
				drawToVolatileImage(config);
			}
			else if (returnCode == VolatileImage.IMAGE_INCOMPATIBLE)
			{
				vi = createVolatileImage(bi, config);
				drawToVolatileImage(config);
			}
			if (transform.isIdentity())
			{
				g.drawImage(vi, x, y, null);
			}
			else
			{
				g.setTransform(transform);
				g.drawImage(vi, x, y, width, height, null);
				g.setTransform(new AffineTransform());
			}
		} 
		while (vi.contentsLost());
	}
	
	/**
	 * Using the VolatileImage's Graphics context, the BufferedImage is drawn onto the VolatileImage.
	 * To accomplish this, the VolatileImage must first be filled with a completely transparent color.
	 * Next, the BufferedImage is drawn at 0,0.
	 * The BufferedImage should have the same size as the VolatileImage because the VolatileImage was created based on the BufferedImage.
	 * This entire sequence is looped if, at the end, the VolatileImage has lost its contents.
	 * 
	 * @param config - the current GraphicsConfiguration
	 */
	private void drawToVolatileImage(GraphicsConfiguration config)
	{
		do
		{
			if (vi.validate(config) == VolatileImage.IMAGE_INCOMPATIBLE)
			{
				vi = createVolatileImage(bi, config);
			}
			Graphics2D viGraphics = vi.createGraphics();
			viGraphics.setComposite(AlphaComposite.Src);
			viGraphics.setColor(new Color(0, 0, 0, 0));
			viGraphics.fillRect(0, 0, vi.getWidth(), vi.getHeight());
			viGraphics.drawImage(bi, 0, 0, null);
			viGraphics.dispose();
		}
		while (vi.contentsLost());
	}
	
	/**
	 * Returns the width of the image by finding the width of the BufferedImage.
	 * 
	 * @return width - the width of the AcceleratedImage
	 */
	public int getWidth()
	{
		return bi.getWidth();
	}
	
	/**
	 * Returns the height of the image by finding the height of the BufferedImage.
	 * 
	 * @return height - the height of the AcceleratedImage
	 */
	public int getHeight()
	{
		return bi.getHeight();
	}
	
	/**
	 * Returns the quality of the image - opaque, bitmask-transparent, or translucent.
	 * 
	 * @return quality - the quality
	 */
	public int getQuality()
	{
		return quality;
	}
	
	/**
	 * Returns the Graphics of the BufferedImage.
	 * 
	 * @return graphics - the Graphics of the BufferedImage
	 */
	public Graphics getGraphics()
	{
		return bi.getGraphics();
	}
	
	/**
	 * Returns the BufferedImage.
	 * 
	 * @return bi - the BufferedImage
	 */
	public BufferedImage getBufferedImage()
	{
		return bi;
	}
	
	/**
	 * Returns the width of the image.
	 * 
	 * @return width - the width of the BufferedImage
	 */
	public int getWidth(ImageObserver observer)
	{
		return getWidth();
	}
	
	/**
	 * Returns the height of the image.
	 * 
	 * @return height - the height of the BufferedImage
	 */
	public int getHeight(ImageObserver observer)
	{
		return getHeight();
	}
	
	/**
	 * Returns the property of the BufferedImage with the given name and observer.
	 */
	public Object getProperty(String name, ImageObserver observer)
	{
		return bi.getProperty(name, observer);
	}
	
	/**
	 * Returns the source of the BufferedImage.
	 */
	public ImageProducer getSource()
	{
		return bi.getSource();
	}
	
	/**
	 * Returns the text name of the given quality, with no caps.
	 * 
	 * @param quality - the quality to be named
	 * @return name - the name of the given quality, "unknown" if the quality is unknown
	 */
	public static String getQualityName(int quality)
	{
		if (quality == OPAQUE)
		{
			return "opaque";
		}
		if (quality == BITMASK)
		{
			return "bitmask-transparent";
		}
		if (quality == TRANSLUCENT)
		{
			return "translucent";
		}
		return "unknown";
	}
}

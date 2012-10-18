package graphics;

import java.awt.AlphaComposite;
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

/**
 * Provides a hardware-accelerated and easy-to-use renderable image.
 * The image's contents are held in a BufferedImage and are copied to a VolatileImage to be
 *  rendered quickly and with a specified quality.
 * Additionally, an AffineTransform can be used to transform the image.
 * 
 * @author zirbinator
 */
public class AcceleratedImage extends Image
{
    /**
     * The AffineTransform applied to this AcceleratedImage when drawing.
     */
    private AffineTransform transform;
    
    /**
     * The BufferedImage holding the contents of this AcceleratedImage.
     */
    private BufferedImage bi;
    
    private int quality;
    /**
     * Opaque quality: all pixels are drawn over by the image, no transparency is allowed.
     * That is, all pixels of this image have an alpha value of 1.0.
     * This is the lowest quality but is typically rendered the most quickly.
     * 
     * @see Transparency#OPAQUE
     */
    public static final int OPAQUE = Transparency.OPAQUE;
    /**
     * Bitmask-transparent quality: all pixels are either drawn over entirely or untouched.
     * That is, all pixels or this image have an alpha value of either 1.0 or 0.0.
     * This is medium quality and is rendered at an average speed.
     * 
     * @see Transparency#BITMASK
     */
    public static final int BITMASK = Transparency.BITMASK;
    /**
     * Translucent quality: pixels can be drawn over not at all, partially, or completely.
     * That is, the pixels of this image can have any alpha value between 0.0 and 1.0, inclusive.
     * This is the highest quality and is rendered the most slowly.
     * 
     * @see Transparency#TRANSLUCENT
     */
    public static final int TRANSLUCENT = Transparency.TRANSLUCENT;
    
    /**
     * The VolatileImage used to draw directly on the screen, but which may lose its contents.
     * It is typically referred to as this AcceleratedImage' "buffer".
     */
    private VolatileImage vi;
    
    /**
     * Creates a new AcceleratedImage with the given BufferedImage and quality.
     * 
     * @param bi - the contents of the image that should be held by this AcceleratedImage
     * @param quality - the quality: {@link #OPAQUE}, {@link #BITMASK}, or {@link #TRANSLUCENT}
     * @throws IllegalArgumentException thrown if the given quality is unknown
     */
    public AcceleratedImage(BufferedImage bi, int quality)
    {
        this.bi = bi;
        this.quality = quality;
        if (quality != OPAQUE && quality != BITMASK && quality != TRANSLUCENT)
        {
            throw new IllegalArgumentException("Illegal quality: " + quality);
        }
        transform = new AffineTransform();
        vi = null;
    }
    
    /**
     * Creates a new AcceleratedImage with the given BufferedImage.
     * The quality given with {@code bi.getTransparency()} is used for the quality of the
     *  AcceleratedImage.
     * 
     * @param bi - the contents of the image that should
     *  be held by this AcceleratedImage
     */
    public AcceleratedImage(BufferedImage bi)
    {
        this.bi = bi;
        quality = bi.getTransparency();
        transform = new AffineTransform();
        vi = null;
    }
    
    /**
     * Creates a new AcceleratedImage with the contents of the given image filename and the given
     *  quality.
     * 
     * @param filename - the location of the contents of this AccelerateImage
     * @param quality - the quality: {@link #OPAQUE}, {@link #BITMASK}, or {@link #TRANSLUCENT}
     * @throws IOException thrown if there is any error reading from the given filename
     */
    public AcceleratedImage(String filename, int quality) throws IOException
    {
        loadImage(filename, quality);
        transform = new AffineTransform();
        vi = null;
    }
    
    /**
     * Creates a new AcceleratedImage with the contents of the given image filename and the
     *  detected quality.
     * 
     * @param filename - the location of the contents of this AcceleratedImage
     * @throws IOException thrown if there is any error reading from the given filename
     */
    public AcceleratedImage(String filename) throws IOException
    {
        loadImage(filename, -1);
        transform = new AffineTransform();
        vi = null;
    }
    
    /**
     * Loads the given image found at the filename into the contents of this AcccelerateImage,
     *  along with the given quality.
     * 
     * @param filename - the location of the contents of this AccelerateImage
     * @param quality - the quality: {@link #OPAQUE}, {@link #BITMASK}, {@link #TRANSLUCENT}, or
     *  {@code -1} to use the quality of the loaded image
     * @throws IOException thrown if there is any error reading from the given filename
     * @throws IllegalArgumentException thrown if the given quality is unknown and not {@code -1}
     */
    public void loadImage(String filename, int quality) throws IOException
    {
        bi = ImageIO.read(new File(filename));
        if (quality == -1)
        {
            this.quality = bi.getTransparency();
        }
        else if (quality == OPAQUE || quality == BITMASK || quality == TRANSLUCENT)
        {
            this.quality = quality;
        }
        else
        {
            throw new IllegalArgumentException("Illegal quality: " + quality);
        }
    }
    
    /**
     * Returns the contents of this AcceleratedImage.
     * Note: the contents are not cloned, so any changes to the returned BufferedImage will affect
     *  this AcceleratedImage.
     * 
     * @return bi - the contents of this AcceleratedImage stored as a BufferedImage
     */
    public BufferedImage getContents()
    {
        return bi;
    }
    
    /**
     * Sets the contents of this AcceleratedImage.
     * Note: this method keeps the previously given quality, even if the given image has a
     *  different quality.
     * 
     * @param bi - the new contents for this AcceleratedImage
     */
    public void setContents(BufferedImage bi)
    {
        this.bi = bi;
    }
    
    /**
     * Returns the width of this AcceleratedImage in pixels.
     * This is equivalent to calling {@code getBufferedImage().getWidth()}.
     * 
     * @return the width of this AcceleratedImage
     */
    public int getWidth()
    {
        return bi.getWidth();
    }
    
    /**
     * Returns the height of this AcceleratedImage in pixels.
     * This is equivalent to calling {@code getBufferedImage().getHeight()}.
     * 
     * @return the height of this AcceleratedImage
     */
    public int getHeight()
    {
        return bi.getHeight();
    }
    
    /**
     * Returns the quality of this AcceleratedImage - {@link #OPAQUE}, {@link #BITMASK}, or
     *  {@link #TRANSLUCENT}.
     * The quality determines how the image should be rendered and typically affects the
     *  performance of the AcceleratedImage.
     * 
     * @return the quality of this AcceleratedImage
     */
    public int getQuality()
    {
        return quality;
    }
    
    /**
     * Sets the quality of this AcceleratedImage to the given quality.
     * If the quality is changed, the volatile buffer of this AcceleratedImage will be
     *  reconstructed when the AcceleratedImage is next drawn.
     * 
     * @param quality - the quality of this AcceleratedImage
     * @throws IllegalArgumentException thrown if the given quality is unknown
     */
    public void setQuality(int quality)
    {
        if (this.quality != quality)
        {
            if (quality != OPAQUE && quality != BITMASK && quality != TRANSLUCENT)
            {
                throw new IllegalArgumentException("Illegal quality: " + quality);
            }
            
            this.quality = quality;
            vi = null;
        }
    }
    
    /**
     * Returns the Graphics of this AcceleratedImage.
     * This is equivalent to calling {@code getBufferedImage().getGraphics()}.
     * 
     * @return the Graphics of the contents of this AcceleratedImage
     */
    public Graphics getGraphics()
    {
        return bi.getGraphics();
    }
    
    /**
     * Returns the width of this AcceleratedImage in pixels.
     * This is equivalent to calling {@link #getWidth()}.
     * 
     * @param observer - an observer for this image, ignored
     * @return the width of this AcceleratedImage
     */
    public int getWidth(ImageObserver observer)
    {
        return getWidth();
    }
    
    /**
     * Returns the height of this AcceleratedImage in pixels.
     * This is equivalent to calling {@link #getHeight()}.
     * 
     * @param observer - an observer for this image, ignored
     * @return the height of this AcceleratedImage
     */
    public int getHeight(ImageObserver observer)
    {
        return getHeight();
    }
    
    /**
     * Returns the property of the contents of this AcceleratedImage with the given name and
     *  observer.
     * This is equivalent to calling {@code getBufferedImage().getProperty(name, observer)}.
     * 
     * @param name - the name of the property to get
     * @param observer - an observer for this image
     * @return the property given by the contents of this AcceleratedImage
     */
    public Object getProperty(String name, ImageObserver observer)
    {
        return bi.getProperty(name, observer);
    }
    
    /**
     * Returns the source of the contents of this AcceleratedImage.
     * This is equivalent to calling {@code getBufferedImage().getSource()}.
     * 
     * @return the source of this image
     */
    public ImageProducer getSource()
    {
        return bi.getSource();
    }
    
    /**
     * Gets the AffineTransform applied to this AcceleratedImage when drawing.
     * Note: the returned AffineTransform is not cloned,
     *  so any changes made to it will be reflected in the AcceleratedImage.
     * 
     * @return the transformation matrix used by this AcceleratedImage
     */
    public AffineTransform getTransform()
    {
        return transform;
    }
    
    /**
     * Sets the AffineTransform applied to this AcceleratedImage
     *  when drawing to the given one.
     * 
     * @param tranform - the new transformation matrix
     */
    public void setTransform(AffineTransform transform)
    {
        this.transform = new AffineTransform(transform);
    }
    
    /**
     * Sets the scale of the transformation matrix to the given values.
     * Note: this method resets the previous scale before scaling.
     * This is equivalent to calling {@code getTransform().setToScale(x, y)}.
     * 
     * @param x - the x-component of the scale
     * @param y - the y-component of the scale
     * @see AffineTransform#setToScale(double, double)
     */
    public void setScale(double x, double y)
    {
        transform.setToScale(x, y);
    }
    
    /**
     * Sets the rotation of the transformation matrix to the given theta.
     * Note: this method resets the previous rotation before rotating.
     * This is equivalent to calling {@code getTransform().setToRotation(theta)}.
     * 
     * @param theta - the rotation amount in radians (positive rotations rotates from the positive
     *  x-axis toward the positive y-axis)
     * @see AffineTransform#setToRotation(double)
     */
    public void setRotation(double theta)
    {
        transform.setToRotation(theta);
    }
    
    /**
     * Draws the contents of this AcceleratedImage as quickly as possible.
     * First, the volatile buffer is checked and created if needed.
     * Then, until the volatile buffer retains its contents, it is drawn onto the given Graphics
     *  using this AcceleratedImage's AffineTransform.
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
            vi = config.createCompatibleVolatileImage(bi.getWidth(), bi.getHeight(), quality);
            drawToVolatileImage(config);
        }
        do
        {
            int code = vi.validate(config);
            if (code == VolatileImage.IMAGE_RESTORED)
            {
                drawToVolatileImage(config);
            }
            else if (code == VolatileImage.IMAGE_INCOMPATIBLE)
            {
                vi = config.createCompatibleVolatileImage(bi.getWidth(), bi.getHeight(), quality);
                drawToVolatileImage(config);
            }
            
            AffineTransform prev = g.getTransform();
            g.transform(transform);
            g.drawImage(vi, x, y, null);
            g.setTransform(prev);
        } while (vi.contentsLost());
    }
    
    /**
     * Validates the volatile buffer of this image using the given Graphics context.
     * That is, if the AcceleratedImage's quickly accessible buffer has been lost it is redrawn,
     *  or if the buffer has been destroyed it is restored and redrawn.
     * 
     * @param g - the context in which to validate and potentially recreate the image
     */
    public void validate(Graphics2D g)
    {
        GraphicsConfiguration config = g.getDeviceConfiguration();
        do
        {
            int code = vi.validate(config);
            if (code == VolatileImage.IMAGE_RESTORED)
            {
                drawToVolatileImage(config);
            }
            else if (code == VolatileImage.IMAGE_INCOMPATIBLE)
            {
                vi = config.createCompatibleVolatileImage(bi.getWidth(), bi.getHeight(), quality);
                drawToVolatileImage(config);
            }
        } while (vi.contentsLost());
    }
    
    /**
     * Draws the contents of this AcceleratedImage stored in a BufferedImage onto the
     *  VolatileImage.
     * The VolatileImage is validated and recreated if necessary, and the BufferedImage is drawn
     *  until the VolatileImage's contents are not lost.
     * 
     * @param config - the current GraphicsConfiguration
     */
    private void drawToVolatileImage(GraphicsConfiguration config)
    {
        do
        {
            if (vi.validate(config) == VolatileImage.IMAGE_INCOMPATIBLE)
            {
                vi = config.createCompatibleVolatileImage(bi.getWidth(), bi.getHeight(), quality);
            }
            Graphics2D viGraphics = vi.createGraphics();
            viGraphics.setComposite(AlphaComposite.Src);
            viGraphics.drawImage(bi, 0, 0, null);
            viGraphics.dispose();
        } while (vi.contentsLost());
    }
}

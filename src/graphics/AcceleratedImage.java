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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

/**
 * Provides a hardware-accelerated and easy-to-use renderable image.
 * The image's contents are held in a BufferedImage and are copied to a VolatileImage to be
 *  rendered quickly and with a specified quality.
 * Additionally, an AffineTransform can be used to transform the image.
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

    /**
     * The transparency used to draw the volatile buffer.
     */
    private float transparency;

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
        transparency = 1;
        vi = null;
    }

    /**
     * Creates a new AcceleratedImage with the given dimensions.
     * This is equivalent to the constructor:
     * <pre>
     * AcceleratedImage(new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB), AcceleratedImage.TRANSLUCENT)
     * </pre>
     *
     * @param width - the width of this AcceleratedImage's contents
     * @param height - the height of this AcceleratedImage's contents
     */
    public AcceleratedImage(int width, int height)
    {
        this(new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB),
                AcceleratedImage.TRANSLUCENT);
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
        transparency = 1;
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
        transparency = 1;
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
        this(filename, -1);
    }

    /**
     * Creates a new AcceleratedImage with the contents of the given InputStream and the given
     *  quality.
     *
     * @param stream - the source of data from which the contents of this AcceleratedImage should
     *  be read
     * @param quality - the quality: {@link #OPAQUE}, {@link #BITMASK}, or {@link #TRANSLUCENT}
     * @throws IOException thrown if there is any error reading from the given stream
     */
    public AcceleratedImage(InputStream stream, int quality) throws IOException
    {
        loadImage(stream, quality);
    }

    /**
     * Creates a new AcceleratedImage with the contents of the given InputStream and the detected
     *  quality.
     *
     * @param stream - the source of data from which the contents of this AcceleratedImage should
     *  be read
     * @throws IOException thrown if there is any error reading from the given stream
     */
    public AcceleratedImage(InputStream stream) throws IOException
    {
        this(stream, -1);
    }

    /**
     * Loads the image found at the filename into the contents of this AcccelerateImage,
     *  along with the given quality.
     * The current transform and transparency are reset to the identity matrix and 1.0.
     *
     * @param filename - the location of the contents of this AccelerateImage
     * @param quality - the quality: {@link #OPAQUE}, {@link #BITMASK}, {@link #TRANSLUCENT}, or
     *  {@code -1} to use the quality of the loaded image
     * @throws IOException thrown if there is any error reading from the given filename
     * @throws IllegalArgumentException thrown if the given quality is unknown and not {@code -1}
     * @see #loadImage(InputStream, int)
     */
    private void loadImage(String filename, int quality) throws IOException
    {
        loadImage(new FileInputStream(filename), quality);
    }

    /**
     * Loads the image from the given stream into the contents of this AcceleratedImage, along with
     *  the given quality.
     * The majority of the loading is done with {@link ImageIO#read(InputStream)}.
     * The current transform and transparency are reset to the identity matrix and 1.0.
     *
     * @param in - the source of the data for the contents of this AcceleratedImage
     * @param quality - the quality: {@link #OPAQUE}, {@link #BITMASK}, {@link #TRANSLUCENT}, or
     *  {@code -1} to use the quality of the loaded image
     * @throws IOException thrown if there is any error reading from the given stream
     * @throws IllegalArgumentException thrown if the given quality is unknown and not {@code -1}
     * @see {@link #loadImage(String, int)}
     */
    private void loadImage(InputStream in, int quality) throws IOException
    {
        bi = ImageIO.read(in);

        transform = new AffineTransform();
        transparency = 1;
        vi = null;
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
     * Sets the scale of the transformation matrix to the given values.
     * Note: this method resets the previous scale and all other transformations before scaling.
     * Use {@link #setTransform(AffineTransform)} to aggregate multiple transformations.
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
     * Note: this method resets the previous rotation and all other transformations before rotating.
     * Use {@link #setTransform(AffineTransform)} to aggregate multiple transformations.
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
     * Sets the rotation of the transformation matrix to the given theta, centered at the given
     *  location.
     * Note: this method resets the previous rotation and all other transformations before rotating.
     * Use {@link #setTransform(AffineTransform)} to aggregate multiple transformations.
     *
     * @param theta - the rotation amount in radians (positive rotations rotates from the positive
     *  x-axis toward the positive y-axis)
     * @param centerx - the x-coordinate of the anchor point
     * @param centery - the y-coordinate of the anchor point
     * @see AffineTransform#setToRotation(double, double, double)
     */
    public void setRotation(double theta, double centerx, double centery)
    {
        transform.setToRotation(theta, centerx, centery);
    }

    /**
     * Sets the translation of the transformation matrix to the given location.
     * Note: this method resets the previous translation
     *  and all other transformations before translating.
     * Use {@link #setTransform(AffineTransform)} to aggregate multiple transformations.
     *
     * @param x - the distance to translate in the x direction
     * @param y - the distance to translate in the y direction
     */
    public void setTranslation(double x, double y)
    {
        transform.setToTranslation(x, y);
    }

    /**
     * Gets a copy of the current transformation applied to this AcceleratedImage when drawing.
     *
     * @return the current transformation matrix
     * @see #setTransform(AffineTransform)
     */
    public AffineTransform getTransform()
    {
        return (AffineTransform) transform.clone();
    }

    /**
     * Sets the current transformation applied to this AcceleratedImage when drawing.
     * While methods such as {@link #setRotation(double)} and {@link #setScale(double, double)}
     *  can be used as a convenience, this functions allows for the full range of transformations
     *  as well as transformation aggregation.
     *
     * @param transform - the matrix used to transform this AcceleratedImage
     */
    public void setTransform(AffineTransform transform)
    {
        this.transform = new AffineTransform(transform);
    }

    /**
     * Gets the transparency with which this AcceleratedImage is currently drawing.
     * The returned transparency is between 0.0 and 1.0, where 0.0 is completely transparent and
     *  1.0 is completely opaque.
     *
     * @return the transparency used to draw the volatile buffer
     */
    public float getTransparency()
    {
        return transparency;
    }

    /**
     * Sets the transparency with which this AcceleratedImage should draw.
     * The transparency is set immediately, but the buffer is redrawn on the next call to
     *  {@link #draw(int, int, Graphics2D)} or {@link #validate(Graphics2D)} (if the given
     *  transparency is different than the current one, otherwise nothing happens).
     * Thus, if it is important that the draw times are consistent, a call to
     *  {@link #validate(Graphics2D)} should be made before the next draw.
     *
     * @param transparency - the transparency with which to draw, between 0 and 1
     */
    public void setTransparency(float transparency)
    {
        if (this.transparency != transparency)
        {
            this.transparency = Math.max(0, Math.min(1, transparency));
            vi = null;
        }
    }

    /**
     * Permanently resizes this AcceleratedImage to the given size.
     * That is, the contents of this BufferedImage are destroyed and set a resized version with the
     *  given width and height.
     * The transparency and transform are reset, as well as the volatile buffer.
     *
     * @param width - the new width for this AcceleratedImage, in pixels
     * @param height - the new height for this AcceleratedImage, in pixels
     */
    public void resize(int width, int height)
    {
        BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D) resized.createGraphics();
        g.setComposite(AlphaComposite.Src);
        g.drawImage(bi, 0, 0, width, height, null);
        bi = resized;
        transform.setToIdentity();
        transparency = 1;
        vi = null;
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

            if (transparency != 0)
            {
                AffineTransform prev = g.getTransform();
                g.transform(transform);
                g.drawImage(vi, (int)(x/transform.getScaleX()), (int)(y/transform.getScaleY()),
                        null);
                g.setTransform(prev);
            }
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
            if (vi == null)
            {
                vi = config.createCompatibleVolatileImage(bi.getWidth(), bi.getHeight(), quality);
                drawToVolatileImage(config);
            }
            else
            {
                int code = vi.validate(config);
                if (code == VolatileImage.IMAGE_RESTORED)
                {
                    drawToVolatileImage(config);
                }
                else if (code == VolatileImage.IMAGE_INCOMPATIBLE)
                {
                    vi = config.createCompatibleVolatileImage(bi.getWidth(), bi.getHeight(),
                            quality);
                    drawToVolatileImage(config);
                }
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

            if (transparency != 0)
            {
                Graphics2D viGraphics = vi.createGraphics();
                viGraphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC,
                        transparency));
                viGraphics.drawImage(bi, 0, 0, null);
                viGraphics.dispose();
            }
        } while (vi.contentsLost());
    }
}

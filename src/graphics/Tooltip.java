package graphics;

import io.Listener;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * Represents a general-purpose tooltip which displays some text when the cursor is hovered over
 *  a certain area of the screen.
 * A Tooltip can be customized with the {@link TooltipTheme} class.
 * Each Tooltip runs in its own Thread and automatically updates its state.
 * 
 * @author zirbinator
 */
public class Tooltip implements Runnable
{
    private AcceleratedImage image;
    
    private boolean hovering;
    
    private float alpha;
    
    private long hoverTime;
    private static final long period = 25;
    
    private Point location;
    
    private Rectangle hoverArea;
    
    private String text;
    
    private final Thread thread;
    private TooltipTheme theme;
    
    private Visibility visibility;
    
    /**
     * Creates a new Tooltip with the given text, hover area, and theme.
     * A new Thread is created and started for this Tooltip.
     * 
     * @param text - the text shown in the Tooltip
     * @param hoverArea - the area on the screen which triggers the Tooltip when the user hovers
     *  over it with the cursor
     * @param theme - the theme used to customize this Tooltip
     */
    public Tooltip(String text, Rectangle hoverArea, TooltipTheme theme)
    {
        this.text = text;
        this.hoverArea = hoverArea;
        this.theme = theme;
        if (theme == null)
        {
            this.theme = new TooltipTheme();
        }
        
        hovering = false;
        visibility = Visibility.INVISIBLE;
        hoverTime = -1;
        image = null;
        location = new Point();
        
        thread = new Thread(this);
        thread.start();
    }
    
    /**
     * Gets the text currently displayed by this Tooltip.
     * 
     * @return the text held in this Tooltip
     */
    public String getText()
    {
        return text;
    }
    
    /**
     * Sets the text displayed by this Tooltip.
     * The Tooltip's image will be recreated in the next call to {@link #draw(Graphics2D)} or
     *  {@link #create(Graphics2D)}.
     * 
     * @param text - the text to be held in this Tooltip
     */
    public void setText(String text)
    {
        this.text = text;
        image = null;
    }
    
    /**
     * Gets the area of the screen over which the user must hover to show this Tooltip.
     * 
     * @return a clone of the hover area
     */
    public Rectangle getHoverArea()
    {
        return (Rectangle) hoverArea.clone();
    }
    
    /**
     * Sets the area of the screen over which the user must hover to show this Tooltip.
     * 
     * @param hoverArea - the new hover area for this Tooltip
     */
    public void setHoverArea(Rectangle hoverArea)
    {
        this.hoverArea = hoverArea;
    }
    
    /**
     * Gets the top-left coordinate of the most recent tooltip box produced by this Tooltip.
     * 
     * @return a clone of the most recent location
     */
    public Point getLocation()
    {
        return (Point) location.clone();
    }
    
    /**
     * Sets the top-left coordinate of this Tooltip, effectively moving it on the screen if it is
     *  visible.
     * 
     * @param location - the new location for this Tooltip on the screen, with coordinates in px
     */
    public void setLocation(Point location)
    {
        this.location = location;
    }
    
    /**
     * Moves this Tooltip to the mouse's current location (keeping the theme's offset).
     */
    public void moveToMouse()
    {
        location = new Point(Listener.getMouse().x + theme.cursorOffset.x,
                Listener.getMouse().y + theme.cursorOffset.y);
    }
    
    /**
     * Sets the theme of this Tooltip used to configure it.
     * The Tooltip's image will be recreated in the next call to {@link #draw(Graphics2D)} or
     *  {@link #create(Graphics2D)}.
     * 
     * @param theme - the new theme for this Tooltip
     */
    public void setTheme(TooltipTheme theme)
    {
        this.theme = theme;
        image = null;
    }
    
    /**
     * Runs the Tooltip's fading animation.
     * An infinite loop keeps the state and transparency of the Tooltip up-to-date.
     * 
     * @see Runnable#run()
     */
    public void run()
    {
        long lastUpdate = System.nanoTime();
        while (true)
        {
            if (hovering)
            {
                if (!hoverArea.contains(Listener.getMouse()))
                {
                    hovering = false;
                    hoverTime = System.nanoTime();
                    visibility = Visibility.FADING_OUT;
                }
                else if (visibility == Visibility.INVISIBLE && 
                        System.nanoTime() - hoverTime >= theme.delay*1000000)
                {
                    hoverTime = System.nanoTime();
                    visibility = Visibility.FADING_IN;
                    location = new Point(Listener.getMouse().x + theme.cursorOffset.x,
                            Listener.getMouse().y + theme.cursorOffset.y);
                }
            }
            else
            {
                if (hoverArea != null && hoverArea.contains(Listener.getMouse()))
                {
                    hovering = true;
                    hoverTime = System.nanoTime();
                }
            }
            
            if (visibility == Visibility.FADING_IN)
            {
                alpha += (System.nanoTime() - lastUpdate)/1000000f/theme.fadeInTime;
                if (alpha >= 1)
                {
                    alpha = 1;
                    visibility = Visibility.VISIBLE;
                }
            }
            else if (visibility == Visibility.FADING_OUT)
            {
                alpha -= (System.nanoTime() - lastUpdate)/1000000f/theme.fadeOutTime;
                if (alpha <= 0)
                {
                    alpha = 0;
                    visibility = Visibility.INVISIBLE;
                }
            }
            
            lastUpdate = System.nanoTime();
            try
            {
                Thread.sleep(period);
            }
            catch (InterruptedException ex)
            {
                ex.printStackTrace();
            }
        }
    }
    
    /**
     * Creates the image backing this Tooltip.
     * This method can be called to immediately refresh the contents of the Tooltip if the contents
     *  have been changed in a way that would impact the appearance of the Tooltip (i.e. changing
     *  the text or theme).
     * This method is called automatically by {@link #draw(Graphics2D)} if the image has been
     *  destroyed.
     * 
     * @param context - the graphics context under which the Tooltip's image should be created
     */
    public void create(Graphics2D context)
    {
        FontMetrics metrics = context.getFontMetrics(theme.font);
        Rectangle2D stringBounds = metrics.getStringBounds(text, context);
        
        BufferedImage bi = new BufferedImage(
                (int) Math.ceil(stringBounds.getWidth() + 2*theme.textBuffer),
                (int) Math.ceil(stringBounds.getHeight() + 2*theme.textBuffer),
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D) bi.getGraphics();
        
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(theme.background);
        g.fillRoundRect(0, 0, bi.getWidth(), bi.getHeight(), theme.arc, theme.arc);
        g.setColor(theme.border);
        g.drawRoundRect(0, 0, bi.getWidth() - 1, bi.getHeight() - 1, theme.arc, theme.arc);
        
        g.setFont(theme.font);
        g.setColor(theme.shadow);
        g.drawString(text,theme.textBuffer + 1.25f,
                (float) (theme.textBuffer/2f + stringBounds.getHeight() + 1.25));
        g.setColor(theme.text);
        g.drawString(text, theme.textBuffer,
                (float) (theme.textBuffer/2f + stringBounds.getHeight()));
        
        image = new AcceleratedImage(bi, AcceleratedImage.TRANSLUCENT);
    }
    
    /**
     * Draws the Tooltip with the given Graphics2D painter.
     * If the image has not yet been created (and it is not currently invisible), it is created
     *  with {@link #create(Graphics2D)} and the given graphics context, which may take a
     *  significant amount of time.
     * 
     * @param g - the current graphics context
     */
    public void draw(Graphics2D g)
    {
        if (visibility != Visibility.INVISIBLE)
        {
            if (image == null)
            {
                create(g);
            }
            
            image.setTransparency(alpha);
            image.draw(location.x, location.y, g);
        }
    }
    
    /**
     * Contains the four possible states of visibility that a {@link Tooltip} can have.
     * 
     * @author zirbinator
     */
    private enum Visibility
    {
        /**
         * The {@link Tooltip} is completely invisible, the image is not drawn at all.
         */
        INVISIBLE,
        /**
         * The {@link Tooltip} is going from invisible to visible, fading towards visibility.
         * The {@link Tooltip} is drawn with a transparency between 0.0 and 1.0.
         */
        FADING_IN,
        /**
         * The {@link Tooltip} is completely visible and drawn with a transparency of 1.0.
         */
        VISIBLE,
        /**
         * The {@link Tooltip} is going from visible to invisible, fading towards invisibility.
         * The {@link Tooltip} is drawn with a transparency between 0.0 and 1.0.
         */
        FADING_OUT;
    }
    
    /**
     * Represents a theme by which a {@link Tooltip} can be customized.
     * This class acts as a data structure and contains fields that change the way in which a
     *  {@link Tooltip} is rendered and when and where it is drawn.
     * 
     * @author zirbinator
     */
    public static class TooltipTheme
    {
        /**
         * The color of the text of the {@link Tooltip}.
         * The default text color is light grey.
         */
        public Color text;
        /**
         * The color of the "shadow" of the text, drawn at a slight offset below and to the right
         *  of the text.
         * The default shadow color is black.
         */
        public Color shadow;
        /**
         * The color of the background of the {@link Tooltip}.
         * The default background color is a dark grey with high transparency.
         */
        public Color background;
        /**
         * The color of the border of the {@link Tooltip}.
         * The default border color is black.
         */
        public Color border;
        
        /**
         * The buffer between the edge of the {@link Tooltip} and the text.
         * That is, this distance (in px) is (roughly) maintained between each edge of the
         *  {@link Tooltip} and the text.
         * The default text buffer is 7.5.
         */
        public float textBuffer;
        /**
         * The font with which the text of the {@link Tooltip} is drawn.
         * The default font is plain Sans-Serif, 14-point.
         */
        public Font font;
        
        /**
         * The arc size of the rounded rectangle that serves as the border and background of the
         *  {@link Tooltip} (used for both arc width and height).
         * That is, this distance (in px) is the diameter of the circle drawn at each of the four
         *  corners of the {@link Tooltip}.
         * The default arc size is 5.
         * 
         * @see Graphics2D#drawRoundRect(int, int, int, int, int, int)
         */
        public int arc;
        
        /**
         * The delay between when the user first moves the cursor over the {@link Tooltip}'s
         *  "hover area" and when the {@link Tooltip} begins to fade in, in ms.
         * The default delay is 800.
         */
        public long delay;
        /**
         * The time it takes for the {@link Tooltip} to fade in entirely, from invisible to visible
         *  in ms.
         * The default fade in time is 700.
         */
        public long fadeInTime;
        /**
         * The time it takes for the {@link Tooltip} to fade out entirely, from visible to
         *  invisible in ms.
         * The default fade out time is 250.
         */
        public long fadeOutTime;
        
        /**
         * The distance (both in the x and y direction) from the cursor that the top-left corner of
         *  the {@link Tooltip} is drawn.
         * The default cursor offset is (0, 20).
         */
        public Point cursorOffset;
        
        /**
         * Creates a new TooltipTheme with the default values for each field.
         */
        public TooltipTheme()
        {
            font = new Font(Font.SANS_SERIF, Font.PLAIN, 14);
            text = new Color(175, 175, 175);
            shadow = Color.black;
            background = new Color(20, 20, 20, 180);
            border = Color.black;
            textBuffer = 7.5f;
            arc = 5;
            delay = 800;
            fadeInTime = 700;
            fadeOutTime = 250;
            cursorOffset = new Point(0, 20);
        }
    }
}

package tests;

import graphics.AcceleratedImage;
import graphics.DisplayMonitor;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

/**
 * Tests the drawing speed of the {@link AcceleratedImage} class as compared to the standard
 *  {@link BufferedImage} class in a typical, full-screen exclusive environment.
 */
public class ImageTest implements Runnable
{
    private ArrayList<AcceleratedImage> acceleratedImages;
    private ArrayList<BufferedImage> bufferedImages;
    private ArrayList<String> names;
    private ArrayList<Long> acceleratedTimes;
    private ArrayList<Long> bufferedTimes;

    /**
     * The number of times to draw each image with each method.
     */
    private static final int cycles = 50;

    private JFrame frame;

    /**
     * Runs a comparison of drawing speeds for different types of images.
     *
     * @param args - command-line arguments, ignored
     */
    public static void main(String[] args)
    {
        new ImageTest().test();
    }

    /**
     * Runs a test of the image drawing speeds with different drawing methods by loading the images
     *  to be tested, creating a full-screen exclusive window, and running a draw loop.
     */
    public void test()
    {
        acceleratedImages = new ArrayList<AcceleratedImage>();
        bufferedImages = new ArrayList<BufferedImage>();
        names = new ArrayList<String>();
        acceleratedTimes = new ArrayList<Long>();
        bufferedTimes = new ArrayList<Long>();

        try
        {
            loadImages();
        }
        catch (IOException ex)
        {
            ex.printStackTrace();
        }

        frame = DisplayMonitor.createFrame("Image Test", null);

        new Thread(this).start();
    }

    /**
     * Loads all images available for testing in the "images" folder.
     *
     * @throws IOException - thrown if there is any error loading images
     */
    private void loadImages() throws IOException
    {
        File folder = new File("images");

        if (!folder.exists() || !folder.isDirectory())
        {
            System.out.println("No images folder found, aborting test.");
            System.exit(0);
        }
        else
        {
            File[] images = folder.listFiles();
            for (int i = 0; i < images.length; i++)
            {
                if (images[i].exists() && !images[i].isDirectory())
                {
                    names.add(images[i].getName());
                    bufferedImages.add(ImageIO.read(images[i]));
                    acceleratedImages.add(new AcceleratedImage(images[i].getPath()));
                }
            }
        }
    }

    /**
     * Runs a draw loop that continuously draws images and displays them on the screen, similar to
     *  the method used by most games and graphics-intensive applications.
     *
     * @see Runnable#run()
     */
    public void run()
    {
        int i = 0, j = 0;
        boolean accelerated = true;
        while (true)
        {
            BufferStrategy strategy = frame.getBufferStrategy();
            Graphics2D g = (Graphics2D)strategy.getDrawGraphics();

            g.setColor(Color.red);
            g.fillRect(0, 0, DisplayMonitor.screen.width, DisplayMonitor.screen.height);
            if (accelerated)
            {
                if (j == 0)
                {
                    acceleratedImages.get(i).validate(g);
                }

                long before = System.nanoTime();
                acceleratedImages.get(i).draw(0, 0, g);
                acceleratedTimes.add(System.nanoTime() - before);
            }
            else
            {
                long before = System.nanoTime();
                g.drawImage(bufferedImages.get(i), 0, 0, null);
                bufferedTimes.add(System.nanoTime() - before);
            }

            g.dispose();

            j++;
            if (j >= cycles)
            {
                if (accelerated)
                {
                    j = 0;
                    accelerated = false;
                }
                else
                {
                    accelerated = true;
                    j = 0;
                    i++;

                    if (i >= acceleratedImages.size())
                    {
                        exit();
                    }
                }
            }

            strategy.show();
            Toolkit.getDefaultToolkit().sync();
        }
    }

    /**
     * Exits the image test by printing the results to the standard out.
     */
    public void exit()
    {
        for (int i = 0; i < acceleratedImages.size(); i++)
        {
            System.out.println(names.get(i) + ":");
            double aSum = 0;
            double bSum = 0;
            for (int j = 0; j < cycles; j++)
            {
                aSum += acceleratedTimes.get(i*cycles + j);
                bSum += bufferedTimes.get(i*cycles + j);
            }
            System.out.println("   a: " + aSum/cycles +
                    " (" + acceleratedImages.get(i).getQuality() + ")");
            System.out.println("   b: " + bSum/cycles);
            System.out.println("   diff: " + (bSum/cycles - aSum/cycles));
            System.out.println();
        }

        System.out.println("\nDetails:\n");

        System.out.println("Accelerated:");
        for (int i = 0; i < acceleratedTimes.size(); i++)
        {
            if (i % cycles == 0)
            {
                System.out.println(names.get(i/cycles) + ":");
            }
            System.out.println(" " + acceleratedTimes.get(i));
        }
        System.out.println("\nBuffered:");
        for (int i = 0; i < bufferedTimes.size(); i++)
        {
            if (i % cycles == 0)
            {
                System.out.println(names.get(i/cycles) + ":");
            }
            System.out.println(bufferedTimes.get(i));
        }

        System.exit(0);
    }
}

package utils;

/**
 * Represents a single point with double precision in the Cartesian plane.
 * 
 * @author Dominic
 */
public class Point2D
{
	public double x;
	public double y;
	
	/**
	 * Creates a new Point2D with the given coordinates.
	 * 
	 * @param x - the x-coordinate
	 * @param y - the y-coordinate
	 */
	public Point2D(double x, double y)
	{
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Creates a new Point2D with the given coordinates.
	 * 
	 * @param x - the x-coordinate
	 * @param y - the y-coordinate
	 */
	public Point2D(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Creates a new Point2D at (0,0).
	 */
	public Point2D()
	{
		x = 0;
		y = 0;
	}
	
	/**
	 * Returns the x-coordinate.
	 * 
	 * @return x - the x-coordinate
	 */
	public double getX()
	{
		return x;
	}
	
	/**
	 * Returns the y-coordinate.
	 * 
	 * @return y - the y-coordinate
	 */
	public double getY()
	{
		return y;
	}
}

package utils;

import java.awt.Point;

/**
 * Represents a rectangle in two dimensions, but in double precision.
 * 
 * @author Dominic
 */
public class Rectangle4D
{
	public double x, y, width, height;
	
	/**
	 * Creates a new Rectangle4D with the given top-left coordinates, width, and height.
	 * 
	 * @param x - the left side, the x-coordinate of the top-left corner
	 * @param y - the top side, the y-coordinate of the top-left corner
	 * @param width - the distance from the left side to the right side, the width of the rectangle
	 * @param height - the distance from the bottom to the top, the height of the rectangle
	 */
	public Rectangle4D(double x, double y, double width, double height)
	{
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	/**
	 * Creates a new Rectangle4D with the given top-left coordinates and a width and height of 0.
	 * 
	 * @param x - the left side, the x-coordinate of the top-left corner
	 * @param y - the top side, the y-coordinate of the top-left corner
	 */
	public Rectangle4D(double x, double y)
	{
		this(x, y, 0, 0);
	}
	
	/**
	 * Creates a new Rectangle4D with the x-coordinate, y-coordinate, width, and height of 0.
	 */
	public Rectangle4D()
	{
		this(0, 0, 0, 0);
	}
	
	/**
	 * Determines whether or not the given point is contained within this rectangle.
	 * Note: if the point is on the boundary of this rectangle, it is counted as being contained.
	 * 
	 * @param x - the x-coordinate of the point
	 * @param y - the y-coordinate of the point
	 * @return contains - true if the point is contained, false otherwise
	 */
	public boolean contains(double x, double y)
	{
		if (this.x >= x && this.x + width <= x && this.y >= y && this.y + height <= y)
		{
			return true;
		}
		return false;
	}
	
	/**
	 * Determines whether or not the given point is contained within this rectangle.
	 * 
	 * @param p - the point
	 * @return contains - true if the point is contained, false otherwise
	 */
	public boolean contains(Point p)
	{
		return contains(p.x, p.y);
	}
	
	/**
	 * Returns the x-coordinate of the top-left corner of this rectangle, the location of the left side.
	 * 
	 * @return x - the x-coordinate
	 */
	public double getX()
	{
		return x;
	}
	
	/**
	 * Returns the y-coordinate of the top-left corner of this rectangle, the location of the top side.
	 * 
	 * @return y - the y-coordinate
	 */
	public double getY()
	{
		return y;
	}
	
	/**
	 * Returns the width of this rectangle, the distance between the right and left sides.
	 * 
	 * @return width - the width
	 */
	public double getWidth()
	{
		return width;
	}
	
	/**
	 * Returns the height of this rectangle, the distance between the top and bottom.
	 * 
	 * @return height - the height
	 */
	public double getHeight()
	{
		return height;
	}
}

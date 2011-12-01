package io;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * This class listens for input events from the keyboard and mouse by implementing various interfaces from java.awt.event.
 * Other classes can receive a notification in the form of a method call by using the overloaded requestNotification() methods.
 * These methods will add a new NotificationRequest to the ArrayList, which is checked whenever a key is pressed or released.
 * A list of the starting indexes of the various notifications types is kept in order that notification can be quicker upon an event.
 * If the correct key was pressed/released, the specified method will be called.
 * 
 * @author Dominic
 */
public class Listener implements KeyListener, MouseMotionListener, MouseListener, MouseWheelListener
{
	private ArrayList<NotificationRequest> notifications;
	
	public static final int TYPE_KEY_PRESSED = KeyEvent.KEY_PRESSED;
	public static final int TYPE_KEY_RELEASED = KeyEvent.KEY_RELEASED;
	public static final int TYPE_MOUSE_MOVED = MouseEvent.MOUSE_MOVED;
	public static final int TYPE_MOUSE_PRESSED = MouseEvent.MOUSE_PRESSED;
	public static final int TYPE_MOUSE_RELEASED = MouseEvent.MOUSE_RELEASED;
	public static final int TYPE_MOUSE_WHEEL = MouseEvent.MOUSE_WHEEL;
	
	public static final int CODE_BUTTON1 = MouseEvent.BUTTON1;
	public static final int CODE_BUTTON2 = MouseEvent.BUTTON2;
	public static final int CODE_BUTTON3 = MouseEvent.BUTTON3;
	public static final int CODE_BUTTON_ALL = MouseEvent.NOBUTTON;
	public static final int CODE_KEY_ALL = -2;
	public static final int CODE_SCROLL_BOTH = 0;
	public static final int CODE_SCROLL_DOWN = 1;
	public static final int CODE_SCROLL_UP = -1;
	
	private int key_pressed_start;
	private int key_released_start;
	private int mouse_moved_start;
	private int mouse_pressed_start;
	private int mouse_released_start;
	private int mouse_wheel_start;
	
	private Point mouseLocation;
	
	/**
	 * Creates a new Listener object with the given Information.
	 * 
	 * @param info - the universal Information
	 */
	public Listener()
	{
		mouseLocation = new Point();
		notifications = new ArrayList<NotificationRequest>();
		key_pressed_start = 0;
		key_released_start = 0;
		mouse_pressed_start = 0;
		mouse_released_start = 0;
		mouse_wheel_start = 0;
		mouse_moved_start = 0;
	}
	
	/**
	 * Requests that a notification be sent via method call when a certain key is pressed or released.
	 * The starting indexes for the various notification types in the notification list are shifted appropriately.
	 * 
	 * @param object - the object whose method is invoked as a notification
	 * @param method - the method called upon notification
	 * @param type - the type of of event that triggers this notification (use Listener for the corresponding ints)
	 * @param code - the key, mouse button, or mouse wheel scroll direction (positive is down/negative is up) that triggers the notification
	 * @param sendCode - whether the method call should use the code as the first parameter
	 * @param sendType - whether the method call should use the type as the second parameter (first if the code is not send)
	 */
	public void requestNotification(Object object, Method method, int type, int code)
	{
		notifications.add(getStart(type), new NotificationRequest(object, method, type, code));
		shiftStarts(type);
	}
	
	/**
	 * Requests that a notification be sent via method call when a certain key is pressed or released.
	 * The starting indexes for the various notification types in the notification list are shifted appropriately.
	 * 
	 * @param object - the object whose method is invoked as a notification
	 * @param methodName - the name of the method called upon notification
	 * @param type - the type of of event that triggers this notification (use Listener for the corresponding ints)
	 * @param code - the key, mouse button, or mouse wheel scroll direction (positive is down/negative is up) that triggers the notification
	 * @param sendCode - whether the method call should use the code as the first parameter
	 * @param sendType - whether the method call should use the type as the second parameter (first if the code is not send)
	 */
	public void requestNotification(Object object, String methodName, int type, int code)
	{
		notifications.add(getStart(type), new NotificationRequest(object, methodName, type, code));
		shiftStarts(type);
	}
	
	/**
	 * Invoked when a key has been pressed.
	 * Checks the registered notifications and sends a method call if a notification matches the event.
	 */
	public void keyPressed(KeyEvent event)
	{
		for (int i = key_pressed_start; i < key_released_start; i++)
		{
			notifications.get(i).call(event);
		}
		event.consume();
	}
	
	/**
	 * Invoked when a key has been released.
	 * Checks the registered notifications and sends a method call if a notification matches the event.
	 */
	public void keyReleased(KeyEvent event) 
	{
		for (int i = key_released_start; i < mouse_pressed_start; i++)
		{
			notifications.get(i).call(event);
		}
		event.consume();
	}
	
	/**
	 * Invoked when a key has been typed.
	 */
	public void keyTyped(KeyEvent event) 
	{
		event.consume();
	}
	
	/**
	 * Invoked when a mouse button has been pressed on a component.
	 * Checks the registered notifications and sends a method call if a notification matches the event.
	 */
	public void mousePressed(MouseEvent event) 
	{
		for (int i = mouse_pressed_start; i < mouse_released_start; i++)
		{
			notifications.get(i).call(event);
		}
		event.consume();
	}
	
	/**
	 * Invoked when a mouse button has been released on a component.
	 * Checks the registered notifications and sends a method call if a notification matches the event.
	 */
	public void mouseReleased(MouseEvent event) 
	{
		for (int i = mouse_released_start; i < mouse_wheel_start; i++)
		{
			notifications.get(i).call(event);
		}
		event.consume();
	}
	
	/**
	 * Invoked when the mouse button has been clicked (pressed and released) on a component.
	 */
	public void mouseClicked(MouseEvent event) 
	{
		event.consume();
	}
	
	/**
	 * Invoked when the mouse enters a component.
	 */
	public void mouseEntered(MouseEvent event) 
	{
		event.consume();
	}
	
	/**
	 * Invoked when the mouse exits a component.
	 */
	public void mouseExited(MouseEvent event) 
	{
		event.consume();
	}
	
	/**
	 * Invoked when the mouse cursor has been moved onto a component but no buttons have been pushed.
	 * Checks the registered notifications and sends a method call if a notification matches the event.
	 */
	public void mouseMoved(MouseEvent event)
	{
		for (int i = mouse_moved_start; i < notifications.size(); i++)
		{
			notifications.get(i).call(event);
		}
		event.consume();
	}
	
	/**
	 * Invoked when a mouse button is pressed on a component and then dragged.
	 * Calls mouseMoved with the same MouseEvent given, which then checks for mouse movement notifications requests.
	 */
	public void mouseDragged(MouseEvent event) 
	{
		mouseMoved(event);
	}
	
	/**
	 * Invoked when the mouse wheel is rotated.
	 * Checks the registered notifications and sends a method call if a notification matches the event.
	 */
	public void mouseWheelMoved(MouseWheelEvent event) 
	{
		for (int i = mouse_wheel_start; i < mouse_moved_start; i++)
		{
			notifications.get(i).call(event);
		}
		event.consume();
	}
	
	/**
	 * Returns the current position of the mouse on the screen, in pixels.
	 * 
	 * @return mouseLocation - the location of the mouse pointer
	 */
	public Point getMouseLocation()
	{
		return mouseLocation;
	}
	
	/**
	 * Determines whether the given mouse wheel rotation is up (away from the user).
	 * The rotation can be found with a MouseWheelEvent's getWheelRotation() method or a NotificationRequest's code (if the type is MOUSE_WHEEL).
	 * Note: if there was no rotation, false is returned, so use this method only to determine is the rotation is up - use isDown to determine if it is down
	 * 
	 * @param rotation - the amount rotated
	 * @return up - true if the rotation was up (away from the user), false otherwise (note: if there was no rotation, false is returned)
	 */
	public static boolean isUp(int rotation)
	{
		if (rotation < 0)
		{
			return true;
		}
		return false;
	}
	
	/**
	 * Determines whether the given mouse wheel rotation is down (toward the user).
	 * The rotation can be found with a MouseWheelEvent's getWheelRotation() method or a NotificationRequest's code (if the type is MOUSE_WHEEL).
	 * Note: if there was no rotation, false is returned, so use this method only to determine is the rotation is down - use isUp to determine if it is up
	 * 
	 * @param rotation - the amount rotated
	 * @return down - true if the rotation was down (toward the user), false otherwise (note: if there was no rotation, false is returned)
	 */
	public static boolean isDown(int rotation)
	{
		if (rotation > 0)
		{
			return true;
		}
		return false;
	}
	
	/**
	 * Shifts the indexes at which various types of notification requests begin.
	 * The indexes are changed so that a notification request with the given type can be added in its correct position.
	 * 
	 * @param type - the type of notification request to be adjusted for
	 */
	private void shiftStarts(int type)
	{
		if (type == TYPE_KEY_PRESSED)
		{
			key_released_start++;
			mouse_pressed_start++;
			mouse_released_start++;
			mouse_wheel_start++;
			mouse_moved_start++;
		}
		else if (type == TYPE_KEY_RELEASED)
		{
			mouse_pressed_start++;
			mouse_released_start++;
			mouse_wheel_start++;
			mouse_moved_start++;
		}
		else if (type == TYPE_MOUSE_PRESSED)
		{
			mouse_released_start++;
			mouse_wheel_start++;
			mouse_moved_start++;
		}
		else if (type == TYPE_MOUSE_RELEASED)
		{
			mouse_wheel_start++;
			mouse_moved_start++;
		}
		else if (type == TYPE_MOUSE_WHEEL)
		{
			mouse_moved_start++;
		}
	}
	
	/**
	 * Returns the starting index of the notification requests of the given type.
	 * A new notification request of the given type should be added to this location.
	 * Either before or after the notification request is added, a call to shiftStarts should be made.
	 * 
	 * @param type - the type of notification request to be added
	 * @return start - the starting index for the given type, -1 is the type is not recognized
	 */
	private int getStart(int type)
	{
		switch(type)
		{
		case TYPE_KEY_PRESSED:
			return key_pressed_start;
		case TYPE_KEY_RELEASED:
			return key_released_start;
		case TYPE_MOUSE_PRESSED:
			return mouse_pressed_start;
		case TYPE_MOUSE_RELEASED:
			return mouse_released_start;
		case TYPE_MOUSE_WHEEL:
			return mouse_wheel_start;
		case TYPE_MOUSE_MOVED:
			return mouse_moved_start;
		default:
			return -1;
		}
	}
}

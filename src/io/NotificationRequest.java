package io;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import utils.ExceptionHandler;

/**
 * This class is used by Listener to keep track of notification requests which have been made.
 * Each NotificationRequest has five components:<br>
 * the boolean working represents whether or not the notification was properly initialized;<br>
 * the Object object is the object whose method is invoked as a method invocation;<br>
 * the Method method is the method called;<br>
 * the int type is the type of event that triggers this notification (examples: key press, mouse moved, mouse wheel scrolled);<br>
 * the int code is the key (use KeyEvent's virtual keyboard), 
 *  mouse button (use MouseEvent's BUTTONS), or mouse wheel scroll direction (positive is down/negative is up) that triggers the notification.<br>
 * NotificationRequests use the Event given upon a call to convey all the possible information to the receiving class 
 *  such as which key, the location of the mouse press, the button pressed on the mouse, and so on.
 * 
 * @author Dominic
 */
public class NotificationRequest
{
	private boolean working;
	
	public static final int TYPE_KEY_PRESSED = Listener.TYPE_KEY_PRESSED;
	public static final int TYPE_KEY_RELEASED = Listener.TYPE_KEY_RELEASED;
	public static final int TYPE_MOUSE_DRAGGED = Listener.TYPE_MOUSE_DRAGGED;
	public static final int TYPE_MOUSE_MOVED = Listener.TYPE_MOUSE_MOVED;
	public static final int TYPE_MOUSE_PRESSED = Listener.TYPE_MOUSE_PRESSED;
	public static final int TYPE_MOUSE_RELEASED = Listener.TYPE_MOUSE_RELEASED;
	public static final int TYPE_MOUSE_WHEEL = Listener.TYPE_MOUSE_WHEEL;
	public static final int CODE_BUTTON1 = Listener.CODE_BUTTON1;
	public static final int CODE_BUTTON2 = Listener.CODE_BUTTON2;
	public static final int CODE_BUTTON3 = Listener.CODE_BUTTON3;
	public static final int CODE_BUTTON_ALL = Listener.CODE_BUTTON_ALL;
	public static final int CODE_KEY_ALL = Listener.CODE_KEY_ALL;
	public static final int CODE_SCROLL_BOTH = Listener.CODE_SCROLL_BOTH;
	public static final int CODE_SCROLL_DOWN = Listener.CODE_SCROLL_DOWN;
	public static final int CODE_SCROLL_UP = Listener.CODE_SCROLL_UP;
	
	private int type;
	private int code;
	
	private Method method;
	
	private Object object;
	
	/**
	 * Creates a new NotificationRequest with the given parameters.
	 * 
	 * @param object - the object whose method is invoked as a notification
	 * @param method - the method called upon notification
	 * @param type - the type of of event that triggers this notification (use Listener for the corresponding ints)
	 * @param code - the key, mouse button, or mouse wheel scroll direction (positive is down/negative is up) that triggers the notification
	 */
	public NotificationRequest(Object object, Method method, int type, int code)
	{
		this.object = object;
		this.method = method;
		this.type = type;
		this.code = code;
		working = true;
	}
	
	/**
	 * Creates a new NotificationRequest with the given parameters.
	 * 
	 * @param object - the object whose method is invoked as a notification
	 * @param methodName - the name of the method called upon notification
	 * @param type - the type of of event that triggers this notification (use Listener for the corresponding ints)
	 * @param code - the key, mouse button, or mouse wheel scroll direction (positive is down/negative is up) that triggers the notification
	 */
	public NotificationRequest(Object object, String methodName, int type, int code)
	{
		this.object = object;
		this.type = type;
		this.code = code;
		try
		{
			switch(type)
			{
			case TYPE_KEY_PRESSED:
			case TYPE_KEY_RELEASED:
				Class<?>[] keyParameters = {KeyEvent.class};
				method = object.getClass().getMethod(methodName, keyParameters);
				break;
			case TYPE_MOUSE_MOVED:
			case TYPE_MOUSE_PRESSED:
			case TYPE_MOUSE_RELEASED:
			case TYPE_MOUSE_DRAGGED:
				Class<?>[] mouseParameters = {MouseEvent.class};
				method = object.getClass().getMethod(methodName, mouseParameters);
				break;
			case TYPE_MOUSE_WHEEL:
				Class<?>[] mouseWheelParameters = {MouseWheelEvent.class};
				method = object.getClass().getMethod(methodName, mouseWheelParameters);
				break;
			default:
				method = null;
				System.out.println("A faulty method name was provided to a notification request with type " + type + " and code " + code + ".");
			}
			working = true;
		}
		catch (SecurityException ex)
		{
			working = false;
			ExceptionHandler.receive(ex, methodName, "Caused by an error in initializing a NotificationRequest.");
		}
		catch (NoSuchMethodException ex) 
		{ 
			working = false;
			ExceptionHandler.receive(ex, methodName, object.getClass().getName(), "Caused by an error in initializing a NotificationRequest.");
		}
	}
	
	/**
	 * Calls the method specified in the constructor with the given KeyEvent.
	 * If this NotificationRequest is not of a type that requires a KeyEvent 
	 *  (TYPE_KEY_PRESSED or TYPE_KEY_RELEASED) then no call will be made and false will be returned.
	 * 
	 * @param event - the KeyEvent that triggered the call
	 * @return called - true if a call was made, false otherwise
	 */
	public boolean call(KeyEvent event)
	{
		if ((type == TYPE_KEY_PRESSED || type == TYPE_KEY_RELEASED) && working)
		{
			if (code == event.getKeyCode() || code == CODE_KEY_ALL)
			{
				try
				{
					method.invoke(object, event);
				}
				catch (IllegalArgumentException ex)
				{
					ExceptionHandler.receive(ex, method.getName(), object.getClass().getName(), 
							"Thrown while trying to call this (discrete) method from a NotificationRequest.");
				} 
				catch (IllegalAccessException ex)
				{
					ExceptionHandler.receive(ex, method.getName(), object.getClass().getName(),
							"Thrown while trying to call this (discrete) method from a NotificationRequest.");
				} 
				catch (InvocationTargetException ex)
				{
					ExceptionHandler.receive(ex, method.getName(), object.getClass().getName(),
							"Thrown while trying to call this (discrete) method from a NotificationRequest.");
				}
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Calls the method specified in the constructor with the given MouseEvent.
	 * If this NotificationRequest is not of a type that requires a MouseEvent 
	 *  (TYPE_MOUSE_MOVED, TYPE_MOUSE_PRESSED, or TYPE_MOUSE_RELEASED) then no call will be made and false will be returned.
	 * 
	 * @param event - the MouseEvent that triggered the call
	 * @return called - true if a call was made, false otherwise
	 */
	public boolean call(MouseEvent event)
	{
		if ((type == TYPE_MOUSE_MOVED || type == TYPE_MOUSE_PRESSED || type == TYPE_MOUSE_RELEASED || type == TYPE_MOUSE_DRAGGED) && working)
		{
			if (code == event.getButton() || code == CODE_BUTTON_ALL || type == TYPE_MOUSE_MOVED || type == TYPE_MOUSE_DRAGGED)
			{
				try
				{
					method.invoke(object, event);
				}
				catch (IllegalArgumentException ex)
				{
					ExceptionHandler.receive(ex, method.getName(), object.getClass().getName(), 
							"Thrown while trying to call this (discrete) method from a NotificationRequest.");
				} 
				catch (IllegalAccessException ex)
				{
					ExceptionHandler.receive(ex, method.getName(), object.getClass().getName(),
							"Thrown while trying to call this (discrete) method from a NotificationRequest.");
				} 
				catch (InvocationTargetException ex)
				{
					ExceptionHandler.receive(ex, method.getName(), object.getClass().getName(),
							"Thrown while trying to call this (discrete) method from a NotificationRequest.");
				}
				return true;
			}
			if (type == TYPE_MOUSE_DRAGGED)
			{
				System.out.println("~ " + code + " " + event.getButton());
			}
		}
		return false;
	}
	
	/**
	 * Calls the method specified in the constructor with the given MouseWheelEvent.
	 * If this NotificationRequest is not of a type that requires a MouseWheelEvent 
	 *  (TYPE_MOUSE_WHEEL) then no call will be made and false will be returned.
	 * 
	 * @param event - the MouseWheelEvent that triggered the call
	 * @return called - true if a call was made, false otherwise
	 */
	public boolean call(MouseWheelEvent event)
	{
		if (type == TYPE_MOUSE_WHEEL && working)
		{
			if (Listener.isUp(code) == Listener.isUp(event.getScrollAmount()) || code == CODE_SCROLL_BOTH)
			{
				try
				{
					method.invoke(object, event);
				}
				catch (IllegalArgumentException ex)
				{
					ExceptionHandler.receive(ex, method.getName(), object.getClass().getName(), 
							"Thrown while trying to call this (discrete) method from a NotificationRequest.");
				} 
				catch (IllegalAccessException ex)
				{
					ExceptionHandler.receive(ex, method.getName(), object.getClass().getName(),
							"Thrown while trying to call this (discrete) method from a NotificationRequest.");
				} 
				catch (InvocationTargetException ex)
				{
					ExceptionHandler.receive(ex, method.getName(), object.getClass().getName(),
							"Thrown while trying to call this (discrete) method from a NotificationRequest.");
				}
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Returns the int type that determines what event triggers the notification call.
	 * 
	 * @return type - the type of event that triggers this notification (use Listener for the corresponding ints)
	 */
	public int getType()
	{
		return type;
	}
	
	/**
	 * Returns the int code that triggers the notification call.
	 * 
	 * @return code - the code: the key, mouse button, or mouse wheel scroll amount
	 */
	public int getCode()
	{
		return code;
	}
}

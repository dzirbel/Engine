package io;

import java.awt.Point;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import utils.ExceptionHandler;

/**
 * This class is used by Listener to keep track of notification requests which have been made.
 * There are two separate types of NotificationRequests.
 * <br><br>
 * The first is invoked when a key or mouse button is pressed or released, that is, it is a discrete notification.
 * The call method calls the method specified in the constructor's parameters.
 * This method call can have four different parameter sets:<br>
 * No parameters<br>
 * Integer keyCode<br>
 * Integer type<br>
 * Integer keyCode, Integer type<br>
 * Each NotificationRequest has seven components:<br>
 * the boolean working represents whether or not the notification was properly initialized;<br>
 * the Object object is the object whose method is invoked as a notification;<br>
 * the Method method is the method called;<br>
 * the int type is the type of event that triggers this notification (use Listener for the corresponding ints);<br>
 * the int code is the key (use KeyEvent's virtual keyboard), 
 *  mouse button (use MouseEvent's BUTTONS), or mouse wheel scroll direction (positive is down/negative is up) that triggers the notification;<br>
 * the boolean sendCode is whether the notification's method call should use the code as the first parameter;<br>
 * the boolean sendType is whether the notification's method call should use the type as the second parameter (first if the code is not sent).
 * <br><br>
 * The second type of NotificationRequest is called when the mouse is moved, thus, it is (to some extent) a continuous notification.
 * The sendType boolean is ignored for this second type of request and the sendCode is the only flag used.
 * If sendCode is true, the point of the movement is given as an argument to the method, if not, then no arguments are given.
 * 
 * @author Dominic
 */
public class NotificationRequest
{
	private boolean working;
	private boolean sendCode;
	private boolean sendType;
	
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
	 * @param code - the key, mouse button, or mouse wheel scroll direction (positive is down/negative is up) that triggers the notification\
	 * @param sendCode - whether the method call should use the code as the first parameter
	 * @param sendType - whether the method call should use the type as the second parameter (first if the code is not send)
	 */
	public NotificationRequest(Object object, Method method, int type, int code, boolean sendCode, boolean sendType)
	{
		this.object = object;
		this.method = method;
		this.type = type;
		this.code = code;
		this.sendCode = sendCode;
		this.sendType = sendType;
		working = true;
	}
	
	/**
	 * Creates a new NotificationRequest with the given parameters.
	 * 
	 * @param object - the object whose method is invoked as a notification
	 * @param methodName - the name of the method called upon notification
	 * @param type - the type of of event that triggers this notification (use Listener for the corresponding ints)
	 * @param code - the key, mouse button, or mouse wheel scroll direction (positive is down/negative is up) that triggers the notification\
	 * @param sendCode - whether the method call should use the code as the first parameter
	 * @param sendType - whether the method call should use the type as the second parameter (first if the code is not send)
	 */
	public NotificationRequest(Object object, String methodName, int type, int code, boolean sendCode, boolean sendType)
	{
		this.object = object;
		this.type = type;
		this.code = code;
		this.sendCode = sendCode;
		this.sendType = sendType;
		try
		{
			if (type == Listener.MOUSE_MOVED)
			{
				if (sendCode)
				{
					Class<?>[] parameters = {Point.class};
					method = object.getClass().getMethod(methodName, parameters);
				}
				else
				{
					method = object.getClass().getMethod(methodName);
				}
			}
			else
			{
				if (sendCode && sendType)
				{
					Class<?>[] parameters = {Integer.class, Integer.class};
					method = object.getClass().getMethod(methodName, parameters);
				}
				else if (sendCode || sendType)
				{
					Class<?>[] parameters = {Integer.class};
					method = object.getClass().getMethod(methodName, parameters);
				}
				else
				{
					method = object.getClass().getMethod(methodName);
				}
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
	 * Calls the method specified in the constructor with the parameters also given.
	 * If the request is discrete (press or release), the method call can have four sets of parameters: none; code; type; or code, type.
	 * These parameter sets are specified by two booleans in the constructor.
	 * The method is then called with the code given upon initialization.
	 * If the request is continuous (movement), the method is called with the Point at (0, 0).
	 * Thus, this method should typically not be used for continuous notification requests.
	 * 
	 * @return call - true if the call was made, false otherwise
	 */
	public boolean call()
	{
		if (type == Listener.MOUSE_MOVED)
		{
			return call(new Point(0, 0));
		}
		return call(code);
	}
	
	/**
	 * Meant for discrete (press or release) notifications, this method calls the method specified in the constructor
	 *  with the parameters also given, using the given code instead of the one given upon initialization.
	 * The method call can have four sets of parameters: none; code; type; or code, type.
	 * These parameter sets are specified by two booleans in the constructor.
	 * Note: if this NotificationRequest is of the continuous (mouse movement) type, false will be returned and no call will be made.
	 * 
	 * @param code - the code used to call the method, if sendCode is true
	 * @return call - true if the call was made, false otherwise
	 */
	public boolean call(int code)
	{
		if (working)
		{
			if (type == Listener.MOUSE_MOVED)
			{
				return false;
			}
			try
			{
				if (sendCode)
				{
					if (sendType)
					{
						method.invoke(object, code, type);
					}
					else
					{
						method.invoke(object, code);
					}
					
				}
				else
				{
					if (sendType)
					{
						method.invoke(object, type);
					}
					else
					{
						method.invoke(object);
					}
				}
				return true;
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
		}
		return false;
	}
	
	/**
	 * Meant for continuous (movement) notifications, this method calls the method specified in the constructor with the parameters given.
	 * If sendCode was set to true in the constructor, the given Point will be used in the method code, otherwise, no argument will be given.
	 * Note: if this NotificationRequest is of the discrete (press or release) type, false will be returned and no call will be made.
	 * 
	 * @param point - the point used in the method invocation, if sendCode is true
	 * @return called - true if the call was made, false otherwise
	 */
	public boolean call(Point point)
	{
		if (working)
		{
			if (type != Listener.MOUSE_MOVED)
			{
				return false;
			}
			try
			{
				if (sendCode)
				{
					method.invoke(object, point);
				}
				else
				{
					method.invoke(object);
				}
			}
			catch (IllegalArgumentException ex)
			{
				ExceptionHandler.receive(ex, method.getName(), object.getClass().getName(), 
						"Thrown while trying to call this (continuous) method from a NotificationRequest.");
			} 
			catch (IllegalAccessException ex)
			{
				ExceptionHandler.receive(ex, method.getName(), object.getClass().getName(),
						"Thrown while trying to call this (continuous) method from a NotificationRequest.");
			} 
			catch (InvocationTargetException ex)
			{
				ExceptionHandler.receive(ex, method.getName(), object.getClass().getName(),
						"Thrown while trying to call this (continuous) method from a NotificationRequest.");
			}
		}
		return false;
	}
	
	/**
	 * Calls the method specified in the constructor with the given arguments.
	 * 
	 * @param args - the parameters of the method call
	 * @return call - true if the call was made, false otherwise
	 */
	public boolean call(Object... args)
	{
		if (working)
		{
			try
			{
				method.invoke(object, args);
				return true;
			} 
			catch (IllegalArgumentException e) { } 
			catch (IllegalAccessException e) { } 
			catch (InvocationTargetException e) { }
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

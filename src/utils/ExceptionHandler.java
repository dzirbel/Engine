package utils;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * Handles all runtime exceptions thrown during execution.
 * When an error is caught the appropriate receive() method is called with the Exception and any applicable fields.
 * An error statement is printed, including the stack trace of the thrown exception.
 * If the exception was fatal, the program immediately exits, if it was not, then it is allowed to run.
 * 
 * @author Dominic
 */
public class ExceptionHandler
{
	public static final boolean FATAL = true;
	public static final boolean NONFATAL = false;
	
	/**
	 * Receives NullPointerExceptions, thrown when an application attempts to use null in a case where an object is required.
	 * 
	 * @param ex - the IOException thrown
	 * @param message - a special message to printed
	 */
	public static void receive(NullPointerException ex, String message)
	{
		System.out.println("[ERROR] Runtime error:");
		ex.printStackTrace();
		if (!(message == null || message.isEmpty()))
		{
			System.out.println("[ERROR] This message was provided with the exception:");
			System.out.println("[ERROR] " + message);
		}
		System.out.println("[ERROR] This exception is fatal and so the program will now exit.");
		System.exit(1);
	}
	
	/**
	 * Receives NullPointerExceptions, thrown when an application attempts to use null in a case where an object is required.
	 * 
	 * @param ex - the IOException thrown
	 */
	public static void receive(NullPointerException ex)
	{
		receive(ex, null);
	}
	
	/**
	 * Receives IOExceptions which are typically thrown when an unavailable resource is accessed or an I/O operation failed or was interrupted.
	 * 
	 * @param ex - the IOException thrown
	 * @param filename - the inaccessible location causing the IOException to be thrown
	 * @param message - a special message to printed
	 */
	public static void receive(IOException ex, String filename, String message)
	{
		System.out.println("[ERROR] Runtime error:");
		ex.printStackTrace();
		if (filename == null)
		{
			System.out.println("[ERROR] No filename was specified for this exception.");
		}
		else
		{
			System.out.println("[ERROR] The error occurred while trying to load the file at " + filename);
		}
		if (!(message == null || message.isEmpty()))
		{
			System.out.println("[ERROR] This message was provided with the exception:");
			System.out.println("[ERROR] " + message);
		}
		System.out.println("[ERROR] This exception is fatal and so the program will now exit.");
		System.exit(1);
	}
	
	/**
	 * Receives IOExceptions which are typically thrown when an unavailable resource is accessed or an I/O operation failed or was interrupted.
	 * 
	 * @param ex - the IOException thrown
	 * @param message - a special message to printed
	 */
	public static void receive(IOException ex, String message)
	{
		receive(ex, null, message);
	}
	
	/**
	 * Receives IOExceptions which are typically thrown when an unavailable resource is accessed or an I/O operation failed or was interrupted.
	 * 
	 * @param ex - the IOException thrown
	 */
	public static void receive(IOException ex)
	{
		receive(ex, null, null);
	}
	
	/**
	 * Receives SecurityExceptions which are typically thrown at a security violation.
	 * 
	 * @param ex - the SecurityException thrown
	 * @param blocked - the name of the item that likely caused the violation
	 * @param message - a special message to be printed
	 */
	public static void receive(SecurityException ex, String blocked, String message)
	{
		System.out.println("[ERROR] Runtime error:");
		ex.printStackTrace();
		if (blocked == null)
		{
			System.out.println("[ERROR] The blocked item is unknown.");
		}
		else
		{
			System.out.println("[ERROR] The blocked item was " + blocked);
		}
		if (!(message == null || message.isEmpty()))
		{
			System.out.println("[ERROR] This message was provided with the exception:");
			System.out.println("[ERROR] " + message);
		}
		System.out.println("[ERROR] This exception is not fatal and the so the program will continue running.");
	}
	
	/**
	 * Receives SecurityExceptions which are typically thrown at a security violation.
	 * 
	 * @param ex - the SecurityException thrown
	 * @param message - a special message to be printed
	 */
	public static void receive(SecurityException ex, String message)
	{
		receive(ex, null, message);
	}
	
	/**
	 * Receives SecurityExceptions which are typically thrown at a security violation.
	 * 
	 * @param ex - the SecurityException thrown
	 */
	public static void receive(SecurityException ex)
	{
		receive(ex, null, null);
	}
	
	/**
	 * Receives NoSuchMethodExceptions which are thrown when a particular method cannot be found.
	 * 
	 * @param ex - the NoSuchMethodException thrown
	 * @param methodName - the name of the method that cannot be found
	 * @param className - the name of the class holding the missing method
	 * @param message - a special message to be printed
	 */
	public static void receive(NoSuchMethodException ex, String methodName, String className, String message)
	{
		System.out.println("[ERROR] Runtime error:");
		ex.printStackTrace();
		if (methodName == null)
		{
			System.out.println("[ERROR] The non-existant method name is unknown.");
		}
		else
		{
			System.out.println("[ERROR] The non-existant method name is " + methodName);
		}
		if (className == null)
		{
			System.out.println("[ERROR] The class called is unknown.");
		}
		else
		{
			System.out.println("[ERROR] The class called is " + className);
		}
		if (!(message == null || message.isEmpty()))
		{
			System.out.println("[ERROR] This message was provided with the exception:");
			System.out.println("[ERROR] " + message);
		}
		System.out.println("[ERROR] This exception is not fatal and the so the program will continue running.");
	}
	
	/**
	 * Receives NoSuchMethodExceptions which are thrown when a particular method cannot be found.
	 * 
	 * @param ex - the NoSuchMethodException thrown
	 * @param methodName - the name of the method that cannot be found
	 * @param message - a special message to be printed
	 */
	public static void receive(NoSuchMethodException ex, String methodName, String message)
	{
		receive(ex, methodName, null, message);
	}
	
	/**
	 * Receives NoSuchMethodExceptions which are thrown when a particular method cannot be found.
	 * 
	 * @param ex - the NoSuchMethodException thrown
	 * @param message - a special message to be printed
	 */
	public static void receive(NoSuchMethodException ex, String message)
	{
		receive(ex, null, null, message);
	}
	
	/**
	 * Receives NoSuchMethodExceptions which are thrown when a particular method cannot be found.
	 * 
	 * @param ex - the NoSuchMethodException thrown
	 */
	public static void receive(NoSuchMethodException ex)
	{
		receive(ex, null, null, null);
	}
	
	/**
	 * Receives IllegalArgumentExceptions which are thrown when a method has been passed an illegal or inappropriate argument.
	 * 
	 * @param ex - the IllegalArgumentException thrown
	 * @param methodName - the name of the inappropriately called method
	 * @param className - the name of the class holding the inappropriately called method
	 * @param message - a special message to be printed
	 */
	public static void receive(IllegalArgumentException ex, String methodName, String className, String message)
	{
		System.out.println("[ERROR] Runtime error:");
		ex.printStackTrace();
		if (methodName == null)
		{
			System.out.println("[ERROR] The inappropriately called method name is unknown.");
		}
		else
		{
			System.out.println("[ERROR] The inappropriately called method name is " + methodName);
		}
		if (className == null)
		{
			System.out.println("[ERROR] The class called is unknown.");
		}
		else
		{
			System.out.println("[ERROR] The class called is " + className);
		}
		if (!(message == null || message.isEmpty()))
		{
			System.out.println("[ERROR] This message was provided with the exception:");
			System.out.println("[ERROR] " + message);
		}
		System.out.println("[ERROR] This exception is not fatal and the so the program will continue running.");
	}
	
	/**
	 * Receives IllegalArgumentExceptions which are thrown when a method has been passed an illegal or inappropriate argument.
	 * 
	 * @param ex - the IllegalArgumentException thrown
	 * @param methodName - the name of the inappropriately called method
	 * @param message - a special message to be printed
	 */
	public static void receive(IllegalArgumentException ex, String methodName, String message)
	{
		receive(ex, methodName, null, message);
	}
	
	/**
	 * Receives IllegalArgumentExceptions which are thrown when a method has been passed an illegal or inappropriate argument.
	 * 
	 * @param ex - the IllegalArgumentException thrown
	 * @param message - a special message to be printed
	 */
	public static void receive(IllegalArgumentException ex, String message)
	{
		receive(ex, null, null, message);
	}
	
	/**
	 * Receives IllegalArgumentExceptions which are thrown when a method has been passed an illegal or inappropriate argument.
	 * 
	 * @param ex - the IllegalArgumentException thrown
	 */
	public static void receive(IllegalArgumentException ex)
	{
		receive(ex, null, null, null);
	}
	
	/**
	 * Receives IllegalAccessExceptions which are thrown when a method is reflexively called to access a field to which it does not have access.
	 * 
	 * @param ex - the IllegalAccessException thrown
	 * @param methodName - the name of the method
	 * @param className - the name of the class holding the method
	 * @param message - a special message to be printed
	 */
	public static void receive(IllegalAccessException ex, String methodName, String className, String message)
	{
		System.out.println("[ERROR] Runtime error:");
		ex.printStackTrace();
		if (methodName == null)
		{
			System.out.println("[ERROR] The method name is unknown.");
		}
		else
		{
			System.out.println("[ERROR] The method name is " + methodName);
		}
		if (className == null)
		{
			System.out.println("[ERROR] The class called is unknown.");
		}
		else
		{
			System.out.println("[ERROR] The class called is " + className);
		}
		if (!(message == null || message.isEmpty()))
		{
			System.out.println("[ERROR] This message was provided with the exception:");
			System.out.println("[ERROR] " + message);
		}
		System.out.println("[ERROR] This exception is not fatal and the so the program will continue running.");
	}
	
	/**
	 * Receives IllegalAccessExceptions which are thrown when a method is reflexively called to access a field to which it does not have access.
	 * 
	 * @param ex - the IllegalAccessException thrown
	 * @param methodName - the name of the method
	 * @param message - a special message to be printed
	 */
	public static void receive(IllegalAccessException ex, String methodName, String message)
	{
		receive(ex, methodName, null, message);
	}
	
	/**
	 * Receives IllegalAccessExceptions which are thrown when a method is reflexively called to access a field to which it does not have access.
	 * 
	 * @param ex - the IllegalAccessException thrown
	 * @param message - a special message to be printed
	 */
	public static void receive(IllegalAccessException ex, String message)
	{
		receive(ex, null, null, message);
	}
	
	/**
	 * Receives IllegalAccessExceptions which are thrown when a method is reflexively called to access a field to which it does not have access.
	 * 
	 * @param ex - the IllegalAccessException thrown
	 */
	public static void receive(IllegalAccessException ex)
	{
		receive(ex, null, null, null);
	}
	
	/**
	 * Receives InvocationTargetExceptions, thrown as a wrapper to exceptions thrown by reflexively called methods.
	 * 
	 * @param ex - the InvocationTargetException thrown
	 * @param methodName - the name of the method
	 * @param className - the name of the class holding the method
	 * @param message - a special message to be printed
	 */
	public static void receive(InvocationTargetException ex, String methodName, String className, String message)
	{
		System.out.println("[ERROR] Runtime error:");
		ex.printStackTrace();
		if (methodName == null)
		{
			System.out.println("[ERROR] The method name is unknown.");
		}
		else
		{
			System.out.println("[ERROR] The method name is " + methodName);
		}
		if (className == null)
		{
			System.out.println("[ERROR] The class called is unknown.");
		}
		else
		{
			System.out.println("[ERROR] The class called is " + className);
		}
		if (!(message == null || message.isEmpty()))
		{
			System.out.println("[ERROR] This message was provided with the exception:");
			System.out.println("[ERROR] " + message);
		}
		System.out.println("[ERROR] This exception is not fatal and the so the program will continue running.");
	}
	
	/**
	 * Receives InvocationTargetExceptions, thrown as a wrapper to exceptions thrown by reflexively called methods.
	 * 
	 * @param ex - the InvocationTargetException thrown
	 * @param methodName - the name of the method
	 * @param message - a special message to be printed
	 */
	public static void receive(InvocationTargetException ex, String methodName, String message)
	{
		receive(ex, methodName, null, message);
	}
	
	/**
	 * Receives InvocationTargetExceptions, thrown as a wrapper to exceptions thrown by reflexively called methods.
	 * 
	 * @param ex - the InvocationTargetException thrown
	 * @param message - a special message to be printed
	 */
	public static void receive(InvocationTargetException ex, String message)
	{
		receive(ex, null, null, message);
	}
	
	/**
	 * Receives InvocationTargetExceptions, thrown as a wrapper to exceptions thrown by reflexively called methods.
	 * 
	 * @param ex - the InvocationTargetException thrown
	 */
	public static void receive(InvocationTargetException ex)
	{
		receive(ex, null, null, null);
	}
}

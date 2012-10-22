package io;

import java.awt.Event;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * This class is used by Listener to keep track of requests for notifications of mouse and keyboard
 *  events (i.e. buttons pressed and released).
 * Each NotificationRequest has four components:
 * 
 * <ol>
 * <li>the Object which has requested to be notified for a certain type of event through a method
 *  invocation</li>
 * <li>the Method to be called upon an appropriate event</li>
 * <li>the type of event that triggers the notification (i.e.: key press, mouse moved, mouse wheel
 *  scrolled)</li>
 * <li>the code that holds data regarding the event, such as the key pressed, mouse button pressed
 *  or released, or scroll direction</li>
 * </ol>
 * 
 * @author zirbinator
 */
public class NotificationRequest
{
    /**
     * The type of event for which this NotificationRequest should invoke the requested method.
     * Ex: button pressed, key pressed, mouse moved.
     * Use {@link Listener} for valid types; i.e. {@link Listener#TYPE_KEY_PRESSED}.
     */
    public final int type;
    /**
     * The code holding the specific data for which the requested method should be invoked.
     * That is, the code is the kind of data that an event of a matching type must be for a
     *  notification to be sent.
     * Ex: "esc" button pressed, mouse wheel scrolled up.
     * Use {@link Listener} for valid codes; i.e. {@link Listener#CODE_BUTTON1}.
     */
    public final int code;
    
    /**
     * The method to be called when an appropriate event occurs.
     */
    private final Method method;
    
    /**
     * The object that has requested to be notified.
     */
    private final Object object;
    
    /**
     * Creates a new NotificationRequest for the given Object's given Method based on the given
     *  type and code.
     * 
     * @param object - the Object whose Method is invoked as a notification (note: for static
     *  methods, any Object, including null, is valid)
     * @param method - the Method to be called as a notification
     * @param type - the type of event that triggers this notification (i.e. 
     *  {@link Listener#TYPE_KEY_PRESSED})
     * @param code - the kind of data that an event of the given type must be for a notification to
     *  be sent (i.e. {@link Listener#CODE_BUTTON1})
     * @throws IllegalArgumentException if the given method is null or the given type or code
     *  is invalid
     */
    public NotificationRequest(Object object, Method method, int type, int code) throws IllegalArgumentException
    {
        this.object = object;
        this.method = method;
        this.type = type;
        this.code = code;
        
        if (method == null)
        {
            throw new IllegalArgumentException("Null method");
        }
        if (!isTypeValid(type))
        {
            throw new IllegalArgumentException("Invalid type: " + type);
        }
        if (!isCodeValid(code, type))
        {
            throw new IllegalArgumentException("Invalid code: " + code);
        }
    }
    
    /**
     * Creates a new NotificationRequest for the given Object's method with the given name based on
     *  the given type and code.
     * First, a list of public methods, both static and non-static, of the given Object is found
     *  reflexively.
     * This list is first searched for a method with the given name and one parameter with the type
     *  matching the given type: either {@link KeyEvent} for {@link Listener#TYPE_KEY_PRESSED} and 
     *  {@link Listener#TYPE_KEY_RELEASED}, {@link MouseEvent} for
     *  {@link Listener#TYPE_MOUSE_DRAGGED}, {@link Listener#TYPE_MOUSE_MOVED},
     *  {@link Listener#TYPE_MOUSE_PRESSED}, and {@link Listener#TYPE_MOUSE_RELEASED}, or 
     *  {@link MouseWheelEvent} for {@link Listener#TYPE_MOUSE_WHEEL}.
     * If such a method is found, it will be used for notification calls; otherwise, the list is
     *  searched again for a method with the given name and no parameters, which is then used.
     * If no matching method is found either of these ways, an {@link IllegalArgumentException}
     *  is thrown.
     * 
     * @param object - the Object whose Method is invoked as a notification (note: for static
     *  methods, any Object, including null, is valid)
     * @param method - the name of the Method to be called as a notification
     * @param type - the type of event that triggers this notification (i.e. 
     * {@link Listener#TYPE_KEY_PRESSED})
     * @param code - the kind of data that an event of the given type must be for a notification to
     *  be sent (i.e. {@link Listener#CODE_BUTTON1})
     * @throws IllegalArgumentException if the given method is null or the given type or code
     *  is invalid
     * @throws NoSuchMethodException if the given method name is invalid
     */
    public NotificationRequest(Object object, String methodName, int type, int code) throws IllegalArgumentException,
            NoSuchMethodException
    {
        this(object, getMethod(object, methodName, type), type, code);
    }
    
    /**
     * Determines whether the given type is a valid type.
     * That is, whether the given type equals one of {@link Listener}'s type constants (i.e.
     *  {@link Listener#TYPE_KEY_PRESSED}.
     * 
     * @param type - the type to check
     * @return whether the type is valid
     */
    private static boolean isTypeValid(int type)
    {
        return type == Listener.TYPE_KEY_PRESSED || type == Listener.TYPE_KEY_RELEASED
                || type == Listener.TYPE_MOUSE_DRAGGED || type == Listener.TYPE_MOUSE_MOVED
                || type == Listener.TYPE_MOUSE_PRESSED || type == Listener.TYPE_MOUSE_RELEASED
                || type == Listener.TYPE_MOUSE_WHEEL;
    }
    
    /**
     * Determines whether the given code is valid for the given type.
     * That is, whether the code equals one of {@link Listener}'s code constants for the given type.
     * This method returns false if the given type is not valid.
     * 
     * @param code - the code to check
     * @param type - the type context under which to check the code
     * @return whether the code is valid for the given type
     */
    private static boolean isCodeValid(int code, int type)
    {
        if (type == Listener.TYPE_KEY_PRESSED || type == Listener.TYPE_KEY_RELEASED
                || type == Listener.TYPE_MOUSE_MOVED || type == Listener.TYPE_MOUSE_DRAGGED)
        {
            return true;
        }
        if (type == Listener.TYPE_MOUSE_PRESSED || type == Listener.TYPE_MOUSE_RELEASED)
        {
            return code == Listener.CODE_BUTTON1 || code == Listener.CODE_BUTTON2 || code == Listener.CODE_BUTTON3
                    || code == Listener.CODE_BUTTON_ALL;
        }
        if (type == Listener.TYPE_MOUSE_WHEEL)
        {
            return code == Listener.CODE_SCROLL_UP || code == Listener.CODE_SCROLL_DOWN
                    || code == Listener.CODE_SCROLL_BOTH;
        }
        return false;
    }
    
    /**
     * Gets a Method with the given name provided by the given Object.
     * The methods supported by the given Object, both static and non-static are first listed by
     *  reflection.
     * The list is then searched for a method with the given name and one parameter with the type
     *  of the {@link Event} associated with the given type.
     * That is, if the given type is {@link Listener#TYPE_KEY_PRESSED} or 
     *  {@link Listener#TYPE_KEY_RELEASED}, the list is searched for a method with the given name
     *  and one parameter of type {@link KeyEvent}; if the given type is 
     *  {@link Listener#TYPE_MOUSE_DRAGGED}, {@link Listener#TYPE_MOUSE_MOVED}, 
     *  {@link Listener#TYPE_MOUSE_PRESSED}, or {@link Listener#TYPE_MOUSE_RELEASED}, the list is
     *  searched for a method with the given name and one parameter or type {@link MouseEvent}; if 
     *  the given type is {@link Listener#TYPE_MOUSE_WHEEL}, the list is searched for a method with
     *  the given name name and one parameter of type {@link MouseWheelEvent}.
     * (If any type other than the types listed above is given an {@link IllegalArgumentException}
     *  is thrown.)
     * If a method is found based on the above process, it is returned.
     * If no method is found by the above process, the list of methods is searched again for a
     *  method with the given name and no parameters, and if one such method is found, it is
     *  returned.
     * Finally, the list is searched again for a method with the given name, regardless of
     *  parameters and if one is found it is returned.
     * If no methods are found by either process, a {@link NoSuchMethodException} is thrown.
     * 
     * @param object - the Object whose methods should be searched
     * @param name - the name of the method for which to search
     * @param type - the type of notification for which to search
     * @return the method that should be used to notify the given Object of an event of the given
     *  type
     * @throws NoSuchMethodException if no suitable method is found
     * @throws IllegalArgumentException if the given type is invalid
     */
    private static Method getMethod(Object object, String name, int type) throws NoSuchMethodException,
            IllegalArgumentException
    {
        Class<?> eventClass;
        switch (type)
        {
        case Listener.TYPE_KEY_PRESSED:
        case Listener.TYPE_KEY_RELEASED:
            eventClass = KeyEvent.class;
            break;
        case Listener.TYPE_MOUSE_DRAGGED:
        case Listener.TYPE_MOUSE_MOVED:
        case Listener.TYPE_MOUSE_PRESSED:
        case Listener.TYPE_MOUSE_RELEASED:
            eventClass = MouseEvent.class;
            break;
        case Listener.TYPE_MOUSE_WHEEL:
            eventClass = MouseWheelEvent.class;
            break;
        default:
            throw new IllegalArgumentException("Illegal type: " + type);
        }
        
        Method[] methods = object.getClass().getMethods();
        for (int i = 0; i < methods.length; i++)
        {
            if (methods[i].getName().equals(name))
            {
                Class<?>[] params = methods[i].getParameterTypes();
                if (params.length == 1 && params[0].equals(eventClass))
                {
                    return methods[i];
                }
            }
        }
        
        for (int i = 0; i < methods.length; i++)
        {
            if (methods[i].getName().equals(name))
            {
                if (methods[i].getParameterTypes().length == 0)
                {
                    return methods[i];
                }
            }
        }
        
        for (int i = 0; i < methods.length; i++)
        {
            if (methods[i].getName().equals(name))
            {
                return methods[i];
            }
        }
        
        throw new NoSuchMethodException("Invalid method name: type " + object.getClass().getCanonicalName()
                + " does not support " + "a valid method named " + name);
    }
    
    /**
     * Determines the type of event for which this NotificationRequest should invoke the requested
     *  method.
     * Ex: button pressed, key pressed, mouse moved.
     * Use {@link Listener} for valid types; i.e. {@link Listener#TYPE_KEY_PRESSED}.
     * 
     * @return the type of event that triggers this notification
     */
    public int getType()
    {
        return type;
    }
    
    /**
     * Determines the specific data for which the requested method should be invoked.
     * Ex: "esc" button pressed, mouse wheel scrolled up.
     * Use {@link Listener} for valid codes; i.e. {@link Listener#CODE_BUTTON1}.
     * 
     * @return the kind of data that an event of a matching type must be for a
     *  notification to be sent
     */
    public int getCode()
    {
        return code;
    }
    
    /**
     * Handles the given {@link KeyEvent}, sending a notification if this NotificationRequest
     *  applies to the given event.
     * 
     * @param event - the {@link KeyEvent} causing this potential notification
     * @return whether a notification was sent based on the given event
     * @throws IllegalArgumentException thrown if the target method is incorrectly configured
     *  or accepts incorrect arguments
     * @throws IllegalAccessException thrown if the target method is incorrectly configured
     * @throws InvocationTargetException thrown if the target method is incorrectly configured
     */
    public boolean call(KeyEvent event) throws IllegalArgumentException, IllegalAccessException,
            InvocationTargetException
    {
        if (type == Listener.TYPE_KEY_PRESSED || type == Listener.TYPE_KEY_RELEASED)
        {
            if (code == event.getKeyCode() || code == Listener.CODE_KEY_ALL)
            {
                Class<?>[] paramTypes = method.getParameterTypes();
                if (paramTypes.length == 0)
                {
                    method.invoke(object);
                }
                else if (paramTypes.length == 1 && paramTypes[0].equals(KeyEvent.class))
                {
                    method.invoke(object, event);
                }
                else
                {
                    Object[] params = new Object[paramTypes.length];
                    for (int i = 0; i < params.length; i++)
                    {
                        params[i] = null;
                    }
                    method.invoke(object, params);
                }
                
                return true;
            }
        }
        return false;
    }
    
    /**
     * Handles the given {@link MouseEvent}, sending a notification if this NotificationRequest
     *  applies to the given event.
     * 
     * @param event - the {@link MouseEvent} causing this potential notification 
     * @return whether a notification was sent based on the given event
     * @throws IllegalArgumentException thrown if the target method is incorrectly configured
     *  or accepts incorrect arguments
     * @throws IllegalAccessException thrown if the target method is incorrectly configured
     * @throws InvocationTargetException thrown if the target method is incorrectly configured
     */
    public boolean call(MouseEvent event) throws IllegalArgumentException, IllegalAccessException,
            InvocationTargetException
    {
        if (type == Listener.TYPE_MOUSE_MOVED || type == Listener.TYPE_MOUSE_PRESSED
                || type == Listener.TYPE_MOUSE_RELEASED || type == Listener.TYPE_MOUSE_DRAGGED)
        {
            if (code == event.getButton() || code == Listener.CODE_BUTTON_ALL || type == Listener.TYPE_MOUSE_MOVED
                    || type == Listener.TYPE_MOUSE_DRAGGED)
            {
                Class<?>[] paramTypes = method.getParameterTypes();
                if (paramTypes.length == 0)
                {
                    method.invoke(object);
                }
                else if (paramTypes.length == 1 && paramTypes[0].equals(MouseEvent.class))
                {
                    method.invoke(object, event);
                }
                else
                {
                    Object[] params = new Object[paramTypes.length];
                    for (int i = 0; i < params.length; i++)
                    {
                        params[i] = null;
                    }
                    method.invoke(object, params);
                }
                
                return true;
            }
        }
        return false;
    }
    
    /**
     * Handles the given {@link MouseWheelEvent}, sending a notification if this
     *  NotificationRequest applies to the given event.
     * 
     * @param event - the {@link MouseWheelEvent} causing this potential notification
     * @return whether a notification was sent based on the given event
     * @throws IllegalArgumentException thrown if the target method is incorrectly configured
     *  or accepts incorrect arguments
     * @throws IllegalAccessException thrown if the target method is incorrectly configured
     * @throws InvocationTargetException thrown if the target method is incorrectly configured
     */
    public boolean call(MouseWheelEvent event) throws IllegalArgumentException, IllegalAccessException,
            InvocationTargetException
    {
        if (type == Listener.TYPE_MOUSE_WHEEL)
        {
            if (Listener.isUp(code) == Listener.isUp(event.getScrollAmount()) || code == Listener.CODE_SCROLL_BOTH)
            {
                Class<?>[] paramTypes = method.getParameterTypes();
                if (paramTypes.length == 0)
                {
                    method.invoke(object);
                }
                else if (paramTypes.length == 1 && paramTypes[0].equals(MouseWheelEvent.class))
                {
                    method.invoke(object, event);
                }
                else
                {
                    Object[] params = new Object[paramTypes.length];
                    for (int i = 0; i < params.length; i++)
                    {
                        params[i] = null;
                    }
                    method.invoke(object, params);
                }
                
                return true;
            }
        }
        return false;
    }
}

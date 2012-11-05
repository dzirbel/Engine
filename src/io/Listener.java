package io;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * Creates a simple interface for input events from the keyboard and mouse through the
 *  {@code java.awt.event} API.
 * Classes can register for notifications on specific events; these notifications are sent as a 
 *  method call to any method supported by the class.
 * 
 * @author zirbinator
 */
public class Listener implements KeyListener, MouseMotionListener, MouseListener,
        MouseWheelListener
{
    private static ArrayList<NotificationRequest> notifications = 
            new ArrayList<NotificationRequest>();
    
    private static boolean shiftHeld = false;
    private static boolean controlHeld = false;
    private static boolean altHeld = false;
    
    /**
     * Corresponds to a key press, triggered when the user presses a key on the keyboard.
     * Valid codes are {@link Listener#CODE_KEY_ALL} or any virtual keyboard code, accessible from
     *  the {@link KeyEvent} class, i.e. {@link KeyEvent#VK_Q} for the "Q" key.
     * 
     * @see KeyEvent#KEY_PRESSED
     */
    public static final int TYPE_KEY_PRESSED = KeyEvent.KEY_PRESSED;
    /**
     * Corresponds to a key release, triggered when the user released a key on the keyboard.
     * Valid codes are {@link Listener#CODE_KEY_ALL} or any virtual keyboard code, accessible from
     *  the {@link KeyEvent} class, i.e. {@link KeyEvent#VK_Q} for the "Q" key.
     * 
     * @see KeyEvent#KEY_RELEASED
     */
    public static final int TYPE_KEY_RELEASED = KeyEvent.KEY_RELEASED;
    /**
     * Corresponds to the mouse being dragged, triggered when a mouse button is held and the mouse
     *  is moved.
     * No code need be provided for this type.
     * 
     * @see MouseEvent#MOUSE_DRAGGED
     */
    public static final int TYPE_MOUSE_DRAGGED = MouseEvent.MOUSE_DRAGGED;
    /**
     * Corresponds to the mouse being moved, triggered when no mouse buttons are held and the mouse
     *  is moved.
     * No code need be provided for this type.
     * 
     * @see MouseEvent#MOUSE_MOVED
     */
    public static final int TYPE_MOUSE_MOVED = MouseEvent.MOUSE_MOVED;
    /**
     * Corresponds to a mouse button being pressed, triggered when the button is pressed.
     * Valid codes are {@link Listener#CODE_BUTTON1}, {@link Listener#CODE_BUTTON2},
     *  {@link Listener#CODE_BUTTON3}, and {@link Listener#CODE_BUTTON_ALL}.
     * 
     * @see MouseEvent#MOUSE_PRESSED
     */
    public static final int TYPE_MOUSE_PRESSED = MouseEvent.MOUSE_PRESSED;
    /**
     * Corresponds to a mouse button being released, triggered when the button is released.
     * Valid codes are {@link Listener#CODE_BUTTON1}, {@link Listener#CODE_BUTTON2},
     *  {@link Listener#CODE_BUTTON3}, and {@link Listener#CODE_BUTTON_ALL}.
     * 
     * @see MouseEvent#MOUSE_RELEASED
     */
    public static final int TYPE_MOUSE_RELEASED = MouseEvent.MOUSE_RELEASED;
    /**
     * Corresponds to the mouse wheel being rotated, triggered when the wheel is moved.
     * Valid codes are {@link Listener#CODE_SCROLL_UP}, {@link Listener#CODE_SCROLL_DOWN}, and
     *  {@link Listener#CODE_SCROLL_BOTH}.
     * 
     * @see MouseEvent#MOUSE_WHEEL
     */
    public static final int TYPE_MOUSE_WHEEL = MouseEvent.MOUSE_WHEEL;
    /**
     * Filters mouse presses and releases to only Button 1 (typically the left button).
     * This code is valid for types {@link Listener#TYPE_MOUSE_PRESSED} and 
     *  {@link Listener#TYPE_MOUSE_RELEASED}.
     * 
     * @see MouseEvent#BUTTON1
     */
    public static final int CODE_BUTTON1 = MouseEvent.BUTTON1;
    /**
     * Filters mouse presses and releases to only Button 2 (typically the right button).
     * This code is valid for types {@link Listener#TYPE_MOUSE_PRESSED} and 
     *  {@link Listener#TYPE_MOUSE_RELEASED}.
     * 
     * @see MouseEvent#BUTTON2
     */
    public static final int CODE_BUTTON2 = MouseEvent.BUTTON2;
    /**
     * Filters mouse presses and releases to only Button 3 (typically the wheel "button").
     * This code is valid for types {@link Listener#TYPE_MOUSE_PRESSED} and 
     *  {@link Listener#TYPE_MOUSE_RELEASED}.
     * 
     * @see MouseEvent#BUTTON3
     */
    public static final int CODE_BUTTON3 = MouseEvent.BUTTON3;
    /**
     * Filters the mouse presses and releases to all buttons.
     * This code is valid for types {@link Listener#TYPE_MOUSE_PRESSED} and 
     *  {@link Listener#TYPE_MOUSE_RELEASED}.
     * 
     * @see MouseEvent#NOBUTTON
     */
    public static final int CODE_BUTTON_ALL = MouseEvent.NOBUTTON;
    /**
     * Filters key presses and releases to all the keys.
     * This code is valid for types {@link Listener#TYPE_KEY_PRESSED} and
     *  {@link Listener#TYPE_KEY_RELEASED}.
     */
    public static final int CODE_KEY_ALL = -2;
    /**
     * Filters mouse wheel scrolls to both up and down scrolls.
     * This code is valid for the type {@link Listener#TYPE_MOUSE_WHEEL}.
     */
    public static final int CODE_SCROLL_BOTH = 0;
    /**
     * Filters mouse wheel scrolls to only down scrolls (toward the user).
     * This code is valid for the type {@link Listener#TYPE_MOUSE_WHEEL}.
     */
    public static final int CODE_SCROLL_DOWN = 1;
    /**
     * Filters mouse wheel scrolls to only down scrolls (away from the user).
     * This code is valid for the type {@link Listener#TYPE_MOUSE_WHEEL}.
     */
    public static final int CODE_SCROLL_UP = -1;
    private static int key_pressed_start = 0;
    private static int key_released_start = 0;
    private static int mouse_moved_start = 0;
    private static int mouse_dragged_start = 0;
    private static int mouse_pressed_start = 0;
    private static int mouse_released_start = 0;
    private static int mouse_wheel_start = 0;
    
    private static Listener listener = new Listener();
    
    private static Point mouseLocation = new Point();
    
    /**
     * Initializes the Listener for the given sources of mouse and keyboard events.
     * This is equivalent to adding a new Listener as a mouse listener, mouse motion listener,
     *  mouse wheel listener, and key listener for each of the given sources.
     * 
     * @param sources - the potential sources of input from the user
     */
    public static void init(Component... sources)
    {
        for (int i = 0; i < sources.length; i++)
        {
            sources[i].addMouseListener(listener);
            sources[i].addMouseMotionListener(listener);
            sources[i].addMouseWheelListener(listener);
            sources[i].addKeyListener(listener);
        }
    }
    
    /**
     * Requests that the given Object's given Method be invoked when an event of the given type
     *  with the given code specification occurs.
     * The method invoked can have as many or few parameters, but null will be passed as all of the
     *  arguments, unless the method has a single parameter with the type of the event related to
     *  the type.
     * For example, if a notification were requested for the type {@link #TYPE_KEY_PRESSED} and the
     *  method given had a single parameter, a {@link KeyEvent}, the {@link KeyEvent} which
     *  triggered the notification would be passed to the method.
     * 
     * @param object - the Object whose method should be called as a notification, may be null if
     *  the method is static
     * @param method - the Method to be invoked upon an associated event, should not be null
     * @param type - the type of event that triggers this notification (i.e. 
     *  {@link #TYPE_KEY_PRESSED})
     * @param code - the kind of data that an event of the given type must be for a notification to
     *  be sent (i.e. {@link Listener#CODE_BUTTON1})
     */
    public static void requestNotification(Object object, Method method, int type, int code)
    {
        notifications.add(getStart(type), new NotificationRequest(object, method, type, code));
        shiftStarts(type);
    }
    
    /**
     * Requests that the given Object's given Method be invoked when an event of the given type
     *  occurs.
     * There is as little restriction on the data of the event as possible, that is, every event
     *  of the given type will trigger a notification.
     * The method invoked can have as many or few parameters, but null will be passed as all of the
     *  arguments, unless the method has a single parameter with the type of the event related to
     *  the type.
     * For example, if a notification were requested for the type {@link #TYPE_KEY_PRESSED} and the
     *  method given had a single parameter, a {@link KeyEvent}, the {@link KeyEvent} which
     *  triggered the notification would be passed to the method.
     * 
     * @param object - the Object whose method should be called as a notification, may be null if
     *  the method is static
     * @param method - the Method to be invoked upon an associated event, should not be null
     * @param type - the type of event that triggers this notification (i.e. 
     *  {@link #TYPE_KEY_PRESSED})
     */
    public static void requestNotification(Object object, Method method, int type)
    {
        notifications.add(getStart(type),
                new NotificationRequest(object, method, type, getCode(type)));
        shiftStarts(type);
    }
    
    /**
     * Requests that the given Object's given Method be invoked when an event of the given type
     *  with the given code specification occurs.
     * The method invoked can have as many or few parameters, but null will be passed as all of the
     *  arguments, unless the method has a single parameter with the type of the event related to
     *  the type.
     * For example, if a notification were requested for the type {@link #TYPE_KEY_PRESSED} and the
     *  method given had a single parameter, a {@link KeyEvent}, the {@link KeyEvent} which
     *  triggered the notification would be passed to the method.
     * 
     * @param object - the Object whose method should be called as a notification, may be null if
     *  the method is static
     * @param methodName - the name of the method to be invoked upon an associated event (note:
     *  prioritized by parameters, if a method with the given name has a single event parameter as
     *  described above, it will be used, otherwise a method will no parameters that also matches
     *  the name will be used, otherwise notifications will not be sent)
     * @param type - the type of event that triggers this notification (i.e. 
     *  {@link #TYPE_KEY_PRESSED})
     * @param code - the kind of data that an event of the given type must be for a notification to
     *  be sent (i.e. {@link Listener#CODE_BUTTON1})
     */
    public static void requestNotification(Object object, String methodName, int type, int code)
    {
        try
        {
            notifications.add(getStart(type),
                    new NotificationRequest(object, methodName, type, code));
        }
        catch (NoSuchMethodException ex)
        {
            ex.printStackTrace();
        }
        shiftStarts(type);
    }
    
    /**
     * Requests that the given Object's given Method be invoked when an event of the given type
     *  with the given code specification occurs.
     * There is as little restriction on the data of the event as possible, that is, every event
     *  of the given type will trigger a notification.
     * The method invoked can have as many or few parameters, but null will be passed as all of the
     *  arguments, unless the method has a single parameter with the type of the event related to
     *  the type.
     * For example, if a notification were requested for the type {@link #TYPE_KEY_PRESSED} and the
     *  method given had a single parameter, a {@link KeyEvent}, the {@link KeyEvent} which
     *  triggered the notification would be passed to the method.
     * 
     * @param object - the Object whose method should be called as a notification, may be null if
     *  the method is static
     * @param methodName - the name of the method to be invoked upon an associated event (note:
     *  prioritized by parameters, if a method with the given name has a single event parameter as
     *  described above, it will be used, otherwise a method will no parameters that also matches
     *  the name will be used, otherwise notifications will not be sent)
     * @param type - the type of event that triggers this notification (i.e. 
     *  {@link #TYPE_KEY_PRESSED})
     * @param code - the kind of data that an event of the given type must be for a notification to
     *  be sent (i.e. {@link Listener#CODE_BUTTON1})
     */
    public static void requestNotification(Object object, String methodName, int type)
    {
        try
        {
            notifications.add(getStart(type),
                    new NotificationRequest(object, methodName, type, getCode(type)));
        }
        catch (NoSuchMethodException ex)
        {
            ex.printStackTrace();
        }
        shiftStarts(type);
    }
    
    /**
     * Gets the most general code that is able to be applied to the given type.
     * That is, if the given type is {@link #TYPE_KEY_PRESSED} or  {@link #TYPE_KEY_RELEASED}, 
     *  {@link #CODE_KEY_ALL} is returned so that events involving any key are caught; if the given
     *  type is {@link #TYPE_MOUSE_WHEEL}, {@link #CODE_SCROLL_BOTH} is returned so that both
     *  upward and downward scrolls are caught; and if the given type is
     *  {@link #TYPE_MOUSE_PRESSED} or  {@link #TYPE_MOUSE_RELEASED}, {@link #CODE_BUTTON_ALL} is
     *  returned so that events on any buttons are caught.
     * Otherwise, {@code -1} is returned.
     * 
     * @param type - the type for which a code should be gotten
     * @return the most inclusive code possible for the given type, or {@code -1}
     */
    private static int getCode(int type)
    {
        if (type == Listener.TYPE_KEY_PRESSED || type == Listener.TYPE_KEY_RELEASED)
        {
            return Listener.CODE_KEY_ALL;
        }
        else if (type == Listener.TYPE_MOUSE_WHEEL)
        {
            return Listener.CODE_SCROLL_BOTH;
        }
        else if (type == Listener.TYPE_MOUSE_PRESSED || type == Listener.TYPE_MOUSE_RELEASED)
        {
            return Listener.CODE_BUTTON_ALL;
        }
        
        return -1;
    }
    
    /**
     * Invoked when a key is pressed.
     * Sends out relevant notifications based on the event.
     */
    public void keyPressed(KeyEvent event)
    {
        if (event.getKeyCode() == KeyEvent.VK_CONTROL)
        {
            controlHeld = true;
        }
        else if (event.getKeyCode() == KeyEvent.VK_SHIFT)
        {
            shiftHeld = true;
        }
        else if (event.getKeyCode() == KeyEvent.VK_ALT)
        {
            altHeld = true;
        }
        
        for (int i = key_pressed_start; i < key_released_start; i++)
        {
            try
            {
                notifications.get(i).call(event);
            }
            catch (IllegalArgumentException ex)
            {
                ex.printStackTrace();
            }
            catch (IllegalAccessException ex)
            {
                ex.printStackTrace();
            }
            catch (InvocationTargetException ex)
            {
                ex.printStackTrace();
            }
        }
        event.consume();
    }
    
    /**
     * Invoked when a key is released.
     * Sends out relevant notifications based on the event.
     */
    public void keyReleased(KeyEvent event)
    {
        if (event.getKeyCode() == KeyEvent.VK_CONTROL)
        {
            controlHeld = false;
        }
        else if (event.getKeyCode() == KeyEvent.VK_SHIFT)
        {
            shiftHeld = false;
        }
        else if (event.getKeyCode() == KeyEvent.VK_ALT)
        {
            altHeld = false;
        }
        
        for (int i = key_released_start; i < mouse_pressed_start; i++)
        {
            try
            {
                notifications.get(i).call(event);
            }
            catch (IllegalArgumentException ex)
            {
                ex.printStackTrace();
            }
            catch (IllegalAccessException ex)
            {
                ex.printStackTrace();
            }
            catch (InvocationTargetException ex)
            {
                ex.printStackTrace();
            }
        }
        event.consume();
    }
    
    /**
     * Invoked when a key is typed.
     */
    public void keyTyped(KeyEvent event)
    {
        event.consume();
    }
    
    /**
     * Invoked when the mouse is pressed over a component.
     * Sends out relevant notifications based on the event.
     */
    public void mousePressed(MouseEvent event)
    {
        for (int i = mouse_pressed_start; i < mouse_released_start; i++)
        {
            try
            {
                notifications.get(i).call(event);
            }
            catch (IllegalArgumentException ex)
            {
                ex.printStackTrace();
            }
            catch (IllegalAccessException ex)
            {
                ex.printStackTrace();
            }
            catch (InvocationTargetException ex)
            {
                ex.printStackTrace();
            }
        }
        event.consume();
    }
    
    /**
     * Invoked when the mouse is released over a component.
     * Sends out relevant notifications based on the event.
     */
    public void mouseReleased(MouseEvent event)
    {
        for (int i = mouse_released_start; i < mouse_wheel_start; i++)
        {
            try
            {
                notifications.get(i).call(event);
            }
            catch (IllegalArgumentException ex)
            {
                ex.printStackTrace();
            }
            catch (IllegalAccessException ex)
            {
                ex.printStackTrace();
            }
            catch (InvocationTargetException ex)
            {
                ex.printStackTrace();
            }
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
     * Invoked when the mouse is moved over a component and no buttons are held.
     * Sends out relevant notifications based on the event.
     */
    public void mouseMoved(MouseEvent event)
    {
        mouseLocation = event.getLocationOnScreen();
        for (int i = mouse_moved_start; i < mouse_dragged_start; i++)
        {
            try
            {
                notifications.get(i).call(event);
            }
            catch (IllegalArgumentException ex)
            {
                ex.printStackTrace();
            }
            catch (IllegalAccessException ex)
            {
                ex.printStackTrace();
            }
            catch (InvocationTargetException ex)
            {
                ex.printStackTrace();
            }
        }
        event.consume();
    }
    
    /**
     * Invoked when the mouse is moved over a component while one of the buttons is held.
     * Sends out relevant notifications based on the event.
     */
    public void mouseDragged(MouseEvent event)
    {
        mouseLocation = event.getLocationOnScreen();
        for (int i = mouse_dragged_start; i < notifications.size(); i++)
        {
            try
            {
                notifications.get(i).call(event);
            }
            catch (IllegalArgumentException ex)
            {
                ex.printStackTrace();
            }
            catch (IllegalAccessException ex)
            {
                ex.printStackTrace();
            }
            catch (InvocationTargetException ex)
            {
                ex.printStackTrace();
            }
        }
        event.consume();
    }
    
    /**
     * Invoked when the mouse wheel is rotated.
     * Sends out relevant notifications based on the event.
     */
    public void mouseWheelMoved(MouseWheelEvent event)
    {
        for (int i = mouse_wheel_start; i < mouse_moved_start; i++)
        {
            try
            {
                notifications.get(i).call(event);
            }
            catch (IllegalArgumentException ex)
            {
                ex.printStackTrace();
            }
            catch (IllegalAccessException ex)
            {
                ex.printStackTrace();
            }
            catch (InvocationTargetException ex)
            {
                ex.printStackTrace();
            }
        }
        event.consume();
    }
    
    /**
     * Returns the current position of the mouse on the screen, in pixels.
     * The x-coordinate of the returned point is the cursor's distance from the left side of the
     *  screen, the y-coordinate of the point is the cursor's distance from the top of the screen.
     * 
     * @return the location of the mouse pointer
     */
    public static Point getMouse()
    {
        return mouseLocation;
    }
    
    /**
     * Determines whether the given mouse wheel rotation is up (away from the user).
     * The rotation can be found with a {@link MouseWheelEvent#getWheelRotation()}.
     * Note: if there was no rotation, false is returned, so use this method only to determine is
     *  the rotation is up - use {@link #isDown(int)} to determine if it is down.
     * 
     * @param rotation - the amount rotated
     * @return true if the rotation was up (away from the user), false otherwise
     * @see #isDown(int)
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
     * The rotation can be found with a {@link MouseWheelEvent#getWheelRotation()}.
     * Note: if there was no rotation, false is returned, so use this method only to determine is
     *  the rotation is down - use {@link #isUp(int)} to determine if it is up.
     * 
     * @param rotation - the amount rotated
     * @return true if the rotation was down (toward the user), false otherwise
     * @see #isUp(int)
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
     * Determines whether the "shift" key is currently pressed.
     * 
     * @return true if the shift key is held, false otherwise
     */
    public static boolean shiftHeld()
    {
        return shiftHeld;
    }
    
    /**
     * Determines whether the "control" key is currently pressed.
     * 
     * @return true if the control key is held, false otherwise
     */
    public static boolean controlHeld()
    {
        return controlHeld;
    }
    
    /**
     * Determines whether the "alt" key is currently pressed.
     * 
     * @return true if the alt key is held, false otherwise
     */
    public static boolean altHeld()
    {
        return altHeld;
    }
    
    /**
     * Shifts the indexes at which various types of notification requests begin.
     * The indexes are changed so that a notification request with the given type can be added in
     *  its correct position.
     * 
     * @param type - the type of notification request to be adjusted for
     * @see #getStart(int)
     */
    private static void shiftStarts(int type)
    {
        if (type == TYPE_KEY_PRESSED)
        {
            key_released_start++;
            mouse_pressed_start++;
            mouse_released_start++;
            mouse_wheel_start++;
            mouse_moved_start++;
            mouse_dragged_start++;
        }
        else if (type == TYPE_KEY_RELEASED)
        {
            mouse_pressed_start++;
            mouse_released_start++;
            mouse_wheel_start++;
            mouse_moved_start++;
            mouse_dragged_start++;
        }
        else if (type == TYPE_MOUSE_PRESSED)
        {
            mouse_released_start++;
            mouse_wheel_start++;
            mouse_moved_start++;
            mouse_dragged_start++;
        }
        else if (type == TYPE_MOUSE_RELEASED)
        {
            mouse_wheel_start++;
            mouse_moved_start++;
            mouse_dragged_start++;
        }
        else if (type == TYPE_MOUSE_WHEEL)
        {
            mouse_moved_start++;
            mouse_dragged_start++;
        }
        else if (type == TYPE_MOUSE_MOVED)
        {
            mouse_dragged_start++;
        }
    }
    
    /**
     * Returns the starting index of the notification requests of the given type.
     * A new notification request of the given type should be added to this location.
     * Either before or after the notification request is added, a call to 
     *  {@link #shiftStarts(int)} should be made.
     * 
     * @param type - the type of notification request to be added
     * @return the starting index for the given type, -1 is the type is not recognized
     * @see #shiftStarts(int)
     */
    private static int getStart(int type)
    {
        switch (type)
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
        case TYPE_MOUSE_DRAGGED:
            return mouse_dragged_start;
        default:
            return -1;
        }
    }
}

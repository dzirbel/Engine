package graphics;

import io.Listener;

import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * This class is a simple interface for creating Button components on the screen.
 * A specified rectangle can be used to create button areas which, when clicked (pressed and
 *  released without dragging the cursor out of the area), trigger a method call.
 * 
 * @author zirbinator
 */
public class ButtonListener
{
    private boolean pressed;
    private boolean on;
    
    private Method method;
    
    private Object object;
    
    private Rectangle button;
    
    private String name;
    
    /**
     * Creates a new ButtonListener for the given area.
     * 
     * @param button - the area of the screen for which this ButtonListener calls the given method
     *  when clicked
     * @param method - the Method to be called when the given button area is clicked
     * @param object - the object on which to invoke the Method when the button area is clicked
     *  (typically <code>this</code>)
     */
    public ButtonListener(Rectangle button, Method method, Object object)
    {
        if (method == null)
        {
            throw new IllegalArgumentException("Null method");
        }
        if (object == null)
        {
            throw new IllegalArgumentException("Null object");
        }
        
        this.method = method;
        this.object = object;
        this.button = button;
        name = null;
        pressed = false;
        on = true;
        
        Listener.requestNotification(this, "mousePressed",
                Listener.TYPE_MOUSE_PRESSED, Listener.CODE_BUTTON1);
        Listener.requestNotification(this, "mouseReleased",
                Listener.TYPE_MOUSE_RELEASED, Listener.CODE_BUTTON1);
        Listener.requestNotification(this, "mouseMoved",
                Listener.TYPE_MOUSE_MOVED);
        Listener.requestNotification(this, "mouseMoved",
                Listener.TYPE_MOUSE_DRAGGED);
    }
    
    /**
     * Creates a new ButtonListener for the given area.
     * The method to be invoked on a click is chosen as follows:
     * <ol>
     * <li>a list of all the methods supported by the given object is found</li>
     * <li>the list is searched for methods whose name is equal to the given name and have exactly
     *  one argument of the type ButtonListener</li>
     * <li>if such a method is found, it will be used and <code>this</code> will be passed as the
     *  argument when the button is clicked</li>
     * <li>otherwise the list of methods is searched again, and the method whose name is equal to
     *  the given name with the fewest number of arguments is used</li>
     * <li>if a method is found with this procedure, it will be called with all <code>null</code>
     *  arguments</li>
     * <li>if no such method is found (and so no method matches the given name), a 
     *  {@link NoSuchMethodException} is thrown</li>
     * </ol>
     * 
     * @param button - the area of the screen for which this ButtonListener calls the given method
     *  when clicked
     * @param methodName - the name of the Method to be called when the given button area is
     *  clicked
     * @param object - the object on which to invoke the Method when the button area is clicked
     * @throws NoSuchMethodException thrown if there is no method matching the given method name
     *  supported by the given object
     */
    public ButtonListener(Rectangle button, String methodName, Object object)
            throws NoSuchMethodException
    {
        this(button, getMethod(object, methodName), object);
    }
    
    /**
     * Determines which Method should be invoked on a click with the given object and method name.
     * The method to be invoked on a click is chosen as follows:
     * <ol>
     * <li>a list of all the methods supported by the given object is found</li>
     * <li>the list is searched for methods whose name is equal to the given name and have exactly
     *  one argument of the type ButtonListener</li>
     * <li>if such a method is found, it will be used and <code>this</code> will be passed as the
     *  argument when the button is clicked</li>
     * <li>otherwise the list of methods is searched again, and the method whose name is equal to
     *  the given name with the fewest number of arguments is used</li>
     * <li>if a method is found with this procedure, it will be called with all <code>null</code>
     *  arguments</li>
     * <li>if no such method is found (and so no method matches the given name), a 
     *  {@link NoSuchMethodException} is thrown</li>
     * </ol>
     * 
     * @param object - the object on which to invoke the Method when the button area is clicked
     * @param methodName - the name of the Method to be called when the given button area is
     *  clicked
     * @return the Method that should be invoked when the button area is clicked
     * @throws NoSuchMethodException thrown if there is no method matching the given method name
     *  supported by the given object
     */
    private static Method getMethod(Object object, String methodName) throws NoSuchMethodException
    {
        Method[] methods = object.getClass().getMethods();
        
        for (int i = 0; i < methods.length; i++)
        {
            if (methods[i].getName().equals(methodName) && 
                    methods[i].getParameterTypes().length == 1 &&
                    methods[i].getParameterTypes()[0] == ButtonListener.class)
            {
                return methods[i];
            }
        }
        
        int fewestArgsIndex = -1;
        for (int i = 0; i < methods.length; i++)
        {
            if (methods[i].getName().equals(methodName) && 
                    (fewestArgsIndex == -1 || methods[i].getParameterTypes().length < 
                    methods[fewestArgsIndex].getParameterTypes().length))
            {
                fewestArgsIndex = i;
            }
        }
        
        if (fewestArgsIndex == -1)
        {
            throw new NoSuchMethodException(object.getClass().getName() + 
                    " does not support a method named " + methodName);
        }
        
        return methods[fewestArgsIndex];
    }
    
    /**
     * Gets the area of the screen which is considered to be the "button area" - the area that this
     *  ButtonListener is listening to and will respond to if clicked.
     * 
     * @return a copy of the button area for this ButtonListener
     * @see #setButton(Rectangle)
     */
    public Rectangle getButton()
    {
        return (Rectangle) button.clone();
    }
    
    /**
     * Sets the area of the screen which is considered to be the "button area" - the area that this
     *  ButtonListener is listening to and will respond to if clicked.
     * Note that the button will no longer be considered pressed if the given button is different
     *  than the current area.
     * 
     * @param button the button area for this ButtonListener
     * @see #getButton()
     */
    public void setButton(Rectangle button)
    {
        if (!this.button.equals(button) && button != null)
        {
            this.button = button;
            pressed = false;
        }
    }
    
    /**
     * Gets the name associated with this ButtonListener.
     * This is typically used to identify which button has been pressed if this object is passed on
     *  the method invocation.
     * 
     * @return this ButtonListener's name
     * @see #setName(String)
     */
    public String getName()
    {
        return name;
    }
    
    /**
     * Sets the name associated with this ButtonListener.
     * This is typically used to identify which button has been pressed if this object is passed on
     *  the method invocation.
     * 
     * @param name - this ButtonListener's name
     * @see #getName(String)
     */
    public void setName(String name)
    {
        this.name = name;
    }
    
    /**
     * Determines whether this ButtonListener is currently "on" - that is, whether it is listening
     *  for click events.
     * 
     * @return true if this ButtonListener is listening for click events, false otherwise
     * @see #setOn(boolean)
     */
    public boolean isOn()
    {
        return on; 
    }
    
    /**
     * Sets whether this ButtonListener should be "on" - that is, listen for click events.
     * 
     * @param on true if this ButtonListener should notify the client when the button area is
     *  pressed, false otherwise
     * @see #isOn()
     */
    public void setOn(boolean on)
    {
        this.on = on;
        if (!on)
        {
            pressed = false;
        }
    }
    
    /**
     * Determines whether the button area is current pressed.
     * 
     * @return true if the button area is pressed, false otherwise
     */
    public boolean isPressed()
    {
        return pressed;
    }
    
    /**
     * Invoked by the {@link Listener} when the mouse is pressed.
     * 
     * @param e - the triggering event
     */
    public void mousePressed(MouseEvent e)
    {
        if (on && button.contains(e.getLocationOnScreen()))
        {
            pressed = true;
        }
    }
    
    /**
     * Invoked by the {@link Listener} when the mouse is moved or dragged.
     * 
     * @param e - the triggering event
     */
    public void mouseMoved(MouseEvent e)
    {
        pressed = pressed && button.contains(e.getLocationOnScreen());
    }
    
    /**
     * Invoked by the {@link Listener} when the mouse is released.
     * 
     * @param e - the triggering event
     */
    public void mouseReleased(MouseEvent e)
    {
        if (pressed)
        {
            int numArgs = method.getParameterTypes().length;
            Object[] args = new Object[numArgs];
            if (numArgs == 1 && method.getParameterTypes()[0] == ButtonListener.class)
            {
                args[0] = this;
            }
            else
            {
                for (int i = 0; i < numArgs; i++)
                {
                    args[i] = null;
                }
            }
            
            try
            {
                method.invoke(object, args);
            }
            catch (IllegalAccessException ex)
            {
                ex.printStackTrace();
            }
            catch (IllegalArgumentException ex)
            {
                ex.printStackTrace();
            }
            catch (InvocationTargetException ex)
            {
                ex.printStackTrace();
            }
            
            pressed = false;
        }
    }
}

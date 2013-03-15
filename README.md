# The Engine

<br>

The Engine is a collection of general-purpose utility classes and functions written in Java created by zirbinator [Dominic Zirbel, zirbinator@gmail.com]. This software is open-source and free to use under the GNU General Public License, Version 3 (http://www.gnu.org/licenses/gpl.html).

## About

These classes are intended primarily as a convenience for graphics-intensive Java applications such as games or graphics-oriented programs. Currently only two dimensional applications are supported. The Engine's client programs are:

* Game of Life [github.com/zirbinator/Game-of-Life]

## Features

The Engine currently includes the features:

* Hardware-accelerated image rendering
* Full-screen exclusive windows
* Customizeable tooltips
* High-level mouse and keyboard input handling
* List utilities, including binary searches and quicksort
* Display capability, image speed, and other tests

## Future

The Engine is currently evolving along with the Game of Life project with the sole purpose of supporting the Game of Life. However I do intend to add generality and functionality to the Engine. Some potential upcoming features are:

* Comprehensive image loading and data management
* Rendering performance
* File I/O

# Using the Engine

The Engine is functional software that gives desktop Java applications some amount of graphics support. To include the Engine in a program, simply include the most recent jar binary, which can be found in the [root repository folder](http://www.github.com/zirbinator/Engine) named "Engine X.X.XX.jar". To download, either select the file and "View Raw" or download the entire repository as a zip and extract it.

## Images

With the Engine, loading images from the disk is simple, and rendering utalizes hardware acceleration. The `AcceleratedImage` class allows for this functionality. Typically, loading an image is as simple as instantiating an `AcceleratedImage` with the filename of the image to load. The `AcceleratedImage` can then be transformed with an `AffineTransform` and rendered to an arbitrary graphics context. See the `AcceleratedImage` documentation for more information.

## User Input

User input from the mouse and keyboard is encompassed by the `Listener` class. To use the functionality of the `Listener`, there are two steps. First, request notifications for specific types of events (i.e. mouse buttons presses, key releases) and then create methods to handle these events when they occur. The `Listener` uses reflection to invoke these methods, and is based on the event handling from `java.awt.event`.

## Display

The Engine has the ability to create a full-screen exclusive window which allows for more control over rendering than a regular window. The `DisplayMonitor` class handles this functionality, and is also able to resize the display resolution (if supported by the operating system and hardware). Finally, the `DisplayMonitor` tracks the size of the screen (in pixels).

## Tooltips

The Engine contains the `Tooltip` class which represent tooltips on the screen, which are activated when the user hovers over a certain area of the screen for a certain amount of time. Tooltips are lightweight and extremely configurable. See the `Tooltip` class for more information.

## List Utilities

Finally, the Engine contains the `ListUtil` class which includes many common utilities for lists, including binary searching, sorting with quicksort, and adding elements, while keeping a list sorted, based on a binary search. See the `ListUtil` class for more information.
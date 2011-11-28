package graphics;

import java.util.ArrayList;

/**
 * Takes care of all image loading so that required images are only loaded once from the file.
 * An ArrayList of AcceleratedImages is kept that holds all the information for loaded images.
 * This list is sorted lexicographically by the reference names so that searches by reference name are optimized for speed.
 * Each loaded image has a reference name (used to get the image from the list), a filename (the location of the image on file), 
 *  an index (used for faster retrieval of the image), and an AcceleratedImage which holds all the contents of the image itself.
 * When an image is known to be needed later, the add() methods should be used with whatever information is available.
 * To access an image, use the get() methods, preferably one that adds the image to the list if it has not been.
 * 
 * @author Dominic
 */
public class ImageLoader
{
	private ArrayList<LoadedImage> images;
	
	public static final int IMAGE = 1;
	public static final int ACCELERATED_IMAGE = 2;
	public static final int BUFFERED_IMAGE = 3;
	
	/**
	 * Creates a new ImageLoader with the given Information.
	 * 
	 * @param info
	 */
	public ImageLoader()
	{
		images = new ArrayList<LoadedImage>();
	}
	
	/**
	 * Adds the image found at the given filename with the given quality to the image list.
	 * The name used to reference the image is set to the same as the filename.
	 * 
	 * @param filename - the location of the image
	 * @param quality - the quality of the image
	 * @return index - the index of the image in the list
	 */
	public int add(String filename, int quality)
	{
		int index = getAddIndex(filename);
		if (index != -1)
		{
			LoadedImage image = new LoadedImage(filename, quality);
			image.setIndex(index);
			images.add(index, image);
			return index;
		}
		return getIndex(filename);
	}
	
	/**
	 * Adds the image found at the given filename with the automatically detected quality to the image list.
	 * The name used to reference the image is set to the same as the filename.
	 * 
	 * @param filename - the location of the image
	 * @return index - the index of the image in the list
	 */
	public int add(String filename)
	{
		int index = getAddIndex(filename);
		if (index != -1)
		{
			LoadedImage image = new LoadedImage(filename);
			image.setIndex(index);
			images.add(index, image);
			return index;
		}
		return getIndex(filename);
	}
	
	/**
	 * Adds the image found at the given filename with the given quality and reference name to the image list.
	 * 
	 * @param filename - the location of the image
	 * @param referenceName - the name typically used to get the image from the ImageLoader
	 * @param quality - the quality of the image
	 * @return index - the index of the image in the list
	 */
	public int add(String filename, String referenceName, int quality)
	{
		int index = getAddIndex(referenceName);
		if (index != -1)
		{
			LoadedImage image = new LoadedImage(filename, referenceName, quality);
			image.setIndex(index);
			images.add(index, image);
			return index;
		}
		return getIndex(referenceName);
	}
	
	/**
	 * Adds the image found at the given filename with the given reference name and automatically detected quality to the image list.
	 * 
	 * @param filename - the location of the image
	 * @param referenceName - the name typically used to get the image from the ImageLoader
	 * @return index - the index of the image in the list
	 */
	public int add(String filename, String referenceName)
	{
		int index = getAddIndex(referenceName);
		if (index != -1)
		{
			LoadedImage image = new LoadedImage(filename, referenceName);
			image.setIndex(index);
			images.add(index, image);
			return index;
		}
		return getIndex(referenceName);
	}
	
	/**
	 * Adds the given image with the given reference name to the image list.
	 * Note: the filename is not given and cannot be found, so calls to getByFile will not find this image.
	 * 
	 * @param image - the image to be added
	 * @param referenceName - the name typically used to get the image from the ImageLoader
	 * @return index - the index of the image in the list
	 */
	public int add(AcceleratedImage image, String referenceName)
	{
		int index = getAddIndex(referenceName);
		if (index != -1)
		{
			LoadedImage loadedImage = new LoadedImage(image, referenceName);
			loadedImage.setIndex(index);
			images.add(index, loadedImage);
			return index;
		}
		return getIndex(referenceName);
	}
	
	/**
	 * Gets the first image from the list with the matching reference name.
	 * If no image is found with the given reference name, null is returned.
	 * Thus, if there is any question as to whether or not the image has been added, 
	 *  an alternate get method should be used that automatically adds a missing image.
	 * This method currently uses the less-efficient linear search, and, if possible, the index should be used to find the image.
	 * 
	 * @param referenceName - the name assigned to the image upon addition, the filename if none was given
	 * @return image - the image with the given reference name, null if none is found
	 */
	public AcceleratedImage get(String referenceName)
	{
		for (int i = 0; i < images.size(); i++)
		{
			if (referenceName.equals(images.get(i).getReferenceName()))
			{
				return images.get(i).getImage();
			}
		}
		return null;
	}
	
	/**
	 * The preferred method of getting an image from the ImageLoader, this method returns the image at the given index.
	 * If the index if out of the bounds of the list, null is returned.
	 * Thus, if there is any question as to whether or not the image has been added,
	 *  an alternate get method should be used that automatically adds a missing image.
	 * 
	 * @param index - the index of the image to be returned
	 * @return image - the image with the given index, null if none exists
	 */
	public AcceleratedImage get(int index)
	{
		try
		{
			return images.get(index).getImage();
		}
		catch (IndexOutOfBoundsException ex)
		{
			return null;
		}
	}
	
	/**
	 * Returns the image with the given filename.
	 * If no such image has been added, one is added with the given filename,
	 *  automatically detected quality, and with the reference name set to the filename.
	 * Note: seeing as the image list is sorted lexicographically by reference name and not filename,
	 *  this search is linear rather than binary, and thus much slower than if the reference name is provided
	 * 
	 * @param filename - the location of the image to be found or added
	 * @return image - the image with the given filename
	 */
	public AcceleratedImage getByFile(String filename)
	{
		for (int i = 0; i < images.size(); i++)
		{
			if (filename.equals(images.get(i).getFilename()))
			{
				return images.get(i).getImage();
			}
		}
		return get(add(filename));
	}
	
	/**
	 * The most robust method of getting an image from the list, this method searches for an image with the given reference name, 
	 *  and if none is found adds a new image with full initialization (filename, reference name, and automatically detected quality).
	 * A binary search is used to query the lexicographically sorted image list for optimized speed.
	 * 
	 * @param filename - the location of the image to be found
	 * @param referenceName - the name used to search the list
	 * @return image - the image with the given reference name, or, none exists, the given filename
	 */
	public AcceleratedImage getImage(String filename, String referenceName)
	{
		int index = getIndex(referenceName);
		if (index == -1)
		{
			return get(add(filename, referenceName));
		}
		else
		{
			return images.get(index).getImage();
		}
	}
	
	/**
	 * Gets the index of the image with the given reference name.
	 * A binary search is used to query the lexicographically sorted image list for optimized speed.
	 * If no image with this name is found, -1 is returned and no image is added.
	 * 
	 * @param referenceName - the name used to access the image from the list
	 * @return index - the index of the image, -1 if none is found
	 */
	public int getIndex(String referenceName)
	{
		int start = 0;
		int end = images.size();
		int mid = (start + end)/2;
		while (start < end)
		{
			mid = (start + end)/2;
			int comparison = referenceName.compareTo(images.get(mid).getReferenceName());
			if (comparison < 0)
			{
				end = mid;
			}
			else if (comparison > 0)
			{
				start = mid + 1;
			}
			else
			{
				return mid;
			}
		}
		return -1;
	}
	
	/**
	 * Returns the index at which a LoadedImage with the given reference name should be added.
	 * The index is found by searching the list of images linearly until a reference name that should come after 
	 *  the given reference name (the given reference name lexicographically precedes the reference name within the list) is found.
	 * If no such name is found, then the size of list is returned, that is, the given name should be appended to the list.
	 * If the name is already in the list, then -1 is returned, a warning message is printed, and the image should not be added.
	 * 
	 * @param referenceName - the name to be added to the list
	 * @return index - where the given reference name should be added, -1 if it already exists within the list
	 */
	private int getAddIndex(String referenceName)
	{
		for (int i = 0; i < images.size(); i++)
		{
			int comparison = referenceName.compareTo(images.get(i).getReferenceName());
			if (comparison < 0)
			{
				return i;
			}
			else if (comparison == 0)
			{
				System.out.println("[WARNING] An attempt was made to add the image with reference name " + referenceName + " to the ImageLoader twice.");
				return -1;
			}
		}
		return images.size();
	}
}

/**
 * Represents an individual image that has been loaded by the ImageLoader.
 * Each LoadedImage has an AcceleratedImage that holds the contents of the image,
 *  a filename which points to the source of the image,
 *  a refereneName which is the name typically used to find the image in the ImageLoader,
 *  and an index that can be used to find the image in the ImageLoader faster.
 * 
 * @author Dominic
 */
class LoadedImage
{
	private AcceleratedImage image;
	
	private int index;
	
	private String filename;
	private String referenceName;
	
	/**
	 * Creates a new LoadedImage with the given filename and quality.
	 * The reference name used to query the ImageLoader is set to the filename.
	 * 
	 * @param filename - the source of the image
	 * @param quality - the quality of the image
	 */
	public LoadedImage(String filename, int quality)
	{
		this.filename = filename;
		referenceName = filename;
		image = new AcceleratedImage(filename, quality);
		index = -1;
	}
	
	/**
	 * Creates a new LoadedImage with the given filename and the automatically detected quality.
	 * The referenceName used to query the ImageLoader is set to the filename.
	 * 
	 * @param filename - the source of the image
	 */
	public LoadedImage(String filename)
	{
		this.filename = filename;
		referenceName = filename;
		image = new AcceleratedImage(filename);
		index = -1;
	}
	
	/**
	 * Creates a new LoadedImage with the given filename, referenceName, and quality.
	 * 
	 * @param filename - the source of the image
	 * @param referenceName - the name used to find the image from the ImageLoader
	 * @param quality - the quality of the image
	 */
	public LoadedImage(String filename, String referenceName, int quality)
	{
		this.filename = filename;
		this.referenceName = referenceName;
		image = new AcceleratedImage(filename, quality);
		index = -1;
	}
	
	/**
	 * Creates a new LoadedImage with the given filename, referenceName, and the automatically detected quality.
	 * 
	 * @param filename - the source of the image
	 * @param referenceName - the name used to find the image from the ImageLoader
	 */
	public LoadedImage(String filename, String referenceName)
	{
		this.filename = filename;
		this.referenceName = referenceName;
		image = new AcceleratedImage(filename);
		index = -1;
	}
	
	/**
	 * Creates a new LoadedImage with the preloaded AcceleratedImage and referenceName.
	 * The filename of this LoadedImage is set to null.
	 * 
	 * @param image - the image used in this LoadedImage
	 * @param referenceName - the name used to find the image from the ImageLoader
	 */
	public LoadedImage(AcceleratedImage image, String referenceName)
	{
		filename = null;
		this.referenceName = referenceName;
		this.image = image;
		index = -1;
	}
	
	/**
	 * Sets the index value stored within this LoadedImage.
	 * Note: this does not actually change the position of the LoadedImage within the ImageLoader's ArrayList.
	 * 
	 * @param index - the new index value
	 */
	public void setIndex(int index)
	{
		this.index = index;
	}
	
	/**
	 * Returns the referenceName used to find the image from the ImageLoader.
	 * 
	 * @return referenceName - the name typically used to find the image from the ImageLoader
	 */
	public String getReferenceName()
	{
		return referenceName;
	}
	
	/**
	 * Returns the filename that was given upon initialization.
	 * Note: if the LoadedImage was initialized with only an AcceleratedImage, this value will be null.
	 * 
	 * @return filename - the source of the image
	 */
	public String getFilename()
	{
		return filename;
	}
	
	/**
	 * Returns the AcceleratedImage that this LoadedImage is holding.
	 * 
	 * @return image - the AcceleratedImage held by this LoadedImage
	 */
	public AcceleratedImage getImage()
	{
		return image;
	}
	
	/**
	 * Returns the index that was given to this LoadedImage.
	 * This index should be the index that is used by the ImageLoader.
	 * Note: if initialization was not robust or setIndex() was not called or called improperly, this index may not be accurate.
	 * 
	 * @return index - the position of this LoadedImage within ImageLoader's ArrayList
	 */
	public int getIndex()
	{
		return index;
	}
}
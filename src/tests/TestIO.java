package tests;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import utils.ExceptionHandler;

/**
 * A class that acts as an interface between the tests and the command line and the file at which the results are saved.
 * This class has a dual function.
 * First, there is the static half of the class, used to statically print the results of a test to the command line and automatically found filename.
 * This half is very self-contained but does not format the results beyond adding a simple header.
 * The second half of the the class is not static and exists simply as an easy way to collect the results of a test.
 * The TestIO object has one field, an ArrayList of Strings, which is added to using the print() and println() methods,
 *  which append the given text to the top String and add a new String comprising of the given text, respectively.
 * This ArrayList of Strings can then be accessed in order to print the results with the getText() method.
 * 
 * @author Dominic
 */
public class TestIO
{
	private ArrayList<String> text;
	
	/**
	 * Creates a new TestIO object.
	 */
	public TestIO()
	{
		text = new ArrayList<String>();
	}
	
	/**
	 * Adds the given String to the text as a new String, thus the text will be printed as a new line.
	 * 
	 * @param line - the text to add
	 */
	public void println(String line)
	{
		text.add(line);
	}
	
	/**
	 * Adds a blank String, creating an empty line.
	 */
	public void println()
	{
		text.add(new String());
	}
	
	/**
	 * Adds the given String to the end of the most recently added String, putting them on the same line.
	 * 
	 * @param line - the text to add
	 */
	public void print(String line)
	{
		text.set(text.size() - 1, text.get(text.size() - 1).concat(line));
	}
	
	/**
	 * Gets the text, as an ArrayList of Strings intended to be printed on separate lines, that has been added via the print() and println() methods thus far.
	 * 
	 * @return text - the current text held by this TextIO object
	 */
	public ArrayList<String> getText()
	{
		return text;
	}
	
	/**
	 * Prints the given results of a Test with the given name to the command line and the automatically found filename.
	 * First, the results are printed to the command line with a header.
	 * Then, a PrintWriter is used to print identical results with a slightly different header to the file found with getResultsFilename().
	 * 
	 * @param test - the name of the test that produced the given results, found with the getName() method
	 * @param results - the results, which should be split into Strings each to be printed on their own line
	 */
	public static void printResults(String testName, ArrayList<String> results)
	{
		System.out.println("A " + testName + " finished its execution at " + getDate() + ".");
		System.out.println();
		for (int i = 0; i < results.size(); i++)
		{
			System.out.println(results.get(i));
		}
		System.out.println();
		
		String filename = getResultsFilename(testName);
		try
		{
			PrintWriter out = new PrintWriter(new FileWriter(filename));
			out.println(testName + " results from " + getDate());
			out.println();
			for (int i = 0; i < results.size(); i++)
			{
				out.println(results.get(i));
			}
			System.out.println("The results of this test have been saved at " + filename + " without error.");
			out.close();
		}
		catch (IOException ex)
		{
			ExceptionHandler.receive(ex, "There was an error while saving the results of the test at " + filename + ".");
		}
	}
	
	/**
	 * Prints the given results of the given Test to the command line and the automatically found filename.
	 * First, the results are printed to the command line with a header.
	 * Then, a PrintWriter is used to print identical results with a slightly different header to the file found with getResultsFilename().
	 * 
	 * @param test - the test that produced the results to be printed
	 * @param results - the results, which should be split into Strings each to be printed on their own line
	 */
	public static void printResults(Test test, ArrayList<String> results)
	{
		printResults(test.getName(), results);
	}
	
	/**
	 * Returns the String filename at which the given test's results should be written.
	 * All test results are first put into the "tests" folder, and then sorted into folders by the given test name.
	 * Inside the folders named by the given test name, found with the getName() method, the tests are listed by name.
	 * This name is constructed by a number of factors and is of the following format:<br>
	 * #testNumber# - yyyy-MM-dd HH.mm.ss (aa)<br>
	 * where #testNumber# is the number of the test that have been run (0,1,2,etc.) and the rest is the current date and time.
	 * 
	 * @param testName - the name of the test whose results are to be printed
	 * @return filename - the location at which the results should be printed
	 */
	private static String getResultsFilename(String testName)
	{
		String folderPath = "tests/" + testName + "/";
		File folder = new File(folderPath);
		folder.mkdir();
		File[] files = folder.listFiles();
		int max = -1;
		Integer prevNumber;
		if (files.length > 0)
		{
			for (int i = 0; i < files.length; i++)
			{
				String prevNumberString = files[i].getName();
				prevNumberString = prevNumberString.substring(0, prevNumberString.indexOf(' '));
				prevNumber = Integer.parseInt(prevNumberString.trim());
				max = Math.max(max, prevNumber);
			}
		}
		else
		{
			max = -1;
		}
		System.out.println("previous: " + max);
		String name = (max + 1) + " - " + getDateFilename();
		return new String("tests/" + testName + "/" + name + ".txt");
	}
	
	/**
	 * Returns the current date and time using the format that Windows will allow to exist in a filename, that is, without "/","\",".", or ":".
	 * 
	 * @return date - the current data and time found using SimpleDateFormat
	 */
	private static String getDateFilename()
	{
		return new SimpleDateFormat("yyyy-MM-dd hh.mm.ss (aa)").format(new Date());
	}
	
	/**
	 * Returns the current date and time using a standard format to be printed to the command line and within files.
	 * 
	 * @return date - the current data and time found using SimpleDateFormat
	 */
	private static String getDate()
	{
		return new SimpleDateFormat("yyyy/MM/dd hh:mm:ss (aa)").format(new Date());
	}
	
	/**
	 * Returns the next index for data entries for the test of the given name.
	 * Data entries are organized by numbers which, starting from 0, are assigned to tests which wish to enter data in their data folder.
	 * This is a similar approach to the organization used by the results text files, but multiple data entries may be entered by one test.
	 * Thus, a single data entry number is assigned to that test and must be used whenever a new data entry is to be made.
	 * 
	 * @param testName - the name of the test wishing to enter data
	 * @return number - the next index which will be assigned to the test
	 */
	public static int getDataEntryNumber(String testName)
	{
		String folderPath = "tests/" + testName + " Data/";
		File folder = new File(folderPath);
		folder.mkdir();
		File[] files = folder.listFiles();
		Integer prevNumber;
		if (files.length > 0)
		{
			String prevNumberString = files[files.length - 1].getName();
			prevNumberString = prevNumberString.substring(0, prevNumberString.indexOf('_'));
			prevNumber = Integer.parseInt(prevNumberString);
		}
		else
		{
			prevNumber = -1;
		}
		return prevNumber + 1;
	}
	
	/**
	 * Writes the given data to a file with the location automatically found using the test name, suffix, and data entry number.
	 * All the data is organized in a manner similar to the results.
	 * There is a master folder with the test name and the word "Data" which contains all the data files.
	 * Inside this folder, the data files are organized primarily by the data entry number.
	 * This unique number for each test assigns it an index.
	 * The index should be found with the getDataEntryNumber() method and used whenever that test wished to enter data.
	 * The last organizational technique is the suffix which is given by the test and is attached after the data entry number.
	 * Finally, a data and time are added to the end of the filename.
	 * 
	 * @param testName - the name of the test that is entering data
	 * @param suffix - the identification String provided with a data entry
	 * @param dataEntryNumber - the unique index for this particular test to enter data
	 * @param data - the data that should written as an array of doubles
	 */
	public static void printData(String testName, String suffix, int dataEntryNumber, double[] data)
	{
		String filename = getDataFilename(testName, suffix, dataEntryNumber);
		System.out.println("Printing data from a " + testName + " to " + filename + ".");
		try
		{
			DataOutputStream out = new DataOutputStream(new FileOutputStream(filename));
			for (int i = 0; i < data.length; i++)
			{
				out.writeDouble(data[i]);
			}
			out.close();
		}
		catch (IOException ex)
		{
			ExceptionHandler.receive(ex, "There was an error while saving the data from a test at " + filename + ".");
		}
		System.out.println("The data was printed without error.");
	}
	
	/**
	 * Writes the given data to a file with the location automatically found using the test name, suffix, and data entry number.
	 * All the data is organized in a manner similar to the results.
	 * There is a master folder with the test name and the word "Data" which contains all the data files.
	 * Inside this folder, the data files are organized primarily by the data entry number.
	 * This unique number for each test assigns it an index.
	 * The index should be found with the getDataEntryNumber() method and used whenever that test wished to enter data.
	 * Finally, a data and time are added to the end of the filename.
	 * 
	 * @param testName - the name of the test that is entering data
	 * @param dataEntryNumber - the unique index for this particular test to enter data
	 * @param data - the data that should written as an array of doubles
	 */
	public static void printData(String testName, int dataEntryNumber, double[] data)
	{
		printData(testName, "", dataEntryNumber, data);
	}
	
	/**
	 * Writes the given data to a file with the location automatically found using the test name, suffix, and data entry number.
	 * All the data is organized in a manner similar to the results.
	 * There is a master folder with the test name and the word "Data" which contains all the data files.
	 * Inside this folder, the data files are organized primarily by the data entry number.
	 * This unique number for each test assigns it an index.
	 * The index should be found with the getDataEntryNumber() method and used whenever that test wished to enter data.
	 * Finally, a data and time are added to the end of the filename.
	 * 
	 * @param testName - the name of the test that is entering data
	 * @param dataEntryNumber - the unique index for this particular test to enter data
	 * @param data - the data that should written as an ArrayList of Longs
	 */
	public static void printData(String testName, String suffix, int dataEntryNumber, ArrayList<Long> data)
	{
		double[] dataD = new double[data.size()];
		for (int i = 0; i < data.size(); i++)
		{
			dataD[i] = data.get(i);
		}
		printData(testName, suffix, dataEntryNumber, dataD);
	}
	
	/**
	 * Writes the given data to a file with the location automatically found using the test name, suffix, and data entry number.
	 * All the data is organized in a manner similar to the results.
	 * There is a master folder with the test name and the word "Data" which contains all the data files.
	 * Inside this folder, the data files are organized primarily by the data entry number.
	 * This unique number for each test assigns it an index.
	 * The index should be found with the getDataEntryNumber() method and used whenever that test wished to enter data.
	 * Finally, a data and time are added to the end of the filename.
	 * 
	 * @param testName - the name of the test that is entering data
	 * @param dataEntryNumber - the unique index for this particular test to enter data
	 * @param data - the data that should written as an ArrayList of Longs
	 */
	public static void printData(String testName, int dataEntryNumber, ArrayList<Long> data)
	{
		double[] dataD = new double[data.size()];
		for (int i = 0; i < data.size(); i++)
		{
			dataD[i] = data.get(i);
		}
		printData(testName, "", dataEntryNumber, dataD);
	}
	
	/**
	 * Returns the filename that is used to enter data for a test with the given name, suffix, and data entry number.
	 * The format for this filename is as follows: tests/testName Data/name.dat
	 *  where the test name is given and the name is a combination of the data entry number, the suffix, and the current data and time.
	 * 
	 * @param testName - the name of the test that is entering data
	 * @param suffix - the identification String provided with a data entry
	 * @param dataEntryNumber - the unique index for this particular test to enter data
	 * @return filename - the location to which the data should be written, as a .bin file
	 */
	private static String getDataFilename(String testName, String suffix, int dataEntryNumber)
	{
		String folderPath = "tests/" + testName + " Data/";
		File folder = new File(folderPath);
		folder.mkdir();
		String name = new String();
		if (suffix == "")
		{
			name = dataEntryNumber + " - " + getDateFilename();
		}
		else
		{
			name = dataEntryNumber + "_" + suffix + " - " + getDateFilename();
		}
		return new String("tests/" + testName + " Data/" + name + ".bin");
	}
}

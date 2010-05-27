/*
 * This file is part of the AusStage ABS Data Fix App
 *
 * The AusStage ABS Data Fix App is free software: you can 
 * redistribute it and/or modify it under the terms of the GNU General 
 * Public License as published by the Free Software Foundation, either 
 * version 3 of the License, or (at your option) any later version.
 *
 * The AusStage ABS Data Fix App is distributed in the hope 
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the 
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the AusStage ABS Data Fix App.  
 * If not, see <http://www.gnu.org/licenses/>.
*/

import java.io.*;
import java.awt.Color;

/**
 * An abstract class used to store common methods & provide the basis for an API
 * for all of the different task classes
 */
public abstract class Tasks {

	// declare private variables
	File input;
	File output;
	File codes;
	
	// declare public constants
	public static final String ROOT_SNIPPET_ELEMENT_NAME = "ABSDataSnippet";
	public static final String DISTRICT_ELEMENT_NAME     = "district";
	public static final String ROOT_ELEMENT_NAME         = "ABSData";

	/**
	 * Constructor for this class
	 *
	 * @param input  the input file
	 * @param output the output file
	 */
	public Tasks(File input, File output) {
	
		// check on the parameters
		if(input == null || output == null) {
			throw new IllegalArgumentException("ERROR: This constructor requires valid file object as parameters");
		} else {
			this.input  = input;
			this.output = output;
		}
		
	} // end constructor
	
	/**
	 * Constructor for this class
	 *
	 * @param input  the input file
	 * @param output the output file
	 * @param codes  the file with the CD code list
	 */
	public Tasks(File input, File output, File codes) {
	
		// check on the parameters
		if(input == null || output == null || codes == null) {
			throw new IllegalArgumentException("ERROR: This constructor requires valid file object as parameters");
		} else {
			this.input  = input;
			this.output = output;
			this.codes  = codes;
		}
		
	} // end constructor
	
	/**
	 * A method used to undertake the required task
	 *
	 * @return true if, and only if, the task completes successfully
	 */
	public abstract boolean doTask();
	
	/**
	 * A method to filter a peice of string based input data
	 *
	 * @param value       the value to filter
	 * @param nullAllowed if true a null value is allowed
	 *
	 * @return            the filtered value
	 */
	public String filterString(String value, boolean nullAllowed) {
		
		// check for nulls	
		if(nullAllowed == false && value == null) {
			throw new IllegalArgumentException("Value cannot be null");
		}
		
		// trim the string
		value = value.trim();
		
		// check for nulls again
		if(nullAllowed == false && value.equals("")) {
			throw new IllegalArgumentException("Value cannot be empty");
		}
	
		// return the filtered value
		return value;	
	
	} // end filterString method
	
	/**
	 * A method to filter a peice of string based input data
	 * by default null values are not allowed
	 *
	 * @param value       the value to filter
	 *
	 * @return            the filtered value
	 */
	public String filterString(String value) {
		
		// return the filtered value
		return filterString(value, false);	
	
	} // end filterString method
	
	/**
	 * A method to filter a peice of string based input data
	 * and ensure it represents an integer
	 *
	 * @param value the value to filter
	 */
	public void filterInteger(String value) {
	
		try {
			Integer.getInteger(value);
		} catch(NumberFormatException ex) {
			throw new IllegalArgumentException("Value must represent an integer");
		}
	}
	
	/**
	 * A method to take a HTML colour representation and 
	 * build a Java Color object
	 *
	 * @param value the colour value in HTML notation
	 *
	 * @return      the newly constructed color object
	 */
	public Color createColorObject(String value) {
	
		value = filterString(value);
		
		if(value.length() != 6) {
			throw new IllegalArgumentException("The HTML colour notation must be six characters long");
		}
		
		// deconstruct the HTML colour into its component parts		
		int red   = Integer.parseInt(value.substring(0, 2), 16);
		int green = Integer.parseInt(value.substring(2, 4), 16);
		int blue  = Integer.parseInt(value.substring(4, 6), 16);
		
		// build a new color object		
		return new Color(red, green, blue);
	}
	
	/** 
	 * A method to take a Java Color object and return
	 * the HTML representation
	 *
	 * @param color  the color object
	 * @param kml    if set to true return the colour in KML notation
	 *
	 * @return       the colour in HTML notation and optionally ordered for use in KML
	 */
	public String colorObjectToHTML(Color color, boolean kml) {
	
		if(color == null) {
			throw new IllegalArgumentException("The color object can not be null");
		}
		
		// deconstruct the color
		String red   = Integer.toHexString(color.getRed());
		String green = Integer.toHexString(color.getGreen());
		String blue  = Integer.toHexString(color.getBlue());
		
		String colour = null;
		
		// double check the values
		if(red.length() == 1) {
			red = "0" + red;
		}
		
		if(blue.length() == 1) {
			blue = "0" + blue;
		}
		
		if(green.length() == 1) {
			green = "0" + green;
		}
		
		if(kml == true) {
			colour = blue + green + red;
		} else {
			colour = red + green + blue;
		}
		
		return colour.toUpperCase();
	}
	
	/**
	* Make a colour darker.
	* 
	* @param color     Color to make darker.
	* @param fraction  Darkness fraction.
	* @return          Darker color.
	*/
	public Color darkerColour (Color colour, double fraction)
	{
		int red   = (int) Math.round (colour.getRed()   * (1.0 - fraction));
		int green = (int) Math.round (colour.getGreen() * (1.0 - fraction));
		int blue  = (int) Math.round (colour.getBlue()  * (1.0 - fraction));

		if (red < 0) {
			red = 0;
		} else if (red   > 255) {
			red = 255;
		}
	
		if (green < 0) {
			green = 0; 
		} else if (green > 255) {
			green = 255;
		}
	
		if (blue  < 0) {
			blue = 0; 
		} else if (blue  > 255) {
			blue  = 255;
		}

		return new Color (red, green, blue);
	}
	
	
} // end class definition

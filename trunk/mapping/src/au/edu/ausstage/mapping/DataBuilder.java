/*
 * This file is part of the AusStage Mapping Service
 *
 * The AusStage Mapping Service is free software: you can redistribute
 * it and/or modify it under the terms of the GNU General Public License 
 * as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * The AusStage Mapping Service is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the AusStage Mapping Service.  
 * If not, see <http://www.gnu.org/licenses/>.
*/
 
package au.edu.ausstage.mapping;

// import additional libraries
import java.util.GregorianCalendar;
import java.text.DateFormat;

/**
 * An abstract class used to define common methods and attributes
 * of those objects used to build datasets
 */

public abstract class DataBuilder {

	// declare common data members
	protected DataManager dataManager;

	/**
	 * Constructor for the DataBuilder object
	 *
	 * @param manager the DataManager object used to access the database
	 */
	public DataBuilder(DataManager manager) {
		this.dataManager = manager;
	}
	
	/**
	 * A method to get XML used to add markers to a Google Map
	 *
	 * @return  the marker XML as a string
	 */
	public abstract String getMarkerXMLString() throws javax.servlet.ServletException, java.lang.NoSuchMethodException;
	
	/**
	 * A abstract method used to get the String representation of the Marker XML
	 *
	 * @param queryParameter the parameter to determine what is of interest
	 *
	 * @return               the string representation of the Marker XML
	 */
	public abstract String getMarkerXMLString(String queryParameter) throws javax.servlet.ServletException, java.lang.NoSuchMethodException;
	
	/**
	 * A method used to get the the Marker XML for an organisation restricted to a date range
	 * using the first date fields in the database
	 *
	 * @param queryParameter the parameter to determine which organisation is of interest
	 * @param startDate      the start date of the date range limit
	 * @param finishDate     the finish date of the date range limit
	 *
	 * @return               the string representation of the Marker XML
	 */
	public abstract String getMarkerXMLString(String queryParameter, String startDate, String finishDate) throws javax.servlet.ServletException, java.lang.NoSuchMethodException;
	
	/**
	 * A abstract method used to get the String representation of the KML document
	 * using the default options
	 *
	 * @return               a string containing the KML XML
	 */
	public abstract String getKMLString() throws javax.servlet.ServletException, java.lang.NoSuchMethodException;
	
	/**
	 * A abstract method used to get the String representation of the KML document
	 * using the default options
	 *
	 * @param queryParameter the parameter to determine what is of interest
	 *
	 * @return               a string containing the KML XML
	 */
	public abstract String getKMLString(String queryParameter) throws javax.servlet.ServletException, java.lang.NoSuchMethodException;
	
	/**
	 * A abstract method used to export data in the KML format
	 *
	 * @param queryParameter the parameter used to dertmine what is of interest
	 * @param exportOptions  the options used to control this export
	 *
	 * @return               a string containing the KML XML
	 */
	public abstract String doKMLExport(String queryParameter, KMLExportOptions exportOptions) throws javax.servlet.ServletException, java.lang.NoSuchMethodException;
	 
	/**
	 * A method used to build a date for use Marker XML and KML data
	 *
	 * @param year  the year component of the date
	 * @param month the month component of the date
	 * @param day   the day component of the month
	 *
	 * @return      a string containing the finalised date
	 */
	public String buildDate(String year, String month, String day) {
	
		// check for at least a year
		if(year != null) {
		 
			String date = year + "-" + month + "-" + day;
		 	date = date.replace("-null","");
			date = date.replace("null","");
			
			return date;
		} else {
			return "";
		}
	 
	} // end buildDate method
	 
	/**
	 * A method used to build a date for display in line with existing formatting rules
	 *
	 * @param year  the year component of the date
	 * @param month the month component of the date
	 * @param day   the day component of the month
	 *
	 * @return      a string containing the finalised date
	 */
	public String buildDisplayDate(String year, String month, String day) {
	
		if(year != null) {
		 
		 	String date = day + " " + this.lookupMonth(month) + " " + year;
		 	date = date.replace("null","");
		 	date = date.trim();
			return date;
		} else {
			return "";
		}
	 
	} // end buildDisplayDate method
	 
	/**
	 * A method used to lookup the name of a month based on its number
	 *
	 * @param month the month as a digit
	 *
	 * @return      a string containing the name of the month
	 */
	private String lookupMonth(String month) {
	 
		// check on the month parameter
	 	if(month == null || month.equals("")) {
	 		return "";
	 	}
	 
	 	// prepare the month
	 	month = month.trim();
	 	
	 	// double check the month parameter
	 	if(month == null || month.equals("")) {
	 		return "";
	 	}
	 
	 	// convert the string to an int
	 	int i = Integer.parseInt(month);
	 	
	 	switch (i) {
	 		case 1:  return "January";
	 		case 2:  return "February";
	 		case 3:  return "March";
	 		case 4:  return "April";
	 		case 5:  return "May";
	 		case 6:  return "June";
	 		case 7:  return "July";
	 		case 8:  return "August";
	 		case 9:  return "September";
	 		case 10: return "October";
	 		case 11: return "November";
	 		case 12: return "December";
	 		default: return "";
	 		}
	} // end lookupMonth method
	 
	/**
	 * A method to get the current date and time to use in a timestamp
	 *
	 * @return  a string containing the timestamp
	 */
	public String getCurrentDateAndTime() {
	 
	 	GregorianCalendar calendar = new GregorianCalendar();
	 	DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL);
	 	
		return formatter.format(calendar.getTime());
	 
	} // end getCurrentDateAndTime method
	
	/**
	 * A method to get the current date to use in a timestamp
	 *
	 * @return  a string containing the timestamp
	 */
	public String getCurrentDate() {
	 
	 	GregorianCalendar calendar = new GregorianCalendar();
	 	DateFormat formatter = DateFormat.getDateInstance(DateFormat.FULL);
	 	
		return formatter.format(calendar.getTime());
	 
	} // end getCurrentDate method
	 
	/**
	 * A method to build a sanitised file name
	 *
	 * @param name the string to sanitise and use as the name
	 * @param ext  the string to use as the extension
	 *
	 * @return     the sanitised file name
	 */
	public String getFileName(String name, String ext) {
	 
	 	String fileName = name.toLowerCase();					// lower case
		fileName = fileName.replaceAll("[^a-zA-Z0-9\\s]", "");  // remove anything not alphanumeric
		fileName = fileName.replaceAll(" ", "-");			    // replace spaces with dashes
		fileName += "." + ext;
		
		return fileName;
	} // end getFileName method
	
	/**
	 * A method to get the last day of a month
	 *
	 * @param year  the four digit year
	 * @param month the two digit month
	 *
	 * @return      the last day of the specified month
	 */
	public String getLastDay(String year, String month) {
	
		// get a calendar object
		GregorianCalendar calendar = new GregorianCalendar();
		
		// convert the year and month to integers
		int yearInt = Integer.parseInt(year);
		int monthInt = Integer.parseInt(month);
		
		// adjust the month for a zero based index
		monthInt = monthInt - 1;
		
		// set the date of the calendar to the date provided
		calendar.set(yearInt, monthInt, 1);
		
		int dayInt = calendar.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);
		
		return Integer.toString(dayInt);
	} // end getLastDay method
	
	/**
	 * A method to explode a date in 8 digit format into the three components.
	 * Where a date component can not be determined null is used
	 * Array elements are [0] - year, [1] - month, [2] - day
	 *
	 * @param date the date to explode
	 *
	 * @return     an array with three elements containing the date components
	 */
	public String[] getExplodedDate(String date) {
	
		// declare helper variables
		String[] explodedDate = new String[3];
		
		// set initial values
		explodedDate[0] = null;
		explodedDate[1] = null;
		explodedDate[2] = null;
		
		if(date == null) {
			return explodedDate;
		}
		
		// explode the date
		if(date.length() == 4) {
			explodedDate[0] = date;
		} else if(date.length() == 6) {
			explodedDate[0] = date.substring(0, 4);
			explodedDate[1] = date.substring(4, 6);
		} else if(date.length() == 7) {
			explodedDate[0] = date.substring(0, 4);
		} else if(date.length() == 8) {
			explodedDate[0] = date.substring(0, 4);
			explodedDate[1] = date.substring(4, 6);
			explodedDate[2] = date.substring(6, 8);
		}
	
		// return the exploded Date
		return explodedDate;
	
	} // end getExplodedDate method

} // end class definition

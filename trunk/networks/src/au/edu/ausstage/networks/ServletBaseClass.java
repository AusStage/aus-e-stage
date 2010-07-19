/*
 * This file is part of the AusStage Navigating Networks Service
 *
 * The AusStage Navigating Networks Service is free software: you can redistribute
 * it and/or modify it under the terms of the GNU General Public License 
 * as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * The AusStage Navigating Networks Service is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the AusStage Mapping Service.  
 * If not, see <http://www.gnu.org/licenses/>.
*/

package au.edu.ausstage.networks;

// import additional classes
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

/**
 * A class to respond to requests to lookup data
 */
public abstract class ServletBaseClass extends HttpServlet {

	/**
	 * Initialise the servlet
	 */
	
	public void init(ServletConfig conf) throws ServletException {
	
		// execute the parent objects init method
		super.init(conf);
		
	} // end init method
	
	/**
	 * check to ensure a parameter value is valid
	 *
	 * @param value the parameter value
	 *
	 * @return      true if, and only if, the parameter is valid
	 */
	public boolean isValid(String value) {
	
		// check on the parameter value
		if(value == null) {
			return false;
		} else {
			return true;
		}
	} // end the isValid method
	
	/**
	 * check to ensure a parameter is valid and is from a list
	 *
	 * @param value the parameter value
	 * @param list  the list of allowed values
	 *
	 * @return      true if, and only if, the parameter is valid
	 */
	public boolean isValid(String value, String[] list) {
	
		// check if it is not null first
		if(isValid(value) == false) {
			return false;
		}
		
		// check the value against the list
		boolean isValid = false;
		
		for(int i = 0; i < list.length; i++) {
			if(list[i].equals(value)) {
				isValid = true;
			}
		}
		
		return isValid;	
	} // end the isValid method with list
	
	/**
	 * check to ensure a parameter value is valid
	 *
	 * @param value the parameter value
	 *
	 * @return      true if, and only if, the parameter is valid
	 */
	public boolean isValidInt(String value) {
	
		if(isValid(value) == false) {
			return false;
		}
		
		
		// can we parse the value as a int?
		try {
			Integer.parseInt(value);
		} catch (java.lang.NumberFormatException ex) {
			// nope
			return false;
		}
		
		// if we get this far everything is OK
		return true;
	
	} // end the isValid method
	
//	/**
//	 * Method to respond to a get request
//	 *
//	 * @param request a HttpServletRequest object representing the current request
//	 * @param response a HttpServletResponse object representing the current response
//	 */
//	public abstract void doGet (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;
//	
//	/**
//	 * Method to respond to a post request
//	 *
//	 * @param request a HttpServletRequest object representing the current request
//	 * @param response a HttpServletResponse object representing the current response
//	 */
//	public abstract void doPost (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;

} // end class definition

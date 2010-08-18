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
 * along with the AusStage Navigating Networks Service.  
 * If not, see <http://www.gnu.org/licenses/>.
*/

package au.edu.ausstage.networks;

// import additional classes
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

import au.edu.ausstage.utils.InputUtils;

/**
 * A class to respond to requests to search for data
 */
public class SearchServlet extends HttpServlet {

	// declare private variables
	private ServletConfig servletConfig;
	private DataManager database;
	
	// declare private constants
	private final String[] TASK_TYPES        = {"key-collaborators", "system-property", "collaborator"};
	private final String[] FORMAT_TYPES      = {"html", "xml", "json"};
	private final String[] SORT_TYPES        = {"count", "id", "name"};
	private final String[] PROPERTY_ID_TYPES = {"datastore-create-date", "export-options"};

	/*
	 * initialise this instance
	 */
	public void init(ServletConfig conf) throws ServletException {
		// execute the parent objects init method
		super.init(conf);
		
		// store configuration for later
		servletConfig = conf;
		
		// instantiate a database manager object
		database = new DataManager(conf);
		
	} // end init method
	
	/**
	 * Method to respond to a get request
	 *
	 * @param request a HttpServletRequest object representing the current request
	 * @param response a HttpServletResponse object representing the current response
	 */
	public void doGet (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		
	
	} // end doGet method
	
	/**
	 * Method to respond to a post request
	 *
	 * @param request a HttpServletRequest object representing the current request
	 * @param response a HttpServletResponse object representing the current response
	 */
	public void doPost (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		// don't respond to post requests
		throw new ServletException("Invalid Request Type");
	
	} // end doPost method

} // end class definition

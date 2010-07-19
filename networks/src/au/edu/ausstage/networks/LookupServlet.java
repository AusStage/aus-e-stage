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
public class LookupServlet extends ServletBaseClass {

	// declare private variables
	private ServletConfig servletConfig;
	private DataManager database;
	
	// declare private constants
	private static final String[] TASK_TYPES   = {"key-collaborators", "key-organisations"};
	private static final String[] FORMAT_TYPES = {"html", "xml", "json"};
	private static final String[] SORT_TYPES   = {"count", "id", "name"};

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
	
		// get the parameters
		String taskType   = request.getParameter("task");
		String id         = request.getParameter("id");
		String formatType = request.getParameter("format");
		String sortType   = request.getParameter("sort");
		
		// check on the taskType parameter
		if(isValid(taskType, TASK_TYPES) == false) {
			// no valid task type was found
			throw new ServletException("Missing task type parameter. Expected: " + java.util.Arrays.toString(TASK_TYPES).replaceAll("[\\]\\[]", ""));
		}

		// check on the id parameter
		if(isValidInt(id) == false) {
			throw new ServletException("Missing or invalid id parameter.");
		}

		// check the format parameter
		if(isValid(formatType) == false) {
			// use default value
			formatType = "html";
		} else {
			if(isValid(formatType, FORMAT_TYPES) == false) {
				throw new ServletException("Missing format type. Expected: " + java.util.Arrays.toString(FORMAT_TYPES).replaceAll("[\\]\\[]", ""));
			}
		}

		// check the sort parameter
		if(isValid(sortType) == false) {
			// use default value
			sortType = "count";
		} else {
			if(isValid(sortType, SORT_TYPES) == false) {
				throw new ServletException("Missing sort type. Expected: " + java.util.Arrays.toString(SORT_TYPES).replaceAll("[\\]\\[]", ""));
			}
		}
		
		// instantiate a lookup object
		LookupManager lookup = new LookupManager(database);
		
		String results = null;
		
		// determine the type of lookup to undertake
		if(taskType.equals("key-collaborators") == true) {
			results = lookup.getKeyCollaborators(id, formatType, sortType);
		}
		
		// output the appropriate mime type
		if(formatType == "html") {
			// output html mime type
			response.setContentType("text/html; charset=UTF-8");
		} else if(formatType == "xml") {
			// output xml mime type
			response.setContentType("text/xml; charset=UTF-8");
		} else if(formatType == "json") {
			// output json mime type
			response.setContentType("application/json; charset=UTF-8");
		}
		
		//debug code
		response.setContentType("text/plain; charset=UTF-8");
		
		// output the results of the lookup
		PrintWriter out = response.getWriter();
		out.print(results);
	
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

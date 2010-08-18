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
	private final String[] TASK_TYPES        = {"collaborator"};
	private final String[] FORMAT_TYPES      = {"html", "xml", "json"};
	private final String[] SORT_TYPES        = {"id", "name"};
	private final int      DEFAULT_LIMIT     = 5;
	private final int      MIN_LIMIT         = 5;
	private final int      MAX_LIMIT         = 25;

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
		String query      = request.getParameter("query");
		String formatType = request.getParameter("format");
		String sortType   = request.getParameter("sort");
		int    limit      = 0;
		
		// check on the taskType parameter
		if(InputUtils.isValid(taskType, TASK_TYPES) == false) {
			// no valid task type was found
			throw new ServletException("Missing task parameter. Expected one of: " + java.util.Arrays.toString(TASK_TYPES).replaceAll("[\\]\\[]", ""));
		}

		// check on the id parameter
		if(InputUtils.isValid(query) == false) {
			throw new ServletException("Missing or invalid query parameter.");
		}

		// check the format parameter
		if(InputUtils.isValid(formatType) == false) {
			// use default value
			formatType = "json";
		} else {
			if(InputUtils.isValid(formatType, FORMAT_TYPES) == false) {
				throw new ServletException("Missing format type. Expected: " + java.util.Arrays.toString(FORMAT_TYPES).replaceAll("[\\]\\[]", ""));
			}
		}
		
		// check the sort parameter
		if(InputUtils.isValid(sortType) == false) {
			// use default value
			sortType = "id";
		} else {
			if(InputUtils.isValid(sortType, SORT_TYPES) == false) {
				throw new ServletException("Missing sort type. Expected: " + java.util.Arrays.toString(SORT_TYPES).replaceAll("[\\]\\[]", ""));
			}
		}
		
		// check the limit parameter
		if(request.getParameter("limit") != null) {
			try {
				// get the parameter and convert to an integer
				limit = Integer.parseInt(request.getParameter("limit"));	
			} catch (NumberFormatException ex) {
				// degrees must be a number
				throw new ServletException("Limit parameter must be an integer");
			}
									
			// double check the parameter
			if(InputUtils.isValidInt(limit, MIN_LIMIT, MAX_LIMIT) == false) {
				throw new ServletException("Limit parameter must be between '" + MIN_LIMIT + "' and '" + MAX_LIMIT + "'");
			}
		} else {
			limit = DEFAULT_LIMIT;
		}
		
		// instantiate a lookup object
		SearchManager search = new SearchManager(database);
		
		String results = null;
		
		// determine what type of search task to undertake
		if(taskType.equals("collaborator") == true) {
			results = search.doCollaboratorSearch(query, formatType, sortType, limit);
		}
		
		// output the appropriate mime type
		if(formatType.equals("html") == true) {
			// output html mime type
			response.setContentType("text/html; charset=UTF-8");
		} else if(formatType.equals("xml") == true) {
			// output xml mime type
			response.setContentType("text/xml; charset=UTF-8");
		} else if(formatType.equals("json") == true) {
			// output json mime type
			response.setContentType("application/json; charset=UTF-8");
		}
		
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

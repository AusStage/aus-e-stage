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

// import additional AusStage libraries
import au.edu.ausstage.utils.*;

// import other packages
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
 
public class SearchServlet extends HttpServlet {

	// declare private class variables
	private ServletConfig servletConfig;
	
	// declare private class constants
	private final String[] TASK_TYPES    = {"organisation", "contributor", "venue", "event"};
	private final String[] SEARCH_TYPES  = {"name", "id"};
	private final String[] FORMAT_TYPES  = {"json"};
	private final String[] SORT_TYPES    = {"name", "id"};
	public static final int      DEFAULT_LIMIT = 5;
	public static final int      MIN_LIMIT     = 5;
	public static final int      MAX_LIMIT     = 25;
	public static final int      MIN_QUERY_LENGTH = 4;
	
	/*
	 * initialise this instance
	 */
	public void init(ServletConfig conf) throws ServletException {
		// execute the parent objects init method
		super.init(conf);
		
		// store configuration for later
		servletConfig = conf;
		
	} // end init method
	
	/**
	 * Method to respond to a get request
	 *
	 * @param request a HttpServletRequest object representing the current request
	 * @param response a HttpServletResponse object representing the current response
	 */
	public void doGet (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		// get the parameters
		// get the parameters
		String taskType   = request.getParameter("task");
		String searchType = request.getParameter("type");
		String query      = request.getParameter("query");
		String formatType = request.getParameter("format");
		String sortType   = request.getParameter("sort");
		int    limit      = 0;
		
		// check on the taskType parameter
		if(InputUtils.isValid(taskType, TASK_TYPES) == false) {
			// no valid task type was found
			throw new ServletException("Missing task parameter. Expected one of: " + InputUtils.arrayToString(TASK_TYPES));
		}
		
		// check on the searchType parameter
		if(InputUtils.isValid(searchType, SEARCH_TYPES) == false) {
			// no valid task type was found
			throw new ServletException("Missing type parameter. Expected one of: " + InputUtils.arrayToString(SEARCH_TYPES));
		}

		// check on the id parameter
		if(searchType.equals("id") != true) {
			if(InputUtils.isValid(query) == false) {
				throw new ServletException("Missing or invalid query parameter.");
			} else if(query.length() < MIN_QUERY_LENGTH) {
				throw new ServletException("The query parameter must be at least " + MIN_QUERY_LENGTH + " characters long");
			}
		}
		
		if(searchType.equals("id") == true) {
			try {
				Integer.parseInt(query);
			} catch (NumberFormatException ex) {
				// limit must be a number
				throw new ServletException("query parameter must be an integer");
			}
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
			sortType = "name";
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
				// limit must be a number
				throw new ServletException("Limit parameter must be an integer");
			}
									
			// double check the parameter
			if(InputUtils.isValidInt(limit, MIN_LIMIT, MAX_LIMIT) == false) {
				throw new ServletException("Limit parameter must be between '" + MIN_LIMIT + "' and '" + MAX_LIMIT + "'");
			}
		} else {
			limit = DEFAULT_LIMIT;
		}
		
		// instantiate a connection to the database
		DbManager database;
		
		try {
			database = new DbManager(servletConfig.getServletContext().getInitParameter("databaseConnectionString"));
		} catch (IllegalArgumentException ex) {
			throw new ServletException("Unable to read the connection string parameter from the web.xml file");
		}
		
		if(database.connect() == false) {
			throw new ServletException("Unable to connect to the database");
		}
		
		// declare helper variables
		String data = null;
		SearchManager manager = new SearchManager(database);
		
		// determine what sort of search to do
		if(taskType.equals("organisation") == true) {
			data = manager.doOrganisationSearch(searchType, query, formatType, sortType, limit);
		} else if(taskType.equals("contributor") == true) {
			data = manager.doContributorSearch(searchType, query, formatType, sortType, limit);
		} else if(taskType.equals("venue") == true) {
			data = manager.doVenueSearch(searchType, query, formatType, sortType, limit);
		} else if(taskType.equals("event") == true) {
			data = manager.doEventSearch(searchType, query, formatType, sortType, limit);
		}
		
		
		// check to see if this is a jsonp request
		if(InputUtils.isValid(request.getParameter("callback")) == false) {
			// output json mime type
			response.setContentType("application/json; charset=UTF-8");
		} else {
			// output the javascript mime type
			response.setContentType("application/javascript; charset=UTF-8");
		}
		
		// output the results of the search
		PrintWriter out = response.getWriter();
		if(InputUtils.isValid(request.getParameter("callback")) == true) {
			out.print(JSONPManager.wrapJSON(data, request.getParameter("callback")));
		} else {
			out.print(data);
		}
	
	} // end doGet method
	
	/**
	 * Method to respond to a post request
	 *
	 * @param request a HttpServletRequest object representing the current request
	 * @param response a HttpServletResponse object representing the current response
	 */
	public void doPost (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		// error for the time being as this isn't a valid request method at present
		throw new ServletException("Invalid Request Type");
	
	} // end doPost method


} // end class definition

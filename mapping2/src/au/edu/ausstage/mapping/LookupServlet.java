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
 
/**
 * A class to respond to requests to lookup data
 */
public class LookupServlet extends HttpServlet {

	// declare private variables
	private ServletConfig servletConfig;
	private DbManager database;
	
	// declare private constants
	private final String[] TASK_TYPES         = {"state-list", "suburb-list", "suburb-venue-list", "organisation", "contributor"};
	private final String[] FORMAT_TYPES       = {"json"};
	public static final String[] VALID_STATES = {"1", "2", "3", "4", "5", "6", "7", "8"};

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
		String taskType       = request.getParameter("task");
		String id             = request.getParameter("id");
		String formatType     = request.getParameter("format");

		// check on the taskType parameter
		if(InputUtils.isValid(taskType, TASK_TYPES) == false) {
			// no valid task type was found
			throw new ServletException("Missing task parameter. Expected one of: " + InputUtils.arrayToString(TASK_TYPES));
		}
		
		// check on the other parameters
		if(taskType.equals("state-list") == false) {
			// this is a lookup task that requires the id
			if(taskType.equals("suburb-list") == true) {
				if(InputUtils.isValid(id, VALID_STATES) == false) {
					throw new ServletException("Missing id parameter. Expected one of: " + InputUtils.arrayToString(VALID_STATES));
				}
			} else if(taskType.equals("suburb-venue-list") == true) {
				if(InputUtils.isValid(id) == false) {
					throw new ServletException("The id parameter is required");
				} else if(id.indexOf("_") == -1) {
					throw new ServletException("The id parameter is required to have a state code followed by a suburb name seperated by a \"_\" character");
				} else {
					String[] tmp = id.split("_");
					if(tmp.length > 2) {
						throw new ServletException("The id parameter is required to have a state code followed by a suburb name seperated by a \"_\" character");
					} else {
						if(InputUtils.isValid(tmp[0], VALID_STATES) == false) {
							throw new ServletException("Invalid state code. Expected one of: " + InputUtils.arrayToString(VALID_STATES));
						}
					}
				}
			} else if(InputUtils.isValid(id) == false) {
				throw new ServletException("The id parameter is required");
			}
		} else {
			id = null;
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
		
		// instantiate a lookup object
		LookupManager lookup = new LookupManager(database);
		
		String results = null;
		
		// determine what to lookup
		if(taskType.equals("state-list") == true) {
			// lookup the list of states
			results = lookup.getStateList();
		} else if(taskType.equals("suburb-list") == true) {
			// lookup the list of available suburbs in selected state
			results = lookup.getSuburbList(id);
		} else if(taskType.equals("suburb-venue-list") == true) {
			// lookup a list of venues in a specific state
			results = lookup.getVenueListBySuburb(id);
		} else if(taskType.equals("organisation") == true) {
			// lookup a list of venues in a specific state
			results = lookup.getOrganisation(id);
		} else if(taskType.equals("contributor") == true) {
			// lookup a list of venues in a specific state
			results = lookup.getContributor(id);
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
			out.print(JSONPManager.wrapJSON(results, request.getParameter("callback")));
		} else {
			out.print(results);
		}
	
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

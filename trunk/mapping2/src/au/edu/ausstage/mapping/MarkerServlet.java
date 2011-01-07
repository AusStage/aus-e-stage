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
 
public class MarkerServlet extends HttpServlet {

	// declare private class variables
	private ServletConfig servletConfig;
	
	// declare private class constants
	private final String[] MARKER_TYPES = {"state", "suburb", "venue", "organisation", "contributor", "event"};
	public static final String[] VALID_STATES = {"1", "2", "3", "4", "5", "6", "7", "8", "99", "999"};

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
	
		// get the request parameters
		String type = request.getParameter("type");
		String id   = request.getParameter("id");
		
		// check on the marker types
		if(InputUtils.isValid(type, MARKER_TYPES) == false) {
			// no valid marker type was found
			throw new ServletException("Missing type parameter. Expected one of: " + InputUtils.arrayToString(MARKER_TYPES));
		}
		
		// check on the id value
		if(type.equals("state") == true) {
			if(InputUtils.isValid(id, VALID_STATES) == false) {
				// no valid state id was found
				throw new ServletException("Missing id parameter. Expected one of: " + InputUtils.arrayToString(VALID_STATES));
			}
		} else if(type.equals("suburb") == true) {
			if(id.indexOf("_") == -1) {
				throw new ServletException("The id parameter is required to have a state code followed by a suburb name seperated by a \"_\" character");
			} else {
				String[] tmp = id.split("_");
				if(tmp.length > 2) {
					throw new ServletException("The id parameter is required to have a state code followed by a suburb name seperated by a \"_\" character");
				} else {
					if(InputUtils.isValid(tmp[0], LookupServlet.VALID_STATES) == false) {
						throw new ServletException("Invalid state code. Expected one of: " + InputUtils.arrayToString(LookupServlet.VALID_STATES));
					}
				}
			}
		} else if(InputUtils.isValid(id) == true) {
			if(id.indexOf(',') == -1) {
				// a single id
				if(InputUtils.isValidInt(id) == false) {
					throw new ServletException("The id parameter must be a valid integer");
				}
			} else {
				// multiple ids
				String[] ids = id.split(",");
			
				if(InputUtils.isValidArrayInt(ids) == false) {
					throw new ServletException("The id parameter must contain a list of valid integers seperated by commas only");
				}
			}
		} else {
			throw new ServletException("Missing id parameter.");
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
		String results = null;
		MarkerManager manager = new MarkerManager(database);
		
		//determine what type or markers to get
		if(type.equals("state") == true) {
			results = manager.getStateMarkers(id);
		} else if(type.equals("suburb") == true) {
			results = manager.getSuburbMarkers(id);
		} else if(type.equals("venue") == true) {
			results = manager.getVenueMarkers(id);
		} else if(type.equals("organisation") == true) {
			results = manager.getOrganisationMarkers(id);
		} else if(type.equals("contributor") == true) {
			results = manager.getContributorMarkers(id);
		} else if(type.equals("event") == true) {
			results = manager.getEventMarkers(id);
		}
		
		// ouput the data
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
	
		// error for the time being as this isn't a valid request method at present
		throw new ServletException("Invalid Request Type");
	
	} // end doPost method


} // end class definition

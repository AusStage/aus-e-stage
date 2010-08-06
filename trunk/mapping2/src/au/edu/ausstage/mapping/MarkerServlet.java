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
	private final String[] MARKER_TYPES = {"organisation", "contributor"};

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
			throw new ServletException("Missing type parameter. Expected one of: " + java.util.Arrays.toString(MARKER_TYPES).replaceAll("[\\]\\[]", ""));
		}
		
		// check on the id
		if(InputUtils.isValidInt(id) == false) {
			// no valid marker type was found
			throw new ServletException("Missing id parameter. Parameter must be a valid integer");
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
		MarkerManager manager = new MarkerManager(database);
		
		// determine what type of markers to get
		if(type.equals(MARKER_TYPES[0]) == true) {
			// get markers for an organisation
			data = manager.getOrganisationMarkers(id);
		} else {
			// get markers for a contributor
			data = manager.getContributorMarkers(id);
		}
		
		// check on the data to return
		if(data == null) {
			throw new ServletException("An error occured during the compilation of the marker data. Contact the system administrator if this continues");
		}
		
		// ouput the data
		// set the appropriate content type
		response.setContentType("text/xml; charset=UTF-8");
		
		// set the appropriate headers to disable caching
		// particularly for IE
		response.setHeader("Cache-Control", "max-age=0,no-cache,no-store,post-check=0,pre-check=0");
		
		//get the output print writer
		PrintWriter out = response.getWriter();
		
		// send some output
		out.print(data);
	
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

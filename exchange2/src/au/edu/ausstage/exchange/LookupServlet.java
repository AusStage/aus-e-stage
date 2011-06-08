/*
 * This file is part of the AusStage Data Exchange Service
 *
 * AusStage Data Exchange Service is free software: you can redistribute
 * it and/or modify it under the terms of the GNU General Public License 
 * as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * AusStage Data Exchange Service is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with AusStage Data Exchange Service.  
 * If not, see <http://www.gnu.org/licenses/>.
*/

package au.edu.ausstage.exchange;

// import additional classes
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

// import additional AusStage libraries
import au.edu.ausstage.utils.*;

/**
 * The main driving class for the lookup of information for the Data Exchange service
 */
public class LookupServlet extends HttpServlet {

	// declare private class variables
	private ServletConfig servletConfig;
	
	// declare public constants
	public static final String[] VALID_TASK_TYPES = {"secgenre","contentindicator", "ressubtype"};

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
		String task = request.getParameter("task");
		
		// validate the request parameters
		if(InputUtils.isValid(task, VALID_TASK_TYPES) == false) {
			// no valid type was found
			throw new ServletException("Missing task parameter. Expected one of: " + InputUtils.arrayToString(VALID_TASK_TYPES));
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
		
		LookupManager lookup = new LookupManager(database);
		
		if(task.equals("secgenre") == true) {
			results = lookup.getSecondaryGenreIdentifiers();
		} else if(task.equals("contentindicator") == true) {
			results = lookup.getContentIndicatorIdentifiers();
		} else if(task.equals("ressubtype") == true) {
			results = lookup.getResourceSubTypeIdentifiers();
		}
		
		// ouput the data
		PrintWriter out = response.getWriter();
		
		if(InputUtils.isValid(request.getParameter("callback")) == false) {
			response.setContentType("application/json; charset=UTF-8");
			out.print(results);
		} else {
			// output the javascript mime type
			response.setContentType("application/javascript; charset=UTF-8");
			out.print(JSONPManager.wrapJSON(results, request.getParameter("callback")));
		}	
	}
	
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



}

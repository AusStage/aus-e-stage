/*
 * This file is part of the AusStage Mobile Service
 *
 * The AusStage Mobile Service is free software: you can redistribute
 * it and/or modify it under the terms of the GNU General Public License 
 * as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * The AusStage Mobile Service is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the AusStage Mobile Service.  
 * If not, see <http://www.gnu.org/licenses/>.
 */
 
package au.edu.ausstage.mobile;

// import additional AusStage libraries
import au.edu.ausstage.utils.*;

// import other packages
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
 
public class FeedbackServlet extends HttpServlet {

	// declare private class variables
	private ServletConfig servletConfig;
	private DbManager database;
	
	// declare private class constants
	private final String[] TASK_TYPES = {"initial", "update"};

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
		String performance = request.getParameter("performance");
		String task        = request.getParameter("task");
		String lastId      = request.getParameter("lastid");
		
		// double check the parameters
		if(InputUtils.isValidInt(performance) == false) {
			// no valid performance id
			throw new ServletException("Missing performance parameter. Expected a valid integer");
		}
		
		if(InputUtils.isValid(task, TASK_TYPES) == false) {
			// no valid task type was found
			throw new ServletException("Missing task parameter. Expected one of: " + InputUtils.arrayToString(TASK_TYPES));
		}
		
		if(task.equals("update") == true) {
			if(InputUtils.isValidInt(lastId) == false) {
				throw new ServletException("Missing lastid parameter. Expected a valid integer");
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
		
		// instantiate the manager class
		FeedbackManager feedback = new FeedbackManager(database);
		
		// define other helper variables
		String results = null;
		
		// determine the task to undertake
		if(task.equals("initial") == true) {
			results = feedback.getInitialFeedback(performance);
		} else if(task.equals("update") == true) {
			results = feedback.getUpdatedFeedback(performance, lastId);
		}
		
		// output json mime type
		response.setContentType("application/json; charset=UTF-8");
		
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

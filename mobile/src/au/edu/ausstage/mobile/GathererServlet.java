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
 
public class GathererServlet extends HttpServlet {

	// declare private class variables
	private ServletConfig servletConfig;
	
	// declare private class constants
	private final String[] INPUT_TYPES = {"sms", "mobile-web"};

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
		
		// check on the input types
		if(InputUtils.isValid(type, INPUT_TYPES) == false) {
			// no valid input type was found
			throw new ServletException("Missing type parameter. Expected one of: " + InputUtils.arrayToString(INPUT_TYPES));
		}
		
		// determine what to do based on the input type
		if(type.equals("sms") == true) {
			/*
			 * process an SMS message 
			 */
			 
			// get the required information
			String callerId = request.getParameter("caller");
			String time     = request.getParameter("time");
			String date     = request.getParameter("date");
			String message  = request.getParameter("message");
			
			// check on the parameters
			if(InputUtils.isValid(callerId) == false) {
				// no valid marker type was found
				throw new ServletException("Missing caller parameter");
			}
			
			if(InputUtils.isValid(time) == false) {
				// no valid marker type was found
				throw new ServletException("Missing time parameter");
			}
			
			if(InputUtils.isValid(date) == false) {
				// no valid marker type was found
				throw new ServletException("Missing date parameter");
			}
			
			if(InputUtils.isValid(message) == false) {
				// no valid marker type was found
				throw new ServletException("Missing message parameter");
			}
			
			// parameters pass initial validation
			
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
			GathererManager manager = new GathererManager(database, servletConfig);
			
			data = manager.processSMS(callerId, time, date, message);
			
			// add additional data
			//data += "Remote Address: " + request.getRemoteAddr() + "\n";
			
			// ouput the data
			// set the appropriate content type
			response.setContentType("text/plain; charset=UTF-8");
	
			//get the output print writer
			PrintWriter out = response.getWriter();
	
			// send some output
			out.print(data);
		} else if(type.equals("mobile-web") == true) {
		
			// get the remaining parameters
			String performance = request.getParameter("performance");
			String date        = request.getParameter("date");
			String time        = request.getParameter("time");
			String message     = request.getParameter("message");
			
			// check to ensure that the parameters are present
			if(InputUtils.isValidInt(performance) == false) {
				throw new ServletException("Missing performance parameter");
			}
			
			if(InputUtils.isValid(date) == false || InputUtils.isValid(time) == false) {
				throw new ServletException("Missing required date and / or time parameters");
			}
			
			if(InputUtils.isValid(message) == false) {
				throw new ServletException("Missing message parameter");
			}
			
			// parameters pass initial validation
			
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
			GathererManager manager = new GathererManager(database, servletConfig);
			
			results = manager.processMobileWeb(performance, date, time, message);
			
			// ouput the data
			// set the appropriate content type
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
		}
	} // end doGet method
	
	/**
	 * Method to respond to a post request
	 *
	 * @param request a HttpServletRequest object representing the current request
	 * @param response a HttpServletResponse object representing the current response
	 */
	public void doPost (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		// do the same tasks for a post and get request
		doGet(request, response);
	
	} // end doPost method

} // end class definition

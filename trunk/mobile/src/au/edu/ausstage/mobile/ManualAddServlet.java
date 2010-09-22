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

package au.edu.ausstage.mobile;

// import additional AusStage libraries
import au.edu.ausstage.utils.*;

// import other packages
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

/**
 * A class to respond to requests to lookup data
 */
public class ManualAddServlet extends HttpServlet {

	// declare private variables
	private ServletConfig servletConfig;
	private DbManager database;
	
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
	
		// don't respond to get requests
		throw new ServletException("Invalid Request Type");
	
	} // end doGet method
	
	/**
	 * Method to respond to a post request
	 *
	 * @param request a HttpServletRequest object representing the current request
	 * @param response a HttpServletResponse object representing the current response
	 */
	public void doPost (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		// get the parameters
		String performance = request.getParameter("performance");
		String question    = request.getParameter("question");
		String sourceType  = request.getParameter("source_type");
		String date        = request.getParameter("date");
		String time        = request.getParameter("time");
		String from        = request.getParameter("from");
		String sourceId    = request.getParameter("source_id");
		String content     = request.getParameter("content");
		
		// check the parameters
		if(InputUtils.isValidInt(performance) == false || InputUtils.isValidInt(question) == false || InputUtils.isValidInt(sourceType) == false) {
			throw new ServletException("One of the performance, question and source_type parameters is invalid");
		}
		
		if(InputUtils.isValid(date) == false || InputUtils.isValid(time) == false) {
			throw new ServletException("One of the date or time parameters is invalid");
		}
		
		if(InputUtils.isValid(from) == false) {
			throw new ServletException("The from parameter is invalid");
		}
		
		if(InputUtils.isValid(content) == false) {
			throw new ServletException("The content parameter is invalid");
		}
		
		// check the hashed parameters
		if(HashUtils.isValid(from) == false) {
			throw new ServletException("The from parameter is not a valid hash");
		}
		
		if(InputUtils.isValid(sourceId) == true) {
			if(HashUtils.isValid(sourceId) == false) {
				throw new ServletException("The source_id parameter is not a valid hash");
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
		ManualAddManager manager = new ManualAddManager(database);
		
		String results = manager.addFeedback(performance, question, sourceType, date, time, from, sourceId, content);
		
		// output json mime type
		response.setContentType("application/json; charset=UTF-8");	
		
		// output the results of the search
		PrintWriter out = response.getWriter();
		out.print(results);
	
	} // end doPost method

} // end class definition

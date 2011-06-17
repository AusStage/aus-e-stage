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

// import additional AusStage classes
import au.edu.ausstage.utils.*;
import au.edu.ausstage.exchange.items.*;

/**
 * The main driving class for the Exchange Data Service
 */
public class FeedbackServlet extends HttpServlet {

	// declare private class variables
	private ServletConfig servletConfig;
	
	// declare public constants
	public static final String[] VALID_OUTPUT_TYPES   = {"html", "json", "xml", "rss", "iframe"};
	public static final String[] VALID_REQUEST_TYPES  = {"performance"};
	public static final String   DEFAULT_RESULT_LIMIT = "10";

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
		String type   = request.getParameter("type");
		String output = request.getParameter("output");
		String id     = request.getParameter("id");
		String limit  = request.getParameter("limit");
		
		// validate the request parameters
		if(InputUtils.isValid(type, VALID_REQUEST_TYPES) == false) {
			// no valid type was found
			throw new ServletException("Missing type parameter. Expected one of: " + InputUtils.arrayToString(VALID_REQUEST_TYPES));
		}
		
		if(InputUtils.isValid(output) == false) {
			output = "html";
		} else if(InputUtils.isValid(output, VALID_OUTPUT_TYPES) == false) {
			// no valid type was found
			throw new ServletException("Missing output parameter. Expected one of: " + InputUtils.arrayToString(VALID_OUTPUT_TYPES));
		}
		
		if(InputUtils.isValid(id) == false) {
			// no valid id was found
			throw new ServletException("Missing id parameter.");
		}
		
		// ensure only one id
		if(id.contains(",") == true) {
			throw new ServletException("The id parameter must contain only one numeric id value");
		}
		
		try {
			Integer.parseInt(id);
		} catch(Exception ex) {
			throw new ServletException("The id parameter must contain numeric values");
		}
		
		String[] ids = new String[1];
		ids[0] = id;
		
		// check the limit parameter
		if(InputUtils.isValid(limit) == false) {
			limit = DEFAULT_RESULT_LIMIT;
		} else if(limit.equals("all") == false) {
			try {
				Integer.parseInt(limit);	
			} catch(Exception ex) {
				throw new ServletException("The limit parameter must be 'all' or a numeric value");
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
		
		// declare helper variables
		String results = null;
		
		// get the results
		if(type.equals("performance") == true) {
			// get event data based on contributor ids
			FeedbackData data = new FeedbackData(database, ids, output, limit);
			results = data.getPerformanceData();
		}
		
		// ouput the data
		PrintWriter out = response.getWriter();
		
		if(InputUtils.isValid(request.getParameter("callback")) == false) {
			// output the appropriate mime type
			if(output.equals("html") == true) {
				response.setContentType("text/plain; charset=UTF-8");
			} else if(output.equals("json")) {
				response.setContentType("application/json; charset=UTF-8");
			} else if(output.equals("xml")) {
				response.setContentType("application/xml; charset=UTF-8");
			} else if(output.equals("rss") == true) {
				response.setContentType("application/rss+xml; charset=UTF-8");
			} else if(output.equals("iframe")) {
				response.setContentType("text/html; charset=UTF-8");
				
				// manipulate the results if required
				String css = request.getParameter("css");
				
				if(css != null) {
					results = results.replace("<!-- optional css to go here -->", "<link rel=\"stylesheet\" href=\"" + css + "\"/>");
				}
			}
			out.print(results);
		} else {
			// output the javascript mime type
			response.setContentType("application/javascript; charset=UTF-8");
			out.print(JSONPManager.wrapJSON(results, request.getParameter("callback")));
		}
		
		// log some useful information
		ServletContext context = servletConfig.getServletContext();
		
		if (request.getHeader("Referer") == null) {
			context.log("Task: feedback QueryString: " + request.getQueryString() + " RemoteAddress: " + request.getRemoteAddr() + " Referer: " + request.getHeader("Referer"));
		} else {
			context.log("Task: feedback QueryString: " + request.getQueryString() + " RemoteAddress: null Referer: " + request.getHeader("Referer"));
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

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

/**
 * The main driving class for the Exchange Data Service
 */
public class ExchangeServlet extends HttpServlet {

	// declare private variables
	private DataManager dataManager;
	
	// list of allowed output types
	private final String[] OUTPUTTYPES = {"html", "json", "xml", "rss"};
	
	// declare other helper variables
	private boolean ok = false;
	private ServletConfig config;

	/*
	 * initialise this instance
	 */
	public void init(ServletConfig conf) throws ServletException {
		// execute the parent objects init method
		super.init(conf);
		
		// store reference to the ServletConfig
		config = conf;
		
	} // end init method
	
	/**
	 * Method to respond to a get request
	 *
	 * @param request a HttpServletRequest object representing the current request
	 * @param response a HttpServletResponse object representing the current response
	 */
	public void doGet (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		// determine what type of request this is
		String type = request.getParameter("type");
		String results = null;
		
		// check the action parameter
		if(type == null) {
			throw new ServletException("Missing type parameter");
		}
		
		// get the id numbers for records of interest
		String id = request.getParameter("id");
		
		// check the id parameter
		if(id == null) {
			throw new ServletException("Missing id parameter");
		}
		
		// double check the parameter
		id = id.trim();
		
		if(id == null || id.equals("")) {
			throw new ServletException("Missing id parameter");
		}
		
		// explode the ids into an array
		String[] ids = id.split(",");
		
		// ensure the ids are numeric
		for(int i = 0; i < ids.length; i++) {
			try {
				Integer.parseInt(ids[i]);
			} catch(Exception ex) {
				throw new ServletException("The id parameter must contain numeric values");
			}
		}
		
		// impose sensible limit on id numbers
		if(ids.length > 10) {
			throw new ServletException("The id parameter must contain no more than 10 numbers");
		}
		
		// get the output type
		String output = request.getParameter("output");
		
		// check the output parameter
		if(output == null) {
		
			// use default output format
			output = "html";
			
		} else {
		
			// double check the parameter
			output = output.trim();
			
			if(output == null || output.equals("")) {
				throw new ServletException("Missing output type");
			}
			
			// reset the check variable
			ok = false;
			
			// check to ensure output type is known
			for(int i = 0; i < OUTPUTTYPES.length; i++) {
				if(output.equals(OUTPUTTYPES[i])) {
					ok = true;
				}
			} 
			
			if(ok == false) {
				throw new ServletException("Unknown output type detected");
			}
		}
		
		// get the limit parameter
		String limit = request.getParameter("limit");
		
		// check the limit parameter
		if(limit == null) {
			limit = "10";
		} else {
			
			// double check the parameter
			limit = limit.trim();
			
			if(output == null || output.equals("")) {
				throw new ServletException("Missing limit type");
			}
			
			if(limit.equals("all") == false) {
				// check for non numeric limit parameter
				try {
					Integer.parseInt(limit);
				} catch(Exception ex) {
					throw new ServletException("The limit parameter must be \"all\" or numeric");
				}
			}
		}
		
		// instantiate the data manager class
		dataManager = new DataManager(config);
		
		// instantiate a copy of the ExchangeData class
		ExchangeData data = new ExchangeData(dataManager);	
		
		if(type.equals("organisation")) {
			
			results = data.getOrganisationData(ids, output, limit);
			
		} else if(type.equals("contributor")) {

			results = data.getContributorData(ids, output, limit);			
		
		} else if(type.equals("venue")) {

			results = data.getVenueData(ids, output, limit);			
		
		} else {
			// unknown type parameter at this time
			throw new ServletException("Unknown type parameter");
		}
		
		// check to see if this is a call for a script
		String script = request.getParameter("script");
		if(script != null && script.equals("true") == true) {
		
			// filter the results
			results = results.replaceAll("\"", "\\\\\"");
						
			// add to the results variable
			results = "function ausstage_data () { return \"" + results + "\";}";			
			response.setContentType("text/javascript; charset=UTF-8");
			
		} else {
					
			// ouput plain results
			// set the appropriate content type for the output
			if(output.equals("html") == true) {
				response.setContentType("text/plain; charset=UTF-8");
			} else if(output.equals("json")) {
				response.setContentType("application/json; charset=UTF-8");
			} else if(output.equals("xml")) {
				response.setContentType("application/xml; charset=UTF-8");
			} else if(output.equals("rss") == true) {
				response.setContentType("application/rss+xml; charset=UTF-8");
			}
		}
				
		//get the output print writer
		PrintWriter out = response.getWriter();
		
		// send some output
		out.print(results);
		
		// log some useful information
		ServletContext context = config.getServletContext();
		
		if (request.getHeader("Referer") == null) {
			context.log("QueryString: " + request.getQueryString() + " RemoteAddress: " + request.getRemoteAddr() + " Referer: " + request.getHeader("Referer"));
		} else {
			context.log("QueryString: " + request.getQueryString() + " RemoteAddress: null Referer: " + request.getHeader("Referer"));
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

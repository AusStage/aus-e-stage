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

// import additional classes
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

/**
 * The main driving class for the Mapping Service
 */
public class GeocodeServlet extends HttpServlet {

	// declare private variables
	private ServletConfig servletConfig;

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
		
		// get the action parameter
		String action = request.getParameter("action");
		
		// check the action parameter
		if(action == null) {
			throw new ServletException("Missing action parameter");
		} 
		
		// get a data manager instance
		DataManager dataManager = new DataManager(servletConfig);
		
		if(action.equals("lookup")) {
			
			// get remaining parameters
			String type = request.getParameter("type");
			String id   = request.getParameter("id");
			
			// check on the parameters
			if(type == null || id == null) {
				throw new ServletException("Missing required parameters");
			}
			
			// double check the parameters
			type = type.trim();
			id   = id.trim();
			
			if(type == null || id == null || type.equals("") || id.equals("")) {
				throw new ServletException("Missing required parameters");
			}
			
			// determine what type of lookup we're doing
			if(type.equals("venue")) {
				// lookup venue info
				
				// get an instance of the geocode class
				GeocodeManager geocode = new GeocodeManager(dataManager);
				
				// return the search results
				String results = geocode.doVenueLookup(id);
				
				// ouput the results
				// set the appropriate content type
				response.setContentType("text/plain; charset=UTF-8");
				
				// set the appropriate headers to disable caching
				// particularly for IE
				response.setHeader("Cache-Control", "max-age=0,no-cache,no-store,post-check=0,pre-check=0");
				
				//get the output print writer
				PrintWriter out = response.getWriter();
				
				// send some output
				out.print(results);
								
			} else {
				throw new ServletException("Unknown type parameter value");
			}
		
		}else {
			// unknown action type
			throw new ServletException("Unknown action parameter value");
		}
	
	} // end doGet method
	
	/**
	 * Method to respond to a post request
	 *
	 * @param request a HttpServletRequest object representing the current request
	 * @param response a HttpServletResponse object representing the current response
	 */
	public void doPost (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		// determine action to take
		String action = request.getParameter("action");
		
		// check action parameter
		if(action == null) {
			throw new ServletException("Missing action parameter");
		}
		
		// get a data manager instance
		DataManager dataManager = new DataManager(servletConfig);
		
		// do an organisation or venue search		
		if(action.equals("org_search") || action.equals("venue_search")) {
			
			// get the requested search term
			String searchTerm = request.getParameter("search_term");
			
			// check on the search term
			if(searchTerm == null) {
				throw new ServletException("Missing search terms, invalid post?");
			}
			
			// double check for empty string
			searchTerm = searchTerm.trim();
			
			if(searchTerm == null || searchTerm.equals("")) {
				throw new ServletException("Missing search terms");
			}
			
			// get the selected operator
			String searchOperator = request.getParameter("operator");
			
			if(searchOperator == null) {
				throw new ServletException("Missing search operator, invalid post?");
			}
			
			if(searchOperator.equals("and") == false && searchOperator.equals("or") == false && searchOperator.equals("exact") == false) {
				throw new ServletException("Invalid search operator, invalid post?");
			}
			
			// determine what to do with the search terms
			if(searchOperator.equals("and") || searchOperator.equals("or")) {
			
				// change search query to lower case
				searchTerm = searchTerm.toLowerCase();
				
				// remove any existing search terms
				searchTerm = searchTerm.replace(" and ", "");
				searchTerm = searchTerm.replace(" or ", "");
				searchTerm = searchTerm.replace(" not ", "");
				
				// rewrite the search terms
				searchTerm = searchTerm.replace(" ", " " + searchOperator + " ");
				
			}					
			
			// get an instance of the geocode class
			GeocodeManager geocode = new GeocodeManager(dataManager);
			
			// return the search results
			String results = geocode.doSearch(action, searchTerm);
			
			// ouput the results
			// set the appropriate content type
			response.setContentType("text/plain; charset=UTF-8");
			
			// set the appropriate headers to disable caching
			// particularly for IE
			response.setHeader("Cache-Control", "max-age=0,no-cache,no-store,post-check=0,pre-check=0");
			
			//get the output print writer
			PrintWriter out = response.getWriter();
			
			// send some output
			out.print(results);		
			
		} else if(action.equals("org_venue_search")) {
			// get a list of venues associated with an organisation
			
			// get the requested search term
			String searchTerm = request.getParameter("search_term");
			
			// check on the search term
			if(searchTerm == null) {
				throw new ServletException("Missing search terms, invalid post?");
			}
			
			// double check for empty string
			searchTerm = searchTerm.trim();
			
			if(searchTerm == null || searchTerm.equals("")) {
				throw new ServletException("Missing search terms");
			}
			
			// get an instance of the geocode class
			GeocodeManager geocode = new GeocodeManager(dataManager);
			
			// return the search results
			String results = geocode.doSearch(action, searchTerm);
			
			// ouput the results
			// set the appropriate content type
			response.setContentType("text/plain; charset=UTF-8");
			
			// set the appropriate headers to disable caching
			// particularly for IE
			response.setHeader("Cache-Control", "max-age=0,no-cache,no-store,post-check=0,pre-check=0");
			
			//get the output print writer
			PrintWriter out = response.getWriter();
			
			// send some output
			out.print(results);					
		
		} else {
			// unknown action type
			throw new ServletException("Unknown action type");
		}	
	
	} // end doPost method
	
} // end class definition

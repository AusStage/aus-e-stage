/*
 * This file is part of the AusStage Audience Participation Service
 *
 * The AusStage Audience Participation Service is free software: you can 
 * redistribute it and/or modify it under the terms of the GNU General 
 * Public License as published by the Free Software Foundation, either 
 * version 3 of the License, or (at your option) any later version.
 *
 * The AusStage Audience Participation Service is distributed in the hope 
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the 
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the AusStage Audience Participation Service.  
 * If not, see <http://www.gnu.org/licenses/>.
*/

package au.edu.ausstage.mobile;

// import additional classes
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

/**
 * A class used to control the flow of information into the system
 * by gathering audience feedback from a variety of different sources
 */
public class LookupServlet extends HttpServlet {

	// declare private variables
	private ServletConfig servletConfig;
	private DataManager dataManager;

	/**
	 * initialise this servlet
	 *
	 * @param conf the ServletConfig object for this container
	 */
	public void init(ServletConfig conf) throws ServletException {
	
		// execute the parent objects init method
		super.init(conf);
		
		// store a reference to this servlet config
		servletConfig = conf;
		
		// instantiate the dataManager object
		dataManager = new DataManager(conf);
	
	} // end init method
	
	/**
	 * Method to respond to a get request
	 *
	 * @param request a HttpServletRequest object representing the current request
	 * @param response a HttpServletResponse object representing the current response
	 */
	public void doGet (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		// determine the request type
		String type = request.getParameter("type");
		
		if(type == null) {
			throw new ServletException("Missing request type");
		}
		
		// declare helper variables
		LookupManager lookup = new LookupManager(dataManager);
		
		if(type.equals("venue-geo")) {
			// lookup a list of venues using the supplied coordinates
			///mobile/lookup?type=venue-geo&ne=-35.019662,138.579566&sw=-35.032278,138.565834
			
			// declare helper variables
			String northEast = request.getParameter("ne");
			String southWest = request.getParameter("sw");
			String results;
			
			if(northEast == null || southWest == null) {
				throw new ServletException("Missing coordinate parameters");
			}
			
			// get the results
			results = lookup.getVenuesByCoords(northEast, southWest);
			
			// ouput the XML
			// set the appropriate content type
			response.setContentType("text/xml; charset=UTF-8");
			
			// set the appropriate headers to disable caching
			// particularly for IE
			response.setHeader("Cache-Control", "max-age=0,no-cache,no-store,post-check=0,pre-check=0");
			
			//get the output print writer
			PrintWriter out = response.getWriter();
			
			// send some output
			out.print(results);
			
		} else {
			throw new ServletException("Missing unknown request type");
		}
	
	} // end doGet method
	
	/**
	 * Method to respond to a post request
	 *
	 * @param request a HttpServletRequest object representing the current request
	 * @param response a HttpServletResponse object representing the current response
	 */
	public void doPost (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		// do not respond to get requests
		throw new ServletException("Invalid request");
	
	} // end doPost method

} // end class definition

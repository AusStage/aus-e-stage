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
 * The main driving class for the Browse by Venue Servlet
 */
public class BrowseServlet extends HttpServlet {

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
		
		// get a DataManager object
		DataManager dataManager = new DataManager(servletConfig);
		
		// determine what action to take
		if(action.equals("markers")) {
		
			// get an instance of the BrowseDataBuilder class
			BrowseDataBuilder data = new BrowseDataBuilder(dataManager);
			
			String results = data.getMarkerXMLString();
			
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
			
		} else if(action.equals("lookup")) {
			// need to lookup data about the venue
			String id = request.getParameter("id");
			
			// get the years
			String startYear = request.getParameter("start");
			String finishYear = request.getParameter("finish");
			
			// check the id parameter
			if(id == null) {
				throw new ServletException("Missing id parameter");
			}
			
			// sanitise the id
			id = id.trim();
			
			if(id == null || id.equals("")) {
				throw new ServletException("Missing id parameter");
			}
			
			try {
				Integer.parseInt(id);
			} catch (Exception ex) {
				throw new ServletException("id paramater must be an integer");
			}
			
			// get an instance of the BrowseDataBuilder class
			BrowseDataBuilder data = new BrowseDataBuilder(dataManager);
			
			// get the event event info for this venue
			String results = null;
			
			if(startYear == null && finishYear == null) {
				results = data.doSearch(id);
			} else {
				results = data.doSearch(id, startYear, finishYear);
			}
			
			// ouput the XML
			// set the appropriate content type
			response.setContentType("text/html; charset=UTF-8");
			
			// set the appropriate headers to disable caching
			// particularly for IE
			response.setHeader("Cache-Control", "max-age=0,no-cache,no-store,post-check=0,pre-check=0");
			
			//get the output print writer
			PrintWriter out = response.getWriter();
			
			// send some output
			out.print(results);
			
		} else if(action.equals("timeline")) {
			// need to get data for the timeline
			
			// need to lookup data about the venue
			String id = request.getParameter("id");
			
			// check the id parameter
			if(id == null) {
				throw new ServletException("Missing id parameter");
			}
			
			// sanitise the id
			id = id.trim();
			
			if(id == null || id.equals("")) {
				throw new ServletException("Missing id parameter");
			}
			
			try {
				Integer.parseInt(id);
			} catch (Exception ex) {
				throw new ServletException("id paramater must be an integer");
			}
			
			// get an instance of the BrowseDataBuilder class
			BrowseDataBuilder data = new BrowseDataBuilder(dataManager);
			
			// get the event event info for this venue
			String results = data.getTimelineXML(id);
			
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
		
		} else if(action.equals("kml")) {
			// need to build a kml file
			
			// get an instance of the BrowseDataBuilder class
			BrowseDataBuilder data = new BrowseDataBuilder(dataManager);
			
			// get the KML data
			String results = data.getKMLString();
			
			// ouput the XML
			// set the appropriate content type
			response.setContentType("application/vnd.google-earth.kml+xml; charset=UTF-8");
			response.setHeader("Content-Disposition", "attachment;filename=ausstage-venue-data.kml");

			//get the output print writer
			PrintWriter out = response.getWriter();
			
			// send some output
			out.print(results);
			
		} else {
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
	
		// don't respond to post requests
		throw new ServletException("Invalid Request Type");
	
	} // end doPost method
	
} // end class definition

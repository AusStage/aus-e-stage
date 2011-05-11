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
 
/**
 * A class to respond to requests to download map data as a KML file
 */
public class KmlDownloadServlet extends HttpServlet {

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
	 * Method to respond to a post request
	 *
	 * @param request a HttpServletRequest object representing the current request
	 * @param response a HttpServletResponse object representing the current response
	 */
	public void doPost (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		// get the parameters
		String[] contributors  = new String[0];
		String[] organisations = new String[0];
		String[] venues        = new String[0];
		String[] events        = new String[0];

		if(InputUtils.isValid(request.getParameter("contributors")) == true) {
			contributors = request.getParameter("contributors").split("-");
		}
		
		if(InputUtils.isValid(request.getParameter("organisations")) == true) {
			organisations = request.getParameter("organisations").split("-");
		}
		
		if(InputUtils.isValid(request.getParameter("venues")) == true) {
			venues = request.getParameter("venues").split("-");
		}
		
		if(InputUtils.isValid(request.getParameter("events")) == true) {
			events = request.getParameter("events").split("-");
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
		
		KmlDownloadManager download = new KmlDownloadManager(database);
		
		try {
			download.prepare(contributors, organisations, venues, events);
		} catch (KmlDownloadException e) {
			throw new ServletException("Error in preparing KML for download", e);
		}
		
		// send the KML to the client
		// set the appropriate content type
		//response.setContentType("application/vnd.google-earth.kml+xml; charset=UTF-8");
				
		//debug code
		response.setContentType("text/plain");
		
		String[] fields = DateUtils.getCurrentDateAsArray();
		response.setHeader("Content-Disposition", "attachment;filename=" + "ausstage-map-" + fields[0] + "-" + fields[1] + "-" + fields[2] + ".kml");
			
		try {
			download.print(response.getWriter());
		} catch (KmlDownloadException e) {
			throw new ServletException("Error in sending KML to client", e);
		}
		
	} // end doPost method
	
		
	/**
	 * Method to respond to a get request
	 *
	 * @param request a HttpServletRequest object representing the current request
	 * @param response a HttpServletResponse object representing the current response
	 */
	public void doGet (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		// don't respond to get requests
		throw new ServletException("Invalid Request Type");
		//doPost(request, response);
		
	} // end doGet method

} // end class definition

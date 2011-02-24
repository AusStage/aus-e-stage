/*
 * This file is part of the Aus-e-Stage Beta Website
 *
 * The Aus-e-Stage Beta Website is free software: you can redistribute
 * it and/or modify it under the terms of the GNU General Public License 
 * as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * The Aus-e-Stage Beta Website is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the AusStage Mapping Service.  
 * If not, see <http://www.gnu.org/licenses/>.
 */

// define the package
package au.edu.ausstage.beta;

// import additional classes
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

// import the ausstage utilities
import au.edu.ausstage.utils.DbManager;
import au.edu.ausstage.utils.InputUtils;

/**
 * A class used to generate the analytics pages for the root website
 *
 * @author corey.wallis@flinders.edu.au
 */
public class AnalyticsServlet extends HttpServlet {

	// declare private variables
	private ServletConfig servletConfig;
	private String reportDirectory;
	
	private final String XSL_FILE_NAME = "analytics-report.xsl";
	
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
		
		reportDirectory = servletConfig.getServletContext().getInitParameter("reportDirectory");
	
	} // end init method
	
	/**
	 * Method to respond to a get request
	 *
	 * @param request a HttpServletRequest object representing the current request
	 * @param response a HttpServletResponse object representing the current response
	 */
	public void doGet (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		// get what type of data is incoming
		String reportFile = request.getParameter("report-file");
		String data       = null;
		
		// check the parameter
		if(InputUtils.isValid(reportFile) == false) {
			throw new ServletException("Missing report-file parameter");
		}
		
		if(reportFile.equals("ausstage-record-count") == true) {
		
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
		
			data = AnalyticsManager.getRecordCountAnalytics(database);
		} else {
			data = AnalyticsManager.processXMLReport(reportDirectory, XSL_FILE_NAME, reportFile);
		}
		
		if(data == null) {
			throw new ServletException("An unexpected error has occured during the XML processing");
		} else {
			
			// ouput the XML
			// set the appropriate content type
			response.setContentType("text/plain; charset=UTF-8");
			
			//get the output print writer
			PrintWriter out = response.getWriter();
			
			// send some output
			out.print(data);
		}
	
	} // end doGet method
	
	/**
	 * Method to respond to a post request
	 *
	 * @param request a HttpServletRequest object representing the current request
	 * @param response a HttpServletResponse object representing the current response
	 */
	public void doPost (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		throw new ServletException("Inavlid request type");
	
	} // end doPost method


} // end class defintion

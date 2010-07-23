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

package au.edu.ausstage.networks;

// import additional classes
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

import au.edu.ausstage.utils.InputUtils;

/**
 * A class to respond to requests to export data
 */
public class ExportServlet extends HttpServlet {

	// declare private variables
	private ServletConfig servletConfig;
	private DataManager database;
	
	// declare private constants
	private static final String[] TASK_TYPES   = {"simple-network"};
	private static final String[] FORMAT_TYPES = {"graphml"};
	private static final String   MIN_DEGREES  = "1";
	private static final int      MAX_DEGREES  = 5;

	/*
	 * initialise this instance
	 */
	public void init(ServletConfig conf) throws ServletException {
		// execute the parent objects init method
		super.init(conf);
		
		// store configuration for later
		servletConfig = conf;
		
		// instantiate a database manager object
		database = new DataManager(conf);
		
	} // end init method
	
	/**
	 * Method to respond to a get request
	 *
	 * @param request a HttpServletRequest object representing the current request
	 * @param response a HttpServletResponse object representing the current response
	 */
	public void doGet (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		// get the parameters
		String taskType   = request.getParameter("task");
		String id         = request.getParameter("id");
		String formatType = request.getParameter("format");
		String degrees    = request.getParameter("degrees");
		
		// check on the taskType parameter
		if(InputUtils.isValid(taskType, TASK_TYPES) == false) {
			// no valid task type was found
			throw new ServletException("Missing task parameter. Expected one of: " + java.util.Arrays.toString(TASK_TYPES).replaceAll("[\\]\\[]", ""));
		}

		// check on the id parameter
		if(InputUtils.isValidInt(id) == false) {
			throw new ServletException("Missing or invalid id parameter.");
		}

		// check the format parameter
		if(InputUtils.isValid(formatType) == false) {
			// use default value
			formatType = "graphml";
		} else {
			if(InputUtils.isValid(formatType, FORMAT_TYPES) == false) {
				throw new ServletException("Missing format type. Expected: " + java.util.Arrays.toString(FORMAT_TYPES).replaceAll("[\\]\\[]", ""));
			}
		}
		
		// check the degrees parameter
		if(InputUtils.isValidInt(degrees) == false) {
			// use default value
			degrees = MIN_DEGREES;
		} else {
			try {
				if(Integer.parseInt(degrees) > MAX_DEGREES) {
					throw new ServletException("Degree parameter must be less than: " + MAX_DEGREES);
				}
			} catch (java.lang.NumberFormatException ex) {
				degrees = MIN_DEGREES;
			}
		}
		
		// instantiate a lookup object
		ExportManager export = new ExportManager(database);
		
		String results = null;
		
		// determine the type of lookup to undertake
		if(taskType.equals("simple-network") == true) {
			results = export.getSimpleNetwork(id, formatType, degrees);
		}
		
		// check on what was returned
		if(results == null) {
			throw new ServletException("An error occured whilst processing this request. If it persists contact the site administrator");
		}
		
		// output the appropriate mime type
		if(formatType == "graphml") {
			// output xml mime type
			response.setContentType("text/xml; charset=UTF-8");
		}
		
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

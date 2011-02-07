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
public class LookupServlet extends HttpServlet {

	// declare private variables
	private ServletConfig servletConfig;
	private DbManager database;
	
	// declare private constants
	private final String[] TASK_TYPES        = {"system-property", "performance", "validate", "question", "location", "date"};
	private final String[] FORMAT_TYPES      = {"json"};
	private final String[] PROPERTY_ID_TYPES = {"feedback-source-types"};
	private final String[] VALIDATION_TYPES  = {"performance", "question"};

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
	
		// get the parameters
		String taskType       = request.getParameter("task");
		String id             = request.getParameter("id");
		String formatType     = request.getParameter("format");
		String latitude       = request.getParameter("lat");
		String longitude      = request.getParameter("lng");
		String distance       = request.getParameter("distance");		
		String startDate      = request.getParameter("startdate");
		String endDate        = request.getParameter("enddate");
		String validationType = null;
		
		// check on the taskType parameter
		if(InputUtils.isValid(taskType, TASK_TYPES) == false) {
			// no valid task type was found
			throw new ServletException("Missing task parameter. Expected one of: " + java.util.Arrays.toString(TASK_TYPES).replaceAll("[\\]\\[]", ""));
		}

		// check on the id parameter
		if(taskType.equals("system-property") == true) {
			// this is a system property lookup request
			if(InputUtils.isValid(id, PROPERTY_ID_TYPES) == false) {
				throw new ServletException("Missing id parameter. Expected one of: " + java.util.Arrays.toString(PROPERTY_ID_TYPES).replaceAll("[\\]\\[]", ""));
			}
		} else if(taskType.equals("validate") == true) {
			// this is a validation request
			if(InputUtils.isValid(request.getParameter("performance")) == false && InputUtils.isValid(request.getParameter("question")) == false) {
				throw new ServletException("Missing parameter for validation. Expected one of: " + java.util.Arrays.toString(VALIDATION_TYPES).replaceAll("[\\]\\[]", ""));
			} else {
				if(InputUtils.isValid(request.getParameter("performance")) == true) {
					// this is a performance id that needs to be validated
					if(InputUtils.isValidInt(request.getParameter("performance")) == true) {
						validationType = "performance";
					} else {
						throw new ServletException("Performance parameter is expected to be a valid integer");
					}
				} else if (InputUtils.isValid(request.getParameter("question")) == true) {
					// this is a performance id that needs to be validated
					if(InputUtils.isValidInt(request.getParameter("question")) == true) {
						validationType = "question";
					} else {
						throw new ServletException("Question parameter is expected to be a valid integer");
					}
				}
			}
		} else if(taskType.equals("location") == true) {
			// this is a location request
			// first round of validation
			if(InputUtils.isValid(latitude) == false || InputUtils.isValid(longitude) == false || InputUtils.isValid(distance) == false) {
				throw new ServletException("Missing parameters, expected lat, lng and distance");
			} else {
				// second round validation
				if(CoordinateManager.isValidLatitude(Float.valueOf(latitude)) == false) {
					throw new ServletException("The value of the latitude parameter did not pass validation as defined by the CoordinateManager class");
				}
				
				if(CoordinateManager.isValidLongitude(Float.valueOf(longitude)) == false) {
					throw new ServletException("The value of the longitude parameter did not pass validation as defined by the CoordinateManager class");
				}
				
				if(InputUtils.isValidInt(distance) == false) {
					throw new ServletException("The value of the distance parameter must be a valid integer");
				}
			}
		} else if(taskType.equals("date") == true) {
			if(InputUtils.isValid(startDate) == false || InputUtils.isValid(endDate) == false) {
				throw new ServletException("Missing parameters, expected 'startdate', 'enddate'");
			}
		
			if(InputUtils.isValidDate(startDate) == false || InputUtils.isValidDate(endDate) == false) {
				throw new IllegalArgumentException("Both parameters, 'startdate', 'enddate', must be in the format yyyy-mm-dd");
			}
		
		} else {
			// this is another type of lookup request
			if(InputUtils.isValidInt(id) == false) {
				throw new ServletException("Missing or invalid id parameter.");
			}
		}

		// check the format parameter
		if(InputUtils.isValid(formatType) == false) {
			// use default value
			formatType = "json";
		} else {
			if(InputUtils.isValid(formatType, FORMAT_TYPES) == false) {
				throw new ServletException("Missing format type. Expected: " + java.util.Arrays.toString(FORMAT_TYPES).replaceAll("[\\]\\[]", ""));
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
		LookupManager lookup = new LookupManager(database);
		
		String results = null;
		
		if(taskType.equals("system-property") == true) {
			// this is a system property that we need to get
			if(id.equals("feedback-source-types") == true) {
				results = lookup.getFeedbackSourceTypes(formatType);
			}
		} else if(taskType.equals("performance") == true) {
			results = lookup.getPerformanceDetails(id, formatType);
		} else if(taskType.equals("question") == true) {
			results = lookup.getQuestionDetails(id, formatType);
		} else if(taskType.equals("validate") == true) {
			if(validationType.equals("performance") == true) {
				results = lookup.getPerformanceDetails(request.getParameter("performance"), formatType);
				if(results.length() > 10) {
					results = "true";
				} else {
					results = "false";
				}
			} else if(validationType.equals("question") == true) {
				results = lookup.getQuestionDetails(request.getParameter("question"), formatType);
				if(results.length() > 10) {
					results = "true";
				} else {
					results = "false";
				}
			}
		} else if(taskType.equals("location") == true) {
			results = lookup.getPerformanceByLocation(latitude, longitude, distance);
		} else if(taskType.equals("date") == true) {
			results = lookup.getPerformanceByDate(startDate, endDate);
		}
		
		
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

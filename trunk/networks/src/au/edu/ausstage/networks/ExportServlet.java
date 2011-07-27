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
	private DataManager rdf;
	private DatabaseManager db;
	private String connectString = null;
	
	// declare constants
	private final String[] TASK_TYPES   = {"ego-centric-network", "event-centric-network",
	                                       "full-edge-list-with-dups", "full-edge-list-no-dups", "full-edge-list-with-dups-id-only", "full-edge-list-no-dups-id-only",
	                                       "actor-edge-list-with-dups", "actor-edge-list-no-dups", "actor-edge-list-with-dups-id-only", "actor-edge-list-no-dups-id-only"};
	                                       
	public final static String[] FORMAT_TYPES = {"graphml", "debug"};
	public final static int      MIN_DEGREES  = 1;
	public final static int      MAX_DEGREES  = 3;
	public final static String[] EXPORT_TYPES_UI = {"ego-centric-network"}; // valid export options via the UI
	
	/*
	 * initialise this instance
	 */
	public void init(ServletConfig conf) throws ServletException {
		// execute the parent objects init method
		super.init(conf);
		
		// store configuration for later
		servletConfig = conf;
		
		// instantiate a database manager object
		rdf = new DataManager(conf);
		
		connectString = conf.getServletContext().getInitParameter("databaseConnectionString");
		db = new DatabaseManager(connectString);
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
		String radius = request.getParameter("radius");
		int degrees = 0;
		boolean simplify = true;
		
		// check on the taskType parameter
		if(InputUtils.isValid(taskType, TASK_TYPES) == false) {
			// no valid task type was found
			throw new ServletException("Missing task parameter. Expected one of: " + java.util.Arrays.toString(TASK_TYPES).replaceAll("[\\]\\[]", ""));
		}
		
		// check the other parameters dependant on the task type
		if(taskType.equalsIgnoreCase("ego-centric-network") == true || taskType.equalsIgnoreCase("event-centric-network") == true) {
			// check the other parameters as they are required
		
			if(radius != null) {
				try {
					// get the parameter and convert to an integer
					degrees = Integer.parseInt(radius);	
				} catch (NumberFormatException ex) {
					// degrees must be a number
					throw new ServletException("Radius parameter must be an integer");
				}
										
				// double check the parameter
				if(InputUtils.isValidInt(degrees, MIN_DEGREES, MAX_DEGREES) == false) {
					throw new ServletException("Radius parameter must be less than: " + MAX_DEGREES);
				}
			} else {
				degrees = 1;
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
		} else {
			// set some logical default parameters
			formatType = "edge-list";
		}			
		
		// output the appropriate mime type
		if(formatType.equals("graphml")) {
			// output xml mime type
			response.setContentType("text/xml; charset=UTF-8");
			
		} else if(formatType.equals("debug")){
			// output plain text mime type
			response.setContentType("text/plain; charset=UTF-8");
			response.setHeader("Content-Disposition", "attachment;filename=ausstage-graph-" + id + "-degrees-" + degrees + "-debug.txt");
		} else if(formatType.equals("edge-list")) {
			response.setContentType("text/plain; charset=UTF-8");
			response.setHeader("Content-Disposition", "attachment;filename=" + taskType + ".edge");
		}
		
		// determine the type of export to undertake
		if(taskType.equals("ego-centric-network")&& formatType.equalsIgnoreCase("graphml")) {
			String filename = "Con-" + id + "-D-" + Integer.toString(degrees);
			filename = filename + "." + formatType;	
			response.setHeader("Content-Disposition", "attachment;filename=" + filename);
			
			// instantiate a lookup object
			ExportManager export = new ExportManager(rdf);
			export.buildCollaboratorNetworkGraphml(id, formatType, degrees, "undirected", response.getWriter());
			
		} else if(taskType.startsWith("full-edge-list")) {
			// instantiate a lookup object
			ExportManager export = new ExportManager(rdf);
			export.getFullEdgeList(taskType, response.getWriter());
			
		} else if(taskType.startsWith("actor-edge-list")) {
			// instantiate a lookup object
			ExportManager export = new ExportManager(rdf);
			export.getActorEdgeList(taskType, response.getWriter());
			
		} else if(taskType.equals("event-centric-network") && formatType.equalsIgnoreCase("graphml")) {
			
			String filename = "Evt-" + id + "-D-" + Integer.toString(degrees);
			if (degrees >= 2) { 
				if (request.getParameter("simplify").equalsIgnoreCase("false")) {				
					simplify  = false;
					filename = filename + "-C";   //complete version
				} else if (request.getParameter("simplify").equalsIgnoreCase("true")) { 
					simplify = true;
					filename = filename + "-S";	//simplified version
				}
			}
			filename = filename + "." + formatType;	
			response.setHeader("Content-Disposition", "attachment;filename=" + filename);
			
			ExportManager export = new ExportManager(db);
			export.buildEvtNetworkGraphml(id, degrees, simplify, "directed", response.getWriter());			
			
		}
		
		try {
			db.closeDB();
		} catch (Throwable e) {
			e.printStackTrace();
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

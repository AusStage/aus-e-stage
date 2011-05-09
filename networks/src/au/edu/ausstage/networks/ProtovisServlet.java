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

//import au.edu.ausstage.utils.DbManager;
import au.edu.ausstage.utils.DatabaseManager;
import au.edu.ausstage.utils.InputUtils;
import au.edu.ausstage.utils.JSONPManager;


/**
 * A class to respond to requests to export data
 */
public class ProtovisServlet extends HttpServlet {

	// declare private variables
	private DataManager RdfDatabase;
	//private DbManager db;
	private DatabaseManager db;
	private String connectString = null;
	
	// declare constants
	private final String[] TASK_TYPES   = {"ego-centric-network", "event-centric-network"};

	/*
	 * initialise this instance
	 */
	public void init(ServletConfig conf) throws ServletException {
		// execute the parent objects init method
		super.init(conf);
		
		//get database connect string from context-param in web.xml
		connectString = conf.getServletContext().getInitParameter("databaseConnectionString");
		
		// instantiate a RDF database manager object
		RdfDatabase = new DataManager(conf);
		
		// instantiate an Oracle database manager object
		//db = new DbManager(connectString);
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
		int radius = 0;
		boolean simplify = true;
		
		// check on the taskType parameter
		if(InputUtils.isValid(taskType, TASK_TYPES) == false) {
			// no valid task type was found
			throw new ServletException("Missing task parameter. Expected one of: " + java.util.Arrays.toString(TASK_TYPES).replaceAll("[\\]\\[]", ""));
		}
		
		// check on the radius parameter		
		if(request.getParameter("radius") != null) {
			try {
				// get the parameter and convert to an integer
				radius = Integer.parseInt(request.getParameter("radius"));	
			} catch (NumberFormatException ex) {
				// degrees must be a number
				throw new ServletException("Radius parameter must be an integer");
			}
									
			// double check the parameter
			if(InputUtils.isValidInt(radius, ExportServlet.MIN_DEGREES, ExportServlet.MAX_DEGREES) == false) {
				throw new ServletException("Radius parameter must be less than: " + ExportServlet.MAX_DEGREES);
			}
		} else {
			radius = ExportServlet.MIN_DEGREES;
		}
		
		// check on the id parameter
		if(InputUtils.isValidInt(id) == false) {
			throw new ServletException("Missing or invalid id parameter.");
		}
		
		if(taskType.equals("ego-centric-network") == true) {
		
			// instantiate a manger object
			ProtovisEgoCentricManager manager = new ProtovisEgoCentricManager(RdfDatabase);
		
			// check how to return the data
			if(InputUtils.isValid(request.getParameter("callback")) == false) {
		
				// output json mime type
				response.setContentType("application/json; charset=UTF-8");
		
				// output the results of the export
				PrintWriter out = response.getWriter();
				out.print(manager.getData(id, radius));
			} else {
				// output javascript mime type
				response.setContentType("application/javascript; charset=UTF-8");
		
				// output the results of the export
				PrintWriter out = response.getWriter();
				JSONPManager.wrapJSON(manager.getData(id, radius), request.getParameter("callback"), out);
			}
		} else if(taskType.equals("event-centric-network") == true) {
		
			if (radius >= 2)
				if (request.getParameter("simplify").equalsIgnoreCase("false"))
					simplify  = false;
				else if (request.getParameter("simplify").equalsIgnoreCase("true"))
					simplify = true;
			
			// instantiate a manger object
			ProtovisEventCentricManager manager = new ProtovisEventCentricManager(db);
		
			// check how to return the data
			if(InputUtils.isValid(request.getParameter("callback")) == false) {
		
				// output json mime type
				response.setContentType("application/json; charset=UTF-8");
		
				// output the results of the export
				PrintWriter out = response.getWriter();
				out.print(manager.getData(id, radius, simplify));
			} else {
				// output javascript mime type
				response.setContentType("application/javascript; charset=UTF-8");
		
				// output the results of the export
				PrintWriter out = response.getWriter();
				JSONPManager.wrapJSON(manager.getData(id, radius, simplify), request.getParameter("callback"), out);
			}		
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

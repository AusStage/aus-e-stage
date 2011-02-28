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

import au.edu.ausstage.utils.InputUtils;

/**
 * A class used to transform CSS into the versions required by Aus-e-Stage websites
 *
 * @author corey.wallis@flinders.edu.au
 */
public class TransformServlet extends HttpServlet {

	// declare private variables
	private ServletConfig servletConfig;
	
	// declare private class constants
	private final String[] TRANSFORM_TYPES = {"css"};
	
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

	
	} // end init method
	
	/**
	 * Method to respond to a get request
	 *
	 * @param request a HttpServletRequest object representing the current request
	 * @param response a HttpServletResponse object representing the current response
	 */
	public void doGet (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		throw new ServletException("Inavlid request type");
	
	} // end doGet method
	
	/**
	 * Method to respond to a post request
	 *
	 * @param request a HttpServletRequest object representing the current request
	 * @param response a HttpServletResponse object representing the current response
	 */
	public void doPost (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		// get the parameters
		String type = request.getParameter("type");
		String source = request.getParameter("source");
		
		// check on the parameters
		if(InputUtils.isValid(type, TRANSFORM_TYPES) == false) {
			throw new ServletException("Missing type parameter. Expected one of: " + InputUtils.arrayToString(TRANSFORM_TYPES));
		}
		
		if(InputUtils.isValid(source) == false) {
			throw new ServletException("Missing cssinput parameter.");
		}
		
		// output json mime type
		response.setContentType("application/json; charset=UTF-8");
		
		
		// output the results
		PrintWriter out = response.getWriter();
		out.print(TransformManager.transform(source));
	
	} // end doPost method


} // end class defintion

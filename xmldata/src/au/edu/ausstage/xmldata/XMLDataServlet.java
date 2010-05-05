/*
 * This file is part of the AusStage XML Data Manager Service
 *
 * The AusStage XML Data Manager Service is free software: you can 
 * redistribute it and/or modify it under the terms of the GNU General 
 * Public License as published by the Free Software Foundation, either 
 * version 3 of the License, or (at your option) any later version.
 *
 * The AusStage XML Data Manager Service is distributed in the hope 
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the 
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the AusStage XML Data Manager Service. 
 * If not, see <http://www.gnu.org/licenses/>.
 */

package au.edu.ausstage.terminator;

// import additional classes
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

/**
 * The main driving class for the this service
 */
public class XMLDataServlet extends HttpServlet {

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

		// get the parameters
		String xmlFileName = request.getParameter("file");  // parameter to name which XML file to query
		String xPathString = request.getParameter("xpath"); // xpath parameter to use as the query

		// check the parameters

		/*
		 * Check for null values
		 * trim the parameters
		 * check for empty strings
		 */

		 // build file path
		 
		 /*
		  * need to take into account the path seperator as well before joining the two variables
		  */
		  
		 String rootDirectory = servletConfig.getServletContext().getInitParameter("rootDirectory");
		 String fullPathToXML = rootDirectory + xmlFileName;

		 // add root directory to file name

		 // check the file
		 File xmlFile = new File(fullPathToXML);

		 /*
		 * check that the file exists
		 * check that the file can be read
		 */

		 // get an instance of the XMLDataManager object
		 XMLDataManager dataManager = new XMLDataManager()

		 // execute the query against the XML file and get the results
		 String results = dataManager.executeQuery(xmlFile, xPathString);

		// return the results to the browser
		// set the appropriate content type
		response.setContentType("text/xml; charset=UTF-8");

		// set the appropriate headers to disable caching
		// particularly for IE
		response.setHeader("Cache-Control", "max-age=0,no-cache,no-store,post-check=0,pre-check=0");

		//get the output print writer
		PrintWriter out = response.getWriter();

		// send some output
		out.print(results);		 

	} // end doGet method

	/**
	 * Method to respond to a post request
	 *
	 * @param request a HttpServletRequest object representing the current request
	 * @param response a HttpServletResponse object representing the current response
	 */
	public void doPost (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		// shouldn't use post requests with this service
		throw new ServletException("Incorrect request type detected");

	} // end doPost method



} // end class definition

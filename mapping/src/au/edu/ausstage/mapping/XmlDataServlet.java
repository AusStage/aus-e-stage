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
 * The main driving class for the Mapping Service
 */
public class XmlDataServlet extends HttpServlet {

	// declare private variables
	private ServletConfig servletConfig;
	
	// valid data types
	private String[] validTypes = {"abs-data"};

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
	
		// get the type
		String type = request.getParameter("type");
		String id   = request.getParameter("id");
		
		// check the action parameter
		if(type == null) {
			throw new ServletException("Missing type parameter");
		}
		
		// double check the type parameter
		boolean validType = false;
		for (int i = 0; i < validTypes.length; i++) {
			if(validTypes[i].equals(type)) {
				validType = true;
				i = validTypes.length + 1;
			}
		}
		
		if(validType != true) {
			throw new ServletException("Invalid type parameter");
		}
		
		// double check the id parameter
		if(id == null) {
			throw new ServletException("Missing id parameter");
		} else {
			id = id.trim();			
			if(id.equals("")) {
				throw new ServletException("Missing id parameter");
			}
		}
		
		// get the parent directory of the XML data file location
		String xmlDataStore = servletConfig.getServletContext().getInitParameter("xmlDataStore");
		
		// variable to hold the results
		String results = null;
		
		// determine what action to process
		if(type.equals("abs-data")) {
		
			// determine which file to use based on the state index
			int state = 0;
			try {
				state = Integer.parseInt(id.substring(0, 1));
			} catch (Exception ex) {
				throw new ServletException("Malformed id number detected");
			}
			
			// choose the right file
			String xmlFileName = null;
			
			switch(state) {
				case 1:
					xmlFileName = "abs-data/abs-data-nsw.xml";
					break;
				case 2:
					xmlFileName = "abs-data/abs-data-vic.xml";
					break;
				case 3:
					xmlFileName = "abs-data/abs-data-qld.xml";
					break;
				case 4:
					xmlFileName = "abs-data/abs-data-sa.xml";
					break;
				case 5:
					xmlFileName = "abs-data/abs-data-wa.xml";
					break;
				case 6:
					xmlFileName = "abs-data/abs-data-tas.xml";
					break;
				case 7:
					xmlFileName = "abs-data/abs-data-nt.xml";
					break;
				case 8:
					xmlFileName = "abs-data/abs-data-act.xml";
					break;
				default:
					throw new ServletException("Unable to determine the appropriate XML file to use");			
			}
			
			// get an XmlDataManager object
			XmlDataManager xmlData = new XmlDataManager(xmlDataStore);
			
			// check to see if the file is valid
			if(xmlData.validatePath(xmlFileName) == false) {
				throw new ServletException("Unable to access the required XML file");
			}
			
			// get the abs data
			results = xmlData.getAbsData(id);
			
			// check on the results
			if(results == null || results.equals("")) {
				throw new ServletException("Invalid collection district id detected");
			}		
		}
		
		// ouput the html
		// set the appropriate content type
		response.setContentType("text/html; charset=UTF-8");
		
		// set the appropriate headers to disable caching
		// particularly for IE
		response.setHeader("Cache-Control", "max-age=0,no-cache,no-store,post-check=0,pre-check=0");
		
		//get the output print writer
		PrintWriter out = response.getWriter();
		
		// ouput the start of the page
		out.print("<html><head></head><body>");
		
		// send some output
		out.print(results);
		
		// output the end of the page
		out.print("</body></html>");
	
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

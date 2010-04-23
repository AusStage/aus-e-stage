/*
 * This file is part of the AusStage Terminator Service
 *
 * The AusStage Terminator Service is free software: you can 
 * redistribute it and/or modify it under the terms of the GNU General 
 * Public License as published by the Free Software Foundation, either 
 * version 3 of the License, or (at your option) any later version.
 *
 * The AusStage Terminator Service is distributed in the hope 
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the 
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the AusStage Terminator Service.  
 * If not, see <http://www.gnu.org/licenses/>.
*/ 

package au.edu.ausstage.terminator;

// import additional classes
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

/**
 * The main driving class for the Mapping Service
 */
public class TerminatorServlet extends HttpServlet {

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
		
		// determine what action to perform
		if(action.equals("execute")) {
			// execute the identified script
			
			// check on the status of the session
			HttpSession session = request.getSession(false); 
			
			if(session == null) {
				throw new ServletException("Unauthorised access attempt detected");
			}
			
			if((Boolean)session.getAttribute("authenticated") != true) {
				throw new ServletException("Unauthorised access attempt detected");
			}
			
			// get the id parameter
			String id = request.getParameter("id");
			
			// check the id parameter
			if(id == null) {
				throw new ServletException("Missing id parameter");
			}
			
			try {
				Integer.parseInt(id);
			} catch (NumberFormatException ex) {
				throw new ServletException("ID parameter must be an integer");
			}
			
			// declare helper variables
			ScriptManager scripts = new ScriptManager(servletConfig);
			
			String results = scripts.executeById(id);
			
			// send the email message
			try {
				MailManager mail = new MailManager(servletConfig, request);
				mail.sendMail("A script was successfully executed by the AusStage Terminator.\n\n" + results, "Message from the AusStage Terminator");			
			} catch (Exception ex) {
				results += "<p><strong>Note: </strong>Sending of the status email failed</p>";
				System.out.println(ex.toString());
			}
			
			// ouput the results
			// set the appropriate content type
			response.setContentType("text/plain; charset=UTF-8");
			
			// set the appropriate headers to disable caching
			// particularly for IE
			response.setHeader("Cache-Control", "max-age=0,no-cache,no-store,post-check=0,pre-check=0");
			
			//get the output print writer
			PrintWriter out = response.getWriter();
			
			// send some output
			out.print(results);
			
			
		} else {
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
	
		// get the action parameter
		String action = request.getParameter("action");
		
		// check the action parameter
		if(action == null) {
			throw new ServletException("Missing action parameter");
		}
		
		// determine what action to perform
		if(action.equals("auth")) {
		
			// undertake authentication
			String authToken = request.getParameter("auth_token");
			
			if(authToken == null) {
				throw new ServletException("Missing auth_token parameter");
			}
			
			// get the security token for comparison
			String securityToken = servletConfig.getServletContext().getInitParameter("securityToken");
			
			if(securityToken == null) {
				throw new ServletException("Unable to load securityToken init parameter");
			}
			
			// declare helper variable
			String results = null;
			AuthenticationManager auth = new AuthenticationManager();
			
			if(auth.checkAuthToken(authToken, securityToken) == true) {
				// get a new session
				HttpSession session = request.getSession(true); 
				
				// add a value to the session
				session.setAttribute("authenticated", true);
				
				// get a list of available scripts
				ScriptManager scripts = new ScriptManager(servletConfig);
				
				results = scripts.getScriptList();
				
			} else {
				throw new ServletException("Authentication failed");
			}
			
			// ouput the results
			// set the appropriate content type
			response.setContentType("text/plain; charset=UTF-8");
			
			// set the appropriate headers to disable caching
			// particularly for IE
			response.setHeader("Cache-Control", "max-age=0,no-cache,no-store,post-check=0,pre-check=0");
			
			//get the output print writer
			PrintWriter out = response.getWriter();
			
			// send some output
			out.print(results);			
			
		} else if(action.equals("new_token")) {
			// generate a new authentication token
			
			// check on the status of the session
			HttpSession session = request.getSession(false); 
			
			if(session == null) {
				throw new ServletException("Unauthorised access attempt detected");
			}
			
			if((Boolean)session.getAttribute("authenticated") != true) {
				throw new ServletException("Unauthorised access attempt detected");
			}
			
			// get the new token parameter
			String authToken = request.getParameter("auth_token");
			
			// check on the authToken
			if(authToken == null) {
				throw new ServletException("Missing auth_token parameter");
			}
			
			// declare helper variables
			AuthenticationManager auth = new AuthenticationManager();
			
			String results = auth.hashNewToken(authToken);
			
			// ouput the results
			// set the appropriate content type
			response.setContentType("text/plain; charset=UTF-8");
			
			// set the appropriate headers to disable caching
			// particularly for IE
			response.setHeader("Cache-Control", "max-age=0,no-cache,no-store,post-check=0,pre-check=0");
			
			//get the output print writer
			PrintWriter out = response.getWriter();
			
			// send some output
			out.print(results);	
			
			
		} else {
			// unknown action parameter
			throw new ServletException("Unknown action parameter value");
		}
			
	} // end doPost method
	
} // end class definition

/*
 * This file is part of the AusStage Audience Participation Service
 *
 * The AusStage Audience Participation Service is free software: you can 
 * redistribute it and/or modify it under the terms of the GNU General 
 * Public License as published by the Free Software Foundation, either 
 * version 3 of the License, or (at your option) any later version.
 *
 * The AusStage Audience Participation Service is distributed in the hope 
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the 
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the AusStage Audience Participation Service.  
 * If not, see <http://www.gnu.org/licenses/>.
*/

package au.edu.ausstage.mobile;

// import additional classes
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.Properties;
import java.util.Date;

// import mail classes
import javax.mail.*;
import javax.mail.internet.*;

/**
 * A class used to control the flow of information into the system
 * by gathering audience feedback from a variety of different sources
 */
public class MailServlet extends HttpServlet {

	// declare private variables
	private ServletConfig servletConfig;

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
		
		// do not respond to get requests
		throw new ServletException("Invalid request type");
	
	} // end doGet method
	
	/**
	 * Method to respond to a post request
	 *
	 * @param request a HttpServletRequest object representing the current request
	 * @param response a HttpServletResponse object representing the current response
	 */
	public void doPost (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		// get what type of data is incoming
		String source = request.getParameter("source");
		
		// double check the paramerter
		if(source == null) {
			// invalid source
			throw new ServletException("Missing Source Parameter");
		}
		
		// determine what to do next
		if(source.equals("venue-error")) {
			// this is an error report from the venue page
			
			// get the additional components of the data
			String errorMessage = request.getParameter("error_message"); // message of the error
			String errorCode    = request.getParameter("error_code");	 // code of the error
			String errorSource  = request.getParameter("error_source");	 // source of the error
			
			// check on the parameters
			if(errorMessage == null) {
				// invalid parameter
				throw new ServletException("Missing Parameter: error_message");
			}
			
			if(errorCode == null) {
				// invalid parameter
				throw new ServletException("Missing Parameter: error_code");
			}
			
			if(errorSource == null) {
				// invalid parameter
				throw new ServletException("Missing Parameter: error_source");
			}
			
			// get additional headers
			String userAgent = request.getHeader("user-agent");
			
			// get the mail configuration options
			String mailHost = servletConfig.getServletContext().getInitParameter("mailHost");
			String mailTo   = servletConfig.getServletContext().getInitParameter("mailTo");
			String mailFrom = servletConfig.getServletContext().getInitParameter("mailFrom");
			
			// check on the parameters
			if(mailHost == null || mailTo == null || mailFrom == null) {
				throw new ServletException("Unable to get mail init parameters");
			}
			
			// Build the message
			StringBuilder message = new StringBuilder("An error has been reported from the " + servletConfig.getServletContext().getInitParameter("systemName") + "\n");
			
			// add the rest of the message
			message.append("Details are as follows:\n\n");
			message.append("Source: View map of venues\n");
			message.append("Message: " + errorMessage + "\n");
			message.append("Code: " + errorCode + "\n");
			message.append("Component: " + errorSource + "\n");
			message.append("User-Agent: " + userAgent + "\n");
			
			// initialise the JavaMail classes
			Properties systemProperties = System.getProperties();
			systemProperties.put("mail.smtp.host", mailHost);
			Session systemSession = Session.getInstance(systemProperties, null);
			
			try {
				// construct the message
				Message mailMessage = new MimeMessage(systemSession); // base object
				mailMessage.setFrom(new InternetAddress(mailFrom)); // set from address
				mailMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(mailTo, false)); // set to address
				mailMessage.setSubject("Error report from " + servletConfig.getServletContext().getInitParameter("systemName")); // subject
				mailMessage.setText(message.toString()); // body of the message
				
				// add additional headers
				mailMessage.setHeader("X-Mailer", "JavaMail");
				mailMessage.setSentDate(new Date());
				
				// send the message
			    Transport.send(mailMessage);
		    } catch(javax.mail.internet.AddressException ex) {
		    	throw new ServletException("Unable to prepare mail message");
		    } catch(javax.mail.MessagingException ex) {
		    	throw new ServletException("Unable to send mail message");
		    }
		    
		    // forward to the thank you page
		    RequestDispatcher requestDispatcher = request.getRequestDispatcher("/reporterror.jsp");
		    requestDispatcher.forward(request, response);
			
		} else {
			// invalid source
			throw new ServletException("Invalid Source Detected");
		}
	
	} // end doPost method

} // end class definition

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
import java.math.BigInteger;
import java.security.MessageDigest;
import javax.servlet.*;
import javax.servlet.http.*;

/**
 * A class used to control the flow of information into the system
 * by gathering audience feedback from a variety of different sources
 */
public class GathererServlet extends HttpServlet {

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
		
		// get what type of data is incoming
		String source = request.getParameter("source");
		
		// double check the paramerter
		if(source == null) {
			// invalid source
			throw new ServletException("Missing Source Parameter");
		}
		
		// determine what to do next
		if(source.equals("sms")) {
			throw new ServletException("SMS messages can only be processed as a HTTP post");
		} else if(source.equals("twitter")) {
			// get new twitter messages
			
			// get the debug parameter
			String debugParam = request.getParameter("debug");
			boolean debug = false;
			if(debugParam != null) {
				debug = true;
			}
			
			// get twitter details
			String twitterUser     = servletConfig.getServletContext().getInitParameter("twitterUser");
			String twitterPassword = servletConfig.getServletContext().getInitParameter("twitterPassword");
			
			// get an instance of the Twitter Gatherer class
			TwitterGatherer gatherer = new TwitterGatherer(twitterUser, twitterPassword);
			
			// declare other helper variables
			int mentionCount = 0; // number of new mentions
			String message = "";       // message to output 
			
			// get new mentions
			try {
				mentionCount = gatherer.getNewMentions();
			} catch (Exception e) {
				throw new ServletException("Unable to gather twitter messages", e);
			}
			
			if(mentionCount > 0) {
				try {
					if(debug == true) {
						// add mentions with debug
						message = gatherer.addNewMentions(true);
					} else {
						gatherer.addNewMentions(true);
					}
				} catch (Exception e) {
					throw new ServletException("Unable add the twitter messages to the database", e);
				}
			}
			
			// set the appropriate content type
			response.setContentType("text/plain; charset=UTF-8");
				
			//get the output print writer
			PrintWriter out = response.getWriter();
			
			if(debug == true) {
				// output more info
				out.write(Integer.toString(mentionCount) + "\n" + message);
			} else {
				// output basic info
				out.write("Number of tweets added: " + Integer.toString(mentionCount));
			}
		
		} else {
			// invalid source
			throw new ServletException("Invalid Source Detected");
		}
		
	
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
		if(source.equals("sms")) {
			// this is an incoming SMS message
			
			// get the additional components of the data
			String mobileNumber = request.getParameter("mobilenumber"); // number of sender
			String message      = request.getParameter("message");		// message from sender
			String dateTime     = request.getParameter("datetime");		// date and time message sent
			String md5Hash      = request.getParameter("hash");			// hash of parameters
			
			// check on the parameters
			if(mobileNumber == null) {
				// invalid parameter
				throw new ServletException("Missing Parameter: mobilenumber");
			}
			
			if(message == null) {
				// invalid parameter
				throw new ServletException("Missing Parameter: message");
			}
			
			if(dateTime == null) {
				// invalid parameter
				throw new ServletException("Missing Parameter: datetime");
			}
			
			if(md5Hash == null) {
				// invalid parameter
				throw new ServletException("Missing Parameter: hash");
			}
			
			// check the hash 
			String hashSecret = servletConfig.getServletContext().getInitParameter("hashSecret");
			
			if(hashSecret == null) {
				// invalid parameter
				throw new ServletException("Missing hash secret config variable");
			}
			
			// build the string to hash
			String toHash = mobileNumber + message + dateTime + hashSecret;
			
			// get an instance of the Message Digest class to do md5 hashing
			MessageDigest hasher = null;
			
			try {
				hasher = MessageDigest.getInstance("MD5");
				hasher.reset();
			} catch (Exception ex) {
				throw new ServletException("Unable to construct MessageDigest object", ex);
			}
			
			// hash the string
			hasher.update(toHash.getBytes());
			
			// get the results of the hash
			byte[] digest = hasher.digest();
			
			// turn the digest into a string, via a number
			BigInteger bigInt = new BigInteger(1, digest);
			String ourHash = bigInt.toString(16);
			
			// zero pad the hash as required
			while(ourHash.length() < 32) {
				ourHash = "0" + ourHash;
			}
			
			// check on the two hashes
			if(md5Hash.equals(ourHash)) {
			
				// go ahead and process the results
				
				/*
				 * debug code 
				 */
				
				// set the appropriate content type
				response.setContentType("text/plain; charset=UTF-8");
				
				//get the output print writer
				PrintWriter out = response.getWriter();
				
				// send some output
				out.println("Message received successfully for processing! :)");
				out.println("\nPhone Number: " + mobileNumber);
				out.println("Message: " + message);
				out.println("Date & Time: " + dateTime);
				out.println("Supplied Hash: " + md5Hash);
				out.println("Calculated Hash: " + ourHash);
			
			} else {
				// hashes do not match
				throw new ServletException("Supplied hash does not match calculated hash");
			}
			
		} else {
			// invalid source
			throw new ServletException("Invalid Source Detected");
		}
	
	} // end doPost method

} // end class definition

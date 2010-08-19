/*
 * This file is part of the AusStage Utilities Package
 *
 * The AusStage Utilities Package is free software: you can redistribute
 * it and/or modify it under the terms of the GNU General Public License 
 * as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * The AusStage Utilities Package is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the AusStage Utilities Package.  
 * If not, see <http://www.gnu.org/licenses/>.
*/

package au.edu.ausstage.utils;

// import the Apache Commons Email classes
import org.apache.commons.mail.*;

/**
 * A class used to send email
 */
public class EmailManager {

	// declare private variables
	private EmailOptions options = null;
	private final boolean DEBUG = false;
	
	/**
	 * Constructor for this class
	 *
	 * @param options a valid EmailOptions object
	 */
	public EmailManager(EmailOptions options) {
	
		if(options != null) {
			this.options = options;
		} else {
			throw new IllegalArgumentException("The EmailOptions object cannot be null");
		}
	
	} // end constructor
	
	/**
	 * A method to update the instance of the EmailOptions class used when
	 * sending email
	 *
	 * @param options a valid EmailOptions object
	 */
	public void updateOptions(EmailOptions options) {
	
		if(options != null) {
			this.options = options;
		} else {
			throw new IllegalArgumentException("The EmailOptions object cannot be null");
		}
	
	} // updateOptions method
	
	/**
	 * A method for sending a simple email message
	 *
	 * @param subject the subject of the message
	 * @param message the text of the message
	 *
	 * @return true, if and only if, the email is successfully sent
	 */
	public boolean sendSimpleMessage(String subject, String message) {
	
		// check the input parameters
		if(InputUtils.isValid(subject) == false || InputUtils.isValid(message) == false) {
			throw new IllegalArgumentException("The subject and message parameters cannot be null");
		}
		
		try {
			// define helper variables
			Email email = new SimpleEmail();
		
			// configure the instance of the email class
			email.setSmtpPort(options.getPortAsInt()); 
		
			// define authentication if required
			if(InputUtils.isValid(options.getUser()) == true) {
				email.setAuthenticator(new DefaultAuthenticator(options.getUser(), options.getPassword()));
			}
		
			// turn on / off debugging
			email.setDebug(DEBUG);
		
			// set the host name
			email.setHostName(options.getHost());
		
			// set the from email address
			email.setFrom(options.getFromAddress());
		
			// set the subject
			email.setSubject(subject);
		
			// set the message
			email.setMsg(message);
		
			// set the to address
			email.addTo(options.getToAddress());
		
			// set the security options
			if(options.getTLS() == true) {
				email.setTLS(true);
			}
		
			if(options.getSSL() == true) {
				email.setSSL(true);
			}
		
			// send the email
			email.send();
			
		} catch(EmailException ex) {
			if(DEBUG) {
				System.err.println("ERROR: Sending of email failed.\n" + ex.toString());
			}
			
			return false;
		}		
		return true;
	} // end sendSimpleMessage method
	
	/**
	 * A method for sending a simple email message
	 *
	 * @param subject the subject of the message
	 * @param message the text of the message
	 * @param attachmentPath the path to the attachment
	 *
	 * @return true, if and only if, the email is successfully sent
	 */
	public boolean sendMessageWithAttachment(String subject, String message, String attachmentPath) {
	
		// check the input parameters
		if(InputUtils.isValid(subject) == false || InputUtils.isValid(message) == false || InputUtils.isValid(attachmentPath) == false) {
			throw new IllegalArgumentException("All parameters are required");
		}
		
		try {
			// define helper variables
			MultiPartEmail email = new MultiPartEmail();
		
			// configure the instance of the email class
			email.setSmtpPort(options.getPortAsInt()); 
		
			// define authentication if required
			if(InputUtils.isValid(options.getUser()) == true) {
				email.setAuthenticator(new DefaultAuthenticator(options.getUser(), options.getPassword()));
			}
		
			// turn on / off debugging
			email.setDebug(DEBUG);
		
			// set the host name
			email.setHostName(options.getHost());
		
			// set the from email address
			email.setFrom(options.getFromAddress());
		
			// set the subject
			email.setSubject(subject);
		
			// set the message
			email.setMsg(message);
		
			// set the to address
			email.addTo(options.getToAddress());
		
			// set the security options
			if(options.getTLS() == true) {
				email.setTLS(true);
			}
		
			if(options.getSSL() == true) {
				email.setSSL(true);
			}
			
			// build the attachment
			EmailAttachment attachment = new EmailAttachment();
			attachment.setPath(attachmentPath);
			attachment.setDisposition(EmailAttachment.ATTACHMENT);
			attachment.setDescription("Sanitised Twitter Message");
			attachment.setName("twitter-message.txt");
			
			// add the attachment
			email.attach(attachment);
		
			// send the email
			email.send();
			
		} catch(EmailException ex) {
			if(DEBUG) {
				System.err.println("ERROR: Sending of email failed.\n" + ex.toString());
			}
			
			return false;
		}		
		return true;
	} // end sendSimpleMessage method


} // end class definition

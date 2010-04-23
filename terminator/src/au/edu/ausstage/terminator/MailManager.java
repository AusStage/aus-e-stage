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

// import additional packages
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.Properties;
import java.util.Date;

// import mail classes
import javax.mail.*;
import javax.mail.internet.*;

/**
 * A class to manage the sending of email
 */
public class MailManager {

	// declare private variables
	private ServletConfig servletConfig = null;
	private HttpServletRequest request = null;	

	/** 
	 * Constructor for this class
	 *
	 * @param config the ServletConfig object for this servlet
	 * @param request the ServletRequest object for the current call to this servler
	 */
	public MailManager(ServletConfig config, HttpServletRequest request) {
		// store reference for resuse
		servletConfig = config;
		this.request  = request;
	}
	
	/**
	 * A method to send an email using the preconfigured options
	 *
	 * @param messageText    the text of the message
	 * @param messageSubject the subject of the message
	 *
	 */
	public void sendMail(String messageText, String messageSubject) {
	
		if(messageText == null || messageSubject == null) {
			throw new RuntimeException("Both the text of the email and a subject are required");
		}
	
		// get additional info on the user
		String userAgent     = request.getHeader("user-agent");
		String userIPAddress = request.getRemoteAddr();
		String userHost      = request.getRemoteHost();
		
		// update the message text
		messageText += "\n--- Additional Info ---\n"
		             + "User Browser:  " + userAgent + "\n"
		             + "User IP Addr:  " + userIPAddress + "\n"
		             + "User Hostname: " + userHost + "\n";
		
		// get the mail configuration options
		String mailHost     = servletConfig.getServletContext().getInitParameter("mailHost");
		String mailTo       = servletConfig.getServletContext().getInitParameter("mailTo");
		String mailFrom     = servletConfig.getServletContext().getInitParameter("mailFrom");
		String mailUser     = servletConfig.getServletContext().getInitParameter("mailUser");
		String mailPassword = servletConfig.getServletContext().getInitParameter("mailPassword");
		String mailTls      = servletConfig.getServletContext().getInitParameter("mailTls");
		String mailSsl      = servletConfig.getServletContext().getInitParameter("mailSsl");
		String mailPort     = servletConfig.getServletContext().getInitParameter("mailPort");
		String mailDebug    = servletConfig.getServletContext().getInitParameter("mailDebug");
		
		// check on the parameters
		if(mailHost == null || mailTo == null || mailFrom == null) {
			throw new RuntimeException("Unable to get mail init parameters");
		}
		
		// prepare to use the JavaMail classes
		Properties mailProperties = System.getProperties();
		mailProperties.put("mail.transport.protocol", "smtp");
		mailProperties.put("mail.smtp.host", mailHost);
		
		// check to see if we need to use a non standard port
		if(mailPort != null && mailPort.equals("25") == false) {	
			mailProperties.put("mail.smtp.port", mailPort); 
		} else {
			mailProperties.put("mail.smtp.port", "25"); 
		}
		
		// check to see if we need to use SSL
		if(mailSsl != null && mailSsl.equals("yes")) {
			mailProperties.put("mail.smtp.ssl.enable", "true");
			mailProperties.put("mail.smtp.ssl.trust", "*");
			
			if(mailPort != null) {
				mailProperties.put("mail.smtp.socketFactory.port", mailPort); 
			} else {
				// assume default standard port for SSL SMTP
				mailProperties.put("mail.smtp.socketFactory.port", "465"); 
			}
			
			mailProperties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory"); 
			mailProperties.put("mail.smtp.socketFactory.fallback", "false"); 
		} else {
			mailProperties.put("mail.smtp.ssl.enable", "false");
		}
		
		// output debug messages
		if(mailDebug != null && mailDebug.equals("yes")) {
			mailProperties.put("mail.debug", "true"); 
		} else {
			mailProperties.put("mail.debug", "false");
		}
		
		// do we need to authenticate?
		if(mailUser != null && mailUser.equals("")) {
			mailProperties.put("mail.smtp.auth", "true"); 
		} else {
			mailProperties.put("mail.smtp.auth", "false");
		} 
		
		// do we need to use TLS?
		if(mailTls != null && mailTls.equals("yes")) {
			mailProperties.put("mail.smtp.starttls.enable","true");
		} else {
			mailProperties.put("mail.smtp.starttls.enable","false");
		}
		
		Session mailSession;
		
		// do we need to do authentication
		if(mailUser != null && mailUser.equals("")) {
			mailSession = Session.getInstance(mailProperties, new SMTPAuthenticator(mailUser, mailPassword));
		} else {
			mailSession = Session.getInstance(mailProperties, null);
		}
		
		try {
			// construct the message
			Message mailMessage = new MimeMessage(mailSession); // base object
			mailMessage.setFrom(new InternetAddress(mailFrom)); // set from address
			mailMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(mailTo, false)); // set to address
			mailMessage.setSubject(messageSubject); // subject
			mailMessage.setText(messageText); // body of the message
			
			// add additional headers
			mailMessage.setHeader("X-Mailer", "JavaMail");
			mailMessage.setSentDate(new Date());
			
			// send the message				
			Transport.send(mailMessage);
			
	    } catch(javax.mail.internet.AddressException ex) {
	    	throw new RuntimeException("Unable to prepare mail message", ex);
	    } catch(javax.mail.MessagingException ex) {
	    	throw new RuntimeException("Unable to send mail message:" + ex.toString());
	    }
	
	} // end sendMail method
	
	/**
	 * A private class to help with authentication of SMTP session
	 */
	private class SMTPAuthenticator extends javax.mail.Authenticator {
		
		// declare private variables
		private String username;
		private String password;
	
		/**
		 * Constructor for this class
		 *
		 * @param user username to use for authentication
		 * @param pass password to use for authentication
		 */
		public SMTPAuthenticator(String user, String pass) {
			super();
			username = user;
			password = pass;
		}
		
		/**
		 * Method used to provide authentication information to JavaMail classes
		 */
        public PasswordAuthentication getPasswordAuthentication() {
			
           return new PasswordAuthentication(username, password);
        }
    } // end class definition

} // end class definition

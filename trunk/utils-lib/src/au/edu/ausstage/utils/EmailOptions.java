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

/**
 * A class used to manage the options for sending emails and configuring the EmailManager class
 */
public class EmailOptions {

	// declare private class variables
	private String host        = null;
	private String user        = null;
	private String password    = null;
	private boolean ssl        = false;
	private boolean tls        = false;
	private String port        = "21";
	private String fromAddress = null;
	private String toAddress   = null;
	
	/*
	 * get and set methods
	 */
	 
	/**
	 * Set the address of the host to use
	 * @param value the new value
	 */
	public void setHost(String value) {
		
		// double check the value
		if(InputUtils.isValid(value) == false) {
			throw new IllegalArgumentException("The parameter to this method is required");
		} else {
			host = value;
		}
	}
	
	/**
	 * A method to get the host parameter
	 *
	 * @return the value requested
	 */
	public String getHost() {
		return host;
	}
	
	/**
	 * Set the user to use for authentication
	 * @param value the new value
	 */
	public void setUser(String value) {
		
		// double check the value
		if(InputUtils.isValid(value) == false) {
			throw new IllegalArgumentException("The parameter to this method is required");
		} else {
			user = value;
		}
	}
	
	/**
	 * A method to get the user name parameter
	 *
	 * @return the value requested
	 */
	public String getUser() {
		return user;
	}
	
	/**
	 * Set the password parameter
	 * @param value the new value
	 */
	public void setPassword(String value) {
		
		// double check the value
		if(InputUtils.isValid(value) == false) {
			throw new IllegalArgumentException("The parameter to this method is required");
		} else {
			password = value;
		}
	}
	
	/**
	 * A method to get the host parameter
	 *
	 * @return the value requested
	 */
	public String getPassword() {
		return password;
	}
	
	/**
	 * Set the SSL parameter
	 * @param value set to true if the use of SSL is required
	 */
	public void setSSL(boolean value) {
		
		ssl = value;
	}
	
	/**
	 * A method to get the SSL parameter
	 *
	 * @return the value requested
	 */
	public boolean getSSL() {
		return ssl;
	}
	
	/**
	 * Set the TLS parameter
	 * @param value set to true if the use of SSL is required
	 */
	public void setTLS(boolean value) {
		
		tls = value;
	}
	
	/**
	 * A method to get the SSL parameter
	 *
	 * @return the value requested
	 */
	public boolean getTLS() {
		return tls;
	}
	
	/**
	 * Set the port parameter
	 * @param value the new value
	 */
	public void setPort(String value) {
		
		// double check the value
		if(InputUtils.isValidInt(value) == false) {
			throw new IllegalArgumentException("The parameter to this method is required, and must be a valid integer");
		} else {
			port = value;
		}
	}
	
	/**
	 * A method to get the host parameter
	 *
	 * @return the value requested
	 */
	public String getPort() {
		return port;
	}
	
	/**
	 * Set the from address parameter
	 * @param value the new value
	 */
	public void setFromAddress(String value) {
		
		// double check the value
		if(InputUtils.isValid(value) == false) {
			throw new IllegalArgumentException("The parameter to this method is required");
		} else {
			fromAddress = value;
		}
	}
	
	/**
	 * A method to get the host parameter
	 *
	 * @return the value requested
	 */
	public String getFromAddress() {
		return fromAddress;
	}
	
	/**
	 * Set the to address parameter
	 * @param value the new value
	 */
	public void setToAddress(String value) {
		
		// double check the value
		if(InputUtils.isValid(value) == false) {
			throw new IllegalArgumentException("The parameter to this method is required");
		} else {
			toAddress = value;
		}
	}
	
	/**
	 * A method to get the host parameter
	 *
	 * @return the value requested
	 */
	public String getToAddress() {
		return toAddress;
	}
	
} // end class definition

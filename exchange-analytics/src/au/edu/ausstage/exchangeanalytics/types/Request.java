/* This file is part of the AusStage Exchange Analytics app
 *
 * The AusStage Exchange Analytics app is free software: you can redistribute
 * it and/or modify it under the terms of the GNU General Public License 
 * as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * The AusStage Exchange Analytics app is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the AusStage Exchange Analytics app.  
 * If not, see <http://www.gnu.org/licenses/>.
 */
 
package au.edu.ausstage.exchangeanalytics.types;

import au.edu.ausstage.utils.InputUtils;

/**
 * a class used to represent a request to the Data Exchange service
 */
public class Request {

	// private class level variables
	String date;
	String requestType;
	String id;
	String outputType = "html";
	String recordLimit = "10";
	String inetAddress;
	String referer;
	String callback = "n";
	
	/**
	 * constructor for this class
	 *
	 * @param date the time at which this request was made
	 *
	 * @throws IllegalArgumentException if the timestamp is empty
	 */
	public Request(String date) {
	
		if(InputUtils.isValid(date) == false) {
			throw new IllegalArgumentException("the timestamp parameter is required");
		}
		
		this.date = date;
	}
	
	/* get and set methods */
	public String getDate() {
		return date;
	}
	
	public void setRequestType(String value) {
		if(InputUtils.isValid(value) == false) {
			throw new IllegalArgumentException("the value parameter cannot be null or an empty string");
		}
		
		requestType = value;
	}
	
	public String getRequestType() {
		return requestType;
	}
	
	public void setId(String value) {
		if(InputUtils.isValid(value) == false) {
			throw new IllegalArgumentException("the value parameter cannot be null or an empty string");
		}
		
		id = value;
	}
	
	public String getId() {
		return id;
	}
	
	public void setOutputType(String value) {
		if(InputUtils.isValid(value) == false) {
			throw new IllegalArgumentException("the value parameter cannot be null or an empty string");
		}
		
		outputType = value;
	}
	
	public String getOutputType() {
		return outputType;
	}
	
	public void setRecordLimit(String value) {
		if(InputUtils.isValid(value) == false) {
			throw new IllegalArgumentException("the value parameter cannot be null or an empty string");
		}
		
		recordLimit = value;
	}
	
	public String getRecordLimit() {
		return recordLimit;
	}
	
	public void setInetAddress(String value) {
		if(InputUtils.isValid(value) == false) {
			throw new IllegalArgumentException("the value parameter cannot be null or an empty string");
		}
		
		inetAddress = value;
	}
	
	public String getInetAddress() {
		return inetAddress;
	}
	
	public void setReferer(String value) {
		if(InputUtils.isValid(value) == false) {
			throw new IllegalArgumentException("the value parameter cannot be null or an empty string");
		}
		
		referer = value;
	}
	
	public String getReferer() {
		return referer;
	}
	
	public void setCallback(String value) {
		if(InputUtils.isValid(value) == false) {
			throw new IllegalArgumentException("the value parameter cannot be null or an empty string");
		}
		
		callback = value;
	}
	
	public String getCallback() {
		return callback;
	}
	
	public String[] getParameters() {
	
//		String date;
//		String requestType;
//		String id;
//		String outputType = "html";
//		String recordLimit = "10";
//		String inetAddress;
//		String referer;
//		String callback = "n";
	
		String[] parameters = new String[8];
		
		parameters[0] = date;
		parameters[1] = requestType;
		parameters[2] = id;
		parameters[3] = outputType;
		parameters[4] = recordLimit;
		parameters[5] = inetAddress;
		parameters[6] = referer;
		parameters[7] = callback;
		
		return parameters;
		
	}
}

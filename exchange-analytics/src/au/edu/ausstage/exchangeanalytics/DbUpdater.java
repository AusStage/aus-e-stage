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
 
package au.edu.ausstage.exchangeanalytics;

import au.edu.ausstage.utils.InputUtils;
import au.edu.ausstage.utils.DbObjects;
import au.edu.ausstage.exchangeanalytics.types.Request;

import java.util.ArrayList;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * class used to update the database
 */
public class DbUpdater {

	/**
	 * add new event requests to the database
	 *
	 * @param requests the list of event requests
	 * @param database the DbManager used to connect to the database
	 */
	public static void addEventRequests(ArrayList<Request> requests, DbManager database) throws java.sql.SQLException {
	
		addRequests("event_requests", requests, database);
	}
	
	/**
	 * add new resource requests to the database
	 *
	 * @param requests the list of event requests
	 * @param database the DbManager used to connect to the database
	 */
	public static void addResourceRequests(ArrayList<Request> requests, DbManager database) throws java.sql.SQLException {
	
		addRequests("resource_requests", requests, database);
	}
	
	/**
	 * add new feedback requests to the database
	 *
	 * @param requests the list of event requests
	 * @param database the DbManager used to connect to the database
	 */
	public static void addFeedbackRequests(ArrayList<Request> requests, DbManager database) throws java.sql.SQLException {
	
		addRequests("feedback_requests", requests, database);
	}
	
	/*
	 * private method used to add data to the database
	 */
	private static void addRequests(String table, ArrayList<Request> requests, DbManager database) throws java.sql.SQLException {
	
		String sql = "INSERT INTO " + table + " (request_date, request_type, id_value, output_type, record_limit, ip_address, referer, callback) "
		           + "VALUES (?,?,?,?,?,?,?,?)";
		           
		String[] parameters;
		
		Request request;
		
		for(int i = 0; i < requests.size(); i++) {
			request = (Request)requests.get(i);
			
			parameters = request.getParameters();
			
			database.executePreparedInsertStatement(sql, parameters);		
		}
	}
}

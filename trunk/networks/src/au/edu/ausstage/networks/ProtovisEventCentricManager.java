/*
 * This file is part of the AusStage Navigating Networks Service
 *
 * The AusStage Navigating Networks Service is free software: you can redistribute
 * it and/or modify it under the terms of the GNU General Public License 
 * as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * The AusStage Navigating Networks Service is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the AusStage Navigating Networks Service.  
 * If not, see <http://www.gnu.org/licenses/>.
*/

package au.edu.ausstage.networks;

// import additional libraries
//jena
import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.tdb.TDBFactory;

//json
import org.json.simple.*;

// general java
import java.util.*;

// import AusStage related packages
import au.edu.ausstage.vocabularies.*;
import au.edu.ausstage.utils.*;
import au.edu.ausstage.networks.types.*;

/**
 * A class to manage the export of information
 */
public class ProtovisEventCentricManager {

	// declare private class level variables
	private DataManager RdfDatabase = null;
	private DbManager   database    = null;

	/**
	 * Constructor for this class
	 *
	 * @param RdfDatabase an already instantiated instance of the DataManager class
	 */
	public ProtovisEventCentricManager(DataManager RdfDatabase) {	
		// store a reference to this DataManager for later
		this.RdfDatabase = RdfDatabase;
	} // end constructor
	
	/**
	 * A method to build the JSON object required by the Protovis visualisation library
	 *
	 * @param id     the unique identifier of the collaborator
	 * @param radius the number of edges from the central node
	 *
	 * @return the JSON encoded data for use with Protovis
	 */
	@SuppressWarnings("unchecked")
	public String getData(String id, int radius) {
	
		// check on the parameters
		if(InputUtils.isValidInt(id) == false) {
			throw new IllegalArgumentException("Error: the id parameter is required");
		}
		
		if(InputUtils.isValidInt(radius, ExportServlet.MIN_DEGREES, ExportServlet.MAX_DEGREES) == false) {
			throw new IllegalArgumentException("Error: the radius parameter must be between " + ExportServlet.MIN_DEGREES + " and " + ExportServlet.MAX_DEGREES);
		}
		
		if(radius != 1) {
			throw new RuntimeException("Error: event-centric networks are currently limited to a radius of one");
		}
		
		// instantiate a connection to the database
		database = new DbManager(getConnectionString());
		
		if(database.connect() == false) {
			throw new RuntimeException("Error: Unable to connect to the database");
		}
		
		/*
		 * declare helper variables
		 */
		ArrayList<Collaborator> collaborators = new ArrayList<Collaborator>();
		Collaborator            collaborator  = null;
		
		/*
		 * get all of the contributors that have worked on this event
		 */
		// define the sql
		String sql = "SELECT DISTINCT contributorid "
				   + "FROM conevlink "
				   + "WHERE eventid = ?";
				   
		// define the paramaters
		String[] sqlParameters = {id};

		// get the data
		DbObjects results = database.executePreparedStatement(sql, sqlParameters);
		
		// check to see that data was returned
		if(results == null) {
			// return an empty JSON Array
			return new JSONArray().toString();
		}
		
		// build the starting list of contributors
		java.sql.ResultSet resultSet = results.getResultSet();
		
		try {
			while(resultSet.next()) {
				// create a new collaborator
				collaborator = new Collaborator(resultSet.getString(1));
				collaborators.add(collaborator);
			}
			
		} catch (java.sql.SQLException ex) {
			// return an empty JSON Array
			return new JSONArray().toString();
		}
		
		
		
		
		//debug
		return "";
	
	} // end the getData method
	
	/**
	 * A private method to get the details for an event
	 *
	 * @param id the event id
	 *
	 * @return a completed event object
	 */
	private au.edu.ausstage.networks.types.Event getEventDetails(String id) {
	
		// define the sql
		String sql = "";
		
		return null;	
	}
	
	/**
	 * A private method to read the connection string parameter from the web.xml file
	 *
	 * @return the database connection string
	 */
	private String getConnectionString() {
		if(InputUtils.isValid(RdfDatabase.getContextParam("databaseConnectionString")) == false) {
			throw new RuntimeException("Unable to read the connection string parameter from the web.xml file");
		} else {
			return RdfDatabase.getContextParam("databaseConnectionString");
		}
	}
}

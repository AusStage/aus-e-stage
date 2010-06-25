/*
 * This file is part of the AusStage RDF Export App
 *
 * The AusStage RDF Export App is free software: you can 
 * redistribute it and/or modify it under the terms of the GNU General 
 * Public License as published by the Free Software Foundation, either 
 * version 3 of the License, or (at your option) any later version.
 *
 * The AusStage RDF Export App is distributed in the hope 
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the 
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the AusStage RDF Export App.  
 * If not, see <http://www.gnu.org/licenses/>.
*/

// include the class in our package
package au.edu.ausstage.rdfexport;

// import additional packages
import java.sql.*;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

// import the Jena related packages
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.tdb.*;

// import the vocabularies
import au.edu.ausstage.vocabularies.*;

/**
 * A Class used to build an RDF based dataset of contributor information
 * as an export from the AusStage database and used as a data source for the 
 * Navigating Networks component
 */
public class BuildNetworkData {

	// declare private class level variables
	private DatabaseManager        database;             // access the AusStage database
	private PropertiesManager      settings;             // access the properties / settings
	private String                 datastorePath = null; // directory for the tdb datastore
	
	/**
	 * A constructor for this class
	 *
	 * @param dataManager the DatabaseManager class connected to the AusStage database
	 * @param properties  the PropertiesManager providing access to properties and settings
	 */
	public BuildNetworkData(DatabaseManager dataManager, PropertiesManager properties) {
		
		// double check the parameters
		if(dataManager == null || properties == null) {
			throw new IllegalArgumentException("ERROR: The parameters to the BuildNetworkData constructor cannot be null");
		}
		
		// store references to these objects for later
		database = dataManager;
		settings = properties;
	}
	
	/**
	 * A method to reset the TDB datastore directory
	 *
	 * @return true if, and only if, the reset was sucessful
	 *
	 */
	public boolean doReset() {
	
		// keep the user informed
		System.out.println("INFO: Deleting the existing TDB datastore...");
	
		// get the path to the datastore
		datastorePath = settings.getProperty("tdb-datastore");
		
		// check the path
		if(datastorePath == null) {
			System.err.println("ERROR: Unable to load the tdb-datastore property");
			return false;
		}
		
		// instantiate a file object
		File datastore = new File(datastorePath);
		
		// check on the datastore
		if(datastore.exists() == false || datastore.canRead() == false || datastore.isDirectory() == false || datastore.canWrite() == false) {
			System.err.println("ERROR: Unable to access the specified datastore directory");
			System.err.println("       " + datastore.getAbsolutePath());
			return false;
		}
		
		// delete any files in the directory
		File[] tdbFiles = datastore.listFiles(new FileListFilter());
		
		// loop through the list of files and delete them
		for(File tdbFile : tdbFiles) {
		
			try {
				boolean status = tdbFile.delete();
				
				if(status == false) {
					System.err.println("ERROR: Unable to delete the following file:");
					System.err.println("       " + tdbFile.getAbsolutePath());
					return false;
				}
			} catch (SecurityException ex) {
				System.err.println("ERROR: Unable to delete the following file:");
				System.err.println("       " + tdbFile.getAbsolutePath());
				return false;
			}		
		}
		
		// create the basic optimisation file the TDB engine
		File opt = new File(datastorePath + "/fixed.opt");
		
		try {
			opt.createNewFile();
		} catch (IOException ex) {
			System.err.println("WARN: Unable to create the TDB optimisation file:");
			System.err.println("       " + opt.getAbsolutePath());
		} catch (SecurityException ex) {
			System.err.println("WARN: Unable to create the TDB optimisation file:");
			System.err.println("       " + opt.getAbsolutePath());
		}
		
		// if we get this far everything is ok
		System.out.println("INFO: Existing TDB datastore deleted");
		return true;
		
	} // end the doReset method
	
	/**
	 * A method to undertake the task of building the dataset
	 *
	 * @return true if, and only if, the task completes successfully
	 */
	public boolean doTask() {
	
		// turn off the TDB logging
		org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger("com.hp.hpl.jena.tdb.info");
		logger.setLevel(org.apache.log4j.Level.OFF);
		
		// declare some helper variables
		int contributorCount   = 0;
		int collaborationCount = 0;
		int eventCount         = 0;
		java.util.Map<String, Resource> contributors = new java.util.HashMap<String, Resource>();		
	
		// create an empty persistent model
		Model model = null;
		
		if(datastorePath == null) {
			System.err.println("ERROR: the doReset() method must be run before building the network datastore");
			return false;
		} else {
			model = TDBFactory.createModel(datastorePath) ;
		}
		
		// set a namespace prefixes
		model.setNsPrefix("foaf"    , FOAF.NS);
		model.setNsPrefix("event"   , Event.NS);
		model.setNsPrefix("dcterms" , DCTerms.NS);
		
		/*
		 * add base contributor information
		 */
		 	   
		try {
		
			// keep the user informed
			System.out.println("INFO: Adding contributor data to the datastore...");
			
			// define the sql
			String sql = "SELECT contributorid, first_name, last_name FROM contributor";
			
			// get the data from the database				   
			java.sql.ResultSet resultSet = database.executeStatement(sql);
	
			// loop through the 
			while (resultSet.next()) {
			
				// get the attributes
				String firstName = resultSet.getString(2);
				String lastName  = resultSet.getString(3);
				
				// double check the attributes
				if(firstName == null) {
					firstName = "";
				} 
				
				if(lastName == null) {
					lastName = "";
				}
	
				// create a new contributor
				Resource contributor = model.createResource(AusStageURI.getContributorURI(resultSet.getString(1)));
				contributor.addProperty(RDF.type, FOAF.Person);
				contributor.addProperty(FOAF.name, firstName + " " + lastName);
				contributor.addProperty(FOAF.givenName, firstName);
				contributor.addProperty(FOAF.familyName, lastName);
				contributor.addProperty(FOAF.page, AusStageURI.getContributorURL(resultSet.getString(1)));
				
				// store a reference to this contributor
				contributors.put(resultSet.getString(1), contributor);
				
				// increment the counter
				contributorCount++;				
			}
			
			// play nice and tidy up
			resultSet.close();
			database.closeStatement();
			System.out.println("INFO: " + contributorCount +   " contributors successfully added to the datastore");
			
		} catch (java.sql.SQLException sqlEx) {
			System.err.println("ERROR: An SQL related error has occured");
			System.err.println("       " + sqlEx.getMessage());
			return false;
		}
		
		/*
		 * add relationships
		 */
		 	   
		try {
		
			// keep the user informed
			System.out.println("INFO: Adding collaboration relationships...");
			
			// declare helper variables
			String currentId = "";
			Resource contributor = null;
			
			// define the sql
			String sql = "SELECT DISTINCT contributorid, c1.collaborator "
					   + "FROM conevlink, (SELECT eventid, contributorid AS collaborator FROM conevlink WHERE contributorid IS NOT NULL) c1  "
					   + "WHERE conevlink.eventid = c1.eventid "
					   + "AND contributorid IS NOT NULL "
					   + "ORDER BY contributorid ";
			
			// get the data from the database				   
			java.sql.ResultSet resultSet = database.executeStatement(sql);
	
			// loop through the 
			while (resultSet.next()) {
			
				// store a copy of the current id, so we don't have to go through the 
				// collection of contributors too much
				if(currentId.equals(resultSet.getString(1)) == false) {
					// store this id
					currentId = resultSet.getString(1);
					
					// lookup the contributor
					contributor = contributors.get(resultSet.getString(1));
				}
				
				// double check the contributor
				if(contributor == null) {
					// missing contributor
					System.out.println("WARN: Unable to locate contributor with id: " + resultSet.getString(1));
				} else {
				
					// don't add a relationship to itself
					if(currentId.equals(resultSet.getString(2)) == false) {
					
						// add the relationship
						contributor.addProperty(FOAF.knows, contributors.get(resultSet.getString(2)));
				
						// count the number of collaborations
						collaborationCount++;
					}
				}				
			}
			
			// play nice and tidy up
			resultSet.close();
			database.closeStatement();
			System.out.println("INFO: " + collaborationCount +   " collaboration relationships successfully added to the datastore");
			
		} catch (java.sql.SQLException sqlEx) {
			System.err.println("ERROR: An SQL related error has occured");
			System.err.println("       " + sqlEx.getMessage());
			return false;
		}
		
		/*
		 * add events
		 */
	 
		try {
		
			// keep the user informed
			System.out.println("INFO: Adding events...");
			
			// declare helper variables
			String currentId     = "";
			Resource contributor = null;
			Resource event       = null;
			
			// define the sql
			String sql = "SELECT DISTINCT e.eventid, e.event_name, c.contributorid "
					   + "FROM events e, conevlink c "
					   + "WHERE e.eventid = c.eventid "
					   + "AND e.eventid IS NOT NULL "
					   + "AND c.contributorid IS NOT NULL "
					   + "ORDER BY e.eventid";
			
			// get the data from the database				   
			java.sql.ResultSet resultSet = database.executeStatement(sql);
	
			// loop through the 
			while (resultSet.next()) {
			
				// have we seen this event id before?
				if(currentId.equals(resultSet.getString(1)) == true) {
					// yes we have
					// lookup the contributor
					contributor = contributors.get(resultSet.getString(3));
					
					// double check the contributor
					if(contributor == null) {
						// missing contributor
						System.out.println("WARN: Unable to locate contributor with id: " + resultSet.getString(3) + " associated with event with id: " + resultSet.getString(1));
					} else {
			
						// add the relationships
						contributor.addProperty(Event.isAgentIn, event);
						event.addProperty(Event.agent, contributor);
					}
					
				} else {
					// no we haven't so create a new event
					event = model.createResource(AusStageURI.getEventURI(resultSet.getString(1)));
					event.addProperty(RDF.type, Event.Event);
			
					// process the title
					String title = resultSet.getString(2);
					title = title.replaceAll("\r", " ");
					title = title.replaceAll("\n", " ");
					
					// add the title and Url
					event.addProperty(DCTerms.title, title);
					event.addProperty(DCTerms.identifier, AusStageURI.getEventURL(resultSet.getString(1)));
					
					// lookup the contributor
					contributor = contributors.get(resultSet.getString(3));
					
					// double check the contributor
					if(contributor == null) {
						// missing contributor
						System.out.println("WARN: Unable to locate contributor with id: " + resultSet.getString(3) + " associated with event with id: " + resultSet.getString(1));
					} else {
			
						// add the relationships
						contributor.addProperty(Event.isAgentIn, event);
						event.addProperty(Event.agent, contributor);
					}
							
					// increment the event count 
					eventCount++;
					
					// store this id
					currentId = resultSet.getString(1);
				}	
			}
			
			// play nice and tidy up
			resultSet.close();
			database.closeStatement();
			System.out.println("INFO: " + eventCount +   " events successfully added to the datastore");
			
		} catch (java.sql.SQLException sqlEx) {
			System.err.println("ERROR: An SQL related error has occured");
			System.err.println("       " + sqlEx.getMessage());
			return false;
		}
		
		
		
		

	
		// if we get this far, everything went OK
		return true;
	} // end the doTask method
	
	/**
	 * A class used to filter the list of files
	 * in the TDB directory
	 */
	class FileListFilter implements FilenameFilter {

		/**
		 * Method to test if the specified file matches the predetermined
		 * name and extension requirements
		 *
		 * @param dir the directory in which the file was found.
		 * @param filename the name of the file
		 *
		 * @return true if and only if the file should be included
		 */
		public boolean accept(File dir, String filename) {
	
			if(filename != null) {
				if(filename.equals(".") == false && filename.equals("..") == false) {
					return true;
				} else {
					return false;
				}		
			} else {
				return false;
			}
		}
	} // end FileListFilter class definition

} // end class definition



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
import java.sql.ResultSet;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

// import the Jena related packages
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.tdb.*;
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;

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
		int contributorCount       = 0;
		int functionCount          = 0;
		int collaboratorCount      = 0;
		int collaborationCount     = 0;
		int eventCount             = 0;
		int functionsAtEventsCount = 0;
		
		// map of contributors
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
		model.setNsPrefix("foaf"     , FOAF.NS);
		model.setNsPrefix("event"    , Event.NS);
		model.setNsPrefix("dcterms"  , DCTerms.NS);
		model.setNsPrefix("time"     , Time.NS);
		model.setNsPrefix("tl"       , Timeline.NS);
		model.setNsPrefix("xsd"      , XSDDatatype.XSD);
		model.setNsPrefix("ausestage", AuseStage.NS);
		
		/*
		 * add base contributor information
		 */
		 	   
		try {
		
			// keep the user informed
			System.out.println("INFO: Adding contributor data to the datastore...");
			
			// define the sql
			String sql = "SELECT c.contributorid, c.first_name, c.last_name, LOWER(g.gender), nationality "
					   + "FROM contributor c, gendermenu g "
					   + "WHERE c.gender = g.genderid(+)";
			
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
				
				// add the name properties
				contributor.addProperty(FOAF.name, firstName + " " + lastName);
				contributor.addProperty(FOAF.givenName, firstName);
				contributor.addProperty(FOAF.familyName, lastName);
				
				// add the url to the contributor page
				contributor.addProperty(FOAF.page, AusStageURI.getContributorURL(resultSet.getString(1)));
				
				// add the gender
				if(resultSet.getString(4) != null) {
					if(resultSet.getString(4).equals("male") == true || resultSet.getString(4).equals("female") == true) {
						contributor.addProperty(FOAF.gender, resultSet.getString(4));
					}
				}
				
				// add the nationality
				if(resultSet.getString(5) != null) {
					contributor.addProperty(AuseStage.nationality, resultSet.getString(5));
				}
				
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
		 * add functions
		 */
		 
		try {
		
			// keep the user informed
			System.out.println("INFO: Adding contributor functions...");
			
			// declare helper variables
			String currentId = "";
			Resource contributor = null;
			
			// define the sql
			String sql = "SELECT c.contributorid, cp.preferredterm "
					   + "FROM contributor c, contributorfunctpreferred cp, contfunctlink cl "
					   + "WHERE c.contributorid = cl.contributorid "
					   + "AND cl.contributorfunctpreferredid = cp.contributorfunctpreferredid";
			
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
				
					// add the function
					contributor.addProperty(AuseStage.function, resultSet.getString(2));
					
					// increment the count
					functionCount++;
				}				
			}
			
			// play nice and tidy up
			resultSet.close();
			database.closeStatement();
			System.out.println("INFO: " + functionCount + " contributor functions successfully added.");
			
		} catch (java.sql.SQLException sqlEx) {
			System.err.println("ERROR: An SQL related error has occured");
			System.err.println("       " + sqlEx.getMessage());
			return false;
		}
		
		/*
		 * add relationships
		 */
		 	   
//		try {
//		
//			// keep the user informed
//			System.out.println("INFO: Adding foaf:knows elements and collaborator counts...");
//			
//			// declare helper variables
//			String currentId = "";
//			Resource contributor = null;
//			
//			// define the sql
//			String sql = "SELECT DISTINCT contributorid, c1.collaborator "
//					   + "FROM conevlink, (SELECT eventid, contributorid AS collaborator FROM conevlink WHERE contributorid IS NOT NULL) c1  "
//					   + "WHERE conevlink.eventid = c1.eventid "
//					   + "AND contributorid IS NOT NULL "
//					   + "ORDER BY contributorid ";
//			
//			// get the data from the database				   
//			java.sql.ResultSet resultSet = database.executeStatement(sql);
//	
//			// loop through the 
//			while (resultSet.next()) {
//			
//				// store a copy of the current id, so we don't have to go through the 
//				// collection of contributors too much
//				if(currentId.equals(resultSet.getString(1)) == false) {
//					// store this id
//					currentId = resultSet.getString(1);
//					
//					// add the collaborator count
//					if(contributor != null) {
//						contributor.addProperty(AuseStage.collaboratorCount, Integer.toString(collaboratorCount));
//						
//						// reset the collaborator count
//						collaboratorCount = 0;
//					}
//					
//					// lookup the contributor
//					contributor = contributors.get(resultSet.getString(1));
//				}
//				
//				// double check the contributor
//				if(contributor == null) {
//					// missing contributor
//					System.out.println("WARN: Unable to locate contributor with id: " + resultSet.getString(1));
//				} else {
//				
//					// don't add a relationship to itself
//					if(currentId.equals(resultSet.getString(2)) == false) {
//					
//						// add the relationship
//						contributor.addProperty(FOAF.knows, contributors.get(resultSet.getString(2)));
//						
//						// increment the collaborator count
//						collaboratorCount++;
//				
//						// count the number of collaborations
//						collaborationCount++;
//					}
//				}				
//			}
//			
//			// play nice and tidy up
//			resultSet.close();
//			database.closeStatement();
//			System.out.println("INFO: " + collaborationCount +   " foaf:knows elements successfully added to the datastore");
//			
//		} catch (java.sql.SQLException sqlEx) {
//			System.err.println("ERROR: An SQL related error has occured");
//			System.err.println("       " + sqlEx.getMessage());
//			return false;
//		}
		
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
			String sql = "SELECT DISTINCT e.eventid, e.event_name, c.contributorid, "
					   + "                e.yyyyfirst_date, e.mmfirst_date, e.ddfirst_date, "
					   + "                e.yyyylast_date, e.mmlast_date, e.ddlast_date "
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
					
					// construct the dates
					String firstDate = null;
					String lastDate  = null;
					
					// first date
					if(resultSet.getString(4) != null) {
						firstDate = buildDate(resultSet.getString(4), resultSet.getString(5), resultSet.getString(6));
					}
					
					// last date
					if(resultSet.getString(7) != null) {
						lastDate = buildDate(resultSet.getString(7), resultSet.getString(8), resultSet.getString(9));
					}
					
					// check on the first date
					if(firstDate == null) {
						// inform user of error
						System.out.println("INFO: A valid first date for event: " + resultSet.getString(1)  + " could not be determined");
					} else {
					
						// adjust the last date if required
						if(lastDate == null) {
							lastDate = firstDate;
						}
						
						// add date information
						 
						// construct a new timeInterval resource
						Resource timeInterval = model.createResource(Time.Interval);

						// add the timeline properties
						// add the typed literals
						timeInterval.addProperty(Timeline.beginsAtDateTime, model.createTypedLiteral(firstDate, XSDDatatype.XSDdate));
						timeInterval.addProperty(Timeline.endsAtDateTime, model.createTypedLiteral(lastDate, XSDDatatype.XSDdate));

						// andd the timeInterval to the Event
						event.addProperty(Event.time, timeInterval);
					}
					
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
		
		/*
		 * Add functions at events
		 */
		 
		try {
		
			// keep the user informed
			System.out.println("INFO: Adding contributor functions at events...");
			
			// declare helper variables
			String currentId     = "";
			Resource contributor = null;
			
			// define the sql
			String sql = "SELECT cl.contributorid, cl.eventid, cfp.preferredterm "
					   + "FROM conevlink cl, contributorfunctpreferred cfp "
					   + "WHERE cl.eventid IS NOT NULL "
					   + "AND cl.contributorid IS NOT NULL "
					   + "AND cl.function = cfp.contributorfunctpreferredid "
					   + "ORDER BY contributorid";
			
			// get the data from the database				   
			java.sql.ResultSet resultSet = database.executeStatement(sql);
	
			// loop through the 
			while (resultSet.next()) {
			
				// have we seen this contributor before
				if(currentId.equals(resultSet.getString(1)) != true) {
					
					// no we haven't so look them up
					contributor = contributors.get(resultSet.getString(1));
					
					// store the id to reduce number of lookups
					currentId = resultSet.getString(1);
					
					// double check the contributor
					if(contributor == null) {
						System.out.println("WARN: Unable to locate contributor with id: " + resultSet.getString(1));
					}
				} else {
					// yes we have so use the found contributor if available
					if(contributor != null) {
					
						// construct a new collaborates resource
						Resource collaborated = model.createResource(AuseStage.collaboration);
						
						// add the on event property
						collaborated.addProperty(AuseStage.onEvent, model.createResource(AusStageURI.getEventURI(resultSet.getString(2))));
						collaborated.addProperty(AuseStage.functionAtEvent, resultSet.getString(3));
						
						// add the collaborates property to the contributor
						contributor.addProperty(AuseStage.hasCollaborated, collaborated);
						
						functionsAtEventsCount++;
					}
				}				
			}
			
			// play nice and tidy up
			resultSet.close();
			database.closeStatement();
			System.out.println("INFO: " + functionsAtEventsCount +   " contributor function at events added");
			
		} catch (java.sql.SQLException sqlEx) {
			System.err.println("ERROR: An SQL related error has occured");
			System.err.println("       " + sqlEx.getMessage());
			return false;
		}
		 
		
		
		
		
		
		// if we get this far, everything went OK
		return true;
	} // end the doTask method
	
	/**
	 * A method used to build a date from the components in the AusStage database
	 *
	 * @param year  the year component of the date
	 * @param month the month component of the date
	 * @param day   the day component of the month
	 *
	 * @return      a string containing the finalised date
	 */
	private String buildDate(String year, String month, String day) {
	
		// check for at least a year
		if(year != null) {
		 
			String date = year + "-" + month + "-" + day;
		 	date = date.replace("-null","");
			date = date.replace("null","");
			
			return date;
		} else {
			return "";
		}
	 
	} // end buildDate method
	
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



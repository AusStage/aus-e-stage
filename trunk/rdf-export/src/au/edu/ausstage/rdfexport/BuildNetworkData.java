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
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.GregorianCalendar;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

// import the Jena related packages
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.tdb.*;
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;


// datastore stats code
import com.hp.hpl.jena.tdb.solver.stats.Stats ;
import com.hp.hpl.jena.tdb.solver.stats.StatsCollector ;
import com.hp.hpl.jena.tdb.store.GraphTDB ;

// import the AusStage classes
import au.edu.ausstage.vocabularies.*;
import au.edu.ausstage.utils.*;

/**
 * A Class used to build an RDF based dataset of contributor information
 * as an export from the AusStage database and used as a data source for the 
 * Navigating Networks component
 */
public class BuildNetworkData {

	// declare private class level variables
	private DbManager              database;             // access the AusStage database
	private PropertiesManager      settings;             // access the properties / settings
	private String                 datastorePath = null; // directory for the tdb datastore
	
	// declare private class level constants
	private final int RECORD_NOTIFY_COUNT = 10000;
	
	// declare other private variables
	// pattern derived from the com.hp.hpl.jena.rdf.model.impl.Util class
	private final Pattern invalidContentPattern = Pattern.compile( "<|>|&|[\0-\37&&[^\n\t]]|\uFFFF|\uFFFE" );
	
	/**
	 * A constructor for this class
	 *
	 * @param dataManager the DatabaseManager class connected to the AusStage database
	 * @param properties  the PropertiesManager providing access to properties and settings
	 */
	public BuildNetworkData(DbManager dataManager, PropertiesManager properties) {
		
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
		if(InputUtils.isValid(datastorePath) == false) {
			System.err.println("ERROR: Unable to load the tdb-datastore property");
			return false;
		}
		
		// check on the datastore directory
		if(FileUtils.doesDirExist(datastorePath) == false) {
			System.err.println("ERROR: Unable to access the specified datastore directory");
			System.err.println("       " + datastorePath);
			return false;
		}
		
		// delete any files in the directory
		File datastore = new File(datastorePath);
		File[] tdbFiles = datastore.listFiles(new FileListFilter());
		
		// loop through the list of files and delete them
		for(File tdbFile : tdbFiles) {
		
			try {
				boolean status = tdbFile.delete();
				
				if(status == false) {
					System.err.println("ERROR: Unable to delete the following file:");
					System.err.println("       " + tdbFile.getCanonicalPath());
					return false;
				}
			} catch (IOException ex) {
				System.err.println("ERROR: Unable to delete one of the files in the datastore directory.");
			} catch (SecurityException ex) {
				System.err.println("ERROR: Unable to delete one of the files in the datastore directory.");
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
		int organisationCount      = 0;
		int rolesAtEventsCount     = 0;
		int recordNotifyDelimCount = 0;
		
		// sets of ids that we've processed to stop rogue resources
		gnu.trove.TIntHashSet contributors  = new gnu.trove.TIntHashSet();
		gnu.trove.TIntHashSet events        = new gnu.trove.TIntHashSet();
		gnu.trove.TIntHashSet organisations = new gnu.trove.TIntHashSet();

		// create an empty persistent model
		Model model = null;
		
		if(InputUtils.isValid(datastorePath) == false) {
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
		model.setNsPrefix("bio"      , Bio.NS);
		model.setNsPrefix("org"      , Org.NS);
		model.setNsPrefix("skos"     , SKOS.NS);
		
		/*
		 * add base contributor information
		 */
		 	   
		try {
		
			// keep the user informed
			System.out.println("INFO: Adding contributor data to the datastore...");
			
			// define the sql
			String sql = "SELECT c.contributorid, c.first_name, c.last_name, LOWER(g.gender), nationality, "
					   + "       c.yyyydate_of_birth, c.mmdate_of_birth, c.dddate_of_birth, "
					   + "       c.other_names "
					   + "FROM contributor c, gendermenu g, "
					   + "     (SELECT DISTINCT contributorid FROM conevlink WHERE eventid IS NOT NULL) ce "
					   + "WHERE c.gender = g.genderid(+) "
					   + "AND c.contributorid = ce.contributorid "
					   + "ORDER BY contributorid";
			
			// get the data from the database
			DbObjects dbObject = database.executeStatement(sql);				   
			java.sql.ResultSet resultSet = dbObject.getResultSet();
			
			// declare helper variables
			String   firstName   = null;
			String   lastName    = null;
			String   otherName   = null;
			String   nationality = null;
			Resource contributor = null;
			Resource bioBirth    = null;
	
			// loop through the 
			while (resultSet.next()) {
			
				// get the attributes
				firstName = resultSet.getString(2);
				lastName  = resultSet.getString(3);
				
				// double check the attributes
				if(firstName == null) {
					firstName = "";
				} 
				
				if(lastName == null) {
					lastName = "";
				}
				
				// double check the name variables
				try {
					// use this method that encodes entities to check the content of the title
					// if it throws an exception the title will not make it into the XML serialised content
					com.hp.hpl.jena.rdf.model.impl.Util.substituteEntitiesInElementContent(firstName);
				
				} catch(com.hp.hpl.jena.shared.CannotEncodeCharacterException ex) {
					// the title will not make it into the XML serialised content
					System.err.println("ERROR: A contributor first name contains invalid content that must be manually fixed.");
					System.err.println("       The contributor id is: " + resultSet.getString(1));
					System.err.println("       The contributor first name is: " + firstName);
				
					Matcher invalidContentMatch = invalidContentPattern.matcher(firstName);
					firstName = invalidContentMatch.replaceAll("");
				}
				
				try {
					// use this method that encodes entities to check the content of the title
					// if it throws an exception the title will not make it into the XML serialised content
					com.hp.hpl.jena.rdf.model.impl.Util.substituteEntitiesInElementContent(lastName);
				
				} catch(com.hp.hpl.jena.shared.CannotEncodeCharacterException ex) {
					// the title will not make it into the XML serialised content
					System.err.println("ERROR: A contributor last name contains invalid content that must be manually fixed.");
					System.err.println("       The contributor id is: " + resultSet.getString(1));
					System.err.println("       The contributor last name is: " + lastName);
				
					Matcher invalidContentMatch = invalidContentPattern.matcher(lastName);
					lastName = invalidContentMatch.replaceAll("");
				}
	
				// create a new contributor
				contributor = model.createResource(AusStageURI.getContributorURI(resultSet.getString(1)));
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
				
					nationality = resultSet.getString(5);
				
					try {
						// use this method that encodes entities to check the content of the title
						// if it throws an exception the title will not make it into the XML serialised content
						com.hp.hpl.jena.rdf.model.impl.Util.substituteEntitiesInElementContent(nationality);
				
					} catch(com.hp.hpl.jena.shared.CannotEncodeCharacterException ex) {
						// the title will not make it into the XML serialised content
						System.err.println("ERROR: A contributor nationality contains invalid content that must be manually fixed.");
						System.err.println("       The contributor id is: " + resultSet.getString(1));
						System.err.println("       The contributor nationality is: " + nationality);
				
						Matcher invalidContentMatch = invalidContentPattern.matcher(nationality);
						nationality = invalidContentMatch.replaceAll("");
					}
					contributor.addProperty(AuseStage.nationality, nationality);
				}
				
				// add the date of birth
				if(resultSet.getString(6) != null) {
					
					bioBirth = model.createResource(Bio.Birth);
					bioBirth.addProperty(Bio.date, DateUtils.buildDate(resultSet.getString(6), resultSet.getString(7), resultSet.getString(8)));	
					contributor.addProperty(Bio.event, bioBirth);
				}		
				
				// add other names
				if(resultSet.getString(9) != null) {
				
					otherName = resultSet.getString(9);
				
					try {
						// use this method that encodes entities to check the content of the title
						// if it throws an exception the title will not make it into the XML serialised content
						com.hp.hpl.jena.rdf.model.impl.Util.substituteEntitiesInElementContent(otherName);
				
					} catch(com.hp.hpl.jena.shared.CannotEncodeCharacterException ex) {
						// the title will not make it into the XML serialised content
						System.err.println("ERROR: A contributor other name contains invalid content that must be manually fixed.");
						System.err.println("       The contributor id is: " + resultSet.getString(1));
						System.err.println("       The contributor other name is: " + otherName);
				
						Matcher invalidContentMatch = invalidContentPattern.matcher(otherName);
						otherName = invalidContentMatch.replaceAll("");
					}
					contributor.addProperty(AuseStage.otherNames, otherName);
				}
				
				// store a reference to this contributor
				contributors.add(Integer.parseInt(resultSet.getString(1)));
				
				// increment the counter
				contributorCount++;				
			}
			
			// play nice and tidy up
			dbObject.tidyUp();
			dbObject = null;
			System.out.format("INFO: %,d contributors successfully added to the datastore%n", contributorCount);
			
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
					   + "FROM contributor c, contributorfunctpreferred cp, contfunctlink cl, "
					   + "     (SELECT DISTINCT contributorid FROM conevlink WHERE eventid IS NOT NULL) ce "
					   + "WHERE c.contributorid = cl.contributorid "
					   + "AND cl.contributorfunctpreferredid = cp.contributorfunctpreferredid "
					   + "AND c.contributorid = ce.contributorid "
					   + "ORDER BY contributorid";
			
			// get the data from the database				   
			DbObjects dbObject = database.executeStatement(sql);				   
			java.sql.ResultSet resultSet = dbObject.getResultSet();
	
			// loop through the 
			while (resultSet.next()) {
			
				// store a copy of the current id, so we don't have to go through the 
				// collection of contributors too much
				if(currentId.equals(resultSet.getString(1)) == false) {
					// store this id
					currentId = resultSet.getString(1);
					
					// lookup the contributor
					if(contributors.contains(Integer.parseInt(resultSet.getString(1))) == true) {
						contributor = model.createResource(AusStageURI.getContributorURI(resultSet.getString(1)));
						
						// add the function
						contributor.addProperty(AuseStage.function, resultSet.getString(2));
					
						// increment the count
						functionCount++;
					} else {
						// missing contributor
						System.err.println("WARN: Unable to locate contributor with id: " + resultSet.getString(1));
						contributor = null;
					}	
				} else {
					// add the function
					contributor.addProperty(AuseStage.function, resultSet.getString(2));
				
					// increment the count
					functionCount++;
				}						
			}
			
			// play nice and tidy up
			dbObject.tidyUp();
			dbObject = null;
			System.out.format("INFO: %,d contributor functions successfully added%n", functionCount);
			
			
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
			System.out.println("INFO: Adding collaborator relationships...");
			System.out.format("INFO: Each '#' below represents %,d collaborations%n", RECORD_NOTIFY_COUNT);
			
			// declare helper variables
			String   currentId     = "";
			Resource contributor   = null;
			Resource collaborator  = null;
			Resource collaboration = null;			
			String   firstDate     = null;
			String   lastDate      = null;
			
			// define the sql
			String sql = "SELECT c.contributorid, c1.collaboratorid, COUNT(c.contributorid) as collaborations, "
					   + "       MIN(CONCAT(e.yyyyfirst_date, CONCAT('-', CONCAT(e.mmfirst_date, CONCAT('-', e.ddfirst_date))))) as first_date, "
					   + "       MAX(CONCAT(e.yyyylast_date, CONCAT('-', CONCAT(e.mmlast_date, CONCAT('-', e.ddlast_date))))) as last_date "
					   + "FROM conevlink c, "
					   + "     (SELECT eventid, contributorid AS collaboratorid FROM conevlink WHERE contributorid IS NOT NULL AND eventid IS NOT NULL) c1, "
					   + "     events e "
					   + "WHERE c.eventid = c1.eventid "
					   + "AND c.contributorid IS NOT NULL "
					   + "AND c.eventid = e.eventid "
					   + "AND c.contributorid < c1.collaboratorid "
					   + "GROUP BY c.contributorid, c1.collaboratorid "
					   + "ORDER BY contributorid ";
			
			// get the data from the database				   
			DbObjects dbObject = database.executeStatement(sql);				   
			java.sql.ResultSet resultSet = dbObject.getResultSet();
	
			// loop through the 
			while (resultSet.next()) {
			
				// store a copy of the current id, so we don't have to go through the 
				// collection of contributors too much
				if(currentId.equals(resultSet.getString(1)) == false) {
					// store this id
					currentId = resultSet.getString(1);
					
					// add the collaborator count
					if(contributor != null) {
						//collaboration.addProperty(AuseStage.collaborationCount, model.createTypedLiteral(resultSet.getString(3), XSDDatatype.XSDinteger));
						contributor.addProperty(AuseStage.collaboratorCount, model.createTypedLiteral(Integer.toString(collaboratorCount), XSDDatatype.XSDinteger));

						// reset the collaborator count
						collaboratorCount = 0;
					}
										
					// lookup the contributor
					if(contributors.contains(Integer.parseInt(resultSet.getString(1))) == true) {
						contributor = model.createResource(AusStageURI.getContributorURI(resultSet.getString(1)));
					} else {
						// missing contributor
						System.err.println("WARN: Unable to locate contributor with id: " + resultSet.getString(1));
						contributor = null;
					}
				}
				
				// double check the contributor
				if(contributor != null) {
				
					// lookup the contributor
					if(contributors.contains(Integer.parseInt(resultSet.getString(2))) == true) {
						// get the collaborator
						collaborator = model.createResource(AusStageURI.getContributorURI(resultSet.getString(2)));
				
						// increment the collaborator count
						collaboratorCount++;
					
						// create a new collaboration
						collaboration = model.createResource(AusStageURI.getRelationshipURI(resultSet.getString(1) + "-" + resultSet.getString(2)));
						collaboration.addProperty(RDF.type, AuseStage.collaboration);
				
						// add the two collaborators
						collaboration.addProperty(AuseStage.collaborator, contributor);
						collaboration.addProperty(AuseStage.collaborator, collaborator);
				
						// add the count
						collaboration.addProperty(AuseStage.collaborationCount, model.createTypedLiteral(resultSet.getString(3), XSDDatatype.XSDinteger));
						//collaboration.addProperty(AuseStage.collaborationCount, resultSet.getString(3));
					
						// add the link to the contributors
						contributor.addProperty(AuseStage.hasCollaboration, collaboration);
						collaborator.addProperty(AuseStage.hasCollaboration, collaboration);
					
						// add the dates of the collaboration
						firstDate = resultSet.getString(4);
						lastDate  = resultSet.getString(5);
	
						// double check the dates
						if(firstDate == null) {
							System.err.println("WARN: A valid time period for '" + resultSet.getString(1) + "' & '" + resultSet.getString(2) + "' could not be determined");
						} else {
							// check on the format of the date
							if(firstDate.length() != 10) {
		
								// first date is shorter than expected
								if(firstDate.length() == 6) {
									// year only so get rid of the '--'
									firstDate = firstDate.substring(0, 4);
								} else if(firstDate.length() == 8) {
									// year and month parameter
									firstDate = firstDate.substring(0, 7);
								}									
							}
		
							// check on the last date
							if(lastDate == null) {
								lastDate = firstDate;
							} else if (lastDate.equals("--")) {
								lastDate = firstDate;
							} else if(lastDate.length() != 10) {									
								// last date is shorter than expected
								if(lastDate.length() == 6) {
									// year only so get rid of the '--'
									lastDate = lastDate.substring(0, 4);
								} else if(firstDate.length() == 8) {
									// year and month parameter
									lastDate = lastDate.substring(0, 7);
								}									
							}
										
							// add the time interval to the collaboration
							collaboration.addProperty(AuseStage.collaborationFirstDate, firstDate);
							collaboration.addProperty(AuseStage.collaborationLastDate, lastDate);
						}
					
						// count the number of collaborations
						collaborationCount++;
					
						// determine if we need to do a sync
						if ((collaborationCount % RECORD_NOTIFY_COUNT) == 0)
						{
							// keep the user informed
							System.out.print("#");
						
							// keep track of the number of syncs
							recordNotifyDelimCount++;
						
							if(recordNotifyDelimCount == 10) {
								System.out.print("|");
								recordNotifyDelimCount = 0;
							}
						}							
					
					} else {
						System.err.println("WARN: Unable to add the collaboration between '" + resultSet.getString(1) + "' & '" + resultSet.getString(2) + "'");					
					}
				}				
			}
			
			// play nice and tidy up
			dbObject.tidyUp();
			dbObject = null;
			
			System.out.format("%nINFO: %,d collaborations successfully added to the datastore%n", collaborationCount);
			
			
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
			System.out.println("INFO: Adding event data to the datastore...");
			
			// declare helper variables
			String   currentId    = "";
			Resource contributor  = null;
			Resource event        = null;
			Resource timeInterval = null;
			String   title        = null;
			String   firstDate    = null;
			String   lastDate     = null;
			
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
			DbObjects dbObject = database.executeStatement(sql);				   
			java.sql.ResultSet resultSet = dbObject.getResultSet();
	
			// loop through the 
			while (resultSet.next()) {
			
				// have we seen this event id before?
				if(currentId.equals(resultSet.getString(1)) == true) {
					// yes we have
					// lookup the contributor
					if(contributors.contains(Integer.parseInt(resultSet.getString(3))) == true) {
						contributor = model.createResource(AusStageURI.getContributorURI(resultSet.getString(3)));
						
						// add the relationships
						contributor.addProperty(Event.isAgentIn, event);
						event.addProperty(Event.agent, contributor);
						
					} else {
						// missing contributor
						System.err.println("WARN: Unable to locate contributor with id: " + resultSet.getString(3) + " associated with event with id: " + resultSet.getString(1));
						contributor = null;
					}
										
				} else {
					// no we haven't so create a new event
					event = model.createResource(AusStageURI.getEventURI(resultSet.getString(1)));
					event.addProperty(RDF.type, Event.Event);
					
					// check the event title
					try {
						// use this method that encodes entities to check the content of the title
						// if it throws an exception the title will not make it into the XML serialised content
						com.hp.hpl.jena.rdf.model.impl.Util.substituteEntitiesInElementContent(resultSet.getString(2));
						title = resultSet.getString(2);
						
					} catch(com.hp.hpl.jena.shared.CannotEncodeCharacterException ex) {
						// the title will not make it into the XML serialised content
						System.err.println("ERROR: An event name contains invalid content that must be manually fixed.");
						System.err.println("       The event id for this event is: " + resultSet.getString(1));
						System.err.println("       The name for this event is: " + resultSet.getString(2));
						
						Matcher invalidContentMatch = invalidContentPattern.matcher(resultSet.getString(2));
						title = invalidContentMatch.replaceAll("");
					}
					
					// add the title and Url
					event.addProperty(DCTerms.title, title);
					event.addProperty(DCTerms.identifier, AusStageURI.getEventURL(resultSet.getString(1)));
					
					// reset the dates
					firstDate = null;
					lastDate  = null;
					
					// first date
					if(resultSet.getString(4) != null) {
						firstDate = DateUtils.buildDate(resultSet.getString(4), resultSet.getString(5), resultSet.getString(6));
					}
					
					// last date
					if(resultSet.getString(7) != null) {
						lastDate = DateUtils.buildDate(resultSet.getString(7), resultSet.getString(8), resultSet.getString(9));
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
						timeInterval = model.createResource(Time.Interval);

						// add the timeline properties
						// add the typed literals
						timeInterval.addProperty(Timeline.beginsAtDateTime, model.createTypedLiteral(firstDate, XSDDatatype.XSDdate));
						timeInterval.addProperty(Timeline.endsAtDateTime, model.createTypedLiteral(lastDate, XSDDatatype.XSDdate));

						// andd the timeInterval to the Event
						event.addProperty(Event.time, timeInterval);
					}
					
					// lookup the contributor
					if(contributors.contains(Integer.parseInt(resultSet.getString(3))) == true) {
					
						// get the contributor
						contributor = model.createResource(AusStageURI.getContributorURI(resultSet.getString(3)));
						
						// add the relationships
						contributor.addProperty(Event.isAgentIn, event);
						event.addProperty(Event.agent, contributor);
						
					} else {
						// missing contributor
						contributor = null;
						System.err.println("WARN: Unable to locate contributor with id: " + resultSet.getString(3) + " associated with event with id: " + resultSet.getString(1));
					} 
							
					// increment the event count 
					eventCount++;
					
					// store a reference to this event
					events.add(Integer.parseInt(resultSet.getString(1)));
					
					// store this id
					currentId = resultSet.getString(1);
				}	
			}
			
			// play nice and tidy up
			dbObject.tidyUp();
			dbObject = null;
			System.out.format("INFO: %,d events successfully added to the datastore%n", eventCount);
			
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
			System.out.println("INFO: Adding contributor functions at event records...");
			
			// declare helper variables
			String currentId         = "";
			Resource contributor     = null;
			Resource functionAtEvent = null;
			
			// define the sql
			String sql = "SELECT cl.contributorid, cl.eventid, cfp.preferredterm "
					   + "FROM conevlink cl, contributorfunctpreferred cfp "
					   + "WHERE cl.eventid IS NOT NULL "
					   + "AND cl.contributorid IS NOT NULL "
					   + "AND cl.function = cfp.contributorfunctpreferredid "
					   + "ORDER BY contributorid";
			
			// get the data from the database				   
			DbObjects dbObject = database.executeStatement(sql);				   
			java.sql.ResultSet resultSet = dbObject.getResultSet();
	
			// loop through the 
			while (resultSet.next()) {
			
				// have we seen this contributor before
				if(currentId.equals(resultSet.getString(1)) != true) {
					
					// lookup the contributor
					if(contributors.contains(Integer.parseInt(resultSet.getString(1))) == true) {

						// get the contributor
						contributor = model.createResource(AusStageURI.getContributorURI(resultSet.getString(1)));
						
						// store the id to reduce number of lookups
						currentId = resultSet.getString(1);
					} else {
						contributor = null;
						System.err.println("WARN: Unable to locate contributor with id: " + resultSet.getString(1));
					}
				} else {
					// yes we have so use the found contributor if available
					if(contributor != null) {
					
						// lookup the event
						if(events.contains(Integer.parseInt(resultSet.getString(2))) == true) {
					
							// construct a new functionAtEvent
							functionAtEvent = model.createResource(AuseStage.functionAtEvent);
						
							// add the on event property
							functionAtEvent.addProperty(AuseStage.atEvent, model.createResource(AusStageURI.getEventURI(resultSet.getString(2))));
							functionAtEvent.addProperty(AuseStage.function, resultSet.getString(3));
						
							// add the collaborates property to the contributor
							contributor.addProperty(AuseStage.undertookFunction, functionAtEvent);
						
							// increment the count
							functionsAtEventsCount++;
						} else {
							System.err.println("WARN: Unable to locate event with id: " + resultSet.getString(2));
						}
					}
				}				
			}
			
			// play nice and tidy up
			dbObject.tidyUp();
			dbObject = null;
			System.out.format("INFO: %,d contributor function at event records added%n", functionsAtEventsCount);
			
		} catch (java.sql.SQLException sqlEx) {
			System.err.println("ERROR: An SQL related error has occured");
			System.err.println("       " + sqlEx.getMessage());
			return false;
		}
		
		/*
		 * add base organisation information
		 */
		 	   
		try {
		
			// keep the user informed
			System.out.println("INFO: Adding organisation data to the datastore...");
			
			// define the sql
			String sql = "SELECT organisationid, name, other_names1, other_names2, other_names3 "
					   + "FROM organisation";
			
			// get the data from the database				   
			DbObjects dbObject = database.executeStatement(sql);				   
			java.sql.ResultSet resultSet = dbObject.getResultSet();
			
			// declare helper variables
			Resource organisation = null;
			String name = null;
	
			// loop through the 
			while (resultSet.next()) {
			
				// create a new organisation
				organisation = model.createResource(AusStageURI.getOrganisationURI(resultSet.getString(1)));
				organisation.addProperty(RDF.type, Org.Organization);
				
				// add the name
				// check the name
				try {
					// use this method that encodes entities to check the content of the title
					// if it throws an exception the title will not make it into the XML serialised content
					com.hp.hpl.jena.rdf.model.impl.Util.substituteEntitiesInElementContent(resultSet.getString(2));
					name = resultSet.getString(2);
					
				} catch(com.hp.hpl.jena.shared.CannotEncodeCharacterException ex) {
					// the title will not make it into the XML serialised content
					System.err.println("ERROR: An organisation name contains invalid content that must be manually fixed.");
					System.err.println("       The organisation id is: " + resultSet.getString(1));
					System.err.println("       The organisation name is: " + resultSet.getString(2));
					
					Matcher invalidContentMatch = invalidContentPattern.matcher(resultSet.getString(2));
					name = invalidContentMatch.replaceAll("");
				}
				// add the name
				organisation.addProperty(SKOS.prefLabel, name);
				
				// add the url
				organisation.addProperty(FOAF.page, AusStageURI.getOrganisationURL(resultSet.getString(1)));
				
				// add any other names if present
				if(resultSet.getString(3) != null) {
					try {
						// use this method that encodes entities to check the content of the title
						// if it throws an exception the title will not make it into the XML serialised content
						com.hp.hpl.jena.rdf.model.impl.Util.substituteEntitiesInElementContent(resultSet.getString(3));
						name = resultSet.getString(3);
					
					} catch(com.hp.hpl.jena.shared.CannotEncodeCharacterException ex) {
						// the title will not make it into the XML serialised content
						System.err.println("ERROR: An organisation other name contains invalid content that must be manually fixed.");
						System.err.println("       The organisation id is: " + resultSet.getString(1));
						System.err.println("       The organisation name is: " + resultSet.getString(3));
					
						Matcher invalidContentMatch = invalidContentPattern.matcher(resultSet.getString(3));
						name = invalidContentMatch.replaceAll("");
					}
					organisation.addProperty(SKOS.altLabel, name);
				}
				
				if(resultSet.getString(4) != null) {
					try {
						// use this method that encodes entities to check the content of the title
						// if it throws an exception the title will not make it into the XML serialised content
						com.hp.hpl.jena.rdf.model.impl.Util.substituteEntitiesInElementContent(resultSet.getString(4));
						name = resultSet.getString(4);
					
					} catch(com.hp.hpl.jena.shared.CannotEncodeCharacterException ex) {
						// the title will not make it into the XML serialised content
						System.err.println("ERROR: An organisation other name contains invalid content that must be manually fixed.");
						System.err.println("       The organisation id is: " + resultSet.getString(1));
						System.err.println("       The organisation name is: " + resultSet.getString(4));
					
						Matcher invalidContentMatch = invalidContentPattern.matcher(resultSet.getString(4));
						name = invalidContentMatch.replaceAll("");
					}
					organisation.addProperty(SKOS.altLabel, name);
				}
				
				if(resultSet.getString(5) != null) {
					try {
						// use this method that encodes entities to check the content of the title
						// if it throws an exception the title will not make it into the XML serialised content
						com.hp.hpl.jena.rdf.model.impl.Util.substituteEntitiesInElementContent(resultSet.getString(5));
						name = resultSet.getString(5);
					
					} catch(com.hp.hpl.jena.shared.CannotEncodeCharacterException ex) {
						// the title will not make it into the XML serialised content
						System.err.println("ERROR: An organisation other name contains invalid content that must be manually fixed.");
						System.err.println("       The organisation id is: " + resultSet.getString(1));
						System.err.println("       The organisation name is: " + resultSet.getString(5));
					
						Matcher invalidContentMatch = invalidContentPattern.matcher(resultSet.getString(5));
						name = invalidContentMatch.replaceAll("");
					}
					organisation.addProperty(SKOS.altLabel, name);
				}
				
				// store a reference to this organisation
				organisations.add(Integer.parseInt(resultSet.getString(1)));
				
				// increment the count
				organisationCount++;				
			}
			
			// play nice and tidy up
			dbObject.tidyUp();
			dbObject = null;
			System.out.format("INFO: %,d organisations successfully added to the datastore%n", organisationCount);
			
		} catch (java.sql.SQLException sqlEx) {
			System.err.println("ERROR: An SQL related error has occured");
			System.err.println("       " + sqlEx.getMessage());
			return false;
		}
		
		/*
		 * Add Roles at events
		 */
		 
		try {
		
			// keep the user informed
			System.out.println("INFO: Adding organisation roles at event records...");
			
			// declare helper variables
			String currentId      = "";
			Resource organisation = null;
			Resource roleAtEvent  = null;
			
			// define the sql
			String sql = "SELECT ol.organisationid, ol.eventid, orgfunction "
					   + "FROM orgevlink ol, orgfunctmenu "
					   + "WHERE ol.function = orgfunctionid "
					   + "AND ol.organisationid IS NOT NULL "
					   + "AND ol.eventid IS NOT NULL "
					   + "ORDER BY ol.organisationid";
			
			// get the data from the database				   
			DbObjects dbObject = database.executeStatement(sql);				   
			java.sql.ResultSet resultSet = dbObject.getResultSet();
	
			// loop through the 
			while (resultSet.next()) {
			
				// have we seen this contributor before
				if(currentId.equals(resultSet.getString(1)) != true) {
					
					// lookup the contributor
					if(organisations.contains(Integer.parseInt(resultSet.getString(1))) == true) {

						// get the organisation
						organisation = model.createResource(AusStageURI.getOrganisationURI(resultSet.getString(1)));
						
						// store the id to reduce number of lookups
						currentId = resultSet.getString(1);
					} else {
						organisation = null;
						System.err.println("WARN: Unable to locate organisation with id: " + resultSet.getString(1));
					}
				} else {
					// yes we have so use the found contributor if available
					if(organisation != null) {
					
						// lookup the event
						if(events.contains(Integer.parseInt(resultSet.getString(2))) == true) {
					
							// construct a new functionAtEvent
							roleAtEvent = model.createResource(AuseStage.roleAtEvent);
						
							// add the on event property
							roleAtEvent.addProperty(AuseStage.atEvent, model.createResource(AusStageURI.getEventURI(resultSet.getString(2))));
							roleAtEvent.addProperty(AuseStage.roleAtEvent, resultSet.getString(3));
						
							// add the collaborates property to the contributor
							organisation.addProperty(AuseStage.undertookRole, roleAtEvent);
						
							// increment the count
							rolesAtEventsCount++;
						} else {
							//System.err.println("WARN: Unable to locate event with id: " + resultSet.getString(2));
						}
					}
				}				
			}
			
			// play nice and tidy up
			dbObject.tidyUp();
			dbObject = null;
			System.out.format("INFO: %,d organisation roles at event records added%n", rolesAtEventsCount);
			
		} catch (java.sql.SQLException sqlEx) {
			System.err.println("ERROR: An SQL related error has occured");
			System.err.println("       " + sqlEx.getMessage());
			return false;
		}
		
		/*
		 * add dataset metadata
		 */
		// get the current date and time
		Resource metadata = model.createResource("ausstage:rdf:metadata");
		metadata.addProperty(RDF.type, AuseStage.rdfMetadata);
		metadata.addProperty(AuseStage.tdbCreateDateTime, DateUtils.getCurrentDateAndTime());
		
		/*
		 * generate the statistics files
		 */
		System.out.println("INFO: Writing BGP optimiser file...");
		
		// ensure that everything has been written
		TDB.sync(model);
		model.close();
		model = null;
		TDB.closedown();
		
		// pause for five seconds to allow writes to finish etc. 
		try {
			Thread.sleep(5000);
		} catch (java.lang.InterruptedException ex) {}
		
		// reconnect to the TDB datastore and generate the statistics file
		model = TDBFactory.createModel(datastorePath);
		 
		// get the graph from the model 
		GraphTDB graph = (GraphTDB) model.getGraph(); // TDB Specific graph class (Graph is a lower level representation of the data)
		
		// get the statistics
		StatsCollector stats = Stats.gatherTDB(graph) ;
		
		// delete the existing stats file
		boolean status;
		File opt = new File(datastorePath + "/fixed.opt");
		
		try {
			status = opt.delete();
			
			if(status == false) {
				System.err.println("WARN: Unable to delete the old query optimisation file:");
				System.err.println("      " + opt.getAbsolutePath());
			}
		} catch (SecurityException ex) {
			System.err.println("WARN: Unable to delete the old query optimisation file:");
			System.err.println("      " + opt.getAbsolutePath());
		}
		
		if(status = true) {
			
			// reset the opt variable to a new file
			opt = null;
			
			Stats.write(datastorePath + "/stats.opt", stats) ;
		}
		
		// play nice and tidy up
		TDB.sync(model);
		model.close();
		model = null;
		TDB.closedown();
		database = null;
		
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



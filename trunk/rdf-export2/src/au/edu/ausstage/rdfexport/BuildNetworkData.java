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
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Map;
import java.util.HashMap;

// import the Jena related packages
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.tdb.*;
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;

// datastore stats code
import com.hp.hpl.jena.tdb.solver.stats.Stats ;
import com.hp.hpl.jena.tdb.solver.stats.StatsCollector ;
import com.hp.hpl.jena.tdb.store.GraphTDB ;

// import the AusStage classes
import au.edu.ausstage.vocabularies.*;
import au.edu.ausstage.utils.*;

// import the XML parser 
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;



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
	
	// declare other private variables
	// pattern derived from the com.hp.hpl.jena.rdf.model.impl.Util class
	private final Pattern invalidContentPattern = Pattern.compile( "<|>|&|[\0-\37&&[^\n\t]]|\uFFFF|\uFFFE" );
	
	
	// Start a map so that we have somewhere to cache the objects that we've loaded from Jena 
	Map<String, Property> usedVocab = new HashMap<String, Property>();
	Map<String, XSDDatatype> usedTypeVocab = new HashMap<String, XSDDatatype>();
	
	// Set interactive boolean up here so we don't need to keep passing it between functions
	boolean interactive = false;
	
	// Load the model we use to generate the Properties
	OntModel ontModel = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM, null );

	// create an empty persistent model
	Model model = null;

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
	 * Returns what the user types in interactive mode
	 * 
	 * @param prompt  The prompt to present to the user
	 * @return  The next line the user types
	 */
	private String promptUser(String prompt) {
		java.util.Scanner input = new java.util.Scanner(System.in);
		System.out.print(prompt + " > ");
		return input.nextLine();
	}
	
	/**
	 * A function to parse a given XML file and return the root element
	 * 
	 *  @param file  The path to the XML file
	 *  @return   An Element object containing the root element of the XML document
	 *  		  or null if there was an error
	 */
	private Element parseXmlFile(String file){
		//get the factory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		try {
			//Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();

			return db.parse(file).getDocumentElement();

		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * A function to return the text value of an element, empty string otherwise
	 * 
	 * @param element  The element containing text
	 * @return   A string containing the text inside the element
	 */
	private String getTextValue(Element element) {
		String retVal = element.getFirstChild().getNodeValue();
		if (retVal == null) return "";
		return retVal;
	}
	
	/**
	 * A function to check that a field value matches a regex
	 * 
	 * @param matches  A regular expression that should match the entire input string
	 * @param finalValue  The input string you wish to match
	 * @return true if the expression matches, false otherwise
	 */
	private boolean validateField(String matches, String finalValue) {
		// validate value against "matches", if applicable
		if (matches != null && !matches.equals("")) {
			try {
				Pattern validPattern = Pattern.compile(matches);
				Matcher validMatcher = validPattern.matcher(finalValue);
				return validMatcher.matches();
			} catch (Exception e) {
				System.err.println("ERROR: Invalid regular expression '" + matches + "', dropping this property.");
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Removes characters that can't be written into the RDF document from a field
	 * 
	 * @param finalValue  The value of the field
	 * @return  finalValue, minus any invalid characters
	 */
	private String checkField(String finalValue) {
		try {
			// use this method that encodes entities to check the content of the title
			// if it throws an exception the value will not make it into the XML serialised content
			com.hp.hpl.jena.rdf.model.impl.Util.substituteEntitiesInElementContent(finalValue);
		
		} catch(com.hp.hpl.jena.shared.CannotEncodeCharacterException ex) {
			// the value will not make it into the XML serialised content-- need to automatically/manually fix it
			
			if (this.interactive) {
				System.err.println("ERROR: An object contains invalid characters. Please fix them or hit Enter to automatically fix.");
				System.err.println("       The object is: " + finalValue);
				
				String inFromUser = promptUser("New value");
				if (inFromUser == "") {
					Matcher invalidContentMatch = invalidContentPattern.matcher(finalValue);
					finalValue = invalidContentMatch.replaceAll("");
				} else {
					finalValue = checkField(inFromUser); // Recursive check to make sure the user didn't type something invalid
				}
				
			} else {
				System.err.println("ERROR: An object contains invalid characters. They have been removed.");
				System.err.println("       The object is: " + finalValue);
			
				Matcher invalidContentMatch = invalidContentPattern.matcher(finalValue);

				finalValue = invalidContentMatch.replaceAll("");
			}
		}
		return finalValue;
	}
	
	/**
	 * Stores and retreives Jena properties from a hashmap so that we're not constantly creating new objects
	 * 
	 * @param predicateUri  The full URI of the predicate to retrieve
	 * @param predicateType  The datatype of the predicate ("key" for foreign keys, datatype or null otherwise)
	 * @return  The existing property object from the hashmap, or a new one if there isn't one there already
	 */
	private Property getPredicateFromUri(String predicateUri, String predicateType) {
		if (!this.usedVocab.containsKey(predicateUri)) {
			try {
				if (predicateType != null && predicateType.equals("key")) {
					this.usedVocab.put(predicateUri, ontModel.createObjectProperty(predicateUri));
				} else {
					this.usedVocab.put(predicateUri, ontModel.createDatatypeProperty(predicateUri));
				}
			} catch (Exception e) {
				System.err.println("ERROR: Could not create predicate from URI '" + predicateUri + "'");
				return null;
			}
		}
		return this.usedVocab.get(predicateUri);
		
	}
	
	/**
	 * Stores and retreives Jena datatypes from a hashmap so that we're not constantly creating new objects
	 * 
	 * @param datatype  The type name (eg. "integer", "date")
	 * @return  The existing datatype from the hashmap, or a new one if there isn't one there already
	 */
	private XSDDatatype getDatatypeFromUri(String datatype) {
		if (!this.usedTypeVocab.containsKey(datatype)) {
			try {
				this.usedTypeVocab.put(datatype, new XSDDatatype(datatype));
			} catch (Exception e) {
				System.err.println("ERROR: Could not create datatype '" + datatype + "'");
				return null;
			}
		}
		return this.usedTypeVocab.get(datatype);
		
	}
	
	/**
	 * Replaces values in {curly_braces} with the corresponding value from a database 
	 * 
	 * @param object  The element containing a specification for the data
	 * @param resultSet  The ResultSet, cued up to the appropriate record
	 * @return  The specification with things in curly braces replaced with values from the ResultSet
	 * @throws SQLException
	 */
	private String parseDatabaseFields(Element object, java.sql.ResultSet resultSet) throws SQLException {
		// parse specification for field names, substitute them in

		String specification = getTextValue(object);
		String finalValue = specification;

		Pattern fieldPattern = Pattern.compile("\\{([^\\}]*)\\}");
		Matcher fieldMatcher = fieldPattern.matcher(specification);
		
		while (fieldMatcher.find()) {
			String val = fieldMatcher.group(1);
			String data = null;
			try {
				data = resultSet.getString(val);
			} catch (Exception e) {
				System.err.println("ERROR: Object spec '" + specification + "' contains invalid database field '" + val + "'");
			}
			if (data == null) data = "";
			finalValue = finalValue.replace("{" + val + "}", data);
		}
		
		// If the field matches the regex return it
		if (validateField(object.getAttribute("matches"), finalValue)) return finalValue;
		// Otherwise don't
		return "";
	}
	
	/**
	 * Fills an RDF subject with the data specified by a NodeList of objects
	 * 
	 * @param subject  An existing Jena subject to add the objects to
	 * @param objects  A NodeList of objects to loop through and add to subject
	 * @param resultSet  A ResultSet containing all relevant data for the subject in question
	 * @return  false if any errors were detected, true otherwise
	 * @throws SQLException
	 */
	private boolean processObjects(Resource subject, NodeList objects, java.sql.ResultSet resultSet) throws SQLException {
		if (objects != null && objects.getLength() > 0) {
			for (int i = 0; i < objects.getLength(); i++) {
				Element object = (Element) objects.item(i);
				
				String predicateUri = object.getAttribute("predicate");
				String predicateType = object.getAttribute("type");
				Property predicate = getPredicateFromUri(predicateUri, predicateType);
				if (predicate == null) {
					System.out.println("ERROR: Invalid predicate.");
					return false;
				}
				String databaseValue = parseDatabaseFields(object, resultSet);
				String finalValue = checkField(databaseValue);
				
				if (!this.interactive && !databaseValue.equals(finalValue)) { 
					// If these are different we've just printed out an invalid message, now we can give them their subject ID 
					System.err.println("       The subject is: " + subject.getNameSpace());
				}
				
				if (!finalValue.equals("")) {
					if (predicateType == null || predicateType.equals("")) {
						// No datatype specified, so don't write one
						subject.addProperty(predicate, finalValue);
					} else {
						if (predicateType.equals("key")) {
							if (!finalValue.endsWith(":")) { // If the finalValue ends with ":" then the database field was null
								// Datatype of "key", run foreign key code
								Resource foreign = null;
								if (finalValue.startsWith("blank:")) {
									foreign = this.model.createResource(new AnonId(finalValue));
								} else {
									foreign = this.model.createResource("ausstage:" + finalValue);
								}
								
								if (predicateUri != null && !predicateUri.equals("")) {
									subject.addProperty(getPredicateFromUri(predicateUri, "key"), foreign);
								}
								if (object.getAttribute("reversePredicate") != null && !object.getAttribute("reversePredicate").equals("")) {
									Property reversePredicate = getPredicateFromUri(object.getAttribute("reversePredicate"), "key");
									if (reversePredicate == null) {
										System.out.println("ERROR: Invalid predicate.");
										return false;
									}
									foreign.addProperty(reversePredicate, subject);
								}
							}
						} else {
							try {
								// Datatype unknown, create it from the XSDDatatypes
								XSDDatatype type = getDatatypeFromUri(predicateType);
								subject.addProperty(predicate, model.createTypedLiteral(finalValue, type));
							} catch (Exception e) {
								System.err.println("ERROR: Datatype '" + predicateType + "' is not valid, or does not match value '" + finalValue +"'");
								return false;
							}
						}
					}
				}
				
			}
		}
		return true;
	}
	
	/**
	 * A method to undertake the task of building the dataset
	 * 
	 * @param inputFile  The XML file to use as a map between the database and the RDF document
	 * @param interactive  true if interactive mode is enabled
	 * @return true if, and only if, the task completes successfully
	 */
	public boolean doTask(File inputFile, boolean interactive) {
	    
		// turn off the TDB logging
		org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger("com.hp.hpl.jena.tdb.info");
		logger.setLevel(org.apache.log4j.Level.OFF);
		
		// just for output purposes
		int counter       = 0;
		
		this.interactive = interactive;
		
		if(InputUtils.isValid(datastorePath) == false) {
			System.err.println("ERROR: the doReset() method must be run before building the network datastore");
			return false;
		} else {
			model = TDBFactory.createModel(datastorePath) ;
		}
		
		// Parse the XML file
		String xmlFile = inputFile.getAbsolutePath();
		Element xmlDoc = parseXmlFile(xmlFile);
		if(xmlDoc == null) {
			System.err.println("ERROR: the XML file " + xmlFile + " could not be parsed.");
			return false;
		}

		// Loop through our xmlDoc and set all the namespace prefixes
		NodeList namespaces = ((Element)(xmlDoc.getElementsByTagName("namespaces")).item(0)).getElementsByTagName("namespace");
		
		if (namespaces != null && namespaces.getLength() > 0) {
			for (int i = 0; i < namespaces.getLength(); i++) {
				// get the namespace element from the nodelist
				Element namespace = (Element) namespaces.item(i);

				// set it in the Jena model
				model.setNsPrefix(namespace.getAttribute("prefix"), namespace.getFirstChild().getNodeValue());
			}
		}
		
		
		NodeList tables = ((Element)(xmlDoc.getElementsByTagName("tables")).item(0)).getElementsByTagName("table");

		if (tables != null && tables.getLength() > 0) {
			for (int i = 0; i < tables.getLength(); i++) {
				// get the table settings from the mapping file
				Element table = (Element) tables.item(i);
				
				String tableName = table.getAttribute("name");
				
				String tablePrefix = tableName + ":";
				if (!tablePrefix.startsWith("blank:")) tablePrefix = "ausstage:" + tablePrefix;
				
				String sql = getTextValue((Element) table.getElementsByTagName("sql").item(0));
				String type = table.getAttribute("type");
				String primaryKey = table.getAttribute("primaryKey");
				Property typeObject = getPredicateFromUri(type, null);
				
				if (typeObject == null) {
					System.out.println("ERROR: Invalid table type.");
					return false;
				}
				System.out.println("INFO: Processing table " + tableName + "...");
				
				counter = 0;
				
				DbObjects dbObject = null;
				java.sql.ResultSet resultSet = null;
				try {
					// get the data from the database
					dbObject = database.executeStatement(sql);				   
					resultSet = dbObject.getResultSet();
				} catch (Exception e) {
					System.err.println("ERROR: Couldn't process the SQL query:");
					System.err.println(sql);
					return false;
				}
				
				// declare helper variables
				Resource subject = null;
					
				// loop through the resultset 
				try {
					while (resultSet.next()) {
						// create a new subject in Jena
						String subjectId = tablePrefix + resultSet.getString(primaryKey);
						
						// handle creation of blank nodes
						if (tablePrefix.startsWith("blank:")) {
							subject = model.createResource(new AnonId(subjectId));
						} else { //regular nodes
							subject = model.createResource(subjectId);
						}
						
						subject.addProperty(RDF.type, typeObject);
						
						// loop through the table's objects in the XML file
						NodeList objects = ((Element)(table.getElementsByTagName("objects")).item(0)).getElementsByTagName("object");

						if (processObjects(subject, objects, resultSet)) {
							counter++;
						} else {
							System.err.println("ERROR: Failed on subject " + subjectId);
							if (interactive && promptUser("Type 'exit' to quit, or press Enter to continue").equals("exit")) {
								return false;
							}
						}
						if (counter % 10000 == 0) TDB.sync(model);
						objects = null;
					}
				} catch (SQLException e) {
					System.err.println("ERROR: There was a SQL error processing the table '" + tableName + "'. Probably due to an invalid primary key in the mapping file.");
					System.err.println("       The query was:");
					System.err.println("       " + sql);
					return false;
				} catch (Exception e) {
					System.err.println("ERROR: There was an error processing the table '" + tableName + "'");
					return false;
				}
				
				// play nice and tidy up
				dbObject.tidyUp();
				dbObject = null;
				System.out.println("INFO: " + counter + " " + tableName + " records successfully added to the datastore");
				
				counter = 0;
				
				// SUBQUERIES
				if (table.getElementsByTagName("subqueries").getLength() == 1) { // There should be a "subqueries" element container
					System.out.println("INFO: Processing " + tableName + " subqueries...");
					
					NodeList subqueries = ((Element)(table.getElementsByTagName("subqueries")).item(0)).getElementsByTagName("subquery");

					if (subqueries != null && subqueries.getLength() > 0) {
						for (int k = 0; k < subqueries.getLength(); k++) {
							System.out.println("INFO: Processing subquery " + (k+1) + "...");
							Element subquery = (Element) subqueries.item(k);
							sql = getTextValue((Element) subquery.getElementsByTagName("sql").item(0));
							
							java.sql.ResultSet subqueryResultSet = null;
							try {
								// get the data from the database
								dbObject = database.executeStatement(sql);				   
								subqueryResultSet = dbObject.getResultSet();
							} catch (Exception e) {
								System.err.println("ERROR: Couldn't process the SQL query:");
								System.err.println(sql);
								return false;
							}
							// loop through the resultset 
							String lastKey = null;
							try {
								while (subqueryResultSet.next()) {
									String subjectId = tablePrefix + subqueryResultSet.getString(primaryKey);
									
									if (!subjectId.equals(lastKey)) {
										// create a new subject in Jena
										if (tablePrefix.startsWith("blank:")) {
											subject = model.createResource(new AnonId(subjectId));
										} else {
											subject = model.createResource(subjectId);	
										}
										
										lastKey = subjectId;
									}
									// loop through the subquery's objects in the mapping file
									NodeList objects = ((Element)(subquery.getElementsByTagName("objects")).item(0)).getElementsByTagName("object");
			
									if (processObjects(subject, objects, subqueryResultSet)) {
										counter++;
									} else {
										System.err.println("ERROR: Failed on subject " + subjectId);
										if (interactive && promptUser("Type 'exit' to quit, or press Enter to continue").equals("exit")) {
											return false;
										}
									}
									if (counter % 10000 == 0) TDB.sync(model);
	
									objects = null;
			
								}
							} catch (SQLException e) {
								System.err.println("ERROR: There was a SQL error processing a subquery on the table '" + tableName + "'.");
								System.err.println("       The query was:");
								System.err.println("       " + sql);
								return false;
							} catch (Exception e) {
								System.err.println("ERROR: There was an error processing the table '" + tableName + "'");
								return false;
							}
							System.out.println("INFO: " + counter + " modifications made to the datastore");
							counter = 0;
							// play nice and tidy up
							dbObject.tidyUp();
							dbObject = null;
						}
					}
				}
			}
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



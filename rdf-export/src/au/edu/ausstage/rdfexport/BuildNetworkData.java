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

// import additional packages
import java.sql.*;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

// import the Jena related packages
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.*;
import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.tdb.*;

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
		return true;
		
	} // end the doReset method
	
	/**
	 * A method to undertake the task of building the dataset
	 *
	 * @return true if, and only if, the task completes successfully
	 */
	public boolean doTask() {
	
        // create an empty Model
		//Model model = ModelFactory.createDefaultModel();

		// set some of the TDB options
		TDB.setExecutionLogging(com.hp.hpl.jena.tdb.solver.Explain.InfoLevel.NONE); // disable logging
		
		// create an empty persistent model
		Model model = null;
		
		if(datastorePath == null) {
			System.err.println("ERROR: the doReset() method must be run before building the network datastore");
			return false;
		} else {
			model = TDBFactory.createModel(datastorePath) ;
		}
		
		// set a namespace prefix
		model.setNsPrefix("foaf", FoAF.NS);
		model.setNsPrefix("AusStage", AusStage.NS);
		
		// create a contributor
		Resource contributor = model.createResource("http://drthorweasel.com");
		contributor.addProperty(RDF.type, FoAF.Person);
		contributor.addProperty(FoAF.title, "Dr");
		contributor.addProperty(FoAF.name, "ThorWeasel");
		
		// create an event
		Resource event = model.createResource("http://www.ausstage.edu.au/e/1234");
		event.addProperty(RDF.type, AusStage.Event);
		event.addProperty(AusStage.name, "Test Event");
		event.addProperty(AusStage.startDate, "2010-01-01");
		event.addProperty(AusStage.endDate, "2010-02-02");
		
		// create another contributor
		contributor = model.createResource("http://mi6.co.uk/people/jamesbond");
		contributor.addProperty(RDF.type, FoAF.Person);
		contributor.addProperty(FoAF.title, "Mr");
		contributor.addProperty(FoAF.name, "James Bond");
		contributor.addProperty(AusStage.contributedTo, "http://www.ausstage.edu.au/e/1234");		
		
		//model.write(System.out);
		model.write(System.out, "RDF/XML-ABBREV");
		
		System.out.println("-----------------------------------------");
		
		// Create a new query
		String queryString = 
			"SELECT ?x " +
			"WHERE { ?y <http://xmlns.com/foaf/0.1/name> ?x}";			

		Query query = QueryFactory.create(queryString);

		// Execute the query and obtain results
		QueryExecution qe = QueryExecutionFactory.create(query, model);
		com.hp.hpl.jena.query.ResultSet results = qe.execSelect();

		// Output query results	
		ResultSetFormatter.out(System.out, results, query);

		// Important - free up resources used running the query
		qe.close();



	
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



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
import java.io.*;

// import the Jena related packages
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.*;
import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.tdb.*;

// import the vocabularies
import au.edu.ausstage.vocabularies.*;

/**
 * A Class used to export an RDF based dataset of contributor information
 */
public class EdgeListExport {

	// declare private class level variables
	private PropertiesManager      settings;             // access the properties / settings
	
	/**
	 * A constructor for this class
	 *
	 * @param dataManager the DatabaseManager class connected to the AusStage database
	 * @param properties  the PropertiesManager providing access to properties and settings
	 */
	public EdgeListExport(PropertiesManager properties) {
		
		// double check the parameters
		if(properties == null) {
			throw new IllegalArgumentException("ERROR: The parameters to the ExportNetworkData constructor cannot be null");
		}
		
		// store references to these objects for later
		settings = properties;
	}
	
	/**
	 * A method to undertake the task of building the dataset
	 *
	 * @param outputFile the file that contains the query to run
	 *
	 * @return true if, and only if, the task completes successfully
	 */
	public boolean doTask(File outputFile) {
	
		// check on the parameters
		if(outputFile == null) {
			throw new IllegalArgumentException("ERROR: The parameters to the doTask method cannot be null");
		}		
	
		// turn off the TDB logging
		org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger("com.hp.hpl.jena.tdb.info");
		logger.setLevel(org.apache.log4j.Level.OFF);
		
		// declare some helper variables
		String datastorePath = null;
		
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
		
		// check for files in this directory
		File[] tdbFiles = datastore.listFiles(new FileListFilter());
		
		if(tdbFiles.length == 0) {
			System.err.println("ERROR: The specified datastore directory appears to be empty");
			return false;
		}		
	
		// gain access to the model
		Model model = TDBFactory.createModel(datastorePath);
		
		// declare a PrintWriter for output
		PrintWriter output = null;
		
		// load the query into a file
		try {
			System.out.println("INFO: Exporting entire dataset as an Edge List file:");
			
			// open the output file
			output = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outputFile), "UTF8"));
			
		} catch (java.io.FileNotFoundException ex) {
			System.err.println("ERROR: Unable to write to output file");
			System.err.println("       " + outputFile.getAbsolutePath());
			return false;
		} catch (java.io.IOException ex) {
			System.err.println("ERROR: Unable to write to output file");
			System.err.println("       " + outputFile.getAbsolutePath());
			return false;
		}
		
		// define the query
		String queryString = "PREFIX foaf:       <http://xmlns.com/foaf/0.1/> "
						   + "PREFIX ausestage:  <http://code.google.com/p/aus-e-stage/wiki/AuseStageOntology#> "
						   + "  "
						   + "SELECT ?contributor ?givenName ?familyName ?collaborator ?collabGivenName ?collabFamilyName "
						   + "WHERE {  "
						   + "       ?contributor   foaf:givenName ?givenName ; "
						   + "                      foaf:familyName ?familyName; "
						   + "                      ausestage:hasCollaboration ?collaboration. "
						   + "       ?collaboration ausestage:collaborator ?collaborator. "
						   + "       ?collaborator  foaf:givenName ?collabGivenName; "
						   + "                      foaf:familyName ?collabFamilyName. "
						   + "       FILTER (?collaborator != ?contributor) "
						   + "}";
						   
		// keep track of the count of lines
		int lineCount = 0;
		
		// get a query object
		Query query = QueryFactory.create(queryString, Syntax.syntaxARQ);
		
		// Execute the query and obtain results
		QueryExecution qe = QueryExecutionFactory.create(query, model);
		com.hp.hpl.jena.query.ResultSet results = qe.execSelect();
		
		// declare helper variables
		String   tmp  = null;
		String[] tmps = null;

		// Output query results	
		while(results.hasNext() == true) {
			// get a result
			com.hp.hpl.jena.query.QuerySolution result = results.next();
			
			// first id
			tmp = result.get("contributor").toString().trim();
			tmps = tmp.split(":");
			
			output.print(tmps[2]);
			output.print("\t");
			
			// first givenName
			output.print(result.get("givenName").toString().trim());
			output.print("\t");
			
			// first familyName
			output.print(result.get("familyName").toString().trim());
			output.print("\t");
			
			// knows
			tmp = result.get("collaborator").toString().trim();
			tmps = tmp.split(":");
			
			output.print(tmps[2]);
			output.print("\t");
			
			// second GivenName
			output.print(result.get("collabGivenName").toString().trim());
			output.print("\t");
			
			// second FamilyName
			output.print(result.get("collabFamilyName").toString().trim());
			output.print("\n");	
			
			// increment the line count
			lineCount++;		
		}			

		// Important - free up resources used running the query
		qe.close();
		
		// close the output file
		output.close();
		
		// if we get this far, everything went ok
		System.out.println("INFO: Output file containing " + lineCount + " lines successfully created");
		System.out.println("      " + outputFile.getAbsolutePath());
		return true;
		
	} // end the doTask method
	
	/**
	 * A method to undertake the task of building the dataset
	 *
	 * @param outputFile the file that contains the query to run
	 * @param deDuplicate a boolean flag to indicate if the list should be deduplicated
	 *
	 * @return true if, and only if, the task completes successfully
	 */
	public boolean doTask(File outputFile, boolean deDuplicate) {
	
		// check on the parameters
		if(outputFile == null) {
			throw new IllegalArgumentException("ERROR: The parameters to the doTask method cannot be null");
		}
		
		// check on what to do
		if(deDuplicate == false) {
			// use the simpler method as we don't need to do de duplication
			return doTask(outputFile);
		}
	
		// turn off the TDB logging
		org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger("com.hp.hpl.jena.tdb.info");
		logger.setLevel(org.apache.log4j.Level.OFF);
		
		// declare some helper variables
		String datastorePath = null;
		
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
		
		// check for files in this directory
		File[] tdbFiles = datastore.listFiles(new FileListFilter());
		
		if(tdbFiles.length == 0) {
			System.err.println("ERROR: The specified datastore directory appears to be empty");
			return false;
		}		
	
		// gain access to the model
		Model model = TDBFactory.createModel(datastorePath);
		
		// declare a PrintWriter for output
		PrintWriter output = null;
		
		// load the query into a file
		try {
			System.out.println("INFO: Exporting entire dataset as a de-duplicated Edge List file:");
			
			// open the output file
			output = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outputFile), "UTF8"));
			
		} catch (java.io.FileNotFoundException ex) {
			System.err.println("ERROR: Unable to write to output file");
			System.err.println("       " + outputFile.getAbsolutePath());
			return false;
		} catch (java.io.IOException ex) {
			System.err.println("ERROR: Unable to write to output file");
			System.err.println("       " + outputFile.getAbsolutePath());
			return false;
		}
		
		// define the query
		String queryString = "PREFIX foaf:       <http://xmlns.com/foaf/0.1/> "
						   + "PREFIX ausestage:  <http://code.google.com/p/aus-e-stage/wiki/AuseStageOntology#> "
						   + "  "
						   + "SELECT ?contributor ?givenName ?familyName ?collaborator ?collabGivenName ?collabFamilyName "
						   + "WHERE {  "
						   + "       ?contributor   foaf:givenName ?givenName ; "
						   + "                      foaf:familyName ?familyName; "
						   + "                      ausestage:hasCollaboration ?collaboration. "
						   + "       ?collaboration ausestage:collaborator ?collaborator. "
						   + "       ?collaborator  foaf:givenName ?collabGivenName; "
						   + "                      foaf:familyName ?collabFamilyName. "
						   + "       FILTER (?collaborator != ?contributor) "
						   + "}";
						   
		// keep track of the count of lines
		int lineCount = 0;
		
		// get a query object
		Query query = QueryFactory.create(queryString, Syntax.syntaxARQ);
		
		// Execute the query and obtain results
		QueryExecution qe = QueryExecutionFactory.create(query, model);
		com.hp.hpl.jena.query.ResultSet results = qe.execSelect();
		
		// declare helper variables
		String   tmp      = null;
		String[] tmps     = null;
		int      firstId  = 0;
		int      secondId = 0;

		// Output query results	
		while(results.hasNext() == true) {
			// get a result
			com.hp.hpl.jena.query.QuerySolution result = results.next();
			
			// first id
			tmp  = result.get("contributor").toString().trim();
			tmps = tmp.split(":");
			
			firstId = Integer.parseInt(tmps[2]);
			
			// second id
			tmp  = result.get("collaborator").toString().trim();
			tmps = tmp.split(":");
			
			secondId = Integer.parseInt(tmps[2]); 

			// determine if we need to add this line			
			if(firstId < secondId) {
				
				// output the data
				output.print(firstId + "\t");
			
				// first givenName
				output.print(result.get("givenName").toString().trim());
				output.print("\t");
		
				// first familyName
				output.print(result.get("familyName").toString().trim());
				output.print("\t");
		
				// knows
				output.print(secondId + "\t");
		
				// second GivenName
				output.print(result.get("collabGivenName").toString().trim());
				output.print("\t");
		
				// second FamilyName
				output.print(result.get("collabFamilyName").toString().trim());
				output.print("\n");	
		
				// increment the line count
				lineCount++;
			}	
		}			

		// Important - free up resources used running the query
		qe.close();
		
		// close the output file
		output.close();
		
		// if we get this far, everything went ok
		System.out.println("INFO: Output file containing " + lineCount + " lines successfully created");
		System.out.println("      " + outputFile.getAbsolutePath());
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



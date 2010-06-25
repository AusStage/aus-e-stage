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
public class ExportNetworkData {

	// declare private class level variables
	private PropertiesManager      settings;             // access the properties / settings
	
	/**
	 * A constructor for this class
	 *
	 * @param dataManager the DatabaseManager class connected to the AusStage database
	 * @param properties  the PropertiesManager providing access to properties and settings
	 */
	public ExportNetworkData(PropertiesManager properties) {
		
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
	 * @param dataFormat the format used to export the data
	 * @param outputFile the file used to write the data to
	 *
	 * @return true if, and only if, the task completes successfully
	 */
	public boolean doTask(String dataFormat, File outputFile) {
	
		// check on the parameters
		if(dataFormat == null || outputFile == null) {
			throw new IllegalArgumentException("ERROR: The parameters to the doTask method cannot be null");
		}
		
		dataFormat = dataFormat.trim();
		
		if(dataFormat.equals("")) {
			throw new IllegalArgumentException("ERROR: The dataFormat parameter cannot be empty");
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
		
		// set a namespace prefixes
		//model.setNsPrefix("FOAF", FOAF.NS);
		
		// get an output stream
		try {
		
			// keep the user informed
			System.out.println("INFO: Exporting datastore into a file with format '" + dataFormat + "'");
			
			// get a new output stream
			BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(outputFile));
			
			// stream the model to this output stream
			model.write(outputStream, dataFormat);
			
			// close the file
			outputStream.close();
			
		} catch (java.io.FileNotFoundException ex) {
			System.err.println("ERROR: Unable to write to output file");
			System.err.println("       " + outputFile.getAbsolutePath());
			return false;
		} catch (java.io.IOException ex) {
			System.err.println("ERROR: Unable to write to output file");
			System.err.println("       " + outputFile.getAbsolutePath());
			return false;
		}		
		
		// if we get this far, everything went ok
		System.out.println("INFO: Export file successfully created");
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



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

// import the Jena related packages
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.*;

/**
 * A Class used to build an RDF based dataset of contributor information
 * as an export from the AusStage database and used as a data source for the 
 * Navigating Networks component
 */
public class BuildNetworkData {

	// declare private class level variables
	private DatabaseManager        database; // access the AusStage database
	private PropertiesManager      settings; // access the properties / settings 
	
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
	 * A method to undertake the task of building the dataset
	 *
	 * @return true if, and only if, the task completes successfully
	 */
	public boolean doTask() {
	
		/* TODO use a persistent model */
        // create an empty Model
		Model model = ModelFactory.createDefaultModel();
		
		// set a namespace prefix
		model.setNsPrefix("foaf", FoAF.NS);
		
		// create a contributor
		Resource contributor = model.createResource("http://drthorweasel.com");
		contributor.addProperty(RDF.type, FoAF.Person);
		contributor.addProperty(FoAF.title, "Dr");
		contributor.addProperty(FoAF.name, "ThorWeasel");
				
		// exploratory code
		// list the statements in the Model
//		StmtIterator iter = model.listStatements();
//
//		// print out the predicate, subject and object of each statement
//		while (iter.hasNext()) {
//			com.hp.hpl.jena.rdf.model.Statement stmt      = iter.nextStatement();  // get next statement
//			Resource  subject   = stmt.getSubject();     // get the subject
//			Property  predicate = stmt.getPredicate();   // get the predicate
//			RDFNode   object    = stmt.getObject();      // get the object
//
//			System.out.print(subject.toString());
//			System.out.print(" " + predicate.toString() + " ");
//			if (object instanceof Resource) {
//			   System.out.print(object.toString());
//			} else {
//				// object is a literal
//				System.out.print(" \"" + object.toString() + "\"");
//			}
//
//			System.out.println(" .");
//		}

		//model.write(System.out);
		model.write(System.out, "RDF/XML-ABBREV");

	
		// if we get this far, everything went OK
		return true;
	} // end the doTask method

} // end class definition

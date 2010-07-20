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

/*
 * import additional libraries
 */

// servlets
import javax.servlet.ServletConfig;

// Jena & TDB related packages
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.query.*;

// import the AusStage vocabularies package
import au.edu.ausstage.vocabularies.*;

/**
 * A class to manage access to data stored in the RDF model
 */
public class DataManager {

	// declare private class variables
	ServletConfig servletConfig             = null; // store a reference to the servlet config
	String sparqlEndpoint                   = null; // store a reference to the SPARQL endpoint
	QueryExecution execution                = null; // an object the executes a query
	com.hp.hpl.jena.query.ResultSet results = null; // resultSet after executing a query

	/** 
	 * Constructor for this class
	 */
	public DataManager (ServletConfig config) {
	
		// store a reference to this ServletConfig for later
		servletConfig = config;
		
		// get the URL to the sparql endpoint
		sparqlEndpoint = config.getServletContext().getInitParameter("sparqlEndpoint");
		
		if(sparqlEndpoint == null) {
			throw new RuntimeException("Unable to load sparqlEndpoint context-param");
		}
		
	} // end constructor
	
	/**
	 * A method to execute a SPARQL Query
	 *
	 * @param sparqlQuery the query to execute
	 *
	 * @return      the results of executing the query
	 */
	public com.hp.hpl.jena.query.ResultSet executeSparqlQuery(String sparqlQuery) {
	
		// get a query object
		//Query query = QueryFactory.create(sparqlQuery, Syntax.syntaxARQ);
		
		// get a query execution object
		execution = QueryExecutionFactory.sparqlService(sparqlEndpoint, sparqlQuery);
		
		// execute the query
		results = execution.execSelect();
		
		// return the results of the execution
		return results;
	
	} // end executeSparqlQuery method
	
	/**
	 * A method to tidy up resources after finished with a query
	 */
	public void tidyUp() {
	
		if(results != null) {
			results = null;
		}
		
		if(execution != null) {
			execution.close();
		}
	
	} // end tidyUp method
	
	/**
	 * Finalize method to be run when the object is destroyed
	 * plays nice and free up Oracle connection resources etc. 
	 */
	protected void finalize() throws Throwable {
		if(execution != null) {
			execution.close();
		}
	} // end finalize method
	
} // end class definition

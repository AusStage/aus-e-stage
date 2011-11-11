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

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.tdb.TDBFactory;

/**
 * A class to manage access to data stored in the RDF model
 */
public class DataManager {

	// declare private class variables
	ServletConfig  servletConfig = null; // store a reference to the servlet config
	String         datastorePath = null; // store a reference to the location of the tdb datastore
	String         accessMethod  = null; // determine how to access the RDF data
	Dataset        dataset       = null; // dataset object representing local data store
	Query          query         = null; // query object to execute query locallly
	QueryExecution execution     = null; // an object the executes a query
	ResultSet      results       = null; // resultSet after executing a query

	/** 
	 * Constructor for this class
	 */
	public DataManager (ServletConfig config) {
	
		// store a reference to this ServletConfig for later
		servletConfig = config;
		
		// determine how to access the RDF data
		accessMethod = config.getServletContext().getInitParameter("rdfAccessMethod");
		
		if(accessMethod != null) {
			// determine access method
			if(accessMethod.equals("http") == true) {
				// use the defined sparql endpoint
				datastorePath = config.getServletContext().getInitParameter("sparqlEndpoint");
				
				if(datastorePath == null) {
					throw new RuntimeException("Unable to load sparqlEndpoint context-param");
				}
			} else if(accessMethod.equals("local") == true) {
				// use the defined TDB datastore locally
				datastorePath = config.getServletContext().getInitParameter("localDataStore");
				
				if(datastorePath == null) {
					throw new RuntimeException("Unable to load localDataStore context-param");
				}
			}
			
			// double check on the parameter
			
		} else {
			throw new RuntimeException("Unable to load rdfAccessMethod context-param");
		}
		
	} // end constructor
	
	public DataManager (){
		
//		accessMethod = "local";
//		datastorePath = "C:\\rdf-data\\tdb-data-store";
		
		accessMethod = "http";
//		datastorePath = "http://beta.ausstage.edu.au/networks/sparql";
//		datastorePath = "http://gamma.ausstage.edu.au/joseki/sparql";
		datastorePath = "http://rdf.csem.flinders.edu.au/joseki/networks";
	}
	
	/**
	 * A method to execute a SPARQL Query
	 *
	 * @param sparqlQuery the query to execute
	 *
	 * @return      the results of executing the query
	 */
	public ResultSet executeSparqlQuery(String sparqlQuery) {
	
		// determine how to execute the query
		if(accessMethod.equals("http") == true) {
			return executeViaEndpoint(sparqlQuery);
		} else {
			return executeViaLocal(sparqlQuery);
		}
	
	} // end executeSparqlQuery method
	
	/**
	 * A private method to execute a query locally
	 *
	 * @param sparqlQuery the query to execute
	 *
	 * @return      the results of executing the query
	 */
	private ResultSet executeViaLocal(String sparqlQuery) {
	
		// play nice and tidy up
		//tidyUp(false);
		
		// connect to the dataset
		if(dataset == null) {
			dataset = TDBFactory.createDataset(datastorePath);
		}
		
		// build the query and query execution objects
		query = QueryFactory.create(sparqlQuery, Syntax.syntaxARQ);
		execution = QueryExecutionFactory.create(query, dataset);
		
		// execute the query
		results = execution.execSelect();
		
		// return the results of the execution
		return results;
	
	} // end executeSparqlQuery method	 
	 
	
	/**
	 * A private method to execute a query via the SPARQL endpoint
	 *
	 * @param sparqlQuery the query to execute
	 *
	 * @return      the results of executing the query
	 */
	private ResultSet executeViaEndpoint(String sparqlQuery) {
	
		// play nice and tidy up
		//tidyUp(false);
	
		// get a query execution object		
		query = QueryFactory.create(sparqlQuery, Syntax.syntaxARQ);
		execution = QueryExecutionFactory.sparqlService(datastorePath, query);
		
		// execute the query
		results = execution.execSelect();
		
		// return the results of the execution
		return results;
	
	} // end executeSparqlQuery method	 
	
	/**
	 * A method to tidy up resources after finished with a query
	 *
	 * @param fullTidy true, if and only if, a full tidy is required
	 */
	public void tidyUp(boolean fullTidy) {
		
		if(fullTidy == true) {
			if(results != null) {
				results = null;
			}
		
			if(execution != null) {
				execution.close();
				execution = null;
			}
		
			if(query != null) {
				query = null;
			}		
			if(dataset != null) {
				dataset.close();
				dataset = null;
			}
		}	
	}
	/**
	 * A method to tidy up resources after finished with a query
	 */
	public void tidyUp() {
	
		if (execution != null) {
			execution.close();
			execution = null;
		}
		
		if (dataset != null) {
			dataset.close();
			dataset = null;
		}
		
		query = null;
		results = null;
	} // end tidyUp method
	
	/**
	 * A method used to get the value of a parameter from the ServletConfig object
	 * of the current servlet
	 *
	 * @param name the name of the parameter
	 *
	 * @return     value of the parameter as a string
	 */
	public String getContextParam(String name) {
	
		return servletConfig.getServletContext().getInitParameter(name);
	}
	
	/**
	 * Finalize method to be run when the object is destroyed
	 * plays nice and free up Oracle connection resources etc. 
	 */
	protected void finalize() throws Throwable {
		try {
			tidyUp(true);
			
			if(accessMethod.equals("http") == false) {
				dataset.close();
				
				//release all TDB related resources
				com.hp.hpl.jena.tdb.TDB.closedown();
			}
		} catch (Exception ex) {}
	} // end finalize method
	
} // end class definition

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
 * along with the AusStage Mapping Service.  
 * If not, see <http://www.gnu.org/licenses/>.
*/

package au.edu.ausstage.networks;

/*
 * import additional libraries
 */

// servlets
import javax.servlet.ServletConfig;

// Jena & TDB related packages
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.tdb.*;

// import the AusStage vocabularies package
import au.edu.ausstage.vocabularies.*;

/**
 * A class to manage access to data stored in the RDF model
 */
public class DataManager {

	// declare private class variables
	ServletConfig servletConfig = null; // store a reference to the servlet config
	
	// Jena and TDB related variables
	String datastorePath = null;
	Model model = null;

	/** 
	 * Constructor for this class
	 */
	public DataManager (ServletConfig config) {
	
		// store a reference to this ServletConfig for later
		servletConfig = config;
		
	} // end constructor
	
	/**
	 * Connect to the RDF datastore
	 *
	 * @param  return true if, and only if, the connection was successful
	 */
	public boolean connect() {
	
		// get the config parameter
		datastorePath = servletConfig.getServletContext().getInitParameter("pathToTDB");
		
		if(datastorePath == null) {
			return false;
		}
		
		// connect to the model		
		model = TDBFactory.createModel(datastorePath);
		
		// check the model
		if(model.isEmpty()) {
			// the model should not be empty
			return false;
		}
		
		// if we get this far assume everything is OK
		return true;
			
	} // end connect method


} // end class definition

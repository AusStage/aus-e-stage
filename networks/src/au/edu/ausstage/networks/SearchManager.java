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

// import additional libraries
import com.hp.hpl.jena.query.*;
import java.util.*;
import org.json.simple.*;
import org.apache.commons.lang.StringEscapeUtils;

// import AusStage related packages
import au.edu.ausstage.vocabularies.*;
import au.edu.ausstage.utils.DateUtils;
import au.edu.ausstage.utils.InputUtils;

/**
 * A class to manage the searches for information
 */
public class SearchManager {

	// declare private class level variables
	private DataManager database = null;

	/**
	 * Constructor for this class
	 */
	public SearchManager(DataManager database) {
	
		// store a reference to this DataManager for later
		this.database = database;
	} // end constructor
	
	


} // end class definition

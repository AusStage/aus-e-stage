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

package au.edu.ausstage.vocabularies;

/**
 * A class to represent a custom URI scheme for AusStage data
 */
public class AusStageURI {

	/*
	 * class level constants for URI to URL translation
	 */
	private static final String BASE_CONTRIBUTOR_URL     = "http://www.ausstage.edu.au/indexdrilldown.jsp?xcid=59&f_contrib_id=[contrib-id]";
	private static final String BASE_CONTIRBUTOR_URL_TAG = "[contrib-id]";
	
	private static final String AUSSTAGE_URI_PREFIX      = "ausstage";
	private static final String CONTRIBUTOR_URI          = "c";
	
	/**
	 * A method to take an AusStage URI and return an AusStage URL
	 *
	 * @param uri the AusStage URI to resolve
	 * @return    the AusStage URL
	 */
	public static String getURL(String uri) {	
	
		// double check the parameter
		// null values are not allowed
		if(uri == null) {
			throw new IllegalArgumentException("URI cannot be null");
		}
	
		// trim the string
		uri = uri.trim();
	
		// check for nulls again
		if(uri.equals("")) {
			throw new IllegalArgumentException("URI cannot be empty");
		}
		
		// turn the URI into the component parts
		String[] elements = uri.split(":");
		
		if(elements.length != 3) {
			throw new IllegalArgumentException("Expected 3 components to the URI, found: " + elements.length);
		}
		
		// check on the first part of the URI
		if(elements[0].equals(AUSSTAGE_URI_PREFIX) == false) {
			throw new IllegalArgumentException("First component of URI expected to be '" + AUSSTAGE_URI_PREFIX + "' found: " + elements[0]);
		}
		
		// check on the second component of the URI
		if(elements[1].equals(CONTRIBUTOR_URI) == true) {
			// build the contributor URL
			return BASE_CONTRIBUTOR_URL.replace(BASE_CONTIRBUTOR_URL_TAG, elements[3]);
		} else {
			throw new IllegalArgumentException("Second component of URI '" + elements[2] + "' is not known");
		}
		
	} // end getURL method
	
	/**
	 * A method to build an AusStage URI for a contributor
	 *
	 * @param id  the contributor id
	 * @return    the AusStage URI
	 */
	public static String getContributorURI(String id) {
		// double check the parameter
		// null values are not allowed
		if(id == null) {
			throw new IllegalArgumentException("URI cannot be null");
		}
	
		// trim the string
		id = id.trim();
	
		// check for nulls again
		if(id.equals("")) {
			throw new IllegalArgumentException("URI cannot be empty");
		}
		
		// return the construted URI
		return AUSSTAGE_URI_PREFIX + ":" + CONTRIBUTOR_URI + ":" + id;
	}
	
	/**
	 * A method to build an AusStage URL for a contributor
	 *
	 * @param id the contributor id
	 * @return   the AusStage URL
	 */
	public static String getContributorURL(String id) {
	
		// double check the parameter
		// null values are not allowed
		if(id == null) {
			throw new IllegalArgumentException("URI cannot be null");
		}
	
		// trim the string
		id = id.trim();
	
		// check for nulls again
		if(id.equals("")) {
			throw new IllegalArgumentException("URI cannot be empty");
		}
		
		// build the contributor URL
		return BASE_CONTRIBUTOR_URL.replace(BASE_CONTIRBUTOR_URL_TAG, id);
	
	}
}

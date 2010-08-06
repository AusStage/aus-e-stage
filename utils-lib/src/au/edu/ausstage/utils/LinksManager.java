/*
 * This file is part of the AusStage Utilities Package
 *
 * The AusStage Utilities Package is free software: you can redistribute
 * it and/or modify it under the terms of the GNU General Public License 
 * as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * The AusStage Utilities Package is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the AusStage Utilities Package.  
 * If not, see <http://www.gnu.org/licenses/>.
*/

package au.edu.ausstage.utils;

/**
 * A class of methods useful when compiling links into the AusStage website
 */
public class LinksManager {

	// declare class level variables
	
	// event URLs
	private static final String EVENT_TEMPLATE = "http://www.ausstage.edu.au/indexdrilldown.jsp?xcid=59&amp;f_event_id=[event-id]";
	private static final String EVENT_TOKEN    = "[event-id]";
	
	// venue URLs
	private static final String VENUE_TEMPLATE = "http://www.ausstage.edu.au/indexdrilldown.jsp?xcid=59&amp;f_venue_id=[venue-id]";
	private static final String VENUE_TOKEN    = "[venue-id]";
	
	// contributor URLs
	private static final String CONTRIBUTOR_TEMPLATE = "http://www.ausstage.edu.au/indexdrilldown.jsp?xcid=59&amp;f_contrib_id=[contrib-id]";
	private static final String CONTRIBUTOR_TOKEN    = "[contrib-id]";
	
	// organisation URLs
	private static final String ORGANISATION_TEMPLATE = "http://www.ausstage.edu.au/indexdrilldown.jsp?xcid=59&amp;f_org_id=[org-id]";
	private static final String ORGANISATION_TOKEN    = "[org-id]";
	
	/**
	 * A method to build an event link
	 *
	 * @param id the event id
	 * @return   the persistent URL for this event
	 */
	public static String getEventLink(String id) {
		// double check the parameter
		if(InputUtils.isValidInt(id) == false) {
			throw new IllegalArgumentException("The id parameter must be a valid integer");
		} else {
			return EVENT_TEMPLATE.replaceAll(EVENT_TOKEN, id);
		}
	} // end the method
	
	/**
	 * A method to build a venue link
	 *
	 * @param id the event id
	 * @return   the persistent URL for this venue
	 */
	public static String getVenueLink(String id) {
		// double check the parameter
		if(InputUtils.isValidInt(id) == false) {
			throw new IllegalArgumentException("The id parameter must be a valid integer");
		} else {
			return VENUE_TEMPLATE.replaceAll(VENUE_TOKEN, id);
		}
	} // end the method
	
	/**
	 * A method to build a contributor link
	 *
	 * @param id the event id
	 * @return   the persistent URL for this venue
	 */
	public static String getContributorLink(String id) {
		// double check the parameter
		if(InputUtils.isValidInt(id) == false) {
			throw new IllegalArgumentException("The id parameter must be a valid integer");
		} else {
			return CONTRIBUTOR_TEMPLATE.replaceAll(CONTRIBUTOR_TOKEN, id);
		}
	} // end the method
	
	/**
	 * A method to build a contributor link
	 *
	 * @param id the event id
	 * @return   the persistent URL for this venue
	 */
	public static String getOrganisationLink(String id) {
		// double check the parameter
		if(InputUtils.isValidInt(id) == false) {
			throw new IllegalArgumentException("The id parameter must be a valid integer");
		} else {
			return ORGANISATION_TEMPLATE.replaceAll(ORGANISATION_TOKEN, id);
		}
	} // end the method
	
} // end class definition

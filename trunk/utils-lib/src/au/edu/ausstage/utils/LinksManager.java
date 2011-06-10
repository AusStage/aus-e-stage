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
	
	// resource / item URLs
	private static final String RESOURCE_TEMPLATE = "http://www.ausstage.edu.au/indexdrilldown.jsp?xcid=59&amp;f_item_cat_id=[item-id]";
	private static final String RESOURCE_TOKEN    = "[item-id]";
	
	// performance feedback URLs
	private static final String PERFORMANCE_TEMPLATE = "http://beta.ausstage.edu.au/mobile/mobile-vis/list.html?performance=[performance-id]";
	private static final String PERFORMANCE_TOKEN    = "[performance-id]";
	
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
			return EVENT_TEMPLATE.replace(EVENT_TOKEN, id);
		}
	} // end the method
	
	/**
	 * A method to build a venue link
	 *
	 * @param id the venue id
	 * @return   the persistent URL for this venue
	 */
	public static String getVenueLink(String id) {
		// double check the parameter
		if(InputUtils.isValidInt(id) == false) {
			throw new IllegalArgumentException("The id parameter must be a valid integer");
		} else {
			return VENUE_TEMPLATE.replace(VENUE_TOKEN, id);
		}
	} // end the method
	
	/**
	 * A method to build a contributor link
	 *
	 * @param id the contributor id
	 * @return   the persistent URL for this contributor
	 */
	public static String getContributorLink(String id) {
		// double check the parameter
		if(InputUtils.isValidInt(id) == false) {
			throw new IllegalArgumentException("The id parameter must be a valid integer");
		} else {
			return CONTRIBUTOR_TEMPLATE.replace(CONTRIBUTOR_TOKEN, id);
		}
	} // end the method
	
	/**
	 * A method to build a contributor link
	 *
	 * @param id the organisation id
	 * @return   the persistent URL for this organisation
	 */
	public static String getOrganisationLink(String id) {
		// double check the parameter
		if(InputUtils.isValidInt(id) == false) {
			throw new IllegalArgumentException("The id parameter must be a valid integer");
		} else {
			return ORGANISATION_TEMPLATE.replace(ORGANISATION_TOKEN, id);
		}
	} // end the method
	
	/**
	 * A method to build a resource / item link
	 *
	 * @param id the resource id
	 * @return   the persistent URL for this resource
	 */
	public static String getResourceLink(String id) {
		// double check the parameter
		if(InputUtils.isValidInt(id) == false) {
			throw new IllegalArgumentException("The id parameter must be a valid integer");
		} else {
			return RESOURCE_TEMPLATE.replace(RESOURCE_TOKEN, id);
		}
	} // end the method
	
	/**
	 * A method to build a performance
	 *
	 * @param id the resource id
	 * @return   the persistent URL for this resource
	 */
	public static String getPerformanceLink(String id) {
		// double check the parameter
		if(InputUtils.isValidInt(id) == false) {
			throw new IllegalArgumentException("The id parameter must be a valid integer");
		} else {
			return PERFORMANCE_TEMPLATE.replace(PERFORMANCE_TOKEN, id);
		}
	} // end the method
	
} // end class definition

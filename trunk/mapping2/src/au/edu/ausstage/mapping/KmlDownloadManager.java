/*
 * This file is part of the AusStage Mapping Service
 *
 * The AusStage Mapping Service is free software: you can redistribute
 * it and/or modify it under the terms of the GNU General Public License 
 * as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * The AusStage Mapping Service is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the AusStage Mapping Service.  
 * If not, see <http://www.gnu.org/licenses/>.
 */
 
package au.edu.ausstage.mapping;

// import additional AusStage libraries
import au.edu.ausstage.utils.*;
import au.edu.ausstage.mapping.types.*;

// import other Java classes / libraries
import java.sql.ResultSet;
import java.io.PrintWriter;
import org.w3c.dom.Element;
import java.util.Set;
import java.util.Iterator;
import java.util.HashMap;
import java.util.TreeSet;

/**
 * A class used to prepare KML ready for download
 */
public class KmlDownloadManager {

	// declare private class variables
	DbManager database;
	KmlDataBuilder builder;
	
	/**
	 * Constructor for this class
	 *
	 * @param database a valid DbManager object
	 */
	public KmlDownloadManager(DbManager database) {
		this.database = database;
	}
	
	/**
	 * Prepare KML that includes the supplied contributors, organisations, venues and events
	 *
	 * @param contributors a list of contributor ids
	 * @param organisations a list of organisation ids
	 * @param venues a list of venue ids
	 * @param events a list of event ids
	 *
	 * @throws KmlDownloadException if something bad happens
	 */
	public void prepare(String[] contributors, String[] organisations, String[] venues, String[] events) throws KmlDownloadException {
	
		// instantiate the required object
		builder = new KmlDataBuilder();
	
		if(contributors.length > 0) {
			addContributors(contributors);
		}
		
		if(organisations.length > 0) {
			addOrganisations(organisations);
		}
		
		if(venues.length > 0) {
			addVenues(venues);
		}
		
		if(events.length > 0) {
			addEvents(events);
		}
	}
	
	//private method to add the contributors
	private void addContributors(String[] ids) throws KmlDownloadException {
	
		// declare helper variables
		String sql;
		
		DbObjects results;
		
		ContributorList  contributors  = new ContributorList();
		Contributor      contributor   = null;
		String[]         functions     = null;
		String           list          = null;
					
		// get the list of contributors
		if(ids.length == 1) {
			sql = "SELECT c.contributorid, first_name, last_name, "
				+ "       rtrim(xmlagg(xmlelement(t, preferredterm || '|')).extract ('//text()'), '|') AS functions "
				+ "FROM contributor c, contfunctlink, contributorfunctpreferred "
				+ "WHERE c.contributorid = contfunctlink.contributorid "
				+ "AND contfunctlink.contributorfunctpreferredid = contributorfunctpreferred.contributorfunctpreferredid "
				+ "AND c.contributorid = ? "
				+ "GROUP BY c.contributorid, first_name, last_name ";
		} else {
			sql = "SELECT c.contributorid, first_name, last_name, "
				+ "       rtrim(xmlagg(xmlelement(t, preferredterm || '|')).extract ('//text()'), '|') AS functions "
				+ "FROM contributor c, contfunctlink, contributorfunctpreferred "
				+ "WHERE c.contributorid = contfunctlink.contributorid "
				+ "AND contfunctlink.contributorfunctpreferredid = contributorfunctpreferred.contributorfunctpreferredid "
				+ "AND c.contributorid = ANY (";
			    
			    // add sufficient place holders for all of the ids
				for(int i = 0; i < ids.length; i++) {
					sql += "?,";
				}

				// tidy up the sql
				sql = sql.substring(0, sql.length() -1);
				
				// finalise the sql string
				sql += ") ";
				sql += "GROUP BY c.contributorid, first_name, last_name ";
		}
		
		// get the data
		results = database.executePreparedStatement(sql, ids);
	
		// check to see that data was returned
		if(results == null) {
			throw new KmlDownloadException("unable to lookup contributor data");
		}
		
		// build the list of contributors
		ResultSet resultSet = results.getResultSet();
		try {
			while (resultSet.next()) {
				contributor = new Contributor(resultSet.getString(1), resultSet.getString(2) + " " + resultSet.getString(3), LinksManager.getContributorLink(resultSet.getString(1)));
				
				contributor.setFirstName(resultSet.getString(2));
				contributor.setLastName(resultSet.getString(3));
				
				contributor.setFunctions(resultSet.getString(4));
				functions = contributor.getFunctionsAsArray();
				list = "";
				
				for(int i = 0; i < functions.length; i++) {
					list += functions[i] + ", ";
				}
				
				list = list.substring(0, list.length() -2);
				
				contributor.setFunctions(list);
				
				contributors.addContributor(contributor);
				
			}
		} catch (java.sql.SQLException ex) {
			throw new KmlDownloadException("unable to build list of contributors: " + ex.toString());
		}
		
		// play nice and tidy up
		resultSet = null;
		results.tidyUp();
		results = null;
		
		
		// get the events for each contributor
		Set<Contributor> contributorSet = contributors.getContributors();
		Iterator   iterator = contributorSet.iterator();
		Event       event;
		String      venue;
		String[]    sortDates;
		
		KmlVenue kmlVenue;
		HashMap<Integer, KmlVenue> kmlVenues;
		
		sql = "SELECT e.eventid, e.event_name, e.yyyyfirst_date, e.mmfirst_date, e.ddfirst_date, "
		    + "       e.yyyylast_date, e.mmlast_date, e.ddlast_date, "
			+ "       v.venueid, v.venue_name, v.street, v.suburb, s.state, v.postcode, "
			+ "       c.countryname, v.latitude, v.longitude "
			+ "FROM events e, conevlink cl, venue v, country c, states s "
			+ "WHERE cl.contributorid = ? "
			+ "AND cl.eventid = e.eventid "
			+ "AND e.venueid = v.venueid "
			+ "AND v.latitude IS NOT NULL "
			+ "AND v.countryid = c.countryid (+) "
			+ "AND v.state = s.stateid (+)";
			
		String sqlParameters[] = new String[1];
		
		while(iterator.hasNext()) {
			contributor = (Contributor)iterator.next();	
			
			kmlVenues = new HashMap<Integer, KmlVenue>();		
			
			sqlParameters[0] = contributor.getId();
			
			// get the data
			results = database.executePreparedStatement(sql, sqlParameters);
	
			// check to see that data was returned
			if(results == null) {
				throw new KmlDownloadException("unable to lookup event data for contributor: " + contributor.getId());
			}
			
			// build the list of events and add them to this contributor
			resultSet = results.getResultSet();
			try {
				while (resultSet.next()) {
				
					// check to see if we've seen this venue before
					if(kmlVenues.containsKey(Integer.parseInt(resultSet.getString(9))) == true) {
						kmlVenue = kmlVenues.get(Integer.parseInt(resultSet.getString(9)));
					} else {
						//KmlVenue(String id, String name, String address, String latitude, String longitude)
						kmlVenue = new KmlVenue(resultSet.getString(9), resultSet.getString(10), buildVenueAddress(resultSet.getString(15), resultSet.getString(11), resultSet.getString(12), resultSet.getString(13)), buildShortVenueAddress(resultSet.getString(15), resultSet.getString(11), resultSet.getString(12), resultSet.getString(13)), resultSet.getString(16), resultSet.getString(17));
						kmlVenues.put(Integer.parseInt(resultSet.getString(9)), kmlVenue);
					}
									
					// build the event
					event = new Event(resultSet.getString(1));
					event.setName(resultSet.getString(2));
					event.setFirstDisplayDate(DateUtils.buildDisplayDate(resultSet.getString(3), resultSet.getString(4), resultSet.getString(5)));
					
					sortDates = DateUtils.getDatesForTimeline(resultSet.getString(3), resultSet.getString(4), resultSet.getString(5), resultSet.getString(6), resultSet.getString(7), resultSet.getString(8));
					event.setSortFirstDate(sortDates[0]);
					event.setSortLastDate(sortDates[1]);
					
					event.setUrl(LinksManager.getEventLink(resultSet.getString(1)));
					
					kmlVenue.addEvent(event);								
				}
				
				contributor.setKmlVenues(kmlVenues);
				
			} catch (java.sql.SQLException ex) {
				throw new KmlDownloadException("unable to build list of events for contributor '" + contributor.getId() + "' " + ex.toString());
			}
			
			// play nice and tidy up
			resultSet = null;
			results.tidyUp();
			results = null;
		}
		
		// add the data to the KML download
		builder.addContributors(contributors);
	}
	
	// private method to add organisation data to the KML
	private void addOrganisations(String[] ids) throws KmlDownloadException {
	
		// declare helper variables
		String sql;
		
		DbObjects results;
		
		OrganisationList  organisations  = new OrganisationList();
		Organisation      organisation   = null;
					
		// get the list of contributors
		if(ids.length == 1) {
			sql = "SELECT o.organisationid, o.name, o.address, o.suburb, s.state, c.countryname "
				+ "FROM organisation o, states s, country c "
				+ "WHERE o.organisationid = ? "
				+ "AND o.state = s.stateid "
				+ "AND o.countryid = c.countryid";
		} else {
			sql = "SELECT o.organisationid, o.name, o.address, o.suburb, s.state, c.countryname "
				+ "FROM organisation o, states s, country c "
				+ "WHERE o.organisationid = ANY (";
			    
			    // add sufficient place holders for all of the ids
				for(int i = 0; i < ids.length; i++) {
					sql += "?,";
				}

				// tidy up the sql
				sql = sql.substring(0, sql.length() -1);
				
				// finalise the sql string
				sql += ") ";
				sql += "AND o.state = s.stateid ";
				sql += "AND o.countryid = c.countryid";
		}
		
		// get the data
		results = database.executePreparedStatement(sql, ids);
	
		// check to see that data was returned
		if(results == null) {
			throw new KmlDownloadException("unable to lookup organisation data");
		}
		
		// build the list of contributors
		ResultSet resultSet = results.getResultSet();
		try {
			while (resultSet.next()) {
				organisation = new Organisation(resultSet.getString(1), resultSet.getString(2), LinksManager.getOrganisationLink(resultSet.getString(1)));
				
				organisation.setAddress(buildShortVenueAddress(resultSet.getString(6), resultSet.getString(3), resultSet.getString(4), resultSet.getString(5)));			
				organisations.addOrganisation(organisation);
				
			}
		} catch (java.sql.SQLException ex) {
			throw new KmlDownloadException("unable to build list of organisations: " + ex.toString());
		}
		
		// play nice and tidy up
		resultSet = null;
		results.tidyUp();
		results = null;
		
		
		// get the events for each contributor
		Set<Organisation> organisationSet = organisations.getOrganisations();
		Iterator   iterator = organisationSet.iterator();
		Event       event;
		String      venue;
		String[]    sortDates;
		
		KmlVenue kmlVenue;
		HashMap<Integer, KmlVenue> kmlVenues;
		
		sql = "SELECT e.eventid, e.event_name, e.yyyyfirst_date, e.mmfirst_date, e.ddfirst_date, "
			+ "       e.yyyylast_date, e.mmlast_date, e.ddlast_date, "
			+ "       v.venueid, v.venue_name, v.street, v.suburb, s.state, v.postcode, "
			+ "       c.countryname, v.latitude, v.longitude "
			+ "FROM events e, orgevlink ol, venue v, country c, states s "
			+ "WHERE ol.organisationid = ? " 
			+ "AND ol.eventid = e.eventid "
			+ "AND e.venueid = v.venueid "
			+ "AND v.latitude IS NOT NULL "
			+ "AND v.countryid = c.countryid (+) "
			+ "AND v.state = s.stateid (+)";
			
		String sqlParameters[] = new String[1];
		
		while(iterator.hasNext()) {
			organisation = (Organisation)iterator.next();	
			
			kmlVenues = new HashMap<Integer, KmlVenue>();		
			
			sqlParameters[0] = organisation.getId();
			
			// get the data
			results = database.executePreparedStatement(sql, sqlParameters);
	
			// check to see that data was returned
			if(results == null) {
				throw new KmlDownloadException("unable to lookup event data for organisation: " + organisation.getId());
			}
			
			// build the list of events and add them to this contributor
			resultSet = results.getResultSet();
			try {
				while (resultSet.next()) {
				
					// check to see if we've seen this venue before
					if(kmlVenues.containsKey(Integer.parseInt(resultSet.getString(9))) == true) {
						kmlVenue = kmlVenues.get(Integer.parseInt(resultSet.getString(9)));
					} else {
						//KmlVenue(String id, String name, String address, String latitude, String longitude)
						kmlVenue = new KmlVenue(resultSet.getString(9), resultSet.getString(10), buildVenueAddress(resultSet.getString(15), resultSet.getString(11), resultSet.getString(12), resultSet.getString(13)), buildShortVenueAddress(resultSet.getString(15), resultSet.getString(11), resultSet.getString(12), resultSet.getString(13)), resultSet.getString(16), resultSet.getString(17));
						kmlVenues.put(Integer.parseInt(resultSet.getString(9)), kmlVenue);
					}
									
					// build the event
					event = new Event(resultSet.getString(1));
					event.setName(resultSet.getString(2));
					event.setFirstDisplayDate(DateUtils.buildDisplayDate(resultSet.getString(3), resultSet.getString(4), resultSet.getString(5)));
					
					sortDates = DateUtils.getDatesForTimeline(resultSet.getString(3), resultSet.getString(4), resultSet.getString(5), resultSet.getString(6), resultSet.getString(7), resultSet.getString(8));
					event.setSortFirstDate(sortDates[0]);
					event.setSortLastDate(sortDates[1]);
					
					event.setUrl(LinksManager.getEventLink(resultSet.getString(1)));
					
					kmlVenue.addEvent(event);								
				}
				
				organisation.setKmlVenues(kmlVenues);
				
			} catch (java.sql.SQLException ex) {
				throw new KmlDownloadException("unable to build list of events for organisation '" + organisation.getId() + "' " + ex.toString());
			}
			
			// play nice and tidy up
			resultSet = null;
			results.tidyUp();
			results = null;
		}
		
		// add the data to the KML download
		builder.addOrganisations(organisations);
	}
	
	// private method to add organisation data to the KML
	private void addVenues(String[] ids) throws KmlDownloadException {
	
		// declare helper variables
		String sql;
		
		DbObjects results;
		Event       event;
		String[]    sortDates;
		
		KmlVenue kmlVenue;
		HashMap<Integer, KmlVenue> kmlVenues = new HashMap<Integer, KmlVenue>();
					
		// get the list of contributors
		if(ids.length == 1) {
			sql = "SELECT v.venueid, v.venue_name, v.street, v.suburb, s.state, c.countryname, v.latitude, v.longitude "
				+ "FROM venue v, states s, country c "
				+ "WHERE v.venueid = ? "
				+ "AND v.state = s.stateid "
				+ "AND v.countryid = c.countryid";
		} else {
			sql = "SELECT v.venueid, v.venue_name, v.street, v.suburb, s.state, c.countryname, v.latitude, v.longitude "
				+ "FROM venue v, states s, country c "
				+ "WHERE v.venueid = ANY (";
			    
			    // add sufficient place holders for all of the ids
				for(int i = 0; i < ids.length; i++) {
					sql += "?,";
				}

				// tidy up the sql
				sql = sql.substring(0, sql.length() -1);
				
				// finalise the sql string
				sql += ") ";
				sql += "AND v.state = s.stateid ";
				sql += "AND v.countryid = c.countryid";
		}
		
		// get the data
		results = database.executePreparedStatement(sql, ids);
	
		// check to see that data was returned
		if(results == null) {
			throw new KmlDownloadException("unable to lookup organisation data");
		}
		
		// build the list of contributors
		ResultSet resultSet = results.getResultSet();
		try {
			while (resultSet.next()) {
				kmlVenue = new KmlVenue(resultSet.getString(1), resultSet.getString(2), buildVenueAddress(resultSet.getString(6), resultSet.getString(3), resultSet.getString(4), resultSet.getString(5)), buildShortVenueAddress(resultSet.getString(6), resultSet.getString(3), resultSet.getString(4), resultSet.getString(5)), resultSet.getString(7), resultSet.getString(8));
				kmlVenues.put(Integer.parseInt(resultSet.getString(1)), kmlVenue);
				
			}
		} catch (java.sql.SQLException ex) {
			throw new KmlDownloadException("unable to build list of organisations: " + ex.toString());
		}
		
		// play nice and tidy up
		resultSet = null;
		results.tidyUp();
		results = null;
		
		sql = "SELECT e.eventid, e.event_name, e.yyyyfirst_date, e.mmfirst_date, e.ddfirst_date, "
			+ "       e.yyyylast_date, e.mmlast_date, e.ddlast_date, "
			+ "       v.venueid, v.venue_name, v.street, v.suburb, s.state, v.postcode, "
			+ "       c.countryname, v.latitude, v.longitude "
			+ "FROM events e, venue v, country c, states s "
			+ "WHERE v.venueid = ? "
			+ "AND e.venueid = v.venueid "
			+ "AND v.latitude IS NOT NULL "
			+ "AND v.countryid = c.countryid (+) "
			+ "AND v.state = s.stateid (+)";
			
		String sqlParameters[] = new String[1];
		
		Set<KmlVenue> venues = new TreeSet<KmlVenue>(new KmlVenueComparator());
		venues.addAll(kmlVenues.values());
		Iterator venueIterator = venues.iterator();
		
		while(venueIterator.hasNext()) {
			
			kmlVenue = (KmlVenue)venueIterator.next();
			
			sqlParameters[0] = Integer.toString(kmlVenue.getId());
			
			// get the data
			results = database.executePreparedStatement(sql, sqlParameters);
	
			// check to see that data was returned
			if(results == null) {
				throw new KmlDownloadException("unable to lookup event data for venue: " + kmlVenue.getId());
			}
			
			// build the list of events and add them to this contributor
			resultSet = results.getResultSet();
			try {
				while (resultSet.next()) {
							
					// build the event
					event = new Event(resultSet.getString(1));
					event.setName(resultSet.getString(2));
					event.setFirstDisplayDate(DateUtils.buildDisplayDate(resultSet.getString(3), resultSet.getString(4), resultSet.getString(5)));
					
					sortDates = DateUtils.getDatesForTimeline(resultSet.getString(3), resultSet.getString(4), resultSet.getString(5), resultSet.getString(6), resultSet.getString(7), resultSet.getString(8));
					event.setSortFirstDate(sortDates[0]);
					event.setSortLastDate(sortDates[1]);
					
					event.setUrl(LinksManager.getEventLink(resultSet.getString(1)));
					
					kmlVenue.addEvent(event);								
				}
				
			} catch (java.sql.SQLException ex) {
				throw new KmlDownloadException("unable to build list of events for venue '" + kmlVenue.getId() + "' " + ex.toString());
			}
			
			// play nice and tidy up
			resultSet = null;
			results.tidyUp();
			results = null;
		}
		
		// add the data to the KML download
		builder.addVenues(venues);
	}
	
	// private method to add organisation data to the KML
	private void addEvents(String[] ids) throws KmlDownloadException {
	
		// declare helper variables
		String sql;
		
		DbObjects results;
		Event       event;
		String[]    sortDates;
		
		KmlEvent kmlEvent;
		TreeSet<KmlEvent> kmlEvents = new TreeSet<KmlEvent>(new KmlEventNameComparator());
					
		// get the list of contributors
		if(ids.length == 1) {
			sql = "SELECT e.eventid, e.event_name, e.yyyyfirst_date, e.mmfirst_date, e.ddfirst_date, "
				+ "       e.yyyylast_date, e.mmlast_date, e.ddlast_date, "
				+ "       v.venueid, v.venue_name, v.street, v.suburb, s.state, v.postcode, "
				+ "       c.countryname, v.latitude, v.longitude "
				+ "FROM events e, venue v, country c, states s "
				+ "WHERE e.eventid = ? "
				+ "AND e.venueid = v.venueid "
				+ "AND v.latitude IS NOT NULL "
				+ "AND v.countryid = c.countryid (+) "
				+ "AND v.state = s.stateid (+)";
		} else {
			sql = "SELECT e.eventid, e.event_name, e.yyyyfirst_date, e.mmfirst_date, e.ddfirst_date, "
				+ "       e.yyyylast_date, e.mmlast_date, e.ddlast_date, "
				+ "       v.venueid, v.venue_name, v.street, v.suburb, s.state, v.postcode, "
				+ "       c.countryname, v.latitude, v.longitude "
				+ "FROM events e, venue v, country c, states s "
				+ "WHERE e.eventid = ANY (";
			    
			    // add sufficient place holders for all of the ids
				for(int i = 0; i < ids.length; i++) {
					sql += "?,";
				}

				// tidy up the sql
				sql = sql.substring(0, sql.length() -1);
				
				// finalise the sql string
				sql += ") "
					+ "AND e.venueid = v.venueid "
					+ "AND v.latitude IS NOT NULL "
					+ "AND v.countryid = c.countryid (+) "
					+ "AND v.state = s.stateid (+)";
		}
		
		// get the data
		results = database.executePreparedStatement(sql, ids);
	
		// check to see that data was returned
		if(results == null) {
			throw new KmlDownloadException("unable to lookup event data");
		}
		
		// build the list of events
		ResultSet resultSet = results.getResultSet();
		try {
			while (resultSet.next()) {
			
				sortDates = DateUtils.getDatesForTimeline(resultSet.getString(3), resultSet.getString(4), resultSet.getString(5), resultSet.getString(6), resultSet.getString(7), resultSet.getString(8));
			
				kmlEvent = new KmlEvent(resultSet.getString(1));
				
				kmlEvent.setName(resultSet.getString(2));
				kmlEvent.setUrl(LinksManager.getEventLink(resultSet.getString(1)));
				kmlEvent.setFirstDate(DateUtils.buildDisplayDate(resultSet.getString(3), resultSet.getString(4), resultSet.getString(5)));
				kmlEvent.setSortFirstDate(sortDates[0]);
				kmlEvent.setSortLastDate(sortDates[1]);
				kmlEvent.setVenueName(resultSet.getString(10));
				kmlEvent.setVenueUrl(LinksManager.getVenueLink(resultSet.getString(9)));
				kmlEvent.setVenueAddress(buildShortVenueAddress(resultSet.getString(15), resultSet.getString(11), resultSet.getString(12), resultSet.getString(13)));
				kmlEvent.setShortVenueAddress(buildShortVenueAddress(resultSet.getString(15), resultSet.getString(11), resultSet.getString(12), resultSet.getString(13)));
				kmlEvent.setLatitude(resultSet.getString(16));
				kmlEvent.setLongitude(resultSet.getString(17));
				
				kmlEvents.add(kmlEvent);
			}	
		} catch (java.sql.SQLException ex) {
				throw new KmlDownloadException("unable to build list of events: " + ex.toString());
		}
			
		// play nice and tidy up
		resultSet = null;
		results.tidyUp();
		results = null;
		
		// add the data to the KML download
		builder.addEvents(kmlEvents);
	}
	
	// private function to build the venue address
	private String buildVenueAddress(String country, String street, String suburb, String state) {
	
		String address = "";
		
		if(InputUtils.isValid(country) && country.equals("Australia")) {
			if(InputUtils.isValid(street) == true) {
				address += street + ", ";
			}
			
			if(InputUtils.isValid(suburb) == true) {
				address += suburb + ", ";
			}
			
			if(InputUtils.isValid(state) == true) {
				address += state;
			} else {
				address = address.substring(0, address.length() - 2);
			}
			
		} else {
		
			if(InputUtils.isValid(street) == true) {
				address += street + ", ";
			}
			
			if(InputUtils.isValid(suburb) == true) {
				address += suburb + ", ";
			}
			
			if(InputUtils.isValid(country) == true) {
				address += country;
			} else {
				address = address.substring(0, address.length() - 2);
			}
		}
		
		return address;
	}
	
	// private function to build the venue address
	private String buildShortVenueAddress(String country, String street, String suburb, String state) {
	
		String address = "";
		
		if(InputUtils.isValid(country) && country.equals("Australia")) {
		
			if(InputUtils.isValid(suburb) == true) {
				address += suburb + ", ";
			}
			
			if(InputUtils.isValid(state) == true) {
				address += state;
			} else {
				address = address.substring(0, address.length() - 2);
			}
			
		} else {
		
			if(InputUtils.isValid(suburb) == true) {
				address += suburb + ", ";
			}
			
			if(InputUtils.isValid(country) == true) {
				address += country;
			} else {
				address = address.substring(0, address.length() - 2);
			}
		}
		
		return address;
	}
	
	/**
	 * Print the prepared KML to the supplied print writer
	 *
	 * @param writer the print writer to use to print the KML
	 *
	 * @throws KmlDownloadException if something bad happens
	 */
	public void print(PrintWriter writer) throws KmlDownloadException {
	
		builder.print(writer);
	
	}

}

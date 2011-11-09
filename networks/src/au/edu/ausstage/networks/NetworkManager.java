package au.edu.ausstage.networks;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import au.edu.ausstage.networks.types.Collaborator;
import au.edu.ausstage.networks.types.Event;
import au.edu.ausstage.networks.types.Network;
import au.edu.ausstage.utils.DateUtils;
import au.edu.ausstage.vocabularies.AusStageURI;


public class NetworkManager {
	public DatabaseManager db = null;
	public Network network = null;
	
	public int degree [][];
	
	public NetworkManager(DatabaseManager db, Network network){
		this.db = db;
		this.network = network;
		
		if(this.db.connect() == false) {
			throw new RuntimeException("Error: Unable to connect to the database");
		}
	}
	
	
	//given an event ID, get its associated contributors set
	public Set<Integer> getAssociatedContributors(int eventId){
	
		Set<Integer> conSet = new HashSet<Integer>();	
		
		if (network.evtId_conSet_map.containsKey(eventId)) 
			conSet = network.evtId_conSet_map.get(eventId);
			
		else{ 
		
			String sql = "SELECT DISTINCT contributorid "
					+ "FROM conevlink "
					+ "WHERE eventid = ? " 	//+ eventId 					
					+ " ORDER BY contributorid";

			conSet = db.getResultfromDB(sql, eventId);
			if (conSet != null)
				network.contributorSet.addAll(conSet);
		
			network.evtId_conSet_map.put(eventId, conSet);
		}
		
		return conSet;
	}
	
	//given a contributor ID, get its associated events set
	public Set<Integer> getAssociatedEvents(int conId){
	
		Set<Integer> evtSet = new HashSet<Integer>();	
		
		if (network.conId_evtSet_map.containsKey(conId)) 
			evtSet = network.conId_evtSet_map.get(conId);
			
		else{ 
		
			String sql = "SELECT DISTINCT eventid "
					+ "FROM conevlink "
					+ "WHERE contributorid = ? " 	//+ eventId 					
					+ " ORDER BY eventid";

			evtSet = db.getResultfromDB(sql, conId);
			
			if (evtSet != null)
				network.eventSet.addAll(evtSet);
		
			network.conId_evtSet_map.put(conId, evtSet);
		}
		
		return evtSet;
	}
	
	/**
	 * A method to get the details for an event
	 *
	 * @param id 		the event id
	 * 
	 * @return 	a completed event object
	 */
	public Event getEventDetail(int evtId) {
		
		Event evt = null;
		
		if (!network.evtId_evtObj_map.containsKey(evtId)){
			String sql = "SELECT DISTINCT e.eventid, e.event_name, e.first_date, v.venue_name, v.suburb, s.state, c.countryname  "
					+ "FROM events e, venue v, states s, country c "
					+ "WHERE e.eventid = ? " // + evtId
					+ "AND e.venueid = v.venueid "
					+ "AND v.state = s.stateid (+) "
					+ "AND v.countryid = c.countryid (+) "
					+ "ORDER BY e.eventid";

			int[] param = { evtId };

			// execute sql statement
			ResultSet results = db.exePreparedStatement(sql, param);

			try {
				// check to see that data was returned
				if (!results.last()) {
					db.tidyup();
					return null;
				} else
					results.beforeFirst();

				// build the event object
				while (results.next() == true) {
					int e_id = results.getInt(1);
					String name = results.getString(2);
					String date = results.getDate(3).toString();
					String venue = results.getString(4);
					String suburb = results.getString(5);
					String state = results.getString(6);
					String country = results.getString(7);
					String venueDetail = venue;

					evt = new Event(Integer.toString(e_id));
					// evt.setId(resultSet.getString(1));
					evt.setMyName(name);
					evt.setMyFirstDate(date);
					if (suburb != null && !suburb.isEmpty())
						venueDetail = venueDetail + ", " + suburb;
					if (country == null && state != null)
						venueDetail = venueDetail + ", " + state;
					if (country != null && state != null && !state.isEmpty()
							&& country.equalsIgnoreCase("Australia"))
						venueDetail = venueDetail + ", " + state;
					if (country != null && !country.isEmpty()
							&& !country.equalsIgnoreCase("Australia"))
						venueDetail = venueDetail + ", " + country;

					evt.setVenue(venueDetail);

				}
				network.evtId_evtObj_map.put(evtId, evt);

			} catch (SQLException e) {
				results = null;
				e.printStackTrace();
			}

			db.tidyup();
			
		} else {
			evt = network.evtId_evtObj_map.get(evtId);
		}
		
		return evt;	
	}
	
	/**
	 * A method to get the details for an event set
	 *
	 * @param set  the event id set
	 * 
	 */
	public void getEventsDetail(Set<Integer> set) {
		
		int[] evtIDArray = new int[set.size()];
		int x = 0;
		for (Integer eID : set) evtIDArray[x++] = eID;

		int SINGLE_BATCH = 1;
		int SMALL_BATCH = 4;
		int MEDIUM_BATCH = 11;
		int LARGE_BATCH = 51;
		int start = 0;
		int totalNumberOfValuesLeftToBatch = set.size();
		Event evt = null;
		//List<Event> evtList = new ArrayList<Event>();
		
		while ( totalNumberOfValuesLeftToBatch > 0 ) {
			
			int batchSize = SINGLE_BATCH;
			if ( totalNumberOfValuesLeftToBatch >= LARGE_BATCH ) {
			  batchSize = LARGE_BATCH;
			} else if ( totalNumberOfValuesLeftToBatch >= MEDIUM_BATCH ) {
			  batchSize = MEDIUM_BATCH;
			} else if ( totalNumberOfValuesLeftToBatch >= SMALL_BATCH ) {
			  batchSize = SMALL_BATCH;
			}
			
			String inClause = new String("");			
			for (int i=0; i < batchSize; i++) {
				inClause = inClause + "? ,";
			}
			inClause = inClause.substring(0, inClause.length()-1);
			
			String sql = "SELECT DISTINCT e.eventid, e.event_name, e.first_date, v.venue_name, v.suburb, s.state, c.countryname "
				+ "FROM events e, venue v, states s, country c "
				+ "WHERE e.eventid in (" + inClause + ") "
				+ "AND e.venueid = v.venueid "
				+ "AND v.state = s.stateid (+) "
				+ "AND v.countryid = c.countryid (+) "	
				+ "ORDER BY e.first_date";
	
			ResultSet results = db.exePreparedINStatement(sql, evtIDArray, start, batchSize);
			
			try{
				if (!results.last()) {
					db.tidyup();
					return;
				} else
				results.beforeFirst();
						
				//build the event object
				while (results.next() == true) {
					int e_id = results.getInt(1);
					String name = results.getString(2);
					String date = results.getDate(3).toString();
					String venue = results.getString(4);
					String suburb = results.getString(5);
					String state = results.getString(6);
					String country = results.getString(7);
					String venueDetail = venue;
					
					evt = new Event(Integer.toString(e_id));
					// evt.setId(resultSet.getString(1));
					evt.setMyName(name);
					evt.setMyFirstDate(date);	
					if (suburb != null && !suburb.isEmpty())
						venueDetail = venueDetail + ", " + suburb;
					if (country == null && state != null) 
						venueDetail = venueDetail + ", " + state;
					if (country != null && state != null && !state.isEmpty() && country.equalsIgnoreCase("Australia"))
						venueDetail = venueDetail + ", " + state;
					if (country != null && !country.isEmpty() && !country.equalsIgnoreCase("Australia"))
						venueDetail = venueDetail + ", " + country;
					
					evt.setVenue(venueDetail);
					//evtList.add(evt);
					network.evtId_evtObj_map.put(e_id, evt);
				}
				
				db.tidyup();
			} catch (SQLException e) {
				results = null;
				db.tidyup();
				e.printStackTrace();
			}
			totalNumberOfValuesLeftToBatch -= batchSize; 			
			start += batchSize;
		}
					
		return ;
		
	}
	
	//given a contributor ID and its source event, get contributor details
	//if this contributor not exist before, then create a new Collaborator instance
	//otherwise add roles to the eventRoleMap
	public Collaborator getContributorDetail(int evtId, int conId){
		
		Collaborator con = null;		
		String c_id = ""; // contributorid
		String f_name = ""; // first_name
		String l_name = ""; // last_name
		String roles = ""; // preferredterm. A contributor may have multiple roles at an event
		
		String sql = "SELECT DISTINCT c.contributorid, c.first_name, c.last_name, cfp.preferredterm "
			+ "FROM conevlink ce, contributor c, contributorfunctpreferred cfp "
			+ "WHERE c.contributorid = ? " //+ conId
			+ " AND ce.eventid = ? " //+ evtId 
			+ " AND ce.contributorid = c.contributorid "	
			+ "AND ce.function = cfp.contributorfunctpreferredid (+)";
			
		int[] param = {conId, evtId};
		//execute sql statement
		ResultSet results = db.exePreparedStatement(sql, param);		
				
		// check to see that data was returned
		try {
			if(!results.last()) { 			
				db.tidyup();				
				return null;				
			}else 
				results.beforeFirst();          
			
			while (results.next()) {
				c_id = Integer.toString(results.getInt(1));
				f_name = results.getString(2);
				l_name = results.getString(3);
				String role = results.getString(4);
				if (role != null)	// contributor's function in conevlink table could be null	
					if (roles.equals(""))
						roles = results.getString(4);
					else
						roles = roles + " | " + results.getString(4);					
			}
						
			if (!network.conId_conObj_map.containsKey(conId)){
				// create a new contributor
				con = new Collaborator(c_id);
				con.setGName(f_name);
				con.setFName(l_name);
				con.setEvtRoleMap(evtId, roles);
				network.conId_conObj_map.put(conId, con);
			}else {
				//add roles to the eventRoleMap
				con = network.conId_conObj_map.get(Integer.parseInt(c_id));
				if (con != null )
					con.setEvtRoleMap(evtId, roles);
			}
			
		} catch (java.sql.SQLException ex) {
			//System.out.println(ex);
			results = null;			
		}
		
		db.tidyup();
		return con;
	}
	
	
	/**
	 * A method to get the details for an contributor set
	 *
	 * @param set  the contributor id set
	 *
	 */
	public void getContributorsDetail(Set<Integer> set){
		int[] conIDArray = new int[set.size()];
		int x = 0;
		for (Integer cID : set) conIDArray[x++] = cID;

		int SINGLE_BATCH = 1;
		int SMALL_BATCH = 4;
		int MEDIUM_BATCH = 11;
		int LARGE_BATCH = 51;
		int start = 0;
		int totalNumberOfValuesLeftToBatch = set.size();
		
		while ( totalNumberOfValuesLeftToBatch > 0 ) {
			
			int batchSize = SINGLE_BATCH;
			if ( totalNumberOfValuesLeftToBatch >= LARGE_BATCH ) {
			  batchSize = LARGE_BATCH;
			} else if ( totalNumberOfValuesLeftToBatch >= MEDIUM_BATCH ) {
			  batchSize = MEDIUM_BATCH;
			} else if ( totalNumberOfValuesLeftToBatch >= SMALL_BATCH ) {
			  batchSize = SMALL_BATCH;
			}
			
			String inClause = new String("");			
			for (int i=0; i < batchSize; i++) {
				inClause = inClause + "? ,";
			}
			inClause = inClause.substring(0, inClause.length()-1);
			
			String sql = "SELECT c.contributorid, c.first_name, c.last_name, DECODE(g.gender, null, 'Unknown', g.gender), DECODE(c.nationality, null, 'Unknown', c.nationality), cp.preferredterm "
				+ "FROM contributor c, gendermenu g, contributorfunctpreferred cp, contfunctlink cl "
				+ "WHERE c.contributorid in (" + inClause + ") " 
				+ "AND c.gender = g.genderid (+) "
				+ "AND c.contributorid = cl.contributorid (+) "
				+ "AND cl.contributorfunctpreferredid = cp.contributorfunctpreferredid (+) "
				+ "order by c.contributorid, cp.preferredterm";
	
			ResultSet results = db.exePreparedINStatement(sql, conIDArray, start, batchSize);
			
			try{
				if (!results.last()) {
					db.tidyup();
					return;
				} else
				results.beforeFirst();
				
				int c_id = 0;
				String first_name = "";
				String last_name = "";
				String gender = "";
				String nationality = "";
				String roles = "";
				int pre_c_id = 0;
				
				//build the contributor object
				while (results.next() == true) {
					
					c_id = results.getInt(1);
					if (c_id == pre_c_id){
						roles = roles + " | " + results.getString(6);
					}else{
				
						if (pre_c_id != 0){		//not the first one					
							// create a new contributor
							Collaborator con = null;
							con = new Collaborator(Integer.toString(pre_c_id));
							con.setGName(first_name);
							con.setFName(last_name);
							con.setGender(gender);
							con.setNationality(nationality);
							con.setRoles(roles);
							network.conId_conObj_map.put(pre_c_id, con);
						}
						first_name = results.getString(2);
						last_name = results.getString(3);
						gender = results.getString(4);
						nationality = results.getString(5);
						roles = results.getString(6);
					}					
									
					pre_c_id = c_id;
				}
				//last one
				Collaborator con = null;
				con = new Collaborator(Integer.toString(c_id));
				con.setGName(first_name);
				con.setFName(last_name);
				con.setGender(gender);
				con.setNationality(nationality);
				con.setRoles(roles);
				network.conId_conObj_map.put(c_id, con);
				
				db.tidyup();
			} catch (SQLException e) {
				results = null;
				db.tidyup();
				e.printStackTrace();
			}
			totalNumberOfValuesLeftToBatch -= batchSize; 			
			start += batchSize;
		}
					
		return ;
		
	}

	public String getName(){
		
		String sql = "";
			
		if (network.type.equalsIgnoreCase("o")) {
			
			sql = "SELECT DISTINCT name "
				+ "FROM organisation "
				+ "WHERE organisationid = ?"; 
			
		} else if (network.type.equalsIgnoreCase("v")) {
			
			sql = "SELECT DISTINCT venue_name "
				+ "FROM venue "
				+ "WHERE venueid = ?";
		}
			
		int[] param = {Integer.parseInt(network.id)};
		
		ResultSet results = db.exePreparedStatement(sql, param);				
		String name = "";
		
		try {			
			// 	check to see that data was returned
			if (!results.last()){	
				db.tidyup();
				return null;
			}else 
				results.beforeFirst();
			
			while(results.next() == true) {
				name = results.getString(1);									
			}									
		} catch (java.sql.SQLException ex) {	
			System.out.println("Exception: " + ex.getMessage());
			results = null;
		}
		
		db.tidyup();
		return name;
	}
	
}

/*
 * This file is part of the AusStage Data Exchange Service
 *
 * AusStage Data Exchange Service is free software: you can redistribute
 * it and/or modify it under the terms of the GNU General Public License 
 * as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * AusStage Data Exchange Service is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with AusStage Data Exchange Service.  
 * If not, see <http://www.gnu.org/licenses/>.
*/

package au.edu.ausstage.exchange;

// import additional AusStage libraries
import au.edu.ausstage.utils.*;
import au.edu.ausstage.exchange.types.*;

import java.util.ArrayList;
import java.sql.ResultSet;

/**
 * The main driving class for the collation of event data using resource sub type ids
 */
public class ResourceData extends BaseData{

	/**
	 * Constructor for this class
	 *
	 * @param database    the DbManager class used to connect to the database
	 * @param ids         the array of unique contributor ids
	 * @param outputType  the output type
	 * @param recordLimit the record limit
	 *
	 * @throws IllegalArgumentException if any of the parameters are empty or do not pass validation
	 *
	 */
	public ResourceData(DbManager database, String[] ids, String outputType, String recordLimit) {
	
		super(database, ids, outputType, recordLimit);
	}
	
	@Override
	public String getEventData() {
		
		throw new UnsupportedOperationException("This method is not valid in the context of Resource data");
	}
	
	@Override
	public String getResourceData() {
		
		String sql;
		DbObjects results;
		Resource resource;
		
		ArrayList<Resource> resourceList = new ArrayList<Resource>();
		
		String[] ids = getIds();
		
		if(ids.length == 1) {
		
			sql = "SELECT i.itemid, i.citation "
				+ "FROM item i, lookup_codes lc "
				+ "WHERE i.item_sub_type_lov_id = lc.code_lov_id "
				+ "AND lc.code_type = 'RESOURCE_SUB_TYPE' "
				+ "AND lc.code_lov_id = ?";
			
		} else {
		
			sql = "SELECT i.itemid, i.citation "
				+ "FROM item i, lookup_codes lc "
				+ "WHERE i.item_sub_type_lov_id = lc.code_lov_id "
				+ "AND lc.code_type = 'RESOURCE_SUB_TYPE' "
				+ "AND lc.code_lov_id = ANY (";
			    
			    // add sufficient place holders for all of the ids
				for(int i = 0; i < ids.length; i++) {
					sql += "?,";
				}

				// tidy up the sql
				sql = sql.substring(0, sql.length() -1);
				
				// finalise the sql string
				sql += ") ";
		}
		
		// get the data
		results = getDatabase().executePreparedStatement(sql, ids);
	
		// check to see that data was returned
		if(results == null) {
			throw new RuntimeException("unable to lookup resource data");
		}
		
		// build the list of contributors
		ResultSet resultSet = results.getResultSet();
		try {
			while (resultSet.next()) {
			
				resource = new Resource(resultSet.getString(1), resultSet.getString(2));
				resourceList.add(resource);
				
			}
		} catch (java.sql.SQLException ex) {
			throw new  RuntimeException("unable to build list of resources: " + ex.toString());
		}
		
		// play nice and tidy up
		resultSet = null;
		results.tidyUp();
		results = null;
		
		// trim the arraylist if necessary
		if(getRecordLimit().equals("all") == false) {
			int limit = getRecordLimitAsInt();
			
			if(resourceList.size() > limit) {
			
				ArrayList<Resource> list = new ArrayList<Resource>();
				
				for(int i = 0; i < limit; i++) {
					list.add(resourceList.get(i));
				}
				
				resourceList = list;
				list = null;
			}
		}
		
		String data = null;
		
		// build the output
		if(getOutputType().equals("html") == true) {
			data =  ResourceDataBuilder.buildHtml(resourceList);
		} else if(getOutputType().equals("json")) {
			data = ResourceDataBuilder.buildJson(resourceList);
		} else if(getOutputType().equals("xml")) {
			data = ResourceDataBuilder.buildXml(resourceList);
		} else if(getOutputType().equals("rss") == true) {
			data = ResourceDataBuilder.buildRss(resourceList);
		}
		
		return data;
	}
}

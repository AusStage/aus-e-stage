package au.edu.ausstage.visualisation;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map.Entry;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.sparql.resultset.ResultSetMem;

import au.edu.ausstage.networks.DataManager;
import au.edu.ausstage.networks.DatabaseManager;
import au.edu.ausstage.vocabularies.AuseStage;
import au.edu.ausstage.vocabularies.FOAF;

public class ChartManager {
	public DatabaseManager   db    = null;
	public DataManager rdf = null;
	public ChartSourceData sourceData = null;
	
	public ChartManager(DatabaseManager db){
		this.db = db;
		
		if(db.connect() == false) {
			throw new RuntimeException("Error: Unable to connect to the database");
		}
	}
	
	public ChartManager(DatabaseManager db, DataManager rdf){
		this.db = db;
		
		if(db.connect() == false) {
			throw new RuntimeException("Error: Unable to connect to the database");
		}
		
		this.rdf = rdf;
	}
	
	public void getEventsByStatusAndPrimaryGenre(){
		//String[] columnName = {"Status", "Theatre_Spoken_Word", "Music_Theatre", "Dance", "Music", "Other"}; 		
		
		String sql = "SELECT statusmenu.status as \"Status\", " +
					 "SUM(decode(events.primary_genre, 1, 1, 0)) as \"Theatre_Spoken_Word\", " +
					 "SUM(decode(events.primary_genre, 2, 1, 0)) as \"Music_Theatre\", " +
					 "SUM(decode(events.primary_genre, 3, 1, 0)) as \"Dance\", " +
					 "SUM(decode(events.primary_genre, 4, 1, 0)) as \"Music\", " +
					 "SUM(decode(events.primary_genre, 5, 1, 0)) as \"Other\", " +
					 "COUNT(events.eventid) as Total " +
					 "FROM statusmenu, events " + 
					 "WHERE statusmenu.statusid = events.status " + 
					 "GROUP BY statusmenu.statusid, statusmenu.status " +
					 "ORDER BY Total";
		
		ResultSet results = db.exeStatement(sql);
		if (results == null)
			return;
		String[] columnName = getColumnName(results);
		if (columnName == null)
			return;
		String[][] infoArr = getResultsToArray(results, columnName.length);
		if (infoArr != null)
			sourceData = new ChartSourceData("EventsByStatusAndPrimaryGenre", columnName, infoArr);
		
		
	}
	
	public void getEventsByStateAndPrimaryGenre(){
		//String[] columnName = {"State", "Theatre_Spoken_Word", "Music_Theatre", "Dance", "Music", "Other"}; 
				
		String sql = "SELECT states.state as \"State\", " +
					 "SUM(decode(events.primary_genre, 1, 1, 0)) as \"Theatre_Spoken_Word\", " +
					 "SUM(decode(events.primary_genre, 2, 1, 0)) as \"Music_Theatre\", " +
					 "SUM(decode(events.primary_genre, 3, 1, 0)) as \"Dance\", " +
					 "SUM(decode(events.primary_genre, 4, 1, 0)) as \"Music\", " +
					 "SUM(decode(events.primary_genre, 5, 1, 0)) as \"Other\", " +
					 "COUNT(events.eventid) as Total " +
					 "FROM  venue, events, states " + 
					 "WHERE venue.venueid=events.venueid and states.stateid=venue.state and states.stateid <> 10 " + 
					 "GROUP BY venue.state, states.state " +
					 "ORDER BY Total";
		
		ResultSet results = db.exeStatement(sql);
		if (results == null)
			return;
		String[] columnName = getColumnName(results);		
		if (columnName == null)
			return;
		String[][] infoArr = getResultsToArray(results, columnName.length);
		if (infoArr != null)
			sourceData = new ChartSourceData("EventsByStateAndPrimaryGenre", columnName, infoArr);
		
	}
	
	public void getEventsByStateAndStatus(){
		//String[] columnName = {"Status", "NSW", "VIC", "SA", "QLD", "WA", "ACT", "TAS", "NT", "OS"}; 
	
		String sql = "select statusmenu.status as \"Status\", " +
					 "SUM(decode(venue.state,3,1,0)) as \"NSW\", " + 
					 "SUM(decode(venue.state,6,1,0)) as \"VIC\", " +
					 "SUM(decode(venue.state,1,1,0)) as \"SA\", " + 
					 "SUM(decode(venue.state,4,1,0)) as \"QLD\", " + 
					 "SUM(decode(venue.state,2,1,0)) as \"WA\", " + 
					 "SUM(decode(venue.state,7,1,0)) as \"ACT\", " +
					 "SUM(decode(venue.state,5,1,0)) as \"TAS\", " +
					 "SUM(decode(venue.state,8,1,0)) as \"NT\", " +
					 "SUM(decode(venue.state,9,1,0)) as \"OS\", " +
					 "SUM(decode(venue.state,10,1,0)) as \"UNKNOWN\", " +
					 "count(events.eventid) Total " +
					 "from statusmenu, venue, events, states " + 
					 "where venue.venueid=events.venueid " +
					 "and states.stateid=venue.state " +
					 "and statusmenu.statusid=events.status " +
					 "group by statusmenu.statusid, statusmenu.status " +
					 "order by Total";
		
		ResultSet results = db.exeStatement(sql);
		if (results == null)
			return;
		String[] columnName = getColumnName(results);		
		if (columnName == null)
			return;
		String[][] infoArr = getResultsToArray(results, columnName.length);
		if (infoArr != null)
			sourceData = new ChartSourceData("EventsByStateAndStatus", columnName, infoArr);
		
	}
	
	public void getEventsByYear(int y1, int y2){
		//String[] columnName = {"Year", "NumOfEvents"}; 
			
		String sql = "select yyyyfirst_date as \"Year\", count(eventid) as \"NumOfEvents\" " +
					 "from events " + 
					 "where yyyyfirst_date between ? and ? " +
					 "group by yyyyfirst_date " +
					 "order by yyyyfirst_date";	
		
		int param[] = {y1, y2};
		
		ResultSet results = db.exePreparedStatement(sql, param);
		if (results == null)
			return;
		String[] columnName = getColumnName(results);			
		if (columnName == null)
			return;
		String[][] infoArr = getResultsToArray(results, columnName.length);
		if (infoArr != null)
			sourceData = new ChartSourceData("EventsByYear-" + y1 + "-" + y2, columnName, infoArr);
		
	}
	
	
	public void getEventsByYearAndState(int y1, int y2){
		//String[] columnName = {"Year", "NSW", "VIC", "SA", "QLD", "WA", "ACT", "TAS", "NT", "OS"}; 
		
		String sql = "select events.yyyyfirst_date as \"Year\", " +
					 "SUM(decode(venue.state,3,1,0)) as \"NSW\", " + 
					 "SUM(decode(venue.state,6,1,0)) as \"VIC\", " +
					 "SUM(decode(venue.state,1,1,0)) as \"SA\", " + 
					 "SUM(decode(venue.state,4,1,0)) as \"QLD\", " + 
					 "SUM(decode(venue.state,2,1,0)) as \"WA\", " + 
					 "SUM(decode(venue.state,7,1,0)) as \"ACT\", " +
					 "SUM(decode(venue.state,5,1,0)) as \"TAS\", " +
					 "SUM(decode(venue.state,8,1,0)) as \"NT\", " +
					 "SUM(decode(venue.state,9,1,0)) as \"OS\", " +
					 "SUM(decode(venue.state,10,1,0)) as \"UNKNOWN\", " +
					 "count(events.eventid) as \"Total\" " +
					 "from venue, events, states " + 
					 "where venue.venueid=events.venueid " +
					 "and states.stateid=venue.state " +
					 "and events.yyyyfirst_date between ? and ? " +
					 "group by events.yyyyfirst_date " +
					 "order by events.yyyyfirst_date";
		
		int param[] = {y1, y2};
		
		ResultSet results = db.exePreparedStatement(sql, param);
		if (results == null)
			return;
		String[] columnName = getColumnName(results);			
		if (columnName == null)
			return;
		String[][] infoArr = getResultsToArray(results, columnName.length);
		if (infoArr != null)
			sourceData = new ChartSourceData("EventsByYearAndState-"+ y1 + "-" + y2, columnName, infoArr);
				
	}
	
	public void getEventsBySecondaryGenreAndYear(int y1, int y2){
		/*String[] columnName = {"Year", "Drama", "Comedy", "Musical", "Opera", "Children's Theatre", 
							   "Music", "Contemporary", "One Person Show", "Cabaret", "Short Play",
							   "Dance", "Ballet", "Stand-up Comedy", "Puppetry", "One Act Plays",
							   "Physical Theatre", "Dance Theatre"}; */
		
		String sql = "select events.yyyyfirst_date as \"Year\", " +
					 "SUM(decode(SECGENRECLASSLINK.SECGENREPREFERREDID,15,1,0)) as \"Drama\", " +
					 "SUM(decode(SECGENRECLASSLINK.SECGENREPREFERREDID,10,1,0)) as \"Comedy\", " +
					 "SUM(decode(SECGENRECLASSLINK.SECGENREPREFERREDID,57,1,0)) as \"Musical\", " +
					 "SUM(decode(SECGENRECLASSLINK.SECGENREPREFERREDID,31,1,0)) as \"Opera\", " +
					 "SUM(decode(SECGENRECLASSLINK.SECGENREPREFERREDID,48,1,0)) as \"Children\'s Theatre\", " + 
					 "SUM(decode(SECGENRECLASSLINK.SECGENREPREFERREDID,30,1,0)) as \"Music\", " +
					 "SUM(decode(SECGENRECLASSLINK.SECGENREPREFERREDID,12,1,0)) as \"Contemporary\", " +
					 "SUM(decode(SECGENRECLASSLINK.SECGENREPREFERREDID,139,1,0)) as \"One Person Show\", " +
					 "SUM(decode(SECGENRECLASSLINK.SECGENREPREFERREDID,6,1,0)) as \"Cabaret\", " +
					 "SUM(decode(SECGENRECLASSLINK.SECGENREPREFERREDID,90,1,0)) as \"Short Play\", " +
					 "SUM(decode(SECGENRECLASSLINK.SECGENREPREFERREDID,14,1,0)) as \"Dance\", " +
					 "SUM(decode(SECGENRECLASSLINK.SECGENREPREFERREDID,4,1,0)) as \"Ballet\", " +
					 "SUM(decode(SECGENRECLASSLINK.SECGENREPREFERREDID,99,1,0)) as \"Stand-up Comedy\", " +
					 "SUM(decode(SECGENRECLASSLINK.SECGENREPREFERREDID,37,1,0)) as \"Puppetry\", " +
					 "SUM(decode(SECGENRECLASSLINK.SECGENREPREFERREDID,66,1,0)) as \"One Act Plays\", " +
					 "SUM(decode(SECGENRECLASSLINK.SECGENREPREFERREDID,58,1,0)) as \"Physical Theatre\", " +
					 "SUM(decode(SECGENRECLASSLINK.SECGENREPREFERREDID,121,1,0)) as \"Dance Theatre\", " +
					 "count(events.eventid) as \"Total\" " +
					 "from events, SECGENRECLASSLINK, SECGENREPREFERRED " + 
					 "where events.eventid = SECGENRECLASSLINK.EVENTID " +
					 "and SECGENRECLASSLINK.SECGENREPREFERREDID = SECGENREPREFERRED.SECGENREPREFERREDID " +
					 "and events.yyyyfirst_date between ? and ? " +
					 "group by events.yyyyfirst_date " +
					 "order by events.yyyyfirst_date";
		
		int param[] = {y1, y2};
		
		ResultSet results = db.exePreparedStatement(sql, param);
		if (results == null)
			return;
		String[] columnName = getColumnName(results);	
		if (columnName == null)
			return;
		
		String[][] infoArr = getResultsToArray(results, columnName.length);
		if (infoArr != null)
			sourceData = new ChartSourceData("EventsByYearAndSecondaryGenre-" + y1 + "-" + y2, columnName, infoArr);
				
	}
	
	public void getRecordCountNodeEdgeWeight(){
		String[]columnName = {"Name", "Value"};
		String[][] infoArr = {
								{"Events", "0"},
								{"Contributors", "0"},
								{"Events-Contributors", "0"},
								{"Contributors-Contributors:Edges", "0"},
								{"Contributors-Contributors:Weights", "0"}
							  };
		
		//get Events count
		int count = 0;
		String sql = "select count(eventid) as Events from events";
		ResultSet results = db.exeStatement(sql);
		count = getResult(results);
		infoArr[0][1] = Integer.toString(count);
		
		//get contributors count
		sql = "select count(contributorid) as Contributors from contributor";
		results = db.exeStatement(sql);
		if (results == null)
			return;
		count = getResult(results);
		infoArr[1][1] = Integer.toString(count);
				
		//get events-contributors count
		sql = "select count(conevlinkid) as \"Events-Contributors\" from conevlink";
		results = db.exeStatement(sql);
		if (results == null)
			return;
		count = getResult(results);
		infoArr[2][1] = Integer.toString(count);
	
		//get contributors-contributors edges
		QuerySolution row  = null;
		String sparql_edges = "PREFIX foaf:       <" + FOAF.NS + "> "
		   					+ "PREFIX ausestage:  <" + AuseStage.NS + "> "
		   					+ "SELECT sum(?count) "
		   					+ "WHERE {  "
		   					+ "       ?x foaf:name ?name."
		   					+ "		  ?x ausestage:collaboratorCount ?count."		
		   					+ "} ";
		
		com.hp.hpl.jena.query.ResultSet rdf_results = rdf.executeSparqlQuery(sparql_edges);
	
		while (rdf_results.hasNext()) {
			row = rdf_results.nextSolution();			
			infoArr[3][1] = row.getLiteral(".1").getString();
		}
		rdf.tidyUp();
		
		//get contributors-contribitors weights:
		String sparql_weights = "PREFIX foaf:       <" + FOAF.NS + "> "
			+ "PREFIX ausestage:  <" + AuseStage.NS + "> "
			+ "SELECT sum(?count) "
			+ "WHERE {  "
			+ "       ?x foaf:name ?name.  "
			+ "		  ?x ausestage:hasCollaboration ?collaboration."
			+ "		  ?collaboration ausestage:collaborationCount ?count."		
			+ "} ";

		rdf_results = rdf.executeSparqlQuery(sparql_weights);

		while (rdf_results.hasNext()) {
			row = rdf_results.nextSolution();			
			infoArr[4][1] = row.getLiteral(".1").getString();
		}
		rdf.tidyUp();
			
		sourceData = new ChartSourceData("RecordCount", columnName, infoArr);
	}
	
	public void getDistributionOfDegree(){
		String[]columnName = {"Num of Artists", "Degree of Connection"};
		
		QuerySolution row  = null;
		String infoArr[][] ;
		int i = 0;
		
		String sparql_degreeOfConnection = "PREFIX foaf:       <" + FOAF.NS + ">"
		   					+ "PREFIX ausestage:  <" + AuseStage.NS + "> "
		   					+ "SELECT count(?x) ?count "
		   					+ "WHERE {  "
		   					+ "       ?x foaf:name ?name."
		   					+ "		  ?x ausestage:collaboratorCount ?count"		
		   					+ "} GROUP BY ?count "
		   					+ "ORDER BY ?count";
		
		com.hp.hpl.jena.query.ResultSet rdf_results = rdf.executeSparqlQuery(sparql_degreeOfConnection);
		ResultSetMem rdf_results_mem = new ResultSetMem(rdf_results);
		
		int len = rdf_results_mem.size();
		infoArr = new String[len][2];
		
		while (rdf_results_mem.hasNext()) {
			row = rdf_results_mem.nextSolution();		
			//System.out.println(row.getLiteral(".1").getString() + "  " + row.getLiteral("count").getString());
			infoArr[i][0] = row.getLiteral(".1").getString();
			infoArr[i][1] = row.getLiteral("count").getString();
			i++;
		}
		rdf.tidyUp();
		
		sourceData = new ChartSourceData("DistributionOfDegree", columnName, infoArr);
	}
	
	public void getEventsCount(){
		String[]columnName = {"Years", "Events Added", "Event Added Total", "Event Updated", "Event Updated Total"};
		int year_begin = 2000;
		int year_end = Calendar.getInstance().get(Calendar.YEAR);

		String[] year_template = new String[year_end - year_begin +1];
		for (int i = 0; i < (year_end - year_begin + 1); i++){
			year_template[i] = Integer.toString(year_begin + i);
		}
		
		String sql_evtAdded = "select TO_CHAR(entered_date, 'YYYY') as Year, count(TO_CHAR(entered_date, 'YYYY')) as \"Events Added\" " + 
					 "from events " + 
					 "where entered_date is not null " +
					 "and TO_CHAR(entered_date, 'yyyy') >= 2000 " +
					 "group by TO_CHAR(entered_date, 'YYYY') " + 
					 "order by Year";

		ResultSet results_evtAdded = db.exeStatement(sql_evtAdded);
		if (results_evtAdded == null)
			return;
		String[] evtadded_columnName = getColumnName(results_evtAdded);	
		if (evtadded_columnName == null)
			return;
		
		String[][] infoArr_evtAdded = getResultsToArray(results_evtAdded, evtadded_columnName.length);
		
		String sql_evtUpdated = "select TO_CHAR(updated_date, 'YYYY') as Year, count(TO_CHAR(updated_date, 'YYYY')) as \"Events Updated\" " +
								"from events " + 
								"where updated_date is not null " +
								"and TO_CHAR(updated_date, 'yyyy') >= 2000 " +
								"group by TO_CHAR(updated_date, 'YYYY') " + 
								"order by Year";

		ResultSet results_evtUpdated = db.exeStatement(sql_evtUpdated);
		if (results_evtUpdated == null)
			return;
		String[] evtupdated_columnName = getColumnName(results_evtUpdated);	
		if (evtupdated_columnName == null)
			return;
		
		String[][] infoArr_evtUpdated = getResultsToArray(results_evtUpdated, evtupdated_columnName.length);
				
		if (infoArr_evtAdded != null && infoArr_evtUpdated != null)
			sourceData = new ChartSourceData("EventsCount", columnName, year_template, infoArr_evtAdded, infoArr_evtUpdated);
	
	}
	
	public void getResourceCount(){
		String[]columnName = {"Years", "Resource Added", "Resource Added Total", "Resource Updated", "Resource Updated Total"};
		//get year template
		int year_begin = 2000;
		int year_end = Calendar.getInstance().get(Calendar.YEAR);

		String[] year_template = new String[year_end - year_begin +1];
		for (int i = 0; i < (year_end - year_begin + 1); i++){
			year_template[i] = Integer.toString(year_begin + i);
		}
		
		//get number of events added during 2001 and 2005
		String sql_evtAdded = "select TO_CHAR(entered_date, 'YYYY') as Year, count(TO_CHAR(entered_date, 'YYYY')) as \"Events Added\" " + 
						"from events " + 
						"where entered_date is not null " +
						"and TO_CHAR(entered_date, 'yyyy') between 2001 and 2005 " +
						"group by TO_CHAR(entered_date, 'YYYY') " + 
						"order by Year";

		ResultSet results_evtAdded = db.exeStatement(sql_evtAdded);
		if (results_evtAdded == null)
			return;
		String[] evtadded_columnName = getColumnName(results_evtAdded);	
		if (evtadded_columnName == null)
			return;

		String[][] infoArr_evtAdded2001_2005 = getResultsToArray(results_evtAdded, evtadded_columnName.length);
	
		//get the total number of resource added during 2001 and 2005
		String sql = "select count(itemid) from item where entered_date is null ";
		ResultSet results = db.exeStatement(sql);
		if (results == null)
			return;
		int numOfResource2001_2005 = getResult(results);
		
		//get the number of estimated article from 2001 to 2005 
		//because the updated_date and entered_date are not recorded for those years
		int[][] rsc2001_2005 = new int[infoArr_evtAdded2001_2005.length][2]; 
		rsc2001_2005 = getEstimatedArticle(numOfResource2001_2005, infoArr_evtAdded2001_2005);
		
		//get total number of resource added since 2000
		String sql_rscAdded = "select TO_CHAR(entered_date, 'YYYY') as Year, count(TO_CHAR(entered_date, 'YYYY')) as \"Resource Added\" " + 
					 "from item " + 
					 "where entered_date is not null " +
					 "and TO_CHAR(entered_date, 'yyyy') >= 2000 " +
					 "group by TO_CHAR(entered_date, 'YYYY') " + 
					 "order by Year";

		ResultSet results_rscAdded = db.exeStatement(sql_rscAdded);
		if (results_rscAdded == null)
			return;
		String[] rscadded_columnName = getColumnName(results_rscAdded);	
		if (rscadded_columnName == null)
			return;
		
		String[][] totalRscAdded = getResultsToArray(results_rscAdded, rscadded_columnName.length);
		
		//merge the estimated article (from 2001 to 2005) into total number of resource from 2000 till now
		totalRscAdded = mergeRscAddedIn2001_2005(rsc2001_2005, totalRscAdded, year_template);
		
		//get number of resource updated since 2000
		String sql_rscUpdated = "select TO_CHAR(updated_date, 'YYYY') as Year, count(TO_CHAR(updated_date, 'YYYY')) as \"Resource Updated\" " +
								"from item " + 
								"where updated_date is not null " +
								"and TO_CHAR(updated_date, 'yyyy') >= 2000 " +
								"group by TO_CHAR(updated_date, 'YYYY') " + 
								"order by Year";

		ResultSet results_rscUpdated = db.exeStatement(sql_rscUpdated);
		if (results_rscUpdated == null)
			return;
		String[] rscUpdated_columnName = getColumnName(results_rscUpdated);	
		if (rscUpdated_columnName == null)
			return;
		
		String[][] totalRscUpdated = getResultsToArray(results_rscUpdated, rscUpdated_columnName.length);
				
		if (totalRscAdded != null && totalRscUpdated != null)
			sourceData = new ChartSourceData("RecourceCount", columnName, year_template, totalRscAdded, totalRscUpdated);
	
	}
	
	/**
	 * Use number of events added during 2001 and 2005 to estimate the number of articles added during this period.
	 * 
	 * estimated num of article in yr X = total num of article * (num of evts in yr X / total num of evts)  
	 * 
	 * @param numOfResource  total number of resource added during 2001 and 2005
	 * @param evtAdded  number of events added during 2001 and 2005 (there is no data in 2001 and 2003)
	 * @return number of articles added during 2001 and 2005 (article = resource) 
	 *
	 */
	public int[][] getEstimatedArticle(int numOfResource, String[][] evtAdded){
		
		int numOfEvt = 0;
		for(int i = 0; i < evtAdded.length; i++)
			numOfEvt = numOfEvt + Integer.parseInt(evtAdded[i][1]);
		
		int[][] rscAdded2001_2005 = new int[evtAdded.length][2]; 
		for(int i = 0; i < evtAdded.length; i++) { 
			rscAdded2001_2005[i][0] = Integer.parseInt(evtAdded[i][0]);
			int value = Integer.parseInt(evtAdded[i][1]);
			float tobeRound = numOfResource * ( (float)value / numOfEvt);
			rscAdded2001_2005[i][1] = (int) Math.round(tobeRound);
		}	
		return rscAdded2001_2005;
		
	}
	
	/**
	 * merge the estimated number of article (from 2001 to 2005) into total number of resource from 2000 till now
	 * since the entered_date is not recored during 2001  and 2005
	 * 
	 * @param rsc2001_2005  estimated number of article added during 2001 and 2005 
	 * @param rscAdded  number of article array from 2001 up to now
	 * @param template  year template from 2000, 2001, 2002, 2003 to now
	 * @return number of article array from 2000 up to now
	 */
	public String [][] mergeRscAddedIn2001_2005(int[][] rsc2001_2005, String[][] rscAdded, String[] template){
		
		String[][] totalRscAdded = new String[template.length][2];
		
		for(int i = 0; i < template.length; i++) {
			totalRscAdded[i][0] = template[i];
			totalRscAdded[i][1] = "0";
		}
		
		for(int i = 0; i < template.length; i++) {
			
			for(int j = 0; j < rsc2001_2005.length; j++){
					
				if (template[i].equals(Integer.toString(rsc2001_2005[j][0]))) {
					totalRscAdded[i][1] = Integer.toString(rsc2001_2005[j][1]);			
					break;
				}				
			}	
			
			for(int h = 0; h < rscAdded.length; h++){
				
				if (template[i].equals(rscAdded[h][0])) {
					totalRscAdded[i][1] = Integer.toString(Integer.parseInt(totalRscAdded[i][1]) + Integer.parseInt(rscAdded[h][1]));
					break;
				}
			}
			
		}
		
		return totalRscAdded;
	}
	
	public void getResourceTypeCount(){
		String sql = "select lk.description as \"Resource Type\", count(item.itemid) as \"Count\" " +
					 "from item, lookup_codes lk " +
					 "where item.item_sub_type_lov_id = lk.code_lov_id " +
					 "group by lk.description " + 
					 "order by count(item.itemid)";
		
		ResultSet results = db.exeStatement(sql);
		if (results == null)
			return;
		String[] columnName = getColumnName(results);	
		if (columnName == null)
			return;
		
		String[][] infoArr = getResultsToArray(results, columnName.length);
		if (infoArr != null)
			sourceData = new ChartSourceData("ResourceTypeCount", columnName, infoArr);				
		
	}
	
	public void getJoin(int code, String name) {
		String sql = getJoinSQL(code);
		int[] param = {code};

		if (sql == null)
			return;
			
		ResultSet results = db.exePreparedStatement(sql, param);
		if (results == null)
			return;
		String[] columnJoinName = new String[9]; 
		String[] columnName = getColumnName(results);
		
		if (columnName == null)
			return;
		
		System.arraycopy(columnName, 0, columnJoinName, 0, columnName.length);
		
		//get template and copy it into infoArr
		String[][] template = getResultsToArray(results, columnName.length);
		String[][] infoArr = new String[template.length][9];		
		if (template != null && infoArr != null)
			for (int i = 0; i < template.length; i++)
				System.arraycopy(template[i], 0, infoArr[i], 0, template[i].length);
		
		//get contributor & merge into big array
		int index = 2;
		String[][] contributor = getJoinContributor(code);
		columnJoinName[index] = "Contributor Count";
		infoArr = join7Column(infoArr, contributor, index);
		
		//get organisation & merge into big array
		index ++;
		String[][] organisation = getJoinOrganisation(code);
		columnJoinName[index] = "Organisation Count";								
		infoArr = join7Column(infoArr, organisation, index);
		
		
		//get venue & merge into big array
		index ++;
		String[][] venue = getJoinVenue(code);
		columnJoinName[index] = "Venue Count";								
		infoArr = join7Column(infoArr, venue, index);
		
		
		//get work & merge into big array
		index ++;
		String[][] work = getJoinWork(code);
		columnJoinName[index] = "Work Count";								
		infoArr = join7Column(infoArr, work, index);
		
		
		//get event & merge into big array
		index ++;
		String[][] event = getJoinEvent(code);
		columnJoinName[index] = "Event Count";								
		infoArr = join7Column(infoArr, event, index);
		
		
		//get secondary genre & merge into big array
		index ++;
		String[][] secgenre = getJoinSecGenre(code);
		columnJoinName[index] = "SecGenre Count";								
		infoArr = join7Column(infoArr, secgenre, index);
		
		
		//get content indicator & merge into big array
		index ++;
		String[][] content = getJoinContentIndicator(code);
		columnJoinName[index] = "ContentIndicator Count";								
		infoArr = join7Column(infoArr, content, index);
				
		if (infoArr != null)
			sourceData = new ChartSourceData(name, columnJoinName, infoArr);
		
	}
	
	public String[][] getJoinContributor(int code){
		
		String sql = null;
		
		switch (code){
			case 5451: 			
			case 43166: sql = "select item.itemid as \"Item ID\", count(item.itemid) as \"Count\" " + 
			 				  "from item, itemconlink, contributor " + 
			 				  "where item.sourceid = ? " +  
			 				  "and item.itemid=itemconlink.itemid " +
			 				  "and itemconlink.contributorid=contributor.contributorid " +
			 				  "and item.itemid>0 " +
			 				  "and itemconlink.creator_flag like 'N' " + 
			 				  "group by item.itemid " +
			 				  "order by item.itemid ";
						break;
						
			case 1060: sql = "select item.itemid as \"Item ID\", count(item.itemid)as \"Count\" " + 
							 "from item, itemconlink, contributor " + 
							 "where item.item_sub_type_lov_id = ? " +							
							 "and item.itemid=itemconlink.itemid " +
							 "and itemconlink.contributorid=contributor.contributorid " +
							 "and item.itemid>0 " +
							 "and itemconlink.creator_flag like 'N' " + 
							 "group by item.itemid " +
							 "order by item.itemid";
						break;
		}
		 
		if (sql == null) return null;
		
		int[] param = {code};
		
		ResultSet results = db.exePreparedStatement(sql, param);
		if (results == null)
			return null;
		String[] columnName = getColumnName(results);
		if (columnName == null)	return null;
		
		String[][] infoArr = getResultsToArray(results, columnName.length);
				
		return infoArr;
	}
	
	public String[][] getJoinOrganisation(int code){
		String sql = null;
		
		switch (code){
			case 5451: 			
			case 43166: sql = "select item.itemid as \"Item ID\", count(item.itemid) as \"Count\" " + 
					 		  "from item, itemorglink, organisation " + 
					 		  "where item.sourceid = ? " + 
					 		  "and item.itemid=itemorglink.itemid " +
					 		  "and itemorglink.organisationid=organisation.organisationid " +
					 		  "and item.itemid>0 " +
					 		  "and itemorglink.creator_flag like 'N' " + 
					 		  "group by item.itemid " +
					 		  "order by item.itemid ";
						break;
			
			case 1060:  sql = "select item.itemid as \"Item ID\", count(item.itemid) as \"Count\" " +
							  "from item, itemorglink, organisation " +
							  "where item.item_sub_type_lov_id = ? " +							 
							  "and item.itemid=itemorglink.itemid " +
							  "and itemorglink.organisationid=organisation.organisationid " +
							  "and item.itemid>0 " +
							  "and itemorglink.creator_flag like 'N' " + 
							  "group by item.itemid " +
							  "order by item.itemid";						
						break;
						
		}
		
		if (sql == null) return null;
		
		int[] param = {code};
		ResultSet results = db.exePreparedStatement(sql, param);
		if (results == null)
			return null;
		String[] columnName = getColumnName(results);
		if (columnName == null) return null;
		
		String[][] infoArr = getResultsToArray(results, columnName.length);
		
		return infoArr;
	}
	
	public String[][] getJoinVenue(int code){
		String sql = null;
		
		switch (code){
			case 5451: 			
			case 43166: sql = "select item.itemid as \"Item ID\", count(item.itemid) as \"Count\" " + 
					 		  "from item, itemvenuelink, venue " + 
					 		  "where item.sourceid = ? " + 
					 		  "and item.itemid=itemvenuelink.itemid " +
					 		  "and itemvenuelink.venueid=venue.venueid " +
					 		  "and item.itemid>0 " +
					 		  "group by item.itemid " +
					 		  "order by item.itemid";
						break;
						
			case 1060:  sql = "select item.itemid as \"Item ID\", count(item.itemid) as \"Count\" " +
							  "from item, itemvenuelink, venue " +
							  "where item.item_sub_type_lov_id = ? " +
							  "and item.itemid=itemvenuelink.itemid " +
							  "and itemvenuelink.venueid=venue.venueid " +
							  "and item.itemid>0 " + 
							  "group by item.itemid " +
							  "order by item.itemid"; 
						break;
		}
		
		if (sql == null) return null;
		
		int[] param = {code};
		ResultSet results = db.exePreparedStatement(sql, param);
		if (results == null)
			return null;
		String[] columnName = getColumnName(results);
		if (columnName == null)	return null;
		
		String[][] infoArr = getResultsToArray(results, columnName.length);
				
		return infoArr;
	}
		
	public String[][] getJoinWork(int code){
		String sql = null;
		
		switch (code){
			case 5451: 			
			case 43166: sql = "select item.itemid as \"Item ID\", count(item.itemid) as \"Count\" " + 
					 		  "from item, itemworklink, work " + 
					 		  "where item.sourceid = ? " + 
					 		  "and item.itemid=itemworklink.itemid " +
					 		  "and itemworklink.workid=work.workid " +
					 		  "and item.itemid>0 " +
					 		  "group by item.itemid " +
					 		  "order by item.itemid ";
						break;
						
			case 1060:  sql = "select item.itemid as \"Item ID\", count(item.itemid) as \"Count\" " + 
							  "from item, itemworklink, work " +
							  "where item.item_sub_type_lov_id = ? " + 
							  "and item.itemid=itemworklink.itemid " +
							  "and work.workid=itemworklink.workid " +
							  "and item.itemid>0 " + 
							  "group by item.itemid " +
							  "order by item.itemid";
						break;
		}
		
		int[] param = {code};
		ResultSet results = db.exePreparedStatement(sql, param);
		if (results == null)
			return null;
		String[] columnName = getColumnName(results);
		if (columnName == null)
			return null;
		
		String[][] infoArr = getResultsToArray(results, columnName.length);
		
		return infoArr;
	}
	
	public String[][] getJoinEvent(int code){
		String sql = null;
		
		switch (code){
			case 5451: 			
			case 43166: sql = "select item.itemid as \"Item ID\", count(item.itemid) as \"Count\" " + 
					 		  "from item, itemevlink, events " + 
					 		  "where item.sourceid = ? " + 
					 		  "and item.itemid=itemevlink.itemid " +
					 		  "and itemevlink.eventid=events.eventid " +
					 		  "and item.itemid>0 " +
					 		  "group by item.itemid " +
					 		  "order by item.itemid ";
						break;
			case 1060:  sql = "select item.itemid as \"Item ID\", count(item.itemid) as \"Count\" " + 
							  "from item, itemevlink, events " +
							  "where item.item_sub_type_lov_id = ? " +
							  "and item.itemid=itemevlink.itemid " +
							  "and events.eventid=itemevlink.eventid " +
							  "and item.itemid>0 " + 
							  "group by item.itemid " +
							  "order by item.itemid";
						break;
		}
		
		int[] param = {code};
		ResultSet results = db.exePreparedStatement(sql, param);
		if (results == null)
			return null;
		String[] columnName = getColumnName(results);
		if (columnName == null)
			return null;
		
		String[][] infoArr = getResultsToArray(results, columnName.length);
		
		return infoArr;
	}
	
	public String[][] getJoinSecGenre(int code){
		String sql = null;
		
		switch (code){
			case 5451: 			
			case 43166: sql = "select item.itemid as \"Item ID\", count(item.itemid) as \"Count\" " + 
					 		  "from item, itemsecgenrelink, secgenrepreferred " + 
					 		  "where item.sourceid = ? " + 
					 		  "and item.itemid = itemsecgenrelink.itemid " +
					 		  "and itemsecgenrelink.secgenrepreferredid = secgenrepreferred.secgenrepreferredid " +
					 		  "and item.itemid>0 " +
					 		  "group by item.itemid " +
					 		  "order by item.itemid ";
						break;
						
			case  1060: sql = "select item.itemid as \"Item ID\", count(item.itemid) as \"Count\" " +
							  "from  item, itemsecgenrelink, secgenrepreferred " +
							  "where item.item_sub_type_lov_id = ? " +
							  "and item.itemid = itemsecgenrelink.itemid " +
							  "and itemsecgenrelink.secgenrepreferredid = secgenrepreferred.secgenrepreferredid " +
							  "and item.itemid>0 " + 
							  "group by item.itemid " +
							  "order by item.itemid";

						break;
		}
		
		int[] param = {code};
		ResultSet results = db.exePreparedStatement(sql, param);
		if (results == null)
			return null;
		String[] columnName = getColumnName(results);
		if (columnName == null)
			return null;
		
		String[][] infoArr = getResultsToArray(results, columnName.length);
		
		return infoArr;
	}
	
	public String[][] getJoinContentIndicator(int code){
		String sql = null;
		
		switch (code){
			case 5451: 			
			case 43166: sql = "select item.itemid as \"Item ID\", count(item.itemid) as \"Count\" " + 
					 		  "from item, itemcontentindlink, contentindicator " + 
					 		  "where item.sourceid = ? " + 
					 		  "and item.itemid = itemcontentindlink.itemid " +
					 		  "and contentindicator.contentindicatorid=itemcontentindlink.contentindicatorid " +
					 		  "and item.itemid>0 " +
					 		  "group by item.itemid " +
					 		  "order by item.itemid ";
						break;
						
			case 1060: sql = "select item.itemid as \"Item ID\", count(item.itemid) as \"Count\" " + 
							 "from item, itemcontentindlink, contentindicator " +
							 "where item.item_sub_type_lov_id = ? " +
							 "and item.itemid=itemcontentindlink.itemid " +
							 "and contentindicator.contentindicatorid=itemcontentindlink.contentindicatorid " +
							 "and item.itemid>0 " + 
							 "group by item.itemid " +
							 "order by item.itemid";
						break;
		}
		
		int[] param = {code};
		ResultSet results = db.exePreparedStatement(sql, param);
		if (results == null)
			return null;
		String[] columnName = getColumnName(results);
		if (columnName == null)
			return null;
		
		String[][] infoArr = getResultsToArray(results, columnName.length);
		
		return infoArr;
	}
	
	/**
	 * merge individual array (e.g. contributor/organisation/work/venue/content indicator etc)
	 * into a big array
	 * 
	 * @param infoArr  the big merged array
	 * @param toBeMergedArr      the array to be merged
	 * @param index	   merged position in big array 
	 * @return		   the big array
	 */
	public String[][] join7Column(String[][] infoArr, String[][] toBeMergedArr, int index){
		//if the column is null, set all the values of that column to 0 
		if (toBeMergedArr == null){
			for (int i = 0; i < infoArr.length; i ++)
				infoArr[i][index] = "0";	
		
			return infoArr;		
		}
		
		int[] sortedItemID = new int[infoArr.length];
		       
		for (int i = 0; i < infoArr.length; i ++){
			infoArr[i][index] = "0";	
			sortedItemID[i] = Integer.parseInt(infoArr[i][0]);
		}
		
		int from = 0;
		int to = infoArr.length;
		
		for (int i = 0; i < toBeMergedArr.length; i ++ ){
			from = Arrays.binarySearch(sortedItemID, from, to, Integer.parseInt(toBeMergedArr[i][0]));
			if (from >= 0)
				infoArr[from][index] = toBeMergedArr[i][1];
			else
				System.out.println("Can not find item ID: " + toBeMergedArr[i][0] + " in index " + index);
		}
		
		return infoArr;
	}
	
	public void getRecordCountsInFiveTables(){
		String[]columnName = {"Table", "RecordCounts"};
		String[][] infoArr = {
								{"Events", "0"},
								{"Contributors", "0"},
								{"Venues", "0"},
								{"Organisations", "0"},
								{"Resources", "0"}
							  };
		
		//get Events count
		int count = 0;
		String sql = "select count(eventid) Events from events";
		ResultSet results = db.exeStatement(sql);
		count = getResult(results);
		infoArr[0][1] = Integer.toString(count);
		
		//get contributors count
		sql = "select count(contributorid) Contributors from contributor";
		results = db.exeStatement(sql);
		count = getResult(results);
		infoArr[1][1] = Integer.toString(count);

		//get venues count
		sql = "select count(venueid) as Venues from venue";
		results = db.exeStatement(sql);
		count = getResult(results);
		infoArr[2][1] = Integer.toString(count);
		
		//get organisations count
		sql = "select count(organisationid) as Organisations from organisation";
		results = db.exeStatement(sql);
		count = getResult(results);
		infoArr[3][1] = Integer.toString(count);
		
		//get resources count
		sql = "select count(itemid) as Resources from item";
		results = db.exeStatement(sql);
		count = getResult(results);
		infoArr[4][1] = Integer.toString(count);
		
		if (infoArr != null)
			sourceData = new ChartSourceData("RecordCountsInFiveTables", columnName, infoArr);
	}
	
	public void	getNumOfContributorsByFunction(){
		String sql = "SELECT cfp.preferredterm as Function, count(c.contributorid) as Count " +
					 "FROM contributor c, contfunctlink cfl,  contributorfunctpreferred cfp " + 
					 "WHERE c.contributorid = cfl.contributorid " +
					 "AND cfl.contributorfunctpreferredid = cfp.contributorfunctpreferredid " +
					 "AND cfp.preferredterm is not null " +
					 "GROUP BY cfp.preferredterm " +
					 "order by Count desc";

		ResultSet results = db.exeStatement(sql);
		if (results == null)
			return;
		String[] columnName = getColumnName(results);
		if (columnName == null)
			return;
		String[][] infoArr = getResultsToArray(results, columnName.length);
		if (infoArr != null)
			sourceData = new ChartSourceData("NumOfContributorsByFunction", columnName, infoArr);

	}
	
	public void getNumOfEventsBySecGenre() {
		String sql = "SELECT sgp.preferredterm as \"Secondary Genre\", count(e.eventid) as Count " +
					 "FROM events e, secgenreclasslink sgl,  secgenrepreferred sgp " + 
					 "WHERE e.eventid = sgl.eventid " +
					 "AND sgl.secgenrepreferredid = sgp.secgenrepreferredid " +
					 "AND sgp.preferredterm is not null " +
					 "GROUP BY sgp.preferredterm " +
					 "order by Count desc";

		ResultSet results = db.exeStatement(sql);
		if (results == null)
			return;
		String[] columnName = getColumnName(results);
		if (columnName == null)
			return;
		String[][] infoArr = getResultsToArray(results, columnName.length);
		if (infoArr != null)
			sourceData = new ChartSourceData("NumOfEventsBySecondaryGenre",	columnName, infoArr);

	}
	
	public void getNumOfEventsByContentIndicator(){
		String sql = "SELECT c.contentindicator as \"Content Indicator\", count(DISTINCT e.eventid) as Count " +
					 "FROM events e, primcontentindicatorevlink p,  contentindicator c " + 
					 "WHERE e.eventid = p.eventid " +
					 "AND p.primcontentindicatorid = c.contentindicatorid " +
					 "AND c.contentindicator is not null " +
					 "AND NOT c.contentindicator = 'Not known' " + 
					 "GROUP BY c.contentindicator " +
					 "order by Count desc";				

		ResultSet results = db.exeStatement(sql);
		if (results == null)
			return;
		String[] columnName = getColumnName(results);
		if (columnName == null)
			return;
		String[][] infoArr = getResultsToArray(results, columnName.length);
		if (infoArr != null)
			sourceData = new ChartSourceData("NumOfEventsByContentIndicator",	columnName, infoArr);

	}
	
	public String[] getColumnName(ResultSet result) {
		
		ResultSetMetaData rsmd;
		int numberOfColumns = 0;
		String[] columnName = null;
		
		try {
			rsmd = result.getMetaData();
			if (rsmd == null)
				return null;
			numberOfColumns = rsmd.getColumnCount();
			columnName = new String[numberOfColumns];
			for (int i = 1; i < numberOfColumns + 1; i++){
				columnName[i - 1] = rsmd.getColumnLabel(i);				
			}
			
		} catch (SQLException e) {			
			e.printStackTrace();
		}
		
		return columnName;
		
	}
	
	public String[][] getResultsToArray(ResultSet results, int columnLength){
		int rowCount = 0;
		String[][] infoArr = null;
		
		try {			
			if (results == null){
				db.tidyup();
				return null;
			}
				
			// 	check to see that data was returned
			if (!results.last()){	
				db.tidyup();
				return null;
			}else {
				rowCount = results.getRow();
				results.beforeFirst();
			}
			
			infoArr = new String[rowCount][columnLength];
			int i = 0;
					
			while(results.next() == true) {
				for (int j = 0 ; j < columnLength; j++){
					if (j == 0)
						infoArr[i][j] = results.getString(j + 1);
					else
						infoArr[i][j] = results.getString(j + 1);
						//infoArr[i][j] = Integer.toString(results.getInt(j + 1));	
				}
				i++;		
			}	
												
		} catch (java.sql.SQLException ex) {	
			System.out.println("Exception: " + ex.getMessage());
			results = null;
		}
		
		db.tidyup();
		
		return infoArr;
	}
	
	public int getResult(ResultSet results){
		int count = 0;
		
		try {			
			// 	check to see that data was returned
			if (!results.last()){	
				db.tidyup();
				return 0;
			}else {
				results.beforeFirst();
			}
					
			while(results.next() == true) {
				 count = results.getInt(1);		
			}	
												
		} catch (java.sql.SQLException ex) {	
			System.out.println("Exception: " + ex.getMessage());
			results = null;
		}
		
		db.tidyup();
		
		return count;
	}
	
	public String getJoinSQL(int code){
		String sql = null;
		
		switch (code) {
			case 5451: 			
			case 43166: sql = "select item.itemid as \"Item ID\", item.citation as \"Citation\"" +
							  "from item " +
							  "where item.sourceid = ? " +   
							  "and item.itemid > 0 " +
							  "order by itemid";
						break;
						
			case 1060: sql = "select item.itemid as \"Item ID\", item.citation as \"Citation\"" +
							 "from item " +
							 "where item.item_sub_type_lov_id = ? " +						
							 "and item.itemid > 0 " +
							 "order by itemid";
						break;		
		}
		
		return sql;
	}

	public static void main(String[] args) throws FileNotFoundException {
	
		String connectString = "jdbc:oracle:thin:ausstage_schema/ausstage@www.ausstage.edu.au:1521:drama11";		
		DatabaseManager db = new DatabaseManager(connectString);
		ChartManager manager = new ChartManager(db);
		//manager.getEventsByStatusAndPrimaryGenre();
		//manager.getEventsByStateAndPrimaryGenre();
		//manager.getEventsByStateAndStatus();
		/*int y1 = 1950;
		int y2 = 2011;*/
		//manager.getEventsByYear(y1, y2);	
		//manager.getEventsByYearAndState(y1, y2);
		//manager.getEventsByYearAndSecondaryGenre(y1, y2);

/*		DataManager rdf = new DataManager();
		ChartManager manager = new ChartManager(db, rdf);*/
		//manager.getRecordCountNodeEdgeWeight();
		//manager.getDistributionOfDegree();
		//manager.getEventsCount();
		//manager.getResourceCount();
		//manager.getResourceTypeCount();
		manager.getJoin(43166, "OnStage_Join");
		//manager.getJoin(5451, "ADS_Join");;
		//manager.getADSContributor();
		
		String filename = "C:\\Documents and Settings\\ehlt_user\\Desktop\\Visualisation\\csv\\" + manager.sourceData.chartName + ".csv";
		PrintWriter writer = new PrintWriter(filename);
		manager.sourceData.toCSV(writer);
		
		if (manager.sourceData == null)
			throw new RuntimeException("Chart source data is null!");			
		
		//manager.sourceData.printDataArr();
		String json = manager.sourceData.toJSON(); 
		System.out.println(json);				
				
		try {
			db.closeDB();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
}

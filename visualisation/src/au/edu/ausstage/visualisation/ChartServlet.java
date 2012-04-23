package au.edu.ausstage.visualisation;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.HashMap;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import au.edu.ausstage.networks.DataManager;
import au.edu.ausstage.networks.DatabaseManager;
import au.edu.ausstage.utils.InputUtils;

/**
 * Servlet implementation class ChartServlet
 */
public class ChartServlet extends HttpServlet {
	
	private ServletConfig servletConfig;
	private DataManager rdf;
	private DatabaseManager db;
	private String connectString = null;
	private int earliest_year = 1700;
	
	private final String[] TASK_TYPES   = {"EventsByStatusAndPrimaryGenre", "EventsByStateAndPrimaryGenre",
			"EventsByStateAndStatus", "EventsCountByYear", "EventsCountByYearAndState", 
			"EventsCountBySecondaryGenreAndYear", "RecordCountNodeEdgeWeight", "DistributionOfDegree",
			"EventsCount", "ResourceCount", "ADSJoin", "OnStageJoin", "BooksJoin", "ResourceTypeCount",
			"RecordCountsInFiveTables", "NumOfConsByFunction", "NumOfEvtsBySecGenre", "NumOfEvtsByContentIndi"
			};
	
	private HashMap<String, Integer> codeMap = new HashMap<String, Integer>();
	
    /*
	 * initialise this instance
	 */
	public void init(ServletConfig conf) throws ServletException {
		// execute the parent objects init method
		super.init(conf);
		
		// store configuration for later
		servletConfig = conf;
		
		// instantiate a database manager object
		rdf = new DataManager(conf);
		
		connectString = conf.getServletContext().getInitParameter("databaseConnectionString");
		db = new DatabaseManager(connectString);
		
		codeMap.put("ADSJoin", new Integer(5451));
		codeMap.put("OnStageJoin", new Integer(43166));
		codeMap.put("BooksJoin", new Integer(1060));
	} 
	
    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String task   = request.getParameter("task");
		String formatType = request.getParameter("format");
		int y1 = 0;
		int y2 = 0;
		
		// check on the taskType parameter
		if(InputUtils.isValid(task, TASK_TYPES) == false) {
			// no valid task type was found
			throw new ServletException("Missing task parameter. Expected one of: " + java.util.Arrays.toString(TASK_TYPES).replaceAll("[\\]\\[]", ""));
		}
		
		ChartManager manager = new ChartManager(db, rdf);
		
		//Metric Visualisations/AusStageMatrices.xlsx
		if (task.equalsIgnoreCase("EventsByStatusAndPrimaryGenre"))
			manager.getEventsByStatusAndPrimaryGenre();
		
		//Metric Visualisations/AusStageMatrices.xlsx
		if (task.equalsIgnoreCase("EventsByStateAndPrimaryGenre")) 
			manager.getEventsByStateAndPrimaryGenre();
		
		//Metric Visualisations/AusStageMatrices.xlsx
		if (task.equalsIgnoreCase("EventsByStateAndStatus")) 
			manager.getEventsByStateAndStatus();
		
		//Metric Visualisations/Event-count.xlsx
		if (task.equalsIgnoreCase("EventsCountByYear")) {
			String start = request.getParameter("start");
			String end = request.getParameter("end");
			
			if (start != null && !start.isEmpty())
				y1 = getYearParam(start);						
			else
				y1 = earliest_year;
			
			if (end !=null && !end.isEmpty())
				y2 = getYearParam(end);
			else
				y2 = Calendar.getInstance().get(Calendar.YEAR);

			if (y1 > y2)
				throw new ServletException("End year should be greater than start year!");
			manager.getEventsByYear(y1, y2);
		}
		
		//Metric Visualisations/Event-count-state.xlsx
		if (task.equalsIgnoreCase("EventsCountByYearAndState")) {
			y1 = getYearParam(request.getParameter("start"));						
			y2 = getYearParam(request.getParameter("end"));
			if (y1 > y2)
				throw new ServletException("End year should be greater than start year!");
			manager.getEventsByYearAndState(y1, y2);
		}
		
		//Metric Visualisations/secondary-genre-matrix2.xls
		if (task.equalsIgnoreCase("EventsCountBySecondaryGenreAndYear")) { 
			y1 = getYearParam(request.getParameter("start"));						
			y2 = getYearParam(request.getParameter("end"));
			if (y1 > y2)
				throw new ServletException("End year should be greater than start year!");
			manager.getEventsBySecondaryGenreAndYear(y1, y2);
		}
		
		//Metric Visualisations/record-count.xls
		if (task.equalsIgnoreCase("RecordCountNodeEdgeWeight")) 
			manager.getRecordCountNodeEdgeWeight();
		
		//Metric Visualisations/distribution-of-degree.png
		if (task.equalsIgnoreCase("DistributionOfDegree")) 
			manager.getDistributionOfDegree();
		
		//ADSA 2011/Event-Resource-Count.xls
		if (task.equalsIgnoreCase("EventsCount")) 
			manager.getEventsCount();
		
		//ADSA 2011/Event-Resource-Count.xls
		if (task.equalsIgnoreCase("ResourceCount")) 
			manager.getResourceCount();
		
		//ADSA 2011/ADS_Joins-new.xls
		if (task.equalsIgnoreCase("ADSJoin")) {			
			if (codeMap.get("ADSJoin") != null)
				manager.getJoin((int) codeMap.get("ADSJoin"), "ADSJoin");
		}
		
		//ADSA 2011/OnStage_Joins.xls
		if (task.equalsIgnoreCase("OnStageJoin")) {			
			if (codeMap.get("OnStageJoin") != null)
				manager.getJoin((int) codeMap.get("OnStageJoin"), "OnStageJoin");
		}
		
		//ADSA 2011/Books.xls
		if (task.equalsIgnoreCase("BooksJoin")) 
			manager.getJoin((int) codeMap.get("BooksJoin"), "BooksJoin");
		
		//ADSA 2011/Resource-Type.xls
		if (task.equalsIgnoreCase("ResourceTypeCount")) 
			manager.getResourceTypeCount();
				
		//ADSA 2011/AusStageRecordCount.pdf
		if (task.equalsIgnoreCase("RecordCountsInFiveTables")) 
			manager.getRecordCountsInFiveTables();
				
		if (task.equalsIgnoreCase("NumOfConsByFunction")) 
			manager.getNumOfContributorsByFunction();
		
		if (task.equalsIgnoreCase("NumOfEvtsBySecGenre")) 
			manager.getNumOfEventsBySecGenre();
		
		if (task.equalsIgnoreCase("NumOfEvtsByContentIndi")) 
			manager.getNumOfEventsByContentIndicator();
				
		if(formatType.equalsIgnoreCase("csv")){
			String filename = manager.sourceData.chartName + ".csv";
			response.setContentType("text/csv");
			response.setHeader("Content-Disposition", "attachment;filename=" + filename);
			PrintWriter out = response.getWriter();			
			manager.sourceData.toCSV(out);
			out.close();
			
		} else if (formatType.equalsIgnoreCase("json")){
			
			response.setContentType("application/json; charset=UTF-8");
			
			PrintWriter out = response.getWriter();
			out.print(manager.sourceData.toJSON());
			out.close();			
		}
		
		
		try {
			db.closeDB();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public int getYearParam(String year) throws ServletException{
		if(InputUtils.isValidInt(year) == false) {
			throw new ServletException("Wrong year parameter.");
		}
		return Integer.parseInt(year);
	}
	
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}

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

// import additional classes
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

/**
 * The main driving class for the Mapping Service
 */
public class MappingServlet extends HttpServlet {

	// declare private variables
	private ServletConfig servletConfig;

	/*
	 * initialise this instance
	 */
	public void init(ServletConfig conf) throws ServletException {
		// execute the parent objects init method
		super.init(conf);
		
		// store configuration for later
		servletConfig = conf;
		
	} // end init method
	
	/**
	 * Method to respond to a get request
	 *
	 * @param request a HttpServletRequest object representing the current request
	 * @param response a HttpServletResponse object representing the current response
	 */
	public void doGet (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		// get the action parameter
		String action = request.getParameter("action");
		
		// check the action parameter
		if(action == null) {
			throw new ServletException("Missing action parameter");
		}
		
		// get a data manager object
		DataManager dataManager = new DataManager(servletConfig);
		
		// determine what action to take
		if(action.equals("markers")) {
			// need to build a markers XML file
			
			// get remaining parameters
			String type       = request.getParameter("type");
			String id         = request.getParameter("id");
			String startDate  = request.getParameter("start");
			String finishDate = request.getParameter("finish");
			String stateLimit = request.getParameter("state");
			
			// check on the parameters
			if(type == null || id == null) {
				throw new ServletException("Missing required parameters");
			}
			
			// double check the parameters
			type = type.trim();
			id   = id.trim();
			
			if(type == null || id == null || type.equals("") || id.equals("")) {
				throw new ServletException("Missing required parameters");
			}
			
			if(startDate != null) {
				startDate = startDate.trim();
			}
			
			if(finishDate != null) {
				finishDate = finishDate.trim();
			}
			
			// determine the type of Marker XML we need
			if(type.equals("orgdata") || type.equals("org")) {
				// build marker XML related to organisations
			
				// get an instance of the OrganisationDataBuilder class
				OrganisationDataBuilder orgData = new OrganisationDataBuilder(dataManager);
				
				// declare helper variable
				String results = null;
				
				// get the markers XML
				if(startDate == null || finishDate == null) {
					if(stateLimit == null) {
						results = orgData.getMarkerXMLString(id);
					} else {
						results = orgData.getMarkerXMLString(id, null, null, stateLimit);
					}
				} else {
					if(stateLimit == null) {
						results = orgData.getMarkerXMLString(id, finishDate, startDate);
					} else {
						results = orgData.getMarkerXMLString(id, finishDate, startDate, stateLimit);
					}
				}
				
				// ouput the XML
				// set the appropriate content type
				response.setContentType("text/xml; charset=UTF-8");
				
				// set the appropriate headers to disable caching
				// particularly for IE
				response.setHeader("Cache-Control", "max-age=0,no-cache,no-store,post-check=0,pre-check=0");
				
				//get the output print writer
				PrintWriter out = response.getWriter();
				
				// send some output
				out.print(results);
				
			} else if(type.equals("contributor")) {
				// build marker XML related to organisations
			
				// get an instance of the OrganisationDataBuilder class
				ContributorDataBuilder data = new ContributorDataBuilder(dataManager);
				
				// declare helper variable
				String results = null;
				
				if(stateLimit == null) {
					results = data.getMarkerXMLString(id);
				} else {
					results = data.getMarkerXMLString(id, stateLimit);
				}
				
				// ouput the XML
				// set the appropriate content type
				response.setContentType("text/xml; charset=UTF-8");
				
				// set the appropriate headers to disable caching
				// particularly for IE
				response.setHeader("Cache-Control", "max-age=0,no-cache,no-store,post-check=0,pre-check=0");
				
				//get the output print writer
				PrintWriter out = response.getWriter();
				
				// send some output
				out.print(results);
				
			} else {
				throw new ServletException("Unknown type parameter value");
			}
			
		} else if(action.equals("kml")) {
			// need to build a kml file
			
			// get remaining parameters
			String type = request.getParameter("type");
			String id   = request.getParameter("id");
			
			// check on the parameters
			if(type == null || id == null) {
				throw new ServletException("Missing required parameters");
			}
			
			// double check the parameters
			type = type.trim();
			id   = id.trim();
			
			if(type == null || id == null || type.equals("") || id.equals("")) {
				throw new ServletException("Missing required parameters");
			}
			
			// determine the type of kml we need
			if(type.equals("orgdata") || type.equals("org")) {
				// build marker KML related to organisations
			
				// get an instance of the OrganisationDataBuilder class
				OrganisationDataBuilder orgData = new OrganisationDataBuilder(dataManager);
				
				// get the markers XML
				String results = orgData.getKMLString(id);
				
				// get the organisation name
				String fileName = orgData.getOrgNameByID(id);
				fileName = fileName.toLowerCase();					   // lower case
				fileName = fileName.replaceAll("[^a-zA-Z0-9\\s]", ""); // remove anything not alphanumeric
				fileName = fileName.replaceAll(" ", "-");			   // replace spaces with dashes
				fileName += ".kml";
				
				// ouput the XML
				// set the appropriate content type
				response.setContentType("application/vnd.google-earth.kml+xml; charset=UTF-8");
				response.setHeader("Content-Disposition", "attachment;filename=" + fileName);

				//get the output print writer
				PrintWriter out = response.getWriter();
				
				// send some output
				out.print(results);
			
			} else if(type.equals("contrib")) {
				// build KML data for contributors
				ContributorDataBuilder data = new ContributorDataBuilder(dataManager);
				
				// get the KML data
				String results = data.getKMLString(id);
				
				// build a file name
				String fileName = null;
				
				if(id.indexOf(',') != -1) {
					fileName = "multiple-contributor-map.kml";
				} else {
					fileName = data.getNameByID(id);
					fileName = fileName.toLowerCase();					   // lower case
					fileName = fileName.replaceAll("[^a-zA-Z0-9\\s]", ""); // remove anything not alphanumeric
					fileName = fileName.replaceAll(" ", "-");			   // replace spaces with dashes
					fileName += ".kml";
				}
				
				// ouput the XML
				// set the appropriate content type
				response.setContentType("application/vnd.google-earth.kml+xml; charset=UTF-8");
				response.setHeader("Content-Disposition", "attachment;filename=" + fileName);

				//get the output print writer
				PrintWriter out = response.getWriter();
				
				// send some output
				out.print(results);
							
			} else {
				throw new ServletException("Unknown type parameter value");
			}
		} else if(action.equals("lookup")) {
			// need to lookup some value
			String results = "";
			
			// get remaining parameters
			String type = request.getParameter("type");
			String id   = request.getParameter("id");
			
			// check on the parameters
			if(type == null || id == null) {
				throw new ServletException("Missing required parameters");
			}
			
			// double check the parameters
			type = type.trim();
			id   = id.trim();
			
			if(type == null || id == null || type.equals("") || id.equals("")) {
				throw new ServletException("Missing required parameters");
			}
			
			// determine the type of lookup to do
			if(type.equals("orgname")) {
				// need to lookup the name of an organisation
				
				// get an instance of the OrganisationDataBuilder class
				OrganisationDataBuilder orgData = new OrganisationDataBuilder(dataManager);
				
				// get the organisation name
				results = orgData.getOrgNameByID(id);				
			
			} else if (type.equals("contribname")) {
				// need to lookup the name of an organisation
				
				// get an instance of the OrganisationDataBuilder class
				ContributorDataBuilder data = new ContributorDataBuilder(dataManager);
				
				// get the organisation name
				results = data.getNameByID(id);				
			
			} else if(type.equals("startdates")) {
				// need to lookup the start dates of events that can be mapped
				
				// get an instance of the OrganisationDataBuilder class
				OrganisationDataBuilder orgData = new OrganisationDataBuilder(dataManager);
				
				// get the organisation name
				results = orgData.getStartDatesForMap(id);
			
			} else {
				throw new ServletException("Unknown type parameter value");
			}			
			
			// ouput the results
			// set the appropriate content type
			response.setContentType("text/plain; charset=UTF-8");
			
			// set the appropriate headers to disable caching
			// particularly for IE
			response.setHeader("Cache-Control", "max-age=0,no-cache,no-store,post-check=0,pre-check=0");
			
			//get the output print writer
			PrintWriter out = response.getWriter();
			
			// send some output
			out.print(results);
		} else {
			// unknown action type
			throw new ServletException("Unknown action parameter value");
		}
	
	} // end doGet method
	
	/**
	 * Method to respond to a post request
	 *
	 * @param request a HttpServletRequest object representing the current request
	 * @param response a HttpServletResponse object representing the current response
	 */
	public void doPost (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		// determine action to take
		String action = request.getParameter("action");
		
		// check action parameter
		if(action == null) {
			throw new ServletException("Missing action parameter");
		}
		
		// get a data manager object
		DataManager dataManager = new DataManager(servletConfig);
		
		// do an organisation search		
		if(action.equals("org_search")) {
			
			// get the requested search term
			String searchTerm = request.getParameter("org_name");
			
			// check on the search term
			if(searchTerm == null) {
				throw new ServletException("Missing search terms, invalid post?");
			}
			
			// double check for empty string
			searchTerm = searchTerm.trim();
			
			if(searchTerm == null || searchTerm.equals("")) {
				throw new ServletException("Missing search terms");
			}
			
			// get the selected operator
			String searchOperator = request.getParameter("operator");
			
			if(searchOperator == null) {
				throw new ServletException("Missing search operator, invalid post?");
			}
			
			if(searchOperator.equals("and") == false && searchOperator.equals("or") == false && searchOperator.equals("exact") == false) {
				throw new ServletException("Invalid search operator, invalid post?");
			}
			
			// determine what to do with the search terms
			if(searchOperator.equals("and") || searchOperator.equals("or")) {
			
				// change search query to lower case
				searchTerm = searchTerm.toLowerCase();
				
				// remove any existing search terms
				searchTerm = searchTerm.replace(" and ", "");
				searchTerm = searchTerm.replace(" or ", "");
				searchTerm = searchTerm.replace(" not ", "");
				
				// rewrite the search terms
				searchTerm = searchTerm.replace(" ", " " + searchOperator + " ");
				
			}
			
			// get the limit parameter
			String searchStateLimit = request.getParameter("state");		
			
			// get an instance of the OrganisationDataBuilder class
			OrganisationDataBuilder orgData = new OrganisationDataBuilder(dataManager);
			
			// declare helper variables
			String results = "";
			
			// do the search
			if(searchStateLimit == null || searchStateLimit.equals("nolimit")) {
				results = orgData.doSearch(searchTerm);
			} else {
				results = orgData.doSearch(searchTerm, searchStateLimit);
			}

			// ouput the results
			// set the appropriate content type
			response.setContentType("text/plain; charset=UTF-8");
			
			// set the appropriate headers to disable caching
			// particularly for IE
			response.setHeader("Cache-Control", "max-age=0,no-cache,no-store,post-check=0,pre-check=0");
			
			//get the output print writer
			PrintWriter out = response.getWriter();
			
			// send some output
			out.print(results);

		} else if(action.equals("contributor_search")) {
			
			// get the requested search term
			String searchTerm = request.getParameter("contributor_name");
			
			// check on the search term
			if(searchTerm == null) {
				throw new ServletException("Missing search terms, invalid post?");
			}
			
			// double check for empty string
			searchTerm = searchTerm.trim();
			
			if(searchTerm == null || searchTerm.equals("")) {
				throw new ServletException("Missing search terms");
			}
			
			// get the selected operator
			String searchOperator = request.getParameter("operator");
			
			if(searchOperator == null) {
				throw new ServletException("Missing search operator, invalid post?");
			}
			
			if(searchOperator.equals("and") == false && searchOperator.equals("or") == false && searchOperator.equals("exact") == false) {
				throw new ServletException("Invalid search operator, invalid post?");
			}
			
			// determine what to do with the search terms
			if(searchOperator.equals("and") || searchOperator.equals("or")) {
			
				// change search query to lower case
				searchTerm = searchTerm.toLowerCase();
				
				// remove any existing search terms
				searchTerm = searchTerm.replace(" and ", "");
				searchTerm = searchTerm.replace(" or ", "");
				searchTerm = searchTerm.replace(" not ", "");
				
				// rewrite the search terms
				searchTerm = searchTerm.replace(" ", " " + searchOperator + " ");
				
			}
			
			// get the limit parameter
			String searchStateLimit = request.getParameter("state");		
			
			// get an instance of the OrganisationDataBuilder class
			ContributorDataBuilder data = new ContributorDataBuilder(dataManager);
			
			// declare helper variables
			String results = "";
			
			// do the search
			if(searchStateLimit == null || searchStateLimit.equals("nolimit")) {
				results = data.doSearch(searchTerm);
			} else {
				results = data.doSearch(searchTerm, searchStateLimit);
			}

			// ouput the results
			// set the appropriate content type
			response.setContentType("text/plain; charset=UTF-8");
			
			// set the appropriate headers to disable caching
			// particularly for IE
			response.setHeader("Cache-Control", "max-age=0,no-cache,no-store,post-check=0,pre-check=0");
			
			//get the output print writer
			PrintWriter out = response.getWriter();
			
			// send some output
			out.print(results);

		} else if(action.equals("export")) {
			
			// need to export some data in the KML format
			
			// get remaining parameters
			String type         = request.getParameter("type");
			String id           = request.getParameter("id");
			String timeSpan     = request.getParameter("time_span");
			String trajectory   = request.getParameter("trajectory");
			String elevation    = request.getParameter("elevation");
			String groupEvents  = request.getParameter("groupevents");
			
			// check on the parameters
			if(type == null || id == null) {
				throw new ServletException("Missing required parameters");
			}
			
			// double check the parameters
			type = type.trim();
			id   = id.trim();
			
			if(type == null || id == null || type.equals("") || id.equals("")) {
				throw new ServletException("Missing required parameters");
			}
			
			// build an options object for the export
			KMLExportOptions exportOptions = new KMLExportOptions();
			
			// include time span elements?
			if(timeSpan != null && timeSpan.equals("on")) {
				exportOptions.setOption("includeTimeSpanElements", "yes");
			}
			
			// include trajectory information?
			if(trajectory != null && trajectory.equals("on")) {
				exportOptions.setOption("includeTrajectoryInfo", "yes");
			}
			
			// include elevation information
			if(elevation != null && trajectory.equals("on")) {
				exportOptions.setOption("includeElevationInfo", "yes");
			}
			
			// include grouped event information
			if(groupEvents != null && groupEvents.equals("on")) {
				exportOptions.setOption("includeGroupedEventInfo", "yes");
			}
			
			// check on the type parameter
			if(type.equals("org")) {
				// build marker KML related to organisations
			
				// get an instance of the OrganisationDataBuilder class
				OrganisationDataBuilder orgData = new OrganisationDataBuilder(dataManager);
				
				// get the markers XML
				String results = orgData.doKMLExport(id, exportOptions);
				
				// get the file name
				String fileName = orgData.getFileName(orgData.getOrgNameByID(id), "kml");
				
				
				// ouput the XML
				// set the appropriate content type
				response.setContentType("application/vnd.google-earth.kml+xml; charset=UTF-8");
				response.setHeader("Content-Disposition", "attachment;filename=" + fileName);

				//get the output print writer
				PrintWriter out = response.getWriter();
				
				// send some output
				out.print(results);
			
			} else if(type.equals("contrib")) {
				// build KML data for contributors
				ContributorDataBuilder data = new ContributorDataBuilder(dataManager);
				
				// get the KML data
				String results = data.doKMLExport(id, exportOptions);
				
				// build a file name
				String fileName = null;
				
				if(id.indexOf(',') != -1) {
					fileName = "multiple-contributor-map.kml";
				} else {
					fileName = data.getNameByID(id);
					fileName = fileName.toLowerCase();					   // lower case
					fileName = fileName.replaceAll("[^a-zA-Z0-9\\s]", ""); // remove anything not alphanumeric
					fileName = fileName.replaceAll(" ", "-");			   // replace spaces with dashes
					fileName += ".kml";
				}
				
				// ouput the XML
				// set the appropriate content type
				response.setContentType("application/vnd.google-earth.kml+xml; charset=UTF-8");
				response.setHeader("Content-Disposition", "attachment;filename=" + fileName);

				//get the output print writer
				PrintWriter out = response.getWriter();
				
				// send some output
				out.print(results);
				
			} else {
				throw new ServletException("Unknown type parameter value");
			}
			// end export routine
		} else if(action.equals("org_id_search")) {
			// search for an organisation using an id
			
			// get the requested search term
			String searchTerm = request.getParameter("org_id");
			
			// check on the search term
			if(searchTerm == null) {
				throw new ServletException("Missing search terms, invalid post?");
			}
			
			// double check for empty string
			searchTerm = searchTerm.trim();
			
			if(searchTerm == null || searchTerm.equals("")) {
				throw new ServletException("Missing search term");
			}
			
			// get an instance of the OrganisationDataBuilder class
			OrganisationDataBuilder orgData = new OrganisationDataBuilder(dataManager);
			
			// do the search
			// if the id isn't in the database this method will throw an exception
			String results = null;
			try {
				results = orgData.getOrgNameByID(searchTerm);
			} catch (Exception ex) {
				// leave results at the null value
			}
			
			// determine what to return
			if(results == null) {
				results = "not found";
			} else {
				results = "/mapping/maplinks.jsp?type=org&id=" + Integer.parseInt(searchTerm);
			}
			
			// ouput the results
			// set the appropriate content type
			response.setContentType("text/plain; charset=UTF-8");
			
			// set the appropriate headers to disable caching
			// particularly for IE
			response.setHeader("Cache-Control", "max-age=0,no-cache,no-store,post-check=0,pre-check=0");
			
			//get the output print writer
			PrintWriter out = response.getWriter();				
			
			// send some output
			out.print(results);
					
		}else {
			// unknown action type
			throw new ServletException("Unknown action type");
		}	
	
	} // end doPost method
	
} // end class definition

<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%
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
%>
<!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="utf-8" />
	<title>AusStage Mapping Service (Beta)</title>
	<link rel="stylesheet" href="assets/main-style.css"/>
	<link rel="stylesheet" href="assets/ausstage-colours.css"/>
	<link rel="stylesheet" href="assets/ausstage-background-colours.css"/>
	<link rel="stylesheet" href="assets/jquery-ui/jquery-ui-1.8.6.custom.css"/>
	<jsp:include page="analytics.jsp"/>
	<!-- libraries -->
	<script type="text/javascript" src="assets/javascript/libraries/jquery-1.4.3.min.js"></script>
	<script type="text/javascript" src="assets/javascript/libraries/jquery-ui-1.8.6.custom.min.js"></script>
	<script type="text/javascript" src="assets/javascript/libraries/jquery.tipsy-1.0.0a.js"></script>
	<script type="text/javascript" src="assets/javascript/libraries/jquery.ajaxmanager-3.06.js"></script>
	<script type="text/javascript" src="assets/javascript/libraries/jquery.validate-1.7.min.js"></script>
	<script type="text/javascript" src="assets/javascript/libraries/jquery.cookie-1.0.js"></script>
	<script type="text/javascript" src="http://maps.google.com/maps/api/js?v=3.3&amp;sensor=false"></script>
	<script type="text/javascript" src="assets/javascript/libraries/markerwithlabel-1.0.1.js"></script>
	<!-- custom code -->
	<script type="text/javascript" src="assets/javascript/common.js"></script>
	<script type="text/javascript" src="assets/javascript/search.js"></script>
	<script type="text/javascript" src="assets/javascript/browse.js"></script>
	<script type="text/javascript" src="assets/javascript/mapping.js"></script>
	<script type="text/javascript" src="assets/javascript/index.js"></script>
	<!-- prevent a FOUC from the messages div -->
	<script type="text/javascript">
		$('html').addClass('js');
	</script>
</head>
<body>
<div class="wrapper">
	<div class="header b-187"><h1>AusStage Mapping Service</h1></div>
	<div class="sidebar b-186 f-184">
		<h2>Main Menu</h2>
		<ul>
			<li><a href="http://www.ausstage.edu.au">AusStage Website</a></li>
			<li><a href="/mapping2/" title="Networks Service Homepage">Mapping Homepage</a></li>
		</ul>
	</div>
	<div class="main b-184 f-187">
		<div id="tabs" class="tab-container">
			<ul class="fix-ui-tabs">
				<li><a href="#tabs-1">Search</a></li>
				<li><a href="#tabs-2">Venues</a></li>
				<li><a href="#tabs-3">Map</a></li>
			</ul>
			<div>
				<div id="tabs-1">
					<h2 style="margin-bottom: -5px;">Search the AusStage Database</h2>
					<form action="/mapping/" method="get" id="search" name="search">
						<table class="formTable" style="height: 60px;">
							<tbody>
							<tr>
								<td style="width: 50%">
									<input type="search" size="40" id="query" name="query" title="Enter a few search terms into this box"/> <input type="submit" name="submit" id="search_btn" value="Search"/> <span id="show_search_help" class="ui-icon ui-icon-help clickable use-tipsy" style="display: inline-block;" title="Search Help"></span>
								</td>
								<td style="width: 40%">
									<div id="messages">
										<div class="ui-state-highlight ui-corner-all search-status-messages" id="status_message">
											<p>
												<span class="ui-icon ui-icon-info status-icon"></span>
												<span id="message_text"></span>
											</p>
										</div>
										<div class="ui-state-error ui-corner-all search-status-messages" id="error_message"> 
											<p><span class="ui-icon ui-icon-alert status-icon"></span> 
											<span id="error_text"></span>
											</p>
										</div>										
									</div>
								</td>
							</tr>
							</tbody>
						</table>
					</form>
					<h3>Search Results</h3>
					<div class="accordion">
						<h3><a href="#" id="contributor_heading">Contributors</a></h3>
							<div id="contributor_results"></div>
					</div>
					<div class="accordion">
						<h3><a href="#" id="organisation_heading">Organisations</a></h3>
							<div id="organisation_results"></div>
					</div>
					<div class="accordion">
						<h3><a href="#" id="venue_heading">Venues</a></h3>
							<div id="venue_results"></div>
					</div>
					<div class="accordion">
						<h3><a href="#" id="event_heading">Events</a></h3>
							<div id="event_results"></div>
					</div>
					<h3 style="padding-top: 15px;">Search History</h3>
					<div class="accordion">
						<h3><a href="#" id="search_history_heading">Previous Searches</a></h3>
						<table class="searchResults">
							<thead>
								<tr>
									<th>Query</th><th>Persistent Link</th><th>Results</th>
								</tr>
							</thead>
							<tbody id="search_history">
							</tbody>
						</table>
					</div>
				</div>
				<div id="tabs-2">
					<h2>Browse Areas to Find Venues</h2>
					<div id="browse_header" style="height: 30px; width=100%">
						<div style="float:left; width: 26%; height: 100%; padding-right: 10px;">
							<h3>Countries and States</h3>
						</div>
						<div style="float:left; width: 32%; height: 100%; margin-left: 10px;">
							<h3>Suburbs</h3>
						</div>
						<div style="float: right; width: 37%; height: 100%; padding-left: 10px;">
							<h3>Venues</h3>
						</div>
					</div>
					<div id="browse_content" style="height: 380px;">
						<div id="browse_major_area" style="float: left; width: 26%; height: 100%; overflow: auto; border-right: 1px solid #000; padding-right: 10px;"></div>
						<div id="browse_suburb" style="float: left; width: 32%; height: 100%; overflow: auto; border-right: 1px solid #000; padding-right: 10px;"></div>
						<div id="browse_venue"  style="float: right; width: 37%; height: 100%; overflow: auto; padding-left: -10px;"></div>
					</div>
					<div id="browse_footer" style="height:50px; width=100%;">
						<div style="float:left; padding-top: 15px;" id="browse_messages"></div>
						<div style="float:right; padding-top: 15px;">
							<input type="button" id="browse_add_btn" value="Add To Map"/><span class="ui-icon ui-icon-help clickable use-tipsy show_add_view_help" style="display: inline-block;" title="Add / View Map Help"></span>
						</div>
					</div>
				</div>
				<div id="tabs-3">
					<h3>Map of the Selected Items</h3>
					<div id="map_div">
					</div>
				</div>
			</div>
		</div>
	</div>
	<!-- always at the bottom of the content -->
	<div class="push"></div>
</div>
<jsp:include page="footer.jsp"/>
<!-- help divs -->
<div id="help_search_div" title="Help on Searching" style="font-size: 90%">
	<p>
		This is a simplified search page that returns results in the four categories outlined below. A search result will contain all of your search terms in the search index, including such items as
		alternate names, and previous names. Please wait for the search to complete before exploring the results.
		<br/>&nbsp;<br/>
		At most 25 search results will be displayed under each category. If you find a search result is missing, refine your search terms. If a search result is still missing, use the main <a href="http://www.ausstage.edu.au/" title="Main AusStage website">AusStage</a> website to ensure
		that the item you are looking for is associated with a venue which has coordinates stored in the AusStage database.			
	</p>
</div>
<div id="help_add_view_div" title="Help on Adding Items and Viewing the Map" style="font-size: 90%">
	<h3>Adding items to the map</h3>
	<p>
		Click the 'Add to Map' button to add the currently selected items to the map. The map will be displayed automatically.
		<br/>&nbsp;<br/>
		Alternatively to view the map click the 'Map' tab at the top of the page.
	</p>
	<h3>Browsing Areas to Find Venues</h3>
	<p>
		Click on the 'Venues' tab if you have not already done so.
		<br/>&nbsp;<br/>
		Click on an item to see a list of items at the next level, tick the box to select all items at lower levels
	</p>
</div>
</body>
</html>

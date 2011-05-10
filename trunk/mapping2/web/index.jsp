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
	<title>Aus-e-Stage Mapping Events</title>
	<link rel="stylesheet" href="assets/main-style.css"/>
	<link rel="stylesheet" href="assets/ausstage-colours.css"/>
	<link rel="stylesheet" href="assets/ausstage-background-colours.css"/>
	<link rel="stylesheet" href="assets/jquery-ui/jquery-ui-1.8.6.custom.css"/>
	<link rel="stylesheet" href="assets/rangeslider/dev.css"/>
	<jsp:include page="analytics.jsp"/>
	<!-- libraries -->
	<script type="text/javascript" src="assets/javascript/libraries/jquery-1.6.min.js"></script>
	<script type="text/javascript" src="assets/javascript/libraries/jquery-ui-1.8.12.custom.min.js"></script>
	<script type="text/javascript" src="assets/javascript/libraries/jquery.tipsy-1.0.0a.js"></script>
	<script type="text/javascript" src="assets/javascript/libraries/jquery.ajaxmanager-3.11.js"></script>
	<script type="text/javascript" src="assets/javascript/libraries/jquery.validate-1.7.min.js"></script>
	<script type="text/javascript" src="assets/javascript/libraries/jquery.cookie-1.0.js"></script>
	<script type="text/javascript" src="assets/javascript/libraries/jquery.scrollto-1.4.2.js"></script>
	<script type="text/javascript" src="http://maps.google.com/maps/api/js?v=3.3&sensor=false"></script>
	<script type="text/javascript" src="assets/javascript/libraries/markerwithlabel-1.1.js"></script>
	<script type="text/javascript" src="assets/javascript/libraries/markerclusterer-1.0.js"></script>
	<script type="text/javascript" src="assets/javascript/libraries/jquery.rangeslider-2.1.js"></script>
	<script type="text/javascript" src="assets/javascript/libraries/jquery.daterangeslider-2.1.js"></script>
	<script type="text/javascript" src="assets/javascript/libraries/jquery.throttle.1.1.js"></script>
	<!-- custom code -->
	<script type="text/javascript" src="assets/javascript/index.js"></script>
	<script type="text/javascript" src="assets/javascript/common.js"></script>
	<script type="text/javascript" src="assets/javascript/search.js"></script>
	<script type="text/javascript" src="assets/javascript/browse.js"></script>
	<script type="text/javascript" src="assets/javascript/mapping.js"></script>
	<script type="text/javascript" src="assets/javascript/map-legend.js"></script>
	<script type="text/javascript" src="assets/javascript/timeline.js"></script>
	<script type="text/javascript" src="assets/javascript/bookmark.js"></script>
	<!-- prevent a FOUC -->
	<script type="text/javascript">
		$('html').addClass('js');
	</script>
</head>
<body>
<div class="wrapper">
	<div class="header b-187"><h1>Mapping Events</h1></div>
	<div class="sidebar b-186 f-184">
		<div class="peekaboo-tohide mainMenu">
			<ul>
				<li>&nbsp;</li>
				<li><a href="http://www.ausstage.edu.au" title="AusStage Website homepage">AusStage Website</a> <span class="peekaboo clickable" style="float: right; padding-right: 1em;">[Hide]</span></li>
				<li><a href="http://beta.ausstage.edu.au/" title="Aus-e-Stage Project homepage">Aus-e-Stage Project</a></li>
				<li><a href="http://beta.ausstage.edu.au/mapping" title="Mapping Events homepage">Mapping Events</a></li>
				<li class="map-icon-help clickable">Map Iconography</li>
			</ul>
		</div>
		<ul class="peekaboo-show peekaboo-big">
			<li class="peekaboo clickable">&raquo;</li>
		</ul>
		<div class="mapControlsContainer js peejaboo-tohide">
			<hr style="width: 95%; margin-top: 5px;" class="f-184 b-184"/>
			<div class="accordion" id="mapLegendPanAndZoomControls">
				<h3><a href="#" id="mapLegendPanAndZoomHeading">Clustering and Focus</a></h3>				
				<div id="mapLegendPanAndZoom" class="mapLegendInnerContainer">
				</div>
			</div>
			<div class="accordion" id="mapLegendAdvancedControls">
				<h3><a href="#" id="mapLegendAdvancedControlsHeading">Bookmark and Download</a></h3>
				<div class="mapLegendInnerContainer">
					<ul>
						<li class="map-bookmark-open clickable">Bookmark this map</li>
						<li class="map-kml-download clickable">Download this map as KML</li>
					</ul>
				</div>
			</div>
		</div>
		<div class="mapLegendContainer js peekaboo-tohide">
			<div class="accordion">
				<h3><a href="#" id="mapLegendContributorHeading">Contributors</a></h3>
				<div id="mapLegendContributors" class="mapLegendInnerContainer">
				</div>
				
			</div>
			<div class="accordion">
				<h3><a href="#" id="mapLegendOrganisationHeading">Organisations</a></h3>
				<div id="mapLegendOrganisations" class="mapLegendInnerContainer">
				</div>
				
			</div>
			<div class="accordion">
				<h3><a href="#" id="mapLegendVenueHeading">Venues</a></h3>
				<div id="mapLegendVenues" class="mapLegendInnerContainer">
				</div>
				
			</div>
			<div class="accordion">
				<h3><a href="#" id="mapLegendEventsHeading">Events</a></h3>
				<div id="mapLegendEvents" class="mapLegendInnerContainer">
				</div>
				
			</div>
		</div>
	</div>
	<div class="main b-184 f-187">
		<div id="tabs" class="tab-container">
			<ul class="fix-ui-tabs">
				<li><a href="#tabs-1">Search</a></li>
				<li><a href="#tabs-2">Browse</a></li>
				<li><a href="#tabs-3">Map</a></li>
			</ul>
			<div>
				<div id="tabs-1">
					<p>Search the AusStage database</p>
					<form action="/mapping/" method="get" id="search" name="search">
						<table class="formTable" style="height: 60px;">
							<tbody>
							<tr>
								<td style="width: 50%">
									<input type="search" size="40" id="query" name="query" title="Enter a few search terms into this box"/> <input type="submit" name="submit" id="search_btn" value="Search"/> <span id="show_search_help" class="ui-icon ui-icon-help clickable use-tipsy" style="display: inline-block;" title="Search Help"></span>
								</td>
								<td style="width: 40%">
									<div id="search_messages" class="js">
										<div class="ui-state-highlight ui-corner-all search-status-messages" id="search_status_message">
											<p>
												<span class="ui-icon ui-icon-info status-icon"></span>
												<span id="search_message_text"></span>
											</p>
										</div>
										<div class="ui-state-error ui-corner-all search-status-messages" id="search_error_message"> 
											<p><span class="ui-icon ui-icon-alert status-icon"></span> 
											<span id="search_error_text"></span>
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
					<div style="max-width: 990px">
						<div id="browse_header" style="height: 30px; width=100%">
							<div style="float:left; width: 26%; height: 100%; padding-right: 10px;">
								<p style="font-weight: bold">Countries and States</p>
							</div>
							<div style="float:left; width: 32%; height: 100%; margin-left: 10px;">
								<p style="font-weight: bold">Cities, Suburbs and Localities</p>
							</div>
							<div style="float: right; width: 37%; height: 100%; padding-left: 10px;">
								<p style="font-weight: bold">Venues</p>
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
								<input type="button" id="browse_reset_btn" value="Reset Browse List"/><input type="button" id="browse_add_btn" value="Add To Map"/><span class="ui-icon ui-icon-help clickable use-tipsy show_add_view_help" style="display: inline-block;" title="Add to Map Help"></span>
							</div>
						</div>
					</div>
				</div>
				<div id="tabs-3">
					<div id="map_container_div">
						<div id="map_div" style="height: 100%; width: 100%">
						</div>
						<div style="height: 25px;"></div>
						<div>
							<div class="timeSliderContainer" style="float: left; height: 30px;">
								<div id="timeSlider">
								</div>
							</div>
							<div id="mapResetButtonContainer" style="float: right;">
									<input type="button" name="btn_reset_map" id="btn_reset_map" value="Reset Map"/>
							</div>
						</div>
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
<div id="help_search_div" title="Help on Searching" class="dialogs">
	<h3>Searching the AusStage database</h3>
	<p>
		This is a simplified search page that returns results in the four categories outlined below. A search result will contain all of your search terms in the search index, including such items as
		alternate names, and previous names. Please wait for the search to complete before exploring the results.
	</p>
	<p>
		At most 25 search results will be displayed under each category. If you find a search result is missing, refine your search terms. If a search result is still missing, use the main <a href="http://www.ausstage.edu.au/" title="Main AusStage website">AusStage</a> website to ensure
		that the item you are looking for is associated with a venue which has coordinates stored in the AusStage database.			
	</p>
</div>
<div id="help_add_view_div" title="Help on Adding Items and Viewing the Map" class="dialogs">
	<h3>Adding items to the map</h3>
	<p>
		Click the 'Add to Map' button to add the currently selected items to the map. The map will be displayed automatically.
	</p>
	<p>
		Alternatively to view the map click the 'Map' tab at the top of the page.
	</p>
	<h3>Browsing Areas to Find Venues</h3>
	<p>
		Click on the 'Venues' tab if you have not already done so.
	</p>
	<p>
		Click on an item to see a list of items at the next level, tick the box to select all items at lower levels
	</p>
</div>
<div id="help_map_icons_div" title="Help on Map Iconography" class="dialogs">
	<h3>AusStage Map Iconography</h3>
	<p>
		Map iconography refers to the way in which information is displayed on a map using icons. The AusStage mapping service uses a number of icons to display information on a map.
	</p>
	<p>
		The colour of an icon indicates the number records associated with the location.
	</p>
	<p>
		When a contributor or organisation is added to a map, one of a sequence of 48 colours will be assigned to that record. This individual colour will be used when there is only one organisation or contributor associated with the location.
	</p>
	<p>
		The iconography and colours used in AusStage maps is as follows:
	</p>
	<p><strong>Map Icons and Colours</strong></p>
	<table id="map_iconography_table" class="mapIconHelpTbl">
		<thead>
			<tr>
				<th class="nowrap">&nbsp;</th>
				<th class="nowrap">1 record</th>
				<th class="nowrap">2 - 5</th>
				<th class="nowrap">6 - 15</th>
				<th class="nowrap">16 - 30</th>
				<th class="nowrap">31+</th>
			</tr>
		</thead>
		<tbody>
			<tr id="map_iconography_contributors">
			</tr>
			<tr id="map_iconography_organisations">
			</tr>
			<tr id="map_iconography_venues">
			</tr>
			<tr id="map_iconography_events">
			</tr>
		</tbody>
	</table>
	<p><strong>Individual Colour Pallette</strong></p>
	<table>
		<tbody id="map_iconography_individual_colours">
		</tbody>
	</table>
	<p><strong>Marker Clusters</strong></p>
	<p>
		When Marker Clustering is enabled markers on the map are clustered together based on how close they are on the map.
	</p>
	<p>
		When two or more markers are close enough to each other at the current map zoom level they are clustered together.
	</p>
	<p>
		A marker cluster is shown on the map using the icon below. 
	</p>
	<p>
		The number shown on a marker cluster icon indicates the number of markers clustered together.
	</p>
	<div style="background-color: #c2d2e1; width:96px; height:96px;">
		<img src="assets/images/iconography/cluster.png" width="96" height="96" alt="Marker Cluster Icon"/>
	</div>
</div>
<div id="map_legend_confirm_delete" title="Confirm Map Marker Deletion" class="dialogs">
	<h3>Are you sure?</h3>
	<p>
		Are you sure you want to delete the marker from the map?
	</p>
	<div id="map_legend_confirm_delete_text">
	</div>
</div>
<div id="map_legend_confirm_reset" title="Confirm Map Reset" class="dialogs">
	<h3>Are you sure?</h3>
	<p>
		Are you sure you want to reset the map and delete all markers from the map?
	</p>
	<p>
		Please note your search history will be preserved and is available under the 'Search History' heading on the Search tab.
	</p>
</div>
<div id="map_legend_clustering_error" title="Unable to Disable Clustering" class="dialogs">
	<h3>Clustering cannot be disabled at this time</h3>
	<p>
		Clustering is automatically enabled when <span class="mlce_max"></span> markers are added to the map (including hidden markers).
	</p>
	<p>
		You currently have <span class="mlce_current"></span> markers added to the map. Delete some markers and then try again.
	</p>
	<p>
		If you feel the limit is too low, please contact the AusStage team
	</p>
</div>
<div id="map_bookmark_div" title="Bookmark this Map" class="dialogs">
	<h3>Create a bookmark for this map</h3>
	<p>
		The link below will be a bookmark to this map including all of the contributors, organisations, venues and events that you have added to the map.
	</p>
	<p>
		It will not preserve any hidden markers or the selected time period in the time slider. 
	</p>
	<p>
		&nbsp;
	</p>
	<p>
		<a href="" title="Bookmark this Map" id="map_bookmark_link">Aus-e-Stage Map Bookmark</a>
	</p>
</div>
<div id="map_bookmark_error_div" title="Bookmark this Map" class="dialogs">
	<h3>A bookmark cannot be created for this map</h3>
	<p>
		Bookmarks are only available when the map contains less than <span class="mlce_max"></span> markers (including hidden markers).
	</p>
	<p>
		You currently have <span class="mlce_current"></span> markers added to the map. Delete some markers and then try again.
	</p>
</div>
<div id="map_bookmark_loading_div" title="Loading Map from Bookmark" class="dialogs">
	<h3>Loading Map Data</h3>
	<div class="ui-state-highlight ui-corner-all search-status-messages" id="status_message">
		<p>
			<span class="ui-icon ui-icon-info status-icon"></span>
			 Loading data for the map, please wait...
		</p>
	</div>
</div>
</body>
</html>

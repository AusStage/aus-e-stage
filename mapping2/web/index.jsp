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
	<script type="text/javascript" src="assets/javascript/download.js"></script>
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
				<li><a href="http://www.ausstage.edu.au" title="AusStage Website homepage">AusStage Website</a> <span style="float: right; padding-right: 1em;">[<span class="peekaboo clickable">Hide</span>]</span></li>
				<li><a href="http://beta.ausstage.edu.au/" title="Aus-e-Stage Project homepage">Aus-e-Stage Project</a></li>
				<li><a href="http://beta.ausstage.edu.au/mapping" title="Mapping Events homepage">Mapping Events</a></li>
				<li class="map-icon-help clickable">About Maps</li>
			</ul>
		</div>
		<ul class="peekaboo-show peekaboo-big">
			<li class="peekaboo clickable">&raquo;</li>
		</ul>
		<div class="mapControlsContainer js peejaboo-tohide">
			<hr style="width: 95%; margin-top: 5px;" class="f-184 b-184"/>
			<div class="accordion" id="mapLegendPanAndZoomControls">
				<h3><a href="#" id="mapLegendPanAndZoomHeading">View</a></h3>				
				<div id="mapLegendPanAndZoom" class="mapLegendInnerContainer">
				</div>
			</div>
			<div class="accordion" id="mapLegendAdvancedControls">
				<h3><a href="#" id="mapLegendAdvancedControlsHeading">Save</a></h3>
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
									<input type="search" size="40" id="query" name="query" title="Enter a few search terms into this box"/> <input type="submit" name="submit" id="search_btn" value="Search"/> <span id="show_search_help" class="helpIcon clickable"></span>
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
					<p>
						&nbsp;
					</p>
					<div class="accordion">
						<h3><a href="#" id="search_history_heading">Search History</a></h3>
						<table class="searchResults">
							<thead>
								<tr>
									<th>Query - click to repeat</th><th>Link - right-click to bookmark</th><th class="alignRight">Contributors</th><th class="alignRight">Organisations</th><th class="alignRight">Venues</th><th class="alignRight">Events</th>
								</tr>
							</thead>
							<tbody id="search_history">
							</tbody>
						</table>
					</div>
					<p>
						&nbsp;
					</p>
					<p>
						A draft information manual for the Mapping Events service is available for download <a href="http://aus-e-stage.googlecode.com/svn/trunk/wiki-assets/Aus-e-Stage-Mapping-Events-module-for-AusStage-Manual.pdf" title="Download the PDF file">here</a>.
					</p>
					<p>
						If you have any feedback, questions or queries about the Mapping Events service, please <a href="http://beta.ausstage.edu.au/?tab=contacts" title="Contact information">contact us</a>.
					</p>
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
								<input type="button" id="browse_reset_btn" value="Reset Browse"/><input type="button" id="browse_add_btn" value="Add To Map"/> <span id="show_browse_help" class="helpIcon clickable"></span>
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
<div id="help_search_div" title="Help" class="dialogs">
	<h3>Searching the AusStage database</h3>
	<p>
		This search retrieves contributors, organisations, venues and events that match all your search terms. Records with matching alternate or previous names will also be retrieved. Please wait for the search to complete before exploring the results. 
	</p>
	<p>
		To search for an exact phrase enclose the phrase in double quotation marks. For example &quot;The Ugly One&quot;. Include only keywords or an exact phrase in a single search, not both. 
	</p>
	<p>
		A maximum of 25 search results will retrieved for each type of record. If you don't see what you're looking for, try refining your search terms. You could also try searching the main <a href="http://www.ausstage.edu.au/" title="AusStage homepage">AusStage</a> website.
	</p>
</div>
<div id="help_add_view_div" title="Help" class="dialogs">
	<h3>Adding Items to the Map</h3>
	<p>
		Select items by ticking the box next to the name. Click the Add to Map button to add the selected items to the map. The map will be displayed when the items have been added. 
	</p>
	<p>
		Switch between the Search, Browse and Map tabs across the top to continue searching, browsing and adding items to the map.
	</p>
</div>
<div id="help_browse_div" title="Help" class="dialogs">
	<h3>Browsing for Venues</h3>
	<p>
		Browse for venues by clicking on the countries or states to reveal cities, suburbs and localities. Click on cities, suburbs and localities to reveal venues. Tick the box next to a name to select items at the next level down.
	</p>
	<p>
		Click the Add to Map button to add venues to the map. The map will be displayed when the venues have been added. 
	</p>
	<p>
		Switch between the Search, Browse and Map tabs across the top to continue searching, browsing and adding items to the map.
	</p>
</div>
<div id="help_map_icons_div" title="About Maps" class="dialogs">
	<h3>Icons</h3>
	<p>
		AusStage uses icons to display information on the map. There are different icons for contributors, organisations, venues and events. When two or more icons are at the same location, they are grouped together in a row above a single marker.
	</p>
	<p>
		The colour of an icon and the number beneath indicate how many records an icon represents. When an individual contributor or organisation is added to the map, an individual colour is assigned to its icon. When an icon on the map represents two or more contributors, organisations, venues or events, colour shades from light to dark indicates how many.
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
	<h3>View</h3>
	<p>
		Change your view of the map by using the pan and zoom controls on the map. You can also change views by clicking on one of the pre-set views for country, state and capital cities.
	</p>
	<p>
		Markers are clustered together to make the map easier to read. When two or more markers are close to each other, they may be replaced by a gray icon representing a cluster. The number indicates how many markers are clustered together. 
	</p>
	<p>
		Click on a cluster to zoom in and reveal the individual markers contained in the cluster. Clustering is automatically enabled when there are 100 or more markers on the map.
	</p>
	<p style="background-color: #c2d2e1; width:96px; height:96px;">
		<img src="assets/images/iconography/cluster.png" width="96" height="96" alt="Marker Cluster Icon"/>
	</p>
	<h3>Save</h3>
	<p>
		Simple maps with less than 100 markers may be saved as a bookmark in your browser. The bookmark will retrieve the latest data from AusStage each time you reload your map.
	</p>
	<p>
		Maps may be downloaded for viewing in other map software such as <a href="http://www.google.com/earth/index.html" title="More information on Google Earth">Google Earth</a>. Map files are formatted in <a href="http://code.google.com/apis/kml/documentation/" title="Documentation on KML">Keyhole Markup Language</a> (KML). 
	</p>
	<h3>Contributors | Organisations | Venues | Events</h3>
	<p>
		The legend lists the contributors, organisations, venues and events on the map. From the legend, you can show and hide icons, and delete icons from the map. Clicking on a venue or contributor in the legend will reveal the icon on the map. 
	</p>
</div>
<div id="map_legend_confirm_delete" title="Confirm Delete" class="dialogs">
	<h3>Do you want to delete this marker from the map?</h3>
	<div id="map_legend_confirm_delete_text">
	</div>
</div>
<div id="map_legend_confirm_reset" title="Confirm Reset" class="dialogs">
	<h3>Do you want to reset the map?</h3>
	<p>
		If you reset the map, all icons will be deleted, the legend will be emptied and the map will be re-centered. Your search history will remain available on the Search tab.
	</p>
</div>
<div id="map_legend_clustering_error" title="Clustering" class="dialogs">
	<h3>Unable to disable clustering</h3>
	<p>
		Clustering is automatically enabled when <span class="mlce_max"></span> markers are added to the map (including hidden markers). You currently have <span class="mlce_current"></span> markers added to the map. To disable clustering, remove some markers from the map and try again. If you feel the limit is too low, please <a href="http://beta.ausstage.edu.au/?tab=contacts" title="Contact information">contact us</a>.
	</p>
</div>
<div id="map_bookmark_div" title="Bookmark" class="dialogs">
	<h3>Save a bookmark for this map</h3>
	<p>
		<a href="" title="Bookmark this Map" id="map_bookmark_link">AusStage Map Bookmark</a> - Add this link as a bookmark to your browser. The bookmark will retrieve the latest data from AusStage each time you reload your map. The map includes all the contributors, organisations, venues and events you have added. Hidden markers will be visible and the time-slider will be maximised.
	</p>
</div>
<div id="map_bookmark_error_div" title="Bookmark" class="dialogs">
	<h3>Save a bookmark for this map</h3>
	<p>
		Bookmarks are only available when the map contains less than <span class="mlce_max"></span> markers (including hidden markers). You currently have <span class="mlce_current"></span> markers added to the map. Delete some markers and then try again.
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

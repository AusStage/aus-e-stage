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
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:v="urn:schemas-microsoft-com:vml" lang="en">
<head>
	<title>AusStage Mapping Service (Beta)</title>
	<meta http-equiv="Content-Type" content="text/html;charset=UTF-8"/> 
	<link rel="stylesheet" type="text/css" media="screen" href="assets/main-style.css"/>
	<link rel="stylesheet" type="text/css" media="screen" href="assets/jquery-ui/jquery-ui-1.7.2.custom.css"/>
	<link rel="stylesheet" type="text/css" media="screen" href="assets/jquery-ui/ui.slider.extras.css"/>
	<link rel="stylesheet" type="text/css" media="screen" href="assets/jquery.cluetip.css"/>
	<script type="text/javascript" src="assets/javascript/libraries/jquery-1.3.2.min.js"></script>
	<script type="text/javascript" src="assets/javascript/libraries/jquery.form-2.36.js"></script>
	<script type="text/javascript" src="assets/javascript/libraries/jquery.validate-1.6.min.js"></script>
	<script type="text/javascript" src="assets/javascript/libraries/jquery.selectboxes-2.2.4.min.js"></script>
	<script type="text/javascript" src="assets/javascript/libraries/jquery-ui-1.7.2.custom.min.js"></script>
	<script type="text/javascript" src="assets/javascript/libraries/selectToUISlider.jQuery.js"></script>
	<script type="text/javascript" src="assets/javascript/libraries/jquery.cookie-1.0.js"></script>
	<script type="text/javascript" src="assets/javascript/libraries/jquery.cluetip.js"></script>
	<script type="text/javascript" src="assets/javascript/libraries/jquery.scrollTo-min.js"></script>	
	<script type="text/javascript" src="assets/javascript/organisations.js"></script>
	<script type="text/javascript" src="assets/javascript/map.functions.js"></script>
	<%
		ServletContext context = getServletContext();
		String mapsAPI   = (String)context.getInitParameter("googleMapsAPIUrl");
	%>
	<script type="text/javascript" src="<%=mapsAPI%>"></script>
	<script type="text/javascript" src="assets/javascript/libraries/mapiconmaker-1.1.js"></script>
</head>
<body>
<div id="wrap">
	<div id="header"><h1>AusStage Mapping Service (Beta)</h1></div>
	<div id="nav">
	</div>
	<!-- Include the sidebar -->
	<jsp:include page="sidebar.jsp"/>
	<div id="main">
		<h2>Map Events by Organisation</h2>
		<p>
			Use the forms below to search for, and then select, organisations to create a map of their events.
		</p>
		<div id="tabs">
			<ul>
				<li><a href="#tabs-1">Search by Name</a></li>
				<li><a href="#tabs-2">Search by ID</a></li>
				<!--<li><a href="#tabs-3">Map Multiple Contributors</a></li>-->
			</ul>
			<div id="tabs-1">
				<form action="data/" method="post" id="name_search" name="name_search">
					<input type="hidden" name="action" value="organisation_search"/>
					<input type="hidden" name="search_type" value="single"/>
					<table class="formTable">
						<tbody>
						<tr>
							<th scope="row">
								<label id="name_label_1" for="organisation_name" class="#cluetip_name" style="cursor: help;">Organisationan Name: </label>
							</th>
							<td>
								<input type="text" size="40" id="organisation_name" name="organisation_name"/>
							</td>
						</tr>
						<tr>
							<th scope="row">
								<label id="operator_label_1" for="operator" class="#cluetip_operator" style="cursor: help;">Search Operator: </label>
							</th>
							<td>
								<select size="1" id="operator" name="operator">
									<option value="and" selected="selected">And</option>
									<option value="or">Or</option>
									<option value="exact">Exact Phrase</option>
								</select>
							</td>
						</tr>
						<tr>
							<td colspan="2">
								<input class="ui-state-default ui-corner-all button" type="submit" name="submit" id="name_search_btn" value="Search"/><br/>
								<span style="font-size: 90%"><strong>Note:</strong> Hover over a form label to see additional help information</span>
							</td>
						</tr>
						</tbody>
					</table>
				</form>
			</div>
			<div id="tabs-2">
				<form action="data/" method="get" id="id_search" name="id_search">
					<input type="hidden" name="action" value="lookup"/>
					<input type="hidden" name="type"   value="contribname"/>
					<table class="formTable">
						<tbody>
						<tr>
							<th scope="row">
								<label for="id">Organisation ID: </label>
							</th>
							<td>
								<input type="text" size="40" id="id" name="id"/>
							</td>
						</tr>
						<tr>
							<td colspan="2">
								<input class="ui-state-default ui-corner-all button" type="submit" name="submit" id="id_search_btn" value="Search"/>
							</td>
						</tr>
						</tbody>
					</table>
				</form>
			</div>
			<!--
			<div id="tabs-3">
				<form action="data/" method="post" id="multi_name_search" name="multi_name_search">
					<input type="hidden" name="action" value="contributor_search"/>
					<input type="hidden" name="search_type" value="multi"/>
					<table class="formTable">
						<tbody>
						<tr>
							<th scope="row">
								<label id="name_label_2" for="contributor_name" class="#cluetip_name" style="cursor: help;">Contributor Name: </label>
							</th>
							<td>
								<input type="text" size="40" id="multi_contributor_name" name="contributor_name"/>
							</td>
						</tr>
						<tr>
							<th scope="row">
								<label id="operator_label_2" for="multi_operator" class="#cluetip_operator" style="cursor: help;">Search Operator: </label>
							</th>
							<td>
								<select size="1" id="operator" name="operator">
									<option value="and" selected="selected">And</option>
									<option value="or">Or</option>
									<option value="exact">Exact Phrase</option>
								</select>
							</td>
						</tr>
						<tr>
							<td colspan="2">
								<input class="ui-state-default ui-corner-all button" type="submit" name="submit" id="multi_name_search_btn" value="Search"/><br/>
								<span style="font-size: 90%"><strong>Note:</strong> Hover over a form label to see additional help information</span>
							</td>
						</tr>
						</tbody>
					</table>
				</form>
			</div>
			-->
		</div>
		<div id="search_waiting">
		<p style="text-align: center;">
			<img src="assets/images/ajax-loader.gif" width="220" height="19" alt=" "/>
			<br/>Loading Search Results...
		</p>
		</div>
		<div id="search_results" style="padding-top: 10px;">
		</div>
		<div id="map_header" class="map_header_footer">
			<h3 id="map_heading"></h3>
			<ul class="map_links">
				<li><a href="#" title="Link for this map" id="map_header_link">Persistent link for this map</a></li>
				<li><a href="#" title="Download KML file" id="map_header_kml">Download KML version of this map</a></li>
				<li><a href="#" title="Export data using advanced options" id="map_header_export">Download KML data with advanced options</a></li>
			</ul>
			<div id="map_content_list">
				<div id="map_contents">
					<span style="font-weight: bold">Contributors on the Map: </span>
				</div>
			</div>
		</div>
		<div id="map">
		</div>
		<p></p>
		<div id="map_footer" class="map_header_footer">
			<h3>Advanced Display Options</h3>
			<form action="" method="get" id="adv_map_display_form" name="adv_map_display_form">
			<table class="formTable" width="100%">
				<tr id="time_slider_option_row_1">
					<th scope="row">Show events that occured between:</th>
				</tr>
				<tr id="time_slider_option_row_2">
					<td>
						<fieldset>
							<label for="event_finish" class="tohide">First Date: </label>
								<select name="event_start" id="event_start" size="1" class="slider"></select>
							<label for="event_start" class="tohide">Last Date: </label>
								<select name="event_finish" id="event_finish" size="1" class="slider"></select>
						</fieldset>
					</td>
				</tr>
				<tr id="state_option_row">
					<th scope="row">
					<label id="state_label" for="state" class="#cluetip_state" style="cursor: help;">Only show venues in: </label>
						<select size="1" id="state" name="state">
							<option value="nolimit" selected="selected">No Limit - All venues</option>
							<option value="a">Australia</option>
							<option value="7"> - Australian Capital Territory</option>
							<option value="3"> - New South Wales</option>
							<option value="8"> - Northern Territory</option>
							<option value="4"> - Queensland</option>
							<option value="1"> - South Australia</option>
							<option value="5"> - Tasmania</option>
							<option value="6"> - Victoria</option>
							<option value="2"> - Western Australia</option>
							<option value="9">Outside Australia</option>
						</select>
					</th>
				</tr>
				<tr id="hidden_options_row">
					<td>
						<strong>Note: </strong> Limiting the display of markers by location and time are only possible when trajectory information is not displayed.
					</td>
				</tr>
				<tr>
					<th scope="row">
						<label for="show_trajectory">Show Trajectory Information: </label> &nbsp; <input type="checkbox" id="show_trajectory" name="show_trajectory" onclick="showTrajectory();" value="on"/>
					</th>
				</tr>
				<tr>
					<td>
						<input class="ui-state-default ui-corner-all button" type="button" name="reload_map" id="reload_map" value="Reload Map"/>
					</td>
				</tr>
			</table>
			
			</form>
		</div>
		<div id="map_legend">
			<table class="mapLegend">
				<thead>
					<tr>
						<th colspan="2">Map Legend</th>
					</tr>
				</thead>
				<tbody>
					<tr>
						<th style="background: #CCBAD7; width: 20px;" scope="row">&nbsp;</th>
						<td>Markers this colour indicate only 1 event</td>
					</tr>
					<tr>
						<th style="background: #9A7BAB; width: 20px;" scope="row">&nbsp;</th>
						<td>Markers this colour indicate between 2 &amp; 5 events</td>
					</tr>
					<tr>
						<th style="background: #7F649B; width: 20px;" scope="row">&nbsp;</th>
						<td>Markers this colour indicate between 6 &amp; 15 events</td>
					</tr>
					<tr>
						<th style="background: #69528E; width: 20px;" scope="row">&nbsp;</th>
						<td>Markers this colour indicate between 16 &amp; 30 events</td>
					</tr>
					<tr>
						<th style="background: #4D3779; width: 20px;" scope="row">&nbsp;</th>
						<td>Markers this colour indicate more than 30 events</td>
					</tr>
					<!--
					<tr>
						<td scope="row" colspan="2">
							Trajectory lines are coloured using a scale:
							<ul>
								<li><span style="background: #FF0000">red</span> is the earliest</li>
								<li><span style="background: #FFFF00">yellow</span> is the latest</li>
								<li>Other times are shades between these two colours</li>
							</ul>
						</td>
					</tr>
					-->
				</tbody>
			</table>					
		</div>
		<!-- clue tip content -->
		<div id="cluetip_name">
			<p>
				Enter a few keywords, or the exact name, of the organisation that is of interest
			</p>
		</div>
		<div id="cluetip_operator">
			<p>
				There are three search operators available:
			</p>
			<ul>
				<li>And: All of the keywords must appear in the name</li>
				<li>Or:  Any of the keywords must appear in the name</li>
				<li>Exact Phrase: The organisation name must much exactly what is entered</li>
			</ul>
		</div>
		<div id="cluetip_state">
			<p>
				Limit the display of venues to only those that appear in the selected state, territory, in Australia or outside Australia only
			</p>
		</div>
	</div>
	<!-- include the footer -->
	<jsp:include page="footer.jsp"/>
</div>
<!-- include the Google Analytics code -->
<jsp:include page="analytics.jsp"/>
</body>
</html>
